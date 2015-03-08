/*
 * Copyright 2014 Databricks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.databricks.jmh;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

public class DeadcodeTest {

  @Benchmark
  public void doNothing() {
  }

  @Benchmark
  public void increment() {
    double x = 0;
    x = x + 1;
  }

  @Benchmark
  public double incrementAndReturn() {
    double x = 0;
    x = x + 1;
    return x;
  }

  public static void main(String[] args) throws RunnerException {
    Options opt = new OptionsBuilder()
      .include(DeadcodeTest.class.getSimpleName())
      .warmupIterations(5)
      .measurementIterations(5)
      .forks(1)
      .jvmArgs("-ea")
      .build();

    new Runner(opt).run();
  }
}
