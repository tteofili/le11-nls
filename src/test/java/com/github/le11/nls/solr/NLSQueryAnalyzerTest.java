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

import com.github.le11.nls.lucene.UIMAAnalyzersUtils;
import org.apache.uima.cas.CAS;
import org.junit.Test;

import java.io.StringReader;
import java.util.Collection;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * @author tommaso
 */
public class NLSQueryAnalyzerTest {
  @Test
  public void testBoostExpansion() {
    try {
      String qstr = "people working at Google Amsterdam office";
      CAS cas = UIMAAnalyzersUtils.getInstance().analyzeInput(new StringReader(qstr), "/OpenNlpTextAnalyzer.xml");
      NLSQueryAnalyzer nlsQueryAnalyzer = new NLSQueryAnalyzer(cas);
      String expandedQuery = nlsQueryAnalyzer.expandBoosts();
      assertNotNull(expandedQuery);
      assertEquals("people working at Google^5.0 Amsterdam^5.0 office", expandedQuery);
    } catch (Exception e) {
      e.printStackTrace();
      fail(e.getLocalizedMessage());
    }
  }

  @Test
  public void testEntitiesExtraction() {
    try {
      String qstr = "was Albert Einstein living in Paris ?";
      CAS cas = UIMAAnalyzersUtils.getInstance().analyzeInput(new StringReader(qstr), "/OpenNlpTextAnalyzer.xml");
      NLSQueryAnalyzer nlsQueryAnalyzer = new NLSQueryAnalyzer(cas);
      Map<String, Collection<String>> entitiesMap = nlsQueryAnalyzer.extractEntities();
      assertNotNull(entitiesMap);
      assertTrue(!entitiesMap.isEmpty());
      for (String k : entitiesMap.keySet()) {
        for (String en : entitiesMap.get(k)) {
          System.out.println(k + " : " + en);
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
      fail(e.getLocalizedMessage());
    }
  }


}
