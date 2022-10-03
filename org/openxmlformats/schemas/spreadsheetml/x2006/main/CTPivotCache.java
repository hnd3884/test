package org.openxmlformats.schemas.spreadsheetml.x2006.main;

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
import org.apache.xmlbeans.XmlUnsignedInt;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTPivotCache extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTPivotCache.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctpivotcache4de9type");
    
    long getCacheId();
    
    XmlUnsignedInt xgetCacheId();
    
    void setCacheId(final long p0);
    
    void xsetCacheId(final XmlUnsignedInt p0);
    
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
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTPivotCache.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTPivotCache newInstance() {
            return (CTPivotCache)getTypeLoader().newInstance(CTPivotCache.type, (XmlOptions)null);
        }
        
        public static CTPivotCache newInstance(final XmlOptions xmlOptions) {
            return (CTPivotCache)getTypeLoader().newInstance(CTPivotCache.type, xmlOptions);
        }
        
        public static CTPivotCache parse(final String s) throws XmlException {
            return (CTPivotCache)getTypeLoader().parse(s, CTPivotCache.type, (XmlOptions)null);
        }
        
        public static CTPivotCache parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTPivotCache)getTypeLoader().parse(s, CTPivotCache.type, xmlOptions);
        }
        
        public static CTPivotCache parse(final File file) throws XmlException, IOException {
            return (CTPivotCache)getTypeLoader().parse(file, CTPivotCache.type, (XmlOptions)null);
        }
        
        public static CTPivotCache parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTPivotCache)getTypeLoader().parse(file, CTPivotCache.type, xmlOptions);
        }
        
        public static CTPivotCache parse(final URL url) throws XmlException, IOException {
            return (CTPivotCache)getTypeLoader().parse(url, CTPivotCache.type, (XmlOptions)null);
        }
        
        public static CTPivotCache parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTPivotCache)getTypeLoader().parse(url, CTPivotCache.type, xmlOptions);
        }
        
        public static CTPivotCache parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTPivotCache)getTypeLoader().parse(inputStream, CTPivotCache.type, (XmlOptions)null);
        }
        
        public static CTPivotCache parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTPivotCache)getTypeLoader().parse(inputStream, CTPivotCache.type, xmlOptions);
        }
        
        public static CTPivotCache parse(final Reader reader) throws XmlException, IOException {
            return (CTPivotCache)getTypeLoader().parse(reader, CTPivotCache.type, (XmlOptions)null);
        }
        
        public static CTPivotCache parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTPivotCache)getTypeLoader().parse(reader, CTPivotCache.type, xmlOptions);
        }
        
        public static CTPivotCache parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTPivotCache)getTypeLoader().parse(xmlStreamReader, CTPivotCache.type, (XmlOptions)null);
        }
        
        public static CTPivotCache parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTPivotCache)getTypeLoader().parse(xmlStreamReader, CTPivotCache.type, xmlOptions);
        }
        
        public static CTPivotCache parse(final Node node) throws XmlException {
            return (CTPivotCache)getTypeLoader().parse(node, CTPivotCache.type, (XmlOptions)null);
        }
        
        public static CTPivotCache parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTPivotCache)getTypeLoader().parse(node, CTPivotCache.type, xmlOptions);
        }
        
        @Deprecated
        public static CTPivotCache parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTPivotCache)getTypeLoader().parse(xmlInputStream, CTPivotCache.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTPivotCache parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTPivotCache)getTypeLoader().parse(xmlInputStream, CTPivotCache.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTPivotCache.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTPivotCache.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
