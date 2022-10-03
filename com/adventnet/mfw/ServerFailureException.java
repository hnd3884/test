package com.adventnet.mfw;

import java.util.logging.Level;
import java.util.logging.Logger;

public class ServerFailureException extends Exception
{
    private static final long serialVersionUID = 1L;
    public static final int SERVER_INSTANCE_ALREADY_RUNNING = 10000;
    public static final int DB_STARTUP_FAILED = 10001;
    public static final int MODULE_LOAD_FAILURE = 10002;
    public static final int SAFE_MODE_NOT_ALLOWED_DURING_COLD_START = 10003;
    public static final int PREVIOUS_START_FAILED = 10004;
    public static final int PATCH_NOT_PROPERLY_APPLIED = 10005;
    public static final int INITIALIZING_SERVICES_FAILED = 10006;
    public static final int ERROR_WHILE_ADDING_MODULES = 10007;
    public static final int ERROR_WHILE_INITIALIZING_PERSISTENCE_MODULE = 10008;
    public static final int UNKNOWN_ERROR = 10009;
    public static final int CONCURRENT_STARTUP_METADATA_LOADING_FAILURE = 10010;
    public static final int CONCURRENT_STARTUP_MODULELEVEL_VALIDATION_FAILURE = 10011;
    public static final int CONCURRENT_STARTUP_TABLECREATION_FAILURE = 10012;
    public static final int CONCURRENT_STARTUP_MODULEPOPULATION_FAILURE = 10013;
    public static final int MODULE_PRE_START_PROCESS_FAILURE = 10014;
    public static final int MODULE_POST_START_PROCESS_FAILURE = 10015;
    public static final int INVALID_CRYPT_TAG = 10016;
    public static final int LICENSE_VALIDATION_FAILURE = 10017;
    private int errorCode;
    private String message;
    private static final Logger OUT;
    Throwable cause;
    
    public ServerFailureException(final int errorCode, final String message) {
        this.errorCode = -1;
        this.message = null;
        this.cause = this;
        this.errorCode = errorCode;
        this.message = message;
    }
    
    public ServerFailureException(final Throwable throwable) {
        this(-1, null, throwable);
    }
    
    public ServerFailureException(final int errorCode, final Throwable throwable) {
        this(errorCode, null, throwable);
    }
    
    public ServerFailureException(final int errorCode, final String message, final Throwable throwable) {
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
    public String getMessage() {
        return this.message;
    }
    
    @Override
    public void printStackTrace() {
        ServerFailureException.OUT.fine("Inside ServerFailureException!!!");
        if (null != this.cause && this.cause instanceof ServerFailureException) {
            ServerFailureException.OUT.fine("Calling ServerFailureException");
            ServerFailureException.OUT.log(Level.INFO, "Error Code : " + ((ServerFailureException)this.cause).getErrorCode());
            ServerFailureException.OUT.log(Level.INFO, "Localized Error Msg : " + ((ServerFailureException)this.cause).getLocalizedMessage());
            ServerFailureException.OUT.log(Level.INFO, "Error Msg : " + ((ServerFailureException)this.cause).getMessage());
            final Throwable getCause = this.cause.getCause();
            if (null != getCause) {
                getCause.printStackTrace();
            }
        }
        else {
            ServerFailureException.OUT.fine("Calling Exception");
            this.cause.printStackTrace();
        }
    }
    
    static {
        OUT = Logger.getLogger(ServerFailureException.class.getName());
    }
}
