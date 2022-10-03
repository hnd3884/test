package com.sun.org.apache.xerces.internal.util;

import javax.xml.parsers.SAXParserFactory;
import com.sun.org.apache.xml.internal.resolver.readers.CatalogReader;
import com.sun.org.apache.xml.internal.resolver.readers.SAXCatalogReader;
import jdk.xml.internal.JdkXmlUtils;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLInputSource;
import com.sun.org.apache.xerces.internal.xni.XMLResourceIdentifier;
import com.sun.org.apache.xerces.internal.dom.DOMInputImpl;
import org.w3c.dom.ls.LSInput;
import java.io.IOException;
import org.xml.sax.SAXException;
import org.xml.sax.InputSource;
import com.sun.org.apache.xml.internal.resolver.Catalog;
import com.sun.org.apache.xml.internal.resolver.CatalogManager;
import org.w3c.dom.ls.LSResourceResolver;
import org.xml.sax.ext.EntityResolver2;
import com.sun.org.apache.xerces.internal.xni.parser.XMLEntityResolver;

public class XMLCatalogResolver implements XMLEntityResolver, EntityResolver2, LSResourceResolver
{
    private CatalogManager fResolverCatalogManager;
    private Catalog fCatalog;
    private String[] fCatalogsList;
    private boolean fCatalogsChanged;
    private boolean fPreferPublic;
    private boolean fUseLiteralSystemId;
    
    public XMLCatalogResolver() {
        this(null, true);
    }
    
    public XMLCatalogResolver(final String[] catalogs) {
        this(catalogs, true);
    }
    
    public XMLCatalogResolver(final String[] catalogs, final boolean preferPublic) {
        this.fResolverCatalogManager = null;
        this.fCatalog = null;
        this.fCatalogsList = null;
        this.fCatalogsChanged = true;
        this.fPreferPublic = true;
        this.fUseLiteralSystemId = true;
        this.init(catalogs, preferPublic);
    }
    
    public final synchronized String[] getCatalogList() {
        return (String[])((this.fCatalogsList != null) ? ((String[])this.fCatalogsList.clone()) : null);
    }
    
    public final synchronized void setCatalogList(final String[] catalogs) {
        this.fCatalogsChanged = true;
        this.fCatalogsList = (String[])((catalogs != null) ? ((String[])catalogs.clone()) : null);
    }
    
    public final synchronized void clear() {
        this.fCatalog = null;
    }
    
    public final boolean getPreferPublic() {
        return this.fPreferPublic;
    }
    
    public final void setPreferPublic(final boolean preferPublic) {
        this.fPreferPublic = preferPublic;
        this.fResolverCatalogManager.setPreferPublic(preferPublic);
    }
    
    public final boolean getUseLiteralSystemId() {
        return this.fUseLiteralSystemId;
    }
    
    public final void setUseLiteralSystemId(final boolean useLiteralSystemId) {
        this.fUseLiteralSystemId = useLiteralSystemId;
    }
    
    @Override
    public InputSource resolveEntity(final String publicId, final String systemId) throws SAXException, IOException {
        String resolvedId = null;
        if (publicId != null && systemId != null) {
            resolvedId = this.resolvePublic(publicId, systemId);
        }
        else if (systemId != null) {
            resolvedId = this.resolveSystem(systemId);
        }
        if (resolvedId != null) {
            final InputSource source = new InputSource(resolvedId);
            source.setPublicId(publicId);
            return source;
        }
        return null;
    }
    
    @Override
    public InputSource resolveEntity(final String name, final String publicId, final String baseURI, String systemId) throws SAXException, IOException {
        String resolvedId = null;
        if (!this.getUseLiteralSystemId() && baseURI != null) {
            try {
                final URI uri = new URI(new URI(baseURI), systemId);
                systemId = uri.toString();
            }
            catch (final URI.MalformedURIException ex) {}
        }
        if (publicId != null && systemId != null) {
            resolvedId = this.resolvePublic(publicId, systemId);
        }
        else if (systemId != null) {
            resolvedId = this.resolveSystem(systemId);
        }
        if (resolvedId != null) {
            final InputSource source = new InputSource(resolvedId);
            source.setPublicId(publicId);
            return source;
        }
        return null;
    }
    
    @Override
    public InputSource getExternalSubset(final String name, final String baseURI) throws SAXException, IOException {
        return null;
    }
    
    @Override
    public LSInput resolveResource(final String type, final String namespaceURI, final String publicId, String systemId, final String baseURI) {
        String resolvedId = null;
        try {
            if (namespaceURI != null) {
                resolvedId = this.resolveURI(namespaceURI);
            }
            if (!this.getUseLiteralSystemId() && baseURI != null) {
                try {
                    final URI uri = new URI(new URI(baseURI), systemId);
                    systemId = uri.toString();
                }
                catch (final URI.MalformedURIException ex) {}
            }
            if (resolvedId == null) {
                if (publicId != null && systemId != null) {
                    resolvedId = this.resolvePublic(publicId, systemId);
                }
                else if (systemId != null) {
                    resolvedId = this.resolveSystem(systemId);
                }
            }
        }
        catch (final IOException ex2) {}
        if (resolvedId != null) {
            return new DOMInputImpl(publicId, resolvedId, baseURI);
        }
        return null;
    }
    
    @Override
    public XMLInputSource resolveEntity(final XMLResourceIdentifier resourceIdentifier) throws XNIException, IOException {
        final String resolvedId = this.resolveIdentifier(resourceIdentifier);
        if (resolvedId != null) {
            return new XMLInputSource(resourceIdentifier.getPublicId(), resolvedId, resourceIdentifier.getBaseSystemId());
        }
        return null;
    }
    
    public String resolveIdentifier(final XMLResourceIdentifier resourceIdentifier) throws IOException, XNIException {
        String resolvedId = null;
        final String namespace = resourceIdentifier.getNamespace();
        if (namespace != null) {
            resolvedId = this.resolveURI(namespace);
        }
        if (resolvedId == null) {
            final String publicId = resourceIdentifier.getPublicId();
            final String systemId = this.getUseLiteralSystemId() ? resourceIdentifier.getLiteralSystemId() : resourceIdentifier.getExpandedSystemId();
            if (publicId != null && systemId != null) {
                resolvedId = this.resolvePublic(publicId, systemId);
            }
            else if (systemId != null) {
                resolvedId = this.resolveSystem(systemId);
            }
        }
        return resolvedId;
    }
    
    public final synchronized String resolveSystem(final String systemId) throws IOException {
        if (this.fCatalogsChanged) {
            this.parseCatalogs();
            this.fCatalogsChanged = false;
        }
        return (this.fCatalog != null) ? this.fCatalog.resolveSystem(systemId) : null;
    }
    
    public final synchronized String resolvePublic(final String publicId, final String systemId) throws IOException {
        if (this.fCatalogsChanged) {
            this.parseCatalogs();
            this.fCatalogsChanged = false;
        }
        return (this.fCatalog != null) ? this.fCatalog.resolvePublic(publicId, systemId) : null;
    }
    
    public final synchronized String resolveURI(final String uri) throws IOException {
        if (this.fCatalogsChanged) {
            this.parseCatalogs();
            this.fCatalogsChanged = false;
        }
        return (this.fCatalog != null) ? this.fCatalog.resolveURI(uri) : null;
    }
    
    private void init(final String[] catalogs, final boolean preferPublic) {
        this.fCatalogsList = (String[])((catalogs != null) ? ((String[])catalogs.clone()) : null);
        this.fPreferPublic = preferPublic;
        (this.fResolverCatalogManager = new CatalogManager()).setAllowOasisXMLCatalogPI(false);
        this.fResolverCatalogManager.setCatalogClassName("com.sun.org.apache.xml.internal.resolver.Catalog");
        this.fResolverCatalogManager.setCatalogFiles("");
        this.fResolverCatalogManager.setIgnoreMissingProperties(true);
        this.fResolverCatalogManager.setPreferPublic(this.fPreferPublic);
        this.fResolverCatalogManager.setRelativeCatalogs(false);
        this.fResolverCatalogManager.setUseStaticCatalog(false);
        this.fResolverCatalogManager.setVerbosity(0);
    }
    
    private void parseCatalogs() throws IOException {
        if (this.fCatalogsList != null) {
            this.attachReaderToCatalog(this.fCatalog = new Catalog(this.fResolverCatalogManager));
            for (int i = 0; i < this.fCatalogsList.length; ++i) {
                final String catalog = this.fCatalogsList[i];
                if (catalog != null && catalog.length() > 0) {
                    this.fCatalog.parseCatalog(catalog);
                }
            }
        }
        else {
            this.fCatalog = null;
        }
    }
    
    private void attachReaderToCatalog(final Catalog catalog) {
        final SAXParserFactory spf = JdkXmlUtils.getSAXFactory(catalog.getCatalogManager().overrideDefaultParser());
        spf.setValidating(false);
        final SAXCatalogReader saxReader = new SAXCatalogReader(spf);
        saxReader.setCatalogParser("urn:oasis:names:tc:entity:xmlns:xml:catalog", "catalog", "com.sun.org.apache.xml.internal.resolver.readers.OASISXMLCatalogReader");
        catalog.addReader("application/xml", saxReader);
    }
}
