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
 * A fixed size {@link BitSet} backed by a {@link DefaultLongArray}.
 */
final class DefaultBitSet implements BitSet {

  private final DefaultLongArray words;
  private final long length;
  private final long numWords;

  public DefaultBitSet(MemoryBlock memory) {
    words = new DefaultLongArray(memory);
    numWords = words.size();
    length = numWords * 64;
  }

  @Override
  public long capacity() {
    return length;
  }

  @Override
  public void set(long index) {
    assert index >= 0 : "index (" + index + ") should >= 0";
    assert index < length : "index (" + index + ") should < length (" + length + ")";
    final long mask = 1L << (index & 0x3f);  // mod 64 and shift
    final long wi = index >> 6;
    words.set(wi, words.get(wi) | mask);  // div by 64 and mask
  }

  @Override
  public void unset(long index) {
    assert index >= 0 : "index (" + index + ") should >= 0";
    assert index < length : "index (" + index + ") should < length (" + length + ")";
    final long mask = 1L << (index & 0x3f);  // mod 64 and shift
    final long wi = index >> 6;
    words.set(wi, words.get(wi) & ~mask);  // div by 64 and mask
  }

  @Override
  public boolean isSet(long index) {
    assert index >= 0 : "index (" + index + ") should >= 0";
    assert index < length : "index (" + index + ") should < length (" + length + ")";
    final long mask = 1L << (index & 0x3f);  // mod 64 and shift
    return (words.get(index >> 6) & mask) != 0;  // div by 64 and mask
  }

  @Override
  public long cardinality() {
    long sum = 0L;
    for (long i = 0; i < numWords; i++) {
      sum += java.lang.Long.bitCount(words.get(i));
    }
    return sum;
  }

  @Override
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
