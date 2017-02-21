package com.pingcap.tools.cdb.binlog.common.store;

import com.pingcap.tools.cdb.binlog.common.store.BinlogProto.Binlog;
import com.pingcap.tools.cdb.binlog.instance.CDBConfig;

import java.io.File;
import java.io.FileOutputStream;
/**
 * Created by cwen on 2017/2/20.
 */
public class CDBFile {
    public static String binlogFilePre = "binlog-";
    public static String currentBinlogFile;
    public static long pos = 0;

    private static String cfgDir;
    private static String cfgBinlogFilePrefix;
    private static int cfgMaxSize;

    static {
        initCurrentBinlogFile();
    }

    public static void writeToFile(Binlog.Builder binlog){
        try {
            File file = new File(cfgDir + "/" + currentBinlogFile);
            if(!file.exists()) {
                file.createNewFile();
            }
            if(file.length()+binlog.toString().length() >= cfgMaxSize) {
                String fileName = file.getName();
                String numStr = fileName.replaceFirst(binlogFilePre, "");
                int nextNum = Integer.parseInt(numStr) + 1;
                String newFileName = binlogFilePre+String.format("%08d", nextNum);
                file = new File(cfgMaxSize + "/" + newFileName);
                if(!file.exists()) {
                    file.createNewFile();
                }
                currentBinlogFile = newFileName;
                FileOutputStream output = new FileOutputStream(file, true);
                try {
                    int binlogLen = binlog.build().toByteArray().length;
                    byte[] data = new byte[4 + binlogLen];
                    System.arraycopy(CDBByte.intToBytes(binlogLen), 0, data, 0, 4);
                    System.arraycopy(binlog.build().toByteArray(), 0, data, 4, binlogLen);
                    output.write(data);
                } finally {
                    pos = file.length();
                    output.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static long getPos() {
        return pos;
    }

    public static String getCurrentBinlogFile() {
        return currentBinlogFile;
    }

    private static void initCurrentBinlogFile() {
        CDBConfig config = new CDBConfig();
        cfgDir = config.getDir();
        cfgBinlogFilePrefix = config.getBinlogFilePrefix();
        cfgMaxSize = config.getMaxSize();
        if(cfgBinlogFilePrefix != "") {
            binlogFilePre = cfgBinlogFilePrefix;
        }
        File file = new File(cfgDir);
        File[] array = file.listFiles();
        int currentNum = 1;
        if (array != null) {
            for(int i = 0; i < array.length; i++) {
                if(array[i].isFile()) {
                    String fileName = array[i].getName();
                    String numStr = fileName.replaceFirst(binlogFilePre, "");
                    int tmpNum = Integer.parseInt(numStr);
                    if (tmpNum > currentNum) {
                        currentNum = tmpNum;
                    }
                }
            }
        }
        currentBinlogFile = binlogFilePre+String.format("%08d", currentNum);
    }
}
