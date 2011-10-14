package com.github.sedtum.lucene;

import com.github.sedtum.lucene.payloads.UIMAPayloadsAnalyzer;
import com.github.sedtum.lucene.payloads.UIMATypeBasedSimilarity;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.*;
import org.apache.lucene.search.payloads.MaxPayloadFunction;
import org.apache.lucene.search.payloads.PayloadTermQuery;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 */
public class UIMATypeBasedSimilarityTest {
  private Analyzer analyzer;
  private IndexWriter writer;
  private RAMDirectory dir;


  @Before
  public void setUp() throws Exception {
    dir = new RAMDirectory();
    analyzer = new UIMAPayloadsAnalyzer("/HmmTaggerAggregate.xml");
    writer = new IndexWriter(dir, new IndexWriterConfig(Version.LUCENE_33, analyzer));

    Document doc = new Document();
    doc.add(new Field("title", "this is a dummy title containing an entity for London", Field.Store.YES,
            Field.Index.ANALYZED));
    doc.add(new Field("contents", "there is some content written here about the british city",
            Field.Store.YES, Field.Index.ANALYZED));
    writer.addDocument(doc, analyzer);
    writer.commit();

    // try the search over the first doc
    IndexSearcher indexSearcher = new IndexSearcher(writer.getReader());
    TopDocs result = indexSearcher.search(
            new MatchAllDocsQuery("contents"), 10);
    assertTrue(result.totalHits > 0);
    Document d = indexSearcher.doc(result.scoreDocs[0].doc);
    assertNotNull(d);
    assertNotNull(d.getFieldable("title"));
    assertNotNull(d.getFieldable("contents"));

    // add a second doc
    doc = new Document();
    doc.add(new Field("title", "some title regarding some article written in English", Field.Store.YES,
            Field.Index.ANALYZED));
    doc.add(new Field("contents", "this is the content of the article about",
            Field.Store.YES, Field.Index.ANALYZED));
    writer.addDocument(doc, analyzer);
    writer.commit();
  }

  @After
  public void tearDown() throws Exception {
    writer.close();
  }

  @Test
  public void baseSimilarityTest() {
    try {
      IndexSearcher searcher = new IndexSearcher(dir, true);
      Similarity payloadSimilarity = new UIMATypeBasedSimilarity();
      searcher.setSimilarity(payloadSimilarity);
//      BooleanQuery booleanQuery = new BooleanQuery();
//      booleanQuery.add(new PayloadTermQuery(new Term("title", "London"), new MaxPayloadFunction()), BooleanClause.Occur.SHOULD);
//      booleanQuery.add(new PayloadTermQuery(new Term("title", "English"), new MaxPayloadFunction()), BooleanClause.Occur.SHOULD);
//      SpanQuery[] clauses = new SpanQuery[]{new PayloadTermQuery(new Term("title","London"),new MaxPayloadFunction()),
//              new PayloadTermQuery(new Term("title","English"),new MaxPayloadFunction())};
//      int slop = 3;
//      boolean inOrder = true;
//      Query query = new PayloadNearQuery(clauses, slop, inOrder);
      Query directQuery = new TermQuery(new Term("title", "London"));
      TopDocs topDocs = searcher.search(directQuery, 10);
      System.out.println("Number of matching docs: " + topDocs.totalHits);
      ScoreDoc doc1 = topDocs.scoreDocs[0];
      System.out.println("Doc: " + doc1.toString());
      System.out.println("Explain: " + searcher.explain(directQuery, doc1.doc));
      Query payloadQuery = new PayloadTermQuery(new Term("title", "London"), new MaxPayloadFunction());
      topDocs = searcher.search(payloadQuery, 10);
      System.out.println("Number of matching docs: " + topDocs.totalHits);
      ScoreDoc doc2 = topDocs.scoreDocs[0];
      System.out.println("Doc: " + doc2.toString());
      System.out.println("Explain: " + searcher.explain(payloadQuery, doc2.doc));
      assertTrue(doc1.score < doc2.score);
    } catch (Exception e) {
      e.printStackTrace();
      fail(e.getLocalizedMessage());
    }
  }


}
