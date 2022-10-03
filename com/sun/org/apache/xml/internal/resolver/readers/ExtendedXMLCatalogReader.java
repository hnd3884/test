package com.sun.org.apache.xml.internal.resolver.readers;

import org.xml.sax.SAXException;
import com.sun.org.apache.xml.internal.resolver.Resolver;
import com.sun.org.apache.xml.internal.resolver.CatalogException;
import com.sun.org.apache.xml.internal.resolver.CatalogEntry;
import com.sun.org.apache.xml.internal.resolver.Catalog;
import java.util.Vector;
import org.xml.sax.Attributes;

public class ExtendedXMLCatalogReader extends OASISXMLCatalogReader
{
    public static final String extendedNamespaceName = "http://nwalsh.com/xcatalog/1.0";
    
    @Override
    public void startElement(final String namespaceURI, final String localName, final String qName, final Attributes atts) throws SAXException {
        final boolean inExtension = this.inExtensionNamespace();
        super.startElement(namespaceURI, localName, qName, atts);
        int entryType = -1;
        Vector entryArgs = new Vector();
        if (namespaceURI != null && "http://nwalsh.com/xcatalog/1.0".equals(namespaceURI) && !inExtension) {
            if (atts.getValue("xml:base") != null) {
                final String baseURI = atts.getValue("xml:base");
                entryType = Catalog.BASE;
                entryArgs.add(baseURI);
                this.baseURIStack.push(baseURI);
                this.debug.message(4, "xml:base", baseURI);
                try {
                    final CatalogEntry ce = new CatalogEntry(entryType, entryArgs);
                    this.catalog.addEntry(ce);
                }
                catch (final CatalogException cex) {
                    if (cex.getExceptionType() == 3) {
                        this.debug.message(1, "Invalid catalog entry type", localName);
                    }
                    else if (cex.getExceptionType() == 2) {
                        this.debug.message(1, "Invalid catalog entry (base)", localName);
                    }
                }
                entryType = -1;
                entryArgs = new Vector();
            }
            else {
                this.baseURIStack.push(this.baseURIStack.peek());
            }
            if (localName.equals("uriSuffix")) {
                if (this.checkAttributes(atts, "suffix", "uri")) {
                    entryType = Resolver.URISUFFIX;
                    entryArgs.add(atts.getValue("suffix"));
                    entryArgs.add(atts.getValue("uri"));
                    this.debug.message(4, "uriSuffix", atts.getValue("suffix"), atts.getValue("uri"));
                }
            }
            else if (localName.equals("systemSuffix")) {
                if (this.checkAttributes(atts, "suffix", "uri")) {
                    entryType = Resolver.SYSTEMSUFFIX;
                    entryArgs.add(atts.getValue("suffix"));
                    entryArgs.add(atts.getValue("uri"));
                    this.debug.message(4, "systemSuffix", atts.getValue("suffix"), atts.getValue("uri"));
                }
            }
            else {
                this.debug.message(1, "Invalid catalog entry type", localName);
            }
            if (entryType >= 0) {
                try {
                    final CatalogEntry ce2 = new CatalogEntry(entryType, entryArgs);
                    this.catalog.addEntry(ce2);
                }
                catch (final CatalogException cex2) {
                    if (cex2.getExceptionType() == 3) {
                        this.debug.message(1, "Invalid catalog entry type", localName);
                    }
                    else if (cex2.getExceptionType() == 2) {
                        this.debug.message(1, "Invalid catalog entry", localName);
                    }
                }
            }
        }
    }
    
    @Override
    public void endElement(final String namespaceURI, final String localName, final String qName) throws SAXException {
        super.endElement(namespaceURI, localName, qName);
        final boolean inExtension = this.inExtensionNamespace();
        int entryType = -1;
        final Vector entryArgs = new Vector();
        if (namespaceURI != null && "http://nwalsh.com/xcatalog/1.0".equals(namespaceURI) && !inExtension) {
            final String popURI = this.baseURIStack.pop();
            final String baseURI = this.baseURIStack.peek();
            if (!baseURI.equals(popURI)) {
                final Catalog catalog = this.catalog;
                entryType = Catalog.BASE;
                entryArgs.add(baseURI);
                this.debug.message(4, "(reset) xml:base", baseURI);
                try {
                    final CatalogEntry ce = new CatalogEntry(entryType, entryArgs);
                    this.catalog.addEntry(ce);
                }
                catch (final CatalogException cex) {
                    if (cex.getExceptionType() == 3) {
                        this.debug.message(1, "Invalid catalog entry type", localName);
                    }
                    else if (cex.getExceptionType() == 2) {
                        this.debug.message(1, "Invalid catalog entry (rbase)", localName);
                    }
                }
            }
        }
    }
}
