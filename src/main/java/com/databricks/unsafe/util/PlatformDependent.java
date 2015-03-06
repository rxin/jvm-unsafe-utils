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

package com.databricks.unsafe.util;

import java.lang.reflect.Field;

import sun.misc.Unsafe;

public final class PlatformDependent {

  public static final Unsafe UNSAFE;

  public static final int BYTE_ARRAY_OFFSET;

  public static final int INT_ARRAY_OFFSET;

  public static final int LONG_ARRAY_OFFSET;

  public static final int DOUBLE_ARRAY_OFFSET;

  /**
   * Limits the number of bytes to copy per {@link Unsafe#copyMemory(long, long, long)} to
   * allow safepoint polling during a large copy.
   */
  private static final long UNSAFE_COPY_THRESHOLD = 1024L * 1024L;

  static {
    sun.misc.Unsafe unsafe;
    try {
      Field unsafeField = Unsafe.class.getDeclaredField("theUnsafe");
      unsafeField.setAccessible(true);
      unsafe = (sun.misc.Unsafe) unsafeField.get(null);
    } catch (Throwable cause) {
      unsafe = null;
    }
    UNSAFE = unsafe;

    if (UNSAFE != null) {
      BYTE_ARRAY_OFFSET = UNSAFE.arrayBaseOffset(byte[].class);
      INT_ARRAY_OFFSET = UNSAFE.arrayBaseOffset(int[].class);
      LONG_ARRAY_OFFSET = UNSAFE.arrayBaseOffset(long[].class);
      DOUBLE_ARRAY_OFFSET = UNSAFE.arrayBaseOffset(double[].class);
    } else {
      BYTE_ARRAY_OFFSET = 0;
      INT_ARRAY_OFFSET = 0;
      LONG_ARRAY_OFFSET = 0;
      DOUBLE_ARRAY_OFFSET = 0;
    }
  }

  static void copyMemory(Object src, long srcOffset, Object dst, long dstOffset, long length) {
    while (length > 0) {
      long size = Math.min(length, UNSAFE_COPY_THRESHOLD);
      UNSAFE.copyMemory(src, srcOffset, dst, dstOffset, size);
      length -= size;
      srcOffset += size;
      dstOffset += size;
    }
  }

  /**
   * Raises an exception bypassing compiler checks for checked exceptions.
   */
  public static void throwException(Throwable t) {
    UNSAFE.throwException(t);
  }
}
