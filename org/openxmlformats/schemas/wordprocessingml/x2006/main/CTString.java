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

public interface CTString extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTString.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctstring9c37type");
    
    String getVal();
    
    STString xgetVal();
    
    void setVal(final String p0);
    
    void xsetVal(final STString p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTString.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTString newInstance() {
            return (CTString)getTypeLoader().newInstance(CTString.type, (XmlOptions)null);
        }
        
        public static CTString newInstance(final XmlOptions xmlOptions) {
            return (CTString)getTypeLoader().newInstance(CTString.type, xmlOptions);
        }
        
        public static CTString parse(final String s) throws XmlException {
            return (CTString)getTypeLoader().parse(s, CTString.type, (XmlOptions)null);
        }
        
        public static CTString parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTString)getTypeLoader().parse(s, CTString.type, xmlOptions);
        }
        
        public static CTString parse(final File file) throws XmlException, IOException {
            return (CTString)getTypeLoader().parse(file, CTString.type, (XmlOptions)null);
        }
        
        public static CTString parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTString)getTypeLoader().parse(file, CTString.type, xmlOptions);
        }
        
        public static CTString parse(final URL url) throws XmlException, IOException {
            return (CTString)getTypeLoader().parse(url, CTString.type, (XmlOptions)null);
        }
        
        public static CTString parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTString)getTypeLoader().parse(url, CTString.type, xmlOptions);
        }
        
        public static CTString parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTString)getTypeLoader().parse(inputStream, CTString.type, (XmlOptions)null);
        }
        
        public static CTString parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTString)getTypeLoader().parse(inputStream, CTString.type, xmlOptions);
        }
        
        public static CTString parse(final Reader reader) throws XmlException, IOException {
            return (CTString)getTypeLoader().parse(reader, CTString.type, (XmlOptions)null);
        }
        
        public static CTString parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTString)getTypeLoader().parse(reader, CTString.type, xmlOptions);
        }
        
        public static CTString parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTString)getTypeLoader().parse(xmlStreamReader, CTString.type, (XmlOptions)null);
        }
        
        public static CTString parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTString)getTypeLoader().parse(xmlStreamReader, CTString.type, xmlOptions);
        }
        
        public static CTString parse(final Node node) throws XmlException {
            return (CTString)getTypeLoader().parse(node, CTString.type, (XmlOptions)null);
        }
        
        public static CTString parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTString)getTypeLoader().parse(node, CTString.type, xmlOptions);
        }
        
        @Deprecated
        public static CTString parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTString)getTypeLoader().parse(xmlInputStream, CTString.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTString parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTString)getTypeLoader().parse(xmlInputStream, CTString.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTString.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTString.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
