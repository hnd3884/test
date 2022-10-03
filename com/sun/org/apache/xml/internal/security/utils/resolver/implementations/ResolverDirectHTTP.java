package com.sun.org.apache.xml.internal.security.utils.resolver.implementations;

import com.sun.org.slf4j.internal.LoggerFactory;
import java.net.SocketAddress;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.io.InputStream;
import java.net.URLConnection;
import java.net.URL;
import java.net.URI;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import com.sun.org.apache.xml.internal.security.utils.resolver.ResourceResolverException;
import java.io.ByteArrayOutputStream;
import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
import java.nio.charset.StandardCharsets;
import com.sun.org.apache.xml.internal.security.signature.XMLSignatureInput;
import com.sun.org.apache.xml.internal.security.utils.resolver.ResourceResolverContext;
import com.sun.org.slf4j.internal.Logger;
import com.sun.org.apache.xml.internal.security.utils.resolver.ResourceResolverSpi;

public class ResolverDirectHTTP extends ResourceResolverSpi
{
    private static final Logger LOG;
    private static final String[] properties;
    private static final int HttpProxyHost = 0;
    private static final int HttpProxyPort = 1;
    private static final int HttpProxyUser = 2;
    private static final int HttpProxyPass = 3;
    private static final int HttpBasicUser = 4;
    private static final int HttpBasicPass = 5;
    
    @Override
    public boolean engineIsThreadSafe() {
        return true;
    }
    
    @Override
    public XMLSignatureInput engineResolveURI(final ResourceResolverContext resourceResolverContext) throws ResourceResolverException {
        try {
            final URI newURI = getNewURI(resourceResolverContext.uriToResolve, resourceResolverContext.baseUri);
            final URL url = newURI.toURL();
            URLConnection urlConnection = this.openConnection(url);
            final String headerField = urlConnection.getHeaderField("WWW-Authenticate");
            if (headerField != null && headerField.startsWith("Basic")) {
                final String engineGetProperty = this.engineGetProperty(ResolverDirectHTTP.properties[4]);
                final String engineGetProperty2 = this.engineGetProperty(ResolverDirectHTTP.properties[5]);
                if (engineGetProperty != null && engineGetProperty2 != null) {
                    urlConnection = this.openConnection(url);
                    urlConnection.setRequestProperty("Authorization", "Basic " + XMLUtils.encodeToString((engineGetProperty + ":" + engineGetProperty2).getBytes(StandardCharsets.ISO_8859_1)));
                }
            }
            final String headerField2 = urlConnection.getHeaderField("Content-Type");
            try (final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                 final InputStream inputStream = urlConnection.getInputStream()) {
                final byte[] array = new byte[4096];
                int n = 0;
                int read;
                while ((read = inputStream.read(array)) >= 0) {
                    byteArrayOutputStream.write(array, 0, read);
                    n += read;
                }
                ResolverDirectHTTP.LOG.debug("Fetched {} bytes from URI {}", n, newURI.toString());
                final XMLSignatureInput xmlSignatureInput = new XMLSignatureInput(byteArrayOutputStream.toByteArray());
                xmlSignatureInput.setSecureValidation(resourceResolverContext.secureValidation);
                xmlSignatureInput.setSourceURI(newURI.toString());
                xmlSignatureInput.setMIMEType(headerField2);
                return xmlSignatureInput;
            }
        }
        catch (final URISyntaxException ex) {
            throw new ResourceResolverException(ex, resourceResolverContext.uriToResolve, resourceResolverContext.baseUri, "generic.EmptyMessage");
        }
        catch (final MalformedURLException ex2) {
            throw new ResourceResolverException(ex2, resourceResolverContext.uriToResolve, resourceResolverContext.baseUri, "generic.EmptyMessage");
        }
        catch (final IOException ex3) {
            throw new ResourceResolverException(ex3, resourceResolverContext.uriToResolve, resourceResolverContext.baseUri, "generic.EmptyMessage");
        }
        catch (final IllegalArgumentException ex4) {
            throw new ResourceResolverException(ex4, resourceResolverContext.uriToResolve, resourceResolverContext.baseUri, "generic.EmptyMessage");
        }
    }
    
    private URLConnection openConnection(final URL url) throws IOException {
        final String engineGetProperty = this.engineGetProperty(ResolverDirectHTTP.properties[0]);
        final String engineGetProperty2 = this.engineGetProperty(ResolverDirectHTTP.properties[1]);
        final String engineGetProperty3 = this.engineGetProperty(ResolverDirectHTTP.properties[2]);
        final String engineGetProperty4 = this.engineGetProperty(ResolverDirectHTTP.properties[3]);
        Proxy proxy = null;
        if (engineGetProperty != null && engineGetProperty2 != null) {
            proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(engineGetProperty, Integer.parseInt(engineGetProperty2)));
        }
        URLConnection urlConnection;
        if (proxy != null) {
            urlConnection = url.openConnection(proxy);
            if (engineGetProperty3 != null && engineGetProperty4 != null) {
                urlConnection.setRequestProperty("Proxy-Authorization", "Basic " + XMLUtils.encodeToString((engineGetProperty3 + ":" + engineGetProperty4).getBytes(StandardCharsets.ISO_8859_1)));
            }
        }
        else {
            urlConnection = url.openConnection();
        }
        return urlConnection;
    }
    
    @Override
    public boolean engineCanResolveURI(final ResourceResolverContext resourceResolverContext) {
        if (resourceResolverContext.uriToResolve == null) {
            ResolverDirectHTTP.LOG.debug("quick fail, uri == null");
            return false;
        }
        if (resourceResolverContext.uriToResolve.equals("") || resourceResolverContext.uriToResolve.charAt(0) == '#') {
            ResolverDirectHTTP.LOG.debug("quick fail for empty URIs and local ones");
            return false;
        }
        ResolverDirectHTTP.LOG.debug("I was asked whether I can resolve {}", resourceResolverContext.uriToResolve);
        if (resourceResolverContext.uriToResolve.startsWith("http:") || (resourceResolverContext.baseUri != null && resourceResolverContext.baseUri.startsWith("http:"))) {
            ResolverDirectHTTP.LOG.debug("I state that I can resolve {}", resourceResolverContext.uriToResolve);
            return true;
        }
        ResolverDirectHTTP.LOG.debug("I state that I can't resolve {}", resourceResolverContext.uriToResolve);
        return false;
    }
    
    @Override
    public String[] engineGetPropertyKeys() {
        return ResolverDirectHTTP.properties.clone();
    }
    
    private static URI getNewURI(final String s, final String s2) throws URISyntaxException {
        URI resolve;
        if (s2 == null || "".equals(s2)) {
            resolve = new URI(s);
        }
        else {
            resolve = new URI(s2).resolve(s);
        }
        if (resolve.getFragment() != null) {
            return new URI(resolve.getScheme(), resolve.getSchemeSpecificPart(), null);
        }
        return resolve;
    }
    
    static {
        LOG = LoggerFactory.getLogger(ResolverDirectHTTP.class);
        properties = new String[] { "http.proxy.host", "http.proxy.port", "http.proxy.username", "http.proxy.password", "http.basic.username", "http.basic.password" };
    }
}
