package com.pingcap.tools.cdb.binlog.starter;

import java.text.MessageFormat;

/**
 * Created by iamxy on 2017/2/17.
 */
public class CDBConstants {

    public static final String MDC_DESTINATION = "destination";
    public static final String ROOT = "cdb";

    public static final String CDB_DESTINATIONS = ROOT + "." + "destinations";
    public static final String CDB_AUTO_SCAN = ROOT + "." + "auto.scan";
    public static final String CDB_AUTO_SCAN_INTERVAL = ROOT + "." + "auto.scan.interval";
    public static final String CDB_CONF_DIR = ROOT + "." + "conf.dir";

    public static final String CDB_DESTINATION_SPLIT = ",";
    public static final String GLOBAL_NAME = "global";

    public static final String INSTANCE_SPRING_XML_TEMPLATE = ROOT + "." + "instance.{0}.spring.xml";

    public static final String CDB_DESTINATION_PROPERTY = ROOT + ".instance.destination";

    public static String getInstancSpringXmlKey(String destination) {
        return MessageFormat.format(INSTANCE_SPRING_XML_TEMPLATE, destination);
    }
}
