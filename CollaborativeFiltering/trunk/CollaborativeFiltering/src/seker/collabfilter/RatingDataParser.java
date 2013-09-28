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
public class RatingDataParser {

    public List<RatingInfo> parse(InputStream is) {
        List<RatingInfo> infos = null;
        if (null != is) {
            infos = new ArrayList<RatingInfo>();
            String line = null;
            RatingInfo info = null;
            int i = 0;
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(is, "utf-8"), 
                        StreamUtils.STREAM_BUFFER_SIZE);
                while (null != (line = reader.readLine())) {
                    info = parse(line);
                    if (null != info) {
                        infos.add(info);
                    }
                    // OutputUtils.output((++i) + ": " + info);
                    if (i > 100000) {
                        break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                StreamUtils.closeSafely(is);
            }
        }
        return infos;
    }
    
    private RatingInfo parse(String line) {
        RatingInfo info = null;
        if (!TextUtils.isEmpty(line)) {
            String[] strs = line.split("::");
            if (null != strs && strs.length == 4) {
                info = new RatingInfo(strs[0], strs[1], Float.parseFloat(strs[2]), Long.parseLong(strs[3]));
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
            fis = new FileInputStream("ratings.dat");
            List<RatingInfo> infos = new RatingDataParser().parse(fis);
            //OutputUtils.output(infos);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

}
