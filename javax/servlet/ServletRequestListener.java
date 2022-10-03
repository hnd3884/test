package javax.servlet;

import java.util.EventListener;

public interface ServletRequestListener extends EventListener
{
    void requestDestroyed(final ServletRequestEvent p0);
    
    void requestInitialized(final ServletRequestEvent p0);
}
