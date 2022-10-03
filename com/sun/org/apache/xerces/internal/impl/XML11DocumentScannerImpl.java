package com.sun.org.apache.xerces.internal.impl;

import com.sun.org.apache.xerces.internal.util.XML11Char;
import com.sun.org.apache.xerces.internal.util.XMLChar;
import com.sun.org.apache.xerces.internal.xni.XMLString;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import java.io.IOException;
import com.sun.org.apache.xerces.internal.util.XMLStringBuffer;

public class XML11DocumentScannerImpl extends XMLDocumentScannerImpl
{
    private final XMLStringBuffer fStringBuffer;
    private final XMLStringBuffer fStringBuffer2;
    private final XMLStringBuffer fStringBuffer3;
    
    public XML11DocumentScannerImpl() {
        this.fStringBuffer = new XMLStringBuffer();
        this.fStringBuffer2 = new XMLStringBuffer();
        this.fStringBuffer3 = new XMLStringBuffer();
    }
    
    @Override
    protected int scanContent(final XMLStringBuffer content) throws IOException, XNIException {
        this.fTempString.length = 0;
        int c = this.fEntityScanner.scanContent(this.fTempString);
        content.append(this.fTempString);
        if (c == 13 || c == 133 || c == 8232) {
            this.fEntityScanner.scanChar(null);
            content.append((char)c);
            c = -1;
        }
        if (c == 93) {
            content.append((char)this.fEntityScanner.scanChar(null));
            this.fInScanContent = true;
            if (this.fEntityScanner.skipChar(93, null)) {
                content.append(']');
                while (this.fEntityScanner.skipChar(93, null)) {
                    content.append(']');
                }
                if (this.fEntityScanner.skipChar(62, null)) {
                    this.reportFatalError("CDEndInContent", null);
                }
            }
            this.fInScanContent = false;
            c = -1;
        }
        return c;
    }
    
    protected boolean scanAttributeValue(final XMLString value, final XMLString nonNormalizedValue, final String atName, final boolean checkEntities, final String eleName, final boolean isNSURI) throws IOException, XNIException {
        final int quote = this.fEntityScanner.peekChar();
        if (quote != 39 && quote != 34) {
            this.reportFatalError("OpenQuoteExpected", new Object[] { eleName, atName });
        }
        this.fEntityScanner.scanChar(NameType.ATTRIBUTE);
        final int entityDepth = this.fEntityDepth;
        int c = this.fEntityScanner.scanLiteral(quote, value, isNSURI);
        int fromIndex = 0;
        if (c == quote && (fromIndex = this.isUnchangedByNormalization(value)) == -1) {
            nonNormalizedValue.setValues(value);
            final int cquote = this.fEntityScanner.scanChar(NameType.ATTRIBUTE);
            if (cquote != quote) {
                this.reportFatalError("CloseQuoteExpected", new Object[] { eleName, atName });
            }
            return true;
        }
        this.fStringBuffer2.clear();
        this.fStringBuffer2.append(value);
        this.normalizeWhitespace(value, fromIndex);
        if (c != quote) {
            this.fScanningAttribute = true;
            this.fStringBuffer.clear();
            do {
                this.fStringBuffer.append(value);
                if (c == 38) {
                    this.fEntityScanner.skipChar(38, NameType.REFERENCE);
                    if (entityDepth == this.fEntityDepth) {
                        this.fStringBuffer2.append('&');
                    }
                    if (this.fEntityScanner.skipChar(35, NameType.REFERENCE)) {
                        if (entityDepth == this.fEntityDepth) {
                            this.fStringBuffer2.append('#');
                        }
                        final int ch = this.scanCharReferenceValue(this.fStringBuffer, this.fStringBuffer2);
                        if (ch != -1) {}
                    }
                    else {
                        final String entityName = this.fEntityScanner.scanName(NameType.REFERENCE);
                        if (entityName == null) {
                            this.reportFatalError("NameRequiredInReference", null);
                        }
                        else if (entityDepth == this.fEntityDepth) {
                            this.fStringBuffer2.append(entityName);
                        }
                        if (!this.fEntityScanner.skipChar(59, NameType.REFERENCE)) {
                            this.reportFatalError("SemicolonRequiredInReference", new Object[] { entityName });
                        }
                        else if (entityDepth == this.fEntityDepth) {
                            this.fStringBuffer2.append(';');
                        }
                        if (this.resolveCharacter(entityName, this.fStringBuffer)) {
                            this.checkEntityLimit(false, this.fEntityScanner.fCurrentEntity.name, 1);
                        }
                        else if (this.fEntityManager.isExternalEntity(entityName)) {
                            this.reportFatalError("ReferenceToExternalEntity", new Object[] { entityName });
                        }
                        else {
                            if (!this.fEntityManager.isDeclaredEntity(entityName)) {
                                if (checkEntities) {
                                    if (this.fValidation) {
                                        this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "EntityNotDeclared", new Object[] { entityName }, (short)1);
                                    }
                                }
                                else {
                                    this.reportFatalError("EntityNotDeclared", new Object[] { entityName });
                                }
                            }
                            this.fEntityManager.startEntity(true, entityName, true);
                        }
                    }
                }
                else if (c == 60) {
                    this.reportFatalError("LessthanInAttValue", new Object[] { eleName, atName });
                    this.fEntityScanner.scanChar(null);
                    if (entityDepth == this.fEntityDepth) {
                        this.fStringBuffer2.append((char)c);
                    }
                }
                else if (c == 37 || c == 93) {
                    this.fEntityScanner.scanChar(null);
                    this.fStringBuffer.append((char)c);
                    if (entityDepth == this.fEntityDepth) {
                        this.fStringBuffer2.append((char)c);
                    }
                }
                else if (c == 10 || c == 13 || c == 133 || c == 8232) {
                    this.fEntityScanner.scanChar(null);
                    this.fStringBuffer.append(' ');
                    if (entityDepth == this.fEntityDepth) {
                        this.fStringBuffer2.append('\n');
                    }
                }
                else if (c != -1 && XMLChar.isHighSurrogate(c)) {
                    this.fStringBuffer3.clear();
                    if (this.scanSurrogates(this.fStringBuffer3)) {
                        this.fStringBuffer.append(this.fStringBuffer3);
                        if (entityDepth == this.fEntityDepth) {
                            this.fStringBuffer2.append(this.fStringBuffer3);
                        }
                    }
                }
                else if (c != -1 && this.isInvalidLiteral(c)) {
                    this.reportFatalError("InvalidCharInAttValue", new Object[] { eleName, atName, Integer.toString(c, 16) });
                    this.fEntityScanner.scanChar(null);
                    if (entityDepth == this.fEntityDepth) {
                        this.fStringBuffer2.append((char)c);
                    }
                }
                c = this.fEntityScanner.scanLiteral(quote, value, isNSURI);
                if (entityDepth == this.fEntityDepth) {
                    this.fStringBuffer2.append(value);
                }
                this.normalizeWhitespace(value);
            } while (c != quote || entityDepth != this.fEntityDepth);
            this.fStringBuffer.append(value);
            value.setValues(this.fStringBuffer);
            this.fScanningAttribute = false;
        }
        nonNormalizedValue.setValues(this.fStringBuffer2);
        final int cquote = this.fEntityScanner.scanChar(null);
        if (cquote != quote) {
            this.reportFatalError("CloseQuoteExpected", new Object[] { eleName, atName });
        }
        return nonNormalizedValue.equals(value.ch, value.offset, value.length);
    }
    
    @Override
    protected boolean scanPubidLiteral(final XMLString literal) throws IOException, XNIException {
        final int quote = this.fEntityScanner.scanChar(null);
        if (quote != 39 && quote != 34) {
            this.reportFatalError("QuoteRequiredInPublicID", null);
            return false;
        }
        this.fStringBuffer.clear();
        boolean skipSpace = true;
        boolean dataok = true;
        while (true) {
            final int c = this.fEntityScanner.scanChar(null);
            if (c == 32 || c == 10 || c == 13 || c == 133 || c == 8232) {
                if (skipSpace) {
                    continue;
                }
                this.fStringBuffer.append(' ');
                skipSpace = true;
            }
            else {
                if (c == quote) {
                    if (skipSpace) {
                        final XMLStringBuffer fStringBuffer = this.fStringBuffer;
                        --fStringBuffer.length;
                    }
                    literal.setValues(this.fStringBuffer);
                    return dataok;
                }
                if (XMLChar.isPubid(c)) {
                    this.fStringBuffer.append((char)c);
                    skipSpace = false;
                }
                else {
                    if (c == -1) {
                        this.reportFatalError("PublicIDUnterminated", null);
                        return false;
                    }
                    dataok = false;
                    this.reportFatalError("InvalidCharInPublicID", new Object[] { Integer.toHexString(c) });
                }
            }
        }
    }
    
    @Override
    protected void normalizeWhitespace(final XMLString value) {
        for (int end = value.offset + value.length, i = value.offset; i < end; ++i) {
            final int c = value.ch[i];
            if (XMLChar.isSpace(c)) {
                value.ch[i] = ' ';
            }
        }
    }
    
    protected void normalizeWhitespace(final XMLString value, final int fromIndex) {
        for (int end = value.offset + value.length, i = value.offset + fromIndex; i < end; ++i) {
            final int c = value.ch[i];
            if (XMLChar.isSpace(c)) {
                value.ch[i] = ' ';
            }
        }
    }
    
    protected int isUnchangedByNormalization(final XMLString value) {
        for (int end = value.offset + value.length, i = value.offset; i < end; ++i) {
            final int c = value.ch[i];
            if (XMLChar.isSpace(c)) {
                return i - value.offset;
            }
        }
        return -1;
    }
    
    @Override
    protected boolean isInvalid(final int value) {
        return XML11Char.isXML11Invalid(value);
    }
    
    @Override
    protected boolean isInvalidLiteral(final int value) {
        return !XML11Char.isXML11ValidLiteral(value);
    }
    
    @Override
    protected boolean isValidNameChar(final int value) {
        return XML11Char.isXML11Name(value);
    }
    
    @Override
    protected boolean isValidNameStartChar(final int value) {
        return XML11Char.isXML11NameStart(value);
    }
    
    @Override
    protected boolean isValidNCName(final int value) {
        return XML11Char.isXML11NCName(value);
    }
    
    @Override
    protected boolean isValidNameStartHighSurrogate(final int value) {
        return XML11Char.isXML11NameHighSurrogate(value);
    }
    
    @Override
    protected boolean versionSupported(final String version) {
        return version.equals("1.1") || version.equals("1.0");
    }
    
    protected String getVersionNotSupportedKey() {
        return "VersionNotSupported11";
    }
}
