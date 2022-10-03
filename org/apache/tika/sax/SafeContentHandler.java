package org.apache.tika.sax;

import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.ContentHandler;

public class SafeContentHandler extends ContentHandlerDecorator
{
    private static final char[] REPLACEMENT;
    private final Output charactersOutput;
    private final Output ignorableWhitespaceOutput;
    
    public SafeContentHandler(final ContentHandler handler) {
        super(handler);
        this.charactersOutput = ((x$0, x$1, x$2) -> ContentHandlerDecorator.this.characters(x$0, x$1, x$2));
        this.ignorableWhitespaceOutput = ((x$0, x$1, x$2) -> ContentHandlerDecorator.this.ignorableWhitespace(x$0, x$1, x$2));
    }
    
    private void filter(final char[] ch, int start, final int length, final Output output) throws SAXException {
        final int end = start + length;
        int j;
        for (int i = start; i < end; i = j) {
            final int c = Character.codePointAt(ch, i, end);
            j = i + Character.charCount(c);
            if (this.isInvalid(c)) {
                if (i > start) {
                    output.write(ch, start, i - start);
                }
                this.writeReplacement(output);
                start = j;
            }
        }
        output.write(ch, start, end - start);
    }
    
    private boolean isInvalid(final String value) {
        final char[] ch = value.toCharArray();
        int c;
        for (int i = 0; i < ch.length; i += Character.charCount(c)) {
            c = Character.codePointAt(ch, i);
            if (this.isInvalid(c)) {
                return true;
            }
        }
        return false;
    }
    
    protected boolean isInvalid(final int ch) {
        if (ch < 32) {
            return ch != 9 && ch != 10 && ch != 13;
        }
        if (ch < 57344) {
            return ch > 55295;
        }
        if (ch < 65536) {
            return ch > 65533;
        }
        return ch > 1114111;
    }
    
    protected void writeReplacement(final Output output) throws SAXException {
        output.write(SafeContentHandler.REPLACEMENT, 0, SafeContentHandler.REPLACEMENT.length);
    }
    
    @Override
    public void startElement(final String uri, final String localName, final String name, Attributes atts) throws SAXException {
        for (int i = 0; i < atts.getLength(); ++i) {
            if (this.isInvalid(atts.getValue(i))) {
                final AttributesImpl filtered = new AttributesImpl();
                for (int j = 0; j < atts.getLength(); ++j) {
                    String value = atts.getValue(j);
                    if (j >= i && this.isInvalid(value)) {
                        final Output buffer = new StringOutput();
                        this.filter(value.toCharArray(), 0, value.length(), buffer);
                        value = buffer.toString();
                    }
                    filtered.addAttribute(atts.getURI(j), atts.getLocalName(j), atts.getQName(j), atts.getType(j), value);
                }
                atts = filtered;
                break;
            }
        }
        super.startElement(uri, localName, name, atts);
    }
    
    @Override
    public void endElement(final String uri, final String localName, final String name) throws SAXException {
        super.endElement(uri, localName, name);
    }
    
    @Override
    public void endDocument() throws SAXException {
        super.endDocument();
    }
    
    @Override
    public void characters(final char[] ch, final int start, final int length) throws SAXException {
        this.filter(ch, start, length, this.charactersOutput);
    }
    
    @Override
    public void ignorableWhitespace(final char[] ch, final int start, final int length) throws SAXException {
        this.filter(ch, start, length, this.ignorableWhitespaceOutput);
    }
    
    static {
        REPLACEMENT = new char[] { '\ufffd' };
    }
    
    private static class StringOutput implements Output
    {
        private final StringBuilder builder;
        
        private StringOutput() {
            this.builder = new StringBuilder();
        }
        
        @Override
        public void write(final char[] ch, final int start, final int length) {
            this.builder.append(ch, start, length);
        }
        
        @Override
        public String toString() {
            return this.builder.toString();
        }
    }
    
    protected interface Output
    {
        void write(final char[] p0, final int p1, final int p2) throws SAXException;
    }
}
