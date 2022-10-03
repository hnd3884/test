package com.sun.org.apache.xerces.internal.impl;

import com.sun.org.apache.xerces.internal.xni.XMLDocumentHandler;
import com.sun.org.apache.xerces.internal.xni.parser.XMLDocumentSource;
import com.sun.org.apache.xerces.internal.xni.parser.XMLConfigurationException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponentManager;
import com.sun.org.apache.xerces.internal.xni.NamespaceContext;
import com.sun.org.apache.xerces.internal.utils.XMLSecurityManager;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import java.io.IOException;
import com.sun.org.apache.xerces.internal.xni.QName;
import com.sun.org.apache.xerces.internal.xni.Augmentations;
import com.sun.org.apache.xerces.internal.xni.XMLAttributes;
import com.sun.org.apache.xerces.internal.util.XMLSymbols;
import com.sun.org.apache.xerces.internal.util.XMLAttributesImpl;
import com.sun.org.apache.xerces.internal.impl.dtd.XMLDTDValidatorFilter;

public class XML11NSDocumentScannerImpl extends XML11DocumentScannerImpl
{
    protected boolean fBindNamespaces;
    protected boolean fPerformValidation;
    private XMLDTDValidatorFilter fDTDValidator;
    private boolean fSawSpace;
    
    public void setDTDValidator(final XMLDTDValidatorFilter validator) {
        this.fDTDValidator = validator;
    }
    
    @Override
    protected boolean scanStartElement() throws IOException, XNIException {
        this.fEntityScanner.scanQName(this.fElementQName, NameType.ELEMENTSTART);
        final String rawname = this.fElementQName.rawname;
        if (this.fBindNamespaces) {
            this.fNamespaceContext.pushContext();
            if (this.fScannerState == 26 && this.fPerformValidation) {
                this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_GRAMMAR_NOT_FOUND", new Object[] { rawname }, (short)1);
                if (this.fDoctypeName == null || !this.fDoctypeName.equals(rawname)) {
                    this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "RootElementTypeMustMatchDoctypedecl", new Object[] { this.fDoctypeName, rawname }, (short)1);
                }
            }
        }
        this.fCurrentElement = this.fElementStack.pushElement(this.fElementQName);
        boolean empty = false;
        this.fAttributes.removeAllAttributes();
        while (true) {
            final boolean sawSpace = this.fEntityScanner.skipSpaces();
            final int c = this.fEntityScanner.peekChar();
            if (c == 62) {
                this.fEntityScanner.scanChar(null);
                break;
            }
            if (c == 47) {
                this.fEntityScanner.scanChar(null);
                if (!this.fEntityScanner.skipChar(62, null)) {
                    this.reportFatalError("ElementUnterminated", new Object[] { rawname });
                }
                empty = true;
                break;
            }
            if ((!this.isValidNameStartChar(c) || !sawSpace) && (!this.isValidNameStartHighSurrogate(c) || !sawSpace)) {
                this.reportFatalError("ElementUnterminated", new Object[] { rawname });
            }
            this.scanAttribute(this.fAttributes);
            if (this.fSecurityManager == null || this.fSecurityManager.isNoLimit(this.fElementAttributeLimit) || this.fAttributes.getLength() <= this.fElementAttributeLimit) {
                continue;
            }
            this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "ElementAttributeLimit", new Object[] { rawname, new Integer(this.fElementAttributeLimit) }, (short)2);
        }
        if (this.fBindNamespaces) {
            if (this.fElementQName.prefix == XMLSymbols.PREFIX_XMLNS) {
                this.fErrorReporter.reportError("http://www.w3.org/TR/1999/REC-xml-names-19990114", "ElementXMLNSPrefix", new Object[] { this.fElementQName.rawname }, (short)2);
            }
            final String prefix = (this.fElementQName.prefix != null) ? this.fElementQName.prefix : XMLSymbols.EMPTY_STRING;
            this.fElementQName.uri = this.fNamespaceContext.getURI(prefix);
            this.fCurrentElement.uri = this.fElementQName.uri;
            if (this.fElementQName.prefix == null && this.fElementQName.uri != null) {
                this.fElementQName.prefix = XMLSymbols.EMPTY_STRING;
                this.fCurrentElement.prefix = XMLSymbols.EMPTY_STRING;
            }
            if (this.fElementQName.prefix != null && this.fElementQName.uri == null) {
                this.fErrorReporter.reportError("http://www.w3.org/TR/1999/REC-xml-names-19990114", "ElementPrefixUnbound", new Object[] { this.fElementQName.prefix, this.fElementQName.rawname }, (short)2);
            }
            final int length = this.fAttributes.getLength();
            for (int i = 0; i < length; ++i) {
                this.fAttributes.getName(i, this.fAttributeQName);
                final String aprefix = (this.fAttributeQName.prefix != null) ? this.fAttributeQName.prefix : XMLSymbols.EMPTY_STRING;
                final String uri = this.fNamespaceContext.getURI(aprefix);
                if (this.fAttributeQName.uri == null || this.fAttributeQName.uri != uri) {
                    if (aprefix != XMLSymbols.EMPTY_STRING) {
                        if ((this.fAttributeQName.uri = uri) == null) {
                            this.fErrorReporter.reportError("http://www.w3.org/TR/1999/REC-xml-names-19990114", "AttributePrefixUnbound", new Object[] { this.fElementQName.rawname, this.fAttributeQName.rawname, aprefix }, (short)2);
                        }
                        this.fAttributes.setURI(i, uri);
                    }
                }
            }
            if (length > 1) {
                final QName name = this.fAttributes.checkDuplicatesNS();
                if (name != null) {
                    if (name.uri != null) {
                        this.fErrorReporter.reportError("http://www.w3.org/TR/1999/REC-xml-names-19990114", "AttributeNSNotUnique", new Object[] { this.fElementQName.rawname, name.localpart, name.uri }, (short)2);
                    }
                    else {
                        this.fErrorReporter.reportError("http://www.w3.org/TR/1999/REC-xml-names-19990114", "AttributeNotUnique", new Object[] { this.fElementQName.rawname, name.rawname }, (short)2);
                    }
                }
            }
        }
        if (empty) {
            --this.fMarkupDepth;
            if (this.fMarkupDepth < this.fEntityStack[this.fEntityDepth - 1]) {
                this.reportFatalError("ElementEntityMismatch", new Object[] { this.fCurrentElement.rawname });
            }
            if (this.fDocumentHandler != null) {
                this.fDocumentHandler.emptyElement(this.fElementQName, this.fAttributes, null);
            }
            this.fScanEndElement = true;
            this.fElementStack.popElement();
        }
        else {
            if (this.dtdGrammarUtil != null) {
                this.dtdGrammarUtil.startElement(this.fElementQName, this.fAttributes);
            }
            if (this.fDocumentHandler != null) {
                this.fDocumentHandler.startElement(this.fElementQName, this.fAttributes, null);
            }
        }
        return empty;
    }
    
    protected void scanStartElementName() throws IOException, XNIException {
        this.fEntityScanner.scanQName(this.fElementQName, NameType.ELEMENTSTART);
        this.fSawSpace = this.fEntityScanner.skipSpaces();
    }
    
    protected boolean scanStartElementAfterName() throws IOException, XNIException {
        final String rawname = this.fElementQName.rawname;
        if (this.fBindNamespaces) {
            this.fNamespaceContext.pushContext();
            if (this.fScannerState == 26 && this.fPerformValidation) {
                this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_GRAMMAR_NOT_FOUND", new Object[] { rawname }, (short)1);
                if (this.fDoctypeName == null || !this.fDoctypeName.equals(rawname)) {
                    this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "RootElementTypeMustMatchDoctypedecl", new Object[] { this.fDoctypeName, rawname }, (short)1);
                }
            }
        }
        this.fCurrentElement = this.fElementStack.pushElement(this.fElementQName);
        boolean empty = false;
        this.fAttributes.removeAllAttributes();
        while (true) {
            final int c = this.fEntityScanner.peekChar();
            if (c == 62) {
                this.fEntityScanner.scanChar(null);
                break;
            }
            if (c == 47) {
                this.fEntityScanner.scanChar(null);
                if (!this.fEntityScanner.skipChar(62, null)) {
                    this.reportFatalError("ElementUnterminated", new Object[] { rawname });
                }
                empty = true;
                break;
            }
            if ((!this.isValidNameStartChar(c) || !this.fSawSpace) && (!this.isValidNameStartHighSurrogate(c) || !this.fSawSpace)) {
                this.reportFatalError("ElementUnterminated", new Object[] { rawname });
            }
            this.scanAttribute(this.fAttributes);
            this.fSawSpace = this.fEntityScanner.skipSpaces();
        }
        if (this.fBindNamespaces) {
            if (this.fElementQName.prefix == XMLSymbols.PREFIX_XMLNS) {
                this.fErrorReporter.reportError("http://www.w3.org/TR/1999/REC-xml-names-19990114", "ElementXMLNSPrefix", new Object[] { this.fElementQName.rawname }, (short)2);
            }
            final String prefix = (this.fElementQName.prefix != null) ? this.fElementQName.prefix : XMLSymbols.EMPTY_STRING;
            this.fElementQName.uri = this.fNamespaceContext.getURI(prefix);
            this.fCurrentElement.uri = this.fElementQName.uri;
            if (this.fElementQName.prefix == null && this.fElementQName.uri != null) {
                this.fElementQName.prefix = XMLSymbols.EMPTY_STRING;
                this.fCurrentElement.prefix = XMLSymbols.EMPTY_STRING;
            }
            if (this.fElementQName.prefix != null && this.fElementQName.uri == null) {
                this.fErrorReporter.reportError("http://www.w3.org/TR/1999/REC-xml-names-19990114", "ElementPrefixUnbound", new Object[] { this.fElementQName.prefix, this.fElementQName.rawname }, (short)2);
            }
            final int length = this.fAttributes.getLength();
            for (int i = 0; i < length; ++i) {
                this.fAttributes.getName(i, this.fAttributeQName);
                final String aprefix = (this.fAttributeQName.prefix != null) ? this.fAttributeQName.prefix : XMLSymbols.EMPTY_STRING;
                final String uri = this.fNamespaceContext.getURI(aprefix);
                if (this.fAttributeQName.uri == null || this.fAttributeQName.uri != uri) {
                    if (aprefix != XMLSymbols.EMPTY_STRING) {
                        if ((this.fAttributeQName.uri = uri) == null) {
                            this.fErrorReporter.reportError("http://www.w3.org/TR/1999/REC-xml-names-19990114", "AttributePrefixUnbound", new Object[] { this.fElementQName.rawname, this.fAttributeQName.rawname, aprefix }, (short)2);
                        }
                        this.fAttributes.setURI(i, uri);
                    }
                }
            }
            if (length > 1) {
                final QName name = this.fAttributes.checkDuplicatesNS();
                if (name != null) {
                    if (name.uri != null) {
                        this.fErrorReporter.reportError("http://www.w3.org/TR/1999/REC-xml-names-19990114", "AttributeNSNotUnique", new Object[] { this.fElementQName.rawname, name.localpart, name.uri }, (short)2);
                    }
                    else {
                        this.fErrorReporter.reportError("http://www.w3.org/TR/1999/REC-xml-names-19990114", "AttributeNotUnique", new Object[] { this.fElementQName.rawname, name.rawname }, (short)2);
                    }
                }
            }
        }
        if (this.fDocumentHandler != null) {
            if (empty) {
                --this.fMarkupDepth;
                if (this.fMarkupDepth < this.fEntityStack[this.fEntityDepth - 1]) {
                    this.reportFatalError("ElementEntityMismatch", new Object[] { this.fCurrentElement.rawname });
                }
                this.fDocumentHandler.emptyElement(this.fElementQName, this.fAttributes, null);
                if (this.fBindNamespaces) {
                    this.fNamespaceContext.popContext();
                }
                this.fElementStack.popElement();
            }
            else {
                this.fDocumentHandler.startElement(this.fElementQName, this.fAttributes, null);
            }
        }
        return empty;
    }
    
    protected void scanAttribute(final XMLAttributesImpl attributes) throws IOException, XNIException {
        this.fEntityScanner.scanQName(this.fAttributeQName, NameType.ATTRIBUTENAME);
        this.fEntityScanner.skipSpaces();
        if (!this.fEntityScanner.skipChar(61, NameType.ATTRIBUTE)) {
            this.reportFatalError("EqRequiredInAttribute", new Object[] { this.fCurrentElement.rawname, this.fAttributeQName.rawname });
        }
        this.fEntityScanner.skipSpaces();
        int attrIndex;
        if (this.fBindNamespaces) {
            attrIndex = attributes.getLength();
            attributes.addAttributeNS(this.fAttributeQName, XMLSymbols.fCDATASymbol, null);
        }
        else {
            final int oldLen = attributes.getLength();
            attrIndex = attributes.addAttribute(this.fAttributeQName, XMLSymbols.fCDATASymbol, null);
            if (oldLen == attributes.getLength()) {
                this.reportFatalError("AttributeNotUnique", new Object[] { this.fCurrentElement.rawname, this.fAttributeQName.rawname });
            }
        }
        final boolean isVC = this.fHasExternalDTD && !this.fStandalone;
        final String localpart = this.fAttributeQName.localpart;
        String prefix = (this.fAttributeQName.prefix != null) ? this.fAttributeQName.prefix : XMLSymbols.EMPTY_STRING;
        final boolean isNSDecl = this.fBindNamespaces & (prefix == XMLSymbols.PREFIX_XMLNS || (prefix == XMLSymbols.EMPTY_STRING && localpart == XMLSymbols.PREFIX_XMLNS));
        this.scanAttributeValue(this.fTempString, this.fTempString2, this.fAttributeQName.rawname, isVC, this.fCurrentElement.rawname, isNSDecl);
        final String value = this.fTempString.toString();
        attributes.setValue(attrIndex, value);
        attributes.setNonNormalizedValue(attrIndex, this.fTempString2.toString());
        attributes.setSpecified(attrIndex, true);
        if (this.fBindNamespaces) {
            if (isNSDecl) {
                if (value.length() > this.fXMLNameLimit) {
                    this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "MaxXMLNameLimit", new Object[] { value, value.length(), this.fXMLNameLimit, this.fSecurityManager.getStateLiteral(XMLSecurityManager.Limit.MAX_NAME_LIMIT) }, (short)2);
                }
                final String uri = this.fSymbolTable.addSymbol(value);
                if (prefix == XMLSymbols.PREFIX_XMLNS && localpart == XMLSymbols.PREFIX_XMLNS) {
                    this.fErrorReporter.reportError("http://www.w3.org/TR/1999/REC-xml-names-19990114", "CantBindXMLNS", new Object[] { this.fAttributeQName }, (short)2);
                }
                if (uri == NamespaceContext.XMLNS_URI) {
                    this.fErrorReporter.reportError("http://www.w3.org/TR/1999/REC-xml-names-19990114", "CantBindXMLNS", new Object[] { this.fAttributeQName }, (short)2);
                }
                if (localpart == XMLSymbols.PREFIX_XML) {
                    if (uri != NamespaceContext.XML_URI) {
                        this.fErrorReporter.reportError("http://www.w3.org/TR/1999/REC-xml-names-19990114", "CantBindXML", new Object[] { this.fAttributeQName }, (short)2);
                    }
                }
                else if (uri == NamespaceContext.XML_URI) {
                    this.fErrorReporter.reportError("http://www.w3.org/TR/1999/REC-xml-names-19990114", "CantBindXML", new Object[] { this.fAttributeQName }, (short)2);
                }
                prefix = ((localpart != XMLSymbols.PREFIX_XMLNS) ? localpart : XMLSymbols.EMPTY_STRING);
                this.fNamespaceContext.declarePrefix(prefix, (uri.length() != 0) ? uri : null);
                attributes.setURI(attrIndex, this.fNamespaceContext.getURI(XMLSymbols.PREFIX_XMLNS));
            }
            else if (this.fAttributeQName.prefix != null) {
                attributes.setURI(attrIndex, this.fNamespaceContext.getURI(this.fAttributeQName.prefix));
            }
        }
    }
    
    @Override
    protected int scanEndElement() throws IOException, XNIException {
        final QName endElementName = this.fElementStack.popElement();
        if (!this.fEntityScanner.skipString(endElementName.rawname)) {
            this.reportFatalError("ETagRequired", new Object[] { endElementName.rawname });
        }
        this.fEntityScanner.skipSpaces();
        if (!this.fEntityScanner.skipChar(62, NameType.ELEMENTEND)) {
            this.reportFatalError("ETagUnterminated", new Object[] { endElementName.rawname });
        }
        --this.fMarkupDepth;
        --this.fMarkupDepth;
        if (this.fMarkupDepth < this.fEntityStack[this.fEntityDepth - 1]) {
            this.reportFatalError("ElementEntityMismatch", new Object[] { endElementName.rawname });
        }
        if (this.fDocumentHandler != null) {
            this.fDocumentHandler.endElement(endElementName, null);
        }
        if (this.dtdGrammarUtil != null) {
            this.dtdGrammarUtil.endElement(endElementName);
        }
        return this.fMarkupDepth;
    }
    
    @Override
    public void reset(final XMLComponentManager componentManager) throws XMLConfigurationException {
        super.reset(componentManager);
        this.fPerformValidation = false;
        this.fBindNamespaces = false;
    }
    
    @Override
    protected Driver createContentDriver() {
        return new NS11ContentDriver();
    }
    
    @Override
    public int next() throws IOException, XNIException {
        if (this.fScannerLastState == 2 && this.fBindNamespaces) {
            this.fScannerLastState = -1;
            this.fNamespaceContext.popContext();
        }
        return this.fScannerLastState = super.next();
    }
    
    protected final class NS11ContentDriver extends ContentDriver
    {
        @Override
        protected boolean scanRootElementHook() throws IOException, XNIException {
            if (XML11NSDocumentScannerImpl.this.fExternalSubsetResolver != null && !XML11NSDocumentScannerImpl.this.fSeenDoctypeDecl && !XML11NSDocumentScannerImpl.this.fDisallowDoctype && (XML11NSDocumentScannerImpl.this.fValidation || XML11NSDocumentScannerImpl.this.fLoadExternalDTD)) {
                XML11NSDocumentScannerImpl.this.scanStartElementName();
                this.resolveExternalSubsetAndRead();
                this.reconfigurePipeline();
                if (XML11NSDocumentScannerImpl.this.scanStartElementAfterName()) {
                    XML11NSDocumentScannerImpl.this.setScannerState(44);
                    XML11NSDocumentScannerImpl.this.setDriver(XML11NSDocumentScannerImpl.this.fTrailingMiscDriver);
                    return true;
                }
            }
            else {
                this.reconfigurePipeline();
                if (XML11NSDocumentScannerImpl.this.scanStartElement()) {
                    XML11NSDocumentScannerImpl.this.setScannerState(44);
                    XML11NSDocumentScannerImpl.this.setDriver(XML11NSDocumentScannerImpl.this.fTrailingMiscDriver);
                    return true;
                }
            }
            return false;
        }
        
        private void reconfigurePipeline() {
            if (XML11NSDocumentScannerImpl.this.fDTDValidator == null) {
                XML11NSDocumentScannerImpl.this.fBindNamespaces = true;
            }
            else if (!XML11NSDocumentScannerImpl.this.fDTDValidator.hasGrammar()) {
                XML11NSDocumentScannerImpl.this.fBindNamespaces = true;
                XML11NSDocumentScannerImpl.this.fPerformValidation = XML11NSDocumentScannerImpl.this.fDTDValidator.validate();
                final XMLDocumentSource source = XML11NSDocumentScannerImpl.this.fDTDValidator.getDocumentSource();
                final XMLDocumentHandler handler = XML11NSDocumentScannerImpl.this.fDTDValidator.getDocumentHandler();
                source.setDocumentHandler(handler);
                if (handler != null) {
                    handler.setDocumentSource(source);
                }
                XML11NSDocumentScannerImpl.this.fDTDValidator.setDocumentSource(null);
                XML11NSDocumentScannerImpl.this.fDTDValidator.setDocumentHandler(null);
            }
        }
    }
}
