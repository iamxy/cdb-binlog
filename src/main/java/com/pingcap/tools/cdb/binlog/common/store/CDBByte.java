package com.pingcap.tools.cdb.binlog.common.store;


import java.nio.ByteBuffer;

/**
 * Created by Administrator on 2017/2/20.
 */
public class CDBByte {
    private static ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);

    public static byte[] intToBytes(int x) {
        buffer.putInt(0, x);
        return buffer.array();
    }

    public static long bytesToInt(byte[] bytes) {
        buffer.put(bytes, 0, bytes.length);
        buffer.flip();//need flip
        return buffer.getInt();
    }
}
