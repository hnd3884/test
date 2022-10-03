package org.apache.tika.sax;

import java.util.Collection;
import java.util.HashSet;
import java.util.Arrays;
import org.xml.sax.SAXException;
import java.io.UnsupportedEncodingException;
import java.io.OutputStream;
import java.util.Set;

public class ToHTMLContentHandler extends ToXMLContentHandler
{
    private static final Set<String> EMPTY_ELEMENTS;
    
    public ToHTMLContentHandler(final OutputStream stream, final String encoding) throws UnsupportedEncodingException {
        super(stream, encoding);
    }
    
    public ToHTMLContentHandler() {
    }
    
    @Override
    public void startDocument() throws SAXException {
    }
    
    @Override
    public void endElement(final String uri, final String localName, final String qName) throws SAXException {
        if (this.inStartElement) {
            this.write('>');
            this.inStartElement = false;
            if (ToHTMLContentHandler.EMPTY_ELEMENTS.contains(localName)) {
                this.namespaces.clear();
                return;
            }
        }
        super.endElement(uri, localName, qName);
    }
    
    static {
        EMPTY_ELEMENTS = new HashSet<String>(Arrays.asList("area", "base", "basefont", "br", "col", "frame", "hr", "img", "input", "isindex", "link", "meta", "param"));
    }
}
