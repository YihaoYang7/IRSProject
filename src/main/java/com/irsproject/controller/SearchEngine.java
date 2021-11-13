package com.irsproject.controller;

import com.google.gson.Gson;
import com.irsproject.util.IndexCreator;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.search.highlight.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Pattern;

@Controller
public class SearchEngine
{
    @RequestMapping("/init")
    public String test()
    {
        Resource resource = new ClassPathResource("/application.properties");
        try
        {
            Properties properties = PropertiesLoaderUtils.loadProperties(resource);
            String dirPath = properties.getProperty("project.new.index.path");
            IndexCreator indexCreator = new IndexCreator(dirPath);
            System.out.println("Creating Index");
            indexCreator.createIndex();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        return "/home2";
    }

    @RequestMapping("/home")
    public String home()
    {
        return "/home2";
    }

    @RequestMapping("/query2")
    @ResponseBody
    public String queryByKeyWords(@RequestBody Map<String, String> p)
    {
        String keywords = p.get("keywords");
        Pattern queryField = Pattern.compile("^(title|author):\".+\"");
        LinkedList<Map<String, Object>> results = new LinkedList<>();
        String queryResults = null;
        try
        {
            Properties properties = PropertiesLoaderUtils.loadProperties(new ClassPathResource("/application" +
                    ".properties"));
            String dirPath = properties.getProperty("project.new.index.path");
            System.out.println(dirPath);
            Directory dir = FSDirectory.open(Paths.get(dirPath));
            DirectoryReader dr = DirectoryReader.open(dir);
            IndexSearcher searcher = new IndexSearcher(dr);
            Analyzer analyzer = new StandardAnalyzer();

            if (queryField.matcher(keywords).matches())
            {
                String[] k = keywords.split(":\"");
                k[1] = k[1].substring(0, k[1].length() - 1);
                QueryParser parser = new QueryParser(k[0], analyzer);
                Query query = parser.parse(k[1]);
                Formatter formatter = new SimpleHTMLFormatter("<em>", "</em>");
                QueryScorer scorer = new QueryScorer(query);
                Highlighter highlighter = new Highlighter(formatter, scorer);
                TopDocs docs = searcher.search(query, 50);
                for (ScoreDoc scoreDoc : docs.scoreDocs)
                {
                    Document doc = searcher.doc(scoreDoc.doc);
                    HashMap<String, Object> result = new HashMap<>();
                    String hTitle = highlighter.getBestFragment(new StandardAnalyzer(), "title", doc.get("title"));
                    result.put("title", hTitle == null ? doc.get("title") : hTitle );
                    String hAuthors = highlighter.getBestFragment(new StandardAnalyzer(), "author", doc.get("author"));
                    result.put("authors", hAuthors == null ? doc.get("author") : hAuthors);
                    String source = doc.get("sources");
                    String[] sources = null;
                    if (source.contains(";"))
                    {
                        sources = source.split(";");
                    }
                    result.put("sources", sources);

                    result.put("date", doc.get("date"));
                    results.add(result);
                }
                Gson gson = new Gson();
                System.out.println(gson.toJson(results));
                queryResults = gson.toJson(results);
            } else
            {
                String[] fields = {"title", "author"};
                QueryParser parser = new MultiFieldQueryParser(fields, analyzer);
                Query query = parser.parse(keywords);
                Formatter formatter = new SimpleHTMLFormatter("<em>", "</em>");
                QueryScorer scorer = new QueryScorer(query);
                Highlighter highlighter = new Highlighter(formatter, scorer);
                TopDocs docs = searcher.search(query, 50);
                for (ScoreDoc scoreDoc : docs.scoreDocs)
                {
                    Document doc = searcher.doc(scoreDoc.doc);
                    HashMap<String, Object> result = new HashMap<>();
//                    result.put("title", doc.get("title"));
                    String hTitle = highlighter.getBestFragment(new StandardAnalyzer(), "title", doc.get("title"));
                    result.put("title", hTitle == null ? doc.get("title") : hTitle );
                    String hAuthors = highlighter.getBestFragment(new StandardAnalyzer(), "author", doc.get("author"));
                    result.put("authors", hAuthors == null ? doc.get("author") : hAuthors);

                    String source = doc.get("sources");
                    String[] sources = null;
                    if (source.contains(";"))
                    {
                        sources = source.split(";");
                    }
                    result.put("sources", sources);

                    result.put("date", doc.get("date"));
                    results.add(result);
                }
                Gson gson = new Gson();
                System.out.println(gson.toJson(results));
                queryResults = gson.toJson(results);
            }
            dir.close();
            dr.close();
        } catch (IOException e)
        {
            e.printStackTrace();
        } catch (ParseException e)
        {
            e.printStackTrace();
        } catch (InvalidTokenOffsetsException e)
        {
            e.printStackTrace();
        }
        return queryResults;
    }
}
