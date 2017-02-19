package com.pingcap.tools.cdb.binlog.starter.monitor;

import com.pingcap.tools.cdb.binlog.common.CDBLifeCycle;

/**
 * Created by iamxy on 2017/2/16.
 */
public interface InstanceConfigMonitor extends CDBLifeCycle {

    void register(String destination, InstanceAction action);

    void unregister(String destination);
}
