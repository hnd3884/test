package com.sun.xml.internal.ws.spi;

import java.security.AccessController;
import com.sun.xml.internal.ws.developer.MemberSubmissionEndpointReference;
import java.security.PrivilegedAction;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLService;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLModel;
import org.xml.sax.EntityResolver;
import com.sun.xml.internal.ws.api.server.WSEndpoint;
import java.util.Iterator;
import com.sun.xml.internal.ws.api.server.Container;
import com.sun.xml.internal.ws.api.addressing.AddressingVersion;
import com.sun.xml.internal.ws.wsdl.parser.RuntimeWSDLParser;
import com.sun.xml.internal.ws.util.ServiceFinder;
import com.sun.xml.internal.ws.api.wsdl.parser.WSDLParserExtension;
import javax.xml.transform.stream.StreamSource;
import com.sun.xml.internal.ws.util.xml.XmlUtil;
import com.sun.xml.internal.ws.api.server.BoundEndpoint;
import com.sun.xml.internal.ws.api.server.Module;
import com.sun.xml.internal.ws.api.server.ContainerResolver;
import java.util.Map;
import javax.xml.ws.wsaddressing.W3CEndpointReference;
import org.w3c.dom.Element;
import java.util.List;
import com.sun.xml.internal.ws.api.WSService;
import com.sun.xml.internal.ws.api.addressing.WSEndpointReference;
import com.sun.xml.internal.ws.resources.ProviderApiMessages;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.JAXBException;
import javax.xml.ws.EndpointReference;
import javax.xml.ws.spi.Invoker;
import javax.xml.transform.Source;
import javax.xml.ws.WebServiceException;
import com.sun.xml.internal.ws.api.ServiceSharedFeatureMarker;
import javax.xml.ws.Service;
import com.sun.xml.internal.ws.client.WSServiceDelegate;
import javax.xml.ws.spi.ServiceDelegate;
import javax.xml.namespace.QName;
import java.net.URL;
import com.sun.xml.internal.ws.transport.http.server.EndpointImpl;
import javax.xml.ws.WebServiceFeature;
import com.sun.xml.internal.ws.api.BindingID;
import javax.xml.ws.Endpoint;
import javax.xml.bind.JAXBContext;
import javax.xml.ws.spi.Provider;

public class ProviderImpl extends Provider
{
    private static final ContextClassloaderLocal<JAXBContext> eprjc;
    public static final ProviderImpl INSTANCE;
    
    @Override
    public Endpoint createEndpoint(final String bindingId, final Object implementor) {
        return new EndpointImpl((bindingId != null) ? BindingID.parse(bindingId) : BindingID.parse(implementor.getClass()), implementor, new WebServiceFeature[0]);
    }
    
    @Override
    public ServiceDelegate createServiceDelegate(final URL wsdlDocumentLocation, final QName serviceName, final Class serviceClass) {
        return new WSServiceDelegate(wsdlDocumentLocation, serviceName, serviceClass, new WebServiceFeature[0]);
    }
    
    @Override
    public ServiceDelegate createServiceDelegate(final URL wsdlDocumentLocation, final QName serviceName, final Class serviceClass, final WebServiceFeature... features) {
        for (final WebServiceFeature feature : features) {
            if (!(feature instanceof ServiceSharedFeatureMarker)) {
                throw new WebServiceException("Doesn't support any Service specific features");
            }
        }
        return new WSServiceDelegate(wsdlDocumentLocation, serviceName, serviceClass, features);
    }
    
    public ServiceDelegate createServiceDelegate(final Source wsdlSource, final QName serviceName, final Class serviceClass) {
        return new WSServiceDelegate(wsdlSource, serviceName, serviceClass, new WebServiceFeature[0]);
    }
    
    @Override
    public Endpoint createAndPublishEndpoint(final String address, final Object implementor) {
        final Endpoint endpoint = new EndpointImpl(BindingID.parse(implementor.getClass()), implementor, new WebServiceFeature[0]);
        endpoint.publish(address);
        return endpoint;
    }
    
    @Override
    public Endpoint createEndpoint(final String bindingId, final Object implementor, final WebServiceFeature... features) {
        return new EndpointImpl((bindingId != null) ? BindingID.parse(bindingId) : BindingID.parse(implementor.getClass()), implementor, features);
    }
    
    @Override
    public Endpoint createAndPublishEndpoint(final String address, final Object implementor, final WebServiceFeature... features) {
        final Endpoint endpoint = new EndpointImpl(BindingID.parse(implementor.getClass()), implementor, features);
        endpoint.publish(address);
        return endpoint;
    }
    
    @Override
    public Endpoint createEndpoint(final String bindingId, final Class implementorClass, final Invoker invoker, final WebServiceFeature... features) {
        return new EndpointImpl((bindingId != null) ? BindingID.parse(bindingId) : BindingID.parse(implementorClass), implementorClass, invoker, features);
    }
    
    @Override
    public EndpointReference readEndpointReference(final Source eprInfoset) {
        try {
            final Unmarshaller unmarshaller = ProviderImpl.eprjc.get().createUnmarshaller();
            return (EndpointReference)unmarshaller.unmarshal(eprInfoset);
        }
        catch (final JAXBException e) {
            throw new WebServiceException("Error creating Marshaller or marshalling.", e);
        }
    }
    
    @Override
    public <T> T getPort(final EndpointReference endpointReference, final Class<T> clazz, final WebServiceFeature... webServiceFeatures) {
        if (endpointReference == null) {
            throw new WebServiceException(ProviderApiMessages.NULL_EPR());
        }
        final WSEndpointReference wsepr = new WSEndpointReference(endpointReference);
        final WSEndpointReference.Metadata metadata = wsepr.getMetaData();
        if (metadata.getWsdlSource() != null) {
            final WSService service = (WSService)this.createServiceDelegate(metadata.getWsdlSource(), metadata.getServiceName(), Service.class);
            return service.getPort(wsepr, clazz, webServiceFeatures);
        }
        throw new WebServiceException("WSDL metadata is missing in EPR");
    }
    
    @Override
    public W3CEndpointReference createW3CEndpointReference(final String address, final QName serviceName, final QName portName, final List<Element> metadata, final String wsdlDocumentLocation, final List<Element> referenceParameters) {
        return this.createW3CEndpointReference(address, null, serviceName, portName, metadata, wsdlDocumentLocation, referenceParameters, null, null);
    }
    
    @Override
    public W3CEndpointReference createW3CEndpointReference(String address, final QName interfaceName, final QName serviceName, final QName portName, List<Element> metadata, final String wsdlDocumentLocation, final List<Element> referenceParameters, final List<Element> elements, final Map<QName, String> attributes) {
        final Container container = ContainerResolver.getInstance().getContainer();
        if (address == null) {
            if (serviceName == null || portName == null) {
                throw new IllegalStateException(ProviderApiMessages.NULL_ADDRESS_SERVICE_ENDPOINT());
            }
            final Module module = container.getSPI(Module.class);
            if (module != null) {
                final List<BoundEndpoint> beList = module.getBoundEndpoints();
                for (final BoundEndpoint be : beList) {
                    final WSEndpoint wse = be.getEndpoint();
                    if (wse.getServiceName().equals(serviceName) && wse.getPortName().equals(portName)) {
                        try {
                            address = be.getAddress().toString();
                        }
                        catch (final WebServiceException ex) {}
                        break;
                    }
                }
            }
            if (address == null) {
                throw new IllegalStateException(ProviderApiMessages.NULL_ADDRESS());
            }
        }
        if (serviceName == null && portName != null) {
            throw new IllegalStateException(ProviderApiMessages.NULL_SERVICE());
        }
        String wsdlTargetNamespace = null;
        if (wsdlDocumentLocation != null) {
            try {
                final EntityResolver er = XmlUtil.createDefaultCatalogResolver();
                final URL wsdlLoc = new URL(wsdlDocumentLocation);
                final WSDLModel wsdlDoc = RuntimeWSDLParser.parse(wsdlLoc, new StreamSource(wsdlLoc.toExternalForm()), er, true, container, (WSDLParserExtension[])ServiceFinder.find(WSDLParserExtension.class).toArray());
                if (serviceName != null) {
                    final WSDLService wsdlService = wsdlDoc.getService(serviceName);
                    if (wsdlService == null) {
                        throw new IllegalStateException(ProviderApiMessages.NOTFOUND_SERVICE_IN_WSDL(serviceName, wsdlDocumentLocation));
                    }
                    if (portName != null) {
                        final WSDLPort wsdlPort = wsdlService.get(portName);
                        if (wsdlPort == null) {
                            throw new IllegalStateException(ProviderApiMessages.NOTFOUND_PORT_IN_WSDL(portName, serviceName, wsdlDocumentLocation));
                        }
                    }
                    wsdlTargetNamespace = serviceName.getNamespaceURI();
                }
                else {
                    final QName firstService = wsdlDoc.getFirstServiceName();
                    wsdlTargetNamespace = firstService.getNamespaceURI();
                }
            }
            catch (final Exception e) {
                throw new IllegalStateException(ProviderApiMessages.ERROR_WSDL(wsdlDocumentLocation), e);
            }
        }
        if (metadata != null && metadata.size() == 0) {
            metadata = null;
        }
        return new WSEndpointReference(AddressingVersion.fromSpecClass(W3CEndpointReference.class), address, serviceName, portName, interfaceName, metadata, wsdlDocumentLocation, wsdlTargetNamespace, referenceParameters, elements, attributes).toSpec(W3CEndpointReference.class);
    }
    
    private static JAXBContext getEPRJaxbContext() {
        return AccessController.doPrivileged((PrivilegedAction<JAXBContext>)new PrivilegedAction<JAXBContext>() {
            @Override
            public JAXBContext run() {
                try {
                    return JAXBContext.newInstance(MemberSubmissionEndpointReference.class, W3CEndpointReference.class);
                }
                catch (final JAXBException e) {
                    throw new WebServiceException("Error creating JAXBContext for W3CEndpointReference. ", e);
                }
            }
        });
    }
    
    static {
        eprjc = new ContextClassloaderLocal<JAXBContext>() {
            @Override
            protected JAXBContext initialValue() throws Exception {
                return getEPRJaxbContext();
            }
        };
        INSTANCE = new ProviderImpl();
    }
}
