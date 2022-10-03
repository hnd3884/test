package com.sun.org.apache.xml.internal.resolver.readers;

import com.sun.org.apache.xml.internal.resolver.CatalogException;
import com.sun.org.apache.xml.internal.resolver.CatalogEntry;
import com.sun.org.apache.xml.internal.resolver.helpers.PublicId;
import java.util.Vector;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.Locator;
import javax.xml.parsers.SAXParserFactory;
import com.sun.org.apache.xml.internal.resolver.Catalog;

public class XCatalogReader extends SAXCatalogReader implements SAXCatalogParser
{
    protected Catalog catalog;
    
    @Override
    public void setCatalog(final Catalog catalog) {
        this.catalog = catalog;
    }
    
    public Catalog getCatalog() {
        return this.catalog;
    }
    
    public XCatalogReader(final SAXParserFactory parserFactory) {
        super(parserFactory);
        this.catalog = null;
    }
    
    @Override
    public void setDocumentLocator(final Locator locator) {
    }
    
    @Override
    public void startDocument() throws SAXException {
    }
    
    @Override
    public void endDocument() throws SAXException {
    }
    
    @Override
    public void startElement(final String namespaceURI, final String localName, final String qName, final Attributes atts) throws SAXException {
        int entryType = -1;
        final Vector entryArgs = new Vector();
        if (localName.equals("Base")) {
            final Catalog catalog = this.catalog;
            entryType = Catalog.BASE;
            entryArgs.add(atts.getValue("HRef"));
            this.catalog.getCatalogManager().debug.message(4, "Base", atts.getValue("HRef"));
        }
        else if (localName.equals("Delegate")) {
            final Catalog catalog2 = this.catalog;
            entryType = Catalog.DELEGATE_PUBLIC;
            entryArgs.add(atts.getValue("PublicId"));
            entryArgs.add(atts.getValue("HRef"));
            this.catalog.getCatalogManager().debug.message(4, "Delegate", PublicId.normalize(atts.getValue("PublicId")), atts.getValue("HRef"));
        }
        else if (localName.equals("Extend")) {
            final Catalog catalog3 = this.catalog;
            entryType = Catalog.CATALOG;
            entryArgs.add(atts.getValue("HRef"));
            this.catalog.getCatalogManager().debug.message(4, "Extend", atts.getValue("HRef"));
        }
        else if (localName.equals("Map")) {
            final Catalog catalog4 = this.catalog;
            entryType = Catalog.PUBLIC;
            entryArgs.add(atts.getValue("PublicId"));
            entryArgs.add(atts.getValue("HRef"));
            this.catalog.getCatalogManager().debug.message(4, "Map", PublicId.normalize(atts.getValue("PublicId")), atts.getValue("HRef"));
        }
        else if (localName.equals("Remap")) {
            final Catalog catalog5 = this.catalog;
            entryType = Catalog.SYSTEM;
            entryArgs.add(atts.getValue("SystemId"));
            entryArgs.add(atts.getValue("HRef"));
            this.catalog.getCatalogManager().debug.message(4, "Remap", atts.getValue("SystemId"), atts.getValue("HRef"));
        }
        else if (!localName.equals("XMLCatalog")) {
            this.catalog.getCatalogManager().debug.message(1, "Invalid catalog entry type", localName);
        }
        if (entryType >= 0) {
            try {
                final CatalogEntry ce = new CatalogEntry(entryType, entryArgs);
                this.catalog.addEntry(ce);
            }
            catch (final CatalogException cex) {
                if (cex.getExceptionType() == 3) {
                    this.catalog.getCatalogManager().debug.message(1, "Invalid catalog entry type", localName);
                }
                else if (cex.getExceptionType() == 2) {
                    this.catalog.getCatalogManager().debug.message(1, "Invalid catalog entry", localName);
                }
            }
        }
    }
    
    @Override
    public void endElement(final String namespaceURI, final String localName, final String qName) throws SAXException {
    }
    
    @Override
    public void characters(final char[] ch, final int start, final int length) throws SAXException {
    }
    
    @Override
    public void ignorableWhitespace(final char[] ch, final int start, final int length) throws SAXException {
    }
    
    @Override
    public void processingInstruction(final String target, final String data) throws SAXException {
    }
}
