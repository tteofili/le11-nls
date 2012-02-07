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
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CAS;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.InvalidXMLException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.IOException;
import java.io.StringReader;
import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertTrue;

/**
 * Testcase for {@link com.github.le11.nls.solr.NLSQueryAnalyzer}
 *
 * @author tommaso
 */
@RunWith(Parameterized.class)
public class NLSQueryValidatorTest {

  private static final String FAILSTRING = " should be identified as a NL query";

  private String query;

  private CAS cas;

  public NLSQueryValidatorTest(String query, CAS cas) {
    this.query = query;
    this.cas = cas;
  }

  @Test
  public void testNLSQuery() {
    NLSQueryAnalyzer queryAnalyzer = new NLSQueryAnalyzer(cas);
    assertTrue("'" + query + "'" + FAILSTRING, queryAnalyzer.isNLSQuery(query));
  }

  @Parameterized.Parameters
  public static Collection<Object[]> provideNLSQuery() throws AnalysisEngineProcessException,
          IOException, ResourceInitializationException, InvalidXMLException {
    Object[][] data = new Object[][]{prepareData("people working at Google"),
            prepareData("places in Rome"), prepareData("cities near Milan"),
            prepareData("where is Rome?"), prepareData("what is a neural network?"),
            prepareData("what is a neural network"), prepareData("how do I become a super hero?"),
            prepareData("movies with kevin spacey"), prepareData("movies by k.spacey"),
            prepareData("books about artificial intelligence")};
    return Arrays.asList(data);

  }

  private static Object[] prepareData(String query) throws AnalysisEngineProcessException,
          IOException, ResourceInitializationException, InvalidXMLException {
    return new Object[]{query, posTagQuery(query)};
  }

  private static CAS posTagQuery(String query) throws IOException, InvalidXMLException,
          ResourceInitializationException, AnalysisEngineProcessException {
    return UIMAAnalyzersUtils.getInstance().analyzeInput(new StringReader(query),
            "/HmmTaggerAggregate.xml");
  }

}
