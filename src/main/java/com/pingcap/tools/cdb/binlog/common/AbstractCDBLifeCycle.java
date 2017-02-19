package com.pingcap.tools.cdb.binlog.common;

/**
 * Created by iamxy on 2017/2/17.
 */
public abstract class AbstractCDBLifeCycle implements CDBLifeCycle {

    protected volatile boolean running = false;

    public boolean isStart() {
        return running;
    }

    public void start() {
        if (running) {
            throw new CDBException(this.getClass().getName() + " is running, can not start repeatedly");
        }
        running = true;
    }

    public void stop() {
        if (!running) {
            throw new CDBException(this.getClass().getName() + " is not running, please check");
        }
        running = false;
    }
}
