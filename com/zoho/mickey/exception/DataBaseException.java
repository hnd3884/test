package com.zoho.mickey.exception;

import java.util.logging.Level;
import java.util.logging.Logger;

public class DataBaseException extends Exception
{
    private static final long serialVersionUID = 1L;
    public static final int ERROR_WHILE_GETTING_CONNECTION = 1000;
    public static final int ENCRYPTION_NOT_SUPPORTED = 1001;
    public static final int MASTER_KEY_NOT_CREATED = 1002;
    public static final int CREATE_MASTER_KEY_FAILED = 1003;
    public static final int OPEN_MASTER_KEY_FAILED = 1004;
    public static final int ASSOCIATE_MASTER_KEY_WITH_SERVICE_MASTER_KEY_FAILED = 1005;
    public static final int CERFICATE_NOT_CREATED = 1006;
    public static final int CREATE_CERTIFICATE_FAILED = 1007;
    public static final int OPEN_CERTIFICATE_FAILED = 1008;
    public static final int SYMMETRIC_KEY_NOT_CREATED = 1009;
    public static final int CREATE_SYMMETRIC_KEY_FAILED = 1010;
    public static final int OPEN_SYMMETRIC_KEY_FAILED = 1011;
    public static final int CLOSE_SYMMETRIC_KEY_FAILED = 1012;
    public static final int CREATE_EXTENSIONS_FAILED = 1013;
    public static final int ERROR_WHILE_RETRIVING_COLLATION = 1014;
    public static final int ERROR_WHILE_RETRIVING_DATABASE_METADATA = 1015;
    public static final int ERROR_WHILE_CREATING_STATEMENT = 1016;
    public static final int ERROR_WHILE_CLOSING_STATEMENT = 1017;
    public static final int ERROR_WHILE_CLOSING_CONNECTION = 1018;
    private int errorCode;
    private String message;
    private static final Logger OUT;
    Throwable cause;
    
    public DataBaseException(final int errorCode, final String message) {
        this.errorCode = -1;
        this.message = null;
        this.cause = this;
        this.errorCode = errorCode;
        this.message = message;
    }
    
    public DataBaseException(final Throwable throwable) {
        this(-1, null, throwable);
    }
    
    public DataBaseException(final int errorCode, final Throwable throwable) {
        this(errorCode, null, throwable);
    }
    
    public DataBaseException(final int errorCode, final String message, final Throwable throwable) {
        super(throwable.getMessage(), throwable);
        this.errorCode = -1;
        this.message = null;
        this.cause = this;
        this.errorCode = errorCode;
        this.message = message;
    }
    
    public int getErrorCode() {
        return this.errorCode;
    }
    
    @Override
    public String getLocalizedMessage() {
        return this.message;
    }
    
    @Override
    public void printStackTrace() {
        if (null != this.cause && this.cause instanceof DataBaseException) {
            DataBaseException.OUT.log(Level.INFO, "Error Code : " + ((DataBaseException)this.cause).getErrorCode());
            DataBaseException.OUT.log(Level.INFO, "Localized Error Msg : " + ((DataBaseException)this.cause).getLocalizedMessage());
            DataBaseException.OUT.log(Level.INFO, "Error Msg : " + this.cause.getMessage());
            final Throwable getCause = this.cause.getCause();
            if (null != getCause) {
                getCause.printStackTrace();
            }
        }
        else {
            DataBaseException.OUT.fine("Calling Exception");
            this.cause.printStackTrace();
        }
    }
    
    static {
        OUT = Logger.getLogger(DataBaseException.class.getName());
    }
}
