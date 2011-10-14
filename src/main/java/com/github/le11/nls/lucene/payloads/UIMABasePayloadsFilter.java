package com.github.sedtum.lucene.payloads;

import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.PayloadAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;
import org.apache.lucene.index.Payload;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.*;
import org.apache.uima.cas.text.AnnotationFS;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.InvalidXMLException;

import java.io.IOException;

public class UIMABasePayloadsFilter extends TokenFilter {

  private CharTermAttribute termAttr;

  private PayloadAttribute payloadAttr;

  private TypeAttribute typeAttr;

  private Payload uimaPayload;


  private CAS cas;
  private FeaturePath featurePath;
  private String featurePathString;
  private String annotationTypeString;
  private FSIterator<AnnotationFS> iterator;

  protected UIMABasePayloadsFilter(TokenStream input, CAS cas, String annotationTypeString, String featurePathString) {
    super(input);
    // initialize attributes
    payloadAttr = addAttribute(PayloadAttribute.class);
    termAttr = addAttribute(CharTermAttribute.class);
    typeAttr = addAttribute(TypeAttribute.class);
    // initialize UIMA fields
    this.featurePathString = featurePathString;
    this.annotationTypeString = annotationTypeString;
    this.cas = cas;
  }

  private void analyzeText() throws InvalidXMLException,
          IOException, ResourceInitializationException, AnalysisEngineProcessException, CASException {
    Type tokenType = cas.getTypeSystem().getType(this.annotationTypeString);
    iterator = cas.getAnnotationIndex(tokenType).iterator();
    featurePath = cas.createFeaturePath();
    featurePath.initialize(this.featurePathString);
  }

  public final boolean incrementToken() throws IOException {
    // TODO implement this
    if (iterator == null) {
      try {
        analyzeText();
      } catch (Exception e) {
        throw new IOException(e);
      }
    }

    if (input.incrementToken()) {
      if (iterator.hasNext()) {
        AnnotationFS annotationFS = iterator.next();
//        if (featurePath.getStringValue(annotationFS)) {
//
//        }
      }
    }


    if (input.incrementToken()) {
      if (termAttr.buffer().toString().equals("warning")) {
        payloadAttr.setPayload(null);
      } else {
        payloadAttr.setPayload(null);
      }
      return true;
    } else
      return false;
  }

}
