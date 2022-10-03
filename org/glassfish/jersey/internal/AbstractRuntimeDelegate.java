package org.glassfish.jersey.internal;

import java.util.Iterator;
import org.glassfish.jersey.message.internal.JerseyLink;
import javax.ws.rs.core.Link;
import org.glassfish.jersey.uri.internal.JerseyUriBuilder;
import javax.ws.rs.core.UriBuilder;
import org.glassfish.jersey.message.internal.OutboundJaxrsResponse;
import org.glassfish.jersey.message.internal.OutboundMessageContext;
import javax.ws.rs.core.Response;
import org.glassfish.jersey.message.internal.VariantListBuilder;
import javax.ws.rs.core.Variant;
import java.util.Date;
import java.net.URI;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.EntityTag;
import java.util.WeakHashMap;
import java.util.Map;
import org.glassfish.jersey.spi.HeaderDelegateProvider;
import java.util.Set;
import javax.ws.rs.ext.RuntimeDelegate;

public abstract class AbstractRuntimeDelegate extends RuntimeDelegate
{
    private final Set<HeaderDelegateProvider> hps;
    private final Map<Class<?>, RuntimeDelegate.HeaderDelegate<?>> map;
    
    protected AbstractRuntimeDelegate(final Set<HeaderDelegateProvider> hps) {
        this.hps = hps;
        (this.map = new WeakHashMap<Class<?>, RuntimeDelegate.HeaderDelegate<?>>()).put(EntityTag.class, this._createHeaderDelegate((Class<?>)EntityTag.class));
        this.map.put(MediaType.class, this._createHeaderDelegate((Class<?>)MediaType.class));
        this.map.put(CacheControl.class, this._createHeaderDelegate((Class<?>)CacheControl.class));
        this.map.put(NewCookie.class, this._createHeaderDelegate((Class<?>)NewCookie.class));
        this.map.put(Cookie.class, this._createHeaderDelegate((Class<?>)Cookie.class));
        this.map.put(URI.class, this._createHeaderDelegate((Class<?>)URI.class));
        this.map.put(Date.class, this._createHeaderDelegate((Class<?>)Date.class));
        this.map.put(String.class, this._createHeaderDelegate((Class<?>)String.class));
    }
    
    public Variant.VariantListBuilder createVariantListBuilder() {
        return new VariantListBuilder();
    }
    
    public Response.ResponseBuilder createResponseBuilder() {
        return new OutboundJaxrsResponse.Builder(new OutboundMessageContext());
    }
    
    public UriBuilder createUriBuilder() {
        return new JerseyUriBuilder();
    }
    
    public Link.Builder createLinkBuilder() {
        return (Link.Builder)new JerseyLink.Builder();
    }
    
    public <T> RuntimeDelegate.HeaderDelegate<T> createHeaderDelegate(final Class<T> type) {
        if (type == null) {
            throw new IllegalArgumentException("type parameter cannot be null");
        }
        final RuntimeDelegate.HeaderDelegate<T> delegate = (RuntimeDelegate.HeaderDelegate<T>)this.map.get(type);
        if (delegate != null) {
            return delegate;
        }
        return (RuntimeDelegate.HeaderDelegate<T>)this._createHeaderDelegate((Class<Object>)type);
    }
    
    private <T> RuntimeDelegate.HeaderDelegate<T> _createHeaderDelegate(final Class<T> type) {
        for (final HeaderDelegateProvider hp : this.hps) {
            if (hp.supports(type)) {
                return (RuntimeDelegate.HeaderDelegate<T>)hp;
            }
        }
        return null;
    }
}
