package com.pingcap.tools.cdb.binlog.server.exception;

import com.pingcap.tools.cdb.binlog.common.CDBException;

/**
 * Created by iamxy on 2017/2/17.
 */
public class CDBServerException extends CDBException {

    private static final long serialVersionUID = 5689086389307780918L;

    public CDBServerException(String errorCode) {
        super(errorCode);
    }

    public CDBServerException(String errorCode, Throwable cause) {
        super(errorCode, cause);
    }

    public CDBServerException(String errorCode, String errorDesc) {
        super(errorCode + ":" + errorDesc);
    }

    public CDBServerException(String errorCode, String errorDesc, Throwable cause) {
        super(errorCode + ":" + errorDesc, cause);
    }

    public CDBServerException(Throwable cause) {
        super(cause);
    }
}
