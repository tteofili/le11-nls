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
 * @version $Id$
 */
public class NLSQueryAnalyzer {

  private Float threshold;
  private CAS cas;
  private String qstring;
  TypeScoreMap scoreMap;
  private final String[] entitiesFSTypes = new String[]{"org.apache.uima.alchemy.ts.entity.BaseEntity"};
  private final String[] entitiesAnnTypes = new String[]{"opennlp.uima.Person", "opennlp.uima.Location"};

  public NLSQueryAnalyzer(CAS cas, String qstring) {
    this.cas = cas;
    this.qstring = qstring;
    this.threshold = 0f;
    this.scoreMap = new TypeScoreMap();
  }

  public NLSQueryAnalyzer(CAS cas, String qstring, Float threshold) {
    this.cas = cas;
    this.qstring = qstring;
    this.threshold = threshold;
    this.scoreMap = new TypeScoreMap();
  }


  public Boolean isNLSQuery() {
    // TODO implement this
    return true;
  }

  public String extractPlaceQuery() {
    // TODO implement this
    return null;
  }

  public String[] extractConcepts() {
    Collection<String> concepts = new HashSet<String>();
    Type conceptsType = cas.getTypeSystem().getType("org.apache.uima.alchemy.ts.concept.ConceptFS");
    FSIterator<FeatureStructure> conceptsIterator = cas.getIndexRepository().getAllIndexedFS(conceptsType);
    while (conceptsIterator.hasNext()) {
      FeatureStructure fs = conceptsIterator.next();
      concepts.add(fs.getStringValue(conceptsType.getFeatureByBaseName("text")));
    }
    Type keywordsType = cas.getTypeSystem().getType("org.apache.uima.alchemy.ts.keywords.KeywordFS");
    FSIterator<FeatureStructure> keywordsIterator = cas.getIndexRepository().getAllIndexedFS(keywordsType);
    while (keywordsIterator.hasNext()) {
      FeatureStructure fs = keywordsIterator.next();
      concepts.add(fs.getStringValue(keywordsType.getFeatureByBaseName("text")));
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
    Type type = cas.getTypeSystem().getType("org.apache.uima.TokenAnnotation");
    FSIterator<AnnotationFS> annotationFSFSIterator = cas.getAnnotationIndex(type).iterator();
    StringBuilder boostedQueryBuilder = new StringBuilder();
    while (annotationFSFSIterator.hasNext()) {
      AnnotationFS a = annotationFSFSIterator.next();
      String word = a.getCoveredText();
      Feature posTag = type.getFeatureByBaseName("posTag");
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
