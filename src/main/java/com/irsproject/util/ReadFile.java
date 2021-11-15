package com.irsproject.util;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;
import java.util.Scanner;

public class ReadFile
{
    private Trie trie = new Trie();

    public ReadFile()
    {
        readFile();
    }

    private void readFile()
    {
        String fileName = "dictionary.txt";

        try
        {
            FileReader inputFile = new FileReader(fileName);
            Scanner parser = new Scanner(inputFile);
            String word = "";

            while (parser.hasNext())
            {
                word = parser.nextLine();
                trie.insert(word);
            }
        }
        catch (FileNotFoundException exception)
        {
            System.out.println(fileName + " not found");
        }
        catch (ArrayIndexOutOfBoundsException exception)
        {
            System.out.println("Array Index Out Of Bound");
        }
        catch (Exception exception)
        {
            exception.printStackTrace();
        }
    }

    public List<String> findWord(String word)
    {
        word = word.toLowerCase();
        return trie.findWord(word);
    }
}
