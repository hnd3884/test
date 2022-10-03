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
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.SchemaType;

public interface PageSheetType extends SheetType
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(PageSheetType.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("pagesheettype679btype");
    
    String getUniqueID();
    
    XmlString xgetUniqueID();
    
    boolean isSetUniqueID();
    
    void setUniqueID(final String p0);
    
    void xsetUniqueID(final XmlString p0);
    
    void unsetUniqueID();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(PageSheetType.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static PageSheetType newInstance() {
            return (PageSheetType)getTypeLoader().newInstance(PageSheetType.type, (XmlOptions)null);
        }
        
        public static PageSheetType newInstance(final XmlOptions xmlOptions) {
            return (PageSheetType)getTypeLoader().newInstance(PageSheetType.type, xmlOptions);
        }
        
        public static PageSheetType parse(final String s) throws XmlException {
            return (PageSheetType)getTypeLoader().parse(s, PageSheetType.type, (XmlOptions)null);
        }
        
        public static PageSheetType parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (PageSheetType)getTypeLoader().parse(s, PageSheetType.type, xmlOptions);
        }
        
        public static PageSheetType parse(final File file) throws XmlException, IOException {
            return (PageSheetType)getTypeLoader().parse(file, PageSheetType.type, (XmlOptions)null);
        }
        
        public static PageSheetType parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (PageSheetType)getTypeLoader().parse(file, PageSheetType.type, xmlOptions);
        }
        
        public static PageSheetType parse(final URL url) throws XmlException, IOException {
            return (PageSheetType)getTypeLoader().parse(url, PageSheetType.type, (XmlOptions)null);
        }
        
        public static PageSheetType parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (PageSheetType)getTypeLoader().parse(url, PageSheetType.type, xmlOptions);
        }
        
        public static PageSheetType parse(final InputStream inputStream) throws XmlException, IOException {
            return (PageSheetType)getTypeLoader().parse(inputStream, PageSheetType.type, (XmlOptions)null);
        }
        
        public static PageSheetType parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (PageSheetType)getTypeLoader().parse(inputStream, PageSheetType.type, xmlOptions);
        }
        
        public static PageSheetType parse(final Reader reader) throws XmlException, IOException {
            return (PageSheetType)getTypeLoader().parse(reader, PageSheetType.type, (XmlOptions)null);
        }
        
        public static PageSheetType parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (PageSheetType)getTypeLoader().parse(reader, PageSheetType.type, xmlOptions);
        }
        
        public static PageSheetType parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (PageSheetType)getTypeLoader().parse(xmlStreamReader, PageSheetType.type, (XmlOptions)null);
        }
        
        public static PageSheetType parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (PageSheetType)getTypeLoader().parse(xmlStreamReader, PageSheetType.type, xmlOptions);
        }
        
        public static PageSheetType parse(final Node node) throws XmlException {
            return (PageSheetType)getTypeLoader().parse(node, PageSheetType.type, (XmlOptions)null);
        }
        
        public static PageSheetType parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (PageSheetType)getTypeLoader().parse(node, PageSheetType.type, xmlOptions);
        }
        
        @Deprecated
        public static PageSheetType parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (PageSheetType)getTypeLoader().parse(xmlInputStream, PageSheetType.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static PageSheetType parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (PageSheetType)getTypeLoader().parse(xmlInputStream, PageSheetType.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, PageSheetType.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, PageSheetType.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
