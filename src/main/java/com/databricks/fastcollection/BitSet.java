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
 * A fixed size uncompressed bit set backed by a {@link LongArray}.
 *
 * Each bit occupies exactly one bit of storage.
 */
public final class BitSet {

  /** A long array for the bits. */
  private final LongArray words;

  /** Length of the long array. */
  private final long numWords;

  /**
   * Creates a new {@link BitSet} using the specified memory block. Size of the memory block must be
   * multiple of 8 bytes (i.e. 64 bits).
   */
  public BitSet(MemoryBlock memory) {
    words = new LongArray(memory);
    numWords = words.size();
  }

  /**
   * Returns the number of bits in this {@code BitSet}.
   */
  public long capacity() {
    return numWords * 64;
  }

  /**
   * Sets the bit at the specified index to {@code true}.
   */
  public void set(long index) {
    assert index >= 0 : "index (" + index + ") should >= 0";
    assert index < numWords * 64 : "index (" + index + ") should < length (" + numWords * 64 + ")";
    final long mask = 1L << (index & 0x3f);  // mod 64 and shift
    final long wi = index >> 6;
    words.set(wi, words.get(wi) | mask);  // div by 64 and mask
  }

  /**
   * Sets the bit at the specified index to {@code false}.
   */
  public void unset(long index) {
    assert index >= 0 : "index (" + index + ") should >= 0";
    assert index < numWords * 64 : "index (" + index + ") should < length (" + numWords * 64 + ")";
    final long mask = 1L << (index & 0x3f);  // mod 64 and shift
    final long wi = index >> 6;
    words.set(wi, words.get(wi) & ~mask);  // div by 64 and mask
  }

  /**
   * Returns {@code true} if the bit is set at the specified index.
   */
  public boolean isSet(long index) {
    assert index >= 0 : "index (" + index + ") should >= 0";
    assert index < numWords * 64 : "index (" + index + ") should < length (" + numWords * 64 + ")";
    final long mask = 1L << (index & 0x3f);  // mod 64 and shift
    return (words.get(index >> 6) & mask) != 0;  // div by 64 and mask
  }

  /**
   * Returns the number of bits set to {@code true} in this {@link BitSet}.
   */
  public long cardinality() {
    long sum = 0L;
    for (long i = 0; i < numWords; i++) {
      sum += java.lang.Long.bitCount(words.get(i));
    }
    return sum;
  }

  /**
   * Returns the index of the first bit that is set to true that occurs on or after the
   * specified starting index. If no such bit exists then {@code -1} is returned.
   * <p>
   * To iterate over the true bits in a BitSet, use the following loop:
   * <pre>
   * <code>
   *  for (int i = bs.nextSetBit(0); i >= 0; i = bs.nextSetBit(i+1)) {
   *    // operate on index i here
   *  }
   * </code>
   * </pre>
   *
   * @param fromIndex the index to start checking from (inclusive)
   * @return the index of the next set bit, or -1 if there is no such bit
   */
  public long nextSetBit(int fromIndex) {
    long wi = fromIndex >> 6;
    if (wi >= numWords) {
      return -1;
    }

    // Try to find the next set bit in the current word
    final long subIndex = fromIndex & 0x3f;
    long word = words.get(wi) >> subIndex;
    if (word != 0) {
      return (wi << 6) + subIndex + java.lang.Long.numberOfTrailingZeros(word);
    }

    // Find the next set bit in the rest of the words
    wi += 1;
    while (wi < numWords) {
      word = words.get(wi);
      if (word != 0) {
        return (wi << 6) + java.lang.Long.numberOfTrailingZeros(word);
      }
      wi += 1;
    }

    return -1;
  }
}
