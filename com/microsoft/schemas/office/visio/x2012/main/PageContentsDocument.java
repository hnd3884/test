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
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface PageContentsDocument extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(PageContentsDocument.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("pagecontentsfc8bdoctype");
    
    PageContentsType getPageContents();
    
    void setPageContents(final PageContentsType p0);
    
    PageContentsType addNewPageContents();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(PageContentsDocument.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static PageContentsDocument newInstance() {
            return (PageContentsDocument)getTypeLoader().newInstance(PageContentsDocument.type, (XmlOptions)null);
        }
        
        public static PageContentsDocument newInstance(final XmlOptions xmlOptions) {
            return (PageContentsDocument)getTypeLoader().newInstance(PageContentsDocument.type, xmlOptions);
        }
        
        public static PageContentsDocument parse(final String s) throws XmlException {
            return (PageContentsDocument)getTypeLoader().parse(s, PageContentsDocument.type, (XmlOptions)null);
        }
        
        public static PageContentsDocument parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (PageContentsDocument)getTypeLoader().parse(s, PageContentsDocument.type, xmlOptions);
        }
        
        public static PageContentsDocument parse(final File file) throws XmlException, IOException {
            return (PageContentsDocument)getTypeLoader().parse(file, PageContentsDocument.type, (XmlOptions)null);
        }
        
        public static PageContentsDocument parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (PageContentsDocument)getTypeLoader().parse(file, PageContentsDocument.type, xmlOptions);
        }
        
        public static PageContentsDocument parse(final URL url) throws XmlException, IOException {
            return (PageContentsDocument)getTypeLoader().parse(url, PageContentsDocument.type, (XmlOptions)null);
        }
        
        public static PageContentsDocument parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (PageContentsDocument)getTypeLoader().parse(url, PageContentsDocument.type, xmlOptions);
        }
        
        public static PageContentsDocument parse(final InputStream inputStream) throws XmlException, IOException {
            return (PageContentsDocument)getTypeLoader().parse(inputStream, PageContentsDocument.type, (XmlOptions)null);
        }
        
        public static PageContentsDocument parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (PageContentsDocument)getTypeLoader().parse(inputStream, PageContentsDocument.type, xmlOptions);
        }
        
        public static PageContentsDocument parse(final Reader reader) throws XmlException, IOException {
            return (PageContentsDocument)getTypeLoader().parse(reader, PageContentsDocument.type, (XmlOptions)null);
        }
        
        public static PageContentsDocument parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (PageContentsDocument)getTypeLoader().parse(reader, PageContentsDocument.type, xmlOptions);
        }
        
        public static PageContentsDocument parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (PageContentsDocument)getTypeLoader().parse(xmlStreamReader, PageContentsDocument.type, (XmlOptions)null);
        }
        
        public static PageContentsDocument parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (PageContentsDocument)getTypeLoader().parse(xmlStreamReader, PageContentsDocument.type, xmlOptions);
        }
        
        public static PageContentsDocument parse(final Node node) throws XmlException {
            return (PageContentsDocument)getTypeLoader().parse(node, PageContentsDocument.type, (XmlOptions)null);
        }
        
        public static PageContentsDocument parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (PageContentsDocument)getTypeLoader().parse(node, PageContentsDocument.type, xmlOptions);
        }
        
        @Deprecated
        public static PageContentsDocument parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (PageContentsDocument)getTypeLoader().parse(xmlInputStream, PageContentsDocument.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static PageContentsDocument parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (PageContentsDocument)getTypeLoader().parse(xmlInputStream, PageContentsDocument.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, PageContentsDocument.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, PageContentsDocument.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
