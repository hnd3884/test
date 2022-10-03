package org.apache.tika.sax.xpath;

import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import java.util.LinkedList;
import org.apache.tika.sax.ContentHandlerDecorator;

public class MatchingContentHandler extends ContentHandlerDecorator
{
    private final LinkedList<Matcher> matchers;
    private Matcher matcher;
    
    public MatchingContentHandler(final ContentHandler delegate, final Matcher matcher) {
        super(delegate);
        this.matchers = new LinkedList<Matcher>();
        this.matcher = matcher;
    }
    
    @Override
    public void startElement(final String uri, final String localName, final String name, final Attributes attributes) throws SAXException {
        this.matchers.addFirst(this.matcher);
        this.matcher = this.matcher.descend(uri, localName);
        final AttributesImpl matches = new AttributesImpl();
        for (int i = 0; i < attributes.getLength(); ++i) {
            final String attributeURI = attributes.getURI(i);
            final String attributeName = attributes.getLocalName(i);
            if (this.matcher.matchesAttribute(attributeURI, attributeName)) {
                matches.addAttribute(attributeURI, attributeName, attributes.getQName(i), attributes.getType(i), attributes.getValue(i));
            }
        }
        if (this.matcher.matchesElement() || matches.getLength() > 0) {
            super.startElement(uri, localName, name, matches);
            if (!this.matcher.matchesElement()) {
                this.matcher = new CompositeMatcher(this.matcher, ElementMatcher.INSTANCE);
            }
        }
    }
    
    @Override
    public void endElement(final String uri, final String localName, final String name) throws SAXException {
        if (this.matcher.matchesElement()) {
            super.endElement(uri, localName, name);
        }
        if (!this.matchers.isEmpty()) {
            this.matcher = this.matchers.removeFirst();
        }
    }
    
    @Override
    public void characters(final char[] ch, final int start, final int length) throws SAXException {
        if (this.matcher.matchesText()) {
            super.characters(ch, start, length);
        }
    }
    
    @Override
    public void ignorableWhitespace(final char[] ch, final int start, final int length) throws SAXException {
        if (this.matcher.matchesText()) {
            super.ignorableWhitespace(ch, start, length);
        }
    }
    
    @Override
    public void processingInstruction(final String target, final String data) {
    }
    
    @Override
    public void skippedEntity(final String name) throws SAXException {
        if (this.matcher.matchesText()) {
            super.skippedEntity(name);
        }
    }
}
