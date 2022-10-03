package org.apache.tika.fork;

import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import java.io.IOException;
import org.xml.sax.SAXException;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import org.xml.sax.ContentHandler;

class ContentHandlerProxy implements ContentHandler, ForkProxy
{
    public static final int START_DOCUMENT = 1;
    public static final int END_DOCUMENT = 2;
    public static final int START_PREFIX_MAPPING = 3;
    public static final int END_PREFIX_MAPPING = 4;
    public static final int START_ELEMENT = 5;
    public static final int END_ELEMENT = 6;
    public static final int CHARACTERS = 7;
    public static final int IGNORABLE_WHITESPACE = 8;
    public static final int PROCESSING_INSTRUCTION = 9;
    public static final int SKIPPED_ENTITY = 10;
    private static final long serialVersionUID = 737511106054617524L;
    private final int resource;
    private transient DataOutputStream output;
    
    public ContentHandlerProxy(final int resource) {
        this.resource = resource;
    }
    
    @Override
    public void init(final DataInputStream input, final DataOutputStream output) {
        this.output = output;
    }
    
    private void sendRequest(final int type) throws SAXException {
        try {
            this.output.writeByte(3);
            this.output.writeByte(this.resource);
            this.output.writeByte(type);
        }
        catch (final IOException e) {
            throw new SAXException("Unexpected fork proxy problem", e);
        }
    }
    
    private void sendString(final String string) throws SAXException {
        try {
            if (string != null) {
                this.output.writeBoolean(true);
                this.writeString(string);
            }
            else {
                this.output.writeBoolean(false);
            }
        }
        catch (final IOException e) {
            throw new SAXException("Unexpected fork proxy problem", e);
        }
    }
    
    private void writeString(final String string) throws IOException {
        final int max = 21845;
        final int frags = (int)Math.ceil(string.length() / (double)max);
        this.output.writeInt(frags);
        for (int i = 0; i < frags; ++i) {
            final int end = (i < frags - 1) ? ((i + 1) * max) : string.length();
            this.output.writeUTF(string.substring(i * max, end));
        }
    }
    
    private void sendCharacters(final char[] ch, final int start, final int length) throws SAXException {
        try {
            this.writeString(new String(ch, start, length));
        }
        catch (final IOException e) {
            throw new SAXException("Unexpected fork proxy problem", e);
        }
    }
    
    private void doneSending() throws SAXException {
        try {
            this.output.flush();
        }
        catch (final IOException e) {
            throw new SAXException("Unexpected fork proxy problem", e);
        }
    }
    
    @Override
    public void setDocumentLocator(final Locator locator) {
    }
    
    @Override
    public void startDocument() throws SAXException {
        this.sendRequest(1);
        this.doneSending();
    }
    
    @Override
    public void endDocument() throws SAXException {
        this.sendRequest(2);
        this.doneSending();
    }
    
    @Override
    public void startPrefixMapping(final String prefix, final String uri) throws SAXException {
        this.sendRequest(3);
        this.sendString(prefix);
        this.sendString(uri);
        this.doneSending();
    }
    
    @Override
    public void endPrefixMapping(final String prefix) throws SAXException {
        this.sendRequest(4);
        this.sendString(prefix);
        this.doneSending();
    }
    
    @Override
    public void startElement(final String uri, final String localName, final String qName, final Attributes atts) throws SAXException {
        this.sendRequest(5);
        this.sendString(uri);
        this.sendString(localName);
        this.sendString(qName);
        int n = -1;
        if (atts != null) {
            n = atts.getLength();
        }
        try {
            this.output.writeInt(n);
        }
        catch (final IOException e) {
            throw new SAXException("Unexpected fork proxy problem", e);
        }
        for (int i = 0; i < n; ++i) {
            this.sendString(atts.getURI(i));
            this.sendString(atts.getLocalName(i));
            this.sendString(atts.getQName(i));
            this.sendString(atts.getType(i));
            this.sendString(atts.getValue(i));
        }
        this.doneSending();
    }
    
    @Override
    public void endElement(final String uri, final String localName, final String qName) throws SAXException {
        this.sendRequest(6);
        this.sendString(uri);
        this.sendString(localName);
        this.sendString(qName);
        this.doneSending();
    }
    
    @Override
    public void characters(final char[] ch, final int start, final int length) throws SAXException {
        this.sendRequest(7);
        this.sendCharacters(ch, start, length);
        this.doneSending();
    }
    
    @Override
    public void ignorableWhitespace(final char[] ch, final int start, final int length) throws SAXException {
        this.sendRequest(8);
        this.sendCharacters(ch, start, length);
        this.doneSending();
    }
    
    @Override
    public void processingInstruction(final String target, final String data) throws SAXException {
        this.sendRequest(9);
        this.sendString(target);
        this.sendString(data);
        this.doneSending();
    }
    
    @Override
    public void skippedEntity(final String name) throws SAXException {
        this.sendRequest(10);
        this.sendString(name);
        this.doneSending();
    }
}
