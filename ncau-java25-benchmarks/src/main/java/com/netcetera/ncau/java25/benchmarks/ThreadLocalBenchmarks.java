package com.netcetera.ncau.java25.benchmarks;

import static java.util.concurrent.TimeUnit.MICROSECONDS;
import static org.openjdk.jmh.annotations.Mode.Throughput;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.OutputTimeUnit;

@BenchmarkMode(Throughput)
@OutputTimeUnit(MICROSECONDS)
public class ThreadLocalBenchmarks {

  private static final ThreadLocal<String> TL_VALUE = new ThreadLocal<>();

  private static final ScopedValue<String> SL_VALUE = ScopedValue.newInstance();

  @Benchmark
  public String threadLocal() {
    TL_VALUE.set("value");
    return readThreadLocal();
  }

  private String readThreadLocal() {
    return TL_VALUE.get();
  }

  @Benchmark
  public String scopedValue() {
    return ScopedValue.where(SL_VALUE, "value").call(this::readScopedValue);
  }

  private String readScopedValue() {
    return SL_VALUE.get();
  }

}
