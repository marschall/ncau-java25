package com.netcetera.ncau.java25.api;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.lang.foreign.Linker;
import java.lang.foreign.MemoryLayout;
import java.util.Map;

import org.junit.jupiter.api.Test;

class FfmTests {
  
  @Test
  void canonicalLayouts() {
    Map<String, MemoryLayout> canonicalLayouts = Linker.nativeLinker().canonicalLayouts();
    assertNotNull(canonicalLayouts);
    
    MemoryLayout sizeT = canonicalLayouts.get("size_t");
    assertNotNull(sizeT);
    
    MemoryLayout voidStar = canonicalLayouts.get("void*");
    assertNotNull(voidStar);
  }

}
