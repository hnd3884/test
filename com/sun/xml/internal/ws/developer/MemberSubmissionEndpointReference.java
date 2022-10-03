package com.sun.xml.internal.ws.developer;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;
import java.util.Iterator;
import org.w3c.dom.Node;
import javax.xml.transform.dom.DOMSource;
import com.sun.xml.internal.ws.wsdl.parser.WSDLConstants;
import javax.xml.bind.Marshaller;
import javax.xml.transform.Result;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.JAXBException;
import javax.xml.ws.WebServiceException;
import com.sun.istack.internal.NotNull;
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
import com.sun.xml.internal.ws.addressing.v200408.MemberSubmissionAddressingConstants;
import javax.xml.ws.EndpointReference;

@XmlRootElement(name = "EndpointReference", namespace = "http://schemas.xmlsoap.org/ws/2004/08/addressing")
@XmlType(name = "EndpointReferenceType", namespace = "http://schemas.xmlsoap.org/ws/2004/08/addressing")
public final class MemberSubmissionEndpointReference extends EndpointReference implements MemberSubmissionAddressingConstants
{
    private static final ContextClassloaderLocal<JAXBContext> msjc;
    @XmlElement(name = "Address", namespace = "http://schemas.xmlsoap.org/ws/2004/08/addressing")
    public Address addr;
    @XmlElement(name = "ReferenceProperties", namespace = "http://schemas.xmlsoap.org/ws/2004/08/addressing")
    public Elements referenceProperties;
    @XmlElement(name = "ReferenceParameters", namespace = "http://schemas.xmlsoap.org/ws/2004/08/addressing")
    public Elements referenceParameters;
    @XmlElement(name = "PortType", namespace = "http://schemas.xmlsoap.org/ws/2004/08/addressing")
    public AttributedQName portTypeName;
    @XmlElement(name = "ServiceName", namespace = "http://schemas.xmlsoap.org/ws/2004/08/addressing")
    public ServiceNameType serviceName;
    @XmlAnyAttribute
    public Map<QName, String> attributes;
    @XmlAnyElement
    public List<Element> elements;
    protected static final String MSNS = "http://schemas.xmlsoap.org/ws/2004/08/addressing";
    
    public MemberSubmissionEndpointReference() {
    }
    
    public MemberSubmissionEndpointReference(@NotNull final Source source) {
        if (source == null) {
            throw new WebServiceException("Source parameter can not be null on constructor");
        }
        try {
            final Unmarshaller unmarshaller = MemberSubmissionEndpointReference.msjc.get().createUnmarshaller();
            final MemberSubmissionEndpointReference epr = unmarshaller.unmarshal(source, MemberSubmissionEndpointReference.class).getValue();
            this.addr = epr.addr;
            this.referenceProperties = epr.referenceProperties;
            this.referenceParameters = epr.referenceParameters;
            this.portTypeName = epr.portTypeName;
            this.serviceName = epr.serviceName;
            this.attributes = epr.attributes;
            this.elements = epr.elements;
        }
        catch (final JAXBException e) {
            throw new WebServiceException("Error unmarshalling MemberSubmissionEndpointReference ", e);
        }
        catch (final ClassCastException e2) {
            throw new WebServiceException("Source did not contain MemberSubmissionEndpointReference", e2);
        }
    }
    
    @Override
    public void writeTo(final Result result) {
        try {
            final Marshaller marshaller = MemberSubmissionEndpointReference.msjc.get().createMarshaller();
            marshaller.marshal(this, result);
        }
        catch (final JAXBException e) {
            throw new WebServiceException("Error marshalling W3CEndpointReference. ", e);
        }
    }
    
    public Source toWSDLSource() {
        Element wsdlElement = null;
        for (final Element elem : this.elements) {
            if (elem.getNamespaceURI().equals("http://schemas.xmlsoap.org/wsdl/") && elem.getLocalName().equals(WSDLConstants.QNAME_DEFINITIONS.getLocalPart())) {
                wsdlElement = elem;
            }
        }
        return new DOMSource(wsdlElement);
    }
    
    private static JAXBContext getMSJaxbContext() {
        try {
            return JAXBContext.newInstance(MemberSubmissionEndpointReference.class);
        }
        catch (final JAXBException e) {
            throw new WebServiceException("Error creating JAXBContext for MemberSubmissionEndpointReference. ", e);
        }
    }
    
    static {
        msjc = new ContextClassloaderLocal<JAXBContext>() {
            @Override
            protected JAXBContext initialValue() throws Exception {
                return getMSJaxbContext();
            }
        };
    }
    
    @XmlType(name = "address", namespace = "http://schemas.xmlsoap.org/ws/2004/08/addressing")
    public static class Address
    {
        @XmlValue
        public String uri;
        @XmlAnyAttribute
        public Map<QName, String> attributes;
    }
    
    @XmlType(name = "elements", namespace = "http://schemas.xmlsoap.org/ws/2004/08/addressing")
    public static class Elements
    {
        @XmlAnyElement
        public List<Element> elements;
    }
    
    public static class AttributedQName
    {
        @XmlValue
        public QName name;
        @XmlAnyAttribute
        public Map<QName, String> attributes;
    }
    
    public static class ServiceNameType extends AttributedQName
    {
        @XmlAttribute(name = "PortName")
        public String portName;
    }
}
