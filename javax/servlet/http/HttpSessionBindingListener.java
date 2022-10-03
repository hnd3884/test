package javax.servlet.http;

import java.util.EventListener;

public interface HttpSessionBindingListener extends EventListener
{
    void valueBound(final HttpSessionBindingEvent p0);
    
    void valueUnbound(final HttpSessionBindingEvent p0);
}
