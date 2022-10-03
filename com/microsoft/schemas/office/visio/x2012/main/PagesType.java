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
import java.util.List;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface PagesType extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(PagesType.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("pagestypef2e7type");
    
    List<PageType> getPageList();
    
    @Deprecated
    PageType[] getPageArray();
    
    PageType getPageArray(final int p0);
    
    int sizeOfPageArray();
    
    void setPageArray(final PageType[] p0);
    
    void setPageArray(final int p0, final PageType p1);
    
    PageType insertNewPage(final int p0);
    
    PageType addNewPage();
    
    void removePage(final int p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(PagesType.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static PagesType newInstance() {
            return (PagesType)getTypeLoader().newInstance(PagesType.type, (XmlOptions)null);
        }
        
        public static PagesType newInstance(final XmlOptions xmlOptions) {
            return (PagesType)getTypeLoader().newInstance(PagesType.type, xmlOptions);
        }
        
        public static PagesType parse(final String s) throws XmlException {
            return (PagesType)getTypeLoader().parse(s, PagesType.type, (XmlOptions)null);
        }
        
        public static PagesType parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (PagesType)getTypeLoader().parse(s, PagesType.type, xmlOptions);
        }
        
        public static PagesType parse(final File file) throws XmlException, IOException {
            return (PagesType)getTypeLoader().parse(file, PagesType.type, (XmlOptions)null);
        }
        
        public static PagesType parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (PagesType)getTypeLoader().parse(file, PagesType.type, xmlOptions);
        }
        
        public static PagesType parse(final URL url) throws XmlException, IOException {
            return (PagesType)getTypeLoader().parse(url, PagesType.type, (XmlOptions)null);
        }
        
        public static PagesType parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (PagesType)getTypeLoader().parse(url, PagesType.type, xmlOptions);
        }
        
        public static PagesType parse(final InputStream inputStream) throws XmlException, IOException {
            return (PagesType)getTypeLoader().parse(inputStream, PagesType.type, (XmlOptions)null);
        }
        
        public static PagesType parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (PagesType)getTypeLoader().parse(inputStream, PagesType.type, xmlOptions);
        }
        
        public static PagesType parse(final Reader reader) throws XmlException, IOException {
            return (PagesType)getTypeLoader().parse(reader, PagesType.type, (XmlOptions)null);
        }
        
        public static PagesType parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (PagesType)getTypeLoader().parse(reader, PagesType.type, xmlOptions);
        }
        
        public static PagesType parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (PagesType)getTypeLoader().parse(xmlStreamReader, PagesType.type, (XmlOptions)null);
        }
        
        public static PagesType parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (PagesType)getTypeLoader().parse(xmlStreamReader, PagesType.type, xmlOptions);
        }
        
        public static PagesType parse(final Node node) throws XmlException {
            return (PagesType)getTypeLoader().parse(node, PagesType.type, (XmlOptions)null);
        }
        
        public static PagesType parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (PagesType)getTypeLoader().parse(node, PagesType.type, xmlOptions);
        }
        
        @Deprecated
        public static PagesType parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (PagesType)getTypeLoader().parse(xmlInputStream, PagesType.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static PagesType parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (PagesType)getTypeLoader().parse(xmlInputStream, PagesType.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, PagesType.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, PagesType.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
