package com.pingcap.tools.cdb.binlog.instance.spring;

import com.pingcap.tools.cdb.binlog.instance.core.AbstractCDBInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by iamxy on 2017/2/17.
 */
public class CDBInstanceWithSpring extends AbstractCDBInstance {

    private static final Logger logger = LoggerFactory.getLogger(CDBInstanceWithSpring.class);

    public void start() {
        logger.info("start CDB Instance for {}-{} ", new Object[]{1, destination});
        super.start();
    }
}
