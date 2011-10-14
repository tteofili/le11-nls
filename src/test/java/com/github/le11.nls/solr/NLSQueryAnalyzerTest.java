package com.github.sedtum.solr;

import com.github.sedtum.lucene.UIMAAnalyzersUtils;
import org.apache.uima.cas.CAS;
import org.junit.Test;

import java.io.StringReader;

import static org.junit.Assert.*;

/**
 * @author tommaso
 * @version $Id$
 */
public class NLSQueryAnalyzerTest {
  @Test
  public void testBoostExpansion() {
    try {
      String qstr = "people working at Google Amsterdam office";
      CAS cas = UIMAAnalyzersUtils.analyzeInput(new StringReader(qstr), "/HmmTaggerAggregate.xml");
      NLSQueryAnalyzer nlsQueryAnalyzer = new NLSQueryAnalyzer(cas, qstr);
      String expandedQuery = nlsQueryAnalyzer.expandBoosts();
      assertNotNull(expandedQuery);
      assertEquals("people working at Google^5.0 Amsterdam^5.0 office", expandedQuery);
    } catch (Exception e) {
      e.printStackTrace();
      fail(e.getLocalizedMessage());
    }
  }
}
