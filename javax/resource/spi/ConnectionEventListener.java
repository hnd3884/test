package javax.resource.spi;

import java.util.EventListener;

public interface ConnectionEventListener extends EventListener
{
    void connectionClosed(final ConnectionEvent p0);
    
    void localTransactionStarted(final ConnectionEvent p0);
    
    void localTransactionCommitted(final ConnectionEvent p0);
    
    void localTransactionRolledback(final ConnectionEvent p0);
    
    void connectionErrorOccurred(final ConnectionEvent p0);
}
