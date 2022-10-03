package org.apache.xerces.util;

import org.apache.xml.resolver.readers.CatalogReader;
import javax.xml.parsers.SAXParserFactory;
import org.apache.xml.resolver.readers.SAXCatalogReader;
import org.apache.xerces.jaxp.SAXParserFactoryImpl;
import org.apache.xerces.xni.XNIException;
import org.apache.xerces.xni.parser.XMLInputSource;
import org.apache.xerces.xni.XMLResourceIdentifier;
import org.apache.xerces.dom.DOMInputImpl;
import org.w3c.dom.ls.LSInput;
import java.io.IOException;
import org.xml.sax.SAXException;
import org.xml.sax.InputSource;
import org.apache.xml.resolver.Catalog;
import org.apache.xml.resolver.CatalogManager;
import org.w3c.dom.ls.LSResourceResolver;
import org.xml.sax.ext.EntityResolver2;
import org.apache.xerces.xni.parser.XMLEntityResolver;

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
    
    public XMLCatalogResolver(final String[] array) {
        this(array, true);
    }
    
    public XMLCatalogResolver(final String[] array, final boolean b) {
        this.fResolverCatalogManager = null;
        this.fCatalog = null;
        this.fCatalogsList = null;
        this.fCatalogsChanged = true;
        this.fPreferPublic = true;
        this.fUseLiteralSystemId = true;
        this.init(array, b);
    }
    
    public final synchronized String[] getCatalogList() {
        return (String[])((this.fCatalogsList != null) ? ((String[])this.fCatalogsList.clone()) : null);
    }
    
    public final synchronized void setCatalogList(final String[] array) {
        this.fCatalogsChanged = true;
        this.fCatalogsList = (String[])((array != null) ? ((String[])array.clone()) : null);
    }
    
    public final synchronized void clear() {
        this.fCatalog = null;
    }
    
    public final boolean getPreferPublic() {
        return this.fPreferPublic;
    }
    
    public final void setPreferPublic(final boolean b) {
        this.fPreferPublic = b;
        this.fResolverCatalogManager.setPreferPublic(b);
    }
    
    public final boolean getUseLiteralSystemId() {
        return this.fUseLiteralSystemId;
    }
    
    public final void setUseLiteralSystemId(final boolean fUseLiteralSystemId) {
        this.fUseLiteralSystemId = fUseLiteralSystemId;
    }
    
    public InputSource resolveEntity(final String publicId, final String s) throws SAXException, IOException {
        String s2 = null;
        if (publicId != null && s != null) {
            s2 = this.resolvePublic(publicId, s);
        }
        else if (s != null) {
            s2 = this.resolveSystem(s);
        }
        if (s2 != null) {
            final InputSource inputSource = new InputSource(s2);
            inputSource.setPublicId(publicId);
            return inputSource;
        }
        return null;
    }
    
    public InputSource resolveEntity(final String s, final String publicId, final String s2, String string) throws SAXException, IOException {
        String s3 = null;
        if (!this.getUseLiteralSystemId() && s2 != null) {
            try {
                string = new URI(new URI(s2), string).toString();
            }
            catch (final URI.MalformedURIException ex) {}
        }
        if (publicId != null && string != null) {
            s3 = this.resolvePublic(publicId, string);
        }
        else if (string != null) {
            s3 = this.resolveSystem(string);
        }
        if (s3 != null) {
            final InputSource inputSource = new InputSource(s3);
            inputSource.setPublicId(publicId);
            return inputSource;
        }
        return null;
    }
    
    public InputSource getExternalSubset(final String s, final String s2) throws SAXException, IOException {
        return null;
    }
    
    public LSInput resolveResource(final String s, final String s2, final String s3, String string, final String s4) {
        String s5 = null;
        try {
            if (s2 != null) {
                s5 = this.resolveURI(s2);
            }
            if (!this.getUseLiteralSystemId() && s4 != null) {
                try {
                    string = new URI(new URI(s4), string).toString();
                }
                catch (final URI.MalformedURIException ex) {}
            }
            if (s5 == null) {
                if (s3 != null && string != null) {
                    s5 = this.resolvePublic(s3, string);
                }
                else if (string != null) {
                    s5 = this.resolveSystem(string);
                }
            }
        }
        catch (final IOException ex2) {}
        if (s5 != null) {
            return new DOMInputImpl(s3, s5, s4);
        }
        return null;
    }
    
    public XMLInputSource resolveEntity(final XMLResourceIdentifier xmlResourceIdentifier) throws XNIException, IOException {
        final String resolveIdentifier = this.resolveIdentifier(xmlResourceIdentifier);
        if (resolveIdentifier != null) {
            return new XMLInputSource(xmlResourceIdentifier.getPublicId(), resolveIdentifier, xmlResourceIdentifier.getBaseSystemId());
        }
        return null;
    }
    
    public String resolveIdentifier(final XMLResourceIdentifier xmlResourceIdentifier) throws IOException, XNIException {
        String s = null;
        final String namespace = xmlResourceIdentifier.getNamespace();
        if (namespace != null) {
            s = this.resolveURI(namespace);
        }
        if (s == null) {
            final String publicId = xmlResourceIdentifier.getPublicId();
            final String s2 = this.getUseLiteralSystemId() ? xmlResourceIdentifier.getLiteralSystemId() : xmlResourceIdentifier.getExpandedSystemId();
            if (publicId != null && s2 != null) {
                s = this.resolvePublic(publicId, s2);
            }
            else if (s2 != null) {
                s = this.resolveSystem(s2);
            }
        }
        return s;
    }
    
    public final synchronized String resolveSystem(final String s) throws IOException {
        if (this.fCatalogsChanged) {
            this.parseCatalogs();
            this.fCatalogsChanged = false;
        }
        return (this.fCatalog != null) ? this.fCatalog.resolveSystem(s) : null;
    }
    
    public final synchronized String resolvePublic(final String s, final String s2) throws IOException {
        if (this.fCatalogsChanged) {
            this.parseCatalogs();
            this.fCatalogsChanged = false;
        }
        return (this.fCatalog != null) ? this.fCatalog.resolvePublic(s, s2) : null;
    }
    
    public final synchronized String resolveURI(final String s) throws IOException {
        if (this.fCatalogsChanged) {
            this.parseCatalogs();
            this.fCatalogsChanged = false;
        }
        return (this.fCatalog != null) ? this.fCatalog.resolveURI(s) : null;
    }
    
    private void init(final String[] array, final boolean fPreferPublic) {
        this.fCatalogsList = (String[])((array != null) ? ((String[])array.clone()) : null);
        this.fPreferPublic = fPreferPublic;
        (this.fResolverCatalogManager = new CatalogManager()).setAllowOasisXMLCatalogPI(false);
        this.fResolverCatalogManager.setCatalogClassName("org.apache.xml.resolver.Catalog");
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
                final String s = this.fCatalogsList[i];
                if (s != null && s.length() > 0) {
                    this.fCatalog.parseCatalog(s);
                }
            }
        }
        else {
            this.fCatalog = null;
        }
    }
    
    private void attachReaderToCatalog(final Catalog catalog) {
        final SAXParserFactoryImpl saxParserFactoryImpl = new SAXParserFactoryImpl();
        saxParserFactoryImpl.setNamespaceAware(true);
        saxParserFactoryImpl.setValidating(false);
        final SAXCatalogReader saxCatalogReader = new SAXCatalogReader((SAXParserFactory)saxParserFactoryImpl);
        saxCatalogReader.setCatalogParser("urn:oasis:names:tc:entity:xmlns:xml:catalog", "catalog", "org.apache.xml.resolver.readers.OASISXMLCatalogReader");
        catalog.addReader("application/xml", (CatalogReader)saxCatalogReader);
    }
}
