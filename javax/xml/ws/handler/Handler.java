package javax.xml.ws.handler;

public interface Handler<C extends MessageContext>
{
    boolean handleMessage(final C p0);
    
    boolean handleFault(final C p0);
    
    void close(final MessageContext p0);
}
