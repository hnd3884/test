package javax.servlet.http;

import java.util.EventListener;

public interface HttpSessionActivationListener extends EventListener
{
    void sessionWillPassivate(final HttpSessionEvent p0);
    
    void sessionDidActivate(final HttpSessionEvent p0);
}
