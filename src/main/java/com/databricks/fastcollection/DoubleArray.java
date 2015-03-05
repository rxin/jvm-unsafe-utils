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

package com.databricks.fastcollection;

/**
 * An array of double values. Compared with native JVM arrays, this:
 * <ul>
 *   <li>supports using both in-heap and off-heap memory</li>
 *   <li>supports 64-bit addressing, i.e. array length greater than {@code Integer.MAX_VALUE}</li>
 *   <li>has no bound checking, and thus can crash the JVM process when assert is turned off</li>
 * </ul>
 */
public interface DoubleArray {

  /**
   * Returns the number of elements this array can hold.
   */
  public long size();

  /**
   * Sets the value at position index.
   */
  public void set(long index, double value);

  /**
   * Returns the value at position index.
   */
  public double get(long index);

  /**
   * Returns a copy of the array as a JVM native array. The caller should make sure this array's
   * length is less than {@code Integer.MAX_VALUE}.
   */
  public double[] toJvmArray();
}
