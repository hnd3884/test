package javax.naming.event;

import java.util.EventListener;

public interface NamingListener extends EventListener
{
    void namingExceptionThrown(final NamingExceptionEvent p0);
}
