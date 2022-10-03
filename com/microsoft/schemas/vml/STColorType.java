package com.microsoft.schemas.vml;

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
import org.apache.xmlbeans.XmlString;

public interface STColorType extends XmlString
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(STColorType.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("stcolortype99c1type");
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        public static STColorType newValue(final Object o) {
            return (STColorType)STColorType.type.newValue(o);
        }
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(STColorType.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static STColorType newInstance() {
            return (STColorType)getTypeLoader().newInstance(STColorType.type, (XmlOptions)null);
        }
        
        public static STColorType newInstance(final XmlOptions xmlOptions) {
            return (STColorType)getTypeLoader().newInstance(STColorType.type, xmlOptions);
        }
        
        public static STColorType parse(final String s) throws XmlException {
            return (STColorType)getTypeLoader().parse(s, STColorType.type, (XmlOptions)null);
        }
        
        public static STColorType parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (STColorType)getTypeLoader().parse(s, STColorType.type, xmlOptions);
        }
        
        public static STColorType parse(final File file) throws XmlException, IOException {
            return (STColorType)getTypeLoader().parse(file, STColorType.type, (XmlOptions)null);
        }
        
        public static STColorType parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STColorType)getTypeLoader().parse(file, STColorType.type, xmlOptions);
        }
        
        public static STColorType parse(final URL url) throws XmlException, IOException {
            return (STColorType)getTypeLoader().parse(url, STColorType.type, (XmlOptions)null);
        }
        
        public static STColorType parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STColorType)getTypeLoader().parse(url, STColorType.type, xmlOptions);
        }
        
        public static STColorType parse(final InputStream inputStream) throws XmlException, IOException {
            return (STColorType)getTypeLoader().parse(inputStream, STColorType.type, (XmlOptions)null);
        }
        
        public static STColorType parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STColorType)getTypeLoader().parse(inputStream, STColorType.type, xmlOptions);
        }
        
        public static STColorType parse(final Reader reader) throws XmlException, IOException {
            return (STColorType)getTypeLoader().parse(reader, STColorType.type, (XmlOptions)null);
        }
        
        public static STColorType parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STColorType)getTypeLoader().parse(reader, STColorType.type, xmlOptions);
        }
        
        public static STColorType parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (STColorType)getTypeLoader().parse(xmlStreamReader, STColorType.type, (XmlOptions)null);
        }
        
        public static STColorType parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (STColorType)getTypeLoader().parse(xmlStreamReader, STColorType.type, xmlOptions);
        }
        
        public static STColorType parse(final Node node) throws XmlException {
            return (STColorType)getTypeLoader().parse(node, STColorType.type, (XmlOptions)null);
        }
        
        public static STColorType parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (STColorType)getTypeLoader().parse(node, STColorType.type, xmlOptions);
        }
        
        @Deprecated
        public static STColorType parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (STColorType)getTypeLoader().parse(xmlInputStream, STColorType.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static STColorType parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (STColorType)getTypeLoader().parse(xmlInputStream, STColorType.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STColorType.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STColorType.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
