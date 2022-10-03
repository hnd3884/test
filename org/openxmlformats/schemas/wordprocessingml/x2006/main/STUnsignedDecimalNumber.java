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
import org.apache.xmlbeans.XmlUnsignedLong;

public interface STUnsignedDecimalNumber extends XmlUnsignedLong
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(STUnsignedDecimalNumber.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("stunsigneddecimalnumber74fdtype");
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        public static STUnsignedDecimalNumber newValue(final Object o) {
            return (STUnsignedDecimalNumber)STUnsignedDecimalNumber.type.newValue(o);
        }
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(STUnsignedDecimalNumber.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static STUnsignedDecimalNumber newInstance() {
            return (STUnsignedDecimalNumber)getTypeLoader().newInstance(STUnsignedDecimalNumber.type, (XmlOptions)null);
        }
        
        public static STUnsignedDecimalNumber newInstance(final XmlOptions xmlOptions) {
            return (STUnsignedDecimalNumber)getTypeLoader().newInstance(STUnsignedDecimalNumber.type, xmlOptions);
        }
        
        public static STUnsignedDecimalNumber parse(final String s) throws XmlException {
            return (STUnsignedDecimalNumber)getTypeLoader().parse(s, STUnsignedDecimalNumber.type, (XmlOptions)null);
        }
        
        public static STUnsignedDecimalNumber parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (STUnsignedDecimalNumber)getTypeLoader().parse(s, STUnsignedDecimalNumber.type, xmlOptions);
        }
        
        public static STUnsignedDecimalNumber parse(final File file) throws XmlException, IOException {
            return (STUnsignedDecimalNumber)getTypeLoader().parse(file, STUnsignedDecimalNumber.type, (XmlOptions)null);
        }
        
        public static STUnsignedDecimalNumber parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STUnsignedDecimalNumber)getTypeLoader().parse(file, STUnsignedDecimalNumber.type, xmlOptions);
        }
        
        public static STUnsignedDecimalNumber parse(final URL url) throws XmlException, IOException {
            return (STUnsignedDecimalNumber)getTypeLoader().parse(url, STUnsignedDecimalNumber.type, (XmlOptions)null);
        }
        
        public static STUnsignedDecimalNumber parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STUnsignedDecimalNumber)getTypeLoader().parse(url, STUnsignedDecimalNumber.type, xmlOptions);
        }
        
        public static STUnsignedDecimalNumber parse(final InputStream inputStream) throws XmlException, IOException {
            return (STUnsignedDecimalNumber)getTypeLoader().parse(inputStream, STUnsignedDecimalNumber.type, (XmlOptions)null);
        }
        
        public static STUnsignedDecimalNumber parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STUnsignedDecimalNumber)getTypeLoader().parse(inputStream, STUnsignedDecimalNumber.type, xmlOptions);
        }
        
        public static STUnsignedDecimalNumber parse(final Reader reader) throws XmlException, IOException {
            return (STUnsignedDecimalNumber)getTypeLoader().parse(reader, STUnsignedDecimalNumber.type, (XmlOptions)null);
        }
        
        public static STUnsignedDecimalNumber parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STUnsignedDecimalNumber)getTypeLoader().parse(reader, STUnsignedDecimalNumber.type, xmlOptions);
        }
        
        public static STUnsignedDecimalNumber parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (STUnsignedDecimalNumber)getTypeLoader().parse(xmlStreamReader, STUnsignedDecimalNumber.type, (XmlOptions)null);
        }
        
        public static STUnsignedDecimalNumber parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (STUnsignedDecimalNumber)getTypeLoader().parse(xmlStreamReader, STUnsignedDecimalNumber.type, xmlOptions);
        }
        
        public static STUnsignedDecimalNumber parse(final Node node) throws XmlException {
            return (STUnsignedDecimalNumber)getTypeLoader().parse(node, STUnsignedDecimalNumber.type, (XmlOptions)null);
        }
        
        public static STUnsignedDecimalNumber parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (STUnsignedDecimalNumber)getTypeLoader().parse(node, STUnsignedDecimalNumber.type, xmlOptions);
        }
        
        @Deprecated
        public static STUnsignedDecimalNumber parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (STUnsignedDecimalNumber)getTypeLoader().parse(xmlInputStream, STUnsignedDecimalNumber.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static STUnsignedDecimalNumber parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (STUnsignedDecimalNumber)getTypeLoader().parse(xmlInputStream, STUnsignedDecimalNumber.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STUnsignedDecimalNumber.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STUnsignedDecimalNumber.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
