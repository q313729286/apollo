/*
 * Copyright (C) 2013 Baidu Inc. All rights reserved.
 */
package seker.common.utils;

import java.util.Iterator;
import java.util.List;

/**
 * 
 * @author seker
 * @since 2013-9-28
 */
public class OutputUtils {
    
    public static void output(Object obj) {
        System.out.println(obj);
    }
    
    public static void output(List<?> list) {
        if (null != list && !list.isEmpty()) {
            Iterator<?> i = list.iterator();
            while (i.hasNext()) {
                System.out.println(i.next());
            }
        }
    }

}
