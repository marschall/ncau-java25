package com.netcetera.ncau.java25.api;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.stream.Gatherer;
import java.util.stream.Stream;

public class VirtualMachineImpl2 {

  private static final long SIGQUIT = 0b100; // mask bit for SIGQUIT

  private static final Path PROC = Path.of("/proc");

  private static boolean checkCatchesAndSendQuitTo(int pid) throws IOException {
    return checkCatchesAndSendQuitTo(PROC.resolve(Integer.toString(pid)));
  }

  static boolean checkCatchesAndSendQuitTo(Path procPid) throws IOException {
    
    record Line(String field, long mask) {
      
      static Line parse(String s) {
        int endOfFieldName = s.indexOf(':');
        String field = s.substring(0, endOfFieldName);
        // skip colon and tab
        long mask = Long.parseLong(s, endOfFieldName + 2, s.length(), 16);
        return new Line(field, mask);
      }
      
      boolean isHandeled(long signal) {
        return (this.mask & signal) != 0L;
      }
      
    }
    
    record ParseResult(boolean quitIgnored, boolean quitCaught) {

      
      boolean okToSendQuit()  {
        // ignore blocked
        return !this.quitIgnored && this.quitCaught;
      }
    }
    
    class ParseState {
      
      boolean quitIgnored = false;
      boolean quitCaught = false;

      boolean readIgnored = false;
      boolean readCaught = false;
      
      void quitCaught(boolean b) {
        this.quitCaught = b;
        this.readCaught = true;
      }
      
      void quitIgnored(boolean b) {
        this.quitIgnored = b;
        this.readIgnored = true;
      }
      
      boolean isDone() {
        return this.readIgnored && this.readCaught;
      }
      
      ParseResult toResult() {
        return new ParseResult(this.quitIgnored, this.quitCaught);
      }
      
    }
    
    if (!Files.exists(procPid)) {
      throw new IOException("non existent JVM pid file: " + procPid);
    }
    

    Optional<ParseResult> result;
    try (Stream<String> lines = Files.lines(procPid.resolve("status"))) {
      Gatherer<Line, ParseState, ParseResult> toResult = Gatherer.ofSequential(() -> new ParseState(), (state, line, downstream) -> {
        
        boolean isSigquitHandeled = line.isHandeled(SIGQUIT);
        
        switch (line.field()) {
          case "SigCgt" -> state.quitCaught(isSigquitHandeled);
          case "SigIgn" -> state.quitIgnored(isSigquitHandeled);
        };

        if (state.isDone()) {
          downstream.push(state.toResult());
          return false;
        } else {
          return true;
        }
      });
      result = lines.filter(line -> line.startsWith("Sig") && line.indexOf(':') == 6)
                    .map(Line::parse)
                    .gather(toResult)
                    .findAny();
    }

    return result.map(ParseResult::okToSendQuit).orElse(Boolean.FALSE);

  }
}
