package java.awt.event;

import java.util.EventListener;

public interface HierarchyBoundsListener extends EventListener
{
    void ancestorMoved(final HierarchyEvent p0);
    
    void ancestorResized(final HierarchyEvent p0);
}
