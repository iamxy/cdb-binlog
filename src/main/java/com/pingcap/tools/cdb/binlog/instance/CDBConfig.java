package com.pingcap.tools.cdb.binlog.instance;

/**
 * Created by Administrator on 2017/2/20.
 */
public class CDBConfig {
    private String dir;
    private String binlogFilePrefix;
    private int maxSize;

    public CDBConfig() {
        super();
    }

    public void setDir(String dir) {
        this.dir = dir;
    }

    public void setBinlogFilePrefix(String binlogFilePrefix) {
        this.binlogFilePrefix = binlogFilePrefix;
    }

    public void setMaxSize(int maxSize) {
        this.maxSize = maxSize;
    }

    public String getDir() {
        return dir;
    }

    public String getBinlogFilePrefix() {
        return binlogFilePrefix;
    }

    public int getMaxSize() {
        return maxSize;
    }
}
