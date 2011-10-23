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
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;
import org.junit.Test;

import java.io.StringReader;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

/**
 * @author tommaso
 */
public class UIMATypeAwareAnalyzerTest {
  @Test
  public void testSimpleUsage() {
    try {
      UIMATypeAwareAnalyzer analyzer = new UIMATypeAwareAnalyzer("/HmmTaggerAggregate.xml",
              "org.apache.uima.TokenAnnotation", "posTag");
      TokenStream ts = analyzer.tokenStream("text", new StringReader("the big brown fox jumped on the wood"));
      CharTermAttribute termAtt = ts.addAttribute(CharTermAttribute.class);
      OffsetAttribute offsetAtt = ts.addAttribute(OffsetAttribute.class);
      TypeAttribute typeAttr = ts.addAttribute(TypeAttribute.class);
      PositionIncrementAttribute posAtt = ts.addAttribute(PositionIncrementAttribute.class);
      while (ts.incrementToken()) {
        assertNotNull(offsetAtt);
        assertNotNull(termAtt);
        assertNotNull(posAtt);
        assertNotNull(typeAttr);
        System.out.println("token '" + termAtt.toString() + "' has type " + typeAttr.type());
      }
    } catch (Exception e) {
      e.printStackTrace();
      fail(e.getLocalizedMessage());
    }
  }
}
