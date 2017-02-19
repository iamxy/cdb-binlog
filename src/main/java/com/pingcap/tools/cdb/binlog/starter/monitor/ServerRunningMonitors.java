package com.pingcap.tools.cdb.binlog.starter.monitor;

import java.util.Map;

/**
 * Created by iamxy on 2017/2/17.
 */
public class ServerRunningMonitors {

    private static Map runningMonitors;

    public static Map<String, ServerRunningMonitor> getRunningMonitors() {
        return runningMonitors;
    }

    public static ServerRunningMonitor getRunningMonitor(String destination) {
        return (ServerRunningMonitor) runningMonitors.get(destination);
    }

    public static void setRunningMonitors(Map runningMonitors) {
        ServerRunningMonitors.runningMonitors = runningMonitors;
    }
}
