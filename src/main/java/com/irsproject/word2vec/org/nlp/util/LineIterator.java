package com.irsproject.word2vec.org.nlp.util;

import java.io.*;
import java.util.Iterator;
import java.util.NoSuchElementException;


public class LineIterator implements Iterator<String> {


    private final BufferedReader bufferedReader;

    private String cachedLine;

    private boolean finished = false;


    public LineIterator(final Reader reader) throws IllegalArgumentException {
        if (reader == null) {
            throw new IllegalArgumentException("输入流不可为null");
        }
        if (reader instanceof BufferedReader) {
            bufferedReader = (BufferedReader) reader;
        } else {
            bufferedReader = new BufferedReader(reader);
        }
    }


    public boolean hasNext() {
        if (cachedLine != null) {
            return true;
        } else if (finished) {
            return false;
        } else {
            try {
                while (true) {
                    String line = bufferedReader.readLine();
                    if (line == null) {
                        finished = true;
                        return false;
                    } else if (isValidLine(line)) {
                        cachedLine = line;
                        return true;
                    }
                }
            } catch(IOException ioe) {
                close();
                throw new IllegalStateException(ioe);
            }
        }
    }


    protected boolean isValidLine(String line) {
        return true;
    }


    public String next() {
        return nextLine();
    }


    public String nextLine() {
        if (!hasNext()) {
            throw new NoSuchElementException("No more lines");
        }
        String currentLine = cachedLine;
        cachedLine = null;
        return currentLine;
    }


    public void close() {
        finished = true;
        try {
            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        cachedLine = null;
    }


    public void remove() {
        throw new UnsupportedOperationException("Remove unsupported on LineIterator");
    }

    public static void closeQuietly(LineIterator iterator) {
        if (iterator != null) {
            iterator.close();
        }
    }

}
