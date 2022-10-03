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

public interface CTPathShadeProperties extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTPathShadeProperties.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctpathshadeproperties7ccctype");
    
    CTRelativeRect getFillToRect();
    
    boolean isSetFillToRect();
    
    void setFillToRect(final CTRelativeRect p0);
    
    CTRelativeRect addNewFillToRect();
    
    void unsetFillToRect();
    
    STPathShadeType.Enum getPath();
    
    STPathShadeType xgetPath();
    
    boolean isSetPath();
    
    void setPath(final STPathShadeType.Enum p0);
    
    void xsetPath(final STPathShadeType p0);
    
    void unsetPath();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTPathShadeProperties.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTPathShadeProperties newInstance() {
            return (CTPathShadeProperties)getTypeLoader().newInstance(CTPathShadeProperties.type, (XmlOptions)null);
        }
        
        public static CTPathShadeProperties newInstance(final XmlOptions xmlOptions) {
            return (CTPathShadeProperties)getTypeLoader().newInstance(CTPathShadeProperties.type, xmlOptions);
        }
        
        public static CTPathShadeProperties parse(final String s) throws XmlException {
            return (CTPathShadeProperties)getTypeLoader().parse(s, CTPathShadeProperties.type, (XmlOptions)null);
        }
        
        public static CTPathShadeProperties parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTPathShadeProperties)getTypeLoader().parse(s, CTPathShadeProperties.type, xmlOptions);
        }
        
        public static CTPathShadeProperties parse(final File file) throws XmlException, IOException {
            return (CTPathShadeProperties)getTypeLoader().parse(file, CTPathShadeProperties.type, (XmlOptions)null);
        }
        
        public static CTPathShadeProperties parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTPathShadeProperties)getTypeLoader().parse(file, CTPathShadeProperties.type, xmlOptions);
        }
        
        public static CTPathShadeProperties parse(final URL url) throws XmlException, IOException {
            return (CTPathShadeProperties)getTypeLoader().parse(url, CTPathShadeProperties.type, (XmlOptions)null);
        }
        
        public static CTPathShadeProperties parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTPathShadeProperties)getTypeLoader().parse(url, CTPathShadeProperties.type, xmlOptions);
        }
        
        public static CTPathShadeProperties parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTPathShadeProperties)getTypeLoader().parse(inputStream, CTPathShadeProperties.type, (XmlOptions)null);
        }
        
        public static CTPathShadeProperties parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTPathShadeProperties)getTypeLoader().parse(inputStream, CTPathShadeProperties.type, xmlOptions);
        }
        
        public static CTPathShadeProperties parse(final Reader reader) throws XmlException, IOException {
            return (CTPathShadeProperties)getTypeLoader().parse(reader, CTPathShadeProperties.type, (XmlOptions)null);
        }
        
        public static CTPathShadeProperties parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTPathShadeProperties)getTypeLoader().parse(reader, CTPathShadeProperties.type, xmlOptions);
        }
        
        public static CTPathShadeProperties parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTPathShadeProperties)getTypeLoader().parse(xmlStreamReader, CTPathShadeProperties.type, (XmlOptions)null);
        }
        
        public static CTPathShadeProperties parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTPathShadeProperties)getTypeLoader().parse(xmlStreamReader, CTPathShadeProperties.type, xmlOptions);
        }
        
        public static CTPathShadeProperties parse(final Node node) throws XmlException {
            return (CTPathShadeProperties)getTypeLoader().parse(node, CTPathShadeProperties.type, (XmlOptions)null);
        }
        
        public static CTPathShadeProperties parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTPathShadeProperties)getTypeLoader().parse(node, CTPathShadeProperties.type, xmlOptions);
        }
        
        @Deprecated
        public static CTPathShadeProperties parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTPathShadeProperties)getTypeLoader().parse(xmlInputStream, CTPathShadeProperties.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTPathShadeProperties parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTPathShadeProperties)getTypeLoader().parse(xmlInputStream, CTPathShadeProperties.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTPathShadeProperties.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTPathShadeProperties.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
