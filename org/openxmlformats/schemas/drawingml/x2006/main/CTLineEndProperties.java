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

public interface CTLineEndProperties extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTLineEndProperties.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctlineendproperties8acbtype");
    
    STLineEndType.Enum getType();
    
    STLineEndType xgetType();
    
    boolean isSetType();
    
    void setType(final STLineEndType.Enum p0);
    
    void xsetType(final STLineEndType p0);
    
    void unsetType();
    
    STLineEndWidth.Enum getW();
    
    STLineEndWidth xgetW();
    
    boolean isSetW();
    
    void setW(final STLineEndWidth.Enum p0);
    
    void xsetW(final STLineEndWidth p0);
    
    void unsetW();
    
    STLineEndLength.Enum getLen();
    
    STLineEndLength xgetLen();
    
    boolean isSetLen();
    
    void setLen(final STLineEndLength.Enum p0);
    
    void xsetLen(final STLineEndLength p0);
    
    void unsetLen();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTLineEndProperties.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTLineEndProperties newInstance() {
            return (CTLineEndProperties)getTypeLoader().newInstance(CTLineEndProperties.type, (XmlOptions)null);
        }
        
        public static CTLineEndProperties newInstance(final XmlOptions xmlOptions) {
            return (CTLineEndProperties)getTypeLoader().newInstance(CTLineEndProperties.type, xmlOptions);
        }
        
        public static CTLineEndProperties parse(final String s) throws XmlException {
            return (CTLineEndProperties)getTypeLoader().parse(s, CTLineEndProperties.type, (XmlOptions)null);
        }
        
        public static CTLineEndProperties parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTLineEndProperties)getTypeLoader().parse(s, CTLineEndProperties.type, xmlOptions);
        }
        
        public static CTLineEndProperties parse(final File file) throws XmlException, IOException {
            return (CTLineEndProperties)getTypeLoader().parse(file, CTLineEndProperties.type, (XmlOptions)null);
        }
        
        public static CTLineEndProperties parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTLineEndProperties)getTypeLoader().parse(file, CTLineEndProperties.type, xmlOptions);
        }
        
        public static CTLineEndProperties parse(final URL url) throws XmlException, IOException {
            return (CTLineEndProperties)getTypeLoader().parse(url, CTLineEndProperties.type, (XmlOptions)null);
        }
        
        public static CTLineEndProperties parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTLineEndProperties)getTypeLoader().parse(url, CTLineEndProperties.type, xmlOptions);
        }
        
        public static CTLineEndProperties parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTLineEndProperties)getTypeLoader().parse(inputStream, CTLineEndProperties.type, (XmlOptions)null);
        }
        
        public static CTLineEndProperties parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTLineEndProperties)getTypeLoader().parse(inputStream, CTLineEndProperties.type, xmlOptions);
        }
        
        public static CTLineEndProperties parse(final Reader reader) throws XmlException, IOException {
            return (CTLineEndProperties)getTypeLoader().parse(reader, CTLineEndProperties.type, (XmlOptions)null);
        }
        
        public static CTLineEndProperties parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTLineEndProperties)getTypeLoader().parse(reader, CTLineEndProperties.type, xmlOptions);
        }
        
        public static CTLineEndProperties parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTLineEndProperties)getTypeLoader().parse(xmlStreamReader, CTLineEndProperties.type, (XmlOptions)null);
        }
        
        public static CTLineEndProperties parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTLineEndProperties)getTypeLoader().parse(xmlStreamReader, CTLineEndProperties.type, xmlOptions);
        }
        
        public static CTLineEndProperties parse(final Node node) throws XmlException {
            return (CTLineEndProperties)getTypeLoader().parse(node, CTLineEndProperties.type, (XmlOptions)null);
        }
        
        public static CTLineEndProperties parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTLineEndProperties)getTypeLoader().parse(node, CTLineEndProperties.type, xmlOptions);
        }
        
        @Deprecated
        public static CTLineEndProperties parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTLineEndProperties)getTypeLoader().parse(xmlInputStream, CTLineEndProperties.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTLineEndProperties parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTLineEndProperties)getTypeLoader().parse(xmlInputStream, CTLineEndProperties.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTLineEndProperties.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTLineEndProperties.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
