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
import org.apache.xmlbeans.XmlString;

public interface STRef extends XmlString
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(STRef.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("stref90a2type");
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        public static STRef newValue(final Object o) {
            return (STRef)STRef.type.newValue(o);
        }
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(STRef.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static STRef newInstance() {
            return (STRef)getTypeLoader().newInstance(STRef.type, (XmlOptions)null);
        }
        
        public static STRef newInstance(final XmlOptions xmlOptions) {
            return (STRef)getTypeLoader().newInstance(STRef.type, xmlOptions);
        }
        
        public static STRef parse(final String s) throws XmlException {
            return (STRef)getTypeLoader().parse(s, STRef.type, (XmlOptions)null);
        }
        
        public static STRef parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (STRef)getTypeLoader().parse(s, STRef.type, xmlOptions);
        }
        
        public static STRef parse(final File file) throws XmlException, IOException {
            return (STRef)getTypeLoader().parse(file, STRef.type, (XmlOptions)null);
        }
        
        public static STRef parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STRef)getTypeLoader().parse(file, STRef.type, xmlOptions);
        }
        
        public static STRef parse(final URL url) throws XmlException, IOException {
            return (STRef)getTypeLoader().parse(url, STRef.type, (XmlOptions)null);
        }
        
        public static STRef parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STRef)getTypeLoader().parse(url, STRef.type, xmlOptions);
        }
        
        public static STRef parse(final InputStream inputStream) throws XmlException, IOException {
            return (STRef)getTypeLoader().parse(inputStream, STRef.type, (XmlOptions)null);
        }
        
        public static STRef parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STRef)getTypeLoader().parse(inputStream, STRef.type, xmlOptions);
        }
        
        public static STRef parse(final Reader reader) throws XmlException, IOException {
            return (STRef)getTypeLoader().parse(reader, STRef.type, (XmlOptions)null);
        }
        
        public static STRef parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STRef)getTypeLoader().parse(reader, STRef.type, xmlOptions);
        }
        
        public static STRef parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (STRef)getTypeLoader().parse(xmlStreamReader, STRef.type, (XmlOptions)null);
        }
        
        public static STRef parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (STRef)getTypeLoader().parse(xmlStreamReader, STRef.type, xmlOptions);
        }
        
        public static STRef parse(final Node node) throws XmlException {
            return (STRef)getTypeLoader().parse(node, STRef.type, (XmlOptions)null);
        }
        
        public static STRef parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (STRef)getTypeLoader().parse(node, STRef.type, xmlOptions);
        }
        
        @Deprecated
        public static STRef parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (STRef)getTypeLoader().parse(xmlInputStream, STRef.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static STRef parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (STRef)getTypeLoader().parse(xmlInputStream, STRef.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STRef.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STRef.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
