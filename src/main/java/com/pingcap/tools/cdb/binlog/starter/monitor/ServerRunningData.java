package com.pingcap.tools.cdb.binlog.starter.monitor;

import com.pingcap.tools.cdb.binlog.common.utils.CDBToStringStyle;
import org.apache.commons.lang.builder.ToStringBuilder;

import java.io.Serializable;

/**
 * Created by iamxy on 2017/2/19.
 */
public class ServerRunningData implements Serializable {

    private static final long serialVersionUID = -5005184611411878417L;

    private String address;
    private boolean active = true;

    public ServerRunningData() {
    }

    public ServerRunningData(String address) {
        this.address = address;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String toString() {
        return ToStringBuilder.reflectionToString(this, CDBToStringStyle.DEFAULT_STYLE);
    }

}
