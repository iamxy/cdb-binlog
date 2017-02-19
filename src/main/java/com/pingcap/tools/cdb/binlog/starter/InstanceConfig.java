package com.pingcap.tools.cdb.binlog.starter;

import com.pingcap.tools.cdb.binlog.common.utils.CDBToStringStyle;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Created by iamxy on 2017/2/16.
 */
public class InstanceConfig {

    private InstanceConfig globalConfig;
    private String springXml;

    public InstanceConfig() {
    }

    public InstanceConfig(InstanceConfig globalConfig) {
        this.globalConfig = globalConfig;
    }

    public String getSpringXml() {
        if (springXml == null && globalConfig != null) {
            return globalConfig.getSpringXml();
        } else {
            return springXml;
        }
    }

    public void setSpringXml(String springXml) {
        this.springXml = springXml;
    }

    public String toString() {
        return ToStringBuilder.reflectionToString(this, CDBToStringStyle.DEFAULT_STYLE);
    }

}
