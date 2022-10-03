package javax.mail.event;

import java.util.EventListener;

public interface ConnectionListener extends EventListener
{
    void opened(final ConnectionEvent p0);
    
    void disconnected(final ConnectionEvent p0);
    
    void closed(final ConnectionEvent p0);
}
