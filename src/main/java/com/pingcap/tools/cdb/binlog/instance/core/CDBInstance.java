package com.pingcap.tools.cdb.binlog.instance.core;

import com.pingcap.tools.cdb.binlog.common.CDBLifeCycle;
import com.pingcap.tools.cdb.binlog.listener.CDBEventListener;

/**
 * Created by iamxy on 2017/2/16.
 */
public interface CDBInstance extends CDBLifeCycle {

    String getDestination();

    CDBEventListener getEventListener();
}
