package com.pingcap.tools.cdb.binlog.starter.monitor;

/**
 * Created by iamxy on 2017/2/16.
 */
public interface InstanceAction {

    void start(String destination);

    void stop(String destination);

    void reload(String destination);
}
