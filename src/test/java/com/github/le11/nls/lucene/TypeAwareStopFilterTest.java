/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.github.le11.nls.lucene;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.util.Version;
import org.junit.Test;

import java.io.StringReader;
import java.util.Arrays;
import java.util.HashSet;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * @author tommaso
 */
public class TypeAwareStopFilterTest {
  @Test
  public void testTypeBasedStop() {
    try {
      TokenStream tokenStream = new StandardTokenizer(Version.LUCENE_34, new StringReader("hey, stop and think!"));
      TypeAwareStopFilter typeAwareStopFilter = new TypeAwareStopFilter(Version.LUCENE_34, tokenStream, new
              HashSet<String>(), true, Arrays.asList(new String[]{"word"}));
      assertTrue(!typeAwareStopFilter.accept());
      assertTrue(!typeAwareStopFilter.accept());
      assertTrue(!typeAwareStopFilter.accept());
    } catch (Exception e) {
      e.printStackTrace();
      fail(e.getLocalizedMessage());
    }
  }
}
