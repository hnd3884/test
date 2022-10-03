package org.apache.xerces.impl.dtd;

import org.apache.xerces.xni.XNIException;
import org.apache.xerces.xni.NamespaceContext;
import org.apache.xerces.util.XMLSymbols;
import org.apache.xerces.xni.Augmentations;
import org.apache.xerces.xni.XMLAttributes;
import org.apache.xerces.xni.QName;

public class XMLNSDTDValidator extends XMLDTDValidator
{
    private final QName fAttributeQName;
    
    public XMLNSDTDValidator() {
        this.fAttributeQName = new QName();
    }
    
    protected final void startNamespaceScope(final QName qName, final XMLAttributes xmlAttributes, final Augmentations augmentations) throws XNIException {
        this.fNamespaceContext.pushContext();
        if (qName.prefix == XMLSymbols.PREFIX_XMLNS) {
            this.fErrorReporter.reportError("http://www.w3.org/TR/1999/REC-xml-names-19990114", "ElementXMLNSPrefix", new Object[] { qName.rawname }, (short)2);
        }
        final int length = xmlAttributes.getLength();
        for (int i = 0; i < length; ++i) {
            final String localName = xmlAttributes.getLocalName(i);
            final String prefix = xmlAttributes.getPrefix(i);
            if (prefix == XMLSymbols.PREFIX_XMLNS || (prefix == XMLSymbols.EMPTY_STRING && localName == XMLSymbols.PREFIX_XMLNS)) {
                final String addSymbol = this.fSymbolTable.addSymbol(xmlAttributes.getValue(i));
                if (prefix == XMLSymbols.PREFIX_XMLNS && localName == XMLSymbols.PREFIX_XMLNS) {
                    this.fErrorReporter.reportError("http://www.w3.org/TR/1999/REC-xml-names-19990114", "CantBindXMLNS", new Object[] { xmlAttributes.getQName(i) }, (short)2);
                }
                if (addSymbol == NamespaceContext.XMLNS_URI) {
                    this.fErrorReporter.reportError("http://www.w3.org/TR/1999/REC-xml-names-19990114", "CantBindXMLNS", new Object[] { xmlAttributes.getQName(i) }, (short)2);
                }
                if (localName == XMLSymbols.PREFIX_XML) {
                    if (addSymbol != NamespaceContext.XML_URI) {
                        this.fErrorReporter.reportError("http://www.w3.org/TR/1999/REC-xml-names-19990114", "CantBindXML", new Object[] { xmlAttributes.getQName(i) }, (short)2);
                    }
                }
                else if (addSymbol == NamespaceContext.XML_URI) {
                    this.fErrorReporter.reportError("http://www.w3.org/TR/1999/REC-xml-names-19990114", "CantBindXML", new Object[] { xmlAttributes.getQName(i) }, (short)2);
                }
                final String s = (localName != XMLSymbols.PREFIX_XMLNS) ? localName : XMLSymbols.EMPTY_STRING;
                if (addSymbol == XMLSymbols.EMPTY_STRING && localName != XMLSymbols.PREFIX_XMLNS) {
                    this.fErrorReporter.reportError("http://www.w3.org/TR/1999/REC-xml-names-19990114", "EmptyPrefixedAttName", new Object[] { xmlAttributes.getQName(i) }, (short)2);
                }
                else {
                    this.fNamespaceContext.declarePrefix(s, (addSymbol.length() != 0) ? addSymbol : null);
                }
            }
        }
        qName.uri = this.fNamespaceContext.getURI((qName.prefix != null) ? qName.prefix : XMLSymbols.EMPTY_STRING);
        if (qName.prefix == null && qName.uri != null) {
            qName.prefix = XMLSymbols.EMPTY_STRING;
        }
        if (qName.prefix != null && qName.uri == null) {
            this.fErrorReporter.reportError("http://www.w3.org/TR/1999/REC-xml-names-19990114", "ElementPrefixUnbound", new Object[] { qName.prefix, qName.rawname }, (short)2);
        }
        for (int j = 0; j < length; ++j) {
            xmlAttributes.getName(j, this.fAttributeQName);
            final String s2 = (this.fAttributeQName.prefix != null) ? this.fAttributeQName.prefix : XMLSymbols.EMPTY_STRING;
            final String rawname = this.fAttributeQName.rawname;
            if (rawname == XMLSymbols.PREFIX_XMLNS) {
                this.fAttributeQName.uri = this.fNamespaceContext.getURI(XMLSymbols.PREFIX_XMLNS);
                xmlAttributes.setName(j, this.fAttributeQName);
            }
            else if (s2 != XMLSymbols.EMPTY_STRING) {
                this.fAttributeQName.uri = this.fNamespaceContext.getURI(s2);
                if (this.fAttributeQName.uri == null) {
                    this.fErrorReporter.reportError("http://www.w3.org/TR/1999/REC-xml-names-19990114", "AttributePrefixUnbound", new Object[] { qName.rawname, rawname, s2 }, (short)2);
                }
                xmlAttributes.setName(j, this.fAttributeQName);
            }
        }
        for (int length2 = xmlAttributes.getLength(), k = 0; k < length2 - 1; ++k) {
            final String uri = xmlAttributes.getURI(k);
            if (uri != null) {
                if (uri != NamespaceContext.XMLNS_URI) {
                    final String localName2 = xmlAttributes.getLocalName(k);
                    for (int l = k + 1; l < length2; ++l) {
                        final String localName3 = xmlAttributes.getLocalName(l);
                        final String uri2 = xmlAttributes.getURI(l);
                        if (localName2 == localName3 && uri == uri2) {
                            this.fErrorReporter.reportError("http://www.w3.org/TR/1999/REC-xml-names-19990114", "AttributeNSNotUnique", new Object[] { qName.rawname, localName2, uri }, (short)2);
                        }
                    }
                }
            }
        }
    }
    
    protected void endNamespaceScope(final QName qName, final Augmentations augmentations, final boolean b) throws XNIException {
        final String prefix = (qName.prefix != null) ? qName.prefix : XMLSymbols.EMPTY_STRING;
        qName.uri = this.fNamespaceContext.getURI(prefix);
        if (qName.uri != null) {
            qName.prefix = prefix;
        }
        if (this.fDocumentHandler != null && !b) {
            this.fDocumentHandler.endElement(qName, augmentations);
        }
        this.fNamespaceContext.popContext();
    }
}
