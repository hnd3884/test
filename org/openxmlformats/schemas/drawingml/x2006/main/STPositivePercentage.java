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

public interface STPositivePercentage extends STPercentage
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(STPositivePercentage.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("stpositivepercentagedb9etype");
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        public static STPositivePercentage newValue(final Object o) {
            return (STPositivePercentage)STPositivePercentage.type.newValue(o);
        }
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(STPositivePercentage.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static STPositivePercentage newInstance() {
            return (STPositivePercentage)getTypeLoader().newInstance(STPositivePercentage.type, (XmlOptions)null);
        }
        
        public static STPositivePercentage newInstance(final XmlOptions xmlOptions) {
            return (STPositivePercentage)getTypeLoader().newInstance(STPositivePercentage.type, xmlOptions);
        }
        
        public static STPositivePercentage parse(final String s) throws XmlException {
            return (STPositivePercentage)getTypeLoader().parse(s, STPositivePercentage.type, (XmlOptions)null);
        }
        
        public static STPositivePercentage parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (STPositivePercentage)getTypeLoader().parse(s, STPositivePercentage.type, xmlOptions);
        }
        
        public static STPositivePercentage parse(final File file) throws XmlException, IOException {
            return (STPositivePercentage)getTypeLoader().parse(file, STPositivePercentage.type, (XmlOptions)null);
        }
        
        public static STPositivePercentage parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STPositivePercentage)getTypeLoader().parse(file, STPositivePercentage.type, xmlOptions);
        }
        
        public static STPositivePercentage parse(final URL url) throws XmlException, IOException {
            return (STPositivePercentage)getTypeLoader().parse(url, STPositivePercentage.type, (XmlOptions)null);
        }
        
        public static STPositivePercentage parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STPositivePercentage)getTypeLoader().parse(url, STPositivePercentage.type, xmlOptions);
        }
        
        public static STPositivePercentage parse(final InputStream inputStream) throws XmlException, IOException {
            return (STPositivePercentage)getTypeLoader().parse(inputStream, STPositivePercentage.type, (XmlOptions)null);
        }
        
        public static STPositivePercentage parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STPositivePercentage)getTypeLoader().parse(inputStream, STPositivePercentage.type, xmlOptions);
        }
        
        public static STPositivePercentage parse(final Reader reader) throws XmlException, IOException {
            return (STPositivePercentage)getTypeLoader().parse(reader, STPositivePercentage.type, (XmlOptions)null);
        }
        
        public static STPositivePercentage parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STPositivePercentage)getTypeLoader().parse(reader, STPositivePercentage.type, xmlOptions);
        }
        
        public static STPositivePercentage parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (STPositivePercentage)getTypeLoader().parse(xmlStreamReader, STPositivePercentage.type, (XmlOptions)null);
        }
        
        public static STPositivePercentage parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (STPositivePercentage)getTypeLoader().parse(xmlStreamReader, STPositivePercentage.type, xmlOptions);
        }
        
        public static STPositivePercentage parse(final Node node) throws XmlException {
            return (STPositivePercentage)getTypeLoader().parse(node, STPositivePercentage.type, (XmlOptions)null);
        }
        
        public static STPositivePercentage parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (STPositivePercentage)getTypeLoader().parse(node, STPositivePercentage.type, xmlOptions);
        }
        
        @Deprecated
        public static STPositivePercentage parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (STPositivePercentage)getTypeLoader().parse(xmlInputStream, STPositivePercentage.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static STPositivePercentage parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (STPositivePercentage)getTypeLoader().parse(xmlInputStream, STPositivePercentage.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STPositivePercentage.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STPositivePercentage.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
