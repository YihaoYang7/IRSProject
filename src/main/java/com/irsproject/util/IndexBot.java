package com.irsproject.util;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.springframework.beans.factory.annotation.Value;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.Locale;

public class IndexBot
{
    @Value("${project.metadata.path}")
    private String metadataPath;

    private Directory directory;

    public IndexBot(String indexDir) throws IOException
    {
        this.directory = FSDirectory.open(Paths.get(indexDir));
    }

    private IndexWriter getIndexWriter() throws IOException
    {
        IndexWriterConfig config = new IndexWriterConfig(new StandardAnalyzer());
        config.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
        return new IndexWriter(directory,config);
    }

    public void createIndex()
    {
        File[] files = new File(metadataPath).listFiles();
        IndexWriter writer = null;
        try
        {
            writer = getIndexWriter();
            for (File f : files)
            {
                File[] texts = f.listFiles();


                for (File text : texts)
                {
                    BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(text),
                            StandardCharsets.UTF_8));
                    char[] bufferZone = new char[10240];
                    br.read(bufferZone, 0, bufferZone.length);
                    String t = new String(bufferZone);
                    int abstractPosition = t.toLowerCase(Locale.ROOT).indexOf("abstract");
                    int introductionPosition = t.toLowerCase(Locale.ROOT).indexOf("introduction");
                    String titleText = t.substring(0, abstractPosition);
                    String abstractText = t.substring(abstractPosition, introductionPosition);
                    StringBuilder mainBodyText =
                            new StringBuilder(t.toLowerCase(Locale.ROOT).substring(introductionPosition));
                    while (true)
                    {
                        bufferZone = new char[1024];
                        int temp = br.read(bufferZone, 0, bufferZone.length);
                        if (temp == -1) break;
                        mainBodyText.append(bufferZone);
                    }
                    br.close();

                    Document doc = new Document();
                    doc.add(new TextField("title",titleText,Field.Store.YES));
                    doc.add(new TextField("abstract", abstractText,Field.Store.YES));
                    doc.add(new TextField("mainbody", mainBodyText.toString(),Field.Store.NO));
                    writer.addDocument(doc);
                }
            }
            writer.close();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
