package com.irsproject.controller;

import com.irsproject.util.IndexBot;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.util.Properties;

@Controller
public class Welcome
{
    @RequestMapping("/hello")
    @ResponseBody
    public String welcomePage()
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
        return "index创建成功";
    }
}
