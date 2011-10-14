package com.github.sedtum.lucene;

import java.util.HashMap;
import java.util.Map;

/**
 * A {@link Map} for retrieving the score associated to a given POS Tag.
 */
public class TypeScoreMap {

  private Map<String, Float> typeMapping;

  /**
   * create a type-score map with the default values
   */
  public TypeScoreMap() {
    initialize();
  }

  /**
   * constructor to create a custom type-scores mapping
   *
   * @param map
   */
  public TypeScoreMap(Map<String, Float> map) {
    this.typeMapping = map;
  }

  private void initialize() {
    this.typeMapping = new HashMap<String, Float>();

    this.typeMapping.put("jj", 1f); // big, brown, tree, Real, French
    this.typeMapping.put("vb", 1f); // approach, speak
    this.typeMapping.put("vv", 1f);
    this.typeMapping.put(".", 1f);  // .
    this.typeMapping.put("cs", 1f); // that, as, if
    this.typeMapping.put("nn", 1f); // fox, chief, interview, Monday
    this.typeMapping.put("(", 1f);  // "
    this.typeMapping.put("in", 1f); // on, to, from,
    this.typeMapping.put("rb", 1f); // away, instead, only
    this.typeMapping.put("at", 1f); // the, a
    this.typeMapping.put("cc", 1f); // or, and
    this.typeMapping.put("vbg", 1f); // accepting, following
    this.typeMapping.put("np", 5f); // Madrid, England
    this.typeMapping.put(":", 1f); // :
    this.typeMapping.put("od", 1f); // first
    this.typeMapping.put("vbd", 1f); // jumped, claimed, chose
    this.typeMapping.put("wdt", 1f);
    this.typeMapping.put("'", 1f); // '
    this.typeMapping.put("pps", 1f); // he, she
    this.typeMapping.put("nns", 1f); // hours, clubs
    this.typeMapping.put("ap", 1f); // former, other, more
    this.typeMapping.put("vbz", 1f); // claims
    this.typeMapping.put("md", 1f); // would
    this.typeMapping.put("bez", 1f); // is
    this.typeMapping.put("*", 1f); // not
    this.typeMapping.put("vbn", 1f); // concerned, won
    this.typeMapping.put("cd", 1f); // one, 100
    this.typeMapping.put("dod", 1f); // did
    this.typeMapping.put("do", 1f); // do
    this.typeMapping.put("ppss", 1f); // I, they
    this.typeMapping.put(",", 1f); // ,
    this.typeMapping.put("to", 1f); // to
    this.typeMapping.put("pp$", 1f); // my
    this.typeMapping.put("dts", 1f); // these
    this.typeMapping.put("ber", 1f); // are
    this.typeMapping.put("ppo", 1f); // you, it, It, me
    this.typeMapping.put("dt", 1f); // this
    this.typeMapping.put("bdez", 1f); // was
    this.typeMapping.put("hv", 1f); // have
    this.typeMapping.put("dti", 1f); // some
    this.typeMapping.put("abn", 1f); // half
    this.typeMapping.put("ql", 1f); // sufficiently, most
    this.typeMapping.put("jjr", 1f); // finer
    this.typeMapping.put("wql", 1f); // However, How
    this.typeMapping.put("pn", 1f); // everything
    this.typeMapping.put("wrb", 1f); // when
    this.typeMapping.put("doz", 1f); // does
    this.typeMapping.put("hvz", 1f); // has
    this.typeMapping.put("jjt", 1f); // best
    this.typeMapping.put("beg", 1f); // being
    this.typeMapping.put("ben", 1f); // been
  }

  public Float getScore(String k) {
    Float v = typeMapping.get(k);
    return v != null ? v : 1f;
  }
}
