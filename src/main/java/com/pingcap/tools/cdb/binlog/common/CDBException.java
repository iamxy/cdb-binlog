package com.pingcap.tools.cdb.binlog.common;

import org.apache.commons.lang.exception.NestableRuntimeException;

/**
 * Created by iamxy on 2017/2/17.
 */
public class CDBException extends NestableRuntimeException {

    private static final long serialVersionUID = 1538612822916207497L;

    public CDBException(String errorCode) {
        super(errorCode);
    }

    public CDBException(String errorCode, Throwable cause) {
        super(errorCode, cause);
    }

    public CDBException(String errorCode, String errorDesc) {
        super(errorCode + ":" + errorDesc);
    }

    public CDBException(String errorCode, String errorDesc, Throwable cause) {
        super(errorCode + ":" + errorDesc, cause);
    }

    public CDBException(Throwable cause) {
        super(cause);
    }

    public Throwable fillInStackTrace() {
        return this;
    }
}
