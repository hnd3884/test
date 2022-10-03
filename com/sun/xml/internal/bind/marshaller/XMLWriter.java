package com.sun.xml.internal.bind.marshaller;

import java.util.Iterator;
import java.util.Map;
import org.xml.sax.SAXException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.IOException;
import org.xml.sax.helpers.AttributesImpl;
import java.io.Writer;
import org.xml.sax.Attributes;
import java.util.HashMap;
import org.xml.sax.helpers.XMLFilterImpl;

public class XMLWriter extends XMLFilterImpl
{
    private final HashMap<String, String> locallyDeclaredPrefix;
    private final Attributes EMPTY_ATTS;
    private int elementLevel;
    private Writer output;
    private String encoding;
    private boolean writeXmlDecl;
    private String header;
    private final CharacterEscapeHandler escapeHandler;
    private boolean startTagIsClosed;
    
    public XMLWriter(final Writer writer, final String encoding, final CharacterEscapeHandler _escapeHandler) {
        this.locallyDeclaredPrefix = new HashMap<String, String>();
        this.EMPTY_ATTS = new AttributesImpl();
        this.elementLevel = 0;
        this.writeXmlDecl = true;
        this.header = null;
        this.startTagIsClosed = true;
        this.init(writer, encoding);
        this.escapeHandler = _escapeHandler;
    }
    
    public XMLWriter(final Writer writer, final String encoding) {
        this(writer, encoding, DumbEscapeHandler.theInstance);
    }
    
    private void init(final Writer writer, final String encoding) {
        this.setOutput(writer, encoding);
    }
    
    public void reset() {
        this.elementLevel = 0;
        this.startTagIsClosed = true;
    }
    
    public void flush() throws IOException {
        this.output.flush();
    }
    
    public void setOutput(final Writer writer, final String _encoding) {
        if (writer == null) {
            this.output = new OutputStreamWriter(System.out);
        }
        else {
            this.output = writer;
        }
        this.encoding = _encoding;
    }
    
    public void setXmlDecl(final boolean _writeXmlDecl) {
        this.writeXmlDecl = _writeXmlDecl;
    }
    
    public void setHeader(final String _header) {
        this.header = _header;
    }
    
    @Override
    public void startPrefixMapping(final String prefix, final String uri) throws SAXException {
        this.locallyDeclaredPrefix.put(prefix, uri);
    }
    
    @Override
    public void startDocument() throws SAXException {
        try {
            this.reset();
            if (this.writeXmlDecl) {
                String e = "";
                if (this.encoding != null) {
                    e = " encoding=\"" + this.encoding + '\"';
                }
                this.writeXmlDecl("<?xml version=\"1.0\"" + e + " standalone=\"yes\"?>");
            }
            if (this.header != null) {
                this.write(this.header);
            }
            super.startDocument();
        }
        catch (final IOException e2) {
            throw new SAXException(e2);
        }
    }
    
    protected void writeXmlDecl(final String decl) throws IOException {
        this.write(decl);
    }
    
    @Override
    public void endDocument() throws SAXException {
        try {
            super.endDocument();
            this.flush();
        }
        catch (final IOException e) {
            throw new SAXException(e);
        }
    }
    
    @Override
    public void startElement(final String uri, final String localName, final String qName, final Attributes atts) throws SAXException {
        try {
            if (!this.startTagIsClosed) {
                this.write(">");
            }
            ++this.elementLevel;
            this.write('<');
            this.write(qName);
            this.writeAttributes(atts);
            if (!this.locallyDeclaredPrefix.isEmpty()) {
                for (final Map.Entry<String, String> e : this.locallyDeclaredPrefix.entrySet()) {
                    final String p = e.getKey();
                    String u = e.getValue();
                    if (u == null) {
                        u = "";
                    }
                    this.write(' ');
                    if ("".equals(p)) {
                        this.write("xmlns=\"");
                    }
                    else {
                        this.write("xmlns:");
                        this.write(p);
                        this.write("=\"");
                    }
                    final char[] ch = u.toCharArray();
                    this.writeEsc(ch, 0, ch.length, true);
                    this.write('\"');
                }
                this.locallyDeclaredPrefix.clear();
            }
            super.startElement(uri, localName, qName, atts);
            this.startTagIsClosed = false;
        }
        catch (final IOException e2) {
            throw new SAXException(e2);
        }
    }
    
    @Override
    public void endElement(final String uri, final String localName, final String qName) throws SAXException {
        try {
            if (this.startTagIsClosed) {
                this.write("</");
                this.write(qName);
                this.write('>');
            }
            else {
                this.write("/>");
                this.startTagIsClosed = true;
            }
            super.endElement(uri, localName, qName);
            --this.elementLevel;
        }
        catch (final IOException e) {
            throw new SAXException(e);
        }
    }
    
    @Override
    public void characters(final char[] ch, final int start, final int len) throws SAXException {
        try {
            if (!this.startTagIsClosed) {
                this.write('>');
                this.startTagIsClosed = true;
            }
            this.writeEsc(ch, start, len, false);
            super.characters(ch, start, len);
        }
        catch (final IOException e) {
            throw new SAXException(e);
        }
    }
    
    @Override
    public void ignorableWhitespace(final char[] ch, final int start, final int length) throws SAXException {
        try {
            this.writeEsc(ch, start, length, false);
            super.ignorableWhitespace(ch, start, length);
        }
        catch (final IOException e) {
            throw new SAXException(e);
        }
    }
    
    @Override
    public void processingInstruction(final String target, final String data) throws SAXException {
        try {
            if (!this.startTagIsClosed) {
                this.write('>');
                this.startTagIsClosed = true;
            }
            this.write("<?");
            this.write(target);
            this.write(' ');
            this.write(data);
            this.write("?>");
            if (this.elementLevel < 1) {
                this.write('\n');
            }
            super.processingInstruction(target, data);
        }
        catch (final IOException e) {
            throw new SAXException(e);
        }
    }
    
    public void startElement(final String uri, final String localName) throws SAXException {
        this.startElement(uri, localName, "", this.EMPTY_ATTS);
    }
    
    public void startElement(final String localName) throws SAXException {
        this.startElement("", localName, "", this.EMPTY_ATTS);
    }
    
    public void endElement(final String uri, final String localName) throws SAXException {
        this.endElement(uri, localName, "");
    }
    
    public void endElement(final String localName) throws SAXException {
        this.endElement("", localName, "");
    }
    
    public void dataElement(final String uri, final String localName, final String qName, final Attributes atts, final String content) throws SAXException {
        this.startElement(uri, localName, qName, atts);
        this.characters(content);
        this.endElement(uri, localName, qName);
    }
    
    public void dataElement(final String uri, final String localName, final String content) throws SAXException {
        this.dataElement(uri, localName, "", this.EMPTY_ATTS, content);
    }
    
    public void dataElement(final String localName, final String content) throws SAXException {
        this.dataElement("", localName, "", this.EMPTY_ATTS, content);
    }
    
    public void characters(final String data) throws SAXException {
        try {
            if (!this.startTagIsClosed) {
                this.write('>');
                this.startTagIsClosed = true;
            }
            final char[] ch = data.toCharArray();
            this.characters(ch, 0, ch.length);
        }
        catch (final IOException e) {
            throw new SAXException(e);
        }
    }
    
    protected final void write(final char c) throws IOException {
        this.output.write(c);
    }
    
    protected final void write(final String s) throws IOException {
        this.output.write(s);
    }
    
    private void writeAttributes(final Attributes atts) throws IOException {
        for (int len = atts.getLength(), i = 0; i < len; ++i) {
            final char[] ch = atts.getValue(i).toCharArray();
            this.write(' ');
            this.write(atts.getQName(i));
            this.write("=\"");
            this.writeEsc(ch, 0, ch.length, true);
            this.write('\"');
        }
    }
    
    private void writeEsc(final char[] ch, final int start, final int length, final boolean isAttVal) throws IOException {
        this.escapeHandler.escape(ch, start, length, isAttVal, this.output);
    }
}
