package com.microsoft.sqlserver.jdbc;

import java.sql.ShardingKey;
import java.sql.SQLException;

public class SQLServerConnection43 extends SQLServerConnection implements ISQLServerConnection43
{
    private static final long serialVersionUID = -6904163521498951547L;
    
    SQLServerConnection43(final String parentInfo) throws SQLServerException {
        super(parentInfo);
    }
    
    @Override
    public void beginRequest() throws SQLException {
        this.beginRequestInternal();
    }
    
    @Override
    public void endRequest() throws SQLException {
        this.endRequestInternal();
    }
    
    public void setShardingKey(final ShardingKey shardingKey) throws SQLException {
        SQLServerException.throwFeatureNotSupportedException();
    }
    
    public void setShardingKey(final ShardingKey shardingKey, final ShardingKey superShardingKey) throws SQLException {
        SQLServerException.throwFeatureNotSupportedException();
    }
    
    public boolean setShardingKeyIfValid(final ShardingKey shardingKey, final int timeout) throws SQLException {
        SQLServerException.throwFeatureNotSupportedException();
        return false;
    }
    
    public boolean setShardingKeyIfValid(final ShardingKey shardingKey, final ShardingKey superShardingKey, final int timeout) throws SQLException {
        SQLServerException.throwFeatureNotSupportedException();
        return false;
    }
}
