package com.pingcap.tools.cdb.binlog.common.store;

import com.pingcap.tools.cdb.binlog.common.store.BinlogProto.Binlog;

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


    public static void writeToFile(Binlog.Builder binlog){
        try {
            File file = new File(cfgDir + "/" + currentBinlogFile);
            if(!file.exists()) {
                file.createNewFile();
            }
            System.out.println(file.length()+binlog.build().toByteArray().length);
            System.out.println(cfgMaxSize);
            if(file.length()+binlog.build().toByteArray().length >= cfgMaxSize) {
                String fileName = file.getName();
                String numStr = fileName.replaceFirst(binlogFilePre, "");
                int nextNum = Integer.parseInt(numStr) + 1;
                String newFileName = binlogFilePre + String.format("%08d", nextNum);
                file = new File(cfgDir + "/" + newFileName);
                if (!file.exists()) {
                    file.createNewFile();
                }
                currentBinlogFile = newFileName;
            }
            FileOutputStream output = new FileOutputStream(file, true);
            try {
                int binlogLen = binlog.build().toByteArray().length;
                System.out.println(CDBByte.longToBytes((long)binlogLen));
                output.write(CDBByte.longToBytes((long)binlogLen));
                binlog.build().writeTo(output);
            } finally {
                pos = file.length();
                output.close();
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

    public static void initCurrentBinlogFile(String tDir, String tBinlogFilePrefix, int tMaxSize) {
        cfgDir = tDir;
        cfgBinlogFilePrefix = tBinlogFilePrefix;
        cfgMaxSize = tMaxSize;
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
