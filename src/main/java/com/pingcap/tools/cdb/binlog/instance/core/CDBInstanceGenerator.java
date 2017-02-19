package com.pingcap.tools.cdb.binlog.instance.core;

/**
 * Created by iamxy on 2017/2/16.
 */
public interface CDBInstanceGenerator {
    CDBInstance generate(String destination);
}
