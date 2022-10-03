package javax.xml.ws.soap;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPFactory;
import java.util.Set;
import javax.xml.ws.Binding;

public interface SOAPBinding extends Binding
{
    public static final String SOAP11HTTP_BINDING = "http://schemas.xmlsoap.org/wsdl/soap/http";
    public static final String SOAP12HTTP_BINDING = "http://www.w3.org/2003/05/soap/bindings/HTTP/";
    public static final String SOAP11HTTP_MTOM_BINDING = "http://schemas.xmlsoap.org/wsdl/soap/http?mtom=true";
    public static final String SOAP12HTTP_MTOM_BINDING = "http://www.w3.org/2003/05/soap/bindings/HTTP/?mtom=true";
    
    Set<String> getRoles();
    
    void setRoles(final Set<String> p0);
    
    boolean isMTOMEnabled();
    
    void setMTOMEnabled(final boolean p0);
    
    SOAPFactory getSOAPFactory();
    
    MessageFactory getMessageFactory();
}
