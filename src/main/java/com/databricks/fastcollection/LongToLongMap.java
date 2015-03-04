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

  public long size();

  public void put(long key, long value);

  public long get(long key);

  public Location getHandler(long key);

  public static interface Location {
    public long getKey();
    public long getValue();
    public long setValue(long value);
  }
}
