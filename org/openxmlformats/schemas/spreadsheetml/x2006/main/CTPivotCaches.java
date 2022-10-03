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
import java.util.List;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTPivotCaches extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTPivotCaches.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctpivotcaches4f32type");
    
    List<CTPivotCache> getPivotCacheList();
    
    @Deprecated
    CTPivotCache[] getPivotCacheArray();
    
    CTPivotCache getPivotCacheArray(final int p0);
    
    int sizeOfPivotCacheArray();
    
    void setPivotCacheArray(final CTPivotCache[] p0);
    
    void setPivotCacheArray(final int p0, final CTPivotCache p1);
    
    CTPivotCache insertNewPivotCache(final int p0);
    
    CTPivotCache addNewPivotCache();
    
    void removePivotCache(final int p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTPivotCaches.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTPivotCaches newInstance() {
            return (CTPivotCaches)getTypeLoader().newInstance(CTPivotCaches.type, (XmlOptions)null);
        }
        
        public static CTPivotCaches newInstance(final XmlOptions xmlOptions) {
            return (CTPivotCaches)getTypeLoader().newInstance(CTPivotCaches.type, xmlOptions);
        }
        
        public static CTPivotCaches parse(final String s) throws XmlException {
            return (CTPivotCaches)getTypeLoader().parse(s, CTPivotCaches.type, (XmlOptions)null);
        }
        
        public static CTPivotCaches parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTPivotCaches)getTypeLoader().parse(s, CTPivotCaches.type, xmlOptions);
        }
        
        public static CTPivotCaches parse(final File file) throws XmlException, IOException {
            return (CTPivotCaches)getTypeLoader().parse(file, CTPivotCaches.type, (XmlOptions)null);
        }
        
        public static CTPivotCaches parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTPivotCaches)getTypeLoader().parse(file, CTPivotCaches.type, xmlOptions);
        }
        
        public static CTPivotCaches parse(final URL url) throws XmlException, IOException {
            return (CTPivotCaches)getTypeLoader().parse(url, CTPivotCaches.type, (XmlOptions)null);
        }
        
        public static CTPivotCaches parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTPivotCaches)getTypeLoader().parse(url, CTPivotCaches.type, xmlOptions);
        }
        
        public static CTPivotCaches parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTPivotCaches)getTypeLoader().parse(inputStream, CTPivotCaches.type, (XmlOptions)null);
        }
        
        public static CTPivotCaches parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTPivotCaches)getTypeLoader().parse(inputStream, CTPivotCaches.type, xmlOptions);
        }
        
        public static CTPivotCaches parse(final Reader reader) throws XmlException, IOException {
            return (CTPivotCaches)getTypeLoader().parse(reader, CTPivotCaches.type, (XmlOptions)null);
        }
        
        public static CTPivotCaches parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTPivotCaches)getTypeLoader().parse(reader, CTPivotCaches.type, xmlOptions);
        }
        
        public static CTPivotCaches parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTPivotCaches)getTypeLoader().parse(xmlStreamReader, CTPivotCaches.type, (XmlOptions)null);
        }
        
        public static CTPivotCaches parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTPivotCaches)getTypeLoader().parse(xmlStreamReader, CTPivotCaches.type, xmlOptions);
        }
        
        public static CTPivotCaches parse(final Node node) throws XmlException {
            return (CTPivotCaches)getTypeLoader().parse(node, CTPivotCaches.type, (XmlOptions)null);
        }
        
        public static CTPivotCaches parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTPivotCaches)getTypeLoader().parse(node, CTPivotCaches.type, xmlOptions);
        }
        
        @Deprecated
        public static CTPivotCaches parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTPivotCaches)getTypeLoader().parse(xmlInputStream, CTPivotCaches.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTPivotCaches parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTPivotCaches)getTypeLoader().parse(xmlInputStream, CTPivotCaches.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTPivotCaches.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTPivotCaches.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
