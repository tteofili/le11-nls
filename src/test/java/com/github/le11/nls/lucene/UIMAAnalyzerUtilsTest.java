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

import org.apache.uima.cas.CAS;
import org.apache.uima.cas.text.AnnotationFS;
import org.apache.uima.cas.text.AnnotationIndex;
import org.junit.Test;

import java.io.StringReader;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

/**
 * @author tommaso
 */
public class UIMAAnalyzerUtilsTest {
  @Test
  public void testAEReconfiguration() {
    try {
      UIMAAnalyzersUtils.getInstance().analyzeInput(new StringReader("this is dummy"), "/NLSSearchAggregateAnnotator.xml");
      UIMAAnalyzersUtils.getInstance().analyzeInput(new StringReader("this is dummy"), "/NLSSearchAggregateAnnotator.xml");
    } catch (Exception e) {
      e.printStackTrace();
      fail(e.getLocalizedMessage());
    }
  }

  @Test
  public void testAsyncCall() {
    try {
      CAS cas = UIMAAnalyzersUtils.getInstance().analyzeAsynchronously(new StringReader("Google has some offices in " +
              "the world, but not in Rome"), "OpenNLPQueue");
      assertNotNull(cas);
      AnnotationIndex<AnnotationFS> annotationIndex = cas.getAnnotationIndex();
      System.err.println(annotationIndex.size());
      for (AnnotationFS a : annotationIndex) {
        System.err.println(a.getCoveredText() + " ");
      }

    } catch (Exception e) {
      e.printStackTrace();
      fail(e.getLocalizedMessage());
    }
  }
}
