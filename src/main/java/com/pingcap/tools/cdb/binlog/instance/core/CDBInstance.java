package com.pingcap.tools.cdb.binlog.instance.core;

import com.pingcap.tools.cdb.binlog.common.CDBLifeCycle;

/**
 * Created by iamxy on 2017/2/16.
 */
public interface CDBInstance extends CDBLifeCycle {

    String getDestination();
}
