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

import com.databricks.unsafe.util.memory.MemoryAllocator;

/**
 * A long to long hash map.
 *
 * This is backed by a power-of-2-sized hash table, using quadratic probing with triangular numbers,
 * which is guaranteed to exhaust the space.
 *
 * Note that even though we use long for indexing, the map can support up to 2^31 keys because
 * we use 32 bit MurmurHash. In either case, if the key cardinality is so high, you should probably
 * be using sorting instead of hashing for better cache locality.
 */
public final class LongToLongMap {

  private static final Murmur3_x86_32 HASHER = new Murmur3_x86_32(0);

  private static final HashMapGrowthStrategy growthStrategy = HashMapGrowthStrategy.DOUBLING;

  private final MemoryAllocator allocator;

  /**
   * A single array to store the key and value.
   *
   * Position {@code 2 * i} in the array is used to track the key at index {@code i}, while
   * position {@code 2 * i + 1} in the array is used to track the value at index {@code i}.
   */
  private LongArray longArray;

  /**
   * A {@link BitSet} used to track location of the map where the key is set.
   * Size of the bitset should be half of the size of the long array.
   */
  private BitSet bitset;

  private final double loadFactor;

  /**
   * Number of keys defined in the map.
   */
  private long size;

  private long growthThreshold;

  private long mask;

  private final Location loc;

  public LongToLongMap(MemoryAllocator allocator, long initialCapacity, double loadFactor) {
    this.allocator = allocator;
    this.loadFactor = loadFactor;
    this.loc = new Location();
    allocate(initialCapacity);
  }

  public LongToLongMap(MemoryAllocator allocator, long initialCapacity) {
    this(allocator, initialCapacity, 0.70);
  }

  /**
   * Returns the number of keys defined in the map.
   */
  public long size() {
    return size;
  }

  /**
   * Returns true if the key is defined in this map.
   */
  public boolean containsKey(long key) {
    return lookup(key).isDefined();
  }

  /**
   * Updates the value the key maps to.
   */
  public void put(long key, long value) {
    lookup(key).setValue(value);
  }

  /**
   * Returns the value to which the specified key is mapped. In the case the key is not defined,
   * this has undefined behavior.
   */
  public long get(long key) {
    return lookup(key).getValue();
  }

  /**
   * Looks up a key, and return a {@link Location} handle that can be used to test existence
   * and read/write values.
   *
   * This function always return the same {@link Location} instance to avoid object allocation.
   */
  public Location lookup(long key) {
    long pos = HASHER.hashLong(key) & mask;
    long step = 1;
    while (true) {
      if (!bitset.isSet(pos)) {
        // This is a new key.
        return loc.with(pos, key, false);
      } else if (longArray.get(pos * 2) == key) {
        // Found an existing key.
        return loc.with(pos, key, true);
      } else {
        pos = (pos + step) & mask;
        step++;
      }
    }
  }

  /**
   * Handle returned by {@link LongToLongMap#lookup(long)} function.
   */
  public final class Location {
    private long pos;
    private long key;
    private boolean isDefined;

    Location with(long pos, long key, boolean isDefined) {
      this.pos = pos;
      this.key = key;
      this.isDefined = isDefined;
      return this;
    }

    /**
     * Returns true if the key is defined at this position, and false otherwise.
     */
    public boolean isDefined() {
      return isDefined;
    }

    /**
     * Returns the key defined at this position. Unspecified behavior if the key is not defined.
     */
    public long getKey() {
      return longArray.get(pos * 2);
    }

    /**
     * Returns the value defined at this position. Unspecified behavior if the key is not defined.
     */
    public long getValue() {
      return longArray.get(pos * 2 + 1);
    }

    /**
     * Updates the value defined at this position. Unspecified behavior if the key is not defined.
     */
    public void setValue(long value) {
      if (!isDefined) {
        size++;
        bitset.set(pos);
        longArray.set(pos * 2, key);
        longArray.set(pos * 2 + 1, value);
        if (size > growthThreshold) {
          growAndRehash();
        }
      } else {
        longArray.set(pos * 2 + 1, value);
      }
    }
  }

  private void allocate(long capacity) {
    capacity = Math.max(nextPowerOf2(capacity), 64);
    longArray = new LongArray(allocator.allocate(capacity * 8 * 2));
    bitset = new BitSet(allocator.allocate(capacity / 8));

    this.growthThreshold = (long) (capacity * loadFactor);
    this.mask = capacity - 1;
  }

  /**
   * Grows the size of the hash table and re-hash everything.
   */
  private void growAndRehash() {
    // Store references to the old data structures to be used when we re-hash
    final LongArray oldLongArray = longArray;
    final BitSet oldBitSet = bitset;
    final long oldCapacity = oldBitSet.capacity();

    // Allocate the new data structures
    allocate(growthStrategy.nextCapacity(oldCapacity));

    // Re-hash
    for (long pos = oldBitSet.nextSetBit(0); pos >= 0; pos = oldBitSet.nextSetBit(pos + 1)) {
      final long key = oldLongArray.get(pos * 2);
      final long value = oldLongArray.get(pos * 2 + 1);
      long newPos = HASHER.hashLong(key) & mask;
      long step = 1;
      boolean keepGoing = true;

      // No need to check for equality here when we insert so this has one less if branch than
      // the similar code path in addWithoutResize.
      while (keepGoing) {
        if (!bitset.isSet(newPos)) {
          longArray.set(newPos * 2, key);
          longArray.set(newPos * 2 + 1, value);
          bitset.set(newPos);
          keepGoing = false;
        } else {
          newPos = (newPos + step) & mask;
          step++;
        }
      }
    }

    // Deallocate the old data structures.
    allocator.free(oldLongArray.memoryBlock());
    allocator.free(oldBitSet.memoryBlock());
  }

  /** Returns the next number greater or equal num that is power of 2. */
  private long nextPowerOf2(long num) {
    final long highBit = Long.highestOneBit(num);
    return (highBit == num) ? num : highBit << 1;
  }
}
