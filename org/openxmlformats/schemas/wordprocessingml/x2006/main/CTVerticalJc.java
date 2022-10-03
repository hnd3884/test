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

public interface CTVerticalJc extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTVerticalJc.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctverticaljca439type");
    
    STVerticalJc.Enum getVal();
    
    STVerticalJc xgetVal();
    
    void setVal(final STVerticalJc.Enum p0);
    
    void xsetVal(final STVerticalJc p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTVerticalJc.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTVerticalJc newInstance() {
            return (CTVerticalJc)getTypeLoader().newInstance(CTVerticalJc.type, (XmlOptions)null);
        }
        
        public static CTVerticalJc newInstance(final XmlOptions xmlOptions) {
            return (CTVerticalJc)getTypeLoader().newInstance(CTVerticalJc.type, xmlOptions);
        }
        
        public static CTVerticalJc parse(final String s) throws XmlException {
            return (CTVerticalJc)getTypeLoader().parse(s, CTVerticalJc.type, (XmlOptions)null);
        }
        
        public static CTVerticalJc parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTVerticalJc)getTypeLoader().parse(s, CTVerticalJc.type, xmlOptions);
        }
        
        public static CTVerticalJc parse(final File file) throws XmlException, IOException {
            return (CTVerticalJc)getTypeLoader().parse(file, CTVerticalJc.type, (XmlOptions)null);
        }
        
        public static CTVerticalJc parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTVerticalJc)getTypeLoader().parse(file, CTVerticalJc.type, xmlOptions);
        }
        
        public static CTVerticalJc parse(final URL url) throws XmlException, IOException {
            return (CTVerticalJc)getTypeLoader().parse(url, CTVerticalJc.type, (XmlOptions)null);
        }
        
        public static CTVerticalJc parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTVerticalJc)getTypeLoader().parse(url, CTVerticalJc.type, xmlOptions);
        }
        
        public static CTVerticalJc parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTVerticalJc)getTypeLoader().parse(inputStream, CTVerticalJc.type, (XmlOptions)null);
        }
        
        public static CTVerticalJc parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTVerticalJc)getTypeLoader().parse(inputStream, CTVerticalJc.type, xmlOptions);
        }
        
        public static CTVerticalJc parse(final Reader reader) throws XmlException, IOException {
            return (CTVerticalJc)getTypeLoader().parse(reader, CTVerticalJc.type, (XmlOptions)null);
        }
        
        public static CTVerticalJc parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTVerticalJc)getTypeLoader().parse(reader, CTVerticalJc.type, xmlOptions);
        }
        
        public static CTVerticalJc parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTVerticalJc)getTypeLoader().parse(xmlStreamReader, CTVerticalJc.type, (XmlOptions)null);
        }
        
        public static CTVerticalJc parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTVerticalJc)getTypeLoader().parse(xmlStreamReader, CTVerticalJc.type, xmlOptions);
        }
        
        public static CTVerticalJc parse(final Node node) throws XmlException {
            return (CTVerticalJc)getTypeLoader().parse(node, CTVerticalJc.type, (XmlOptions)null);
        }
        
        public static CTVerticalJc parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTVerticalJc)getTypeLoader().parse(node, CTVerticalJc.type, xmlOptions);
        }
        
        @Deprecated
        public static CTVerticalJc parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTVerticalJc)getTypeLoader().parse(xmlInputStream, CTVerticalJc.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTVerticalJc parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTVerticalJc)getTypeLoader().parse(xmlInputStream, CTVerticalJc.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTVerticalJc.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTVerticalJc.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
