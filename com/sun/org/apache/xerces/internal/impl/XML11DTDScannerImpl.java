package com.sun.org.apache.xerces.internal.impl;

import com.sun.org.apache.xerces.internal.util.XML11Char;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import java.io.IOException;
import com.sun.org.apache.xerces.internal.util.XMLChar;
import com.sun.org.apache.xerces.internal.xni.XMLString;
import com.sun.org.apache.xerces.internal.util.SymbolTable;
import com.sun.org.apache.xerces.internal.util.XMLStringBuffer;

public class XML11DTDScannerImpl extends XMLDTDScannerImpl
{
    private XMLStringBuffer fStringBuffer;
    
    public XML11DTDScannerImpl() {
        this.fStringBuffer = new XMLStringBuffer();
    }
    
    public XML11DTDScannerImpl(final SymbolTable symbolTable, final XMLErrorReporter errorReporter, final XMLEntityManager entityManager) {
        super(symbolTable, errorReporter, entityManager);
        this.fStringBuffer = new XMLStringBuffer();
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
        return !XML11Char.isXML11Valid(value);
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
