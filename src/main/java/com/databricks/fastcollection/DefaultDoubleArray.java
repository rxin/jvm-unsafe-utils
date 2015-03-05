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

final class DefaultDoubleArray implements DoubleArray {

  private static final int WIDTH = 8;
  private static final long ARRAY_OFFSET = PlatformDependent.DOUBLE_ARRAY_OFFSET;

  private final Object baseObj;
  private final long baseOffset;

  private final long length;

  public DefaultDoubleArray(MemoryBlock memory) {
    assert memory.size() % WIDTH == 0 : "Memory not aligned (" + memory.size() + ")";
    this.baseObj = memory.getBaseObject();
    this.baseOffset = memory.getBaseOffset();
    this.length = memory.size() / WIDTH;
  }

  @Override
  public long size() {
    return length;
  }

  @Override
  public void set(long index, double value) {
    assert index >= 0 : "index (" + index + ") should >= 0";
    assert index < length : "index (" + index + ") should < length (" + length + ")";
    PlatformDependent.UNSAFE.putDouble(baseObj, baseOffset + index * WIDTH, value);
  }

  @Override
  public double get(long index) {
    assert index >= 0 : "index (" + index + ") should >= 0";
    assert index < length : "index (" + index + ") should < length (" + length + ")";
    return PlatformDependent.UNSAFE.getDouble(baseObj, baseOffset + index * WIDTH);
  }

  @Override
  public double[] toJvmArray() throws IndexOutOfBoundsException {
    if (length > Integer.MAX_VALUE) {
      throw new IndexOutOfBoundsException(
        "array size (" + length + ") too large and cannot be converted into JVM array");
    }

    final double[] arr = new double[(int) length];
    PlatformDependent.UNSAFE.copyMemory(
      baseObj,
      baseOffset,
      arr,
      ARRAY_OFFSET,
      length * WIDTH);
    return arr;
  }
}
