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

public interface CTTextAlignment extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTTextAlignment.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("cttextalignment495ctype");
    
    STTextAlignment.Enum getVal();
    
    STTextAlignment xgetVal();
    
    void setVal(final STTextAlignment.Enum p0);
    
    void xsetVal(final STTextAlignment p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTTextAlignment.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTTextAlignment newInstance() {
            return (CTTextAlignment)getTypeLoader().newInstance(CTTextAlignment.type, (XmlOptions)null);
        }
        
        public static CTTextAlignment newInstance(final XmlOptions xmlOptions) {
            return (CTTextAlignment)getTypeLoader().newInstance(CTTextAlignment.type, xmlOptions);
        }
        
        public static CTTextAlignment parse(final String s) throws XmlException {
            return (CTTextAlignment)getTypeLoader().parse(s, CTTextAlignment.type, (XmlOptions)null);
        }
        
        public static CTTextAlignment parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTTextAlignment)getTypeLoader().parse(s, CTTextAlignment.type, xmlOptions);
        }
        
        public static CTTextAlignment parse(final File file) throws XmlException, IOException {
            return (CTTextAlignment)getTypeLoader().parse(file, CTTextAlignment.type, (XmlOptions)null);
        }
        
        public static CTTextAlignment parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTextAlignment)getTypeLoader().parse(file, CTTextAlignment.type, xmlOptions);
        }
        
        public static CTTextAlignment parse(final URL url) throws XmlException, IOException {
            return (CTTextAlignment)getTypeLoader().parse(url, CTTextAlignment.type, (XmlOptions)null);
        }
        
        public static CTTextAlignment parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTextAlignment)getTypeLoader().parse(url, CTTextAlignment.type, xmlOptions);
        }
        
        public static CTTextAlignment parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTTextAlignment)getTypeLoader().parse(inputStream, CTTextAlignment.type, (XmlOptions)null);
        }
        
        public static CTTextAlignment parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTextAlignment)getTypeLoader().parse(inputStream, CTTextAlignment.type, xmlOptions);
        }
        
        public static CTTextAlignment parse(final Reader reader) throws XmlException, IOException {
            return (CTTextAlignment)getTypeLoader().parse(reader, CTTextAlignment.type, (XmlOptions)null);
        }
        
        public static CTTextAlignment parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTextAlignment)getTypeLoader().parse(reader, CTTextAlignment.type, xmlOptions);
        }
        
        public static CTTextAlignment parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTTextAlignment)getTypeLoader().parse(xmlStreamReader, CTTextAlignment.type, (XmlOptions)null);
        }
        
        public static CTTextAlignment parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTTextAlignment)getTypeLoader().parse(xmlStreamReader, CTTextAlignment.type, xmlOptions);
        }
        
        public static CTTextAlignment parse(final Node node) throws XmlException {
            return (CTTextAlignment)getTypeLoader().parse(node, CTTextAlignment.type, (XmlOptions)null);
        }
        
        public static CTTextAlignment parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTTextAlignment)getTypeLoader().parse(node, CTTextAlignment.type, xmlOptions);
        }
        
        @Deprecated
        public static CTTextAlignment parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTTextAlignment)getTypeLoader().parse(xmlInputStream, CTTextAlignment.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTTextAlignment parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTTextAlignment)getTypeLoader().parse(xmlInputStream, CTTextAlignment.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTTextAlignment.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTTextAlignment.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
