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

public interface STPositiveFixedPercentage extends STPercentage
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(STPositiveFixedPercentage.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("stpositivefixedpercentagee756type");
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        public static STPositiveFixedPercentage newValue(final Object o) {
            return (STPositiveFixedPercentage)STPositiveFixedPercentage.type.newValue(o);
        }
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(STPositiveFixedPercentage.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static STPositiveFixedPercentage newInstance() {
            return (STPositiveFixedPercentage)getTypeLoader().newInstance(STPositiveFixedPercentage.type, (XmlOptions)null);
        }
        
        public static STPositiveFixedPercentage newInstance(final XmlOptions xmlOptions) {
            return (STPositiveFixedPercentage)getTypeLoader().newInstance(STPositiveFixedPercentage.type, xmlOptions);
        }
        
        public static STPositiveFixedPercentage parse(final String s) throws XmlException {
            return (STPositiveFixedPercentage)getTypeLoader().parse(s, STPositiveFixedPercentage.type, (XmlOptions)null);
        }
        
        public static STPositiveFixedPercentage parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (STPositiveFixedPercentage)getTypeLoader().parse(s, STPositiveFixedPercentage.type, xmlOptions);
        }
        
        public static STPositiveFixedPercentage parse(final File file) throws XmlException, IOException {
            return (STPositiveFixedPercentage)getTypeLoader().parse(file, STPositiveFixedPercentage.type, (XmlOptions)null);
        }
        
        public static STPositiveFixedPercentage parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STPositiveFixedPercentage)getTypeLoader().parse(file, STPositiveFixedPercentage.type, xmlOptions);
        }
        
        public static STPositiveFixedPercentage parse(final URL url) throws XmlException, IOException {
            return (STPositiveFixedPercentage)getTypeLoader().parse(url, STPositiveFixedPercentage.type, (XmlOptions)null);
        }
        
        public static STPositiveFixedPercentage parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STPositiveFixedPercentage)getTypeLoader().parse(url, STPositiveFixedPercentage.type, xmlOptions);
        }
        
        public static STPositiveFixedPercentage parse(final InputStream inputStream) throws XmlException, IOException {
            return (STPositiveFixedPercentage)getTypeLoader().parse(inputStream, STPositiveFixedPercentage.type, (XmlOptions)null);
        }
        
        public static STPositiveFixedPercentage parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STPositiveFixedPercentage)getTypeLoader().parse(inputStream, STPositiveFixedPercentage.type, xmlOptions);
        }
        
        public static STPositiveFixedPercentage parse(final Reader reader) throws XmlException, IOException {
            return (STPositiveFixedPercentage)getTypeLoader().parse(reader, STPositiveFixedPercentage.type, (XmlOptions)null);
        }
        
        public static STPositiveFixedPercentage parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STPositiveFixedPercentage)getTypeLoader().parse(reader, STPositiveFixedPercentage.type, xmlOptions);
        }
        
        public static STPositiveFixedPercentage parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (STPositiveFixedPercentage)getTypeLoader().parse(xmlStreamReader, STPositiveFixedPercentage.type, (XmlOptions)null);
        }
        
        public static STPositiveFixedPercentage parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (STPositiveFixedPercentage)getTypeLoader().parse(xmlStreamReader, STPositiveFixedPercentage.type, xmlOptions);
        }
        
        public static STPositiveFixedPercentage parse(final Node node) throws XmlException {
            return (STPositiveFixedPercentage)getTypeLoader().parse(node, STPositiveFixedPercentage.type, (XmlOptions)null);
        }
        
        public static STPositiveFixedPercentage parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (STPositiveFixedPercentage)getTypeLoader().parse(node, STPositiveFixedPercentage.type, xmlOptions);
        }
        
        @Deprecated
        public static STPositiveFixedPercentage parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (STPositiveFixedPercentage)getTypeLoader().parse(xmlInputStream, STPositiveFixedPercentage.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static STPositiveFixedPercentage parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (STPositiveFixedPercentage)getTypeLoader().parse(xmlInputStream, STPositiveFixedPercentage.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STPositiveFixedPercentage.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STPositiveFixedPercentage.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
