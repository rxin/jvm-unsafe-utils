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

import org.junit.Assert;
import org.junit.Test;

public class TestIntArray {

  private IntArray createTestData() {
    byte[] bytes = new byte[16];
    IntArray arr = new IntArray(MemoryBlock.fromByteArray(bytes));
    arr.set(0, 1);
    arr.set(1, 2);
    arr.set(2, 3);
    arr.set(3, 4);
    arr.set(3, 5);
    return arr;
  }

  @Test
  public void basicTest() {
    IntArray arr = createTestData();
    Assert.assertEquals(4, arr.size());
    Assert.assertEquals(1, arr.get(0));
    Assert.assertEquals(2, arr.get(1));
    Assert.assertEquals(3, arr.get(2));
    Assert.assertEquals(5, arr.get(3));
  }

  @Test
  public void toJvmArray() {
    IntArray arr = createTestData();
    int[] expected = {1, 2, 3, 5};
    Assert.assertArrayEquals(expected, arr.toJvmArray());
  }
}
