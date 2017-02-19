package com.pingcap.tools.cdb.binlog.starter;

import com.google.common.base.Function;
import com.google.common.collect.MapMaker;
import com.google.common.collect.MigrateMap;
import com.pingcap.tools.cdb.binlog.common.CDBException;
import com.pingcap.tools.cdb.binlog.instance.core.CDBInstance;
import com.pingcap.tools.cdb.binlog.instance.core.CDBInstanceGenerator;
import com.pingcap.tools.cdb.binlog.instance.spring.SpringCDBInstanceGenerator;
import com.pingcap.tools.cdb.binlog.server.embedded.CDBServerWithEmbedded;
import com.pingcap.tools.cdb.binlog.server.exception.CDBServerException;
import com.pingcap.tools.cdb.binlog.starter.monitor.*;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Map;
import java.util.Properties;

/**
 * Created by iamxy on 2017/2/16.
 */
public class CDBController {

    private static final Logger logger = LoggerFactory.getLogger(CDBController.class);
    private Map<String, InstanceConfig> instanceConfigs;
    private InstanceConfig globalInstanceConfig;
    private boolean autoScan = true;
    private InstanceAction defaultAction;
    private InstanceConfigMonitor instanceConfigMonitor;
    private CDBInstanceGenerator instanceGenerator;
    private CDBServerWithEmbedded embededCDBServer;

    public CDBController() {
        this(System.getProperties());
    }

    public CDBController(final Properties properties) {
        globalInstanceConfig = initGlobalConfig(properties);
        instanceConfigs = initInstanceConfig(properties);

        instanceGenerator = new CDBInstanceGenerator() {
            public CDBInstance generate(String destination) {
                InstanceConfig config = instanceConfigs.get(destination);
                if (config == null) {
                    throw new CDBServerException("can't find destination:{}");
                }

                SpringCDBInstanceGenerator instanceGenerator = new SpringCDBInstanceGenerator();
                synchronized (this) {
                    try {
                        System.setProperty(CDBConstants.CDB_DESTINATION_PROPERTY, destination);
                        instanceGenerator.setBeanFactory(getBeanFactory(config.getSpringXml()));
                        return instanceGenerator.generate(destination);
                    } catch (Throwable e) {
                        logger.error("generate instance failed", e);
                        throw new CDBException(e);
                    } finally {
                        System.setProperty(CDBConstants.CDB_DESTINATION_PROPERTY, "");
                    }
                }
            }
        };

        embededCDBServer = CDBServerWithEmbedded.instance();
        embededCDBServer.setCDBInstanceGenerator(instanceGenerator);

        ServerRunningMonitors.setRunningMonitors(MigrateMap.makeComputingMap((Function<String, ServerRunningMonitor>) destination -> {
            ServerRunningMonitor runningMonitor = new ServerRunningMonitor(destination);
            runningMonitor.setListener(new ServerRunningListener() {
                public void processActiveEnter() {
                    try {
                        MDC.put(CDBConstants.MDC_DESTINATION, String.valueOf(destination));
                        embededCDBServer.startInstance(destination);
                    } finally {
                        MDC.remove(CDBConstants.MDC_DESTINATION);
                    }
                }

                public void processActiveExit() {
                    try {
                        MDC.put(CDBConstants.MDC_DESTINATION, String.valueOf(destination));
                        embededCDBServer.stopInstance(destination);
                    } finally {
                        MDC.remove(CDBConstants.MDC_DESTINATION);
                    }
                }

                public void processStart() {
                    // TODO: handle start
                }

                public void processStop() {
                    // TODO: handle stop
                }
            });

            return runningMonitor;
        }));

        autoScan = BooleanUtils.toBoolean(getProperty(properties, CDBConstants.CDB_AUTO_SCAN));

        if (autoScan) {
            defaultAction = new InstanceAction() {
                public void start(String destination) {
                    InstanceConfig config = instanceConfigs.computeIfAbsent(destination, k -> parseInstanceConfig(properties, destination));
                    ServerRunningMonitor runningMonitor = ServerRunningMonitors.getRunningMonitor(destination);
                    if (!runningMonitor.isStart()) {
                        runningMonitor.start();
                    }
                }

                public void stop(String destination) {
                    InstanceConfig config = instanceConfigs.remove(destination);
                    if (config != null) {
                        ServerRunningMonitor runningMonitor = ServerRunningMonitors.getRunningMonitor(destination);
                        if (runningMonitor.isStart()) {
                            runningMonitor.stop();
                        }
                    }
                }

                public void reload(String destination) {
                    stop(destination);
                    start(destination);
                }
            };


            int scanInterval = Integer.valueOf(getProperty(properties, CDBConstants.CDB_AUTO_SCAN_INTERVAL));

            SpringInstanceConfigMonitor springMonitor = new SpringInstanceConfigMonitor();
            springMonitor.setScanIntervalInSecond(scanInterval);
            springMonitor.setDefaultAction(defaultAction);

            String rootDir = getProperty(properties, CDBConstants.CDB_CONF_DIR);
            if (StringUtils.isEmpty(rootDir)) {
                rootDir = "../conf";
            }

            if (StringUtils.equals("cdb-binlog", System.getProperty("appName"))) {
                springMonitor.setRootConf(rootDir);
            } else {
                springMonitor.setRootConf("src/main/resources/");
            }

            instanceConfigMonitor = springMonitor;
        }
    }

    private InstanceConfig initGlobalConfig(Properties properties) {
        InstanceConfig globalConfig = new InstanceConfig();
        String springXml = getProperty(properties, CDBConstants.getInstancSpringXmlKey(CDBConstants.GLOBAL_NAME));
        if (StringUtils.isNotEmpty(springXml)) {
            globalConfig.setSpringXml(springXml);
        }
        return globalConfig;
    }

    private BeanFactory getBeanFactory(String springXml) {
        return new ClassPathXmlApplicationContext(springXml);
    }

    private Map<String, InstanceConfig> initInstanceConfig(Properties properties) {
        Map<String, InstanceConfig> map = new MapMaker().makeMap();
        String destinationStr = getProperty(properties, CDBConstants.CDB_DESTINATIONS);
        String[] destinations = StringUtils.split(destinationStr, CDBConstants.CDB_DESTINATION_SPLIT);

        for (String destination : destinations) {
            InstanceConfig config = parseInstanceConfig(properties, destination);
            InstanceConfig oldConfig = map.put(destination, config);

            if (oldConfig != null) {
                logger.warn("destination:{} old_config:{} has replace by new_config:{}",
                        destination, oldConfig, config);
            }
        }

        return map;
    }

    private InstanceConfig parseInstanceConfig(Properties properties, String destination) {
        InstanceConfig config = new InstanceConfig(globalInstanceConfig);

        String springXml = getProperty(properties, CDBConstants.getInstancSpringXmlKey(destination));
        if (StringUtils.isNotEmpty(springXml)) {
            config.setSpringXml(springXml);
        }

        return config;
    }

    private String getProperty(Properties properties, String key) {
        return StringUtils.trim(properties.getProperty(StringUtils.trim(key)));
    }

    public void start() throws Throwable {
        logger.info("## cdb-binlog server is starting ...");
        embededCDBServer.start();

        for (Map.Entry<String, InstanceConfig> entry : instanceConfigs.entrySet()) {
            final String destination = entry.getKey();
            InstanceConfig config = entry.getValue();

            ServerRunningMonitor runningMonitor = ServerRunningMonitors.getRunningMonitor(destination);
            if (!runningMonitor.isStart()) {
                runningMonitor.start();
            }

            if (autoScan) {
                instanceConfigMonitor.register(destination, defaultAction);
            }
        }

        if (autoScan) {
            if (!instanceConfigMonitor.isStart()) {
                instanceConfigMonitor.start();
            }
        }

        logger.info("## cdb-binlog server has started!");
    }

    public void stop() throws Throwable {
        logger.info("## cdb-binlog server is shutting down ...");

        if (autoScan) {
            if (instanceConfigMonitor.isStart()) {
                instanceConfigMonitor.stop();
            }
        }

        for (ServerRunningMonitor runningMonitor : ServerRunningMonitors.getRunningMonitors().values()) {
            if (runningMonitor.isStart()) {
                runningMonitor.stop();
            }
        }

        logger.info("## cdb-binlog server has stopped!");
    }
}
