package com.pingcap.tools.cdb.binlog.server;

import com.pingcap.tools.cdb.binlog.server.exception.CDBServerException;

/**
 * Created by iamxy on 2017/2/17.
 */
public interface CDBServer {

    void startInstance(String destination) throws CDBServerException;

    void stopInstance(String destination) throws CDBServerException;

    boolean isInstanceStart(String destination);
}
