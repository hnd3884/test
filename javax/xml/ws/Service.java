package javax.xml.ws;

import java.util.concurrent.Executor;
import javax.xml.ws.handler.HandlerResolver;
import java.util.Iterator;
import javax.xml.bind.JAXBContext;
import javax.xml.ws.spi.Provider;
import javax.xml.namespace.QName;
import java.net.URL;
import javax.xml.ws.spi.ServiceDelegate;

public class Service
{
    private ServiceDelegate delegate;
    
    protected Service(final URL wsdlDocumentLocation, final QName serviceName) {
        this.delegate = Provider.provider().createServiceDelegate(wsdlDocumentLocation, serviceName, this.getClass());
    }
    
    protected Service(final URL wsdlDocumentLocation, final QName serviceName, final WebServiceFeature... features) {
        this.delegate = Provider.provider().createServiceDelegate(wsdlDocumentLocation, serviceName, this.getClass(), features);
    }
    
    public <T> T getPort(final QName portName, final Class<T> serviceEndpointInterface) {
        return this.delegate.getPort(portName, serviceEndpointInterface);
    }
    
    public <T> T getPort(final QName portName, final Class<T> serviceEndpointInterface, final WebServiceFeature... features) {
        return this.delegate.getPort(portName, serviceEndpointInterface, features);
    }
    
    public <T> T getPort(final Class<T> serviceEndpointInterface) {
        return this.delegate.getPort(serviceEndpointInterface);
    }
    
    public <T> T getPort(final Class<T> serviceEndpointInterface, final WebServiceFeature... features) {
        return this.delegate.getPort(serviceEndpointInterface, features);
    }
    
    public <T> T getPort(final EndpointReference endpointReference, final Class<T> serviceEndpointInterface, final WebServiceFeature... features) {
        return this.delegate.getPort(endpointReference, serviceEndpointInterface, features);
    }
    
    public void addPort(final QName portName, final String bindingId, final String endpointAddress) {
        this.delegate.addPort(portName, bindingId, endpointAddress);
    }
    
    public <T> Dispatch<T> createDispatch(final QName portName, final Class<T> type, final Mode mode) {
        return this.delegate.createDispatch(portName, type, mode);
    }
    
    public <T> Dispatch<T> createDispatch(final QName portName, final Class<T> type, final Mode mode, final WebServiceFeature... features) {
        return this.delegate.createDispatch(portName, type, mode, features);
    }
    
    public <T> Dispatch<T> createDispatch(final EndpointReference endpointReference, final Class<T> type, final Mode mode, final WebServiceFeature... features) {
        return this.delegate.createDispatch(endpointReference, type, mode, features);
    }
    
    public Dispatch<Object> createDispatch(final QName portName, final JAXBContext context, final Mode mode) {
        return this.delegate.createDispatch(portName, context, mode);
    }
    
    public Dispatch<Object> createDispatch(final QName portName, final JAXBContext context, final Mode mode, final WebServiceFeature... features) {
        return this.delegate.createDispatch(portName, context, mode, features);
    }
    
    public Dispatch<Object> createDispatch(final EndpointReference endpointReference, final JAXBContext context, final Mode mode, final WebServiceFeature... features) {
        return this.delegate.createDispatch(endpointReference, context, mode, features);
    }
    
    public QName getServiceName() {
        return this.delegate.getServiceName();
    }
    
    public Iterator<QName> getPorts() {
        return this.delegate.getPorts();
    }
    
    public URL getWSDLDocumentLocation() {
        return this.delegate.getWSDLDocumentLocation();
    }
    
    public HandlerResolver getHandlerResolver() {
        return this.delegate.getHandlerResolver();
    }
    
    public void setHandlerResolver(final HandlerResolver handlerResolver) {
        this.delegate.setHandlerResolver(handlerResolver);
    }
    
    public Executor getExecutor() {
        return this.delegate.getExecutor();
    }
    
    public void setExecutor(final Executor executor) {
        this.delegate.setExecutor(executor);
    }
    
    public static Service create(final URL wsdlDocumentLocation, final QName serviceName) {
        return new Service(wsdlDocumentLocation, serviceName);
    }
    
    public static Service create(final URL wsdlDocumentLocation, final QName serviceName, final WebServiceFeature... features) {
        return new Service(wsdlDocumentLocation, serviceName, features);
    }
    
    public static Service create(final QName serviceName) {
        return new Service(null, serviceName);
    }
    
    public static Service create(final QName serviceName, final WebServiceFeature... features) {
        return new Service(null, serviceName, features);
    }
    
    public enum Mode
    {
        MESSAGE, 
        PAYLOAD;
    }
}
