package com.sun.xml.internal.txw2.output;

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
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.XMLFilterImpl;

public class XMLWriter extends XMLFilterImpl implements LexicalHandler
{
    private final HashMap locallyDeclaredPrefix;
    private final Attributes EMPTY_ATTS;
    private boolean inCDATA;
    private int elementLevel;
    private Writer output;
    private String encoding;
    private boolean writeXmlDecl;
    private String header;
    private final CharacterEscapeHandler escapeHandler;
    private boolean startTagIsClosed;
    
    public XMLWriter(final Writer writer, final String encoding, final CharacterEscapeHandler _escapeHandler) {
        this.locallyDeclaredPrefix = new HashMap();
        this.EMPTY_ATTS = new AttributesImpl();
        this.inCDATA = false;
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
    
    public void setEncoding(final String encoding) {
        this.encoding = encoding;
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
                    e = " encoding=\"" + this.encoding + "\"";
                }
                this.write("<?xml version=\"1.0\"" + e + " standalone=\"yes\"?>\n");
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
    
    @Override
    public void endDocument() throws SAXException {
        try {
            if (!this.startTagIsClosed) {
                this.write("/>");
                this.startTagIsClosed = true;
            }
            this.write('\n');
            super.endDocument();
            try {
                this.flush();
            }
            catch (final IOException e) {
                throw new SAXException(e);
            }
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
            this.writeName(uri, localName, qName, true);
            this.writeAttributes(atts);
            if (!this.locallyDeclaredPrefix.isEmpty()) {
                for (final Map.Entry e : this.locallyDeclaredPrefix.entrySet()) {
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
                this.writeName(uri, localName, qName, true);
                this.write('>');
            }
            else {
                this.write("/>");
                this.startTagIsClosed = true;
            }
            if (this.elementLevel == 1) {
                this.write('\n');
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
            if (this.inCDATA) {
                this.output.write(ch, start, len);
            }
            else {
                this.writeEsc(ch, start, len, false);
            }
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
    
    @Override
    public void startDTD(final String name, final String publicId, final String systemId) throws SAXException {
    }
    
    @Override
    public void endDTD() throws SAXException {
    }
    
    @Override
    public void startEntity(final String name) throws SAXException {
    }
    
    @Override
    public void endEntity(final String name) throws SAXException {
    }
    
    @Override
    public void startCDATA() throws SAXException {
        try {
            if (!this.startTagIsClosed) {
                this.write('>');
                this.startTagIsClosed = true;
            }
            this.write("<![CDATA[");
            this.inCDATA = true;
        }
        catch (final IOException e) {
            throw new SAXException(e);
        }
    }
    
    @Override
    public void endCDATA() throws SAXException {
        try {
            this.inCDATA = false;
            this.write("]]>");
        }
        catch (final IOException e) {
            throw new SAXException(e);
        }
    }
    
    @Override
    public void comment(final char[] ch, final int start, final int length) throws SAXException {
        try {
            this.output.write("<!--");
            this.output.write(ch, start, length);
            this.output.write("-->");
        }
        catch (final IOException e) {
            throw new SAXException(e);
        }
    }
    
    private void write(final char c) throws IOException {
        this.output.write(c);
    }
    
    private void write(final String s) throws IOException {
        this.output.write(s);
    }
    
    private void writeAttributes(final Attributes atts) throws IOException, SAXException {
        for (int len = atts.getLength(), i = 0; i < len; ++i) {
            final char[] ch = atts.getValue(i).toCharArray();
            this.write(' ');
            this.writeName(atts.getURI(i), atts.getLocalName(i), atts.getQName(i), false);
            this.write("=\"");
            this.writeEsc(ch, 0, ch.length, true);
            this.write('\"');
        }
    }
    
    private void writeEsc(final char[] ch, final int start, final int length, final boolean isAttVal) throws SAXException, IOException {
        this.escapeHandler.escape(ch, start, length, isAttVal, this.output);
    }
    
    private void writeName(final String uri, final String localName, final String qName, final boolean isElement) throws IOException {
        this.write(qName);
    }
}
