package com.pingcap.tools.cdb.binlog.listener.mysql;

import com.qcloud.dts.message.DataMessage.Record.Type;

import java.util.List;

/**
 * Created by cwen0 on 2017/2/20.
 */
public class CDBFieldData {
    private String dbName;
    private String tableName;
    private Type type;
    private int columnCount;
    private List<String> columns;
    private List<String> values;
    private List<String> primaryKey;
    private List<String> primaryValue;
    private String checkPoint;

    public CDBFieldData() {
        super();
    }

    public CDBFieldData(String dbName, String tableName,Type type, int columnCount, List<String> columns,
                     List<String> values, List<String> primaryKey, List<String> primaryValue, String checkPoint) {
        super();
        this.dbName = dbName;
        this.tableName = tableName;
        this.type = type;
        this.columnCount = columnCount;
        this.columns = columns;
        this.values = values;
        this.primaryKey = primaryKey;
        this.primaryValue = primaryValue;
        this.checkPoint = checkPoint;
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public int getColumnCount() {
        return columnCount;
    }

    public void setColumnCount(int columnCount) {
        this.columnCount = columnCount;
    }

    public List<String> getColumns() {
        return this.columns;
    }

    public void setColumns(List<String> columns) {
        this.columns = columns;
    }

    public List<String> getValues() {
        return this.values;
    }

    public void setValues(List<String> values) {
        this.values = values;
    }

    public List<String> getPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(List<String> primaryKey) {
        this.primaryKey = primaryKey;
    }

    public List<String> getPrimaryValue() {
        return primaryValue;
    }

    public void setPrimaryValue(List<String> primaryValue) {
        this.primaryValue = primaryValue;
    }

    public String getCheckPoint() {
        return checkPoint;
    }

    public void setCheckPoint(String checkPoint) {
        this.checkPoint = checkPoint;
    }
}
