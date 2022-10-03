package javax.servlet;

import java.util.EventListener;

public interface ServletContextListener extends EventListener
{
    void contextInitialized(final ServletContextEvent p0);
    
    void contextDestroyed(final ServletContextEvent p0);
}
