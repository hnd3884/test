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

public interface STPercentage extends XmlInt
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(STPercentage.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("stpercentage0a85type");
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        public static STPercentage newValue(final Object o) {
            return (STPercentage)STPercentage.type.newValue(o);
        }
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(STPercentage.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static STPercentage newInstance() {
            return (STPercentage)getTypeLoader().newInstance(STPercentage.type, (XmlOptions)null);
        }
        
        public static STPercentage newInstance(final XmlOptions xmlOptions) {
            return (STPercentage)getTypeLoader().newInstance(STPercentage.type, xmlOptions);
        }
        
        public static STPercentage parse(final String s) throws XmlException {
            return (STPercentage)getTypeLoader().parse(s, STPercentage.type, (XmlOptions)null);
        }
        
        public static STPercentage parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (STPercentage)getTypeLoader().parse(s, STPercentage.type, xmlOptions);
        }
        
        public static STPercentage parse(final File file) throws XmlException, IOException {
            return (STPercentage)getTypeLoader().parse(file, STPercentage.type, (XmlOptions)null);
        }
        
        public static STPercentage parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STPercentage)getTypeLoader().parse(file, STPercentage.type, xmlOptions);
        }
        
        public static STPercentage parse(final URL url) throws XmlException, IOException {
            return (STPercentage)getTypeLoader().parse(url, STPercentage.type, (XmlOptions)null);
        }
        
        public static STPercentage parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STPercentage)getTypeLoader().parse(url, STPercentage.type, xmlOptions);
        }
        
        public static STPercentage parse(final InputStream inputStream) throws XmlException, IOException {
            return (STPercentage)getTypeLoader().parse(inputStream, STPercentage.type, (XmlOptions)null);
        }
        
        public static STPercentage parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STPercentage)getTypeLoader().parse(inputStream, STPercentage.type, xmlOptions);
        }
        
        public static STPercentage parse(final Reader reader) throws XmlException, IOException {
            return (STPercentage)getTypeLoader().parse(reader, STPercentage.type, (XmlOptions)null);
        }
        
        public static STPercentage parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STPercentage)getTypeLoader().parse(reader, STPercentage.type, xmlOptions);
        }
        
        public static STPercentage parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (STPercentage)getTypeLoader().parse(xmlStreamReader, STPercentage.type, (XmlOptions)null);
        }
        
        public static STPercentage parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (STPercentage)getTypeLoader().parse(xmlStreamReader, STPercentage.type, xmlOptions);
        }
        
        public static STPercentage parse(final Node node) throws XmlException {
            return (STPercentage)getTypeLoader().parse(node, STPercentage.type, (XmlOptions)null);
        }
        
        public static STPercentage parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (STPercentage)getTypeLoader().parse(node, STPercentage.type, xmlOptions);
        }
        
        @Deprecated
        public static STPercentage parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (STPercentage)getTypeLoader().parse(xmlInputStream, STPercentage.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static STPercentage parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (STPercentage)getTypeLoader().parse(xmlInputStream, STPercentage.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STPercentage.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STPercentage.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
