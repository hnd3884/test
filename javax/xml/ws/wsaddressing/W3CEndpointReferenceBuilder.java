package javax.xml.ws.wsaddressing;

import javax.xml.ws.spi.Provider;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Map;
import javax.xml.namespace.QName;
import org.w3c.dom.Element;
import java.util.List;

public final class W3CEndpointReferenceBuilder
{
    private String address;
    private List<Element> referenceParameters;
    private List<Element> metadata;
    private QName interfaceName;
    private QName serviceName;
    private QName endpointName;
    private String wsdlDocumentLocation;
    private Map<QName, String> attributes;
    private List<Element> elements;
    
    public W3CEndpointReferenceBuilder() {
        this.referenceParameters = new ArrayList<Element>();
        this.metadata = new ArrayList<Element>();
        this.attributes = new HashMap<QName, String>();
        this.elements = new ArrayList<Element>();
    }
    
    public W3CEndpointReferenceBuilder address(final String address) {
        this.address = address;
        return this;
    }
    
    public W3CEndpointReferenceBuilder interfaceName(final QName interfaceName) {
        this.interfaceName = interfaceName;
        return this;
    }
    
    public W3CEndpointReferenceBuilder serviceName(final QName serviceName) {
        this.serviceName = serviceName;
        return this;
    }
    
    public W3CEndpointReferenceBuilder endpointName(final QName endpointName) {
        if (this.serviceName == null) {
            throw new IllegalStateException("The W3CEndpointReferenceBuilder's serviceName must be set before setting the endpointName: " + endpointName);
        }
        this.endpointName = endpointName;
        return this;
    }
    
    public W3CEndpointReferenceBuilder wsdlDocumentLocation(final String wsdlDocumentLocation) {
        this.wsdlDocumentLocation = wsdlDocumentLocation;
        return this;
    }
    
    public W3CEndpointReferenceBuilder referenceParameter(final Element referenceParameter) {
        if (referenceParameter == null) {
            throw new IllegalArgumentException("The referenceParameter cannot be null.");
        }
        this.referenceParameters.add(referenceParameter);
        return this;
    }
    
    public W3CEndpointReferenceBuilder metadata(final Element metadataElement) {
        if (metadataElement == null) {
            throw new IllegalArgumentException("The metadataElement cannot be null.");
        }
        this.metadata.add(metadataElement);
        return this;
    }
    
    public W3CEndpointReferenceBuilder element(final Element element) {
        if (element == null) {
            throw new IllegalArgumentException("The extension element cannot be null.");
        }
        this.elements.add(element);
        return this;
    }
    
    public W3CEndpointReferenceBuilder attribute(final QName name, final String value) {
        if (name == null || value == null) {
            throw new IllegalArgumentException("The extension attribute name or value cannot be null.");
        }
        this.attributes.put(name, value);
        return this;
    }
    
    public W3CEndpointReference build() {
        if (this.elements.isEmpty() && this.attributes.isEmpty() && this.interfaceName == null) {
            return Provider.provider().createW3CEndpointReference(this.address, this.serviceName, this.endpointName, this.metadata, this.wsdlDocumentLocation, this.referenceParameters);
        }
        return Provider.provider().createW3CEndpointReference(this.address, this.interfaceName, this.serviceName, this.endpointName, this.metadata, this.wsdlDocumentLocation, this.referenceParameters, this.elements, this.attributes);
    }
}
