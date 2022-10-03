package com.microsoft.schemas.office.visio.x2012.main;

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
import org.openxmlformats.schemas.officeDocument.x2006.relationships.STRelationshipId;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface RelType extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(RelType.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("reltype05f2type");
    
    String getId();
    
    STRelationshipId xgetId();
    
    void setId(final String p0);
    
    void xsetId(final STRelationshipId p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(RelType.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static RelType newInstance() {
            return (RelType)getTypeLoader().newInstance(RelType.type, (XmlOptions)null);
        }
        
        public static RelType newInstance(final XmlOptions xmlOptions) {
            return (RelType)getTypeLoader().newInstance(RelType.type, xmlOptions);
        }
        
        public static RelType parse(final String s) throws XmlException {
            return (RelType)getTypeLoader().parse(s, RelType.type, (XmlOptions)null);
        }
        
        public static RelType parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (RelType)getTypeLoader().parse(s, RelType.type, xmlOptions);
        }
        
        public static RelType parse(final File file) throws XmlException, IOException {
            return (RelType)getTypeLoader().parse(file, RelType.type, (XmlOptions)null);
        }
        
        public static RelType parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (RelType)getTypeLoader().parse(file, RelType.type, xmlOptions);
        }
        
        public static RelType parse(final URL url) throws XmlException, IOException {
            return (RelType)getTypeLoader().parse(url, RelType.type, (XmlOptions)null);
        }
        
        public static RelType parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (RelType)getTypeLoader().parse(url, RelType.type, xmlOptions);
        }
        
        public static RelType parse(final InputStream inputStream) throws XmlException, IOException {
            return (RelType)getTypeLoader().parse(inputStream, RelType.type, (XmlOptions)null);
        }
        
        public static RelType parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (RelType)getTypeLoader().parse(inputStream, RelType.type, xmlOptions);
        }
        
        public static RelType parse(final Reader reader) throws XmlException, IOException {
            return (RelType)getTypeLoader().parse(reader, RelType.type, (XmlOptions)null);
        }
        
        public static RelType parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (RelType)getTypeLoader().parse(reader, RelType.type, xmlOptions);
        }
        
        public static RelType parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (RelType)getTypeLoader().parse(xmlStreamReader, RelType.type, (XmlOptions)null);
        }
        
        public static RelType parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (RelType)getTypeLoader().parse(xmlStreamReader, RelType.type, xmlOptions);
        }
        
        public static RelType parse(final Node node) throws XmlException {
            return (RelType)getTypeLoader().parse(node, RelType.type, (XmlOptions)null);
        }
        
        public static RelType parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (RelType)getTypeLoader().parse(node, RelType.type, xmlOptions);
        }
        
        @Deprecated
        public static RelType parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (RelType)getTypeLoader().parse(xmlInputStream, RelType.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static RelType parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (RelType)getTypeLoader().parse(xmlInputStream, RelType.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, RelType.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, RelType.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
