package com.sun.org.apache.xerces.internal.impl.dtd;

import com.sun.org.apache.xerces.internal.xni.XNIException;
import com.sun.org.apache.xerces.internal.xni.NamespaceContext;
import com.sun.org.apache.xerces.internal.util.XMLSymbols;
import com.sun.org.apache.xerces.internal.xni.Augmentations;
import com.sun.org.apache.xerces.internal.xni.XMLAttributes;
import com.sun.org.apache.xerces.internal.xni.QName;

public class XML11NSDTDValidator extends XML11DTDValidator
{
    private QName fAttributeQName;
    
    public XML11NSDTDValidator() {
        this.fAttributeQName = new QName();
    }
    
    @Override
    protected final void startNamespaceScope(final QName element, final XMLAttributes attributes, final Augmentations augs) throws XNIException {
        this.fNamespaceContext.pushContext();
        if (element.prefix == XMLSymbols.PREFIX_XMLNS) {
            this.fErrorReporter.reportError("http://www.w3.org/TR/1999/REC-xml-names-19990114", "ElementXMLNSPrefix", new Object[] { element.rawname }, (short)2);
        }
        final int length = attributes.getLength();
        for (int i = 0; i < length; ++i) {
            final String localpart = attributes.getLocalName(i);
            String prefix = attributes.getPrefix(i);
            if (prefix == XMLSymbols.PREFIX_XMLNS || (prefix == XMLSymbols.EMPTY_STRING && localpart == XMLSymbols.PREFIX_XMLNS)) {
                final String uri = this.fSymbolTable.addSymbol(attributes.getValue(i));
                if (prefix == XMLSymbols.PREFIX_XMLNS && localpart == XMLSymbols.PREFIX_XMLNS) {
                    this.fErrorReporter.reportError("http://www.w3.org/TR/1999/REC-xml-names-19990114", "CantBindXMLNS", new Object[] { attributes.getQName(i) }, (short)2);
                }
                if (uri == NamespaceContext.XMLNS_URI) {
                    this.fErrorReporter.reportError("http://www.w3.org/TR/1999/REC-xml-names-19990114", "CantBindXMLNS", new Object[] { attributes.getQName(i) }, (short)2);
                }
                if (localpart == XMLSymbols.PREFIX_XML) {
                    if (uri != NamespaceContext.XML_URI) {
                        this.fErrorReporter.reportError("http://www.w3.org/TR/1999/REC-xml-names-19990114", "CantBindXML", new Object[] { attributes.getQName(i) }, (short)2);
                    }
                }
                else if (uri == NamespaceContext.XML_URI) {
                    this.fErrorReporter.reportError("http://www.w3.org/TR/1999/REC-xml-names-19990114", "CantBindXML", new Object[] { attributes.getQName(i) }, (short)2);
                }
                prefix = ((localpart != XMLSymbols.PREFIX_XMLNS) ? localpart : XMLSymbols.EMPTY_STRING);
                this.fNamespaceContext.declarePrefix(prefix, (uri.length() != 0) ? uri : null);
            }
        }
        final String prefix2 = (element.prefix != null) ? element.prefix : XMLSymbols.EMPTY_STRING;
        element.uri = this.fNamespaceContext.getURI(prefix2);
        if (element.prefix == null && element.uri != null) {
            element.prefix = XMLSymbols.EMPTY_STRING;
        }
        if (element.prefix != null && element.uri == null) {
            this.fErrorReporter.reportError("http://www.w3.org/TR/1999/REC-xml-names-19990114", "ElementPrefixUnbound", new Object[] { element.prefix, element.rawname }, (short)2);
        }
        for (int j = 0; j < length; ++j) {
            attributes.getName(j, this.fAttributeQName);
            final String aprefix = (this.fAttributeQName.prefix != null) ? this.fAttributeQName.prefix : XMLSymbols.EMPTY_STRING;
            final String arawname = this.fAttributeQName.rawname;
            if (arawname == XMLSymbols.PREFIX_XMLNS) {
                this.fAttributeQName.uri = this.fNamespaceContext.getURI(XMLSymbols.PREFIX_XMLNS);
                attributes.setName(j, this.fAttributeQName);
            }
            else if (aprefix != XMLSymbols.EMPTY_STRING) {
                this.fAttributeQName.uri = this.fNamespaceContext.getURI(aprefix);
                if (this.fAttributeQName.uri == null) {
                    this.fErrorReporter.reportError("http://www.w3.org/TR/1999/REC-xml-names-19990114", "AttributePrefixUnbound", new Object[] { element.rawname, arawname, aprefix }, (short)2);
                }
                attributes.setName(j, this.fAttributeQName);
            }
        }
        for (int attrCount = attributes.getLength(), k = 0; k < attrCount - 1; ++k) {
            final String auri = attributes.getURI(k);
            if (auri != null) {
                if (auri != NamespaceContext.XMLNS_URI) {
                    final String alocalpart = attributes.getLocalName(k);
                    for (int l = k + 1; l < attrCount; ++l) {
                        final String blocalpart = attributes.getLocalName(l);
                        final String buri = attributes.getURI(l);
                        if (alocalpart == blocalpart && auri == buri) {
                            this.fErrorReporter.reportError("http://www.w3.org/TR/1999/REC-xml-names-19990114", "AttributeNSNotUnique", new Object[] { element.rawname, alocalpart, auri }, (short)2);
                        }
                    }
                }
            }
        }
    }
    
    @Override
    protected void endNamespaceScope(final QName element, final Augmentations augs, final boolean isEmpty) throws XNIException {
        final String eprefix = (element.prefix != null) ? element.prefix : XMLSymbols.EMPTY_STRING;
        element.uri = this.fNamespaceContext.getURI(eprefix);
        if (element.uri != null) {
            element.prefix = eprefix;
        }
        if (this.fDocumentHandler != null && !isEmpty) {
            this.fDocumentHandler.endElement(element, augs);
        }
        this.fNamespaceContext.popContext();
    }
}
