package seker.tfidf;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import seker.common.utils.StreamUtils.Word;

public class DocumentLibs extends Document {
    List<Document> docs;
    
    public DocumentLibs(File dir){
        super(dir);
    }
    
    public String getFileList() {
        StringBuilder buf = new StringBuilder(file.getAbsolutePath()).append("\n");
        if (null != docs && !docs.isEmpty()) {
            for (Document doc : docs) {
                buf.append(doc.file.getAbsolutePath()).append("\n");
            }
        }
        return buf.toString();
    }
    
    @Override
    protected void parse(File dir) {
        if (null == dir) {
            System.out.println("null == dir");
        } else {
            if (!dir.exists()) {
                System.out.println(dir.getAbsolutePath() + " is not exists.");
            } else {
                if (dir.isFile()) {
                    Document doc = new Document(dir);
                    mergeDocument(doc);
                    
                    if (null == docs) {
                        docs = new LinkedList<Document>();
                    }
                    docs.add(doc);
                } else if (dir.isDirectory()) {
                    File[] dirs = dir.listFiles();
                    if (null == dirs) {
                        System.out.println("null == dirs");
                    } else if (0 == dirs.length) {
                        System.out.println("0 == dirs.length");
                    } else {
                        for (File d : dirs) {
                            parse(d);
                        }
                    }
                } else {
                    System.out.println(dir.getAbsolutePath() + " is not a file and not a directory.");
                }
            }
        }
    }
    
    private void mergeDocument(Document doc) {
        HashMap<String, Word> wordshp = doc.getWordsHashMap();
        if (null != wordshp && !wordshp.isEmpty()) {
            if (null == wordsHashMap) {
                wordsHashMap = new HashMap<String, Word>();
            }
            
            Collection<Word> collections = wordshp.values();
            Iterator<Word> iterator = collections.iterator();
            while (iterator.hasNext()) {
                Word w = iterator.next();
                String key = w.getWord();
                
                Word word = wordsHashMap.get(key);
                if (null == word) {
                    wordsHashMap.put(key, w);
                } else {
                    word.addCount(w.getCount());
                }
            }
        }
    }
}
