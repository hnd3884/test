package javax.mail.event;

public abstract class TransportAdapter implements TransportListener
{
    @Override
    public void messageDelivered(final TransportEvent e) {
    }
    
    @Override
    public void messageNotDelivered(final TransportEvent e) {
    }
    
    @Override
    public void messagePartiallyDelivered(final TransportEvent e) {
    }
}
