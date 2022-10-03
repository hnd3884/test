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
import org.apache.xmlbeans.XmlString;

public interface STXstring extends XmlString
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(STXstring.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("stxstringb8cdtype");
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        public static STXstring newValue(final Object o) {
            return (STXstring)STXstring.type.newValue(o);
        }
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(STXstring.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static STXstring newInstance() {
            return (STXstring)getTypeLoader().newInstance(STXstring.type, (XmlOptions)null);
        }
        
        public static STXstring newInstance(final XmlOptions xmlOptions) {
            return (STXstring)getTypeLoader().newInstance(STXstring.type, xmlOptions);
        }
        
        public static STXstring parse(final String s) throws XmlException {
            return (STXstring)getTypeLoader().parse(s, STXstring.type, (XmlOptions)null);
        }
        
        public static STXstring parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (STXstring)getTypeLoader().parse(s, STXstring.type, xmlOptions);
        }
        
        public static STXstring parse(final File file) throws XmlException, IOException {
            return (STXstring)getTypeLoader().parse(file, STXstring.type, (XmlOptions)null);
        }
        
        public static STXstring parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STXstring)getTypeLoader().parse(file, STXstring.type, xmlOptions);
        }
        
        public static STXstring parse(final URL url) throws XmlException, IOException {
            return (STXstring)getTypeLoader().parse(url, STXstring.type, (XmlOptions)null);
        }
        
        public static STXstring parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STXstring)getTypeLoader().parse(url, STXstring.type, xmlOptions);
        }
        
        public static STXstring parse(final InputStream inputStream) throws XmlException, IOException {
            return (STXstring)getTypeLoader().parse(inputStream, STXstring.type, (XmlOptions)null);
        }
        
        public static STXstring parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STXstring)getTypeLoader().parse(inputStream, STXstring.type, xmlOptions);
        }
        
        public static STXstring parse(final Reader reader) throws XmlException, IOException {
            return (STXstring)getTypeLoader().parse(reader, STXstring.type, (XmlOptions)null);
        }
        
        public static STXstring parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STXstring)getTypeLoader().parse(reader, STXstring.type, xmlOptions);
        }
        
        public static STXstring parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (STXstring)getTypeLoader().parse(xmlStreamReader, STXstring.type, (XmlOptions)null);
        }
        
        public static STXstring parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (STXstring)getTypeLoader().parse(xmlStreamReader, STXstring.type, xmlOptions);
        }
        
        public static STXstring parse(final Node node) throws XmlException {
            return (STXstring)getTypeLoader().parse(node, STXstring.type, (XmlOptions)null);
        }
        
        public static STXstring parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (STXstring)getTypeLoader().parse(node, STXstring.type, xmlOptions);
        }
        
        @Deprecated
        public static STXstring parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (STXstring)getTypeLoader().parse(xmlInputStream, STXstring.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static STXstring parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (STXstring)getTypeLoader().parse(xmlInputStream, STXstring.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STXstring.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STXstring.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
