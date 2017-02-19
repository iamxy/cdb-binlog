package com.pingcap.tools.cdb.binlog.starter.monitor;

/**
 * Created by iamxy on 2017/2/17.
 */
public interface ServerRunningListener {

    public void processStart();

    public void processStop();

    public void processActiveEnter();

    public void processActiveExit();
}
