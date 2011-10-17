package com.github.le11.nls.lucene;

import org.apache.commons.io.IOUtils;
import org.apache.uima.UIMAFramework;
import org.apache.uima.aae.client.UimaAsBaseCallbackListener;
import org.apache.uima.aae.client.UimaAsynchronousEngine;
import org.apache.uima.adapter.jms.client.BaseUIMAAsynchronousEngine_impl;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CAS;
import org.apache.uima.collection.EntityProcessStatus;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceSpecifier;
import org.apache.uima.util.InvalidXMLException;
import org.apache.uima.util.XMLInputSource;

import java.io.IOException;
import java.io.Reader;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Utility class for UIMA related common activities
 */
public class UIMAAnalyzersUtils {

  private static UIMAAnalyzersUtils instance;

  private UIMAAnalyzersUtils() {
  }

  public static UIMAAnalyzersUtils getInstance() {
    if (instance == null) {
      instance = new UIMAAnalyzersUtils();
    }
    return instance;
  }

  public CAS analyzeInput(Reader input, String descriptorPath) throws InvalidXMLException,
          IOException, ResourceInitializationException, AnalysisEngineProcessException {
    URL url = UIMAAnalyzersUtils.class.getResource(descriptorPath);
    XMLInputSource in = new XMLInputSource(url);
    ResourceSpecifier specifier = UIMAFramework.getXMLParser().parseResourceSpecifier(in);
    AnalysisEngine ae = UIMAFramework.produceAnalysisEngine(specifier);
    // AEProvider aeProvider = AEProviderFactory.getInstance().getAEProvider("", descriptorPath, new
    // HashMap<String, Object>());
    // AnalysisEngine ae = aeProvider.getAE();
    CAS cas = ae.newCAS();
    cas.setDocumentText(IOUtils.toString(input));
    ae.process(cas);
    ae.destroy();
    return cas;
  }

  public CAS analyzeAsynchronously(Reader input, String queueName) throws Exception {
    final UimaAsynchronousEngine uimaAsEngine = new BaseUIMAAsynchronousEngine_impl();
    uimaAsEngine.addStatusCallbackListener(new SimpleUimaAsBaseCallbackListener());
    Map<String, Object> appCtx = new HashMap<String, Object>();
    appCtx.put(UimaAsynchronousEngine.ServerUri, "tcp://localhost:61616");
    appCtx.put(UimaAsynchronousEngine.Endpoint, queueName);
    appCtx.put(UimaAsynchronousEngine.CasPoolSize, 2);
    uimaAsEngine.initialize(appCtx);
    CAS cas = uimaAsEngine.getCAS();
    String text = IOUtils.toString(input);
    cas.setDocumentText(text);
    uimaAsEngine.sendAndReceiveCAS(cas);
    return cas;
  }

  class SimpleUimaAsBaseCallbackListener extends UimaAsBaseCallbackListener {
    @Override
    public void entityProcessComplete(CAS aCas, EntityProcessStatus aStatus) {
      if (aStatus != null && aStatus.isException()) {
        List exceptions = aStatus.getExceptions();
        for (int i = 0; i < exceptions.size(); i++) {
          ((Throwable) exceptions.get(i)).printStackTrace();
        }
        try {
        } catch (Exception e) {
          e.printStackTrace();
        }
        return;
      }
    }
  }
}