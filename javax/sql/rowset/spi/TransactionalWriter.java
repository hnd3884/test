package javax.sql.rowset.spi;

import java.sql.Savepoint;
import java.sql.SQLException;
import javax.sql.RowSetWriter;

public interface TransactionalWriter extends RowSetWriter
{
    void commit() throws SQLException;
    
    void rollback() throws SQLException;
    
    void rollback(final Savepoint p0) throws SQLException;
}
