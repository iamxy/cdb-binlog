package com.pingcap.tools.cdb.binlog.starter;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.util.Properties;

/**
 * Created by iamxy on 2017/2/16.
 */
public class CDBLauncher {

    private static final String CLASSPATH_URL_PREFIX = "classpath:";
    private static final Logger logger = LoggerFactory.getLogger(CDBLauncher.class);

    public static void main(String[] args) throws Throwable {
        try {
            String conf = System.getProperty("binlog.conf", "classpath:cdb-binlog.properties");
            Properties properties = new Properties();
            if (conf.startsWith(CLASSPATH_URL_PREFIX)) {
                conf = StringUtils.substringAfter(conf, CLASSPATH_URL_PREFIX);
                properties.load(CDBLauncher.class.getClassLoader().getResourceAsStream(conf));
            } else {
                properties.load(new FileInputStream(conf));
            }

            final CDBController controller = new CDBController(properties);
            controller.start();

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try {
                    controller.stop();
                } catch (Throwable e) {
                    logger.warn("## some error while shutting down:\n{}",
                            ExceptionUtils.getFullStackTrace(e));
                } finally {
                    logger.info("## cdb-binlog server has exited!");
                }
            }));
        } catch (Throwable e) {
            logger.error("## unexpected error:\n{}",
                    ExceptionUtils.getFullStackTrace(e));
            System.exit(-1);
        }
    }
}
