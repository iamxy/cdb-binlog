package com.pingcap.tools.cdb.binlog.starter.monitor;

import com.pingcap.tools.cdb.binlog.common.AbstractCDBLifeCycle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by iamxy on 2017/2/17.
 */
public class ServerRunningMonitor extends AbstractCDBLifeCycle {

    private static final Logger logger = LoggerFactory.getLogger(ServerRunningMonitor.class);
    private String destination;
    private ServerRunningListener listener;

    public ServerRunningMonitor(String destination) {
        this.destination = destination;
    }

    public void init() {
        processStart();
    }

    public void start() {
        super.start();
        processStart();
        processActiveEnter();
    }

    public void release() {
        processActiveExit();
    }

    public void stop() {
        processActiveExit();
        processStop();
        super.stop();

    }

    private void processStart() {
        if (listener != null) {
            try {
                listener.processStart();
            } catch (Exception e) {
                logger.error("processStart failed", e);
            }
        }
    }

    private void processStop() {
        if (listener != null) {
            try {
                listener.processStop();
            } catch (Exception e) {
                logger.error("processStop failed", e);
            }
        }
    }

    private void processActiveEnter() {
        if (listener != null) {
            try {
                listener.processActiveEnter();
            } catch (Exception e) {
                logger.error("processActiveEnter failed", e);
            }
        }
    }

    private void processActiveExit() {
        if (listener != null) {
            try {
                listener.processActiveExit();
            } catch (Exception e) {
                logger.error("processActiveExit failed", e);
            }
        }
    }

    public void setListener(ServerRunningListener listener) {
        this.listener = listener;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

}
