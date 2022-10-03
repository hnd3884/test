package com.sun.xml.internal.ws.api;

import java.security.AccessController;
import java.lang.reflect.Field;
import java.security.PrivilegedAction;
import com.sun.xml.internal.ws.client.WSServiceDelegate;
import java.net.URL;
import com.sun.istack.internal.Nullable;
import java.util.Iterator;
import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.server.Container;
import javax.xml.bind.JAXBContext;
import javax.xml.ws.Dispatch;
import javax.xml.ws.Service;
import javax.xml.namespace.QName;
import javax.xml.ws.WebServiceFeature;
import com.sun.xml.internal.ws.api.addressing.WSEndpointReference;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.Set;
import javax.xml.ws.spi.ServiceDelegate;

public abstract class WSService extends ServiceDelegate implements ComponentRegistry
{
    private final Set<Component> components;
    protected static final ThreadLocal<InitParams> INIT_PARAMS;
    protected static final InitParams EMPTY_PARAMS;
    
    protected WSService() {
        this.components = new CopyOnWriteArraySet<Component>();
    }
    
    public abstract <T> T getPort(final WSEndpointReference p0, final Class<T> p1, final WebServiceFeature... p2);
    
    public abstract <T> Dispatch<T> createDispatch(final QName p0, final WSEndpointReference p1, final Class<T> p2, final Service.Mode p3, final WebServiceFeature... p4);
    
    public abstract Dispatch<Object> createDispatch(final QName p0, final WSEndpointReference p1, final JAXBContext p2, final Service.Mode p3, final WebServiceFeature... p4);
    
    @NotNull
    public abstract Container getContainer();
    
    @Nullable
    @Override
    public <S> S getSPI(@NotNull final Class<S> spiType) {
        for (final Component c : this.components) {
            final S s = c.getSPI(spiType);
            if (s != null) {
                return s;
            }
        }
        return this.getContainer().getSPI(spiType);
    }
    
    @NotNull
    @Override
    public Set<Component> getComponents() {
        return this.components;
    }
    
    public static WSService create(final URL wsdlDocumentLocation, final QName serviceName) {
        return new WSServiceDelegate(wsdlDocumentLocation, serviceName, Service.class, new WebServiceFeature[0]);
    }
    
    public static WSService create(final QName serviceName) {
        return create(null, serviceName);
    }
    
    public static WSService create() {
        return create(null, new QName(WSService.class.getName(), "dummy"));
    }
    
    public static Service create(final URL wsdlDocumentLocation, final QName serviceName, final InitParams properties) {
        if (WSService.INIT_PARAMS.get() != null) {
            throw new IllegalStateException("someone left non-null InitParams");
        }
        WSService.INIT_PARAMS.set(properties);
        try {
            final Service svc = Service.create(wsdlDocumentLocation, serviceName);
            if (WSService.INIT_PARAMS.get() != null) {
                throw new IllegalStateException("Service " + svc + " didn't recognize InitParams");
            }
            return svc;
        }
        finally {
            WSService.INIT_PARAMS.set(null);
        }
    }
    
    public static WSService unwrap(final Service svc) {
        return AccessController.doPrivileged((PrivilegedAction<WSService>)new PrivilegedAction<WSService>() {
            @Override
            public WSService run() {
                try {
                    final Field f = svc.getClass().getField("delegate");
                    f.setAccessible(true);
                    final Object delegate = f.get(svc);
                    if (!(delegate instanceof WSService)) {
                        throw new IllegalArgumentException();
                    }
                    return (WSService)delegate;
                }
                catch (final NoSuchFieldException e) {
                    final AssertionError x = new AssertionError((Object)"Unexpected service API implementation");
                    x.initCause(e);
                    throw x;
                }
                catch (final IllegalAccessException e2) {
                    final IllegalAccessError x2 = new IllegalAccessError(e2.getMessage());
                    x2.initCause(e2);
                    throw x2;
                }
            }
        });
    }
    
    static {
        INIT_PARAMS = new ThreadLocal<InitParams>();
        EMPTY_PARAMS = new InitParams();
    }
    
    public static final class InitParams
    {
        private Container container;
        
        public void setContainer(final Container c) {
            this.container = c;
        }
        
        public Container getContainer() {
            return this.container;
        }
    }
}
