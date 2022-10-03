package javax.mail.event;

public abstract class ConnectionAdapter implements ConnectionListener
{
    @Override
    public void opened(final ConnectionEvent e) {
    }
    
    @Override
    public void disconnected(final ConnectionEvent e) {
    }
    
    @Override
    public void closed(final ConnectionEvent e) {
    }
}
