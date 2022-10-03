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

public interface PagesDocument extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(PagesDocument.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("pages52f4doctype");
    
    PagesType getPages();
    
    void setPages(final PagesType p0);
    
    PagesType addNewPages();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(PagesDocument.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static PagesDocument newInstance() {
            return (PagesDocument)getTypeLoader().newInstance(PagesDocument.type, (XmlOptions)null);
        }
        
        public static PagesDocument newInstance(final XmlOptions xmlOptions) {
            return (PagesDocument)getTypeLoader().newInstance(PagesDocument.type, xmlOptions);
        }
        
        public static PagesDocument parse(final String s) throws XmlException {
            return (PagesDocument)getTypeLoader().parse(s, PagesDocument.type, (XmlOptions)null);
        }
        
        public static PagesDocument parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (PagesDocument)getTypeLoader().parse(s, PagesDocument.type, xmlOptions);
        }
        
        public static PagesDocument parse(final File file) throws XmlException, IOException {
            return (PagesDocument)getTypeLoader().parse(file, PagesDocument.type, (XmlOptions)null);
        }
        
        public static PagesDocument parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (PagesDocument)getTypeLoader().parse(file, PagesDocument.type, xmlOptions);
        }
        
        public static PagesDocument parse(final URL url) throws XmlException, IOException {
            return (PagesDocument)getTypeLoader().parse(url, PagesDocument.type, (XmlOptions)null);
        }
        
        public static PagesDocument parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (PagesDocument)getTypeLoader().parse(url, PagesDocument.type, xmlOptions);
        }
        
        public static PagesDocument parse(final InputStream inputStream) throws XmlException, IOException {
            return (PagesDocument)getTypeLoader().parse(inputStream, PagesDocument.type, (XmlOptions)null);
        }
        
        public static PagesDocument parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (PagesDocument)getTypeLoader().parse(inputStream, PagesDocument.type, xmlOptions);
        }
        
        public static PagesDocument parse(final Reader reader) throws XmlException, IOException {
            return (PagesDocument)getTypeLoader().parse(reader, PagesDocument.type, (XmlOptions)null);
        }
        
        public static PagesDocument parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (PagesDocument)getTypeLoader().parse(reader, PagesDocument.type, xmlOptions);
        }
        
        public static PagesDocument parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (PagesDocument)getTypeLoader().parse(xmlStreamReader, PagesDocument.type, (XmlOptions)null);
        }
        
        public static PagesDocument parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (PagesDocument)getTypeLoader().parse(xmlStreamReader, PagesDocument.type, xmlOptions);
        }
        
        public static PagesDocument parse(final Node node) throws XmlException {
            return (PagesDocument)getTypeLoader().parse(node, PagesDocument.type, (XmlOptions)null);
        }
        
        public static PagesDocument parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (PagesDocument)getTypeLoader().parse(node, PagesDocument.type, xmlOptions);
        }
        
        @Deprecated
        public static PagesDocument parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (PagesDocument)getTypeLoader().parse(xmlInputStream, PagesDocument.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static PagesDocument parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (PagesDocument)getTypeLoader().parse(xmlInputStream, PagesDocument.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, PagesDocument.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, PagesDocument.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
