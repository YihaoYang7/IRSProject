import java.io.IOException;
import java.nio.file.Paths;
import java.util.Scanner;

import org.apache.lucene.analysis.core.KeywordAnalyzer;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.spell.JaroWinklerDistance;
import org.apache.lucene.search.spell.PlainTextDictionary;
import org.apache.lucene.search.spell.SpellChecker;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class Spell_Checker
{
    public void check_txt_dictionary(String input_word) throws IOException
    {
        // Creating the index
        Directory directory = FSDirectory.open(Paths.get("Index"));
        //PlainTextDictionary txt_dict = new PlainTextDictionary(Paths.get("dblp_title.txt"));
        SpellChecker checker = new SpellChecker(directory);       
        
        // Searching and presenting the suggested words by selecting a string distance
        checker.setStringDistance(new JaroWinklerDistance());
        
        String[] suggestions = checker.suggestSimilar(input_word, 5);
        
        System.out.println("By '" + input_word + "' did you mean:");
        for(String suggestion : suggestions)
            System.out.println("\t" + suggestion);
    }
    
    
    public static void main(String[] args) throws IOException, Throwable
    {
        Scanner scan = new Scanner(System.in);
        Spell_Checker spell_checker = new Spell_Checker();
  
        System.out.print("\nType a word to spell check: ");
        String input_word = scan.next();

        spell_checker.check_txt_dictionary(input_word);
    }
}