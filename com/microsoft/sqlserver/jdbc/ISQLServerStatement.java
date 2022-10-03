package com.microsoft.sqlserver.jdbc;

import java.io.Serializable;
import java.sql.Statement;

public interface ISQLServerStatement extends Statement, Serializable
{
    void setResponseBuffering(final String p0) throws SQLServerException;
    
    String getResponseBuffering() throws SQLServerException;
    
    int getCancelQueryTimeout() throws SQLServerException;
    
    void setCancelQueryTimeout(final int p0) throws SQLServerException;
}
