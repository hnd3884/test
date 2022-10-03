package javax.xml.ws;

import org.w3c.dom.Element;
import java.util.Map;
import java.util.concurrent.Executor;
import javax.xml.transform.Source;
import java.util.List;
import javax.xml.ws.spi.http.HttpContext;
import javax.xml.ws.spi.Provider;

public abstract class Endpoint
{
    public static final String WSDL_SERVICE = "javax.xml.ws.wsdl.service";
    public static final String WSDL_PORT = "javax.xml.ws.wsdl.port";
    
    public static Endpoint create(final Object implementor) {
        return create(null, implementor);
    }
    
    public static Endpoint create(final Object implementor, final WebServiceFeature... features) {
        return create(null, implementor, features);
    }
    
    public static Endpoint create(final String bindingId, final Object implementor) {
        return Provider.provider().createEndpoint(bindingId, implementor);
    }
    
    public static Endpoint create(final String bindingId, final Object implementor, final WebServiceFeature... features) {
        return Provider.provider().createEndpoint(bindingId, implementor, features);
    }
    
    public abstract Binding getBinding();
    
    public abstract Object getImplementor();
    
    public abstract void publish(final String p0);
    
    public static Endpoint publish(final String address, final Object implementor) {
        return Provider.provider().createAndPublishEndpoint(address, implementor);
    }
    
    public static Endpoint publish(final String address, final Object implementor, final WebServiceFeature... features) {
        return Provider.provider().createAndPublishEndpoint(address, implementor, features);
    }
    
    public abstract void publish(final Object p0);
    
    public void publish(final HttpContext serverContext) {
        throw new UnsupportedOperationException("JAX-WS 2.2 implementation must override this default behaviour.");
    }
    
    public abstract void stop();
    
    public abstract boolean isPublished();
    
    public abstract List<Source> getMetadata();
    
    public abstract void setMetadata(final List<Source> p0);
    
    public abstract Executor getExecutor();
    
    public abstract void setExecutor(final Executor p0);
    
    public abstract Map<String, Object> getProperties();
    
    public abstract void setProperties(final Map<String, Object> p0);
    
    public abstract EndpointReference getEndpointReference(final Element... p0);
    
    public abstract <T extends EndpointReference> T getEndpointReference(final Class<T> p0, final Element... p1);
    
    public void setEndpointContext(final EndpointContext ctxt) {
        throw new UnsupportedOperationException("JAX-WS 2.2 implementation must override this default behaviour.");
    }
}
