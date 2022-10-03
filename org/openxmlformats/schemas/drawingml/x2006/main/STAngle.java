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
import org.apache.xmlbeans.XmlInt;

public interface STAngle extends XmlInt
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(STAngle.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("stangle8074type");
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        public static STAngle newValue(final Object o) {
            return (STAngle)STAngle.type.newValue(o);
        }
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(STAngle.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static STAngle newInstance() {
            return (STAngle)getTypeLoader().newInstance(STAngle.type, (XmlOptions)null);
        }
        
        public static STAngle newInstance(final XmlOptions xmlOptions) {
            return (STAngle)getTypeLoader().newInstance(STAngle.type, xmlOptions);
        }
        
        public static STAngle parse(final String s) throws XmlException {
            return (STAngle)getTypeLoader().parse(s, STAngle.type, (XmlOptions)null);
        }
        
        public static STAngle parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (STAngle)getTypeLoader().parse(s, STAngle.type, xmlOptions);
        }
        
        public static STAngle parse(final File file) throws XmlException, IOException {
            return (STAngle)getTypeLoader().parse(file, STAngle.type, (XmlOptions)null);
        }
        
        public static STAngle parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STAngle)getTypeLoader().parse(file, STAngle.type, xmlOptions);
        }
        
        public static STAngle parse(final URL url) throws XmlException, IOException {
            return (STAngle)getTypeLoader().parse(url, STAngle.type, (XmlOptions)null);
        }
        
        public static STAngle parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STAngle)getTypeLoader().parse(url, STAngle.type, xmlOptions);
        }
        
        public static STAngle parse(final InputStream inputStream) throws XmlException, IOException {
            return (STAngle)getTypeLoader().parse(inputStream, STAngle.type, (XmlOptions)null);
        }
        
        public static STAngle parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STAngle)getTypeLoader().parse(inputStream, STAngle.type, xmlOptions);
        }
        
        public static STAngle parse(final Reader reader) throws XmlException, IOException {
            return (STAngle)getTypeLoader().parse(reader, STAngle.type, (XmlOptions)null);
        }
        
        public static STAngle parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STAngle)getTypeLoader().parse(reader, STAngle.type, xmlOptions);
        }
        
        public static STAngle parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (STAngle)getTypeLoader().parse(xmlStreamReader, STAngle.type, (XmlOptions)null);
        }
        
        public static STAngle parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (STAngle)getTypeLoader().parse(xmlStreamReader, STAngle.type, xmlOptions);
        }
        
        public static STAngle parse(final Node node) throws XmlException {
            return (STAngle)getTypeLoader().parse(node, STAngle.type, (XmlOptions)null);
        }
        
        public static STAngle parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (STAngle)getTypeLoader().parse(node, STAngle.type, xmlOptions);
        }
        
        @Deprecated
        public static STAngle parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (STAngle)getTypeLoader().parse(xmlInputStream, STAngle.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static STAngle parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (STAngle)getTypeLoader().parse(xmlInputStream, STAngle.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STAngle.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STAngle.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
