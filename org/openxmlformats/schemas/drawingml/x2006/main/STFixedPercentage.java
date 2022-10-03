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

public interface STFixedPercentage extends STPercentage
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(STFixedPercentage.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("stfixedpercentagef0cftype");
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        public static STFixedPercentage newValue(final Object o) {
            return (STFixedPercentage)STFixedPercentage.type.newValue(o);
        }
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(STFixedPercentage.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static STFixedPercentage newInstance() {
            return (STFixedPercentage)getTypeLoader().newInstance(STFixedPercentage.type, (XmlOptions)null);
        }
        
        public static STFixedPercentage newInstance(final XmlOptions xmlOptions) {
            return (STFixedPercentage)getTypeLoader().newInstance(STFixedPercentage.type, xmlOptions);
        }
        
        public static STFixedPercentage parse(final String s) throws XmlException {
            return (STFixedPercentage)getTypeLoader().parse(s, STFixedPercentage.type, (XmlOptions)null);
        }
        
        public static STFixedPercentage parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (STFixedPercentage)getTypeLoader().parse(s, STFixedPercentage.type, xmlOptions);
        }
        
        public static STFixedPercentage parse(final File file) throws XmlException, IOException {
            return (STFixedPercentage)getTypeLoader().parse(file, STFixedPercentage.type, (XmlOptions)null);
        }
        
        public static STFixedPercentage parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STFixedPercentage)getTypeLoader().parse(file, STFixedPercentage.type, xmlOptions);
        }
        
        public static STFixedPercentage parse(final URL url) throws XmlException, IOException {
            return (STFixedPercentage)getTypeLoader().parse(url, STFixedPercentage.type, (XmlOptions)null);
        }
        
        public static STFixedPercentage parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STFixedPercentage)getTypeLoader().parse(url, STFixedPercentage.type, xmlOptions);
        }
        
        public static STFixedPercentage parse(final InputStream inputStream) throws XmlException, IOException {
            return (STFixedPercentage)getTypeLoader().parse(inputStream, STFixedPercentage.type, (XmlOptions)null);
        }
        
        public static STFixedPercentage parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STFixedPercentage)getTypeLoader().parse(inputStream, STFixedPercentage.type, xmlOptions);
        }
        
        public static STFixedPercentage parse(final Reader reader) throws XmlException, IOException {
            return (STFixedPercentage)getTypeLoader().parse(reader, STFixedPercentage.type, (XmlOptions)null);
        }
        
        public static STFixedPercentage parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STFixedPercentage)getTypeLoader().parse(reader, STFixedPercentage.type, xmlOptions);
        }
        
        public static STFixedPercentage parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (STFixedPercentage)getTypeLoader().parse(xmlStreamReader, STFixedPercentage.type, (XmlOptions)null);
        }
        
        public static STFixedPercentage parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (STFixedPercentage)getTypeLoader().parse(xmlStreamReader, STFixedPercentage.type, xmlOptions);
        }
        
        public static STFixedPercentage parse(final Node node) throws XmlException {
            return (STFixedPercentage)getTypeLoader().parse(node, STFixedPercentage.type, (XmlOptions)null);
        }
        
        public static STFixedPercentage parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (STFixedPercentage)getTypeLoader().parse(node, STFixedPercentage.type, xmlOptions);
        }
        
        @Deprecated
        public static STFixedPercentage parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (STFixedPercentage)getTypeLoader().parse(xmlInputStream, STFixedPercentage.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static STFixedPercentage parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (STFixedPercentage)getTypeLoader().parse(xmlInputStream, STFixedPercentage.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STFixedPercentage.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STFixedPercentage.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
