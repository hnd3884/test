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

public interface CTItems extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTItems.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctitemsecdftype");
    
    List<CTItem> getItemList();
    
    @Deprecated
    CTItem[] getItemArray();
    
    CTItem getItemArray(final int p0);
    
    int sizeOfItemArray();
    
    void setItemArray(final CTItem[] p0);
    
    void setItemArray(final int p0, final CTItem p1);
    
    CTItem insertNewItem(final int p0);
    
    CTItem addNewItem();
    
    void removeItem(final int p0);
    
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
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTItems.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTItems newInstance() {
            return (CTItems)getTypeLoader().newInstance(CTItems.type, (XmlOptions)null);
        }
        
        public static CTItems newInstance(final XmlOptions xmlOptions) {
            return (CTItems)getTypeLoader().newInstance(CTItems.type, xmlOptions);
        }
        
        public static CTItems parse(final String s) throws XmlException {
            return (CTItems)getTypeLoader().parse(s, CTItems.type, (XmlOptions)null);
        }
        
        public static CTItems parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTItems)getTypeLoader().parse(s, CTItems.type, xmlOptions);
        }
        
        public static CTItems parse(final File file) throws XmlException, IOException {
            return (CTItems)getTypeLoader().parse(file, CTItems.type, (XmlOptions)null);
        }
        
        public static CTItems parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTItems)getTypeLoader().parse(file, CTItems.type, xmlOptions);
        }
        
        public static CTItems parse(final URL url) throws XmlException, IOException {
            return (CTItems)getTypeLoader().parse(url, CTItems.type, (XmlOptions)null);
        }
        
        public static CTItems parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTItems)getTypeLoader().parse(url, CTItems.type, xmlOptions);
        }
        
        public static CTItems parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTItems)getTypeLoader().parse(inputStream, CTItems.type, (XmlOptions)null);
        }
        
        public static CTItems parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTItems)getTypeLoader().parse(inputStream, CTItems.type, xmlOptions);
        }
        
        public static CTItems parse(final Reader reader) throws XmlException, IOException {
            return (CTItems)getTypeLoader().parse(reader, CTItems.type, (XmlOptions)null);
        }
        
        public static CTItems parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTItems)getTypeLoader().parse(reader, CTItems.type, xmlOptions);
        }
        
        public static CTItems parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTItems)getTypeLoader().parse(xmlStreamReader, CTItems.type, (XmlOptions)null);
        }
        
        public static CTItems parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTItems)getTypeLoader().parse(xmlStreamReader, CTItems.type, xmlOptions);
        }
        
        public static CTItems parse(final Node node) throws XmlException {
            return (CTItems)getTypeLoader().parse(node, CTItems.type, (XmlOptions)null);
        }
        
        public static CTItems parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTItems)getTypeLoader().parse(node, CTItems.type, xmlOptions);
        }
        
        @Deprecated
        public static CTItems parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTItems)getTypeLoader().parse(xmlInputStream, CTItems.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTItems parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTItems)getTypeLoader().parse(xmlInputStream, CTItems.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTItems.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTItems.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
