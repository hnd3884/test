package org.openxmlformats.schemas.wordprocessingml.x2006.main;

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

public interface CTOnOff extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTOnOff.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctonoff04c2type");
    
    STOnOff.Enum getVal();
    
    STOnOff xgetVal();
    
    boolean isSetVal();
    
    void setVal(final STOnOff.Enum p0);
    
    void xsetVal(final STOnOff p0);
    
    void unsetVal();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTOnOff.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTOnOff newInstance() {
            return (CTOnOff)getTypeLoader().newInstance(CTOnOff.type, (XmlOptions)null);
        }
        
        public static CTOnOff newInstance(final XmlOptions xmlOptions) {
            return (CTOnOff)getTypeLoader().newInstance(CTOnOff.type, xmlOptions);
        }
        
        public static CTOnOff parse(final String s) throws XmlException {
            return (CTOnOff)getTypeLoader().parse(s, CTOnOff.type, (XmlOptions)null);
        }
        
        public static CTOnOff parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTOnOff)getTypeLoader().parse(s, CTOnOff.type, xmlOptions);
        }
        
        public static CTOnOff parse(final File file) throws XmlException, IOException {
            return (CTOnOff)getTypeLoader().parse(file, CTOnOff.type, (XmlOptions)null);
        }
        
        public static CTOnOff parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTOnOff)getTypeLoader().parse(file, CTOnOff.type, xmlOptions);
        }
        
        public static CTOnOff parse(final URL url) throws XmlException, IOException {
            return (CTOnOff)getTypeLoader().parse(url, CTOnOff.type, (XmlOptions)null);
        }
        
        public static CTOnOff parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTOnOff)getTypeLoader().parse(url, CTOnOff.type, xmlOptions);
        }
        
        public static CTOnOff parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTOnOff)getTypeLoader().parse(inputStream, CTOnOff.type, (XmlOptions)null);
        }
        
        public static CTOnOff parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTOnOff)getTypeLoader().parse(inputStream, CTOnOff.type, xmlOptions);
        }
        
        public static CTOnOff parse(final Reader reader) throws XmlException, IOException {
            return (CTOnOff)getTypeLoader().parse(reader, CTOnOff.type, (XmlOptions)null);
        }
        
        public static CTOnOff parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTOnOff)getTypeLoader().parse(reader, CTOnOff.type, xmlOptions);
        }
        
        public static CTOnOff parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTOnOff)getTypeLoader().parse(xmlStreamReader, CTOnOff.type, (XmlOptions)null);
        }
        
        public static CTOnOff parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTOnOff)getTypeLoader().parse(xmlStreamReader, CTOnOff.type, xmlOptions);
        }
        
        public static CTOnOff parse(final Node node) throws XmlException {
            return (CTOnOff)getTypeLoader().parse(node, CTOnOff.type, (XmlOptions)null);
        }
        
        public static CTOnOff parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTOnOff)getTypeLoader().parse(node, CTOnOff.type, xmlOptions);
        }
        
        @Deprecated
        public static CTOnOff parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTOnOff)getTypeLoader().parse(xmlInputStream, CTOnOff.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTOnOff parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTOnOff)getTypeLoader().parse(xmlInputStream, CTOnOff.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTOnOff.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTOnOff.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
