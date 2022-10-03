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
import org.apache.xmlbeans.XmlAnySimpleType;

public interface STLang extends XmlAnySimpleType
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(STLang.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("stlanga02atype");
    
    Object getObjectValue();
    
    void setObjectValue(final Object p0);
    
    @Deprecated
    Object objectValue();
    
    @Deprecated
    void objectSet(final Object p0);
    
    SchemaType instanceType();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        public static STLang newValue(final Object o) {
            return (STLang)STLang.type.newValue(o);
        }
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(STLang.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static STLang newInstance() {
            return (STLang)getTypeLoader().newInstance(STLang.type, (XmlOptions)null);
        }
        
        public static STLang newInstance(final XmlOptions xmlOptions) {
            return (STLang)getTypeLoader().newInstance(STLang.type, xmlOptions);
        }
        
        public static STLang parse(final String s) throws XmlException {
            return (STLang)getTypeLoader().parse(s, STLang.type, (XmlOptions)null);
        }
        
        public static STLang parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (STLang)getTypeLoader().parse(s, STLang.type, xmlOptions);
        }
        
        public static STLang parse(final File file) throws XmlException, IOException {
            return (STLang)getTypeLoader().parse(file, STLang.type, (XmlOptions)null);
        }
        
        public static STLang parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STLang)getTypeLoader().parse(file, STLang.type, xmlOptions);
        }
        
        public static STLang parse(final URL url) throws XmlException, IOException {
            return (STLang)getTypeLoader().parse(url, STLang.type, (XmlOptions)null);
        }
        
        public static STLang parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STLang)getTypeLoader().parse(url, STLang.type, xmlOptions);
        }
        
        public static STLang parse(final InputStream inputStream) throws XmlException, IOException {
            return (STLang)getTypeLoader().parse(inputStream, STLang.type, (XmlOptions)null);
        }
        
        public static STLang parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STLang)getTypeLoader().parse(inputStream, STLang.type, xmlOptions);
        }
        
        public static STLang parse(final Reader reader) throws XmlException, IOException {
            return (STLang)getTypeLoader().parse(reader, STLang.type, (XmlOptions)null);
        }
        
        public static STLang parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STLang)getTypeLoader().parse(reader, STLang.type, xmlOptions);
        }
        
        public static STLang parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (STLang)getTypeLoader().parse(xmlStreamReader, STLang.type, (XmlOptions)null);
        }
        
        public static STLang parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (STLang)getTypeLoader().parse(xmlStreamReader, STLang.type, xmlOptions);
        }
        
        public static STLang parse(final Node node) throws XmlException {
            return (STLang)getTypeLoader().parse(node, STLang.type, (XmlOptions)null);
        }
        
        public static STLang parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (STLang)getTypeLoader().parse(node, STLang.type, xmlOptions);
        }
        
        @Deprecated
        public static STLang parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (STLang)getTypeLoader().parse(xmlInputStream, STLang.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static STLang parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (STLang)getTypeLoader().parse(xmlInputStream, STLang.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STLang.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STLang.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
