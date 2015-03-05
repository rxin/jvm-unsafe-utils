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
 * A long to long map that does not allow the removal of keys.
 */
public interface LongToLongMap {

  /**
   * Returns the number of keys defined in the map.
   */
  public long size();

  /**
   * Returns true if the key is defined in this map.
   */
  public boolean containsKey(long key);

  /**
   * Updates the value the key maps to.
   */
  public void put(long key, long value);

  /**
   * Returns the value to which the specified key is mapped. In the case the key is not defined,
   * this has undefined behavior.
   */
  public long get(long key);

  /**
   * Looks up a key, and return a {@link Location} handle that can be used to test existence
   * and read/write values.
   */
  public Location lookup(long key);

  /**
   * Handle returned by {@link LongToLongMap#lookup(long)} function.
   */
  public static interface Location {

    /**
     * Returns true if the key is defined at this position, and false otherwise.
     */
    public boolean isDefined();

    /**
     * Returns the key defined at this position. Unspecified behavior if the key is not defined.
     */
    public long getKey();

    /**
     * Returns the value defined at this position. Unspecified behavior if the key is not defined.
     */
    public long getValue();

    /**
     * Updates the value defined at this position. Unspecified behavior if the key is not defined.
     */
    public void setValue(long value);
  }
}
