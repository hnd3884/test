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
import org.apache.xmlbeans.XmlInteger;

public interface STDecimalNumber extends XmlInteger
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(STDecimalNumber.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("stdecimalnumber8d28type");
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        public static STDecimalNumber newValue(final Object o) {
            return (STDecimalNumber)STDecimalNumber.type.newValue(o);
        }
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(STDecimalNumber.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static STDecimalNumber newInstance() {
            return (STDecimalNumber)getTypeLoader().newInstance(STDecimalNumber.type, (XmlOptions)null);
        }
        
        public static STDecimalNumber newInstance(final XmlOptions xmlOptions) {
            return (STDecimalNumber)getTypeLoader().newInstance(STDecimalNumber.type, xmlOptions);
        }
        
        public static STDecimalNumber parse(final String s) throws XmlException {
            return (STDecimalNumber)getTypeLoader().parse(s, STDecimalNumber.type, (XmlOptions)null);
        }
        
        public static STDecimalNumber parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (STDecimalNumber)getTypeLoader().parse(s, STDecimalNumber.type, xmlOptions);
        }
        
        public static STDecimalNumber parse(final File file) throws XmlException, IOException {
            return (STDecimalNumber)getTypeLoader().parse(file, STDecimalNumber.type, (XmlOptions)null);
        }
        
        public static STDecimalNumber parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STDecimalNumber)getTypeLoader().parse(file, STDecimalNumber.type, xmlOptions);
        }
        
        public static STDecimalNumber parse(final URL url) throws XmlException, IOException {
            return (STDecimalNumber)getTypeLoader().parse(url, STDecimalNumber.type, (XmlOptions)null);
        }
        
        public static STDecimalNumber parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STDecimalNumber)getTypeLoader().parse(url, STDecimalNumber.type, xmlOptions);
        }
        
        public static STDecimalNumber parse(final InputStream inputStream) throws XmlException, IOException {
            return (STDecimalNumber)getTypeLoader().parse(inputStream, STDecimalNumber.type, (XmlOptions)null);
        }
        
        public static STDecimalNumber parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STDecimalNumber)getTypeLoader().parse(inputStream, STDecimalNumber.type, xmlOptions);
        }
        
        public static STDecimalNumber parse(final Reader reader) throws XmlException, IOException {
            return (STDecimalNumber)getTypeLoader().parse(reader, STDecimalNumber.type, (XmlOptions)null);
        }
        
        public static STDecimalNumber parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STDecimalNumber)getTypeLoader().parse(reader, STDecimalNumber.type, xmlOptions);
        }
        
        public static STDecimalNumber parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (STDecimalNumber)getTypeLoader().parse(xmlStreamReader, STDecimalNumber.type, (XmlOptions)null);
        }
        
        public static STDecimalNumber parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (STDecimalNumber)getTypeLoader().parse(xmlStreamReader, STDecimalNumber.type, xmlOptions);
        }
        
        public static STDecimalNumber parse(final Node node) throws XmlException {
            return (STDecimalNumber)getTypeLoader().parse(node, STDecimalNumber.type, (XmlOptions)null);
        }
        
        public static STDecimalNumber parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (STDecimalNumber)getTypeLoader().parse(node, STDecimalNumber.type, xmlOptions);
        }
        
        @Deprecated
        public static STDecimalNumber parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (STDecimalNumber)getTypeLoader().parse(xmlInputStream, STDecimalNumber.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static STDecimalNumber parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (STDecimalNumber)getTypeLoader().parse(xmlInputStream, STDecimalNumber.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STDecimalNumber.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STDecimalNumber.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
