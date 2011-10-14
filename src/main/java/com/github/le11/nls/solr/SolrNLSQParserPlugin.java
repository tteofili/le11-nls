package com.github.sedtum.solr;

import com.github.sedtum.lucene.UIMAAnalyzersUtils;
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

/**
 * derived from edismax
 * analyze user query
 * identify keyword vs nl queries (optional)
 * high boost to concept field
 * use types/payloads in the query for boost and similarity
 * consider token types when expanding synonims (i.e. entities/nouns/etc.)
 * identify place queries with solr spatial query functionality
 * search within sentences
 *
 * @author tommaso
 * @version $Id$
 */
public class SolrNLSQParserPlugin extends DisMaxQParserPlugin {

  @Override
  public QParser createParser(String qstr, SolrParams localParams, SolrParams params, SolrQueryRequest req) {
    return new NLSQParser(qstr, localParams, params, req);
  }

  class NLSQParser extends DisMaxQParser {

    private CAS cas;

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
      // analyze the query
      try {
//        cas = UIMAAnalyzersUtils.analyzeInput(new StringReader(qstr), String.valueOf(localParams.get("descriptor")));
        cas = UIMAAnalyzersUtils.analyzeInput(new StringReader(qstr), "/NLSSearchAggregateAnnotator.xml");
      } catch (Exception e) {
        e.printStackTrace();
        return super.parse();
      }

      NLSQueryAnalyzer nlsQueryAnalyzer = new NLSQueryAnalyzer(cas, qstr);
      if (nlsQueryAnalyzer.isNLSQuery()) {
        String explicitNLSQuery = new NLSQueryTranslator().createNLSExplicitQueryString(qstr, nlsQueryAnalyzer);
        return new LuceneQParserPlugin().createParser(explicitNLSQuery, localParams, params, req).parse();
      } else {
        return super.parse();
      }


    }
  }
}
