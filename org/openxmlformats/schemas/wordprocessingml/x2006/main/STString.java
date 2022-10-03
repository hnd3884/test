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
import org.apache.xmlbeans.XmlString;

public interface STString extends XmlString
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(STString.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ststringa627type");
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        public static STString newValue(final Object o) {
            return (STString)STString.type.newValue(o);
        }
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(STString.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static STString newInstance() {
            return (STString)getTypeLoader().newInstance(STString.type, (XmlOptions)null);
        }
        
        public static STString newInstance(final XmlOptions xmlOptions) {
            return (STString)getTypeLoader().newInstance(STString.type, xmlOptions);
        }
        
        public static STString parse(final String s) throws XmlException {
            return (STString)getTypeLoader().parse(s, STString.type, (XmlOptions)null);
        }
        
        public static STString parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (STString)getTypeLoader().parse(s, STString.type, xmlOptions);
        }
        
        public static STString parse(final File file) throws XmlException, IOException {
            return (STString)getTypeLoader().parse(file, STString.type, (XmlOptions)null);
        }
        
        public static STString parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STString)getTypeLoader().parse(file, STString.type, xmlOptions);
        }
        
        public static STString parse(final URL url) throws XmlException, IOException {
            return (STString)getTypeLoader().parse(url, STString.type, (XmlOptions)null);
        }
        
        public static STString parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STString)getTypeLoader().parse(url, STString.type, xmlOptions);
        }
        
        public static STString parse(final InputStream inputStream) throws XmlException, IOException {
            return (STString)getTypeLoader().parse(inputStream, STString.type, (XmlOptions)null);
        }
        
        public static STString parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STString)getTypeLoader().parse(inputStream, STString.type, xmlOptions);
        }
        
        public static STString parse(final Reader reader) throws XmlException, IOException {
            return (STString)getTypeLoader().parse(reader, STString.type, (XmlOptions)null);
        }
        
        public static STString parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STString)getTypeLoader().parse(reader, STString.type, xmlOptions);
        }
        
        public static STString parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (STString)getTypeLoader().parse(xmlStreamReader, STString.type, (XmlOptions)null);
        }
        
        public static STString parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (STString)getTypeLoader().parse(xmlStreamReader, STString.type, xmlOptions);
        }
        
        public static STString parse(final Node node) throws XmlException {
            return (STString)getTypeLoader().parse(node, STString.type, (XmlOptions)null);
        }
        
        public static STString parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (STString)getTypeLoader().parse(node, STString.type, xmlOptions);
        }
        
        @Deprecated
        public static STString parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (STString)getTypeLoader().parse(xmlInputStream, STString.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static STString parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (STString)getTypeLoader().parse(xmlInputStream, STString.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STString.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STString.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
