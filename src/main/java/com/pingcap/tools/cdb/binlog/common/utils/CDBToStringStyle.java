package com.pingcap.tools.cdb.binlog.common.utils;

import org.apache.commons.lang.builder.ToStringStyle;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by iamxy on 2017/2/19.
 */
public class CDBToStringStyle extends ToStringStyle {

    private static final long serialVersionUID = -7935818251713616588L;

    private static final String DEFAULT_TIME = "yyyy-MM-dd HH:mm:ss";
    private static final String DEFAULT_DAY = "yyyy-MM-dd";

    public static final ToStringStyle TIME_STYLE = new OtterDateStyle(DEFAULT_TIME);

    public static final ToStringStyle DAY_STYLE = new OtterDateStyle(DEFAULT_DAY);

    public static final ToStringStyle DEFAULT_STYLE = CDBToStringStyle.TIME_STYLE;

    private static class OtterDateStyle extends ToStringStyle {

        private static final long serialVersionUID = 5208917932254652886L;

        private String pattern;

        public OtterDateStyle(String pattern) {
            super();
            this.setUseShortClassName(true);
            this.setUseIdentityHashCode(false);
            this.pattern = pattern;
        }

        protected void appendDetail(StringBuffer buffer, String fieldName, Object value) {
            if (value instanceof Date) {
                value = new SimpleDateFormat(pattern).format(value);
            }
            buffer.append(value);
        }
    }
}
