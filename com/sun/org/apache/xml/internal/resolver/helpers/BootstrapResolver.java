package com.sun.org.apache.xml.internal.resolver.helpers;

import javax.xml.transform.sax.SAXSource;
import java.net.MalformedURLException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.Source;
import java.io.InputStream;
import org.xml.sax.InputSource;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import javax.xml.transform.URIResolver;
import org.xml.sax.EntityResolver;

public class BootstrapResolver implements EntityResolver, URIResolver
{
    public static final String xmlCatalogXSD = "http://www.oasis-open.org/committees/entity/release/1.0/catalog.xsd";
    public static final String xmlCatalogRNG = "http://www.oasis-open.org/committees/entity/release/1.0/catalog.rng";
    public static final String xmlCatalogPubId = "-//OASIS//DTD XML Catalogs V1.0//EN";
    public static final String xmlCatalogSysId = "http://www.oasis-open.org/committees/entity/release/1.0/catalog.dtd";
    private final Map<String, String> publicMap;
    private final Map<String, String> systemMap;
    private final Map<String, String> uriMap;
    
    public BootstrapResolver() {
        this.publicMap = new HashMap<String, String>();
        this.systemMap = new HashMap<String, String>();
        this.uriMap = new HashMap<String, String>();
        URL url = this.getClass().getResource("/com/sun/org/apache/xml/internal/resolver/etc/catalog.dtd");
        if (url != null) {
            this.publicMap.put("-//OASIS//DTD XML Catalogs V1.0//EN", url.toString());
            this.systemMap.put("http://www.oasis-open.org/committees/entity/release/1.0/catalog.dtd", url.toString());
        }
        url = this.getClass().getResource("/com/sun/org/apache/xml/internal/resolver/etc/catalog.rng");
        if (url != null) {
            this.uriMap.put("http://www.oasis-open.org/committees/entity/release/1.0/catalog.rng", url.toString());
        }
        url = this.getClass().getResource("/com/sun/org/apache/xml/internal/resolver/etc/catalog.xsd");
        if (url != null) {
            this.uriMap.put("http://www.oasis-open.org/committees/entity/release/1.0/catalog.xsd", url.toString());
        }
    }
    
    @Override
    public InputSource resolveEntity(final String publicId, final String systemId) {
        String resolved = null;
        if (systemId != null && this.systemMap.containsKey(systemId)) {
            resolved = this.systemMap.get(systemId);
        }
        else if (publicId != null && this.publicMap.containsKey(publicId)) {
            resolved = this.publicMap.get(publicId);
        }
        if (resolved != null) {
            try {
                final InputSource iSource = new InputSource(resolved);
                iSource.setPublicId(publicId);
                final URL url = new URL(resolved);
                final InputStream iStream = url.openStream();
                iSource.setByteStream(iStream);
                return iSource;
            }
            catch (final Exception e) {
                return null;
            }
        }
        return null;
    }
    
    @Override
    public Source resolve(final String href, final String base) throws TransformerException {
        String uri = href;
        String fragment = null;
        final int hashPos = href.indexOf("#");
        if (hashPos >= 0) {
            uri = href.substring(0, hashPos);
            fragment = href.substring(hashPos + 1);
        }
        String result = null;
        if (href != null && this.uriMap.containsKey(href)) {
            result = this.uriMap.get(href);
        }
        if (result == null) {
            try {
                URL url = null;
                if (base == null) {
                    url = new URL(uri);
                    result = url.toString();
                }
                else {
                    final URL baseURL = new URL(base);
                    url = ((href.length() == 0) ? baseURL : new URL(baseURL, uri));
                    result = url.toString();
                }
            }
            catch (final MalformedURLException mue) {
                final String absBase = this.makeAbsolute(base);
                if (!absBase.equals(base)) {
                    return this.resolve(href, absBase);
                }
                throw new TransformerException("Malformed URL " + href + "(base " + base + ")", mue);
            }
        }
        final SAXSource source = new SAXSource();
        source.setInputSource(new InputSource(result));
        return source;
    }
    
    private String makeAbsolute(String uri) {
        if (uri == null) {
            uri = "";
        }
        try {
            final URL url = new URL(uri);
            return url.toString();
        }
        catch (final MalformedURLException mue) {
            try {
                final URL fileURL = FileURL.makeURL(uri);
                return fileURL.toString();
            }
            catch (final MalformedURLException mue2) {
                return uri;
            }
        }
    }
}
