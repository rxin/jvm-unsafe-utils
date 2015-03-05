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

import com.databricks.fastcollection.util.Murmur3_x86_32;

/**
 * A {@link LongToLongMap} implementation using open addressing.
 *
 * This is backed by a power-of-2-sized hash table, using quadratic probing with triangular numbers,
 * which is guaranteed to exhaust the space.
 *
 * Note that even though we use long for indexing, the map can support up to 2^31 keys because
 * we use 32 bit MurmurHash.
 */
final class DefaultLongToLongMap implements LongToLongMap {

  private static final Murmur3_x86_32 HASHER = new Murmur3_x86_32(0);

  /**
   * A single array to store the key and value.
   *
   * Position {@code 2 * i} in the array is used to track the key at index {@code i}, while
   * position {@code 2 * i + 1} in the array is used to track the value at index {@code i}.
   */
  private final DefaultLongArray longArray;

  /**
   * A {@link BitSet} used to track location of the map where the key is set.
   * Size of the bitset should be half of the size of the long array.
   */
  private final DefaultBitSet bitset;

  /**
   * Number of keys defined in the map.
   */
  private long size;

  private long capacity;

  private long mask;

  private final Location loc;

  DefaultLongToLongMap(DefaultLongArray longArray, DefaultBitSet bitset) {
    assert longArray.size() == bitset.capacity() * 2 : "array (" + longArray.size() +
        ") should be twice the capacity of the bitset (" + bitset.capacity() + ")";
    this.longArray = longArray;
    this.bitset = bitset;
    this.capacity = bitset.capacity();
    this.mask = capacity - 1;
    this.loc = new Location();
  }

  @Override
  public long size() {
    return size;
  }

  @Override
  public boolean containsKey(long key) {
    return lookup(key).isDefined();
  }

  @Override
  public void put(long key, long value) {
    lookup(key).setValue(value);
  }

  @Override
  public long get(long key) {
    return lookup(key).getValue();
  }

  @Override
  public Location lookup(long key) {
    long pos = HASHER.hashLong(key) & mask;
    long step = 1;
    while (true) {
      if (!bitset.isSet(pos)) {
        // This is a new key.
        loc.pos = pos;
        loc.isDefined = false;
        loc.key = key;
        return loc;
      } else if (longArray.get(pos * 2) == key) {
        // Found an existing key.
        loc.pos = pos;
        loc.isDefined = true;
        loc.key = key;
        return loc;
      } else {
        pos = (pos + step) & mask;
        step++;
      }
    }
  }

  class Location implements LongToLongMap.Location {

    long pos;

    long key;

    boolean isDefined;

    @Override
    public boolean isDefined() {
      return isDefined;
    }

    @Override
    public long getKey() {
      return longArray.get(pos * 2);
    }

    @Override
    public long getValue() {
      return longArray.get(pos * 2 + 1);
    }

    @Override
    public void setValue(long value) {
      if (!isDefined) {
        size++;
        bitset.set(pos);
        longArray.set(pos * 2, key);
      }
      longArray.set(pos * 2 + 1, value);
    }
  }
}
