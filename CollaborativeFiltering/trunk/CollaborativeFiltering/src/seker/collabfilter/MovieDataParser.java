/*
 * Copyright (C) 2013 Baidu Inc. All rights reserved.
 */
package seker.collabfilter;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import seker.common.utils.OutputUtils;
import seker.common.utils.StreamUtils;
import seker.common.utils.TextUtils;

/**
 * 
 * @author seker
 * @since 2013-9-28
 */
public class MovieDataParser {

    public List<MovieInfo> parse(InputStream is) {
        List<MovieInfo> infos = null;
        if (null != is) {
            infos = new ArrayList<MovieInfo>();
            String line = null;
            MovieInfo info = null;
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(is, "utf-8"), 
                        StreamUtils.STREAM_BUFFER_SIZE);
                while (null != (line = reader.readLine())) {
                    info = parse(line);
                    if (null != info) {
                        infos.add(info);
                    }
                    //OutputUtils.output(info);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                StreamUtils.closeSafely(is);
            }
        }
        return infos;
    }
    
    private MovieInfo parse(String line) {
        MovieInfo info = null;
        if (!TextUtils.isEmpty(line)) {
            String[] strs = line.split("::");
            if (null != strs && strs.length == 3) {
                info = new MovieInfo(strs[0], strs[1], strs[2]);
            }
        }
        return info;
    }
    
    /**
     * @param args
     */
    public static void main(String[] args) {
        FileInputStream fis;
        try {
            fis = new FileInputStream("movies.dat");
            List<MovieInfo> infos = new MovieDataParser().parse(fis);
            // OutputUtils.output(infos);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

}
