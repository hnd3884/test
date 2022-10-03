package javax.xml.ws;

import javax.xml.ws.handler.Handler;
import java.util.List;

public interface Binding
{
    List<Handler> getHandlerChain();
    
    void setHandlerChain(final List<Handler> p0);
    
    String getBindingID();
}
