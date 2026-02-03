package com.netcetera.ncau.java25.api;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.stream.Gatherer;
import java.util.stream.Stream;

public class VirtualMachineImpl2 {
  private static final String FIELD = "field";

  private static final String MASK = "mask";

  private static final long SIGQUIT = 0b100; // mask bit for SIGQUIT

  private static final Path PROC = Path.of("/proc");

  private static boolean checkCatchesAndSendQuitTo(int pid) throws IOException {
    return checkCatchesAndSendQuitTo(PROC.resolve(Integer.toString(pid)));
  }

  static boolean checkCatchesAndSendQuitTo(Path procPid) throws IOException {
    
    record Line(String signal, long mask) {
      
      static Line parse(String s) {
        int endOfName = s.indexOf(':');
        String signal = s.substring(0, endOfName);
        // skip colon and tab
        long mask = Long.parseLong(s, endOfName + 2, s.length(), 16);
        return new Line(signal, mask);
      }
      
    }
    
    record ParseResult(boolean quitIgn, boolean quitCgt) {

      
      boolean okToSendQuit()  {
        // ignore blocked
        return !this.quitIgn && this.quitCgt;
      }
    }
    
    class ParseState {
      
      boolean quitIgn = false;
      boolean quitCgt = false;

      boolean readIgn = false;
      boolean readCgt = false;
      
      void quitCgt(boolean b) {
        this.quitCgt = b;
        this.readCgt = true;
      }
      
      void quitIgn(boolean b) {
        this.quitIgn = b;
        this.readIgn = true;
      }
      
      boolean isDone() {
        return this.readIgn && this.readCgt;
      }
      
      ParseResult toResult() {
        return new ParseResult(this.quitIgn, this.quitCgt);
      }
      
    }
    
    if (!Files.exists(procPid)) {
      throw new IOException("non existent JVM pid file: " + procPid);
    }
    

    Optional<ParseResult> result;
    try (Stream<String> lines = Files.lines(procPid.resolve("status"))) {
      Gatherer<Line, ParseState, ParseResult> toResult = Gatherer.ofSequential(() -> new ParseState(), (state, line, downstream) -> {
        
        boolean sigquit = (line.mask() & SIGQUIT) != 0L;
        
        switch (line.signal()) {
          case "SigCgt" -> state.quitCgt(sigquit);
          case "SigIgn" -> state.quitIgn(sigquit);
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
