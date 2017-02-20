package com.pingcap.tools.cdb.binlog.instance.spring;

import com.pingcap.tools.cdb.binlog.instance.core.AbstractCDBInstance;
import com.pingcap.tools.cdb.binlog.listener.CDBEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by iamxy on 2017/2/17.
 */
public class CDBInstanceWithSpring extends AbstractCDBInstance {

    private static final Logger logger = LoggerFactory.getLogger(CDBInstanceWithSpring.class);

    @Override
    public void start() {
        logger.info("CDB Instance for {}-{} is starting ...", new Object[]{1, destination});
        super.start();
        this.eventListener.start();
        logger.info("CDB Instance for {}-{} started", new Object[]{1, destination});
    }

    @Override
    public void stop() {
        logger.info("CDB Instance for {}-{} is stopping ...", new Object[]{1, destination});
        this.eventListener.stop();
        super.stop();
        logger.info("CDB Instance for {}-{} stopped", new Object[]{1, destination});
    }

    public void setEventListener(CDBEventListener eventListener) {
        this.eventListener = eventListener;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }
}
