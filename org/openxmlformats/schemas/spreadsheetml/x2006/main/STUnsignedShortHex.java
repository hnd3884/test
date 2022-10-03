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

public interface STUnsignedShortHex extends XmlHexBinary
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(STUnsignedShortHex.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("stunsignedshorthex0bedtype");
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        public static STUnsignedShortHex newValue(final Object o) {
            return (STUnsignedShortHex)STUnsignedShortHex.type.newValue(o);
        }
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(STUnsignedShortHex.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static STUnsignedShortHex newInstance() {
            return (STUnsignedShortHex)getTypeLoader().newInstance(STUnsignedShortHex.type, (XmlOptions)null);
        }
        
        public static STUnsignedShortHex newInstance(final XmlOptions xmlOptions) {
            return (STUnsignedShortHex)getTypeLoader().newInstance(STUnsignedShortHex.type, xmlOptions);
        }
        
        public static STUnsignedShortHex parse(final String s) throws XmlException {
            return (STUnsignedShortHex)getTypeLoader().parse(s, STUnsignedShortHex.type, (XmlOptions)null);
        }
        
        public static STUnsignedShortHex parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (STUnsignedShortHex)getTypeLoader().parse(s, STUnsignedShortHex.type, xmlOptions);
        }
        
        public static STUnsignedShortHex parse(final File file) throws XmlException, IOException {
            return (STUnsignedShortHex)getTypeLoader().parse(file, STUnsignedShortHex.type, (XmlOptions)null);
        }
        
        public static STUnsignedShortHex parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STUnsignedShortHex)getTypeLoader().parse(file, STUnsignedShortHex.type, xmlOptions);
        }
        
        public static STUnsignedShortHex parse(final URL url) throws XmlException, IOException {
            return (STUnsignedShortHex)getTypeLoader().parse(url, STUnsignedShortHex.type, (XmlOptions)null);
        }
        
        public static STUnsignedShortHex parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STUnsignedShortHex)getTypeLoader().parse(url, STUnsignedShortHex.type, xmlOptions);
        }
        
        public static STUnsignedShortHex parse(final InputStream inputStream) throws XmlException, IOException {
            return (STUnsignedShortHex)getTypeLoader().parse(inputStream, STUnsignedShortHex.type, (XmlOptions)null);
        }
        
        public static STUnsignedShortHex parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STUnsignedShortHex)getTypeLoader().parse(inputStream, STUnsignedShortHex.type, xmlOptions);
        }
        
        public static STUnsignedShortHex parse(final Reader reader) throws XmlException, IOException {
            return (STUnsignedShortHex)getTypeLoader().parse(reader, STUnsignedShortHex.type, (XmlOptions)null);
        }
        
        public static STUnsignedShortHex parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STUnsignedShortHex)getTypeLoader().parse(reader, STUnsignedShortHex.type, xmlOptions);
        }
        
        public static STUnsignedShortHex parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (STUnsignedShortHex)getTypeLoader().parse(xmlStreamReader, STUnsignedShortHex.type, (XmlOptions)null);
        }
        
        public static STUnsignedShortHex parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (STUnsignedShortHex)getTypeLoader().parse(xmlStreamReader, STUnsignedShortHex.type, xmlOptions);
        }
        
        public static STUnsignedShortHex parse(final Node node) throws XmlException {
            return (STUnsignedShortHex)getTypeLoader().parse(node, STUnsignedShortHex.type, (XmlOptions)null);
        }
        
        public static STUnsignedShortHex parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (STUnsignedShortHex)getTypeLoader().parse(node, STUnsignedShortHex.type, xmlOptions);
        }
        
        @Deprecated
        public static STUnsignedShortHex parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (STUnsignedShortHex)getTypeLoader().parse(xmlInputStream, STUnsignedShortHex.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static STUnsignedShortHex parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (STUnsignedShortHex)getTypeLoader().parse(xmlInputStream, STUnsignedShortHex.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STUnsignedShortHex.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STUnsignedShortHex.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
