package javax.xml.ws.spi;

import java.util.Map;
import javax.xml.ws.wsaddressing.W3CEndpointReference;
import org.w3c.dom.Element;
import java.util.List;
import javax.xml.ws.EndpointReference;
import javax.xml.transform.Source;
import javax.xml.ws.Endpoint;
import javax.xml.ws.WebServiceFeature;
import javax.xml.ws.Service;
import javax.xml.namespace.QName;
import java.util.Iterator;
import java.net.URL;
import javax.xml.ws.WebServiceException;
import java.lang.reflect.Method;

public abstract class Provider
{
    public static final String JAXWSPROVIDER_PROPERTY = "javax.xml.ws.spi.Provider";
    static final String DEFAULT_JAXWSPROVIDER = "com.sun.xml.internal.ws.spi.ProviderImpl";
    private static final Method loadMethod;
    private static final Method iteratorMethod;
    
    protected Provider() {
    }
    
    public static Provider provider() {
        try {
            Object provider = getProviderUsingServiceLoader();
            if (provider == null) {
                provider = FactoryFinder.find("javax.xml.ws.spi.Provider", "com.sun.xml.internal.ws.spi.ProviderImpl");
            }
            if (!(provider instanceof Provider)) {
                final Class pClass = Provider.class;
                final String classnameAsResource = pClass.getName().replace('.', '/') + ".class";
                ClassLoader loader = pClass.getClassLoader();
                if (loader == null) {
                    loader = ClassLoader.getSystemClassLoader();
                }
                final URL targetTypeURL = loader.getResource(classnameAsResource);
                throw new LinkageError("ClassCastException: attempting to cast" + provider.getClass().getClassLoader().getResource(classnameAsResource) + "to" + targetTypeURL.toString());
            }
            return (Provider)provider;
        }
        catch (final WebServiceException ex) {
            throw ex;
        }
        catch (final Exception ex2) {
            throw new WebServiceException("Unable to createEndpointReference Provider", ex2);
        }
    }
    
    private static Provider getProviderUsingServiceLoader() {
        if (Provider.loadMethod != null) {
            Object loader;
            try {
                loader = Provider.loadMethod.invoke(null, Provider.class);
            }
            catch (final Exception e) {
                throw new WebServiceException("Cannot invoke java.util.ServiceLoader#load()", e);
            }
            Iterator<Provider> it;
            try {
                it = (Iterator)Provider.iteratorMethod.invoke(loader, new Object[0]);
            }
            catch (final Exception e2) {
                throw new WebServiceException("Cannot invoke java.util.ServiceLoader#iterator()", e2);
            }
            return it.hasNext() ? it.next() : null;
        }
        return null;
    }
    
    public abstract ServiceDelegate createServiceDelegate(final URL p0, final QName p1, final Class<? extends Service> p2);
    
    public ServiceDelegate createServiceDelegate(final URL wsdlDocumentLocation, final QName serviceName, final Class<? extends Service> serviceClass, final WebServiceFeature... features) {
        throw new UnsupportedOperationException("JAX-WS 2.2 implementation must override this default behaviour.");
    }
    
    public abstract Endpoint createEndpoint(final String p0, final Object p1);
    
    public abstract Endpoint createAndPublishEndpoint(final String p0, final Object p1);
    
    public abstract EndpointReference readEndpointReference(final Source p0);
    
    public abstract <T> T getPort(final EndpointReference p0, final Class<T> p1, final WebServiceFeature... p2);
    
    public abstract W3CEndpointReference createW3CEndpointReference(final String p0, final QName p1, final QName p2, final List<Element> p3, final String p4, final List<Element> p5);
    
    public W3CEndpointReference createW3CEndpointReference(final String address, final QName interfaceName, final QName serviceName, final QName portName, final List<Element> metadata, final String wsdlDocumentLocation, final List<Element> referenceParameters, final List<Element> elements, final Map<QName, String> attributes) {
        throw new UnsupportedOperationException("JAX-WS 2.2 implementation must override this default behaviour.");
    }
    
    public Endpoint createAndPublishEndpoint(final String address, final Object implementor, final WebServiceFeature... features) {
        throw new UnsupportedOperationException("JAX-WS 2.2 implementation must override this default behaviour.");
    }
    
    public Endpoint createEndpoint(final String bindingId, final Object implementor, final WebServiceFeature... features) {
        throw new UnsupportedOperationException("JAX-WS 2.2 implementation must override this default behaviour.");
    }
    
    public Endpoint createEndpoint(final String bindingId, final Class<?> implementorClass, final Invoker invoker, final WebServiceFeature... features) {
        throw new UnsupportedOperationException("JAX-WS 2.2 implementation must override this default behaviour.");
    }
    
    static {
        Method tLoadMethod = null;
        Method tIteratorMethod = null;
        try {
            final Class<?> clazz = Class.forName("java.util.ServiceLoader");
            tLoadMethod = clazz.getMethod("load", Class.class);
            tIteratorMethod = clazz.getMethod("iterator", (Class<?>[])new Class[0]);
        }
        catch (final ClassNotFoundException ex) {}
        catch (final NoSuchMethodException ex2) {}
        loadMethod = tLoadMethod;
        iteratorMethod = tIteratorMethod;
    }
}
