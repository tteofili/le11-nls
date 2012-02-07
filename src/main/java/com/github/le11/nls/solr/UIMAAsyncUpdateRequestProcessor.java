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

import com.github.le11.nls.lucene.UIMAAnalyzersUtils;
import org.apache.solr.common.SolrException;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.core.SolrCore;
import org.apache.solr.schema.SchemaField;
import org.apache.solr.uima.processor.SolrUIMAConfiguration;
import org.apache.solr.uima.processor.UIMAToSolrMapper;
import org.apache.solr.update.AddUpdateCommand;
import org.apache.solr.update.processor.UpdateRequestProcessor;
import org.apache.uima.jcas.JCas;

import java.io.IOException;
import java.io.StringReader;
import java.util.Map;

/**
 * @author tommaso
 */
public class UIMAAsyncUpdateRequestProcessor extends UpdateRequestProcessor {

  public UIMAAsyncUpdateRequestProcessor(UpdateRequestProcessor next, SolrCore solrCore, SolrUIMAConfiguration config) {
    super(next);
    initialize(solrCore, config);
  }

  SolrUIMAConfiguration solrUIMAConfiguration;

  private SolrCore solrCore;


  private void initialize(SolrCore solrCore, SolrUIMAConfiguration config) {
    this.solrCore = solrCore;
    solrUIMAConfiguration = config;
  }

  @Override
  public void processAdd(AddUpdateCommand cmd) throws IOException {
    String text = null;
    try {
      /* get Solr document */
      SolrInputDocument solrInputDocument = cmd.getSolrInputDocument();

      /* get the fields to analyze */
      String[] texts = getTextsToAnalyze(solrInputDocument);
      for (int i = 0; i < texts.length; i++) {
        text = texts[i];
        if (text != null && text.length() > 0) {
          /* process the text value */
          JCas jcas = UIMAAnalyzersUtils.getInstance().analyzeAsynchronously(new StringReader(text),
                  solrUIMAConfiguration.getAePath()).getJCas();

          UIMAToSolrMapper uimaToSolrMapper = new UIMAToSolrMapper(solrInputDocument, jcas);
          /* get field mapping from config */
          /* map type features on fields */
          for (String typeFQN : solrUIMAConfiguration.getTypesFeaturesFieldsMapping().keySet()) {
            uimaToSolrMapper.map(typeFQN, solrUIMAConfiguration.getTypesFeaturesFieldsMapping().get(typeFQN));
          }
        }
      }
    } catch (Exception e) {
      String logField = solrUIMAConfiguration.getLogField();
      if (logField == null) {
        SchemaField uniqueKeyField = solrCore.getSchema().getUniqueKeyField();
        if (uniqueKeyField != null) {
          logField = uniqueKeyField.getName();
        }
      }
      String optionalFieldInfo = logField == null ? "." :
              new StringBuilder(". ").append(logField).append("=")
                      .append((String) cmd.getSolrInputDocument().getField(logField).getValue())
                      .append(", ").toString();
      int len = Math.min(text.length(), 100);
      if (solrUIMAConfiguration.isIgnoreErrors()) {
        log.warn(new StringBuilder("skip the text processing due to ")
                .append(e.getLocalizedMessage()).append(optionalFieldInfo)
                .append(" text=\"").append(text.substring(0, len)).append("...\"").toString());
      } else {
        throw new SolrException(SolrException.ErrorCode.SERVER_ERROR,
                new StringBuilder("processing error: ")
                        .append(e.getLocalizedMessage()).append(optionalFieldInfo)
                        .append(" text=\"").append(text.substring(0, len)).append("...\"").toString(), e);
      }
    }
    super.processAdd(cmd);
  }

  /*
   * get the texts to analyze from the corresponding fields
   */
  private String[] getTextsToAnalyze(SolrInputDocument solrInputDocument) {
    String[] fieldsToAnalyze = solrUIMAConfiguration.getFieldsToAnalyze();
    boolean merge = solrUIMAConfiguration.isFieldsMerging();
    String[] textVals;
    if (merge) {
      StringBuilder unifiedText = new StringBuilder("");
      for (int i = 0; i < fieldsToAnalyze.length; i++) {
        unifiedText.append(String.valueOf(solrInputDocument.getFieldValue(fieldsToAnalyze[i])));
      }
      textVals = new String[1];
      textVals[0] = unifiedText.toString();
    } else {
      textVals = new String[fieldsToAnalyze.length];
      for (int i = 0; i < fieldsToAnalyze.length; i++) {
        textVals[i] = String.valueOf(solrInputDocument.getFieldValue(fieldsToAnalyze[i]));
      }
    }
    return textVals;
  }

}
