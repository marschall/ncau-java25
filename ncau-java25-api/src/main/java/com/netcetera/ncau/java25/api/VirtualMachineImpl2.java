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
    // https://man7.org/linux/man-pages/man5/proc_pid_status.5.html
    // https://man7.org/linux/man-pages/man7/signal.7.html
    
    record Line(String field, long mask) {
      
      static Line parse(String s) {
        int endOfFieldName = s.indexOf(':');
        String field = s.substring(0, endOfFieldName);
        // skip colon and tab
        long mask = Long.parseLong(s, endOfFieldName + 2, s.length(), 16);
        return new Line(field, mask);
      }
      
      boolean isSignalSet(long signal) {
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
      
      private boolean quitIgnored = false;
      private boolean quitCaught = false;

      private boolean readIgnored = false;
      private boolean readCaught = false;
      
      void quitCaught(boolean b) {
        this.quitCaught = b;
        this.readCaught = true;
      }
      
      void quitIgnored(boolean b) {
        this.quitIgnored = b;
        this.readIgnored = true;
      }
      
      boolean isParsingComplete() {
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
      Gatherer<Line, ParseState, ParseResult> toResult = Gatherer.ofSequential(ParseState::new, (state, line, downstream) -> {
        
        boolean isSigquit = line.isSignalSet(SIGQUIT);
        
        switch (line.field()) {
          case "SigCgt" -> state.quitCaught(isSigquit);
          case "SigIgn" -> state.quitIgnored(isSigquit);
        };

        if (state.isParsingComplete()) {
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
