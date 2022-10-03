package javax.swing.event;

import java.util.EventListener;

public interface ListDataListener extends EventListener
{
    void intervalAdded(final ListDataEvent p0);
    
    void intervalRemoved(final ListDataEvent p0);
    
    void contentsChanged(final ListDataEvent p0);
}
