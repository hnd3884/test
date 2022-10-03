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

public interface PageContentsType extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(PageContentsType.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("pagecontentstypea5d0type");
    
    ShapesType getShapes();
    
    boolean isSetShapes();
    
    void setShapes(final ShapesType p0);
    
    ShapesType addNewShapes();
    
    void unsetShapes();
    
    ConnectsType getConnects();
    
    boolean isSetConnects();
    
    void setConnects(final ConnectsType p0);
    
    ConnectsType addNewConnects();
    
    void unsetConnects();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(PageContentsType.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static PageContentsType newInstance() {
            return (PageContentsType)getTypeLoader().newInstance(PageContentsType.type, (XmlOptions)null);
        }
        
        public static PageContentsType newInstance(final XmlOptions xmlOptions) {
            return (PageContentsType)getTypeLoader().newInstance(PageContentsType.type, xmlOptions);
        }
        
        public static PageContentsType parse(final String s) throws XmlException {
            return (PageContentsType)getTypeLoader().parse(s, PageContentsType.type, (XmlOptions)null);
        }
        
        public static PageContentsType parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (PageContentsType)getTypeLoader().parse(s, PageContentsType.type, xmlOptions);
        }
        
        public static PageContentsType parse(final File file) throws XmlException, IOException {
            return (PageContentsType)getTypeLoader().parse(file, PageContentsType.type, (XmlOptions)null);
        }
        
        public static PageContentsType parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (PageContentsType)getTypeLoader().parse(file, PageContentsType.type, xmlOptions);
        }
        
        public static PageContentsType parse(final URL url) throws XmlException, IOException {
            return (PageContentsType)getTypeLoader().parse(url, PageContentsType.type, (XmlOptions)null);
        }
        
        public static PageContentsType parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (PageContentsType)getTypeLoader().parse(url, PageContentsType.type, xmlOptions);
        }
        
        public static PageContentsType parse(final InputStream inputStream) throws XmlException, IOException {
            return (PageContentsType)getTypeLoader().parse(inputStream, PageContentsType.type, (XmlOptions)null);
        }
        
        public static PageContentsType parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (PageContentsType)getTypeLoader().parse(inputStream, PageContentsType.type, xmlOptions);
        }
        
        public static PageContentsType parse(final Reader reader) throws XmlException, IOException {
            return (PageContentsType)getTypeLoader().parse(reader, PageContentsType.type, (XmlOptions)null);
        }
        
        public static PageContentsType parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (PageContentsType)getTypeLoader().parse(reader, PageContentsType.type, xmlOptions);
        }
        
        public static PageContentsType parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (PageContentsType)getTypeLoader().parse(xmlStreamReader, PageContentsType.type, (XmlOptions)null);
        }
        
        public static PageContentsType parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (PageContentsType)getTypeLoader().parse(xmlStreamReader, PageContentsType.type, xmlOptions);
        }
        
        public static PageContentsType parse(final Node node) throws XmlException {
            return (PageContentsType)getTypeLoader().parse(node, PageContentsType.type, (XmlOptions)null);
        }
        
        public static PageContentsType parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (PageContentsType)getTypeLoader().parse(node, PageContentsType.type, xmlOptions);
        }
        
        @Deprecated
        public static PageContentsType parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (PageContentsType)getTypeLoader().parse(xmlInputStream, PageContentsType.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static PageContentsType parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (PageContentsType)getTypeLoader().parse(xmlInputStream, PageContentsType.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, PageContentsType.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, PageContentsType.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
