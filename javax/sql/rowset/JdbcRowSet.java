package javax.sql.rowset;

import java.sql.Savepoint;
import java.sql.SQLException;
import javax.sql.RowSet;

public interface JdbcRowSet extends RowSet, Joinable
{
    boolean getShowDeleted() throws SQLException;
    
    void setShowDeleted(final boolean p0) throws SQLException;
    
    RowSetWarning getRowSetWarnings() throws SQLException;
    
    void commit() throws SQLException;
    
    boolean getAutoCommit() throws SQLException;
    
    void setAutoCommit(final boolean p0) throws SQLException;
    
    void rollback() throws SQLException;
    
    void rollback(final Savepoint p0) throws SQLException;
}
