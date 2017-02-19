package com.pingcap.tools.cdb.binlog.instance.core;

import com.pingcap.tools.cdb.binlog.common.AbstractCDBLifeCycle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by iamxy on 2017/2/17.
 */
public class AbstractCDBInstance extends AbstractCDBLifeCycle implements CDBInstance {

    private static final Logger logger = LoggerFactory.getLogger(AbstractCDBInstance.class);
    protected String destination;


    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }

    @Override
    public String getDestination() {
        return destination;
    }
}
