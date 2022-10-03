package com.adventnet.ds.query;

import java.util.logging.Level;
import java.util.logging.Logger;

public class QueryConstructionException extends Exception
{
    static final Logger OUT;
    public static final int INVALID_BOOLEAN_VALUE = 1;
    public static final int INVALID_CHAR_VALUE = 2;
    public static final int INVALID_SCHAR_VALUE = 3;
    public static final int INVALID_NCHAR_VALUE = 4;
    public static final int INVALID_DATE_VALUE = 5;
    public static final int INVALID_TIME_VALUE = 6;
    public static final int INVALID_DATETIME_VALUE = 7;
    public static final int INVALID_TIMESTAMP_VALUE = 8;
    public static final int INVALID_INTEGER_VALUE = 9;
    public static final int INVALID_TINYINT_VALUE = 10;
    public static final int INVALID_BIGINT_VALUE = 11;
    public static final int INVALID_DECIMAL_VALUE = 12;
    public static final int INVALID_FLOAT_VALUE = 13;
    public static final int INVALID_DOUBLE_VALUE = 14;
    public static final int INVALID_GROUPBY_CLAUSE = 51;
    private int errorCode;
    
    public QueryConstructionException() {
        this.errorCode = 0;
    }
    
    public QueryConstructionException(final String s) {
        super(s);
        this.errorCode = 0;
    }
    
    public QueryConstructionException(final String s, final int code) {
        super(s);
        this.errorCode = 0;
        this.setErrorCode(code);
    }
    
    public QueryConstructionException(final String s, final int code, final Throwable ex) {
        super(s, ex);
        this.errorCode = 0;
        this.setErrorCode(code);
    }
    
    private void setErrorCode(final int code) {
        if ((code < 1 || code > 14) && code != 51) {
            QueryConstructionException.OUT.log(Level.WARNING, "Invalid error code defined");
            Thread.dumpStack();
        }
        else {
            this.errorCode = code;
        }
    }
    
    public QueryConstructionException(final String s, final Throwable ex) {
        super(s, ex);
        this.errorCode = 0;
    }
    
    public int getErrorCode() {
        return this.errorCode;
    }
    
    public static int getErrorCodeForType(final int type) {
        switch (type) {
            case 4: {
                return 9;
            }
            case -6: {
                return 10;
            }
            case -5: {
                return 11;
            }
            case 3: {
                return 12;
            }
            case 6: {
                return 13;
            }
            case 8: {
                return 14;
            }
            case 91: {
                return 5;
            }
            case 92: {
                return 6;
            }
            case 93: {
                return 8;
            }
            case 16: {
                return 1;
            }
            default: {
                return 0;
            }
        }
    }
    
    static {
        OUT = Logger.getLogger(QueryConstructionException.class.getName());
    }
}
