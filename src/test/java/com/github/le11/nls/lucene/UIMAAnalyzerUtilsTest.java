package com.github.le11.nls.lucene;

import org.apache.uima.cas.CAS;
import org.apache.uima.cas.text.AnnotationFS;
import org.apache.uima.cas.text.AnnotationIndex;
import org.junit.Test;

import java.io.StringReader;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

/**
 * @author tommaso
 * @version $Id$
 */
public class UIMAAnalyzerUtilsTest {
  @Test
  public void testAEReconfiguration() {
    try {
      UIMAAnalyzersUtils.getInstance().analyzeInput(new StringReader("this is dummy"), "/NLSSearchAggregateAnnotator.xml");
      UIMAAnalyzersUtils.getInstance().analyzeInput(new StringReader("this is dummy"), "/NLSSearchAggregateAnnotator.xml");
    } catch (Exception e) {
      e.printStackTrace();
      fail(e.getLocalizedMessage());
    }
  }

  @Test
  public void testAsyncCall() {
    try {
      CAS cas = UIMAAnalyzersUtils.getInstance().analyzeAsynchronously(new StringReader("Google has some offices in " +
              "the world, but not in Rome"), "OpenNLPQueue");
      assertNotNull(cas);
      AnnotationIndex<AnnotationFS> annotationIndex = cas.getAnnotationIndex();
      System.err.println(annotationIndex.size());
      for (AnnotationFS a : annotationIndex) {
        System.err.println(a.getCoveredText() + " ");
      }

    } catch (Exception e) {
      e.printStackTrace();
      fail(e.getLocalizedMessage());
    }
  }
}
