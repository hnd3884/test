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

public interface CTCacheFields extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTCacheFields.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctcachefieldsf5fatype");
    
    List<CTCacheField> getCacheFieldList();
    
    @Deprecated
    CTCacheField[] getCacheFieldArray();
    
    CTCacheField getCacheFieldArray(final int p0);
    
    int sizeOfCacheFieldArray();
    
    void setCacheFieldArray(final CTCacheField[] p0);
    
    void setCacheFieldArray(final int p0, final CTCacheField p1);
    
    CTCacheField insertNewCacheField(final int p0);
    
    CTCacheField addNewCacheField();
    
    void removeCacheField(final int p0);
    
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
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTCacheFields.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTCacheFields newInstance() {
            return (CTCacheFields)getTypeLoader().newInstance(CTCacheFields.type, (XmlOptions)null);
        }
        
        public static CTCacheFields newInstance(final XmlOptions xmlOptions) {
            return (CTCacheFields)getTypeLoader().newInstance(CTCacheFields.type, xmlOptions);
        }
        
        public static CTCacheFields parse(final String s) throws XmlException {
            return (CTCacheFields)getTypeLoader().parse(s, CTCacheFields.type, (XmlOptions)null);
        }
        
        public static CTCacheFields parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTCacheFields)getTypeLoader().parse(s, CTCacheFields.type, xmlOptions);
        }
        
        public static CTCacheFields parse(final File file) throws XmlException, IOException {
            return (CTCacheFields)getTypeLoader().parse(file, CTCacheFields.type, (XmlOptions)null);
        }
        
        public static CTCacheFields parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTCacheFields)getTypeLoader().parse(file, CTCacheFields.type, xmlOptions);
        }
        
        public static CTCacheFields parse(final URL url) throws XmlException, IOException {
            return (CTCacheFields)getTypeLoader().parse(url, CTCacheFields.type, (XmlOptions)null);
        }
        
        public static CTCacheFields parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTCacheFields)getTypeLoader().parse(url, CTCacheFields.type, xmlOptions);
        }
        
        public static CTCacheFields parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTCacheFields)getTypeLoader().parse(inputStream, CTCacheFields.type, (XmlOptions)null);
        }
        
        public static CTCacheFields parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTCacheFields)getTypeLoader().parse(inputStream, CTCacheFields.type, xmlOptions);
        }
        
        public static CTCacheFields parse(final Reader reader) throws XmlException, IOException {
            return (CTCacheFields)getTypeLoader().parse(reader, CTCacheFields.type, (XmlOptions)null);
        }
        
        public static CTCacheFields parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTCacheFields)getTypeLoader().parse(reader, CTCacheFields.type, xmlOptions);
        }
        
        public static CTCacheFields parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTCacheFields)getTypeLoader().parse(xmlStreamReader, CTCacheFields.type, (XmlOptions)null);
        }
        
        public static CTCacheFields parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTCacheFields)getTypeLoader().parse(xmlStreamReader, CTCacheFields.type, xmlOptions);
        }
        
        public static CTCacheFields parse(final Node node) throws XmlException {
            return (CTCacheFields)getTypeLoader().parse(node, CTCacheFields.type, (XmlOptions)null);
        }
        
        public static CTCacheFields parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTCacheFields)getTypeLoader().parse(node, CTCacheFields.type, xmlOptions);
        }
        
        @Deprecated
        public static CTCacheFields parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTCacheFields)getTypeLoader().parse(xmlInputStream, CTCacheFields.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTCacheFields parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTCacheFields)getTypeLoader().parse(xmlInputStream, CTCacheFields.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTCacheFields.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTCacheFields.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
