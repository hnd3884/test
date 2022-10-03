package org.openxmlformats.schemas.drawingml.x2006.chart;

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

public interface CTRelId extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTRelId.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctrelida492type");
    
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
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTRelId.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTRelId newInstance() {
            return (CTRelId)getTypeLoader().newInstance(CTRelId.type, (XmlOptions)null);
        }
        
        public static CTRelId newInstance(final XmlOptions xmlOptions) {
            return (CTRelId)getTypeLoader().newInstance(CTRelId.type, xmlOptions);
        }
        
        public static CTRelId parse(final String s) throws XmlException {
            return (CTRelId)getTypeLoader().parse(s, CTRelId.type, (XmlOptions)null);
        }
        
        public static CTRelId parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTRelId)getTypeLoader().parse(s, CTRelId.type, xmlOptions);
        }
        
        public static CTRelId parse(final File file) throws XmlException, IOException {
            return (CTRelId)getTypeLoader().parse(file, CTRelId.type, (XmlOptions)null);
        }
        
        public static CTRelId parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTRelId)getTypeLoader().parse(file, CTRelId.type, xmlOptions);
        }
        
        public static CTRelId parse(final URL url) throws XmlException, IOException {
            return (CTRelId)getTypeLoader().parse(url, CTRelId.type, (XmlOptions)null);
        }
        
        public static CTRelId parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTRelId)getTypeLoader().parse(url, CTRelId.type, xmlOptions);
        }
        
        public static CTRelId parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTRelId)getTypeLoader().parse(inputStream, CTRelId.type, (XmlOptions)null);
        }
        
        public static CTRelId parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTRelId)getTypeLoader().parse(inputStream, CTRelId.type, xmlOptions);
        }
        
        public static CTRelId parse(final Reader reader) throws XmlException, IOException {
            return (CTRelId)getTypeLoader().parse(reader, CTRelId.type, (XmlOptions)null);
        }
        
        public static CTRelId parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTRelId)getTypeLoader().parse(reader, CTRelId.type, xmlOptions);
        }
        
        public static CTRelId parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTRelId)getTypeLoader().parse(xmlStreamReader, CTRelId.type, (XmlOptions)null);
        }
        
        public static CTRelId parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTRelId)getTypeLoader().parse(xmlStreamReader, CTRelId.type, xmlOptions);
        }
        
        public static CTRelId parse(final Node node) throws XmlException {
            return (CTRelId)getTypeLoader().parse(node, CTRelId.type, (XmlOptions)null);
        }
        
        public static CTRelId parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTRelId)getTypeLoader().parse(node, CTRelId.type, xmlOptions);
        }
        
        @Deprecated
        public static CTRelId parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTRelId)getTypeLoader().parse(xmlInputStream, CTRelId.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTRelId parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTRelId)getTypeLoader().parse(xmlInputStream, CTRelId.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTRelId.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTRelId.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
