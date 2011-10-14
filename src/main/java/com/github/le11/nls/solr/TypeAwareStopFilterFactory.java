package com.github.sedtum.solr;

import com.github.sedtum.lucene.TypeAwareStopFilter;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.StopAnalyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.solr.analysis.BaseTokenFilterFactory;
import org.apache.solr.common.ResourceLoader;
import org.apache.solr.util.plugin.ResourceLoaderAware;

import java.io.IOException;
import java.util.Set;

/**
 * @author tommaso
 * @version $Id$
 */
public class TypeAwareStopFilterFactory extends BaseTokenFilterFactory implements ResourceLoaderAware {
  public void inform(ResourceLoader loader) {
    String stopWordFiles = args.get("words");
    ignoreCase = getBoolean("ignoreCase", false);
    enablePositionIncrements = getBoolean("enablePositionIncrements", false);

    if (stopWordFiles != null) {
      try {
        stopWords = getWordSet(loader, stopWordFiles, ignoreCase);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    } else {
      stopWords = new CharArraySet(luceneMatchVersion, StopAnalyzer.ENGLISH_STOP_WORDS_SET, ignoreCase);
    }
  }

  private CharArraySet stopWords;
  private boolean ignoreCase;
  private boolean enablePositionIncrements;

  public boolean isEnablePositionIncrements() {
    return enablePositionIncrements;
  }

  public boolean isIgnoreCase() {
    return ignoreCase;
  }

  public Set<?> getStopWords() {
    return stopWords;
  }

  @Override
  public TokenStream create(TokenStream input) {
    TypeAwareStopFilter stopFilter = new TypeAwareStopFilter(luceneMatchVersion, input, stopWords, ignoreCase);
    stopFilter.setEnablePositionIncrements(enablePositionIncrements);
    return stopFilter;
  }
}
