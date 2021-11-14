    @RequestMapping("/spell")
    @ResponseBody
    public String SpellChecker(@RequestBody Map<String, String> p) throws IOException {
        LinkedList<String> spellList = new LinkedList<String>();
        String  spellResults = "";
        Gson gson = new Gson();
        String keywords = p.get("keywords");
        ArrayList<String> list = new ArrayList<String>();
        StringTokenizer st = new StringTokenizer(keywords, " ");
        while (st.hasMoreTokens())
            list.add(st.nextToken());
        String[] keywordsarr = new String[list.size()];
        String[] suggestions = new String[list.size()];
        int count = 0;
        for(int i = 0 ;i < list.size() ; i ++) {
            keywordsarr[i] = list.get(i);
            Properties properties = PropertiesLoaderUtils.loadProperties(new ClassPathResource("/application" +
                    ".properties"));
            String dirPath = properties.getProperty("index.path");
            Directory directory = FSDirectory.open(Paths.get(dirPath));
            SpellChecker checker = new SpellChecker(directory);
            checker.setStringDistance(new JaroWinklerDistance());
            String[] str = checker.suggestSimilar(keywordsarr[i], 5);
            suggestions[i] = str[0];
            if (keywordsarr[i].equals(suggestions[i].substring(0, suggestions[i].length() - 1)))
                count ++;
            else {
                keywordsarr[i] = suggestions[i];
            }
        }
        if (count == list.size()) {
            spellResults = null;
            spellResults = gson.toJson(spellList);
        }
        else {
            for (int i = 0; i < list.size(); i++) {
                spellResults = spellResults + " " + keywordsarr[i];
            }
            spellList.add("Did you mean: " + spellResults);
            spellResults = gson.toJson(spellList);
        }
        return spellResults;
    }