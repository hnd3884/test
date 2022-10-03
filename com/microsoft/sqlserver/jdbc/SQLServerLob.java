package com.microsoft.sqlserver.jdbc;

import java.sql.SQLException;
import java.io.Serializable;

abstract class SQLServerLob implements Serializable
{
    private static final long serialVersionUID = -6444654924359581662L;
    
    abstract void fillFromStream() throws SQLException;
}
