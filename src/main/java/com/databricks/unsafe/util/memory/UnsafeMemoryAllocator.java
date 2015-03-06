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

package com.databricks.unsafe.util.memory;

import com.databricks.unsafe.util.PlatformDependent;

/**
 * A simple {@link MemoryAllocator} that uses {@code Unsafe} to allocate off-heap memory.
 */
public class UnsafeMemoryAllocator implements MemoryAllocator {

  @Override
  public MemoryBlock allocate(long size) throws OutOfMemoryError {
    long address = PlatformDependent.UNSAFE.allocateMemory(size);
    PlatformDependent.UNSAFE.setMemory(address, size, (byte) 0);
    return new MemoryBlock(null, address, size);
  }

  @Override
  public void free(MemoryBlock memory) {
    if (memory.obj != null) {
      PlatformDependent.UNSAFE.freeMemory(memory.offset);
    }
  }
}
