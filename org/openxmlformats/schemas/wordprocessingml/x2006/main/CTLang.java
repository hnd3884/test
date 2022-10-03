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

public interface CTLang extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTLang.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctlangda3atype");
    
    Object getVal();
    
    STLang xgetVal();
    
    void setVal(final Object p0);
    
    void xsetVal(final STLang p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTLang.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTLang newInstance() {
            return (CTLang)getTypeLoader().newInstance(CTLang.type, (XmlOptions)null);
        }
        
        public static CTLang newInstance(final XmlOptions xmlOptions) {
            return (CTLang)getTypeLoader().newInstance(CTLang.type, xmlOptions);
        }
        
        public static CTLang parse(final String s) throws XmlException {
            return (CTLang)getTypeLoader().parse(s, CTLang.type, (XmlOptions)null);
        }
        
        public static CTLang parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTLang)getTypeLoader().parse(s, CTLang.type, xmlOptions);
        }
        
        public static CTLang parse(final File file) throws XmlException, IOException {
            return (CTLang)getTypeLoader().parse(file, CTLang.type, (XmlOptions)null);
        }
        
        public static CTLang parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTLang)getTypeLoader().parse(file, CTLang.type, xmlOptions);
        }
        
        public static CTLang parse(final URL url) throws XmlException, IOException {
            return (CTLang)getTypeLoader().parse(url, CTLang.type, (XmlOptions)null);
        }
        
        public static CTLang parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTLang)getTypeLoader().parse(url, CTLang.type, xmlOptions);
        }
        
        public static CTLang parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTLang)getTypeLoader().parse(inputStream, CTLang.type, (XmlOptions)null);
        }
        
        public static CTLang parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTLang)getTypeLoader().parse(inputStream, CTLang.type, xmlOptions);
        }
        
        public static CTLang parse(final Reader reader) throws XmlException, IOException {
            return (CTLang)getTypeLoader().parse(reader, CTLang.type, (XmlOptions)null);
        }
        
        public static CTLang parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTLang)getTypeLoader().parse(reader, CTLang.type, xmlOptions);
        }
        
        public static CTLang parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTLang)getTypeLoader().parse(xmlStreamReader, CTLang.type, (XmlOptions)null);
        }
        
        public static CTLang parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTLang)getTypeLoader().parse(xmlStreamReader, CTLang.type, xmlOptions);
        }
        
        public static CTLang parse(final Node node) throws XmlException {
            return (CTLang)getTypeLoader().parse(node, CTLang.type, (XmlOptions)null);
        }
        
        public static CTLang parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTLang)getTypeLoader().parse(node, CTLang.type, xmlOptions);
        }
        
        @Deprecated
        public static CTLang parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTLang)getTypeLoader().parse(xmlInputStream, CTLang.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTLang parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTLang)getTypeLoader().parse(xmlInputStream, CTLang.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTLang.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTLang.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
