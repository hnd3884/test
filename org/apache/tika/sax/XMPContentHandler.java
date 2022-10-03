package org.apache.tika.sax;

import java.util.Iterator;
import org.apache.tika.metadata.Property;
import org.apache.tika.metadata.Metadata;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.SAXException;
import org.xml.sax.ContentHandler;
import org.xml.sax.Attributes;

public class XMPContentHandler extends SafeContentHandler
{
    public static final String RDF = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
    public static final String XMP = "http://ns.adobe.com/xap/1.0/";
    private static final Attributes EMPTY_ATTRIBUTES;
    private String prefix;
    private String uri;
    
    public XMPContentHandler(final ContentHandler handler) {
        super(handler);
        this.prefix = null;
        this.uri = null;
    }
    
    @Override
    public void startDocument() throws SAXException {
        super.startDocument();
        this.startPrefixMapping("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
        this.startPrefixMapping("xmp", "http://ns.adobe.com/xap/1.0/");
        this.startElement("http://www.w3.org/1999/02/22-rdf-syntax-ns#", "RDF", "rdf:RDF", XMPContentHandler.EMPTY_ATTRIBUTES);
    }
    
    @Override
    public void endDocument() throws SAXException {
        this.endElement("http://www.w3.org/1999/02/22-rdf-syntax-ns#", "RDF", "rdf:RDF");
        this.endPrefixMapping("xmp");
        this.endPrefixMapping("rdf");
        super.endDocument();
    }
    
    public void startDescription(final String about, final String prefix, final String uri) throws SAXException {
        this.startPrefixMapping(this.prefix = prefix, this.uri = uri);
        final AttributesImpl attributes = new AttributesImpl();
        attributes.addAttribute("http://www.w3.org/1999/02/22-rdf-syntax-ns#", "about", "rdf:about", "CDATA", about);
        this.startElement("http://www.w3.org/1999/02/22-rdf-syntax-ns#", "Description", "rdf:Description", attributes);
    }
    
    public void endDescription() throws SAXException {
        this.endElement("http://www.w3.org/1999/02/22-rdf-syntax-ns#", "Description", "rdf:Description");
        this.endPrefixMapping(this.prefix);
        this.uri = null;
        this.prefix = null;
    }
    
    public void property(final String name, final String value) throws SAXException {
        final String qname = this.prefix + ":" + name;
        this.startElement(this.uri, name, qname, XMPContentHandler.EMPTY_ATTRIBUTES);
        this.characters(value.toCharArray(), 0, value.length());
        this.endElement(this.uri, name, qname);
    }
    
    public void metadata(final Metadata metadata) throws SAXException {
        this.description(metadata, "xmp", "http://ns.adobe.com/xap/1.0/");
        this.description(metadata, "dc", "http://purl.org/dc/elements/1.1/");
        this.description(metadata, "xmpTPg", "http://ns.adobe.com/xap/1.0/t/pg/");
        this.description(metadata, "xmpRigths", "http://ns.adobe.com/xap/1.0/rights/");
        this.description(metadata, "xmpMM", "http://ns.adobe.com/xap/1.0/mm/");
        this.description(metadata, "xmpidq", "http://ns.adobe.com/xmp/identifier/qual/1.0/");
        this.description(metadata, "xmpBJ", "http://ns.adobe.com/xap/1.0/bj/");
        this.description(metadata, "xmpDM", "http://ns.adobe.com/xmp/1.0/DynamicMedia/");
        this.description(metadata, "pdf", "http://ns.adobe.com/pdf/1.3/");
        this.description(metadata, "photoshop", "s http://ns.adobe.com/photoshop/1.0/");
        this.description(metadata, "crs", "http://ns.adobe.com/camera-raw-settings/1.0/");
        this.description(metadata, "tiff", "http://ns.adobe.com/tiff/1.0/");
        this.description(metadata, "exif", "http://ns.adobe.com/exif/1.0/");
        this.description(metadata, "aux", "http://ns.adobe.com/exif/1.0/aux/");
    }
    
    private void description(final Metadata metadata, final String prefix, final String uri) throws SAXException {
        int count = 0;
        for (final Property property : Property.getProperties(prefix)) {
            final String value = metadata.get(property);
            if (value != null) {
                if (count++ == 0) {
                    this.startDescription("", prefix, uri);
                }
                this.property(property.getName().substring(prefix.length() + 1), value);
            }
        }
        if (count > 0) {
            this.endDescription();
        }
    }
    
    static {
        EMPTY_ATTRIBUTES = new AttributesImpl();
    }
}
