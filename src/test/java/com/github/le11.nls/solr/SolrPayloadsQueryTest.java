package com.github.sedtum.solr;

import org.apache.solr.request.LocalSolrQueryRequest;
import org.apache.solr.util.TestHarness;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * @author tommaso
 * @version $Id$
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
      LocalSolrQueryRequest request = testHarness.getRequestFactory("plq", 0, 10).makeRequest("q","solr new york","debugQuery","true");
      String response = testHarness.query("plq", request);
      assertTrue(response != null);
      System.out.println(response);
    } catch (Exception e) {
      e.printStackTrace();
      fail(e.getLocalizedMessage());
    }

  }
}
