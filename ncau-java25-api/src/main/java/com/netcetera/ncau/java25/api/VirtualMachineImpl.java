package com.netcetera.ncau.java25.api;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Pattern;

public class VirtualMachineImpl {
  private static final String FIELD = "field";

  private static final String MASK = "mask";

  private static final long SIGQUIT = 0b100; // mask bit for SIGQUIT

  private static final Path PROC = Path.of("/proc");

  private static final Pattern SIGNAL_MASK_PATTERN = Pattern
      .compile("(?<" + FIELD + ">Sig\\p{Alpha}{3}):\\s+(?<" + MASK + ">\\p{XDigit}{16}).*");

  private static boolean checkCatchesAndSendQuitTo(int pid) throws IOException {

    var quitIgn = false;
    var quitBlk = false;
    var quitCgt = false;

    final var procPid = PROC.resolve(Integer.toString(pid));

    var readBlk = false;
    var readIgn = false;
    var readCgt = false;

    if (!Files.exists(procPid)) {
      throw new IOException("non existent JVM pid: " + pid);
    }

    for (var line : Files.readAllLines(procPid.resolve("status"))) {

      if (!line.startsWith("Sig")) {
        continue; // to speed things up ... avoids the matcher/RE invocation...
      }

      final var m = SIGNAL_MASK_PATTERN.matcher(line);

      if (!m.matches()) {
        continue;
      }

      var sigmask = m.group(MASK);
      final var slen = sigmask.length();
      sigmask = sigmask.substring(slen / 2, slen); // only really interested in the non r/t signals ...
      final var sigquit = (Long.valueOf(sigmask, 16) & SIGQUIT) != 0L;

      switch (m.group(FIELD)) {
        case "SigBlk": {
          quitBlk = sigquit;
          readBlk = true;
          break;
        }
  
        case "SigIgn": {
          quitIgn = sigquit;
          readIgn = true;
          break;
        }
  
        case "SigCgt": {
          quitCgt = sigquit;
          readCgt = true;
          break;
        }

      }

      if (readBlk && readIgn && readCgt) {
        break;
      }

    }

    return (!quitIgn && quitCgt); // ignore blocked

  }
}
