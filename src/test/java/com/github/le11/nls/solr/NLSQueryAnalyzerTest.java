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
 * @version $Id$
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
