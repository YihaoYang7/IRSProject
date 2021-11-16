package com.irsproject.controller;

import com.google.gson.Gson;
import com.irsproject.util.IndexBot;
import com.irsproject.util.IndexCreator;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
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
public class Welcome
{
    @RequestMapping("/hello")
    public String welcome()
    {
        return "/home";
    }

    @RequestMapping("/search")
    public String searchInit()
    {
        Resource resource = new ClassPathResource("/application.properties");
        try
        {
            Properties properties = PropertiesLoaderUtils.loadProperties(resource);
            String dirPath = properties.getProperty("index.path");
            IndexBot indexBot = new IndexBot(dirPath);
            System.out.println("Creating Index");
            indexBot.createIndex();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        return "/home";
    }

    @RequestMapping("/query")
    @ResponseBody
    public String queryByKeywords(@RequestBody Map<String, String> p)
    {
        String keywords = p.get("keywords");
        Pattern queryField = Pattern.compile("^(title|abstract|content):\".+\"");
        LinkedList<Map<String, String>> results = new LinkedList<>();
        String queryResults = null;
        try
        {
            Properties properties = PropertiesLoaderUtils.loadProperties(new ClassPathResource("/application" +
                    ".properties"));
            String dirPath = properties.getProperty("index.path");
            Directory dir = FSDirectory.open(Paths.get(dirPath));
            DirectoryReader dr = DirectoryReader.open(dir);
            IndexSearcher searcher = new IndexSearcher(dr);
            Analyzer analyzer = new StandardAnalyzer();
            if (queryField.matcher(keywords).matches())
            {
                String[] k = keywords.split(":\"");
                k[1] = k[1].substring(0, k[1].length() - 1);
                QueryParser parser = new QueryParser(k[0], analyzer);
                org.apache.lucene.search.Query query = parser.parse(k[1]);
                TopDocs docs = searcher.search(query, 50);
                for (ScoreDoc scoreDoc : docs.scoreDocs)
                {
                    Document doc = searcher.doc(scoreDoc.doc);
                    HashMap<String, String> result = new HashMap<>();
                    switch (k[0])
                    {
                        case "title":
                        case "abstract":
                        {
                            String title = doc.get("title");
                            int position = title.indexOf(k[1]);
                            title = cut(title, position, "title");
                            result.put("title", title);
                            String content = doc.get("abstract");
                            position = content.indexOf(k[1]);
                            content = cut(content, position, "content");
                            result.put("content", content);
                            result.put("path", doc.get("path"));
                            results.add(result);
                            break;
                        }
                        case "content":
                        {
                            String title = doc.get("title");
                            int position = title.indexOf(k[1]);
                            title = cut(title, position, "title");
                            result.put("title", title);
                            String content = doc.get("content");
                            position = content.indexOf(k[1]);
                            content = cut(content, position, "content");
                            result.put("content", content);
                            result.put("path", doc.get("path"));
                            results.add(result);
                            break;
                        }
                    }
                }
            } else
            {
                String[] fields = {"title", "abstract", "content"};
                QueryParser parser = new MultiFieldQueryParser(fields, analyzer);
                org.apache.lucene.search.Query query = parser.parse(keywords);
                TopDocs docs = searcher.search(query, 50);
                for (ScoreDoc scoreDoc : docs.scoreDocs)
                {
                    Document doc = searcher.doc(scoreDoc.doc);
                    HashMap<String, String> result = new HashMap<>();

                    String title = doc.get("title");
                    int position = title.indexOf(keywords);
                    title = cut(title, position, "title");
                    result.put("title", title);
                    String content = doc.get("content");
                    position = content.indexOf(keywords);
                    content = cut(content, position, "content");
                    result.put("content", content);

                    result.put("path", doc.get("path"));
                    results.add(result);
                }
            }
            Gson gson = new Gson();
            queryResults = gson.toJson(results);
        } catch (IOException e)
        {
            e.printStackTrace();
        } catch (ParseException e)
        {
            e.printStackTrace();
        }
        return queryResults;
    }

    public String cut(String originalText, int position, String type)
    {
        int idealLength = 0;
        int begin = 0;
        int end = originalText.length();
        switch (type)
        {
            case "title":
                idealLength = 100;
                break;
            case "content":
                idealLength = 600;
                break;
        }
        String cutString;

        if (position == -1)
        {
            if (originalText.length() > idealLength) cutString = originalText.substring(0, idealLength - 10);
            else cutString = originalText;
        } else
        {
            if (position - 300 < 0) begin = 0;
            else begin = position - 300;
            if (position + 300 > originalText.length()) end = originalText.length();
            else end = position + 300;
            cutString = originalText.substring(begin, end);
        }
        return cutString;
    }
}