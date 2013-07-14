package seker.tfidf;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;

import seker.common.utils.StreamUtils;
import seker.common.utils.StreamUtils.Word;

public class Document implements Comparator<Word>{
    
    Word[] words;
    HashMap<String, Word> wordsHashMap;
    File file;
    
    public Document(File f){
        file = f;
        parse(file);
        
        if (null == wordsHashMap || wordsHashMap.isEmpty()) {
            System.out.println("null == wordsHashMap || wordsHashMap.isEmpty() \n" + file.getAbsolutePath());
        } else {
            Collection<Word> collections = wordsHashMap.values();
            words = new Word[wordsHashMap.size()];
            collections.toArray(words);
            Arrays.sort(words, this);
        }
    }
    
    protected void parse(File file) {
        if (null == file) {
            System.out.println("null == file");
        } else if (!file.exists()) {
            System.out.println(file.getAbsolutePath() + " is not exists.");
        } else if (!file.isFile()) {
            System.out.println(file.getAbsolutePath() + " is not a file.");
        } else {
            // System.out.println(file.getPath());
            try {
                wordsHashMap = StreamUtils.streamToWords(new FileInputStream(file), "UTF-8");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
    
    public Word[] getWords() {
        return words;
    }
    
    public HashMap<String, Word> getWordsHashMap() {
        return wordsHashMap;
    }
    
    @Override
    public String toString() {
        if (null != words && 0 != words.length) {
            StringBuilder buf = new StringBuilder();
            for (Word word : words) {
                buf.append(word.toString()).append("\n");
            }
            return buf.deleteCharAt(buf.length() - 1).toString();
        } else {
            return "The document is empty.";
        }
    }

    @Override
    public int compare(Word w1, Word w2) {
        // return w1.getWord().compareTo(w2.getWord());
        return w1.getCount() - w2.getCount();
    }
}