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

public interface CTBorders extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTBorders.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctborders0d66type");
    
    List<CTBorder> getBorderList();
    
    @Deprecated
    CTBorder[] getBorderArray();
    
    CTBorder getBorderArray(final int p0);
    
    int sizeOfBorderArray();
    
    void setBorderArray(final CTBorder[] p0);
    
    void setBorderArray(final int p0, final CTBorder p1);
    
    CTBorder insertNewBorder(final int p0);
    
    CTBorder addNewBorder();
    
    void removeBorder(final int p0);
    
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
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTBorders.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTBorders newInstance() {
            return (CTBorders)getTypeLoader().newInstance(CTBorders.type, (XmlOptions)null);
        }
        
        public static CTBorders newInstance(final XmlOptions xmlOptions) {
            return (CTBorders)getTypeLoader().newInstance(CTBorders.type, xmlOptions);
        }
        
        public static CTBorders parse(final String s) throws XmlException {
            return (CTBorders)getTypeLoader().parse(s, CTBorders.type, (XmlOptions)null);
        }
        
        public static CTBorders parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTBorders)getTypeLoader().parse(s, CTBorders.type, xmlOptions);
        }
        
        public static CTBorders parse(final File file) throws XmlException, IOException {
            return (CTBorders)getTypeLoader().parse(file, CTBorders.type, (XmlOptions)null);
        }
        
        public static CTBorders parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTBorders)getTypeLoader().parse(file, CTBorders.type, xmlOptions);
        }
        
        public static CTBorders parse(final URL url) throws XmlException, IOException {
            return (CTBorders)getTypeLoader().parse(url, CTBorders.type, (XmlOptions)null);
        }
        
        public static CTBorders parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTBorders)getTypeLoader().parse(url, CTBorders.type, xmlOptions);
        }
        
        public static CTBorders parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTBorders)getTypeLoader().parse(inputStream, CTBorders.type, (XmlOptions)null);
        }
        
        public static CTBorders parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTBorders)getTypeLoader().parse(inputStream, CTBorders.type, xmlOptions);
        }
        
        public static CTBorders parse(final Reader reader) throws XmlException, IOException {
            return (CTBorders)getTypeLoader().parse(reader, CTBorders.type, (XmlOptions)null);
        }
        
        public static CTBorders parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTBorders)getTypeLoader().parse(reader, CTBorders.type, xmlOptions);
        }
        
        public static CTBorders parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTBorders)getTypeLoader().parse(xmlStreamReader, CTBorders.type, (XmlOptions)null);
        }
        
        public static CTBorders parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTBorders)getTypeLoader().parse(xmlStreamReader, CTBorders.type, xmlOptions);
        }
        
        public static CTBorders parse(final Node node) throws XmlException {
            return (CTBorders)getTypeLoader().parse(node, CTBorders.type, (XmlOptions)null);
        }
        
        public static CTBorders parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTBorders)getTypeLoader().parse(node, CTBorders.type, xmlOptions);
        }
        
        @Deprecated
        public static CTBorders parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTBorders)getTypeLoader().parse(xmlInputStream, CTBorders.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTBorders parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTBorders)getTypeLoader().parse(xmlInputStream, CTBorders.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTBorders.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTBorders.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
