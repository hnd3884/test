package javax.xml.ws.spi;

import java.util.concurrent.Executor;
import javax.xml.ws.handler.HandlerResolver;
import java.net.URL;
import java.util.Iterator;
import javax.xml.bind.JAXBContext;
import javax.xml.ws.Dispatch;
import javax.xml.ws.Service;
import javax.xml.ws.EndpointReference;
import javax.xml.ws.WebServiceFeature;
import javax.xml.namespace.QName;

public abstract class ServiceDelegate
{
    protected ServiceDelegate() {
    }
    
    public abstract <T> T getPort(final QName p0, final Class<T> p1);
    
    public abstract <T> T getPort(final QName p0, final Class<T> p1, final WebServiceFeature... p2);
    
    public abstract <T> T getPort(final EndpointReference p0, final Class<T> p1, final WebServiceFeature... p2);
    
    public abstract <T> T getPort(final Class<T> p0);
    
    public abstract <T> T getPort(final Class<T> p0, final WebServiceFeature... p1);
    
    public abstract void addPort(final QName p0, final String p1, final String p2);
    
    public abstract <T> Dispatch<T> createDispatch(final QName p0, final Class<T> p1, final Service.Mode p2);
    
    public abstract <T> Dispatch<T> createDispatch(final QName p0, final Class<T> p1, final Service.Mode p2, final WebServiceFeature... p3);
    
    public abstract <T> Dispatch<T> createDispatch(final EndpointReference p0, final Class<T> p1, final Service.Mode p2, final WebServiceFeature... p3);
    
    public abstract Dispatch<Object> createDispatch(final QName p0, final JAXBContext p1, final Service.Mode p2);
    
    public abstract Dispatch<Object> createDispatch(final QName p0, final JAXBContext p1, final Service.Mode p2, final WebServiceFeature... p3);
    
    public abstract Dispatch<Object> createDispatch(final EndpointReference p0, final JAXBContext p1, final Service.Mode p2, final WebServiceFeature... p3);
    
    public abstract QName getServiceName();
    
    public abstract Iterator<QName> getPorts();
    
    public abstract URL getWSDLDocumentLocation();
    
    public abstract HandlerResolver getHandlerResolver();
    
    public abstract void setHandlerResolver(final HandlerResolver p0);
    
    public abstract Executor getExecutor();
    
    public abstract void setExecutor(final Executor p0);
}
