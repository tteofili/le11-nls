package com.github.sedtum.lucene;

import org.apache.commons.io.IOUtils;
import org.apache.uima.UIMAFramework;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CAS;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceSpecifier;
import org.apache.uima.util.InvalidXMLException;
import org.apache.uima.util.XMLInputSource;

import java.io.IOException;
import java.io.Reader;
import java.net.URL;

/**
 * Utility class for UIMA related common activities
 */
public class UIMAAnalyzersUtils {

  public static CAS analyzeInput(Reader input, String descriptorPath) throws InvalidXMLException,
          IOException, ResourceInitializationException, AnalysisEngineProcessException {
    URL url = UIMAAnalyzersUtils.class.getResource(descriptorPath);
    XMLInputSource in = new XMLInputSource(url);
    ResourceSpecifier specifier = UIMAFramework.getXMLParser().parseResourceSpecifier(
            in);
    AnalysisEngine ae = UIMAFramework.produceAnalysisEngine(specifier);
    CAS cas = ae.newCAS();
    cas.setDocumentText(IOUtils.toString(input));
    ae.process(cas);
    return cas;
  }
}
