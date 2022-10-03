package com.me.ems.summaryserver.common.sync;

public class SyncException extends Exception
{
    private int errorCode;
    private Long probeID;
    private long moduleID;
    private String tableName;
    private String errorMessage;
    
    public SyncException(final int errorCode, final String errorMessage) {
        this.probeID = -1L;
        this.moduleID = -1L;
        this.tableName = null;
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }
    
    public SyncException(final int errorCode, final Throwable cause) {
        super(cause);
        this.probeID = -1L;
        this.moduleID = -1L;
        this.tableName = null;
        this.errorCode = errorCode;
    }
    
    public SyncException(final int errorCode, final String tableName, final String errorMessage) {
        this.probeID = -1L;
        this.moduleID = -1L;
        this.tableName = null;
        this.errorCode = errorCode;
        this.tableName = tableName;
        this.errorMessage = errorMessage;
    }
    
    public SyncException(final int errorCode, final String tableName, final Throwable cause) {
        super(cause);
        this.probeID = -1L;
        this.moduleID = -1L;
        this.tableName = null;
        this.errorCode = errorCode;
        this.tableName = tableName;
    }
    
    public SyncException(final int errorCode, final Long probeID, final long moduleID, final Throwable cause) {
        super(cause);
        this.probeID = -1L;
        this.moduleID = -1L;
        this.tableName = null;
        this.errorCode = errorCode;
        this.probeID = probeID;
        this.moduleID = moduleID;
    }
    
    public SyncException(final int errorCode, final Long probeID, final long moduleID, final String tableName, final String errorMessage) {
        this.probeID = -1L;
        this.moduleID = -1L;
        this.tableName = null;
        this.errorCode = errorCode;
        this.probeID = probeID;
        this.moduleID = moduleID;
        this.tableName = tableName;
        this.errorMessage = errorMessage;
    }
    
    public SyncException(final int errorCode, final Long probeID, final long moduleID, final String tableName, final Throwable cause) {
        super(cause);
        this.probeID = -1L;
        this.moduleID = -1L;
        this.tableName = null;
        this.errorCode = errorCode;
        this.probeID = probeID;
        this.moduleID = moduleID;
        this.tableName = tableName;
    }
    
    public int getErrorCode() {
        return this.errorCode;
    }
    
    public Long getProbeID() {
        return this.probeID;
    }
    
    public long getModuleID() {
        return this.moduleID;
    }
    
    public String getTableName() {
        return this.tableName;
    }
    
    public String getErrorMessage() {
        return this.errorMessage;
    }
    
    @Override
    public String toString() {
        String message = "Sync Error Code : " + this.errorCode;
        message = ((this.errorMessage != null) ? (message + "\tError message : " + this.errorMessage) : message);
        message = ((this.probeID != -1L) ? (message + "\tProbe ID : " + this.probeID) : message);
        message = ((this.moduleID != -1L) ? (message + "\tModule ID : " + this.moduleID) : message);
        message = ((this.tableName != null) ? (message + "\tTable Name : " + this.tableName) : message);
        return message;
    }
}
