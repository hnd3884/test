package javax.sql;

import java.util.EventObject;

public class RowSetEvent extends EventObject
{
    public RowSetEvent(final RowSet rowSet) {
        super(rowSet);
    }
}
