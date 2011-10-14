package com.github.sedtum.lucene;

import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.text.AnnotationFS;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.InvalidXMLException;

import java.io.IOException;
import java.io.Reader;

/**
 * a {@link Tokenizer} which creates tokens from UIMA Annotations
 */
public final class UIMABaseTokenizer extends Tokenizer {

  private CharTermAttribute termAttr;

  private OffsetAttribute offsetAttr;

  private FSIterator<AnnotationFS> iterator;

  private String tokenTypeString;

  private String descriptorPath;

  public UIMABaseTokenizer(String descriptorPath, String tokenType, Reader input) {
    super(input);
    this.tokenTypeString = tokenType;
    this.termAttr = addAttribute(CharTermAttribute.class);
    this.offsetAttr = addAttribute(OffsetAttribute.class);
    this.descriptorPath = descriptorPath;
  }

  private void analyzeText(Reader input, String descriptorPath) throws InvalidXMLException,
          IOException, ResourceInitializationException, AnalysisEngineProcessException {
    CAS cas = UIMAAnalyzersUtils.analyzeInput(input, descriptorPath);
    Type tokenType = cas.getTypeSystem().getType(this.tokenTypeString);
    iterator = cas.getAnnotationIndex(tokenType).iterator();
  }

  @Override
  public boolean incrementToken() throws IOException {
    if (iterator == null) {
      try {
        analyzeText(input, descriptorPath);
      } catch (Exception e) {
        throw new IOException(e);
      }
    }
    if (iterator.hasNext()) {
      AnnotationFS next = iterator.next();
      termAttr.setEmpty();
      termAttr.append(next.getCoveredText());
      termAttr.setLength(next.getCoveredText().length());
      offsetAttr.setOffset(next.getBegin(), next.getEnd());
      return true;
    } else {
      iterator = null;
      return false;
    }
  }

}
