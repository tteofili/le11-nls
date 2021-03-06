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
package com.github.le11.nls.solr;

import com.github.le11.nls.lucene.TypeScoreMap;
import org.apache.uima.cas.*;
import org.apache.uima.cas.text.AnnotationFS;
import org.apache.uima.cas.text.AnnotationIndex;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * @author tommaso
 */
public class NLSQueryAnalyzer {

  @SuppressWarnings("unused")
  private Float threshold;
  private CAS cas;
  private TypeScoreMap scoreMap;
  private final String[] entitiesFSTypes = new String[]{"org.apache.uima.alchemy.ts.entity.BaseEntity"};
  private final String[] entitiesAnnTypes = new String[]{"opennlp.uima.Person", "opennlp.uima.Location", "opennlp.uima.Organization"};

  public NLSQueryAnalyzer(CAS cas) {
    this.cas = cas;
    this.threshold = 0f;
    this.scoreMap = new TypeScoreMap();
  }

  public NLSQueryAnalyzer(CAS cas, Float threshold) {
    this.cas = cas;
    this.threshold = threshold;
    this.scoreMap = new TypeScoreMap();
  }


  public Boolean isNLSQuery(String qstr) {
    return !qstr.contains(":"); // TODO : this check should be much improved
  }

  public String extractPlaceQuery() {
    // TODO implement this
    return null;
  }

  public String[] extractConcepts() {
    Collection<String> concepts = new HashSet<String>();
    Type conceptsType = cas.getTypeSystem().getType("org.apache.uima.alchemy.ts.concept.ConceptFS");
    if (conceptsType != null) {
      FSIterator<FeatureStructure> conceptsIterator = cas.getIndexRepository().getAllIndexedFS(conceptsType);
      while (conceptsIterator.hasNext()) {
        FeatureStructure fs = conceptsIterator.next();
        concepts.add(fs.getStringValue(conceptsType.getFeatureByBaseName("text")));
      }
    }

    Type keywordsType = cas.getTypeSystem().getType("org.apache.uima.alchemy.ts.keywords.KeywordFS");
    if (keywordsType != null) {
      FSIterator<FeatureStructure> keywordsIterator = cas.getIndexRepository().getAllIndexedFS(keywordsType);
      while (keywordsIterator.hasNext()) {
        FeatureStructure fs = keywordsIterator.next();
        concepts.add(fs.getStringValue(keywordsType.getFeatureByBaseName("text")));
      }
    }
    String[] a = new String[concepts.size()];
    return concepts.toArray(a);
  }

  public Map<String, Collection<String>> extractEntities() {
    Map<String, Collection<String>> entitiesMap = new HashMap<String, Collection<String>>();
    for (String stringType : entitiesFSTypes) {
      Type entitiesType = cas.getTypeSystem().getType(stringType);

      if (entitiesType != null) {
        FSIterator<FeatureStructure> featureStructureFSIterator = cas.getIndexRepository().getAllIndexedFS(entitiesType);
        while (featureStructureFSIterator.hasNext()) {
          FeatureStructure fs = featureStructureFSIterator.next();
          String entityTypeName = fs.getType().getName();
          Collection<String> existingTypedEntitiesValues = entitiesMap.get(entityTypeName);
          String value = fs.getStringValue(entitiesType.getFeatureByBaseName("text"));
          if (existingTypedEntitiesValues != null) {
            existingTypedEntitiesValues.add(value);
          } else {
            existingTypedEntitiesValues = new HashSet<String>();
            existingTypedEntitiesValues.add(value);
            entitiesMap.put(entityTypeName, existingTypedEntitiesValues);
          }
        }
      }
    }
    for (String stringType : entitiesAnnTypes) {
      Type entitiesType = cas.getTypeSystem().getType(stringType);

      AnnotationIndex<AnnotationFS> annotationIndex = cas.getAnnotationIndex(entitiesType);
      for (AnnotationFS fs : annotationIndex) {
        String entityTypeName = fs.getType().getName();
        Collection<String> existingTypedEntitiesValues = entitiesMap.get(entityTypeName);
        String value = fs.getCoveredText();
        if (existingTypedEntitiesValues != null) {
          existingTypedEntitiesValues.add(value);
        } else {
          existingTypedEntitiesValues = new HashSet<String>();
          existingTypedEntitiesValues.add(value);
          entitiesMap.put(entityTypeName.substring(entityTypeName.lastIndexOf(".") + 1), existingTypedEntitiesValues);
        }
      }
    }
    return entitiesMap;
  }

  public String expandBoosts() {
    Type type = cas.getTypeSystem().getType("opennlp.uima.Token");
    FSIterator<AnnotationFS> annotationFSFSIterator = cas.getAnnotationIndex(type).iterator();
    StringBuilder boostedQueryBuilder = new StringBuilder();
    while (annotationFSFSIterator.hasNext()) {
      AnnotationFS a = annotationFSFSIterator.next();
      String word = a.getCoveredText();
      Feature posTag = type.getFeatureByBaseName("pos");
      String stringValue = a.getStringValue(posTag);
      Float boost = scoreMap.getScore(stringValue);
      boostedQueryBuilder.append(word);
      if (boost > 1.0f)
        boostedQueryBuilder.append("^").append(boost);
      if (annotationFSFSIterator.hasNext())
        boostedQueryBuilder.append(" ");
    }
    return boostedQueryBuilder.toString();
  }
}
