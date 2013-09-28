package seker.multimedia.bean;

public class FileStatus {

    public enum Type {
        FILE, DIRECTORY;
    }

    public long accessTime;
    public long blockSize;
    public String group;
    public long length;
    public long modificationTime;
    public String owner;
    public String pathSuffix;
    public String permission;
    @Override
    public String toString() {
        return "FileStatus [accessTime=" + accessTime + ", blockSize=" + blockSize + ", group=" + group + ", length="
                + length + ", modificationTime=" + modificationTime + ", owner=" + owner + ", pathSuffix=" + pathSuffix
                + ", permission=" + permission + ", replication=" + replication + ", type=" + type + "]";
    }
    public long replication;
    public String type;
}
