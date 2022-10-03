package java.awt.event;

import java.util.EventListener;

public interface ContainerListener extends EventListener
{
    void componentAdded(final ContainerEvent p0);
    
    void componentRemoved(final ContainerEvent p0);
}
