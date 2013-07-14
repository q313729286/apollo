package seker.multimedia.bean;

import java.util.ArrayList;

public class FileStatusWrapper {
    public ArrayList<FileStatus> FileStatus;

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        for (FileStatus file : FileStatus) {
            buf.append(file.toString()).append(", ");
        }
        if (buf.length() > 0) {
            buf.delete(buf.length() - 2, buf.length());
        }
        return "FileStatusWrapper: [FileStatus=" + buf.toString() + "]";
    }
}
