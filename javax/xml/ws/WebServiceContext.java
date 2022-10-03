package javax.xml.ws;

import org.w3c.dom.Element;
import java.security.Principal;
import javax.xml.ws.handler.MessageContext;

public interface WebServiceContext
{
    MessageContext getMessageContext();
    
    Principal getUserPrincipal();
    
    boolean isUserInRole(final String p0);
    
    EndpointReference getEndpointReference(final Element... p0);
    
     <T extends EndpointReference> T getEndpointReference(final Class<T> p0, final Element... p1);
}
