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
 * A fixed size bit set.
 */
public interface BitSet {

  /**
   * Returns the number of bits in this {@code BitSet}.
   */
  public long capacity();

  /**
   * Sets the bit at the specified index to {@code true}.
   */
  public void set(long index);

  /**
   * Sets the bit at the specified index to {@code false}.
   */
  public void unset(long index);

  /**
   * Returns {@code true} if the bit is set at the specified index.
   */
  public boolean isSet(long index);

  /**
   * Returns the number of bits set to {@code true} in this {@link BitSet}.
   */
  public long cardinality();

  /**
   * Returns the index of the first bit that is set to true that occurs on or after the
   * specified starting index. If no such bit exists then {@code -1} is returned.
   *
   * To iterate over the true bits in a BitSet, use the following loop:
   *
   * <code>
   *  for (int i = bs.nextSetBit(0); i >= 0; i = bs.nextSetBit(i+1)) {
   *    // operate on index i here
   *  }
   * </code>
   *
   * @param fromIndex the index to start checking from (inclusive)
   * @return the index of the next set bit, or -1 if there is no such bit
   */
  public long nextSetBit(int fromIndex);
}
