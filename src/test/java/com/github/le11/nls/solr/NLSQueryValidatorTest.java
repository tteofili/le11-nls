package com.github.sedtum.solr;

import com.github.sedtum.lucene.UIMAAnalyzersUtils;
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
 * Testcase for {@link com.github.sedtum.solr.NLSQueryAnalyzer}
 *
 * @author tommaso
 * @version $Id$
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
    NLSQueryAnalyzer queryAnalyzer = new NLSQueryAnalyzer(cas, query);
    assertTrue("'" + query + "'" + FAILSTRING, queryAnalyzer.isNLSQuery());
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
    return UIMAAnalyzersUtils.analyzeInput(new StringReader(query),
            "/HmmTaggerAggregate.xml");
  }

}
