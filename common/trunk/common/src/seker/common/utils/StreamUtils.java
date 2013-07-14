package seker.common.utils;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

public final class StreamUtils {

    public static class Word {
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
    }
    
    /**
     * Stream buffer size.
     */
    public static final int STREAM_BUFFER_SIZE = 8192;

    private StreamUtils() {
    }

    /**
     * stream to bytes
     * 
     * @param is
     *            inputstream
     * @return bytes
     */
    public static byte[] streamToBytes(InputStream is) {
        if (null == is) {
            return null;
        }

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try {
            byte[] buffer = new byte[STREAM_BUFFER_SIZE];
            int n = 0;
            while (-1 != (n = is.read(buffer))) {
                output.write(buffer, 0, n);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeSafely(is);
        }
        return output.toByteArray();
    }

    /**
     * 转换Stream成string
     * 
     * @param is
     *            Stream源
     * @return 目标String
     */
    public static String streamToString(InputStream is) {
        return streamToString(is, "UTF-8");
    }

    /**
     * 按照特定的编码格式转换Stream成string
     * 
     * @param is
     *            Stream源
     * @param enc
     *            编码格式
     * @return 目标String
     */
    public static String streamToString(InputStream is, String enc) {
        if (null == is) {
            return null;
        }

        StringBuilder buffer = new StringBuilder();
        String line = null;
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, enc), STREAM_BUFFER_SIZE);
            while (null != (line = reader.readLine())) {
                buffer.append(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeSafely(is);
        }
        return buffer.toString();
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
            String str = streamToString(is, enc);
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
    
    /**
     * 将输入流中的数据保存到文件
     * 
     * @param is
     *            输入流
     * @param file
     *            目标文件
     * @return true:保存成功，false:保存失败
     */
    public static boolean streamToFile(InputStream is, File file) {
        boolean bRet = false;
        if (null == is || null == file) {
            return bRet;
        }

        // 下载后文件存在临时目录中
        File dir = file.getParentFile();
        if (!dir.exists()) {
            dir.mkdirs();
        }

        if (file.exists()) {
            file.delete();
        }

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            byte[] buffer = new byte[STREAM_BUFFER_SIZE];
            int length = -1;
            while ((length = is.read(buffer)) != -1) {
                fos.write(buffer, 0, length);
            }
            fos.flush();
            bRet = true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeSafely(fos);
            closeSafely(is);
        }
        return bRet;
    }

    /**
     * 安全关闭.
     * 
     * @param closeable
     *            Closeable.
     */
    public static void closeSafely(Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
