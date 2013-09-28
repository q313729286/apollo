/*
 * Copyright (C) 2013 Seker. All rights reserved.
 */
package seker.tfidf;

import java.io.InputStream;
import java.util.HashMap;

import seker.common.utils.StreamUtils;

/**
 * 
 * @author seker
 * @since 2013年11月9日
 */
public class Word {
    final String word;
    int count;

    public Word(String w) {
        word = w;
        count = 1;
    }
    
    public String getWord() {
        return word;
    }

    public int getCount() {
        return count;
    }
    
    public void addCount(int c) {
        count += c;
    }
    
    @Override
    public String toString() {
        return "[word=" + word + ", count=" + count + "]";
    }
    
    /**
     * 按照特定的编码格式英文分词
     * 
     * @param is
     *            Stream源
     * @param enc
     *            编码格式
     * @return 英文分词
     */
    public static HashMap<String, Word> streamToWords(InputStream is, String enc) {
        HashMap<String, Word> words = null;
        if (null == is) {
            System.out.println("null == is");
        } else {
            String str = StreamUtils.streamToString(is, enc);
            if (null == str || 0 == str.length()) {
                System.out.println("null == str || 0 == str.length()");
            } else {
                words = new HashMap<String, Word>();
//                String regex = "[^a-zA-Z]+";
//                Pattern pattern = Pattern.compile(regex);  
//                Matcher  ma = pattern.matcher(str);  
//                while(ma.find()){  
//                    String s = ma.group();
//                    Word word = words.get(s);
//                    if (null == word) {
//                        word = new Word(s);
//                        words.put(s, word);
//                    } else {
//                        word.count++;
//                    }
//                }
                
                String[] strs = str.split("[^a-zA-Z]+");
                if (null == strs || 0 == strs.length) {
                    System.out.println("null == strs || 0 == strs.length");
                } else {
                    for (String s : strs) {
                        Word word = words.get(s);
                        if (null == word) {
                            word = new Word(s);
                            words.put(s, word);
                        } else {
                            word.count++;
                        }
                    }
                }
            }
        }
        return words;
    }
}
