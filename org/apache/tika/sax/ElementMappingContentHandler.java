package org.apache.tika.sax;

import org.xml.sax.helpers.AttributesImpl;
import java.util.Collections;
import org.xml.sax.SAXException;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import javax.xml.namespace.QName;
import java.util.Map;

public class ElementMappingContentHandler extends ContentHandlerDecorator
{
    private final Map<QName, TargetElement> mappings;
    
    public ElementMappingContentHandler(final ContentHandler handler, final Map<QName, TargetElement> mappings) {
        super(handler);
        this.mappings = mappings;
    }
    
    protected static final String getQNameAsString(final QName qname) {
        final String prefix = qname.getPrefix();
        if (prefix.length() > 0) {
            return prefix + ":" + qname.getLocalPart();
        }
        return qname.getLocalPart();
    }
    
    @Override
    public void startElement(final String namespaceURI, final String localName, final String qName, final Attributes atts) throws SAXException {
        final TargetElement mapping = this.mappings.get(new QName(namespaceURI, localName));
        if (mapping != null) {
            final QName tag = mapping.getMappedTagName();
            super.startElement(tag.getNamespaceURI(), tag.getLocalPart(), getQNameAsString(tag), mapping.mapAttributes(atts));
        }
    }
    
    @Override
    public void endElement(final String namespaceURI, final String localName, final String qName) throws SAXException {
        final TargetElement mapping = this.mappings.get(new QName(namespaceURI, localName));
        if (mapping != null) {
            final QName tag = mapping.getMappedTagName();
            super.endElement(tag.getNamespaceURI(), tag.getLocalPart(), getQNameAsString(tag));
        }
    }
    
    public static class TargetElement
    {
        private final QName mappedTagName;
        private final Map<QName, QName> attributesMapping;
        
        public TargetElement(final QName mappedTagName, final Map<QName, QName> attributesMapping) {
            this.mappedTagName = mappedTagName;
            this.attributesMapping = attributesMapping;
        }
        
        public TargetElement(final String mappedTagURI, final String mappedTagLocalName, final Map<QName, QName> attributesMapping) {
            this(new QName(mappedTagURI, mappedTagLocalName), attributesMapping);
        }
        
        public TargetElement(final QName mappedTagName) {
            this(mappedTagName, Collections.emptyMap());
        }
        
        public TargetElement(final String mappedTagURI, final String mappedTagLocalName) {
            this(mappedTagURI, mappedTagLocalName, Collections.emptyMap());
        }
        
        public QName getMappedTagName() {
            return this.mappedTagName;
        }
        
        public Map<QName, QName> getAttributesMapping() {
            return this.attributesMapping;
        }
        
        public Attributes mapAttributes(final Attributes atts) {
            final AttributesImpl natts = new AttributesImpl();
            for (int i = 0; i < atts.getLength(); ++i) {
                final QName name = this.attributesMapping.get(new QName(atts.getURI(i), atts.getLocalName(i)));
                if (name != null) {
                    natts.addAttribute(name.getNamespaceURI(), name.getLocalPart(), ElementMappingContentHandler.getQNameAsString(name), atts.getType(i), atts.getValue(i));
                }
            }
            return natts;
        }
    }
}
