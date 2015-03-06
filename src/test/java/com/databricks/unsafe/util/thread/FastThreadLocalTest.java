/*
 * Copyright 2014 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package com.databricks.unsafe.util.thread;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class FastThreadLocalTest {
  @Before
  public void setUp() {
    FastThreadLocal.removeAll();
    Assert.assertEquals(FastThreadLocal.size(), 0);
  }

  @Test(timeout = 10000)
  public void testRemoveAll() throws Exception {
    final AtomicBoolean removed = new AtomicBoolean();
    final FastThreadLocal<Boolean> var = new FastThreadLocal<Boolean>() {
      @Override
      protected void onRemoval(Boolean value) {
        removed.set(true);
      }
    };

    // Initialize a thread-local variable.
    Assert.assertEquals(var.get(), null);
    Assert.assertEquals(FastThreadLocal.size(), 1);

    // And then remove it.
    FastThreadLocal.removeAll();
    Assert.assertTrue(removed.get());
    Assert.assertEquals(FastThreadLocal.size(), 0);
  }

  @Test(timeout = 10000)
  public void testRemoveAllFromFTLThread() throws Throwable {
    final AtomicReference<Throwable> throwable = new AtomicReference<Throwable>();
    final Thread thread = new FastThreadLocalThread() {
      @Override
      public void run() {
        try {
          testRemoveAll();
        } catch (Throwable t) {
          throwable.set(t);
        }
      }
    };

    thread.start();
    thread.join();

    Throwable t = throwable.get();
    if (t != null) {
      throw t;
    }
  }
}
