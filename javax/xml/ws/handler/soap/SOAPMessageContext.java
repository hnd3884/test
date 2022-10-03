package javax.xml.ws.handler.soap;

import java.util.Set;
import javax.xml.bind.JAXBContext;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.handler.MessageContext;

public interface SOAPMessageContext extends MessageContext
{
    SOAPMessage getMessage();
    
    void setMessage(final SOAPMessage p0);
    
    Object[] getHeaders(final QName p0, final JAXBContext p1, final boolean p2);
    
    Set<String> getRoles();
}
