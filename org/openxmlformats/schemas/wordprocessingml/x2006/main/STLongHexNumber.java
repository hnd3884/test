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
import org.apache.xmlbeans.XmlHexBinary;

public interface STLongHexNumber extends XmlHexBinary
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(STLongHexNumber.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("stlonghexnumberd6batype");
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        public static STLongHexNumber newValue(final Object o) {
            return (STLongHexNumber)STLongHexNumber.type.newValue(o);
        }
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(STLongHexNumber.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static STLongHexNumber newInstance() {
            return (STLongHexNumber)getTypeLoader().newInstance(STLongHexNumber.type, (XmlOptions)null);
        }
        
        public static STLongHexNumber newInstance(final XmlOptions xmlOptions) {
            return (STLongHexNumber)getTypeLoader().newInstance(STLongHexNumber.type, xmlOptions);
        }
        
        public static STLongHexNumber parse(final String s) throws XmlException {
            return (STLongHexNumber)getTypeLoader().parse(s, STLongHexNumber.type, (XmlOptions)null);
        }
        
        public static STLongHexNumber parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (STLongHexNumber)getTypeLoader().parse(s, STLongHexNumber.type, xmlOptions);
        }
        
        public static STLongHexNumber parse(final File file) throws XmlException, IOException {
            return (STLongHexNumber)getTypeLoader().parse(file, STLongHexNumber.type, (XmlOptions)null);
        }
        
        public static STLongHexNumber parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STLongHexNumber)getTypeLoader().parse(file, STLongHexNumber.type, xmlOptions);
        }
        
        public static STLongHexNumber parse(final URL url) throws XmlException, IOException {
            return (STLongHexNumber)getTypeLoader().parse(url, STLongHexNumber.type, (XmlOptions)null);
        }
        
        public static STLongHexNumber parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STLongHexNumber)getTypeLoader().parse(url, STLongHexNumber.type, xmlOptions);
        }
        
        public static STLongHexNumber parse(final InputStream inputStream) throws XmlException, IOException {
            return (STLongHexNumber)getTypeLoader().parse(inputStream, STLongHexNumber.type, (XmlOptions)null);
        }
        
        public static STLongHexNumber parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STLongHexNumber)getTypeLoader().parse(inputStream, STLongHexNumber.type, xmlOptions);
        }
        
        public static STLongHexNumber parse(final Reader reader) throws XmlException, IOException {
            return (STLongHexNumber)getTypeLoader().parse(reader, STLongHexNumber.type, (XmlOptions)null);
        }
        
        public static STLongHexNumber parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STLongHexNumber)getTypeLoader().parse(reader, STLongHexNumber.type, xmlOptions);
        }
        
        public static STLongHexNumber parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (STLongHexNumber)getTypeLoader().parse(xmlStreamReader, STLongHexNumber.type, (XmlOptions)null);
        }
        
        public static STLongHexNumber parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (STLongHexNumber)getTypeLoader().parse(xmlStreamReader, STLongHexNumber.type, xmlOptions);
        }
        
        public static STLongHexNumber parse(final Node node) throws XmlException {
            return (STLongHexNumber)getTypeLoader().parse(node, STLongHexNumber.type, (XmlOptions)null);
        }
        
        public static STLongHexNumber parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (STLongHexNumber)getTypeLoader().parse(node, STLongHexNumber.type, xmlOptions);
        }
        
        @Deprecated
        public static STLongHexNumber parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (STLongHexNumber)getTypeLoader().parse(xmlInputStream, STLongHexNumber.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static STLongHexNumber parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (STLongHexNumber)getTypeLoader().parse(xmlInputStream, STLongHexNumber.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STLongHexNumber.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STLongHexNumber.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
