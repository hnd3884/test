package org.apache.tika.sax;

import org.xml.sax.SAXException;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;

public class TextAndAttributeContentHandler extends TextContentHandler
{
    public TextAndAttributeContentHandler(final ContentHandler delegate) {
        this(delegate, false);
    }
    
    public TextAndAttributeContentHandler(final ContentHandler delegate, final boolean addSpaceBetweenElements) {
        super(delegate, addSpaceBetweenElements);
    }
    
    @Override
    public void startElement(final String uri, final String localName, final String qName, final Attributes attributes) throws SAXException {
        super.startElement(uri, localName, qName, attributes);
        final int attributeLength = attributes.getLength();
        if (attributeLength > 0) {
            final char[] elementName = (localName.trim() + " ").toCharArray();
            this.characters(elementName, 0, elementName.length);
            for (int i = 0; i < attributeLength; ++i) {
                final char[] attributeName = (attributes.getLocalName(i).trim() + " ").toCharArray();
                final char[] attributeValue = (attributes.getValue(i).trim() + " ").toCharArray();
                this.characters(attributeName, 0, attributeName.length);
                this.characters(attributeValue, 0, attributeValue.length);
            }
        }
    }
}
