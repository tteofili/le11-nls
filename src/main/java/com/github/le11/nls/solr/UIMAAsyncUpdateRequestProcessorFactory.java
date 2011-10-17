package com.github.le11.nls.solr;

import org.apache.solr.common.util.NamedList;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.response.SolrQueryResponse;
import org.apache.solr.uima.processor.SolrUIMAConfigurationReader;
import org.apache.solr.uima.processor.UIMAUpdateRequestProcessorFactory;
import org.apache.solr.update.processor.UpdateRequestProcessor;

/**
 * @author tommaso
 * @version $Id$
 */
public class UIMAAsyncUpdateRequestProcessorFactory extends UIMAUpdateRequestProcessorFactory {
  private NamedList<Object> args;

  @SuppressWarnings("unchecked")
  @Override
  public void init(@SuppressWarnings("rawtypes") NamedList args) {
    this.args = (NamedList<Object>) args.get("uimaConfig");
  }

  @Override
  public UpdateRequestProcessor getInstance(SolrQueryRequest req, SolrQueryResponse rsp,
                                            UpdateRequestProcessor next) {
    return new UIMAAsyncUpdateRequestProcessor(next, req.getCore(),
            new SolrUIMAConfigurationReader(args).readSolrUIMAConfiguration());
  }
}
