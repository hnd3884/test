package javax.sql.rowset;

import java.sql.SQLException;
import javax.sql.RowSet;

public interface Predicate
{
    boolean evaluate(final RowSet p0);
    
    boolean evaluate(final Object p0, final int p1) throws SQLException;
    
    boolean evaluate(final Object p0, final String p1) throws SQLException;
}
