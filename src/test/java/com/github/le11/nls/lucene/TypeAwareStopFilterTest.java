package com.github.le11.nls.lucene;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.util.Version;
import org.junit.Test;

import java.io.StringReader;
import java.util.Arrays;
import java.util.HashSet;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * @author tommaso
 * @version $Id$
 */
public class TypeAwareStopFilterTest {
  @Test
  public void testTypeBasedStop() {
    try {
      TokenStream tokenStream = new StandardTokenizer(Version.LUCENE_34, new StringReader("hey, stop and think!"));
      TypeAwareStopFilter typeAwareStopFilter = new TypeAwareStopFilter(Version.LUCENE_34, tokenStream, new
              HashSet<String>(), true, Arrays.asList(new String[]{"word"}));
      assertTrue(!typeAwareStopFilter.accept());
      assertTrue(!typeAwareStopFilter.accept());
      assertTrue(!typeAwareStopFilter.accept());
    } catch (Exception e) {
      e.printStackTrace();
      fail(e.getLocalizedMessage());
    }
  }
}
