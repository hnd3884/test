package org.apache.tika.fork;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.AttributesImpl;
import java.io.IOException;
import org.xml.sax.SAXException;
import java.io.DataOutputStream;
import java.io.DataInputStream;
import org.xml.sax.ContentHandler;

class ContentHandlerResource implements ForkResource
{
    private final ContentHandler handler;
    
    public ContentHandlerResource(final ContentHandler handler) {
        this.handler = handler;
    }
    
    @Override
    public Throwable process(final DataInputStream input, final DataOutputStream output) throws IOException {
        try {
            this.internalProcess(input);
            return null;
        }
        catch (final SAXException e) {
            return e;
        }
    }
    
    private void internalProcess(final DataInputStream input) throws IOException, SAXException {
        final int type = input.readUnsignedByte();
        if (type == 1) {
            this.handler.startDocument();
        }
        else if (type == 2) {
            this.handler.endDocument();
        }
        else if (type == 3) {
            this.handler.startPrefixMapping(this.readString(input), this.readString(input));
        }
        else if (type == 4) {
            this.handler.endPrefixMapping(this.readString(input));
        }
        else if (type == 5) {
            final String uri = this.readString(input);
            final String localName = this.readString(input);
            final String qName = this.readString(input);
            AttributesImpl atts = null;
            final int n = input.readInt();
            if (n >= 0) {
                atts = new AttributesImpl();
                for (int i = 0; i < n; ++i) {
                    atts.addAttribute(this.readString(input), this.readString(input), this.readString(input), this.readString(input), this.readString(input));
                }
            }
            this.handler.startElement(uri, localName, qName, atts);
        }
        else if (type == 6) {
            final String uri = this.readString(input);
            final String localName = this.readString(input);
            final String qName = this.readString(input);
            this.handler.endElement(uri, localName, qName);
        }
        else if (type == 7) {
            final char[] ch = this.readCharacters(input);
            this.handler.characters(ch, 0, ch.length);
        }
        else if (type == 8) {
            final char[] ch = this.readCharacters(input);
            this.handler.characters(ch, 0, ch.length);
        }
        else if (type == 9) {
            this.handler.processingInstruction(this.readString(input), this.readString(input));
        }
        else if (type == 10) {
            this.handler.skippedEntity(this.readString(input));
        }
    }
    
    private String readString(final DataInputStream input) throws IOException {
        if (input.readBoolean()) {
            return this.readStringUTF(input);
        }
        return null;
    }
    
    private char[] readCharacters(final DataInputStream input) throws IOException {
        return this.readStringUTF(input).toCharArray();
    }
    
    private String readStringUTF(final DataInputStream input) throws IOException {
        final int frags = input.readInt();
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < frags; ++i) {
            sb.append(input.readUTF());
        }
        return sb.toString();
    }
}
