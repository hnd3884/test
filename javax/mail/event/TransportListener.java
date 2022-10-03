package javax.mail.event;

import java.util.EventListener;

public interface TransportListener extends EventListener
{
    void messageDelivered(final TransportEvent p0);
    
    void messageNotDelivered(final TransportEvent p0);
    
    void messagePartiallyDelivered(final TransportEvent p0);
}
