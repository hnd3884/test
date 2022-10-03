package javax.sql;

import java.util.EventListener;

public interface RowSetListener extends EventListener
{
    void cursorMoved(final RowSetEvent p0);
    
    void rowChanged(final RowSetEvent p0);
    
    void rowSetChanged(final RowSetEvent p0);
}
