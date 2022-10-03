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
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTColors extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTColors.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctcolors6579type");
    
    CTIndexedColors getIndexedColors();
    
    boolean isSetIndexedColors();
    
    void setIndexedColors(final CTIndexedColors p0);
    
    CTIndexedColors addNewIndexedColors();
    
    void unsetIndexedColors();
    
    CTMRUColors getMruColors();
    
    boolean isSetMruColors();
    
    void setMruColors(final CTMRUColors p0);
    
    CTMRUColors addNewMruColors();
    
    void unsetMruColors();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTColors.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTColors newInstance() {
            return (CTColors)getTypeLoader().newInstance(CTColors.type, (XmlOptions)null);
        }
        
        public static CTColors newInstance(final XmlOptions xmlOptions) {
            return (CTColors)getTypeLoader().newInstance(CTColors.type, xmlOptions);
        }
        
        public static CTColors parse(final String s) throws XmlException {
            return (CTColors)getTypeLoader().parse(s, CTColors.type, (XmlOptions)null);
        }
        
        public static CTColors parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTColors)getTypeLoader().parse(s, CTColors.type, xmlOptions);
        }
        
        public static CTColors parse(final File file) throws XmlException, IOException {
            return (CTColors)getTypeLoader().parse(file, CTColors.type, (XmlOptions)null);
        }
        
        public static CTColors parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTColors)getTypeLoader().parse(file, CTColors.type, xmlOptions);
        }
        
        public static CTColors parse(final URL url) throws XmlException, IOException {
            return (CTColors)getTypeLoader().parse(url, CTColors.type, (XmlOptions)null);
        }
        
        public static CTColors parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTColors)getTypeLoader().parse(url, CTColors.type, xmlOptions);
        }
        
        public static CTColors parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTColors)getTypeLoader().parse(inputStream, CTColors.type, (XmlOptions)null);
        }
        
        public static CTColors parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTColors)getTypeLoader().parse(inputStream, CTColors.type, xmlOptions);
        }
        
        public static CTColors parse(final Reader reader) throws XmlException, IOException {
            return (CTColors)getTypeLoader().parse(reader, CTColors.type, (XmlOptions)null);
        }
        
        public static CTColors parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTColors)getTypeLoader().parse(reader, CTColors.type, xmlOptions);
        }
        
        public static CTColors parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTColors)getTypeLoader().parse(xmlStreamReader, CTColors.type, (XmlOptions)null);
        }
        
        public static CTColors parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTColors)getTypeLoader().parse(xmlStreamReader, CTColors.type, xmlOptions);
        }
        
        public static CTColors parse(final Node node) throws XmlException {
            return (CTColors)getTypeLoader().parse(node, CTColors.type, (XmlOptions)null);
        }
        
        public static CTColors parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTColors)getTypeLoader().parse(node, CTColors.type, xmlOptions);
        }
        
        @Deprecated
        public static CTColors parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTColors)getTypeLoader().parse(xmlInputStream, CTColors.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTColors parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTColors)getTypeLoader().parse(xmlInputStream, CTColors.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTColors.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTColors.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
