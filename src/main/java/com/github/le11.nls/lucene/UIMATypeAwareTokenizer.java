package com.github.sedtum.lucene;

import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.*;
import org.apache.uima.cas.text.AnnotationFS;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.InvalidXMLException;

import java.io.IOException;
import java.io.Reader;

/**
 * A {@link Tokenizer} which creates token from UIMA Annotations, filling also they {@link TypeAttribute} evaluating
 * {@link FeaturePath}s
 */
public final class UIMATypeAwareTokenizer extends Tokenizer {

  private TypeAttribute typeAttr;

  private CharTermAttribute termAttr;

  private OffsetAttribute offsetAttr;

  private PositionIncrementAttribute positionIncrementAttr;

  private FSIterator<AnnotationFS> iterator;

  private String tokenTypeString;

  private String descriptorPath;

  private String typeAttributeFeaturePath;

  private FeaturePath featurePath;

  public UIMATypeAwareTokenizer(String descriptorPath, String tokenType, String typeAttributeFeaturePath, Reader input) {
    super(input);
    this.tokenTypeString = tokenType;
    this.termAttr = addAttribute(CharTermAttribute.class);
    this.typeAttr = addAttribute(TypeAttribute.class);
    this.offsetAttr = addAttribute(OffsetAttribute.class);
    this.positionIncrementAttr = addAttribute(PositionIncrementAttribute.class);
    this.typeAttributeFeaturePath = typeAttributeFeaturePath;
    this.descriptorPath = descriptorPath;
  }

  private void analyzeText() throws InvalidXMLException,
          IOException, ResourceInitializationException, AnalysisEngineProcessException, CASException {
    CAS cas = UIMAAnalyzersUtils.analyzeInput(input, descriptorPath);
    Type tokenType = cas.getTypeSystem().getType(tokenTypeString);
    iterator = cas.getAnnotationIndex(tokenType).iterator();
    featurePath = cas.createFeaturePath();
    featurePath.initialize(this.typeAttributeFeaturePath);
  }

  @Override
  public boolean incrementToken() throws IOException {
    if (iterator == null) {
      try {
        analyzeText();
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
      typeAttr.setType(featurePath.getValueAsString(next));
      positionIncrementAttr.setPositionIncrement(next.getEnd() - next.getBegin());
      return true;
    } else {
      iterator = null;
      return false;
    }
  }

}
