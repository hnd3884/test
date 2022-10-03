package com.adventnet.db.adapter;

public class BackupRestoreException extends Exception
{
    private static final long serialVersionUID = 1L;
    private String errorCode;
    
    public BackupRestoreException(final String message) {
        super(message);
        this.errorCode = null;
    }
    
    public BackupRestoreException(final BackupErrors error) {
        this(error.getMessage(), error.getCode());
    }
    
    public BackupRestoreException(final BackupErrors error, final Throwable t) {
        super(error.getMessage(), t);
        this.errorCode = null;
        this.errorCode = error.getCode();
    }
    
    public BackupRestoreException(final RestoreErrors error) {
        this(error.getMessage(), error.getCode());
    }
    
    public BackupRestoreException(final RestoreErrors error, final Throwable t) {
        super(error.getMessage(), t);
        this.errorCode = null;
        this.errorCode = error.getCode();
    }
    
    public BackupRestoreException(final String message, final String errorCode) {
        super(message);
        this.errorCode = null;
        this.errorCode = errorCode;
    }
    
    public BackupRestoreException(final String message, final Throwable t) {
        super(message, t);
        this.errorCode = null;
    }
    
    public BackupRestoreException(final String message, final Throwable t, final String errorCode) {
        super(message, t);
        this.errorCode = null;
        this.errorCode = errorCode;
    }
    
    public String getErrorCode() {
        return this.errorCode;
    }
    
    @Override
    public String toString() {
        final String s = this.getClass().getName();
        final String message = this.getLocalizedMessage();
        if (this.errorCode != null) {
            return (message != null) ? (s + ": " + this.errorCode + ": " + message) : s;
        }
        return (message != null) ? (s + ": " + message) : s;
    }
}
