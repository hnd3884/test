package javax.sql.rowset;

import java.sql.SQLException;

public interface FilteredRowSet extends WebRowSet
{
    void setFilter(final Predicate p0) throws SQLException;
    
    Predicate getFilter();
}
