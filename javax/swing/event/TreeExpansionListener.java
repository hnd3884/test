package javax.swing.event;

import java.util.EventListener;

public interface TreeExpansionListener extends EventListener
{
    void treeExpanded(final TreeExpansionEvent p0);
    
    void treeCollapsed(final TreeExpansionEvent p0);
}
