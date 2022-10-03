package javax.swing.event;

import java.util.EventListener;

public interface AncestorListener extends EventListener
{
    void ancestorAdded(final AncestorEvent p0);
    
    void ancestorRemoved(final AncestorEvent p0);
    
    void ancestorMoved(final AncestorEvent p0);
}
