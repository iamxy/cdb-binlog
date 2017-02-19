package com.pingcap.tools.cdb.binlog.common;

/**
 * Created by iamxy on 2017/2/17.
 */
public interface CDBLifeCycle {

    void start();

    void stop();

    boolean isStart();
}
