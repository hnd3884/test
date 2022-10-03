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
import org.apache.xmlbeans.XmlUnsignedInt;
import java.util.List;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTPivotCacheRecords extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTPivotCacheRecords.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctpivotcacherecords5be1type");
    
    List<CTRecord> getRList();
    
    @Deprecated
    CTRecord[] getRArray();
    
    CTRecord getRArray(final int p0);
    
    int sizeOfRArray();
    
    void setRArray(final CTRecord[] p0);
    
    void setRArray(final int p0, final CTRecord p1);
    
    CTRecord insertNewR(final int p0);
    
    CTRecord addNewR();
    
    void removeR(final int p0);
    
    CTExtensionList getExtLst();
    
    boolean isSetExtLst();
    
    void setExtLst(final CTExtensionList p0);
    
    CTExtensionList addNewExtLst();
    
    void unsetExtLst();
    
    long getCount();
    
    XmlUnsignedInt xgetCount();
    
    boolean isSetCount();
    
    void setCount(final long p0);
    
    void xsetCount(final XmlUnsignedInt p0);
    
    void unsetCount();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTPivotCacheRecords.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTPivotCacheRecords newInstance() {
            return (CTPivotCacheRecords)getTypeLoader().newInstance(CTPivotCacheRecords.type, (XmlOptions)null);
        }
        
        public static CTPivotCacheRecords newInstance(final XmlOptions xmlOptions) {
            return (CTPivotCacheRecords)getTypeLoader().newInstance(CTPivotCacheRecords.type, xmlOptions);
        }
        
        public static CTPivotCacheRecords parse(final String s) throws XmlException {
            return (CTPivotCacheRecords)getTypeLoader().parse(s, CTPivotCacheRecords.type, (XmlOptions)null);
        }
        
        public static CTPivotCacheRecords parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTPivotCacheRecords)getTypeLoader().parse(s, CTPivotCacheRecords.type, xmlOptions);
        }
        
        public static CTPivotCacheRecords parse(final File file) throws XmlException, IOException {
            return (CTPivotCacheRecords)getTypeLoader().parse(file, CTPivotCacheRecords.type, (XmlOptions)null);
        }
        
        public static CTPivotCacheRecords parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTPivotCacheRecords)getTypeLoader().parse(file, CTPivotCacheRecords.type, xmlOptions);
        }
        
        public static CTPivotCacheRecords parse(final URL url) throws XmlException, IOException {
            return (CTPivotCacheRecords)getTypeLoader().parse(url, CTPivotCacheRecords.type, (XmlOptions)null);
        }
        
        public static CTPivotCacheRecords parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTPivotCacheRecords)getTypeLoader().parse(url, CTPivotCacheRecords.type, xmlOptions);
        }
        
        public static CTPivotCacheRecords parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTPivotCacheRecords)getTypeLoader().parse(inputStream, CTPivotCacheRecords.type, (XmlOptions)null);
        }
        
        public static CTPivotCacheRecords parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTPivotCacheRecords)getTypeLoader().parse(inputStream, CTPivotCacheRecords.type, xmlOptions);
        }
        
        public static CTPivotCacheRecords parse(final Reader reader) throws XmlException, IOException {
            return (CTPivotCacheRecords)getTypeLoader().parse(reader, CTPivotCacheRecords.type, (XmlOptions)null);
        }
        
        public static CTPivotCacheRecords parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTPivotCacheRecords)getTypeLoader().parse(reader, CTPivotCacheRecords.type, xmlOptions);
        }
        
        public static CTPivotCacheRecords parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTPivotCacheRecords)getTypeLoader().parse(xmlStreamReader, CTPivotCacheRecords.type, (XmlOptions)null);
        }
        
        public static CTPivotCacheRecords parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTPivotCacheRecords)getTypeLoader().parse(xmlStreamReader, CTPivotCacheRecords.type, xmlOptions);
        }
        
        public static CTPivotCacheRecords parse(final Node node) throws XmlException {
            return (CTPivotCacheRecords)getTypeLoader().parse(node, CTPivotCacheRecords.type, (XmlOptions)null);
        }
        
        public static CTPivotCacheRecords parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTPivotCacheRecords)getTypeLoader().parse(node, CTPivotCacheRecords.type, xmlOptions);
        }
        
        @Deprecated
        public static CTPivotCacheRecords parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTPivotCacheRecords)getTypeLoader().parse(xmlInputStream, CTPivotCacheRecords.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTPivotCacheRecords parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTPivotCacheRecords)getTypeLoader().parse(xmlInputStream, CTPivotCacheRecords.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTPivotCacheRecords.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTPivotCacheRecords.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
