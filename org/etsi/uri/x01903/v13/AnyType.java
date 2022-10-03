package org.etsi.uri.x01903.v13;

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
import org.apache.xmlbeans.XmlObject;

public interface AnyType extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(AnyType.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.s8C3F193EE11A2F798ACF65489B9E6078").resolveHandle("anytype96c8type");
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(AnyType.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static AnyType newInstance() {
            return (AnyType)getTypeLoader().newInstance(AnyType.type, (XmlOptions)null);
        }
        
        public static AnyType newInstance(final XmlOptions xmlOptions) {
            return (AnyType)getTypeLoader().newInstance(AnyType.type, xmlOptions);
        }
        
        public static AnyType parse(final String s) throws XmlException {
            return (AnyType)getTypeLoader().parse(s, AnyType.type, (XmlOptions)null);
        }
        
        public static AnyType parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (AnyType)getTypeLoader().parse(s, AnyType.type, xmlOptions);
        }
        
        public static AnyType parse(final File file) throws XmlException, IOException {
            return (AnyType)getTypeLoader().parse(file, AnyType.type, (XmlOptions)null);
        }
        
        public static AnyType parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (AnyType)getTypeLoader().parse(file, AnyType.type, xmlOptions);
        }
        
        public static AnyType parse(final URL url) throws XmlException, IOException {
            return (AnyType)getTypeLoader().parse(url, AnyType.type, (XmlOptions)null);
        }
        
        public static AnyType parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (AnyType)getTypeLoader().parse(url, AnyType.type, xmlOptions);
        }
        
        public static AnyType parse(final InputStream inputStream) throws XmlException, IOException {
            return (AnyType)getTypeLoader().parse(inputStream, AnyType.type, (XmlOptions)null);
        }
        
        public static AnyType parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (AnyType)getTypeLoader().parse(inputStream, AnyType.type, xmlOptions);
        }
        
        public static AnyType parse(final Reader reader) throws XmlException, IOException {
            return (AnyType)getTypeLoader().parse(reader, AnyType.type, (XmlOptions)null);
        }
        
        public static AnyType parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (AnyType)getTypeLoader().parse(reader, AnyType.type, xmlOptions);
        }
        
        public static AnyType parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (AnyType)getTypeLoader().parse(xmlStreamReader, AnyType.type, (XmlOptions)null);
        }
        
        public static AnyType parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (AnyType)getTypeLoader().parse(xmlStreamReader, AnyType.type, xmlOptions);
        }
        
        public static AnyType parse(final Node node) throws XmlException {
            return (AnyType)getTypeLoader().parse(node, AnyType.type, (XmlOptions)null);
        }
        
        public static AnyType parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (AnyType)getTypeLoader().parse(node, AnyType.type, xmlOptions);
        }
        
        @Deprecated
        public static AnyType parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (AnyType)getTypeLoader().parse(xmlInputStream, AnyType.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static AnyType parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (AnyType)getTypeLoader().parse(xmlInputStream, AnyType.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, AnyType.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, AnyType.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
