package org.apache.xml.security.utils.resolver.implementations;

import java.util.Hashtable;
import org.apache.commons.logging.LogFactory;
import java.io.InputStream;
import java.net.URLConnection;
import java.io.IOException;
import java.net.MalformedURLException;
import org.apache.xml.security.utils.resolver.ResourceResolverException;
import java.io.ByteArrayOutputStream;
import org.apache.xml.security.utils.Base64;
import java.net.URL;
import org.apache.xml.utils.URI;
import org.apache.xml.security.signature.XMLSignatureInput;
import org.w3c.dom.Attr;
import org.apache.commons.logging.Log;
import org.apache.xml.security.utils.resolver.ResourceResolverSpi;

public class ResolverDirectHTTP extends ResourceResolverSpi
{
    static Log log;
    private static final String[] properties;
    private static final int HttpProxyHost = 0;
    private static final int HttpProxyPort = 1;
    private static final int HttpProxyUser = 2;
    private static final int HttpProxyPass = 3;
    private static final int HttpBasicUser = 4;
    private static final int HttpBasicPass = 5;
    
    public boolean engineIsThreadSafe() {
        return true;
    }
    
    public XMLSignatureInput engineResolve(final Attr attr, final String s) throws ResourceResolverException {
        try {
            boolean b = false;
            final String engineGetProperty = this.engineGetProperty(ResolverDirectHTTP.properties[0]);
            final String engineGetProperty2 = this.engineGetProperty(ResolverDirectHTTP.properties[1]);
            if (engineGetProperty != null && engineGetProperty2 != null) {
                b = true;
            }
            final String s2 = ((Hashtable<K, String>)System.getProperties()).get("http.proxySet");
            final String s3 = ((Hashtable<K, String>)System.getProperties()).get("http.proxyHost");
            final String s4 = ((Hashtable<K, String>)System.getProperties()).get("http.proxyPort");
            final boolean b2 = s2 != null && s3 != null && s4 != null;
            if (b) {
                if (ResolverDirectHTTP.log.isDebugEnabled()) {
                    ResolverDirectHTTP.log.debug((Object)("Use of HTTP proxy enabled: " + engineGetProperty + ":" + engineGetProperty2));
                }
                ((Hashtable<String, String>)System.getProperties()).put("http.proxySet", "true");
                ((Hashtable<String, String>)System.getProperties()).put("http.proxyHost", engineGetProperty);
                ((Hashtable<String, String>)System.getProperties()).put("http.proxyPort", engineGetProperty2);
            }
            final URI newURI = this.getNewURI(attr.getNodeValue(), s);
            final URI uri = new URI(newURI);
            uri.setFragment((String)null);
            final URL url = new URL(uri.toString());
            URLConnection urlConnection = url.openConnection();
            final String engineGetProperty3 = this.engineGetProperty(ResolverDirectHTTP.properties[2]);
            final String engineGetProperty4 = this.engineGetProperty(ResolverDirectHTTP.properties[3]);
            if (engineGetProperty3 != null && engineGetProperty4 != null) {
                urlConnection.setRequestProperty("Proxy-Authorization", Base64.encode((engineGetProperty3 + ":" + engineGetProperty4).getBytes()));
            }
            final String headerField = urlConnection.getHeaderField("WWW-Authenticate");
            if (headerField != null && headerField.startsWith("Basic")) {
                final String engineGetProperty5 = this.engineGetProperty(ResolverDirectHTTP.properties[4]);
                final String engineGetProperty6 = this.engineGetProperty(ResolverDirectHTTP.properties[5]);
                if (engineGetProperty5 != null && engineGetProperty6 != null) {
                    urlConnection = url.openConnection();
                    urlConnection.setRequestProperty("Authorization", "Basic " + Base64.encode((engineGetProperty5 + ":" + engineGetProperty6).getBytes()));
                }
            }
            final String headerField2 = urlConnection.getHeaderField("Content-Type");
            final InputStream inputStream = urlConnection.getInputStream();
            final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            final byte[] array = new byte[4096];
            int n = 0;
            int read;
            while ((read = inputStream.read(array)) >= 0) {
                byteArrayOutputStream.write(array, 0, read);
                n += read;
            }
            ResolverDirectHTTP.log.debug((Object)("Fetched " + n + " bytes from URI " + newURI.toString()));
            final XMLSignatureInput xmlSignatureInput = new XMLSignatureInput(byteArrayOutputStream.toByteArray());
            xmlSignatureInput.setSourceURI(newURI.toString());
            xmlSignatureInput.setMIMEType(headerField2);
            if (b2) {
                ((Hashtable<String, String>)System.getProperties()).put("http.proxySet", s2);
                ((Hashtable<String, String>)System.getProperties()).put("http.proxyHost", s3);
                ((Hashtable<String, String>)System.getProperties()).put("http.proxyPort", s4);
            }
            return xmlSignatureInput;
        }
        catch (final MalformedURLException ex) {
            throw new ResourceResolverException("generic.EmptyMessage", ex, attr, s);
        }
        catch (final IOException ex2) {
            throw new ResourceResolverException("generic.EmptyMessage", ex2, attr, s);
        }
    }
    
    public boolean engineCanResolve(final Attr attr, final String s) {
        if (attr == null) {
            ResolverDirectHTTP.log.debug((Object)"quick fail, uri == null");
            return false;
        }
        final String nodeValue = attr.getNodeValue();
        if (nodeValue.equals("") || nodeValue.charAt(0) == '#') {
            ResolverDirectHTTP.log.debug((Object)"quick fail for empty URIs and local ones");
            return false;
        }
        if (ResolverDirectHTTP.log.isDebugEnabled()) {
            ResolverDirectHTTP.log.debug((Object)("I was asked whether I can resolve " + nodeValue));
        }
        if (nodeValue.startsWith("http:") || (s != null && s.startsWith("http:"))) {
            if (ResolverDirectHTTP.log.isDebugEnabled()) {
                ResolverDirectHTTP.log.debug((Object)("I state that I can resolve " + nodeValue));
            }
            return true;
        }
        if (ResolverDirectHTTP.log.isDebugEnabled()) {
            ResolverDirectHTTP.log.debug((Object)("I state that I can't resolve " + nodeValue));
        }
        return false;
    }
    
    public String[] engineGetPropertyKeys() {
        return ResolverDirectHTTP.properties.clone();
    }
    
    private URI getNewURI(final String s, final String s2) throws URI.MalformedURIException {
        if (s2 == null || "".equals(s2)) {
            return new URI(s);
        }
        return new URI(new URI(s2), s);
    }
    
    static {
        ResolverDirectHTTP.log = LogFactory.getLog(ResolverDirectHTTP.class.getName());
        properties = new String[] { "http.proxy.host", "http.proxy.port", "http.proxy.username", "http.proxy.password", "http.basic.username", "http.basic.password" };
    }
}
