package org.apache.xerces.impl;

import org.apache.xerces.xni.XMLDocumentHandler;
import org.apache.xerces.xni.parser.XMLDocumentSource;
import org.apache.xerces.xni.parser.XMLConfigurationException;
import org.apache.xerces.xni.parser.XMLComponentManager;
import org.apache.xerces.xni.NamespaceContext;
import org.apache.xerces.util.XMLAttributesImpl;
import org.apache.xerces.xni.XNIException;
import java.io.IOException;
import org.apache.xerces.xni.QName;
import org.apache.xerces.xni.Augmentations;
import org.apache.xerces.xni.XMLAttributes;
import org.apache.xerces.util.XMLSymbols;
import org.apache.xerces.impl.dtd.XMLDTDValidatorFilter;

public class XML11NSDocumentScannerImpl extends XML11DocumentScannerImpl
{
    protected boolean fBindNamespaces;
    protected boolean fPerformValidation;
    private XMLDTDValidatorFilter fDTDValidator;
    private boolean fSawSpace;
    
    public void setDTDValidator(final XMLDTDValidatorFilter fdtdValidator) {
        this.fDTDValidator = fdtdValidator;
    }
    
    protected boolean scanStartElement() throws IOException, XNIException {
        this.fEntityScanner.scanQName(this.fElementQName);
        final String rawname = this.fElementQName.rawname;
        if (this.fBindNamespaces) {
            this.fNamespaceContext.pushContext();
            if (this.fScannerState == 6 && this.fPerformValidation) {
                this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_GRAMMAR_NOT_FOUND", new Object[] { rawname }, (short)1);
                if (this.fDoctypeName == null || !this.fDoctypeName.equals(rawname)) {
                    this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "RootElementTypeMustMatchDoctypedecl", new Object[] { this.fDoctypeName, rawname }, (short)1);
                }
            }
        }
        this.fCurrentElement = this.fElementStack.pushElement(this.fElementQName);
        boolean b = false;
        this.fAttributes.removeAllAttributes();
        while (true) {
            final boolean skipSpaces = this.fEntityScanner.skipSpaces();
            final int peekChar = this.fEntityScanner.peekChar();
            if (peekChar == 62) {
                this.fEntityScanner.scanChar();
                break;
            }
            if (peekChar == 47) {
                this.fEntityScanner.scanChar();
                if (!this.fEntityScanner.skipChar(62)) {
                    this.reportFatalError("ElementUnterminated", new Object[] { rawname });
                }
                b = true;
                break;
            }
            if ((!this.isValidNameStartChar(peekChar) || !skipSpaces) && (!this.isValidNameStartHighSurrogate(peekChar) || !skipSpaces)) {
                this.reportFatalError("ElementUnterminated", new Object[] { rawname });
            }
            this.scanAttribute(this.fAttributes);
        }
        if (this.fBindNamespaces) {
            if (this.fElementQName.prefix == XMLSymbols.PREFIX_XMLNS) {
                this.fErrorReporter.reportError("http://www.w3.org/TR/1999/REC-xml-names-19990114", "ElementXMLNSPrefix", new Object[] { this.fElementQName.rawname }, (short)2);
            }
            this.fElementQName.uri = this.fNamespaceContext.getURI((this.fElementQName.prefix != null) ? this.fElementQName.prefix : XMLSymbols.EMPTY_STRING);
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
                final String s = (this.fAttributeQName.prefix != null) ? this.fAttributeQName.prefix : XMLSymbols.EMPTY_STRING;
                final String uri = this.fNamespaceContext.getURI(s);
                if (this.fAttributeQName.uri == null || this.fAttributeQName.uri != uri) {
                    if (s != XMLSymbols.EMPTY_STRING) {
                        if ((this.fAttributeQName.uri = uri) == null) {
                            this.fErrorReporter.reportError("http://www.w3.org/TR/1999/REC-xml-names-19990114", "AttributePrefixUnbound", new Object[] { this.fElementQName.rawname, this.fAttributeQName.rawname, s }, (short)2);
                        }
                        this.fAttributes.setURI(i, uri);
                    }
                }
            }
            if (length > 1) {
                final QName checkDuplicatesNS = this.fAttributes.checkDuplicatesNS();
                if (checkDuplicatesNS != null) {
                    if (checkDuplicatesNS.uri != null) {
                        this.fErrorReporter.reportError("http://www.w3.org/TR/1999/REC-xml-names-19990114", "AttributeNSNotUnique", new Object[] { this.fElementQName.rawname, checkDuplicatesNS.localpart, checkDuplicatesNS.uri }, (short)2);
                    }
                    else {
                        this.fErrorReporter.reportError("http://www.w3.org/TR/1999/REC-xml-names-19990114", "AttributeNotUnique", new Object[] { this.fElementQName.rawname, checkDuplicatesNS.rawname }, (short)2);
                    }
                }
            }
        }
        if (this.fDocumentHandler != null) {
            if (b) {
                --this.fMarkupDepth;
                if (this.fMarkupDepth < this.fEntityStack[this.fEntityDepth - 1]) {
                    this.reportFatalError("ElementEntityMismatch", new Object[] { this.fCurrentElement.rawname });
                }
                this.fDocumentHandler.emptyElement(this.fElementQName, this.fAttributes, null);
                if (this.fBindNamespaces) {
                    this.fNamespaceContext.popContext();
                }
                this.fElementStack.popElement(this.fElementQName);
            }
            else {
                this.fDocumentHandler.startElement(this.fElementQName, this.fAttributes, null);
            }
        }
        return b;
    }
    
    protected void scanStartElementName() throws IOException, XNIException {
        this.fEntityScanner.scanQName(this.fElementQName);
        this.fSawSpace = this.fEntityScanner.skipSpaces();
    }
    
    protected boolean scanStartElementAfterName() throws IOException, XNIException {
        final String rawname = this.fElementQName.rawname;
        if (this.fBindNamespaces) {
            this.fNamespaceContext.pushContext();
            if (this.fScannerState == 6 && this.fPerformValidation) {
                this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_GRAMMAR_NOT_FOUND", new Object[] { rawname }, (short)1);
                if (this.fDoctypeName == null || !this.fDoctypeName.equals(rawname)) {
                    this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "RootElementTypeMustMatchDoctypedecl", new Object[] { this.fDoctypeName, rawname }, (short)1);
                }
            }
        }
        this.fCurrentElement = this.fElementStack.pushElement(this.fElementQName);
        boolean b = false;
        this.fAttributes.removeAllAttributes();
        while (true) {
            final int peekChar = this.fEntityScanner.peekChar();
            if (peekChar == 62) {
                this.fEntityScanner.scanChar();
                break;
            }
            if (peekChar == 47) {
                this.fEntityScanner.scanChar();
                if (!this.fEntityScanner.skipChar(62)) {
                    this.reportFatalError("ElementUnterminated", new Object[] { rawname });
                }
                b = true;
                break;
            }
            if ((!this.isValidNameStartChar(peekChar) || !this.fSawSpace) && (!this.isValidNameStartHighSurrogate(peekChar) || !this.fSawSpace)) {
                this.reportFatalError("ElementUnterminated", new Object[] { rawname });
            }
            this.scanAttribute(this.fAttributes);
            this.fSawSpace = this.fEntityScanner.skipSpaces();
        }
        if (this.fBindNamespaces) {
            if (this.fElementQName.prefix == XMLSymbols.PREFIX_XMLNS) {
                this.fErrorReporter.reportError("http://www.w3.org/TR/1999/REC-xml-names-19990114", "ElementXMLNSPrefix", new Object[] { this.fElementQName.rawname }, (short)2);
            }
            this.fElementQName.uri = this.fNamespaceContext.getURI((this.fElementQName.prefix != null) ? this.fElementQName.prefix : XMLSymbols.EMPTY_STRING);
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
                final String s = (this.fAttributeQName.prefix != null) ? this.fAttributeQName.prefix : XMLSymbols.EMPTY_STRING;
                final String uri = this.fNamespaceContext.getURI(s);
                if (this.fAttributeQName.uri == null || this.fAttributeQName.uri != uri) {
                    if (s != XMLSymbols.EMPTY_STRING) {
                        if ((this.fAttributeQName.uri = uri) == null) {
                            this.fErrorReporter.reportError("http://www.w3.org/TR/1999/REC-xml-names-19990114", "AttributePrefixUnbound", new Object[] { this.fElementQName.rawname, this.fAttributeQName.rawname, s }, (short)2);
                        }
                        this.fAttributes.setURI(i, uri);
                    }
                }
            }
            if (length > 1) {
                final QName checkDuplicatesNS = this.fAttributes.checkDuplicatesNS();
                if (checkDuplicatesNS != null) {
                    if (checkDuplicatesNS.uri != null) {
                        this.fErrorReporter.reportError("http://www.w3.org/TR/1999/REC-xml-names-19990114", "AttributeNSNotUnique", new Object[] { this.fElementQName.rawname, checkDuplicatesNS.localpart, checkDuplicatesNS.uri }, (short)2);
                    }
                    else {
                        this.fErrorReporter.reportError("http://www.w3.org/TR/1999/REC-xml-names-19990114", "AttributeNotUnique", new Object[] { this.fElementQName.rawname, checkDuplicatesNS.rawname }, (short)2);
                    }
                }
            }
        }
        if (this.fDocumentHandler != null) {
            if (b) {
                --this.fMarkupDepth;
                if (this.fMarkupDepth < this.fEntityStack[this.fEntityDepth - 1]) {
                    this.reportFatalError("ElementEntityMismatch", new Object[] { this.fCurrentElement.rawname });
                }
                this.fDocumentHandler.emptyElement(this.fElementQName, this.fAttributes, null);
                if (this.fBindNamespaces) {
                    this.fNamespaceContext.popContext();
                }
                this.fElementStack.popElement(this.fElementQName);
            }
            else {
                this.fDocumentHandler.startElement(this.fElementQName, this.fAttributes, null);
            }
        }
        return b;
    }
    
    protected void scanAttribute(final XMLAttributesImpl xmlAttributesImpl) throws IOException, XNIException {
        this.fEntityScanner.scanQName(this.fAttributeQName);
        this.fEntityScanner.skipSpaces();
        if (!this.fEntityScanner.skipChar(61)) {
            this.reportFatalError("EqRequiredInAttribute", new Object[] { this.fCurrentElement.rawname, this.fAttributeQName.rawname });
        }
        this.fEntityScanner.skipSpaces();
        int n;
        if (this.fBindNamespaces) {
            n = xmlAttributesImpl.getLength();
            xmlAttributesImpl.addAttributeNS(this.fAttributeQName, XMLSymbols.fCDATASymbol, null);
        }
        else {
            final int length = xmlAttributesImpl.getLength();
            n = xmlAttributesImpl.addAttribute(this.fAttributeQName, XMLSymbols.fCDATASymbol, null);
            if (length == xmlAttributesImpl.getLength()) {
                this.reportFatalError("AttributeNotUnique", new Object[] { this.fCurrentElement.rawname, this.fAttributeQName.rawname });
            }
        }
        final boolean scanAttributeValue = this.scanAttributeValue(this.fTempString, this.fTempString2, this.fAttributeQName.rawname, this.fIsEntityDeclaredVC, this.fCurrentElement.rawname);
        final String string = this.fTempString.toString();
        xmlAttributesImpl.setValue(n, string);
        if (!scanAttributeValue) {
            xmlAttributesImpl.setNonNormalizedValue(n, this.fTempString2.toString());
        }
        xmlAttributesImpl.setSpecified(n, true);
        if (this.fBindNamespaces) {
            final String localpart = this.fAttributeQName.localpart;
            final String s = (this.fAttributeQName.prefix != null) ? this.fAttributeQName.prefix : XMLSymbols.EMPTY_STRING;
            if (s == XMLSymbols.PREFIX_XMLNS || (s == XMLSymbols.EMPTY_STRING && localpart == XMLSymbols.PREFIX_XMLNS)) {
                final String addSymbol = this.fSymbolTable.addSymbol(string);
                if (s == XMLSymbols.PREFIX_XMLNS && localpart == XMLSymbols.PREFIX_XMLNS) {
                    this.fErrorReporter.reportError("http://www.w3.org/TR/1999/REC-xml-names-19990114", "CantBindXMLNS", new Object[] { this.fAttributeQName }, (short)2);
                }
                if (addSymbol == NamespaceContext.XMLNS_URI) {
                    this.fErrorReporter.reportError("http://www.w3.org/TR/1999/REC-xml-names-19990114", "CantBindXMLNS", new Object[] { this.fAttributeQName }, (short)2);
                }
                if (localpart == XMLSymbols.PREFIX_XML) {
                    if (addSymbol != NamespaceContext.XML_URI) {
                        this.fErrorReporter.reportError("http://www.w3.org/TR/1999/REC-xml-names-19990114", "CantBindXML", new Object[] { this.fAttributeQName }, (short)2);
                    }
                }
                else if (addSymbol == NamespaceContext.XML_URI) {
                    this.fErrorReporter.reportError("http://www.w3.org/TR/1999/REC-xml-names-19990114", "CantBindXML", new Object[] { this.fAttributeQName }, (short)2);
                }
                this.fNamespaceContext.declarePrefix((localpart != XMLSymbols.PREFIX_XMLNS) ? localpart : XMLSymbols.EMPTY_STRING, (addSymbol.length() != 0) ? addSymbol : null);
                xmlAttributesImpl.setURI(n, this.fNamespaceContext.getURI(XMLSymbols.PREFIX_XMLNS));
            }
            else if (this.fAttributeQName.prefix != null) {
                xmlAttributesImpl.setURI(n, this.fNamespaceContext.getURI(this.fAttributeQName.prefix));
            }
        }
    }
    
    protected int scanEndElement() throws IOException, XNIException {
        this.fElementStack.popElement(this.fElementQName);
        if (!this.fEntityScanner.skipString(this.fElementQName.rawname)) {
            this.reportFatalError("ETagRequired", new Object[] { this.fElementQName.rawname });
        }
        this.fEntityScanner.skipSpaces();
        if (!this.fEntityScanner.skipChar(62)) {
            this.reportFatalError("ETagUnterminated", new Object[] { this.fElementQName.rawname });
        }
        --this.fMarkupDepth;
        --this.fMarkupDepth;
        if (this.fMarkupDepth < this.fEntityStack[this.fEntityDepth - 1]) {
            this.reportFatalError("ElementEntityMismatch", new Object[] { this.fCurrentElement.rawname });
        }
        if (this.fDocumentHandler != null) {
            this.fDocumentHandler.endElement(this.fElementQName, null);
            if (this.fBindNamespaces) {
                this.fNamespaceContext.popContext();
            }
        }
        return this.fMarkupDepth;
    }
    
    public void reset(final XMLComponentManager xmlComponentManager) throws XMLConfigurationException {
        super.reset(xmlComponentManager);
        this.fPerformValidation = false;
        this.fBindNamespaces = false;
    }
    
    protected Dispatcher createContentDispatcher() {
        return new NS11ContentDispatcher();
    }
    
    protected final class NS11ContentDispatcher extends ContentDispatcher
    {
        protected boolean scanRootElementHook() throws IOException, XNIException {
            if (XML11NSDocumentScannerImpl.this.fExternalSubsetResolver != null && !XML11NSDocumentScannerImpl.this.fSeenDoctypeDecl && !XML11NSDocumentScannerImpl.this.fDisallowDoctype && (XML11NSDocumentScannerImpl.this.fValidation || XML11NSDocumentScannerImpl.this.fLoadExternalDTD)) {
                XML11NSDocumentScannerImpl.this.scanStartElementName();
                this.resolveExternalSubsetAndRead();
                this.reconfigurePipeline();
                if (XML11NSDocumentScannerImpl.this.scanStartElementAfterName()) {
                    XML11NSDocumentScannerImpl.this.setScannerState(12);
                    XML11NSDocumentScannerImpl.this.setDispatcher(XML11NSDocumentScannerImpl.this.fTrailingMiscDispatcher);
                    return true;
                }
            }
            else {
                this.reconfigurePipeline();
                if (XML11NSDocumentScannerImpl.this.scanStartElement()) {
                    XML11NSDocumentScannerImpl.this.setScannerState(12);
                    XML11NSDocumentScannerImpl.this.setDispatcher(XML11NSDocumentScannerImpl.this.fTrailingMiscDispatcher);
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
                final XMLDocumentSource documentSource = XML11NSDocumentScannerImpl.this.fDTDValidator.getDocumentSource();
                final XMLDocumentHandler documentHandler = XML11NSDocumentScannerImpl.this.fDTDValidator.getDocumentHandler();
                documentSource.setDocumentHandler(documentHandler);
                if (documentHandler != null) {
                    documentHandler.setDocumentSource(documentSource);
                }
                XML11NSDocumentScannerImpl.this.fDTDValidator.setDocumentSource(null);
                XML11NSDocumentScannerImpl.this.fDTDValidator.setDocumentHandler(null);
            }
        }
    }
}
