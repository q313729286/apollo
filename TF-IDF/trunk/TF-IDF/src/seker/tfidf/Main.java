package seker.tfidf;

import java.io.File;

public class Main {
    public static void main(String[] args) {
        if (null == args || 0 == args.length) {
            System.out.println("null == args || 0 == args.length");
        } else {
            if (null == args[0]) {
                System.out.println("null == args[0]");
            } else {
                DocumentLibs documentlibs = new DocumentLibs(new File(args[0]));
                System.out.println(documentlibs.getFileList());
                // System.out.println(documentlibs.toString());
            }
        }
    }
}
