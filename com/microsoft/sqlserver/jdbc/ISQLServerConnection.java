package com.microsoft.sqlserver.jdbc;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.UUID;
import java.sql.Connection;

public interface ISQLServerConnection extends Connection
{
    public static final int TRANSACTION_SNAPSHOT = 4096;
    
    UUID getClientConnectionId() throws SQLServerException;
    
    Statement createStatement(final int p0, final int p1, final int p2, final SQLServerStatementColumnEncryptionSetting p3) throws SQLServerException;
    
    PreparedStatement prepareStatement(final String p0, final int p1, final SQLServerStatementColumnEncryptionSetting p2) throws SQLServerException;
    
    PreparedStatement prepareStatement(final String p0, final int[] p1, final SQLServerStatementColumnEncryptionSetting p2) throws SQLServerException;
    
    PreparedStatement prepareStatement(final String p0, final String[] p1, final SQLServerStatementColumnEncryptionSetting p2) throws SQLServerException;
    
    PreparedStatement prepareStatement(final String p0, final int p1, final int p2, final int p3, final SQLServerStatementColumnEncryptionSetting p4) throws SQLServerException;
    
    CallableStatement prepareCall(final String p0, final int p1, final int p2, final int p3, final SQLServerStatementColumnEncryptionSetting p4) throws SQLServerException;
    
    void setSendTimeAsDatetime(final boolean p0) throws SQLServerException;
    
    boolean getSendTimeAsDatetime() throws SQLServerException;
    
    int getDiscardedServerPreparedStatementCount();
    
    void closeUnreferencedPreparedStatementHandles();
    
    boolean getEnablePrepareOnFirstPreparedStatementCall();
    
    void setEnablePrepareOnFirstPreparedStatementCall(final boolean p0);
    
    int getServerPreparedStatementDiscardThreshold();
    
    void setServerPreparedStatementDiscardThreshold(final int p0);
    
    void setStatementPoolingCacheSize(final int p0);
    
    int getStatementPoolingCacheSize();
    
    boolean isStatementPoolingEnabled();
    
    int getStatementHandleCacheEntryCount();
    
    void setDisableStatementPooling(final boolean p0);
    
    boolean getDisableStatementPooling();
    
    boolean getUseFmtOnly();
    
    void setUseFmtOnly(final boolean p0);
}
