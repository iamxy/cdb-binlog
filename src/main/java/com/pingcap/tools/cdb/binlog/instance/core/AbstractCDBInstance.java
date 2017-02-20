package com.pingcap.tools.cdb.binlog.instance.core;

import com.pingcap.tools.cdb.binlog.common.AbstractCDBLifeCycle;
import com.pingcap.tools.cdb.binlog.listener.CDBEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by iamxy on 2017/2/17.
 */
public abstract class AbstractCDBInstance extends AbstractCDBLifeCycle implements CDBInstance {

    private static final Logger logger = LoggerFactory.getLogger(AbstractCDBInstance.class);

    protected String destination;
    protected CDBEventListener eventListener;

    @Override
    public void start() {
        super.start();
    }

    @Override
    public void stop() {
        super.stop();
    }

    @Override
    public String getDestination() {
        return destination;
    }

    @Override
    public CDBEventListener getEventListener() {
        return eventListener;
    }
}
