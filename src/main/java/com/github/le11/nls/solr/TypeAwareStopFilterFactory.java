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

import com.github.le11.nls.lucene.TypeAwareStopFilter;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.StopAnalyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.solr.analysis.BaseTokenFilterFactory;
import org.apache.solr.common.ResourceLoader;
import org.apache.solr.util.plugin.ResourceLoaderAware;

import java.io.IOException;
import java.util.Set;

/**
 * @author tommaso
 */
public class TypeAwareStopFilterFactory extends BaseTokenFilterFactory implements ResourceLoaderAware {
  public void inform(ResourceLoader loader) {
    String stopWordFiles = args.get("words");
    ignoreCase = getBoolean("ignoreCase", false);
    enablePositionIncrements = getBoolean("enablePositionIncrements", false);

    if (stopWordFiles != null) {
      try {
        stopWords = getWordSet(loader, stopWordFiles, ignoreCase);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    } else {
      stopWords = new CharArraySet(luceneMatchVersion, StopAnalyzer.ENGLISH_STOP_WORDS_SET, ignoreCase);
    }
  }

  private CharArraySet stopWords;
  private boolean ignoreCase;
  private boolean enablePositionIncrements;

  public boolean isEnablePositionIncrements() {
    return enablePositionIncrements;
  }

  public boolean isIgnoreCase() {
    return ignoreCase;
  }

  public Set<?> getStopWords() {
    return stopWords;
  }

  @Override
  public TokenStream create(TokenStream input) {
    TypeAwareStopFilter stopFilter = new TypeAwareStopFilter(luceneMatchVersion, input, stopWords, ignoreCase);
    stopFilter.setEnablePositionIncrements(enablePositionIncrements);
    return stopFilter;
  }
}
