package com.irsproject.controller;

import com.google.gson.Gson;
import com.irsproject.util.ReadFile;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import java.util.*;



@Controller
public class Input {

    @RequestMapping("/input")
    @ResponseBody
    public String SpellChecker(@RequestBody Map<String, String> p) {
        String inputResults = null;
        String keywords = p.get("keywords");
        Gson gson = new Gson();
        ReadFile read = new ReadFile();
        List<String> list = read.findWord(keywords);
        if (list.size() > 10)
            list = list.subList(0, 10);
        LinkedList inputList = new LinkedList(list);
        inputResults = gson.toJson(inputList);
        return inputResults;
    }
}