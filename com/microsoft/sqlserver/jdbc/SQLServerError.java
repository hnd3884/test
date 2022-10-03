package com.microsoft.sqlserver.jdbc;

import java.io.Serializable;

public final class SQLServerError extends StreamPacket implements Serializable
{
    private static final long serialVersionUID = -7304033613218700719L;
    private String errorMessage;
    private int errorNumber;
    private int errorState;
    private int errorSeverity;
    private String serverName;
    private String procName;
    private long lineNumber;
    static final /* synthetic */ boolean $assertionsDisabled;
    
    public String getErrorMessage() {
        return this.errorMessage;
    }
    
    public int getErrorNumber() {
        return this.errorNumber;
    }
    
    public int getErrorState() {
        return this.errorState;
    }
    
    public int getErrorSeverity() {
        return this.errorSeverity;
    }
    
    public String getServerName() {
        return this.serverName;
    }
    
    public String getProcedureName() {
        return this.procName;
    }
    
    public long getLineNumber() {
        return this.lineNumber;
    }
    
    SQLServerError() {
        super(170);
        this.errorMessage = "";
    }
    
    @Override
    void setFromTDS(final TDSReader tdsReader) throws SQLServerException {
        if (170 != tdsReader.readUnsignedByte() && !SQLServerError.$assertionsDisabled) {
            throw new AssertionError();
        }
        this.setContentsFromTDS(tdsReader);
    }
    
    void setContentsFromTDS(final TDSReader tdsReader) throws SQLServerException {
        tdsReader.readUnsignedShort();
        this.errorNumber = tdsReader.readInt();
        this.errorState = tdsReader.readUnsignedByte();
        this.errorSeverity = tdsReader.readUnsignedByte();
        this.errorMessage = tdsReader.readUnicodeString(tdsReader.readUnsignedShort());
        this.serverName = tdsReader.readUnicodeString(tdsReader.readUnsignedByte());
        this.procName = tdsReader.readUnicodeString(tdsReader.readUnsignedByte());
        this.lineNumber = tdsReader.readUnsignedInt();
    }
}
