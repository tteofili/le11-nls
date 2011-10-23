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

import org.apache.solr.request.LocalSolrQueryRequest;
import org.apache.solr.util.TestHarness;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * @author tommaso
 */
public class SolrPayloadsQueryTest {
  private TestHarness testHarness;

  @Before
  public void setUp() {
    testHarness = new TestHarness("target/solr/data", "src/test/resources/solr/conf/solrconfig.xml",
            "src/test/resources/solr/conf/schema.xml");
  }

  @Test
  public void testQuery() {
    try {
      testHarness.validateAddDoc("id", "1", "title", "this is a solr document title", "text_uima", "I'm Tommaso and I live in Rome");
      testHarness.validateAddDoc("id", "2", "title", "another solr title", "text_uima", "I'm a guy living in Rome");
      testHarness.validateUpdate("<commit/>");
      LocalSolrQueryRequest request = testHarness.getRequestFactory("plq", 0, 10).makeRequest("q", "solr new york", "debugQuery", "true");
      String response = testHarness.query("plq", request);
      assertTrue(response != null);
      System.out.println(response);
    } catch (Exception e) {
      e.printStackTrace();
      fail(e.getLocalizedMessage());
    }

  }
}
