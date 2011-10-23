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

import com.github.le11.nls.lucene.payloads.UIMAPayloadsAnalyzer;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.PayloadAttribute;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.StringReader;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

/**
 * Testcase for {@link com.github.le11.nls.lucene.payloads.UIMAPayloadsAnalyzer}
 */
public class UIMAPayloadsAnalyzerTest {
  private Analyzer analyzer;
  private IndexWriter writer;
  private RAMDirectory dir;

  @Before
  public void setUp() throws Exception {
    dir = new RAMDirectory();
    analyzer = new UIMAPayloadsAnalyzer("/HmmTaggerAggregate.xml");
    writer = new IndexWriter(dir, new IndexWriterConfig(Version.LUCENE_33, analyzer));
  }

  @After
  public void tearDown() throws Exception {
    writer.close();
  }

  @Test
  public void baseUIMAPayloadsAnalyzerStreamTest() {
    try {
      TokenStream ts = analyzer.tokenStream("text", new StringReader("the big brown fox jumped on the wood"));
      CharTermAttribute termAtt = ts.addAttribute(CharTermAttribute.class);
      PayloadAttribute payloadAttribute = ts.addAttribute(PayloadAttribute.class);
      while (ts.incrementToken()) {
        assertNotNull(termAtt);
        assertNotNull(payloadAttribute);
        System.out.println("token '" + termAtt.toString() + "' has payload " + new String(payloadAttribute.getPayload().getData()));
      }
    } catch (Exception e) {
      e.printStackTrace();
      fail(e.getLocalizedMessage());
    }
  }

}
