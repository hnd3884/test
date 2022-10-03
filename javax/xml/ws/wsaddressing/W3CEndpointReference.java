package javax.xml.ws.wsaddressing;

import javax.xml.bind.annotation.XmlValue;
import javax.xml.bind.Marshaller;
import javax.xml.transform.Result;
import javax.xml.bind.JAXBException;
import javax.xml.ws.WebServiceException;
import javax.xml.transform.Source;
import javax.xml.bind.annotation.XmlAnyElement;
import org.w3c.dom.Element;
import java.util.List;
import javax.xml.bind.annotation.XmlAnyAttribute;
import javax.xml.namespace.QName;
import java.util.Map;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.ws.EndpointReference;

@XmlRootElement(name = "EndpointReference", namespace = "http://www.w3.org/2005/08/addressing")
@XmlType(name = "EndpointReferenceType", namespace = "http://www.w3.org/2005/08/addressing")
public final class W3CEndpointReference extends EndpointReference
{
    private final JAXBContext w3cjc;
    protected static final String NS = "http://www.w3.org/2005/08/addressing";
    @XmlElement(name = "Address", namespace = "http://www.w3.org/2005/08/addressing")
    private Address address;
    @XmlElement(name = "ReferenceParameters", namespace = "http://www.w3.org/2005/08/addressing")
    private Elements referenceParameters;
    @XmlElement(name = "Metadata", namespace = "http://www.w3.org/2005/08/addressing")
    private Elements metadata;
    @XmlAnyAttribute
    Map<QName, String> attributes;
    @XmlAnyElement
    List<Element> elements;
    
    protected W3CEndpointReference() {
        this.w3cjc = getW3CJaxbContext();
    }
    
    public W3CEndpointReference(final Source source) {
        this.w3cjc = getW3CJaxbContext();
        try {
            final W3CEndpointReference epr = this.w3cjc.createUnmarshaller().unmarshal(source, W3CEndpointReference.class).getValue();
            this.address = epr.address;
            this.metadata = epr.metadata;
            this.referenceParameters = epr.referenceParameters;
            this.elements = epr.elements;
            this.attributes = epr.attributes;
        }
        catch (final JAXBException e) {
            throw new WebServiceException("Error unmarshalling W3CEndpointReference ", e);
        }
        catch (final ClassCastException e2) {
            throw new WebServiceException("Source did not contain W3CEndpointReference", e2);
        }
    }
    
    @Override
    public void writeTo(final Result result) {
        try {
            final Marshaller marshaller = this.w3cjc.createMarshaller();
            marshaller.marshal(this, result);
        }
        catch (final JAXBException e) {
            throw new WebServiceException("Error marshalling W3CEndpointReference. ", e);
        }
    }
    
    private static JAXBContext getW3CJaxbContext() {
        try {
            return JAXBContext.newInstance(W3CEndpointReference.class);
        }
        catch (final JAXBException e) {
            throw new WebServiceException("Error creating JAXBContext for W3CEndpointReference. ", e);
        }
    }
    
    @XmlType(name = "address", namespace = "http://www.w3.org/2005/08/addressing")
    private static class Address
    {
        @XmlValue
        String uri;
        @XmlAnyAttribute
        Map<QName, String> attributes;
        
        protected Address() {
        }
    }
    
    @XmlType(name = "elements", namespace = "http://www.w3.org/2005/08/addressing")
    private static class Elements
    {
        @XmlAnyElement
        List<Element> elements;
        @XmlAnyAttribute
        Map<QName, String> attributes;
        
        protected Elements() {
        }
    }
}
