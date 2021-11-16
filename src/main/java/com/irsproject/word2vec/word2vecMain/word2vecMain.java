package com.irsproject.word2vec.word2vecMain;


import com.irsproject.word2vec.org.nlp.vec.VectorModel;

import java.util.Collections;
import java.util.Set;


import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import java.util.*;


@Controller
public class word2vecMain {

    @Value("${topic.index}")
    private String topicIndex;

    @RequestMapping("/word2vec")
    @ResponseBody
    public String Word2Vec(@RequestBody Map<String, String> p) {
        String wordResults = "";
        String keywords = p.get("keywords");
        Gson gson = new Gson();
        String modelFilePath = topicIndex;
        LinkedList wordList = new LinkedList();
        ArrayList<String> arr = new ArrayList<String>();
        StringTokenizer st = new StringTokenizer(keywords, " ");
        while (st.hasMoreTokens())
            arr.add(st.nextToken());
        for (int i = 0; i < arr.size(); i++) {
            VectorModel vm = VectorModel.loadFromFile(modelFilePath);
            Set<VectorModel.WordScore> result1 = Collections.emptySet();
            result1 = vm.similar(arr.get(i));
            int count = 0;
            for (VectorModel.WordScore we : result1) {
                wordList.add(we.name);
                count++;
                if (count == 2)
                    break;
            }
        }
        wordResults = gson.toJson(wordList);
        return wordResults;
    }
}
