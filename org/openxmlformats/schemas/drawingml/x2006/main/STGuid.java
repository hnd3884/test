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
import org.apache.xmlbeans.XmlToken;

public interface STGuid extends XmlToken
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(STGuid.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("stguidd0f4type");
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        public static STGuid newValue(final Object o) {
            return (STGuid)STGuid.type.newValue(o);
        }
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(STGuid.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static STGuid newInstance() {
            return (STGuid)getTypeLoader().newInstance(STGuid.type, (XmlOptions)null);
        }
        
        public static STGuid newInstance(final XmlOptions xmlOptions) {
            return (STGuid)getTypeLoader().newInstance(STGuid.type, xmlOptions);
        }
        
        public static STGuid parse(final String s) throws XmlException {
            return (STGuid)getTypeLoader().parse(s, STGuid.type, (XmlOptions)null);
        }
        
        public static STGuid parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (STGuid)getTypeLoader().parse(s, STGuid.type, xmlOptions);
        }
        
        public static STGuid parse(final File file) throws XmlException, IOException {
            return (STGuid)getTypeLoader().parse(file, STGuid.type, (XmlOptions)null);
        }
        
        public static STGuid parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STGuid)getTypeLoader().parse(file, STGuid.type, xmlOptions);
        }
        
        public static STGuid parse(final URL url) throws XmlException, IOException {
            return (STGuid)getTypeLoader().parse(url, STGuid.type, (XmlOptions)null);
        }
        
        public static STGuid parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STGuid)getTypeLoader().parse(url, STGuid.type, xmlOptions);
        }
        
        public static STGuid parse(final InputStream inputStream) throws XmlException, IOException {
            return (STGuid)getTypeLoader().parse(inputStream, STGuid.type, (XmlOptions)null);
        }
        
        public static STGuid parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STGuid)getTypeLoader().parse(inputStream, STGuid.type, xmlOptions);
        }
        
        public static STGuid parse(final Reader reader) throws XmlException, IOException {
            return (STGuid)getTypeLoader().parse(reader, STGuid.type, (XmlOptions)null);
        }
        
        public static STGuid parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STGuid)getTypeLoader().parse(reader, STGuid.type, xmlOptions);
        }
        
        public static STGuid parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (STGuid)getTypeLoader().parse(xmlStreamReader, STGuid.type, (XmlOptions)null);
        }
        
        public static STGuid parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (STGuid)getTypeLoader().parse(xmlStreamReader, STGuid.type, xmlOptions);
        }
        
        public static STGuid parse(final Node node) throws XmlException {
            return (STGuid)getTypeLoader().parse(node, STGuid.type, (XmlOptions)null);
        }
        
        public static STGuid parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (STGuid)getTypeLoader().parse(node, STGuid.type, xmlOptions);
        }
        
        @Deprecated
        public static STGuid parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (STGuid)getTypeLoader().parse(xmlInputStream, STGuid.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static STGuid parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (STGuid)getTypeLoader().parse(xmlInputStream, STGuid.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STGuid.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STGuid.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
