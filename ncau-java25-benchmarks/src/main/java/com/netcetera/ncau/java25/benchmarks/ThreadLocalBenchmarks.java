package com.netcetera.ncau.java25.benchmarks;

import static java.util.concurrent.TimeUnit.MICROSECONDS;
import static org.openjdk.jmh.annotations.Mode.Throughput;
import static org.openjdk.jmh.annotations.Scope.Benchmark;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.State;

@State(Benchmark)
@BenchmarkMode(Throughput)
@OutputTimeUnit(MICROSECONDS)
public class ThreadLocalBenchmarks {

  private static final ThreadLocal<String> TL_VALUE = new ThreadLocal<>();

  private static final ScopedValue<String> SL_VALUE = ScopedValue.newInstance();

  @Param({"1", "10", "100"})
  public int stackDepth;

  @Benchmark
  public String threadLocal() {
    TL_VALUE.set("value");
    try {
      return readThreadLocal();
    } finally {
      TL_VALUE.remove();
    }
  }

  private String readThreadLocal() {
    return readThreadLocal(this.stackDepth);
  }

  private String readThreadLocal(int n) {
    if (n == 0) {
      return TL_VALUE.get();
    }
    return readThreadLocal(n - 1);
  }

  @Benchmark
  public String scopedValue() {
    return ScopedValue.where(SL_VALUE, "value").call(this::readScopedValue);
  }

  private String readScopedValue() {
    return readScopedValue(this.stackDepth);
  }
  
  private String readScopedValue(int n) {
    if (n == 0) {
      return SL_VALUE.get();
    }
    return readScopedValue(n - 1);
  }
  

}
