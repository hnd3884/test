package com.sun.xml.internal.messaging.saaj.util;

import java.util.StringTokenizer;
import java.io.Writer;
import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.io.PushbackReader;

public class XMLDeclarationParser
{
    private String m_encoding;
    private PushbackReader m_pushbackReader;
    private boolean m_hasHeader;
    private String xmlDecl;
    static String gt16;
    static String utf16Decl;
    
    public XMLDeclarationParser(final PushbackReader pr) {
        this.xmlDecl = null;
        this.m_pushbackReader = pr;
        this.m_encoding = "utf-8";
        this.m_hasHeader = false;
    }
    
    public String getEncoding() {
        return this.m_encoding;
    }
    
    public String getXmlDeclaration() {
        return this.xmlDecl;
    }
    
    public void parse() throws TransformerException, IOException {
        int c = 0;
        int index = 0;
        final char[] aChar = new char[65535];
        final StringBuffer xmlDeclStr = new StringBuffer();
        while ((c = this.m_pushbackReader.read()) != -1) {
            aChar[index] = (char)c;
            xmlDeclStr.append((char)c);
            ++index;
            if (c == 62) {
                break;
            }
        }
        final int len = index;
        final String decl = xmlDeclStr.toString();
        boolean utf16 = false;
        boolean utf17 = false;
        int xmlIndex = decl.indexOf(XMLDeclarationParser.utf16Decl);
        if (xmlIndex > -1) {
            utf16 = true;
        }
        else {
            xmlIndex = decl.indexOf("<?xml");
            if (xmlIndex > -1) {
                utf17 = true;
            }
        }
        if (!utf16 && !utf17) {
            this.m_pushbackReader.unread(aChar, 0, len);
            return;
        }
        this.m_hasHeader = true;
        if (utf16) {
            this.xmlDecl = new String(decl.getBytes(), "utf-16");
            this.xmlDecl = this.xmlDecl.substring(this.xmlDecl.indexOf("<"));
        }
        else {
            this.xmlDecl = decl;
        }
        if (xmlIndex != 0) {
            throw new IOException("Unexpected characters before XML declaration");
        }
        final int versionIndex = this.xmlDecl.indexOf("version");
        if (versionIndex == -1) {
            throw new IOException("Mandatory 'version' attribute Missing in XML declaration");
        }
        final int encodingIndex = this.xmlDecl.indexOf("encoding");
        if (encodingIndex == -1) {
            return;
        }
        if (versionIndex > encodingIndex) {
            throw new IOException("The 'version' attribute should preceed the 'encoding' attribute in an XML Declaration");
        }
        final int stdAloneIndex = this.xmlDecl.indexOf("standalone");
        if (stdAloneIndex > -1 && (stdAloneIndex < versionIndex || stdAloneIndex < encodingIndex)) {
            throw new IOException("The 'standalone' attribute should be the last attribute in an XML Declaration");
        }
        final int eqIndex = this.xmlDecl.indexOf("=", encodingIndex);
        if (eqIndex == -1) {
            throw new IOException("Missing '=' character after 'encoding' in XML declaration");
        }
        this.m_encoding = this.parseEncoding(this.xmlDecl, eqIndex);
        if (this.m_encoding.startsWith("\"")) {
            this.m_encoding = this.m_encoding.substring(this.m_encoding.indexOf("\"") + 1, this.m_encoding.lastIndexOf("\""));
        }
        else if (this.m_encoding.startsWith("'")) {
            this.m_encoding = this.m_encoding.substring(this.m_encoding.indexOf("'") + 1, this.m_encoding.lastIndexOf("'"));
        }
    }
    
    public void writeTo(final Writer wr) throws IOException {
        if (!this.m_hasHeader) {
            return;
        }
        wr.write(this.xmlDecl.toString());
    }
    
    private String parseEncoding(final String xmlDeclFinal, final int eqIndex) throws IOException {
        final StringTokenizer strTok = new StringTokenizer(xmlDeclFinal.substring(eqIndex + 1));
        if (!strTok.hasMoreTokens()) {
            throw new IOException("Error parsing 'encoding' attribute in XML declaration");
        }
        final String encodingTok = strTok.nextToken();
        final int indexofQ = encodingTok.indexOf("?");
        if (indexofQ > -1) {
            return encodingTok.substring(0, indexofQ);
        }
        return encodingTok;
    }
    
    static {
        XMLDeclarationParser.gt16 = null;
        XMLDeclarationParser.utf16Decl = null;
        try {
            XMLDeclarationParser.gt16 = new String(">".getBytes("utf-16"));
            XMLDeclarationParser.utf16Decl = new String("<?xml".getBytes("utf-16"));
        }
        catch (final Exception ex) {}
    }
}
