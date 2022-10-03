package org.w3.x2000.x09.xmldsig;

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
import org.apache.xmlbeans.XmlBase64Binary;

public interface DigestValueType extends XmlBase64Binary
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(DigestValueType.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.s8C3F193EE11A2F798ACF65489B9E6078").resolveHandle("digestvaluetype010atype");
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        public static DigestValueType newValue(final Object o) {
            return (DigestValueType)DigestValueType.type.newValue(o);
        }
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(DigestValueType.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static DigestValueType newInstance() {
            return (DigestValueType)getTypeLoader().newInstance(DigestValueType.type, (XmlOptions)null);
        }
        
        public static DigestValueType newInstance(final XmlOptions xmlOptions) {
            return (DigestValueType)getTypeLoader().newInstance(DigestValueType.type, xmlOptions);
        }
        
        public static DigestValueType parse(final String s) throws XmlException {
            return (DigestValueType)getTypeLoader().parse(s, DigestValueType.type, (XmlOptions)null);
        }
        
        public static DigestValueType parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (DigestValueType)getTypeLoader().parse(s, DigestValueType.type, xmlOptions);
        }
        
        public static DigestValueType parse(final File file) throws XmlException, IOException {
            return (DigestValueType)getTypeLoader().parse(file, DigestValueType.type, (XmlOptions)null);
        }
        
        public static DigestValueType parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (DigestValueType)getTypeLoader().parse(file, DigestValueType.type, xmlOptions);
        }
        
        public static DigestValueType parse(final URL url) throws XmlException, IOException {
            return (DigestValueType)getTypeLoader().parse(url, DigestValueType.type, (XmlOptions)null);
        }
        
        public static DigestValueType parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (DigestValueType)getTypeLoader().parse(url, DigestValueType.type, xmlOptions);
        }
        
        public static DigestValueType parse(final InputStream inputStream) throws XmlException, IOException {
            return (DigestValueType)getTypeLoader().parse(inputStream, DigestValueType.type, (XmlOptions)null);
        }
        
        public static DigestValueType parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (DigestValueType)getTypeLoader().parse(inputStream, DigestValueType.type, xmlOptions);
        }
        
        public static DigestValueType parse(final Reader reader) throws XmlException, IOException {
            return (DigestValueType)getTypeLoader().parse(reader, DigestValueType.type, (XmlOptions)null);
        }
        
        public static DigestValueType parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (DigestValueType)getTypeLoader().parse(reader, DigestValueType.type, xmlOptions);
        }
        
        public static DigestValueType parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (DigestValueType)getTypeLoader().parse(xmlStreamReader, DigestValueType.type, (XmlOptions)null);
        }
        
        public static DigestValueType parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (DigestValueType)getTypeLoader().parse(xmlStreamReader, DigestValueType.type, xmlOptions);
        }
        
        public static DigestValueType parse(final Node node) throws XmlException {
            return (DigestValueType)getTypeLoader().parse(node, DigestValueType.type, (XmlOptions)null);
        }
        
        public static DigestValueType parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (DigestValueType)getTypeLoader().parse(node, DigestValueType.type, xmlOptions);
        }
        
        @Deprecated
        public static DigestValueType parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (DigestValueType)getTypeLoader().parse(xmlInputStream, DigestValueType.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static DigestValueType parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (DigestValueType)getTypeLoader().parse(xmlInputStream, DigestValueType.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, DigestValueType.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, DigestValueType.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
