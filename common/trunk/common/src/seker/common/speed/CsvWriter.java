/*
 * Copyright (C) 2012 Baidu Inc. All rights reserved.
 */
package seker.common.speed;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

/**
 * 保存信息到文件，保存的格式为csv文件，csv文件可以用excel打开后另存称为标准excel文件。
 * @author Liu Xinjian
 *
 */
class CsvWriter {
    /** 默认编码 */
    public static final String GB2312 = "gb2312";
    
    /**输出流Writer*/
    private OutputStreamWriter mWriter;
    
    /**输出字符编码*/
    private String mCharset = GB2312;
    
    /**
     * 构造函数。默认将覆盖之前的文件，采用gb2313编码。
     * @param filePath  将要写入的csv文件的位置。
     * @throws UnsupportedEncodingException 编码格式指定错误。
     * @throws FileNotFoundException    不能创建文件。
     */
    public CsvWriter(File filePath) throws UnsupportedEncodingException, FileNotFoundException {
        this(filePath, true);
    }
    
    /**
     * 构造函数
     * @param filePath 将要写入的csv文件的位置。
     * @param append 是否追加写入
     * @throws UnsupportedEncodingException 不支持的编码格式
     * @throws FileNotFoundException 不能创建文件
     */
    public CsvWriter(File filePath, boolean append) throws UnsupportedEncodingException, FileNotFoundException {
        this(filePath, append, GB2312);
    }
    
    /**
     * 构造函数
     * @param filePath 将要写入的csv文件的位置。
     * @param append 是否追加写入
     * @param charset 编码格式
     * @throws UnsupportedEncodingException 不支持的编码格式
     * @throws FileNotFoundException 不能创建文件
     */
    public CsvWriter(File filePath, boolean append, String charset)
            throws UnsupportedEncodingException, FileNotFoundException {
        if (charset != null && charset.length() > 0) {
            mCharset = charset;
        }
        mWriter = new OutputStreamWriter(new FileOutputStream(filePath, append), mCharset);
    }

    /**
     * 写一整行
     * @param row 一个string数组，里面为要写入的一行内容。
     * @throws IOException io异常
     */
    public void writeRow(String[] row) throws IOException {
        StringBuilder line = new StringBuilder();
        for (String str : row) {
            line.append(str.replace(',', '-')).append(",");
        }
        line.append("\r\n");
        synchronized (this) {
            mWriter.write(line.toString());
            mWriter.flush(); 
        }
    }
    /**
     * 写一个单元格
     * @param cell  单元格内容 
     * @throws IOException io异常
     */
    public void writeCell(String cell) throws IOException {
        cell = cell.replace(',', '-');
        synchronized (this) {
            mWriter.write(cell + ",");
            mWriter.flush();
        }
    }
    /**
     * 关闭
     * @throws IOException io异常
     */
    public void close() throws IOException {
        synchronized (this) {
            mWriter.write("\r\n");
            mWriter.close();
        }
    }
}

