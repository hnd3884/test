package com.microsoft.sqlserver.jdbc;

import java.sql.SQLException;

public interface ISQLServerConnection43 extends ISQLServerConnection
{
    void beginRequest() throws SQLException;
    
    void endRequest() throws SQLException;
}
