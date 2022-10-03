package com.sun.xml.internal.ws.api;

import java.io.IOException;
import java.net.URLConnection;
import java.util.Iterator;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.net.ProxySelector;
import javax.xml.ws.WebServiceException;
import java.net.URISyntaxException;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URI;
import com.sun.istack.internal.Nullable;
import java.net.URL;

public final class EndpointAddress
{
    @Nullable
    private URL url;
    private final URI uri;
    private final String stringForm;
    private volatile boolean dontUseProxyMethod;
    private Proxy proxy;
    
    public EndpointAddress(final URI uri) {
        this.uri = uri;
        this.stringForm = uri.toString();
        try {
            this.initURL();
            this.proxy = this.chooseProxy();
        }
        catch (final MalformedURLException ex) {}
    }
    
    public EndpointAddress(final String url) throws URISyntaxException {
        this.uri = new URI(url);
        this.stringForm = url;
        try {
            this.initURL();
            this.proxy = this.chooseProxy();
        }
        catch (final MalformedURLException ex) {}
    }
    
    private void initURL() throws MalformedURLException {
        String scheme = this.uri.getScheme();
        if (scheme == null) {
            this.url = new URL(this.uri.toString());
            return;
        }
        scheme = scheme.toLowerCase();
        if ("http".equals(scheme) || "https".equals(scheme)) {
            this.url = new URL(this.uri.toASCIIString());
        }
        else {
            this.url = this.uri.toURL();
        }
    }
    
    public static EndpointAddress create(final String url) {
        try {
            return new EndpointAddress(url);
        }
        catch (final URISyntaxException e) {
            throw new WebServiceException("Illegal endpoint address: " + url, e);
        }
    }
    
    private Proxy chooseProxy() {
        final ProxySelector sel = AccessController.doPrivileged((PrivilegedAction<ProxySelector>)new PrivilegedAction<ProxySelector>() {
            @Override
            public ProxySelector run() {
                return ProxySelector.getDefault();
            }
        });
        if (sel == null) {
            return Proxy.NO_PROXY;
        }
        if (!sel.getClass().getName().equals("sun.net.spi.DefaultProxySelector")) {
            return null;
        }
        final Iterator<Proxy> it = sel.select(this.uri).iterator();
        if (it.hasNext()) {
            return it.next();
        }
        return Proxy.NO_PROXY;
    }
    
    public URL getURL() {
        return this.url;
    }
    
    public URI getURI() {
        return this.uri;
    }
    
    public URLConnection openConnection() throws IOException {
        if (this.url == null) {
            throw new WebServiceException("URI=" + this.uri + " doesn't have the corresponding URL");
        }
        if (this.proxy != null && !this.dontUseProxyMethod) {
            try {
                return this.url.openConnection(this.proxy);
            }
            catch (final UnsupportedOperationException e) {
                this.dontUseProxyMethod = true;
            }
        }
        return this.url.openConnection();
    }
    
    @Override
    public String toString() {
        return this.stringForm;
    }
}
