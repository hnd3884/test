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

public interface CTJc extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTJc.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctjc158ftype");
    
    STJc.Enum getVal();
    
    STJc xgetVal();
    
    void setVal(final STJc.Enum p0);
    
    void xsetVal(final STJc p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTJc.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTJc newInstance() {
            return (CTJc)getTypeLoader().newInstance(CTJc.type, (XmlOptions)null);
        }
        
        public static CTJc newInstance(final XmlOptions xmlOptions) {
            return (CTJc)getTypeLoader().newInstance(CTJc.type, xmlOptions);
        }
        
        public static CTJc parse(final String s) throws XmlException {
            return (CTJc)getTypeLoader().parse(s, CTJc.type, (XmlOptions)null);
        }
        
        public static CTJc parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTJc)getTypeLoader().parse(s, CTJc.type, xmlOptions);
        }
        
        public static CTJc parse(final File file) throws XmlException, IOException {
            return (CTJc)getTypeLoader().parse(file, CTJc.type, (XmlOptions)null);
        }
        
        public static CTJc parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTJc)getTypeLoader().parse(file, CTJc.type, xmlOptions);
        }
        
        public static CTJc parse(final URL url) throws XmlException, IOException {
            return (CTJc)getTypeLoader().parse(url, CTJc.type, (XmlOptions)null);
        }
        
        public static CTJc parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTJc)getTypeLoader().parse(url, CTJc.type, xmlOptions);
        }
        
        public static CTJc parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTJc)getTypeLoader().parse(inputStream, CTJc.type, (XmlOptions)null);
        }
        
        public static CTJc parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTJc)getTypeLoader().parse(inputStream, CTJc.type, xmlOptions);
        }
        
        public static CTJc parse(final Reader reader) throws XmlException, IOException {
            return (CTJc)getTypeLoader().parse(reader, CTJc.type, (XmlOptions)null);
        }
        
        public static CTJc parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTJc)getTypeLoader().parse(reader, CTJc.type, xmlOptions);
        }
        
        public static CTJc parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTJc)getTypeLoader().parse(xmlStreamReader, CTJc.type, (XmlOptions)null);
        }
        
        public static CTJc parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTJc)getTypeLoader().parse(xmlStreamReader, CTJc.type, xmlOptions);
        }
        
        public static CTJc parse(final Node node) throws XmlException {
            return (CTJc)getTypeLoader().parse(node, CTJc.type, (XmlOptions)null);
        }
        
        public static CTJc parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTJc)getTypeLoader().parse(node, CTJc.type, xmlOptions);
        }
        
        @Deprecated
        public static CTJc parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTJc)getTypeLoader().parse(xmlInputStream, CTJc.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTJc parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTJc)getTypeLoader().parse(xmlInputStream, CTJc.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTJc.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTJc.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
