package com.github.sedtum.solr;

import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.*;
import org.apache.lucene.search.payloads.MaxPayloadFunction;
import org.apache.lucene.search.payloads.PayloadFunction;
import org.apache.lucene.search.payloads.PayloadNearQuery;
import org.apache.lucene.search.payloads.PayloadTermQuery;
import org.apache.lucene.search.spans.SpanQuery;
import org.apache.solr.common.params.DisMaxParams;
import org.apache.solr.common.params.SolrParams;
import org.apache.solr.common.util.NamedList;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.search.DisMaxQParser;
import org.apache.solr.search.ExtendedDismaxQParserPlugin;
import org.apache.solr.search.QParser;
import org.apache.solr.util.SolrPluginUtils;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Modified query parser for dismax queries which uses payloads
 */
public class PayloadDisMaxQParserPlugin extends ExtendedDismaxQParserPlugin {

  @Override
  public QParser createParser(String qstr, SolrParams localParams, SolrParams params, SolrQueryRequest req) {
    return new PayloadDisMaxQParser(qstr, localParams, params, req);
  }

  class PayloadDisMaxQParser extends DisMaxQParser {
    public static final String PAYLOAD_FIELDS_PARAM_NAME = "plf";

    public PayloadDisMaxQParser(String qstr, SolrParams localParams,
                                SolrParams params, SolrQueryRequest req) {
      super(qstr, localParams, params, req);
    }

    protected HashSet<String> payloadFields = new HashSet<String>();

    private final PayloadFunction func = new MaxPayloadFunction();

    float tiebreaker = 0f;

    protected void addMainQuery(BooleanQuery query, SolrParams solrParams)
            throws ParseException {
      Map<String, Float> phraseFields = SolrPluginUtils
              .parseFieldBoosts(solrParams.getParams(DisMaxParams.PF));

      tiebreaker = solrParams.getFloat(DisMaxParams.TIE, 0.0f);

      // get the comma separated list of fields used for payload
      String[] plfarray = solrParams.get(PAYLOAD_FIELDS_PARAM_NAME, "")
              .split(",");
      for (String plf : plfarray)
        payloadFields.add(plf.trim());

      /*
      * a parser for dealing with user input, which will convert things to
      * DisjunctionMaxQueries
      */
      SolrPluginUtils.DisjunctionMaxQueryParser up = getParser(queryFields,
              DisMaxParams.QS, solrParams, tiebreaker);

      /* for parsing sloppy phrases using DisjunctionMaxQueries */
      SolrPluginUtils.DisjunctionMaxQueryParser pp = getParser(phraseFields,
              DisMaxParams.PS, solrParams, tiebreaker);

      /*               * * Main User Query * * */
      parsedUserQuery = null;
      String userQuery = getString();
      altUserQuery = null;
      if (userQuery == null || userQuery.trim().length() < 1) {
        // If no query is specified, we may have an alternate
        altUserQuery = getAlternateUserQuery(solrParams);
        query.add(altUserQuery, BooleanClause.Occur.MUST);
      } else {
        // There is a valid query string
        userQuery = SolrPluginUtils.partialEscape(
                SolrPluginUtils.stripUnbalancedQuotes(userQuery))
                .toString();
        userQuery = SolrPluginUtils.stripIllegalOperators(userQuery)
                .toString();

        parsedUserQuery = getUserQuery(userQuery, up, solrParams);

        // recursively rewrite the elements of the query
        Query payloadedUserQuery = rewriteQueriesAsPLQueries(parsedUserQuery);
        query.add(payloadedUserQuery, BooleanClause.Occur.MUST);

        Query phrase = getPhraseQuery(userQuery, pp);
        if (null != phrase) {
          query.add(phrase, BooleanClause.Occur.SHOULD);
        }
      }
    }

    /**
     * Substitutes original query objects with payload ones *
     */
    private Query rewriteQueriesAsPLQueries(Query input) {
      Query output = input;
      // rewrite TermQueries
      if (input instanceof TermQuery) {
        Term term = ((TermQuery) input).getTerm();

        // check that this is done on a field that has payloads
        if (payloadFields.contains(term.field()) == false)
          return input;

        output = new PayloadTermQuery(term, func);
      }
      // rewrite PhraseQueries
      else if (input instanceof PhraseQuery) {
        PhraseQuery pin = (PhraseQuery) input;
        Term[] terms = pin.getTerms();
        int slop = pin.getSlop();
        boolean inorder = false;

        // check that this is done on a field that has payloads
        if (terms.length > 0
                && payloadFields.contains(terms[0].field()) == false)
          return input;

        SpanQuery[] clauses = new SpanQuery[terms.length];
        // phrase queries : keep the default function i.e. average
        for (int i = 0; i < terms.length; i++)
          clauses[i] = new PayloadTermQuery(terms[i], func);

        output = new PayloadNearQuery(clauses, slop, inorder);
      }
      // recursively rewrite DJMQs
      else if (input instanceof DisjunctionMaxQuery) {
        DisjunctionMaxQuery s = ((DisjunctionMaxQuery) input);
        DisjunctionMaxQuery t = new DisjunctionMaxQuery(tiebreaker);
        Iterator<Query> disjunctsiterator = s.iterator();
        while (disjunctsiterator.hasNext()) {
          Query rewrittenQuery = rewriteQueriesAsPLQueries(disjunctsiterator
                  .next());
          t.add(rewrittenQuery);
        }
        output = t;
      }
      // recursively rewrite BooleanQueries
      else if (input instanceof BooleanQuery) {
        for (BooleanClause clause : (List<BooleanClause>) ((BooleanQuery) input)
                .clauses()) {
          Query rewrittenQuery = rewriteQueriesAsPLQueries(clause
                  .getQuery());
          clause.setQuery(rewrittenQuery);
        }
      }

      output.setBoost(input.getBoost());
      return output;
    }

    public void addDebugInfo(NamedList<Object> debugInfo) {
      super.addDebugInfo(debugInfo);
      if (this.payloadFields.size() > 0) {
        Iterator<String> iter = this.payloadFields.iterator();
        while (iter.hasNext())
          debugInfo.add("payloadField", iter.next());
      }
    }
  }
}
