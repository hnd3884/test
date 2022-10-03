package org.openxmlformats.schemas.drawingml.x2006.main;

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

public interface CTStretchInfoProperties extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTStretchInfoProperties.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctstretchinfopropertiesde57type");
    
    CTRelativeRect getFillRect();
    
    boolean isSetFillRect();
    
    void setFillRect(final CTRelativeRect p0);
    
    CTRelativeRect addNewFillRect();
    
    void unsetFillRect();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTStretchInfoProperties.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTStretchInfoProperties newInstance() {
            return (CTStretchInfoProperties)getTypeLoader().newInstance(CTStretchInfoProperties.type, (XmlOptions)null);
        }
        
        public static CTStretchInfoProperties newInstance(final XmlOptions xmlOptions) {
            return (CTStretchInfoProperties)getTypeLoader().newInstance(CTStretchInfoProperties.type, xmlOptions);
        }
        
        public static CTStretchInfoProperties parse(final String s) throws XmlException {
            return (CTStretchInfoProperties)getTypeLoader().parse(s, CTStretchInfoProperties.type, (XmlOptions)null);
        }
        
        public static CTStretchInfoProperties parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTStretchInfoProperties)getTypeLoader().parse(s, CTStretchInfoProperties.type, xmlOptions);
        }
        
        public static CTStretchInfoProperties parse(final File file) throws XmlException, IOException {
            return (CTStretchInfoProperties)getTypeLoader().parse(file, CTStretchInfoProperties.type, (XmlOptions)null);
        }
        
        public static CTStretchInfoProperties parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTStretchInfoProperties)getTypeLoader().parse(file, CTStretchInfoProperties.type, xmlOptions);
        }
        
        public static CTStretchInfoProperties parse(final URL url) throws XmlException, IOException {
            return (CTStretchInfoProperties)getTypeLoader().parse(url, CTStretchInfoProperties.type, (XmlOptions)null);
        }
        
        public static CTStretchInfoProperties parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTStretchInfoProperties)getTypeLoader().parse(url, CTStretchInfoProperties.type, xmlOptions);
        }
        
        public static CTStretchInfoProperties parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTStretchInfoProperties)getTypeLoader().parse(inputStream, CTStretchInfoProperties.type, (XmlOptions)null);
        }
        
        public static CTStretchInfoProperties parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTStretchInfoProperties)getTypeLoader().parse(inputStream, CTStretchInfoProperties.type, xmlOptions);
        }
        
        public static CTStretchInfoProperties parse(final Reader reader) throws XmlException, IOException {
            return (CTStretchInfoProperties)getTypeLoader().parse(reader, CTStretchInfoProperties.type, (XmlOptions)null);
        }
        
        public static CTStretchInfoProperties parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTStretchInfoProperties)getTypeLoader().parse(reader, CTStretchInfoProperties.type, xmlOptions);
        }
        
        public static CTStretchInfoProperties parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTStretchInfoProperties)getTypeLoader().parse(xmlStreamReader, CTStretchInfoProperties.type, (XmlOptions)null);
        }
        
        public static CTStretchInfoProperties parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTStretchInfoProperties)getTypeLoader().parse(xmlStreamReader, CTStretchInfoProperties.type, xmlOptions);
        }
        
        public static CTStretchInfoProperties parse(final Node node) throws XmlException {
            return (CTStretchInfoProperties)getTypeLoader().parse(node, CTStretchInfoProperties.type, (XmlOptions)null);
        }
        
        public static CTStretchInfoProperties parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTStretchInfoProperties)getTypeLoader().parse(node, CTStretchInfoProperties.type, xmlOptions);
        }
        
        @Deprecated
        public static CTStretchInfoProperties parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTStretchInfoProperties)getTypeLoader().parse(xmlInputStream, CTStretchInfoProperties.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTStretchInfoProperties parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTStretchInfoProperties)getTypeLoader().parse(xmlInputStream, CTStretchInfoProperties.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTStretchInfoProperties.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTStretchInfoProperties.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
