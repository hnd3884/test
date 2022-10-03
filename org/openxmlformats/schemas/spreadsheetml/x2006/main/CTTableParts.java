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

public interface CTTableParts extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTTableParts.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("cttablepartsf6bbtype");
    
    List<CTTablePart> getTablePartList();
    
    @Deprecated
    CTTablePart[] getTablePartArray();
    
    CTTablePart getTablePartArray(final int p0);
    
    int sizeOfTablePartArray();
    
    void setTablePartArray(final CTTablePart[] p0);
    
    void setTablePartArray(final int p0, final CTTablePart p1);
    
    CTTablePart insertNewTablePart(final int p0);
    
    CTTablePart addNewTablePart();
    
    void removeTablePart(final int p0);
    
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
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTTableParts.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTTableParts newInstance() {
            return (CTTableParts)getTypeLoader().newInstance(CTTableParts.type, (XmlOptions)null);
        }
        
        public static CTTableParts newInstance(final XmlOptions xmlOptions) {
            return (CTTableParts)getTypeLoader().newInstance(CTTableParts.type, xmlOptions);
        }
        
        public static CTTableParts parse(final String s) throws XmlException {
            return (CTTableParts)getTypeLoader().parse(s, CTTableParts.type, (XmlOptions)null);
        }
        
        public static CTTableParts parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTTableParts)getTypeLoader().parse(s, CTTableParts.type, xmlOptions);
        }
        
        public static CTTableParts parse(final File file) throws XmlException, IOException {
            return (CTTableParts)getTypeLoader().parse(file, CTTableParts.type, (XmlOptions)null);
        }
        
        public static CTTableParts parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTableParts)getTypeLoader().parse(file, CTTableParts.type, xmlOptions);
        }
        
        public static CTTableParts parse(final URL url) throws XmlException, IOException {
            return (CTTableParts)getTypeLoader().parse(url, CTTableParts.type, (XmlOptions)null);
        }
        
        public static CTTableParts parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTableParts)getTypeLoader().parse(url, CTTableParts.type, xmlOptions);
        }
        
        public static CTTableParts parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTTableParts)getTypeLoader().parse(inputStream, CTTableParts.type, (XmlOptions)null);
        }
        
        public static CTTableParts parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTableParts)getTypeLoader().parse(inputStream, CTTableParts.type, xmlOptions);
        }
        
        public static CTTableParts parse(final Reader reader) throws XmlException, IOException {
            return (CTTableParts)getTypeLoader().parse(reader, CTTableParts.type, (XmlOptions)null);
        }
        
        public static CTTableParts parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTableParts)getTypeLoader().parse(reader, CTTableParts.type, xmlOptions);
        }
        
        public static CTTableParts parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTTableParts)getTypeLoader().parse(xmlStreamReader, CTTableParts.type, (XmlOptions)null);
        }
        
        public static CTTableParts parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTTableParts)getTypeLoader().parse(xmlStreamReader, CTTableParts.type, xmlOptions);
        }
        
        public static CTTableParts parse(final Node node) throws XmlException {
            return (CTTableParts)getTypeLoader().parse(node, CTTableParts.type, (XmlOptions)null);
        }
        
        public static CTTableParts parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTTableParts)getTypeLoader().parse(node, CTTableParts.type, xmlOptions);
        }
        
        @Deprecated
        public static CTTableParts parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTTableParts)getTypeLoader().parse(xmlInputStream, CTTableParts.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTTableParts parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTTableParts)getTypeLoader().parse(xmlInputStream, CTTableParts.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTTableParts.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTTableParts.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
