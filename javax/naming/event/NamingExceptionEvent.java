package javax.naming.event;

import javax.naming.NamingException;
import java.util.EventObject;

public class NamingExceptionEvent extends EventObject
{
    private NamingException exception;
    private static final long serialVersionUID = -4877678086134736336L;
    
    public NamingExceptionEvent(final EventContext eventContext, final NamingException exception) {
        super(eventContext);
        this.exception = exception;
    }
    
    public NamingException getException() {
        return this.exception;
    }
    
    public EventContext getEventContext() {
        return (EventContext)this.getSource();
    }
    
    public void dispatch(final NamingListener namingListener) {
        namingListener.namingExceptionThrown(this);
    }
}
