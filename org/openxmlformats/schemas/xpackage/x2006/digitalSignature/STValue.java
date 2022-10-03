package org.openxmlformats.schemas.xpackage.x2006.digitalSignature;

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

public interface STValue extends XmlString
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(STValue.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.s8C3F193EE11A2F798ACF65489B9E6078").resolveHandle("stvalueb6e1type");
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        public static STValue newValue(final Object o) {
            return (STValue)STValue.type.newValue(o);
        }
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(STValue.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static STValue newInstance() {
            return (STValue)getTypeLoader().newInstance(STValue.type, (XmlOptions)null);
        }
        
        public static STValue newInstance(final XmlOptions xmlOptions) {
            return (STValue)getTypeLoader().newInstance(STValue.type, xmlOptions);
        }
        
        public static STValue parse(final String s) throws XmlException {
            return (STValue)getTypeLoader().parse(s, STValue.type, (XmlOptions)null);
        }
        
        public static STValue parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (STValue)getTypeLoader().parse(s, STValue.type, xmlOptions);
        }
        
        public static STValue parse(final File file) throws XmlException, IOException {
            return (STValue)getTypeLoader().parse(file, STValue.type, (XmlOptions)null);
        }
        
        public static STValue parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STValue)getTypeLoader().parse(file, STValue.type, xmlOptions);
        }
        
        public static STValue parse(final URL url) throws XmlException, IOException {
            return (STValue)getTypeLoader().parse(url, STValue.type, (XmlOptions)null);
        }
        
        public static STValue parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STValue)getTypeLoader().parse(url, STValue.type, xmlOptions);
        }
        
        public static STValue parse(final InputStream inputStream) throws XmlException, IOException {
            return (STValue)getTypeLoader().parse(inputStream, STValue.type, (XmlOptions)null);
        }
        
        public static STValue parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STValue)getTypeLoader().parse(inputStream, STValue.type, xmlOptions);
        }
        
        public static STValue parse(final Reader reader) throws XmlException, IOException {
            return (STValue)getTypeLoader().parse(reader, STValue.type, (XmlOptions)null);
        }
        
        public static STValue parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STValue)getTypeLoader().parse(reader, STValue.type, xmlOptions);
        }
        
        public static STValue parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (STValue)getTypeLoader().parse(xmlStreamReader, STValue.type, (XmlOptions)null);
        }
        
        public static STValue parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (STValue)getTypeLoader().parse(xmlStreamReader, STValue.type, xmlOptions);
        }
        
        public static STValue parse(final Node node) throws XmlException {
            return (STValue)getTypeLoader().parse(node, STValue.type, (XmlOptions)null);
        }
        
        public static STValue parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (STValue)getTypeLoader().parse(node, STValue.type, xmlOptions);
        }
        
        @Deprecated
        public static STValue parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (STValue)getTypeLoader().parse(xmlInputStream, STValue.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static STValue parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (STValue)getTypeLoader().parse(xmlInputStream, STValue.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STValue.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STValue.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
