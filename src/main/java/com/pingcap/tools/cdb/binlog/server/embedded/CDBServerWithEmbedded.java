package com.pingcap.tools.cdb.binlog.server.embedded;

import com.google.common.collect.MigrateMap;
import com.pingcap.tools.cdb.binlog.common.AbstractCDBLifeCycle;
import com.pingcap.tools.cdb.binlog.instance.core.CDBInstance;
import com.pingcap.tools.cdb.binlog.instance.core.CDBInstanceGenerator;
import com.pingcap.tools.cdb.binlog.server.CDBServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.util.Map;

/**
 * Created by iamxy on 2017/2/17.
 */
public class CDBServerWithEmbedded extends AbstractCDBLifeCycle implements CDBServer {

    private static final Logger logger = LoggerFactory.getLogger(CDBServerWithEmbedded.class);
    private Map<String, CDBInstance> cdbInstances;
    private CDBInstanceGenerator cdbInstanceGenerator;

    private static class SingletonHolder {

        private static final CDBServerWithEmbedded CDB_SERVER_WITH_EMBEDDED = new CDBServerWithEmbedded();
    }

    public CDBServerWithEmbedded() {
    }

    public static CDBServerWithEmbedded instance() {
        return SingletonHolder.CDB_SERVER_WITH_EMBEDDED;
    }

    public void start() {
        if (!isStart()) {
            super.start();
            cdbInstances = MigrateMap.makeComputingMap(destination -> cdbInstanceGenerator.generate(destination));
        }
    }

    public void stop() {
        super.stop();

        for (Map.Entry<String, CDBInstance> entry : cdbInstances.entrySet()) {
            String destination = entry.getKey();
            CDBInstance instance = entry.getValue();

            try {
                if (instance.isStart()) {
                    try {
                        MDC.put("destination", destination);
                        instance.stop();
                        logger.info("stop CDB Instances[{}] successfully", destination);
                    } finally {
                        MDC.remove("destination");
                    }
                }
            } catch (Exception e) {
                logger.error(String.format("stop CDB Instance[%s] error", destination), e);
            }
        }
    }

    public void startInstance(final String destination) {
        final CDBInstance cdbInstance = cdbInstances.get(destination);
        if (!cdbInstance.isStart()) {
            try {
                MDC.put("destination", destination);
                cdbInstance.start();
                logger.info("start CDB Instances[{}] successfully", destination);
            } finally {
                MDC.remove("destination");
            }
        }
    }

    public void stopInstance(final String destination) {
        CDBInstance cdbInstance = cdbInstances.remove(destination);
        if (cdbInstance != null) {
            if (cdbInstance.isStart()) {
                try {
                    MDC.put("destination", destination);
                    cdbInstance.stop();
                    logger.info("stop CDB Instances[{}] successfully", destination);
                } finally {
                    MDC.remove("destination");
                }
            }
        }
    }

    public boolean isInstanceStart(String destination) {
        return cdbInstances.containsKey(destination) && cdbInstances.get(destination).isStart();
    }

    public void setCDBInstanceGenerator(CDBInstanceGenerator cdbInstanceGenerator) {
        this.cdbInstanceGenerator = cdbInstanceGenerator;
    }
}
