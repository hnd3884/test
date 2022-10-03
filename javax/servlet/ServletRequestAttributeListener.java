package javax.servlet;

import java.util.EventListener;

public interface ServletRequestAttributeListener extends EventListener
{
    void attributeAdded(final ServletRequestAttributeEvent p0);
    
    void attributeRemoved(final ServletRequestAttributeEvent p0);
    
    void attributeReplaced(final ServletRequestAttributeEvent p0);
}
