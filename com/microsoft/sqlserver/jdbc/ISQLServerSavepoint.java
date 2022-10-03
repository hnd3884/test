package com.microsoft.sqlserver.jdbc;

import java.io.Serializable;
import java.sql.Savepoint;

public interface ISQLServerSavepoint extends Savepoint, Serializable
{
    String getSavepointName() throws SQLServerException;
    
    String getLabel();
    
    boolean isNamed();
}
