package org.openxmlformats.schemas.drawingml.x2006.chart;

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
import org.apache.xmlbeans.XmlDouble;

public interface STLogBase extends XmlDouble
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(STLogBase.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("stlogbase11a1type");
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        public static STLogBase newValue(final Object o) {
            return (STLogBase)STLogBase.type.newValue(o);
        }
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(STLogBase.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static STLogBase newInstance() {
            return (STLogBase)getTypeLoader().newInstance(STLogBase.type, (XmlOptions)null);
        }
        
        public static STLogBase newInstance(final XmlOptions xmlOptions) {
            return (STLogBase)getTypeLoader().newInstance(STLogBase.type, xmlOptions);
        }
        
        public static STLogBase parse(final String s) throws XmlException {
            return (STLogBase)getTypeLoader().parse(s, STLogBase.type, (XmlOptions)null);
        }
        
        public static STLogBase parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (STLogBase)getTypeLoader().parse(s, STLogBase.type, xmlOptions);
        }
        
        public static STLogBase parse(final File file) throws XmlException, IOException {
            return (STLogBase)getTypeLoader().parse(file, STLogBase.type, (XmlOptions)null);
        }
        
        public static STLogBase parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STLogBase)getTypeLoader().parse(file, STLogBase.type, xmlOptions);
        }
        
        public static STLogBase parse(final URL url) throws XmlException, IOException {
            return (STLogBase)getTypeLoader().parse(url, STLogBase.type, (XmlOptions)null);
        }
        
        public static STLogBase parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STLogBase)getTypeLoader().parse(url, STLogBase.type, xmlOptions);
        }
        
        public static STLogBase parse(final InputStream inputStream) throws XmlException, IOException {
            return (STLogBase)getTypeLoader().parse(inputStream, STLogBase.type, (XmlOptions)null);
        }
        
        public static STLogBase parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STLogBase)getTypeLoader().parse(inputStream, STLogBase.type, xmlOptions);
        }
        
        public static STLogBase parse(final Reader reader) throws XmlException, IOException {
            return (STLogBase)getTypeLoader().parse(reader, STLogBase.type, (XmlOptions)null);
        }
        
        public static STLogBase parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STLogBase)getTypeLoader().parse(reader, STLogBase.type, xmlOptions);
        }
        
        public static STLogBase parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (STLogBase)getTypeLoader().parse(xmlStreamReader, STLogBase.type, (XmlOptions)null);
        }
        
        public static STLogBase parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (STLogBase)getTypeLoader().parse(xmlStreamReader, STLogBase.type, xmlOptions);
        }
        
        public static STLogBase parse(final Node node) throws XmlException {
            return (STLogBase)getTypeLoader().parse(node, STLogBase.type, (XmlOptions)null);
        }
        
        public static STLogBase parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (STLogBase)getTypeLoader().parse(node, STLogBase.type, xmlOptions);
        }
        
        @Deprecated
        public static STLogBase parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (STLogBase)getTypeLoader().parse(xmlInputStream, STLogBase.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static STLogBase parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (STLogBase)getTypeLoader().parse(xmlInputStream, STLogBase.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STLogBase.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STLogBase.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
