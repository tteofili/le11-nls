package com.github.sedtum.lucene.payloads;

import com.github.sedtum.lucene.TypeScoreMap;
import org.apache.lucene.search.DefaultSimilarity;


public class UIMATypeBasedSimilarity extends DefaultSimilarity {

  private TypeScoreMap scoreMap;

  public UIMATypeBasedSimilarity() {
    this.scoreMap = new TypeScoreMap();
  }

  @Override
  public float scorePayload(int docId, String fieldName, int start, int end, byte[] payload, int offset, int length) {
    String[] ks = new String(payload).split("\\u0000");
    Float score = 1.0f;
    for (String k : ks) {
      score *= scoreMap.getScore(k);
      System.out.println("found score " + score + " for payload " + k);
    }
    return score;
  }
}
