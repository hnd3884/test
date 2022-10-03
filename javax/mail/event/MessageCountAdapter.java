package javax.mail.event;

public abstract class MessageCountAdapter implements MessageCountListener
{
    @Override
    public void messagesAdded(final MessageCountEvent e) {
    }
    
    @Override
    public void messagesRemoved(final MessageCountEvent e) {
    }
}
