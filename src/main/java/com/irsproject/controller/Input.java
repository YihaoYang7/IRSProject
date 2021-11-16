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
        public String Words(@RequestBody Map<String, String> p) {
        String inputResults = null;
        String keywords = p.get("keywords");
        Gson gson = new Gson();
        ReadFile read = new ReadFile();
        LinkedList inputList = new LinkedList();
        ArrayList<String> arr = new ArrayList<String>();
        StringTokenizer st = new StringTokenizer(keywords, " ");
        while (st.hasMoreTokens())
            arr.add(st.nextToken());
        for(int i = 0 ;i < arr.size() ; i ++) {
            List<String> list = read.findWord(arr.get(i));
            if (list.size() > 10)
                list = list.subList(0, 10);
            inputList.add(list);
        }
        inputResults = gson.toJson(inputList);
        System.out.println(inputResults);
        return inputResults;
    }

}
