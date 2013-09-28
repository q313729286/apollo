package seker.common.utils;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

public final class StreamUtils {

    
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
