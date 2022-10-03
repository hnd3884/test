package org.apache.xerces.impl;

import org.apache.xerces.util.XML11Char;
import org.apache.xerces.xni.XNIException;
import java.io.IOException;
import org.apache.xerces.util.XMLChar;
import org.apache.xerces.xni.XMLString;
import org.apache.xerces.util.SymbolTable;
import org.apache.xerces.util.XMLStringBuffer;

public class XML11DTDScannerImpl extends XMLDTDScannerImpl
{
    private final XMLStringBuffer fStringBuffer;
    
    public XML11DTDScannerImpl() {
        this.fStringBuffer = new XMLStringBuffer();
    }
    
    public XML11DTDScannerImpl(final SymbolTable symbolTable, final XMLErrorReporter xmlErrorReporter, final XMLEntityManager xmlEntityManager) {
        super(symbolTable, xmlErrorReporter, xmlEntityManager);
        this.fStringBuffer = new XMLStringBuffer();
    }
    
    protected boolean scanPubidLiteral(final XMLString xmlString) throws IOException, XNIException {
        final int scanChar = this.fEntityScanner.scanChar();
        if (scanChar != 39 && scanChar != 34) {
            this.reportFatalError("QuoteRequiredInPublicID", null);
            return false;
        }
        this.fStringBuffer.clear();
        int n = 1;
        boolean b = true;
        while (true) {
            final int scanChar2 = this.fEntityScanner.scanChar();
            if (scanChar2 == 32 || scanChar2 == 10 || scanChar2 == 13 || scanChar2 == 133 || scanChar2 == 8232) {
                if (n != 0) {
                    continue;
                }
                this.fStringBuffer.append(' ');
                n = 1;
            }
            else {
                if (scanChar2 == scanChar) {
                    if (n != 0) {
                        final XMLStringBuffer fStringBuffer = this.fStringBuffer;
                        --fStringBuffer.length;
                    }
                    xmlString.setValues(this.fStringBuffer);
                    return b;
                }
                if (XMLChar.isPubid(scanChar2)) {
                    this.fStringBuffer.append((char)scanChar2);
                    n = 0;
                }
                else {
                    if (scanChar2 == -1) {
                        this.reportFatalError("PublicIDUnterminated", null);
                        return false;
                    }
                    b = false;
                    this.reportFatalError("InvalidCharInPublicID", new Object[] { Integer.toHexString(scanChar2) });
                }
            }
        }
    }
    
    protected void normalizeWhitespace(final XMLString xmlString) {
        for (int n = xmlString.offset + xmlString.length, i = xmlString.offset; i < n; ++i) {
            if (XMLChar.isSpace(xmlString.ch[i])) {
                xmlString.ch[i] = ' ';
            }
        }
    }
    
    protected void normalizeWhitespace(final XMLString xmlString, final int n) {
        for (int n2 = xmlString.offset + xmlString.length, i = xmlString.offset + n; i < n2; ++i) {
            if (XMLChar.isSpace(xmlString.ch[i])) {
                xmlString.ch[i] = ' ';
            }
        }
    }
    
    protected int isUnchangedByNormalization(final XMLString xmlString) {
        for (int n = xmlString.offset + xmlString.length, i = xmlString.offset; i < n; ++i) {
            if (XMLChar.isSpace(xmlString.ch[i])) {
                return i - xmlString.offset;
            }
        }
        return -1;
    }
    
    protected boolean isInvalid(final int n) {
        return !XML11Char.isXML11Valid(n);
    }
    
    protected boolean isInvalidLiteral(final int n) {
        return !XML11Char.isXML11ValidLiteral(n);
    }
    
    protected boolean isValidNameChar(final int n) {
        return XML11Char.isXML11Name(n);
    }
    
    protected boolean isValidNameStartChar(final int n) {
        return XML11Char.isXML11NameStart(n);
    }
    
    protected boolean isValidNCName(final int n) {
        return XML11Char.isXML11NCName(n);
    }
    
    protected boolean isValidNameStartHighSurrogate(final int n) {
        return XML11Char.isXML11NameHighSurrogate(n);
    }
    
    protected boolean versionSupported(final String s) {
        return s.equals("1.1") || s.equals("1.0");
    }
    
    protected String getVersionNotSupportedKey() {
        return "VersionNotSupported11";
    }
}
