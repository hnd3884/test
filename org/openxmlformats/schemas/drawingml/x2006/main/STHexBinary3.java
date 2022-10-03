package org.openxmlformats.schemas.drawingml.x2006.main;

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

public interface STHexBinary3 extends XmlHexBinary
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(STHexBinary3.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("sthexbinary314e2type");
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        public static STHexBinary3 newValue(final Object o) {
            return (STHexBinary3)STHexBinary3.type.newValue(o);
        }
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(STHexBinary3.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static STHexBinary3 newInstance() {
            return (STHexBinary3)getTypeLoader().newInstance(STHexBinary3.type, (XmlOptions)null);
        }
        
        public static STHexBinary3 newInstance(final XmlOptions xmlOptions) {
            return (STHexBinary3)getTypeLoader().newInstance(STHexBinary3.type, xmlOptions);
        }
        
        public static STHexBinary3 parse(final String s) throws XmlException {
            return (STHexBinary3)getTypeLoader().parse(s, STHexBinary3.type, (XmlOptions)null);
        }
        
        public static STHexBinary3 parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (STHexBinary3)getTypeLoader().parse(s, STHexBinary3.type, xmlOptions);
        }
        
        public static STHexBinary3 parse(final File file) throws XmlException, IOException {
            return (STHexBinary3)getTypeLoader().parse(file, STHexBinary3.type, (XmlOptions)null);
        }
        
        public static STHexBinary3 parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STHexBinary3)getTypeLoader().parse(file, STHexBinary3.type, xmlOptions);
        }
        
        public static STHexBinary3 parse(final URL url) throws XmlException, IOException {
            return (STHexBinary3)getTypeLoader().parse(url, STHexBinary3.type, (XmlOptions)null);
        }
        
        public static STHexBinary3 parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STHexBinary3)getTypeLoader().parse(url, STHexBinary3.type, xmlOptions);
        }
        
        public static STHexBinary3 parse(final InputStream inputStream) throws XmlException, IOException {
            return (STHexBinary3)getTypeLoader().parse(inputStream, STHexBinary3.type, (XmlOptions)null);
        }
        
        public static STHexBinary3 parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STHexBinary3)getTypeLoader().parse(inputStream, STHexBinary3.type, xmlOptions);
        }
        
        public static STHexBinary3 parse(final Reader reader) throws XmlException, IOException {
            return (STHexBinary3)getTypeLoader().parse(reader, STHexBinary3.type, (XmlOptions)null);
        }
        
        public static STHexBinary3 parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STHexBinary3)getTypeLoader().parse(reader, STHexBinary3.type, xmlOptions);
        }
        
        public static STHexBinary3 parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (STHexBinary3)getTypeLoader().parse(xmlStreamReader, STHexBinary3.type, (XmlOptions)null);
        }
        
        public static STHexBinary3 parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (STHexBinary3)getTypeLoader().parse(xmlStreamReader, STHexBinary3.type, xmlOptions);
        }
        
        public static STHexBinary3 parse(final Node node) throws XmlException {
            return (STHexBinary3)getTypeLoader().parse(node, STHexBinary3.type, (XmlOptions)null);
        }
        
        public static STHexBinary3 parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (STHexBinary3)getTypeLoader().parse(node, STHexBinary3.type, xmlOptions);
        }
        
        @Deprecated
        public static STHexBinary3 parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (STHexBinary3)getTypeLoader().parse(xmlInputStream, STHexBinary3.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static STHexBinary3 parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (STHexBinary3)getTypeLoader().parse(xmlInputStream, STHexBinary3.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STHexBinary3.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STHexBinary3.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
