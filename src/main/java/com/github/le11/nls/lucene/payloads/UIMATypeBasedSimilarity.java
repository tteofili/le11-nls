package com.github.le11.nls.lucene.payloads;

import com.github.le11.nls.lucene.TypeScoreMap;
import org.apache.lucene.search.DefaultSimilarity;


public class UIMATypeBasedSimilarity extends DefaultSimilarity {

  private static final long serialVersionUID = 1L;

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
