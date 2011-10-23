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
package com.github.le11.nls.solr;

import org.apache.lucene.search.Query;
import org.apache.solr.common.params.MapSolrParams;
import org.apache.solr.request.LocalSolrQueryRequest;
import org.apache.solr.search.QParser;
import org.apache.solr.util.TestHarness;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

/**
 * Testcase for {@link SolrNLSQParserPlugin}
 *
 * @author tommaso
 */
public class SolrNLSQParserPluginTest {
  private TestHarness testHarness;

  @Before
  public void setUp() {
    testHarness = new TestHarness("target/solrnls/data", "src/test/resources/solr/conf/solrconfig.xml",
            "src/test/resources/solr/conf/schema.xml");
  }

  @Test
  public void testSimple() {
    try {
      SolrNLSQParserPlugin solrNLSQParserPlugin = new SolrNLSQParserPlugin();
      LocalSolrQueryRequest request = testHarness.getRequestFactory("standard", 0, 10).makeRequest("q", "\"people working at Google Amsterdam office\"", "debugQuery", "true");
      QParser nlsQParser = solrNLSQParserPlugin.createParser("people working at Google Amsterdam office", new
              MapSolrParams(new HashMap<String, String>()), new MapSolrParams(new HashMap<String, String>()),
              request);
      Query q = nlsQParser.parse();
      assertNotNull(q);
      System.out.println(q.toString());
    } catch (Exception e) {
      e.printStackTrace();
      fail(e.getLocalizedMessage());
    }
  }

}
