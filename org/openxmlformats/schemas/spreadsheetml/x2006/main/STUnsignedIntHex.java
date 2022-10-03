package org.openxmlformats.schemas.spreadsheetml.x2006.main;

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

public interface STUnsignedIntHex extends XmlHexBinary
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(STUnsignedIntHex.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("stunsignedinthex27datype");
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        public static STUnsignedIntHex newValue(final Object o) {
            return (STUnsignedIntHex)STUnsignedIntHex.type.newValue(o);
        }
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(STUnsignedIntHex.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static STUnsignedIntHex newInstance() {
            return (STUnsignedIntHex)getTypeLoader().newInstance(STUnsignedIntHex.type, (XmlOptions)null);
        }
        
        public static STUnsignedIntHex newInstance(final XmlOptions xmlOptions) {
            return (STUnsignedIntHex)getTypeLoader().newInstance(STUnsignedIntHex.type, xmlOptions);
        }
        
        public static STUnsignedIntHex parse(final String s) throws XmlException {
            return (STUnsignedIntHex)getTypeLoader().parse(s, STUnsignedIntHex.type, (XmlOptions)null);
        }
        
        public static STUnsignedIntHex parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (STUnsignedIntHex)getTypeLoader().parse(s, STUnsignedIntHex.type, xmlOptions);
        }
        
        public static STUnsignedIntHex parse(final File file) throws XmlException, IOException {
            return (STUnsignedIntHex)getTypeLoader().parse(file, STUnsignedIntHex.type, (XmlOptions)null);
        }
        
        public static STUnsignedIntHex parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STUnsignedIntHex)getTypeLoader().parse(file, STUnsignedIntHex.type, xmlOptions);
        }
        
        public static STUnsignedIntHex parse(final URL url) throws XmlException, IOException {
            return (STUnsignedIntHex)getTypeLoader().parse(url, STUnsignedIntHex.type, (XmlOptions)null);
        }
        
        public static STUnsignedIntHex parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STUnsignedIntHex)getTypeLoader().parse(url, STUnsignedIntHex.type, xmlOptions);
        }
        
        public static STUnsignedIntHex parse(final InputStream inputStream) throws XmlException, IOException {
            return (STUnsignedIntHex)getTypeLoader().parse(inputStream, STUnsignedIntHex.type, (XmlOptions)null);
        }
        
        public static STUnsignedIntHex parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STUnsignedIntHex)getTypeLoader().parse(inputStream, STUnsignedIntHex.type, xmlOptions);
        }
        
        public static STUnsignedIntHex parse(final Reader reader) throws XmlException, IOException {
            return (STUnsignedIntHex)getTypeLoader().parse(reader, STUnsignedIntHex.type, (XmlOptions)null);
        }
        
        public static STUnsignedIntHex parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STUnsignedIntHex)getTypeLoader().parse(reader, STUnsignedIntHex.type, xmlOptions);
        }
        
        public static STUnsignedIntHex parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (STUnsignedIntHex)getTypeLoader().parse(xmlStreamReader, STUnsignedIntHex.type, (XmlOptions)null);
        }
        
        public static STUnsignedIntHex parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (STUnsignedIntHex)getTypeLoader().parse(xmlStreamReader, STUnsignedIntHex.type, xmlOptions);
        }
        
        public static STUnsignedIntHex parse(final Node node) throws XmlException {
            return (STUnsignedIntHex)getTypeLoader().parse(node, STUnsignedIntHex.type, (XmlOptions)null);
        }
        
        public static STUnsignedIntHex parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (STUnsignedIntHex)getTypeLoader().parse(node, STUnsignedIntHex.type, xmlOptions);
        }
        
        @Deprecated
        public static STUnsignedIntHex parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (STUnsignedIntHex)getTypeLoader().parse(xmlInputStream, STUnsignedIntHex.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static STUnsignedIntHex parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (STUnsignedIntHex)getTypeLoader().parse(xmlInputStream, STUnsignedIntHex.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STUnsignedIntHex.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STUnsignedIntHex.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
