package org.apache.tika.sax;

import java.util.Locale;
import org.xml.sax.Attributes;
import java.io.IOException;
import org.xml.sax.SAXException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.io.OutputStream;
import java.io.Writer;
import org.xml.sax.helpers.DefaultHandler;

public class ToTextContentHandler extends DefaultHandler
{
    private static final String STYLE = "STYLE";
    private static final String SCRIPT = "SCRIPT";
    private final Writer writer;
    private int styleDepth;
    private int scriptDepth;
    
    public ToTextContentHandler(final Writer writer) {
        this.styleDepth = 0;
        this.scriptDepth = 0;
        this.writer = writer;
    }
    
    @Deprecated
    public ToTextContentHandler(final OutputStream stream) {
        this(new OutputStreamWriter(stream, Charset.defaultCharset()));
    }
    
    public ToTextContentHandler(final OutputStream stream, final String encoding) throws UnsupportedEncodingException {
        this(new OutputStreamWriter(stream, encoding));
    }
    
    public ToTextContentHandler() {
        this(new StringWriter());
    }
    
    @Override
    public void characters(final char[] ch, final int start, final int length) throws SAXException {
        if (this.styleDepth + this.scriptDepth != 0) {
            return;
        }
        try {
            this.writer.write(ch, start, length);
        }
        catch (final IOException e) {
            throw new SAXException("Error writing: " + new String(ch, start, length), e);
        }
    }
    
    @Override
    public void ignorableWhitespace(final char[] ch, final int start, final int length) throws SAXException {
        this.characters(ch, start, length);
    }
    
    @Override
    public void endDocument() throws SAXException {
        try {
            this.writer.flush();
        }
        catch (final IOException e) {
            throw new SAXException("Error flushing character output", e);
        }
    }
    
    @Override
    public void startElement(final String uri, final String localName, final String qName, final Attributes atts) throws SAXException {
        final String uc = (qName == null) ? "" : qName.toUpperCase(Locale.ENGLISH);
        if (uc.equals("STYLE")) {
            ++this.styleDepth;
        }
        if (uc.equals("SCRIPT")) {
            ++this.scriptDepth;
        }
    }
    
    @Override
    public void endElement(final String uri, final String localName, final String qName) throws SAXException {
        final String uc = (qName == null) ? "" : qName.toUpperCase(Locale.ENGLISH);
        if (uc.equals("STYLE")) {
            --this.styleDepth;
        }
        if (uc.equals("SCRIPT")) {
            --this.scriptDepth;
        }
    }
    
    @Override
    public String toString() {
        return this.writer.toString();
    }
}
