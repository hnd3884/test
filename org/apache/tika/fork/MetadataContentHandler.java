package org.apache.tika.fork;

import org.xml.sax.SAXException;
import org.xml.sax.Attributes;
import org.apache.tika.metadata.Metadata;
import org.xml.sax.helpers.DefaultHandler;

class MetadataContentHandler extends DefaultHandler
{
    private final Metadata metadata;
    
    public MetadataContentHandler(final Metadata metadata) {
        this.metadata = metadata;
    }
    
    @Override
    public void startElement(final String uri, final String local, final String name, final Attributes attributes) throws SAXException {
        if ("meta".equals(local)) {
            final String aname = attributes.getValue("name");
            final String content = attributes.getValue("content");
            this.metadata.add(aname, content);
        }
    }
}
