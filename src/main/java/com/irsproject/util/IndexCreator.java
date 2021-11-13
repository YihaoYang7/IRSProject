package com.irsproject.util;

import com.google.gson.Gson;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Properties;

public class IndexCreator
{
    private String metadataPath;

    private static Directory directory;

    public IndexCreator(String indexDir) throws IOException
    {
        this.directory = FSDirectory.open(Paths.get(indexDir));
        Resource resource = new ClassPathResource("/application.properties");
        Properties properties = PropertiesLoaderUtils.loadProperties(resource);
        this.metadataPath = properties.getProperty("project.new.metadata.path");
    }

    private IndexWriter getIndexWriter() throws IOException
    {
        IndexWriterConfig config = new IndexWriterConfig(new StandardAnalyzer());
        config.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
        return new IndexWriter(directory, config);
    }

    public void createIndex()
    {
        XMLInputFactory factory = XMLInputFactory.newInstance();
        try
        {
            XMLStreamReader xmlReader = factory.createXMLStreamReader(new FileReader(this.metadataPath));
            IndexWriter iWriter = getIndexWriter();
            int count = 0;
            while (xmlReader.hasNext())
            {
                xmlReader.next();
                if (xmlReader.getEventType() == XMLStreamReader.START_ELEMENT)
                {
                    String elementName = xmlReader.getLocalName();

                    if (labelClass(elementName) == 0)
                    {
                        continue;
                    }
                    String title = null;
                    StringBuilder authors = new StringBuilder();
                    StringBuilder ee = new StringBuilder();
                    StringBuilder releaseDate = new StringBuilder();
                    /* Label includes <inproceedings>, <phdthesis>, <incollection>, <proceedings>, <article>,
                    <mastersthesis>, <book>
                     */
                    while (labelClass(elementName) == 1)
                    {
                        xmlReader.next();
                        if (xmlReader.getEventType() == XMLStreamConstants.END_ELEMENT && xmlReader.getLocalName().equals(elementName))
                        {
                            break;
                        } else if (xmlReader.getEventType() == XMLStreamConstants.START_ELEMENT && labelClass(xmlReader.getLocalName()) == 2)
                        {
                            /* Entering 2 level label.
                            Label includes <editor>, <author>, <ee>, <title>, <ee>
                             */
                            String temp = xmlReader.getLocalName();
                            while (true)
                            {
                                xmlReader.next();
                                if (xmlReader.getEventType() == XMLStreamReader.END_ELEMENT && xmlReader.getLocalName().equals(temp))
                                {
                                    break;
                                } else if (xmlReader.getEventType() == XMLStreamConstants.CHARACTERS)
                                {
                                    switch (temp)
                                    {
                                        case "editor":
                                        case "author":
                                            if (authors.length()==0)
                                            {
                                                authors.append(xmlReader.getText());
                                            }
                                            else
                                            {
                                                authors.append(", ").append(xmlReader.getText());
                                            }
                                            break;
                                        case "ee":
                                            if (ee.length() == 0)
                                            {
                                                ee.append(xmlReader.getText());
                                            }else
                                            {
                                                ee.append(";").append(xmlReader.getText());
                                            }
                                            break;
                                        case "title":
                                            title = xmlReader.getText();
                                            break;
                                        case "year":
                                            releaseDate.append(xmlReader.getText());
                                            break;
                                        case "month":
                                            releaseDate.append(" ").append(xmlReader.getText());
                                            break;
                                        default:
                                            break;
                                    }
                                }
                            }

                        }
                    }
                    Document doc = new Document();
                    if (title != null)
                    {
                        doc.add(new TextField("title", title, Field.Store.YES));
                        doc.add(new TextField("author",authors.toString(),Field.Store.YES));
                        doc.add(new TextField("sources",ee.toString(),Field.Store.YES));
                        doc.add(new TextField("date",releaseDate.toString(),Field.Store.YES));
                        iWriter.addDocument(doc);
                        count++;
                        System.out.println(count);
                    }
                }
            }
            iWriter.close();
        } catch (XMLStreamException e)
        {
            e.printStackTrace();
        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private int labelClass(String labelName)
    {
        int labelClass = 0;
        switch (labelName)
        {
            case "inproceedings":
            case "phdthesis":
            case "mastersthesis":
            case "incollection":
            case "proceedings":
            case "article":
            case "book":
                labelClass = 1;
                break;
            case "editor":
            case "author":
            case "ee":
            case "title":
            case "year":
            case "month":
                labelClass = 2;
                break;
            default:
                labelClass = 0;
                break;
        }
        return labelClass;
    }

    public static void main(String[] args)
    {
        String test = "hello;Yihao Yang;ppe";
        String[] s = test.split(";");
        HashMap<String,Object> map = new HashMap<>();
        map.put("source",s);
        Gson gson = new Gson();
        System.out.println(gson.toJson(map));

    }


}
