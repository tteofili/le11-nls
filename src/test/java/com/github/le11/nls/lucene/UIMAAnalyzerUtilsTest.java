package com.github.le11.nls.lucene;

import org.junit.Test;

import java.io.StringReader;

import static org.junit.Assert.fail;

/**
 * @author tommaso
 * @version $Id$
 */
public class UIMAAnalyzerUtilsTest {
  @Test
  public void testAEReconfiguration() {
    try {
      UIMAAnalyzersUtils.analyzeInput(new StringReader("this is dummy"), "/NLSSearchAggregateAnnotator.xml");
      UIMAAnalyzersUtils.analyzeInput(new StringReader("this is dummy"), "/NLSSearchAggregateAnnotator.xml");
    } catch (Exception e) {
      e.printStackTrace();
      fail(e.getLocalizedMessage());
    }
  }
}
