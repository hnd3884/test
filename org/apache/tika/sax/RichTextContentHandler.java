package org.apache.tika.sax;

import org.xml.sax.SAXException;
import org.xml.sax.Attributes;
import java.io.Writer;

public class RichTextContentHandler extends WriteOutContentHandler
{
    public RichTextContentHandler(final Writer writer) {
        super(writer);
    }
    
    @Override
    public void startElement(final String uri, final String localName, final String qName, final Attributes attributes) throws SAXException {
        super.startElement(uri, localName, qName, attributes);
        if ("img".equals(localName) && attributes.getValue("alt") != null) {
            final String nfo = "[image: " + attributes.getValue("alt") + ']';
            this.characters(nfo.toCharArray(), 0, nfo.length());
        }
        if ("a".equals(localName) && attributes.getValue("name") != null) {
            final String nfo = "[bookmark: " + attributes.getValue("name") + ']';
            this.characters(nfo.toCharArray(), 0, nfo.length());
        }
    }
}
