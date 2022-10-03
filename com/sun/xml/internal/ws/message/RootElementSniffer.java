package com.sun.xml.internal.ws.message;

import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.SAXException;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

public final class RootElementSniffer extends DefaultHandler
{
    private String nsUri;
    private String localName;
    private Attributes atts;
    private final boolean parseAttributes;
    private static final SAXException aSAXException;
    private static final Attributes EMPTY_ATTRIBUTES;
    
    public RootElementSniffer(final boolean parseAttributes) {
        this.nsUri = "##error";
        this.localName = "##error";
        this.parseAttributes = parseAttributes;
    }
    
    public RootElementSniffer() {
        this(true);
    }
    
    @Override
    public void startElement(final String uri, final String localName, final String qName, final Attributes a) throws SAXException {
        this.nsUri = uri;
        this.localName = localName;
        if (this.parseAttributes) {
            if (a.getLength() == 0) {
                this.atts = RootElementSniffer.EMPTY_ATTRIBUTES;
            }
            else {
                this.atts = new AttributesImpl(a);
            }
        }
        throw RootElementSniffer.aSAXException;
    }
    
    public String getNsUri() {
        return this.nsUri;
    }
    
    public String getLocalName() {
        return this.localName;
    }
    
    public Attributes getAttributes() {
        return this.atts;
    }
    
    static {
        aSAXException = new SAXException();
        EMPTY_ATTRIBUTES = new AttributesImpl();
    }
}
