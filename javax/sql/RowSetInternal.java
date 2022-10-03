package javax.sql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Connection;

public interface RowSetInternal
{
    Connection getConnection() throws SQLException;
    
    ResultSet getOriginal() throws SQLException;
    
    ResultSet getOriginalRow() throws SQLException;
    
    Object[] getParams() throws SQLException;
    
    void setMetaData(final RowSetMetaData p0) throws SQLException;
}
