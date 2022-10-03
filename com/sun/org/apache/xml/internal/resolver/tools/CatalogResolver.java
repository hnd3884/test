package com.sun.org.apache.xml.internal.resolver.tools;

import com.sun.org.apache.xml.internal.resolver.helpers.FileURL;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.XMLReader;
import org.xml.sax.SAXException;
import javax.xml.parsers.ParserConfigurationException;
import jdk.xml.internal.JdkXmlUtils;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.TransformerException;
import javax.xml.transform.Source;
import java.io.InputStream;
import java.net.URL;
import org.xml.sax.InputSource;
import java.io.IOException;
import java.net.MalformedURLException;
import com.sun.org.apache.xml.internal.resolver.CatalogManager;
import com.sun.org.apache.xml.internal.resolver.Catalog;
import javax.xml.transform.URIResolver;
import org.xml.sax.EntityResolver;

public class CatalogResolver implements EntityResolver, URIResolver
{
    public boolean namespaceAware;
    public boolean validating;
    private Catalog catalog;
    private CatalogManager catalogManager;
    
    public CatalogResolver() {
        this.namespaceAware = true;
        this.validating = false;
        this.catalog = null;
        this.catalogManager = CatalogManager.getStaticManager();
        this.initializeCatalogs(false);
    }
    
    public CatalogResolver(final boolean privateCatalog) {
        this.namespaceAware = true;
        this.validating = false;
        this.catalog = null;
        this.catalogManager = CatalogManager.getStaticManager();
        this.initializeCatalogs(privateCatalog);
    }
    
    public CatalogResolver(final CatalogManager manager) {
        this.namespaceAware = true;
        this.validating = false;
        this.catalog = null;
        this.catalogManager = CatalogManager.getStaticManager();
        this.catalogManager = manager;
        this.initializeCatalogs(!this.catalogManager.getUseStaticCatalog());
    }
    
    private void initializeCatalogs(final boolean privateCatalog) {
        this.catalog = this.catalogManager.getCatalog();
    }
    
    public Catalog getCatalog() {
        return this.catalog;
    }
    
    public String getResolvedEntity(final String publicId, final String systemId) {
        String resolved = null;
        if (this.catalog == null) {
            this.catalogManager.debug.message(1, "Catalog resolution attempted with null catalog; ignored");
            return null;
        }
        if (systemId != null) {
            try {
                resolved = this.catalog.resolveSystem(systemId);
            }
            catch (final MalformedURLException me) {
                this.catalogManager.debug.message(1, "Malformed URL exception trying to resolve", publicId);
                resolved = null;
            }
            catch (final IOException ie) {
                this.catalogManager.debug.message(1, "I/O exception trying to resolve", publicId);
                resolved = null;
            }
        }
        if (resolved == null) {
            if (publicId != null) {
                try {
                    resolved = this.catalog.resolvePublic(publicId, systemId);
                }
                catch (final MalformedURLException me) {
                    this.catalogManager.debug.message(1, "Malformed URL exception trying to resolve", publicId);
                }
                catch (final IOException ie) {
                    this.catalogManager.debug.message(1, "I/O exception trying to resolve", publicId);
                }
            }
            if (resolved != null) {
                this.catalogManager.debug.message(2, "Resolved public", publicId, resolved);
            }
        }
        else {
            this.catalogManager.debug.message(2, "Resolved system", systemId, resolved);
        }
        return resolved;
    }
    
    @Override
    public InputSource resolveEntity(final String publicId, final String systemId) {
        final String resolved = this.getResolvedEntity(publicId, systemId);
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
                this.catalogManager.debug.message(1, "Failed to create InputSource", resolved);
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
        try {
            result = this.catalog.resolveURI(href);
        }
        catch (final Exception ex) {}
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
        this.catalogManager.debug.message(2, "Resolved URI", href, result);
        final SAXSource source = new SAXSource();
        source.setInputSource(new InputSource(result));
        this.setEntityResolver(source);
        return source;
    }
    
    private void setEntityResolver(final SAXSource source) throws TransformerException {
        XMLReader reader = source.getXMLReader();
        if (reader == null) {
            final SAXParserFactory spf = JdkXmlUtils.getSAXFactory(this.catalogManager.overrideDefaultParser());
            try {
                reader = spf.newSAXParser().getXMLReader();
            }
            catch (final ParserConfigurationException ex) {
                throw new TransformerException(ex);
            }
            catch (final SAXException ex2) {
                throw new TransformerException(ex2);
            }
        }
        reader.setEntityResolver(this);
        source.setXMLReader(reader);
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
