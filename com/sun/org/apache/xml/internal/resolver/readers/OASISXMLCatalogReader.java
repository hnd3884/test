package com.sun.org.apache.xml.internal.resolver.readers;

import com.sun.org.apache.xml.internal.resolver.helpers.PublicId;
import com.sun.org.apache.xml.internal.resolver.CatalogException;
import com.sun.org.apache.xml.internal.resolver.CatalogEntry;
import java.util.Vector;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.Locator;
import java.util.Enumeration;
import java.util.Stack;
import com.sun.org.apache.xml.internal.resolver.Catalog;

public class OASISXMLCatalogReader extends SAXCatalogReader implements SAXCatalogParser
{
    protected Catalog catalog;
    public static final String namespaceName = "urn:oasis:names:tc:entity:xmlns:xml:catalog";
    public static final String tr9401NamespaceName = "urn:oasis:names:tc:entity:xmlns:tr9401:catalog";
    protected Stack baseURIStack;
    protected Stack overrideStack;
    protected Stack namespaceStack;
    
    public OASISXMLCatalogReader() {
        this.catalog = null;
        this.baseURIStack = new Stack();
        this.overrideStack = new Stack();
        this.namespaceStack = new Stack();
    }
    
    @Override
    public void setCatalog(final Catalog catalog) {
        this.catalog = catalog;
        this.debug = catalog.getCatalogManager().debug;
    }
    
    public Catalog getCatalog() {
        return this.catalog;
    }
    
    protected boolean inExtensionNamespace() {
        boolean inExtension = false;
        String ns;
        for (Enumeration elements = this.namespaceStack.elements(); !inExtension && elements.hasMoreElements(); inExtension = (ns == null || (!ns.equals("urn:oasis:names:tc:entity:xmlns:tr9401:catalog") && !ns.equals("urn:oasis:names:tc:entity:xmlns:xml:catalog")))) {
            ns = elements.nextElement();
        }
        return inExtension;
    }
    
    @Override
    public void setDocumentLocator(final Locator locator) {
    }
    
    @Override
    public void startDocument() throws SAXException {
        this.baseURIStack.push(this.catalog.getCurrentBase());
        this.overrideStack.push(this.catalog.getDefaultOverride());
    }
    
    @Override
    public void endDocument() throws SAXException {
    }
    
    @Override
    public void startElement(final String namespaceURI, final String localName, final String qName, final Attributes atts) throws SAXException {
        int entryType = -1;
        Vector entryArgs = new Vector();
        this.namespaceStack.push(namespaceURI);
        final boolean inExtension = this.inExtensionNamespace();
        if (namespaceURI != null && "urn:oasis:names:tc:entity:xmlns:xml:catalog".equals(namespaceURI) && !inExtension) {
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
            if ((localName.equals("catalog") || localName.equals("group")) && atts.getValue("prefer") != null) {
                String override = atts.getValue("prefer");
                if (override.equals("public")) {
                    override = "yes";
                }
                else if (override.equals("system")) {
                    override = "no";
                }
                else {
                    this.debug.message(1, "Invalid prefer: must be 'system' or 'public'", localName);
                    override = this.catalog.getDefaultOverride();
                }
                entryType = Catalog.OVERRIDE;
                entryArgs.add(override);
                this.overrideStack.push(override);
                this.debug.message(4, "override", override);
                try {
                    final CatalogEntry ce = new CatalogEntry(entryType, entryArgs);
                    this.catalog.addEntry(ce);
                }
                catch (final CatalogException cex) {
                    if (cex.getExceptionType() == 3) {
                        this.debug.message(1, "Invalid catalog entry type", localName);
                    }
                    else if (cex.getExceptionType() == 2) {
                        this.debug.message(1, "Invalid catalog entry (override)", localName);
                    }
                }
                entryType = -1;
                entryArgs = new Vector();
            }
            else {
                this.overrideStack.push(this.overrideStack.peek());
            }
            if (localName.equals("delegatePublic")) {
                if (this.checkAttributes(atts, "publicIdStartString", "catalog")) {
                    entryType = Catalog.DELEGATE_PUBLIC;
                    entryArgs.add(atts.getValue("publicIdStartString"));
                    entryArgs.add(atts.getValue("catalog"));
                    this.debug.message(4, "delegatePublic", PublicId.normalize(atts.getValue("publicIdStartString")), atts.getValue("catalog"));
                }
            }
            else if (localName.equals("delegateSystem")) {
                if (this.checkAttributes(atts, "systemIdStartString", "catalog")) {
                    entryType = Catalog.DELEGATE_SYSTEM;
                    entryArgs.add(atts.getValue("systemIdStartString"));
                    entryArgs.add(atts.getValue("catalog"));
                    this.debug.message(4, "delegateSystem", atts.getValue("systemIdStartString"), atts.getValue("catalog"));
                }
            }
            else if (localName.equals("delegateURI")) {
                if (this.checkAttributes(atts, "uriStartString", "catalog")) {
                    entryType = Catalog.DELEGATE_URI;
                    entryArgs.add(atts.getValue("uriStartString"));
                    entryArgs.add(atts.getValue("catalog"));
                    this.debug.message(4, "delegateURI", atts.getValue("uriStartString"), atts.getValue("catalog"));
                }
            }
            else if (localName.equals("rewriteSystem")) {
                if (this.checkAttributes(atts, "systemIdStartString", "rewritePrefix")) {
                    entryType = Catalog.REWRITE_SYSTEM;
                    entryArgs.add(atts.getValue("systemIdStartString"));
                    entryArgs.add(atts.getValue("rewritePrefix"));
                    this.debug.message(4, "rewriteSystem", atts.getValue("systemIdStartString"), atts.getValue("rewritePrefix"));
                }
            }
            else if (localName.equals("systemSuffix")) {
                if (this.checkAttributes(atts, "systemIdSuffix", "uri")) {
                    entryType = Catalog.SYSTEM_SUFFIX;
                    entryArgs.add(atts.getValue("systemIdSuffix"));
                    entryArgs.add(atts.getValue("uri"));
                    this.debug.message(4, "systemSuffix", atts.getValue("systemIdSuffix"), atts.getValue("uri"));
                }
            }
            else if (localName.equals("rewriteURI")) {
                if (this.checkAttributes(atts, "uriStartString", "rewritePrefix")) {
                    entryType = Catalog.REWRITE_URI;
                    entryArgs.add(atts.getValue("uriStartString"));
                    entryArgs.add(atts.getValue("rewritePrefix"));
                    this.debug.message(4, "rewriteURI", atts.getValue("uriStartString"), atts.getValue("rewritePrefix"));
                }
            }
            else if (localName.equals("uriSuffix")) {
                if (this.checkAttributes(atts, "uriSuffix", "uri")) {
                    entryType = Catalog.URI_SUFFIX;
                    entryArgs.add(atts.getValue("uriSuffix"));
                    entryArgs.add(atts.getValue("uri"));
                    this.debug.message(4, "uriSuffix", atts.getValue("uriSuffix"), atts.getValue("uri"));
                }
            }
            else if (localName.equals("nextCatalog")) {
                if (this.checkAttributes(atts, "catalog")) {
                    entryType = Catalog.CATALOG;
                    entryArgs.add(atts.getValue("catalog"));
                    this.debug.message(4, "nextCatalog", atts.getValue("catalog"));
                }
            }
            else if (localName.equals("public")) {
                if (this.checkAttributes(atts, "publicId", "uri")) {
                    entryType = Catalog.PUBLIC;
                    entryArgs.add(atts.getValue("publicId"));
                    entryArgs.add(atts.getValue("uri"));
                    this.debug.message(4, "public", PublicId.normalize(atts.getValue("publicId")), atts.getValue("uri"));
                }
            }
            else if (localName.equals("system")) {
                if (this.checkAttributes(atts, "systemId", "uri")) {
                    entryType = Catalog.SYSTEM;
                    entryArgs.add(atts.getValue("systemId"));
                    entryArgs.add(atts.getValue("uri"));
                    this.debug.message(4, "system", atts.getValue("systemId"), atts.getValue("uri"));
                }
            }
            else if (localName.equals("uri")) {
                if (this.checkAttributes(atts, "name", "uri")) {
                    entryType = Catalog.URI;
                    entryArgs.add(atts.getValue("name"));
                    entryArgs.add(atts.getValue("uri"));
                    this.debug.message(4, "uri", atts.getValue("name"), atts.getValue("uri"));
                }
            }
            else if (!localName.equals("catalog")) {
                if (!localName.equals("group")) {
                    this.debug.message(1, "Invalid catalog entry type", localName);
                }
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
        if (namespaceURI != null && "urn:oasis:names:tc:entity:xmlns:tr9401:catalog".equals(namespaceURI) && !inExtension) {
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
            if (localName.equals("doctype")) {
                final Catalog catalog = this.catalog;
                entryType = Catalog.DOCTYPE;
                entryArgs.add(atts.getValue("name"));
                entryArgs.add(atts.getValue("uri"));
            }
            else if (localName.equals("document")) {
                final Catalog catalog2 = this.catalog;
                entryType = Catalog.DOCUMENT;
                entryArgs.add(atts.getValue("uri"));
            }
            else if (localName.equals("dtddecl")) {
                final Catalog catalog3 = this.catalog;
                entryType = Catalog.DTDDECL;
                entryArgs.add(atts.getValue("publicId"));
                entryArgs.add(atts.getValue("uri"));
            }
            else if (localName.equals("entity")) {
                entryType = Catalog.ENTITY;
                entryArgs.add(atts.getValue("name"));
                entryArgs.add(atts.getValue("uri"));
            }
            else if (localName.equals("linktype")) {
                entryType = Catalog.LINKTYPE;
                entryArgs.add(atts.getValue("name"));
                entryArgs.add(atts.getValue("uri"));
            }
            else if (localName.equals("notation")) {
                entryType = Catalog.NOTATION;
                entryArgs.add(atts.getValue("name"));
                entryArgs.add(atts.getValue("uri"));
            }
            else if (localName.equals("sgmldecl")) {
                entryType = Catalog.SGMLDECL;
                entryArgs.add(atts.getValue("uri"));
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
    
    public boolean checkAttributes(final Attributes atts, final String attName) {
        if (atts.getValue(attName) == null) {
            this.debug.message(1, "Error: required attribute " + attName + " missing.");
            return false;
        }
        return true;
    }
    
    public boolean checkAttributes(final Attributes atts, final String attName1, final String attName2) {
        return this.checkAttributes(atts, attName1) && this.checkAttributes(atts, attName2);
    }
    
    @Override
    public void endElement(final String namespaceURI, final String localName, final String qName) throws SAXException {
        int entryType = -1;
        final Vector entryArgs = new Vector();
        final boolean inExtension = this.inExtensionNamespace();
        if (namespaceURI != null && !inExtension && ("urn:oasis:names:tc:entity:xmlns:xml:catalog".equals(namespaceURI) || "urn:oasis:names:tc:entity:xmlns:tr9401:catalog".equals(namespaceURI))) {
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
        if (namespaceURI != null && "urn:oasis:names:tc:entity:xmlns:xml:catalog".equals(namespaceURI) && !inExtension && (localName.equals("catalog") || localName.equals("group"))) {
            final String popOverride = this.overrideStack.pop();
            final String override = this.overrideStack.peek();
            if (!override.equals(popOverride)) {
                final Catalog catalog2 = this.catalog;
                entryType = Catalog.OVERRIDE;
                entryArgs.add(override);
                this.overrideStack.push(override);
                this.debug.message(4, "(reset) override", override);
                try {
                    final CatalogEntry ce = new CatalogEntry(entryType, entryArgs);
                    this.catalog.addEntry(ce);
                }
                catch (final CatalogException cex) {
                    if (cex.getExceptionType() == 3) {
                        this.debug.message(1, "Invalid catalog entry type", localName);
                    }
                    else if (cex.getExceptionType() == 2) {
                        this.debug.message(1, "Invalid catalog entry (roverride)", localName);
                    }
                }
            }
        }
        this.namespaceStack.pop();
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
    
    @Override
    public void skippedEntity(final String name) throws SAXException {
    }
    
    @Override
    public void startPrefixMapping(final String prefix, final String uri) throws SAXException {
    }
    
    @Override
    public void endPrefixMapping(final String prefix) throws SAXException {
    }
}
