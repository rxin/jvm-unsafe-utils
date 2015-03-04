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

class DefaultLongToLongMap implements LongToLongMap {

  private final DefaultLongArray longArray;
  private final DefaultBitSet bitset;
  private long size;

  DefaultLongToLongMap(DefaultLongArray longArray, DefaultBitSet bitset) {
    this.longArray = longArray;
    this.bitset = bitset;
  }

  @Override
  public long size() {
    return size;
  }

  @Override
  public void put(long key, long value) {

  }

  @Override
  public long get(long key) {
    return 0;
  }

  @Override
  public LongToLongMap.Location getHandler(long key) {
    return null;
  }

  class Location implements LongToLongMap.Location {

    private final long position;

    Location(long position) {
      this.position = position;
    }

    @Override
    public long getKey() {
      return 0;
    }

    @Override
    public long getValue() {
      return 0;
    }

    @Override
    public long setValue(long value) {
      return 0;
    }
  }

  private long getPosition(long k) {
    return 0;
  }
}
