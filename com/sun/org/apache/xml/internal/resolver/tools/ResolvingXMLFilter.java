package com.sun.org.apache.xml.internal.resolver.tools;

import com.sun.org.apache.xml.internal.resolver.helpers.FileURL;
import java.net.MalformedURLException;
import org.xml.sax.Attributes;
import java.io.InputStream;
import org.xml.sax.SAXException;
import java.io.IOException;
import org.xml.sax.InputSource;
import com.sun.org.apache.xml.internal.resolver.Catalog;
import org.xml.sax.XMLReader;
import java.net.URL;
import com.sun.org.apache.xml.internal.resolver.CatalogManager;
import org.xml.sax.helpers.XMLFilterImpl;

public class ResolvingXMLFilter extends XMLFilterImpl
{
    public static boolean suppressExplanation;
    CatalogManager catalogManager;
    private CatalogResolver catalogResolver;
    private CatalogResolver piCatalogResolver;
    private boolean allowXMLCatalogPI;
    private boolean oasisXMLCatalogPI;
    private URL baseURL;
    
    public ResolvingXMLFilter() {
        this.catalogManager = CatalogManager.getStaticManager();
        this.catalogResolver = null;
        this.piCatalogResolver = null;
        this.allowXMLCatalogPI = false;
        this.oasisXMLCatalogPI = false;
        this.baseURL = null;
        this.catalogResolver = new CatalogResolver(this.catalogManager);
    }
    
    public ResolvingXMLFilter(final XMLReader parent) {
        super(parent);
        this.catalogManager = CatalogManager.getStaticManager();
        this.catalogResolver = null;
        this.piCatalogResolver = null;
        this.allowXMLCatalogPI = false;
        this.oasisXMLCatalogPI = false;
        this.baseURL = null;
        this.catalogResolver = new CatalogResolver(this.catalogManager);
    }
    
    public ResolvingXMLFilter(final CatalogManager manager) {
        this.catalogManager = CatalogManager.getStaticManager();
        this.catalogResolver = null;
        this.piCatalogResolver = null;
        this.allowXMLCatalogPI = false;
        this.oasisXMLCatalogPI = false;
        this.baseURL = null;
        this.catalogManager = manager;
        this.catalogResolver = new CatalogResolver(this.catalogManager);
    }
    
    public ResolvingXMLFilter(final XMLReader parent, final CatalogManager manager) {
        super(parent);
        this.catalogManager = CatalogManager.getStaticManager();
        this.catalogResolver = null;
        this.piCatalogResolver = null;
        this.allowXMLCatalogPI = false;
        this.oasisXMLCatalogPI = false;
        this.baseURL = null;
        this.catalogManager = manager;
        this.catalogResolver = new CatalogResolver(this.catalogManager);
    }
    
    public Catalog getCatalog() {
        return this.catalogResolver.getCatalog();
    }
    
    @Override
    public void parse(final InputSource input) throws IOException, SAXException {
        this.allowXMLCatalogPI = true;
        this.setupBaseURI(input.getSystemId());
        try {
            super.parse(input);
        }
        catch (final InternalError ie) {
            this.explain(input.getSystemId());
            throw ie;
        }
    }
    
    @Override
    public void parse(final String systemId) throws IOException, SAXException {
        this.allowXMLCatalogPI = true;
        this.setupBaseURI(systemId);
        try {
            super.parse(systemId);
        }
        catch (final InternalError ie) {
            this.explain(systemId);
            throw ie;
        }
    }
    
    @Override
    public InputSource resolveEntity(final String publicId, final String systemId) {
        this.allowXMLCatalogPI = false;
        String resolved = this.catalogResolver.getResolvedEntity(publicId, systemId);
        if (resolved == null && this.piCatalogResolver != null) {
            resolved = this.piCatalogResolver.getResolvedEntity(publicId, systemId);
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
                this.catalogManager.debug.message(1, "Failed to create InputSource", resolved);
                return null;
            }
        }
        return null;
    }
    
    @Override
    public void notationDecl(final String name, final String publicId, final String systemId) throws SAXException {
        this.allowXMLCatalogPI = false;
        super.notationDecl(name, publicId, systemId);
    }
    
    @Override
    public void unparsedEntityDecl(final String name, final String publicId, final String systemId, final String notationName) throws SAXException {
        this.allowXMLCatalogPI = false;
        super.unparsedEntityDecl(name, publicId, systemId, notationName);
    }
    
    @Override
    public void startElement(final String uri, final String localName, final String qName, final Attributes atts) throws SAXException {
        this.allowXMLCatalogPI = false;
        super.startElement(uri, localName, qName, atts);
    }
    
    @Override
    public void processingInstruction(final String target, final String pidata) throws SAXException {
        if (target.equals("oasis-xml-catalog")) {
            URL catalog = null;
            String data = pidata;
            int pos = data.indexOf("catalog=");
            if (pos >= 0) {
                data = data.substring(pos + 8);
                if (data.length() > 1) {
                    final String quote = data.substring(0, 1);
                    data = data.substring(1);
                    pos = data.indexOf(quote);
                    if (pos >= 0) {
                        data = data.substring(0, pos);
                        try {
                            if (this.baseURL != null) {
                                catalog = new URL(this.baseURL, data);
                            }
                            else {
                                catalog = new URL(data);
                            }
                        }
                        catch (final MalformedURLException ex) {}
                    }
                }
            }
            if (this.allowXMLCatalogPI) {
                if (this.catalogManager.getAllowOasisXMLCatalogPI()) {
                    this.catalogManager.debug.message(4, "oasis-xml-catalog PI", pidata);
                    if (catalog != null) {
                        try {
                            this.catalogManager.debug.message(4, "oasis-xml-catalog", catalog.toString());
                            this.oasisXMLCatalogPI = true;
                            if (this.piCatalogResolver == null) {
                                this.piCatalogResolver = new CatalogResolver(true);
                            }
                            this.piCatalogResolver.getCatalog().parseCatalog(catalog.toString());
                        }
                        catch (final Exception e) {
                            this.catalogManager.debug.message(3, "Exception parsing oasis-xml-catalog: " + catalog.toString());
                        }
                    }
                    else {
                        this.catalogManager.debug.message(3, "PI oasis-xml-catalog unparseable: " + pidata);
                    }
                }
                else {
                    this.catalogManager.debug.message(4, "PI oasis-xml-catalog ignored: " + pidata);
                }
            }
            else {
                this.catalogManager.debug.message(3, "PI oasis-xml-catalog occurred in an invalid place: " + pidata);
            }
        }
        else {
            super.processingInstruction(target, pidata);
        }
    }
    
    private void setupBaseURI(final String systemId) {
        URL cwd = null;
        try {
            cwd = FileURL.makeURL("basename");
        }
        catch (final MalformedURLException mue) {
            cwd = null;
        }
        try {
            this.baseURL = new URL(systemId);
        }
        catch (final MalformedURLException mue) {
            if (cwd != null) {
                try {
                    this.baseURL = new URL(cwd, systemId);
                }
                catch (final MalformedURLException mue2) {
                    this.baseURL = null;
                }
            }
            else {
                this.baseURL = null;
            }
        }
    }
    
    private void explain(final String systemId) {
        if (!ResolvingXMLFilter.suppressExplanation) {
            System.out.println("XMLReader probably encountered bad URI in " + systemId);
            System.out.println("For example, replace '/some/uri' with 'file:/some/uri'.");
        }
        ResolvingXMLFilter.suppressExplanation = true;
    }
    
    static {
        ResolvingXMLFilter.suppressExplanation = false;
    }
}
