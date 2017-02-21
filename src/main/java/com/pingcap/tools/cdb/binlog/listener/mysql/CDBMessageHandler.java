package com.pingcap.tools.cdb.binlog.listener.mysql;

import com.qcloud.dts.message.DataMessage;
import com.qcloud.dts.message.DataMessage.Record;
import com.qcloud.dts.message.DataMessage.Record.Type;
import com.qcloud.dts.message.DataMessage.Record.Field;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.pingcap.tools.cdb.binlog.common.store.BinlogProto.Binlog;
import com.pingcap.tools.cdb.binlog.common.store.BinlogProto.Pos;
import com.pingcap.tools.cdb.binlog.common.store.BinlogProto.BinlogType;
import com.pingcap.tools.cdb.binlog.common.store.BinlogProto.Row;
import com.pingcap.tools.cdb.binlog.common.store.CDBFile;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cwen on 2017/2/20.
 */
public class CDBMessageHandler {
    private Logger log = LoggerFactory.getLogger(CDBMessageHandler.class);

    public void handle(Record record) {
        try {
            CDBFieldData data = getColumnsAndValues(record);
            Binlog.Builder binlog = Binlog.newBuilder();
            switch (data.getType()){
                case INSERT:
                    binlog.setType(BinlogType.INSERT);
                    break;
                case UPDATE:
                    binlog.setType(BinlogType.UPDATE);
                    break;
                case DELETE:
                    binlog.setType(BinlogType.DELETE);
                    break;
                case DDL:
                    binlog.setType(BinlogType.DDL);
                    break;
                default:
                    break;
            }
            for(int i = 0; i < data.getColumnCount();i++) {
                Row.Builder row = Row.newBuilder();
                row.setColumnName(data.getColumns().get(i));
                row.setColumnValue(data.getValues().get(i));
                row.setSql(data.getValues().get(i));
                binlog.setRows(i, row);
            }

            for(int i = 0; i < data.getPrimaryKey().size(); i++) {
                Row.Builder row = Row.newBuilder();
                row.setColumnName(data.getPrimaryKey().get(i));
                row.setColumnValue(data.getPrimaryValue().get(i));
                binlog.setPrimaryKey(i, row);
            }
            Pos.Builder pos = Pos.newBuilder();
            pos.setBinlogFile(CDBFile.getCurrentBinlogFile());
            pos.setPos(CDBFile.getPos());
            binlog.setPostion(pos);
            binlog.setDbName(data.getDbName());
            binlog.setTableName(data.getTableName());
            binlog.setColumnCount(data.getColumnCount());
            binlog.setCheckPoint(data.getCheckPoint());
            CDBFile.writeToFile(binlog);

        } catch (Exception e) {
            log.error("write to file error", e);
        }
    }

    public CDBFieldData getColumnsAndValues(DataMessage.Record record) {
        String dbName = record.getDbName();
        String tableName = record.getTableName();
        List<String> columns = new ArrayList<String>();
        List<String> values = new ArrayList<String>();
        List<String> primaryKey = new ArrayList<String>();
        List<String> primaryValue = new ArrayList<String>();

        int columnCount = 0;
        if (record.getOpt() == Type.UPDATE) {
            columnCount = record.getFieldCount() / 2;
        }else {
            columnCount = record.getFieldCount();
        }
        List<Field> fields = record.getFieldList();
        int i = 0;
        while (i < fields.size()) {
            Field f = null;
            if(record.getOpt() == Type.UPDATE) {
                f = fields.get(i + 1);
            }else {
                f = fields.get(i);
            }

            try {
                String key = f.getFieldname();
                String value = f.getValue().toString();
                columns.add(key);
                values.add(value);
                if(f.isPrimary()) {
                    primaryKey.add(key);
                    primaryValue.add(value);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            if(record.getOpt() == Type.UPDATE) {
                i += 2;
            }else {
                i++;
            }
        }
        CDBFieldData data = new CDBFieldData(dbName, tableName,record.getOpt(),columnCount,columns,values,
                primaryKey,primaryValue, record.getCheckpoint());
        return data;
    }
}
