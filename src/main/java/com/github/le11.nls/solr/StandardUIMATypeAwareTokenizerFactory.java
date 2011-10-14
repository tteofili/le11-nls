package com.github.sedtum.solr;

import com.github.sedtum.lucene.UIMATypeAwareTokenizer;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.solr.analysis.BaseTokenizerFactory;

import java.io.Reader;

/**
 * @author tommaso
 * @version $Id$
 */
public class StandardUIMATypeAwareTokenizerFactory extends BaseTokenizerFactory {
  @Override
  public Tokenizer create(Reader input) {
    return new UIMATypeAwareTokenizer("/HmmTaggerAggregate.xml", "org.apache.uima.TokenAnnotation", "posTag", input);
  }
}
