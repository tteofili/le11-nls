/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.github.le11.nls.lucene.payloads;

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
    if (iterator == null) {
      try {
        analyzeText();
      } catch (Exception e) {
        throw new IOException(e);
      }
    }
    // TODO implement this
    return false;
  }

}
