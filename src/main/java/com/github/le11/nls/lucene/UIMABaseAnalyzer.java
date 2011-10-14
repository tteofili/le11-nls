package com.github.sedtum.lucene;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;

import java.io.Reader;

/**
 * An {@link Analyzer} which use the {@link UIMABaseTokenizer} for creating tokens
 */
public final class UIMABaseAnalyzer extends Analyzer {

  private String descriptorPath;
  private String tokenType;

  public UIMABaseAnalyzer(String descriptorPath, String tokenType) {
    this.descriptorPath = descriptorPath;
    this.tokenType = tokenType;
  }

  @Override
  public TokenStream tokenStream(String fieldName, Reader reader) {
    return new UIMABaseTokenizer(descriptorPath, tokenType, reader);
  }

}
