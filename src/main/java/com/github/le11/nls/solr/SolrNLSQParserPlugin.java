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
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.Query;
import org.apache.solr.common.params.SolrParams;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.search.DisMaxQParser;
import org.apache.solr.search.DisMaxQParserPlugin;
import org.apache.solr.search.LuceneQParserPlugin;
import org.apache.solr.search.QParser;
import org.apache.uima.cas.CAS;

import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

/**
 * analyze user query
 * identify keyword vs nl queries (optional)
 * high boost to concept field
 * use types/payloads in the query for boost and similarity
 * consider token types when expanding synonims (i.e. entities/nouns/etc.)
 * identify place queries with solr spatial query functionality
 * search within sentences
 *
 * @author tommaso
 */
public class SolrNLSQParserPlugin extends DisMaxQParserPlugin {

  private Map<String, String> cache = new HashMap<String, String>();

  @Override
  public QParser createParser(String qstr, SolrParams localParams, SolrParams params, SolrQueryRequest req) {
    return new NLSQParser(qstr, localParams, params, req);
  }

  class NLSQParser extends DisMaxQParser {

    /**
     * Constructor for the QParser
     *
     * @param qstr        The part of the query string specific to this parser
     * @param localParams The set of parameters that are specific to this QParser.  See http://wiki.apache.org/solr/LocalParams
     * @param params      The rest of the {@link org.apache.solr.common.params.SolrParams}
     * @param req         The original {@link org.apache.solr.request.SolrQueryRequest}.
     */
    public NLSQParser(String qstr, SolrParams localParams, SolrParams params, SolrQueryRequest req) {
      super(qstr, localParams, params, req);
    }

    @Override
    public Query parse() throws ParseException {
      String explicitNLSQuery = cache.get(qstr);
      if (explicitNLSQuery == null) {
        CAS cas;
        // analyze the query
        try {
          cas = UIMAAnalyzersUtils.getInstance().analyzeAsynchronously(new StringReader(qstr), "OpenNLPQueue");
        } catch (Exception e) {
          e.printStackTrace();
          return super.parse();
        }

        NLSQueryAnalyzer nlsQueryAnalyzer = new NLSQueryAnalyzer(cas);
        if (nlsQueryAnalyzer.isNLSQuery()) {
          explicitNLSQuery = new NLSQueryTranslator().createNLSExplicitQueryString(qstr, nlsQueryAnalyzer);
          cache.put(qstr, explicitNLSQuery);
          return new LuceneQParserPlugin().createParser(explicitNLSQuery, localParams, params, req).parse();
        } else {
          return super.parse();
        }
      } else
        return new LuceneQParserPlugin().createParser(explicitNLSQuery, localParams, params, req).parse();

    }
  }
}
