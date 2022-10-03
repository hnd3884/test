package com.sun.org.apache.xml.internal.serialize;

import com.sun.org.apache.xerces.internal.util.XMLChar;
import org.w3c.dom.DOMError;
import com.sun.org.apache.xerces.internal.dom.DOMMessageFormatter;
import java.io.IOException;
import org.xml.sax.SAXException;
import com.sun.org.apache.xerces.internal.util.XML11Char;
import java.io.OutputStream;
import java.io.Writer;
import com.sun.org.apache.xerces.internal.util.SymbolTable;
import com.sun.org.apache.xerces.internal.util.NamespaceSupport;

public class XML11Serializer extends XMLSerializer
{
    protected static final boolean DEBUG = false;
    protected NamespaceSupport fNSBinder;
    protected NamespaceSupport fLocalNSBinder;
    protected SymbolTable fSymbolTable;
    protected boolean fDOML1;
    protected int fNamespaceCounter;
    protected static final String PREFIX = "NS";
    protected boolean fNamespaces;
    private boolean fPreserveSpace;
    
    public XML11Serializer() {
        this.fDOML1 = false;
        this.fNamespaceCounter = 1;
        this.fNamespaces = false;
        this._format.setVersion("1.1");
    }
    
    public XML11Serializer(final OutputFormat format) {
        super(format);
        this.fDOML1 = false;
        this.fNamespaceCounter = 1;
        this.fNamespaces = false;
        this._format.setVersion("1.1");
    }
    
    public XML11Serializer(final Writer writer, final OutputFormat format) {
        super(writer, format);
        this.fDOML1 = false;
        this.fNamespaceCounter = 1;
        this.fNamespaces = false;
        this._format.setVersion("1.1");
    }
    
    public XML11Serializer(final OutputStream output, final OutputFormat format) {
        super(output, (format != null) ? format : new OutputFormat("xml", null, false));
        this.fDOML1 = false;
        this.fNamespaceCounter = 1;
        this.fNamespaces = false;
        this._format.setVersion("1.1");
    }
    
    @Override
    public void characters(final char[] chars, final int start, final int length) throws SAXException {
        try {
            final ElementState state = this.content();
            if (state.inCData || state.doCData) {
                if (!state.inCData) {
                    this._printer.printText("<![CDATA[");
                    state.inCData = true;
                }
                final int saveIndent = this._printer.getNextIndent();
                this._printer.setNextIndent(0);
                for (int end = start + length, index = start; index < end; ++index) {
                    final char ch = chars[index];
                    if (ch == ']' && index + 2 < end && chars[index + 1] == ']' && chars[index + 2] == '>') {
                        this._printer.printText("]]]]><![CDATA[>");
                        index += 2;
                    }
                    else if (!XML11Char.isXML11Valid(ch)) {
                        if (++index < end) {
                            this.surrogates(ch, chars[index]);
                        }
                        else {
                            this.fatalError("The character '" + ch + "' is an invalid XML character");
                        }
                    }
                    else if (this._encodingInfo.isPrintable(ch) && XML11Char.isXML11ValidLiteral(ch)) {
                        this._printer.printText(ch);
                    }
                    else {
                        this._printer.printText("]]>&#x");
                        this._printer.printText(Integer.toHexString(ch));
                        this._printer.printText(";<![CDATA[");
                    }
                }
                this._printer.setNextIndent(saveIndent);
            }
            else if (state.preserveSpace) {
                final int saveIndent = this._printer.getNextIndent();
                this._printer.setNextIndent(0);
                this.printText(chars, start, length, true, state.unescaped);
                this._printer.setNextIndent(saveIndent);
            }
            else {
                this.printText(chars, start, length, false, state.unescaped);
            }
        }
        catch (final IOException except) {
            throw new SAXException(except);
        }
    }
    
    @Override
    protected void printEscaped(final String source) throws IOException {
        for (int length = source.length(), i = 0; i < length; ++i) {
            final int ch = source.charAt(i);
            if (!XML11Char.isXML11Valid(ch)) {
                if (++i < length) {
                    this.surrogates(ch, source.charAt(i));
                }
                else {
                    this.fatalError("The character '" + (char)ch + "' is an invalid XML character");
                }
            }
            else if (ch == 10 || ch == 13 || ch == 9 || ch == 133 || ch == 8232) {
                this.printHex(ch);
            }
            else if (ch == 60) {
                this._printer.printText("&lt;");
            }
            else if (ch == 38) {
                this._printer.printText("&amp;");
            }
            else if (ch == 34) {
                this._printer.printText("&quot;");
            }
            else if (ch >= 32 && this._encodingInfo.isPrintable((char)ch)) {
                this._printer.printText((char)ch);
            }
            else {
                this.printHex(ch);
            }
        }
    }
    
    @Override
    protected final void printCDATAText(final String text) throws IOException {
        for (int length = text.length(), index = 0; index < length; ++index) {
            final char ch = text.charAt(index);
            if (ch == ']' && index + 2 < length && text.charAt(index + 1) == ']' && text.charAt(index + 2) == '>') {
                if (this.fDOMErrorHandler != null) {
                    if ((this.features & 0x10) == 0x0 && (this.features & 0x2) == 0x0) {
                        final String msg = DOMMessageFormatter.formatMessage("http://apache.org/xml/serializer", "EndingCDATA", null);
                        this.modifyDOMError(msg, (short)3, null, this.fCurrentNode);
                        final boolean continueProcess = this.fDOMErrorHandler.handleError(this.fDOMError);
                        if (!continueProcess) {
                            throw new IOException();
                        }
                    }
                    else {
                        final String msg = DOMMessageFormatter.formatMessage("http://apache.org/xml/serializer", "SplittingCDATA", null);
                        this.modifyDOMError(msg, (short)1, null, this.fCurrentNode);
                        this.fDOMErrorHandler.handleError(this.fDOMError);
                    }
                }
                this._printer.printText("]]]]><![CDATA[>");
                index += 2;
            }
            else if (!XML11Char.isXML11Valid(ch)) {
                if (++index < length) {
                    this.surrogates(ch, text.charAt(index));
                }
                else {
                    this.fatalError("The character '" + ch + "' is an invalid XML character");
                }
            }
            else if (this._encodingInfo.isPrintable(ch) && XML11Char.isXML11ValidLiteral(ch)) {
                this._printer.printText(ch);
            }
            else {
                this._printer.printText("]]>&#x");
                this._printer.printText(Integer.toHexString(ch));
                this._printer.printText(";<![CDATA[");
            }
        }
    }
    
    @Override
    protected final void printXMLChar(final int ch) throws IOException {
        if (ch == 13 || ch == 133 || ch == 8232) {
            this.printHex(ch);
        }
        else if (ch == 60) {
            this._printer.printText("&lt;");
        }
        else if (ch == 38) {
            this._printer.printText("&amp;");
        }
        else if (ch == 62) {
            this._printer.printText("&gt;");
        }
        else if (this._encodingInfo.isPrintable((char)ch) && XML11Char.isXML11ValidLiteral(ch)) {
            this._printer.printText((char)ch);
        }
        else {
            this.printHex(ch);
        }
    }
    
    @Override
    protected final void surrogates(final int high, final int low) throws IOException {
        if (XMLChar.isHighSurrogate(high)) {
            if (!XMLChar.isLowSurrogate(low)) {
                this.fatalError("The character '" + (char)low + "' is an invalid XML character");
            }
            else {
                final int supplemental = XMLChar.supplemental((char)high, (char)low);
                if (!XML11Char.isXML11Valid(supplemental)) {
                    this.fatalError("The character '" + (char)supplemental + "' is an invalid XML character");
                }
                else if (this.content().inCData) {
                    this._printer.printText("]]>&#x");
                    this._printer.printText(Integer.toHexString(supplemental));
                    this._printer.printText(";<![CDATA[");
                }
                else {
                    this.printHex(supplemental);
                }
            }
        }
        else {
            this.fatalError("The character '" + (char)high + "' is an invalid XML character");
        }
    }
    
    @Override
    protected void printText(final String text, final boolean preserveSpace, final boolean unescaped) throws IOException {
        final int length = text.length();
        if (preserveSpace) {
            for (int index = 0; index < length; ++index) {
                final char ch = text.charAt(index);
                if (!XML11Char.isXML11Valid(ch)) {
                    if (++index < length) {
                        this.surrogates(ch, text.charAt(index));
                    }
                    else {
                        this.fatalError("The character '" + ch + "' is an invalid XML character");
                    }
                }
                else if (unescaped && XML11Char.isXML11ValidLiteral(ch)) {
                    this._printer.printText(ch);
                }
                else {
                    this.printXMLChar(ch);
                }
            }
        }
        else {
            for (int index = 0; index < length; ++index) {
                final char ch = text.charAt(index);
                if (!XML11Char.isXML11Valid(ch)) {
                    if (++index < length) {
                        this.surrogates(ch, text.charAt(index));
                    }
                    else {
                        this.fatalError("The character '" + ch + "' is an invalid XML character");
                    }
                }
                else if (unescaped && XML11Char.isXML11ValidLiteral(ch)) {
                    this._printer.printText(ch);
                }
                else {
                    this.printXMLChar(ch);
                }
            }
        }
    }
    
    @Override
    protected void printText(final char[] chars, int start, int length, final boolean preserveSpace, final boolean unescaped) throws IOException {
        if (preserveSpace) {
            while (length-- > 0) {
                final char ch = chars[start++];
                if (!XML11Char.isXML11Valid(ch)) {
                    if (length-- > 0) {
                        this.surrogates(ch, chars[start++]);
                    }
                    else {
                        this.fatalError("The character '" + ch + "' is an invalid XML character");
                    }
                }
                else if (unescaped && XML11Char.isXML11ValidLiteral(ch)) {
                    this._printer.printText(ch);
                }
                else {
                    this.printXMLChar(ch);
                }
            }
        }
        else {
            while (length-- > 0) {
                final char ch = chars[start++];
                if (!XML11Char.isXML11Valid(ch)) {
                    if (length-- > 0) {
                        this.surrogates(ch, chars[start++]);
                    }
                    else {
                        this.fatalError("The character '" + ch + "' is an invalid XML character");
                    }
                }
                else if (unescaped && XML11Char.isXML11ValidLiteral(ch)) {
                    this._printer.printText(ch);
                }
                else {
                    this.printXMLChar(ch);
                }
            }
        }
    }
    
    @Override
    public boolean reset() {
        super.reset();
        return true;
    }
}
