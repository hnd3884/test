package org.openxmlformats.schemas.officeDocument.x2006.relationships;

import org.apache.xmlbeans.xml.stream.XMLStreamException;
import org.apache.xmlbeans.xml.stream.XMLInputStream;
import org.w3c.dom.Node;
import javax.xml.stream.XMLStreamReader;
import java.io.Reader;
import java.io.InputStream;
import java.net.URL;
import java.io.IOException;
import java.io.File;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlOptions;
import org.apache.xmlbeans.SchemaTypeLoader;
import java.lang.ref.SoftReference;
import org.apache.xmlbeans.XmlBeans;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlString;

public interface STRelationshipId extends XmlString
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(STRelationshipId.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("strelationshipid1e94type");
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        public static STRelationshipId newValue(final Object o) {
            return (STRelationshipId)STRelationshipId.type.newValue(o);
        }
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(STRelationshipId.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static STRelationshipId newInstance() {
            return (STRelationshipId)getTypeLoader().newInstance(STRelationshipId.type, (XmlOptions)null);
        }
        
        public static STRelationshipId newInstance(final XmlOptions xmlOptions) {
            return (STRelationshipId)getTypeLoader().newInstance(STRelationshipId.type, xmlOptions);
        }
        
        public static STRelationshipId parse(final String s) throws XmlException {
            return (STRelationshipId)getTypeLoader().parse(s, STRelationshipId.type, (XmlOptions)null);
        }
        
        public static STRelationshipId parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (STRelationshipId)getTypeLoader().parse(s, STRelationshipId.type, xmlOptions);
        }
        
        public static STRelationshipId parse(final File file) throws XmlException, IOException {
            return (STRelationshipId)getTypeLoader().parse(file, STRelationshipId.type, (XmlOptions)null);
        }
        
        public static STRelationshipId parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STRelationshipId)getTypeLoader().parse(file, STRelationshipId.type, xmlOptions);
        }
        
        public static STRelationshipId parse(final URL url) throws XmlException, IOException {
            return (STRelationshipId)getTypeLoader().parse(url, STRelationshipId.type, (XmlOptions)null);
        }
        
        public static STRelationshipId parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STRelationshipId)getTypeLoader().parse(url, STRelationshipId.type, xmlOptions);
        }
        
        public static STRelationshipId parse(final InputStream inputStream) throws XmlException, IOException {
            return (STRelationshipId)getTypeLoader().parse(inputStream, STRelationshipId.type, (XmlOptions)null);
        }
        
        public static STRelationshipId parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STRelationshipId)getTypeLoader().parse(inputStream, STRelationshipId.type, xmlOptions);
        }
        
        public static STRelationshipId parse(final Reader reader) throws XmlException, IOException {
            return (STRelationshipId)getTypeLoader().parse(reader, STRelationshipId.type, (XmlOptions)null);
        }
        
        public static STRelationshipId parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STRelationshipId)getTypeLoader().parse(reader, STRelationshipId.type, xmlOptions);
        }
        
        public static STRelationshipId parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (STRelationshipId)getTypeLoader().parse(xmlStreamReader, STRelationshipId.type, (XmlOptions)null);
        }
        
        public static STRelationshipId parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (STRelationshipId)getTypeLoader().parse(xmlStreamReader, STRelationshipId.type, xmlOptions);
        }
        
        public static STRelationshipId parse(final Node node) throws XmlException {
            return (STRelationshipId)getTypeLoader().parse(node, STRelationshipId.type, (XmlOptions)null);
        }
        
        public static STRelationshipId parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (STRelationshipId)getTypeLoader().parse(node, STRelationshipId.type, xmlOptions);
        }
        
        @Deprecated
        public static STRelationshipId parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (STRelationshipId)getTypeLoader().parse(xmlInputStream, STRelationshipId.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static STRelationshipId parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (STRelationshipId)getTypeLoader().parse(xmlInputStream, STRelationshipId.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STRelationshipId.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STRelationshipId.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
