package javax.swing.event;

import java.util.EventListener;

public interface TableModelListener extends EventListener
{
    void tableChanged(final TableModelEvent p0);
}
