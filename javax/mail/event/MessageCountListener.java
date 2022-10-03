package javax.mail.event;

import java.util.EventListener;

public interface MessageCountListener extends EventListener
{
    void messagesAdded(final MessageCountEvent p0);
    
    void messagesRemoved(final MessageCountEvent p0);
}
