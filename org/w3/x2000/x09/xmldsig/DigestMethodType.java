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
import org.apache.xmlbeans.XmlAnyURI;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface DigestMethodType extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(DigestMethodType.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.s8C3F193EE11A2F798ACF65489B9E6078").resolveHandle("digestmethodtype5ce0type");
    
    String getAlgorithm();
    
    XmlAnyURI xgetAlgorithm();
    
    void setAlgorithm(final String p0);
    
    void xsetAlgorithm(final XmlAnyURI p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(DigestMethodType.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static DigestMethodType newInstance() {
            return (DigestMethodType)getTypeLoader().newInstance(DigestMethodType.type, (XmlOptions)null);
        }
        
        public static DigestMethodType newInstance(final XmlOptions xmlOptions) {
            return (DigestMethodType)getTypeLoader().newInstance(DigestMethodType.type, xmlOptions);
        }
        
        public static DigestMethodType parse(final String s) throws XmlException {
            return (DigestMethodType)getTypeLoader().parse(s, DigestMethodType.type, (XmlOptions)null);
        }
        
        public static DigestMethodType parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (DigestMethodType)getTypeLoader().parse(s, DigestMethodType.type, xmlOptions);
        }
        
        public static DigestMethodType parse(final File file) throws XmlException, IOException {
            return (DigestMethodType)getTypeLoader().parse(file, DigestMethodType.type, (XmlOptions)null);
        }
        
        public static DigestMethodType parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (DigestMethodType)getTypeLoader().parse(file, DigestMethodType.type, xmlOptions);
        }
        
        public static DigestMethodType parse(final URL url) throws XmlException, IOException {
            return (DigestMethodType)getTypeLoader().parse(url, DigestMethodType.type, (XmlOptions)null);
        }
        
        public static DigestMethodType parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (DigestMethodType)getTypeLoader().parse(url, DigestMethodType.type, xmlOptions);
        }
        
        public static DigestMethodType parse(final InputStream inputStream) throws XmlException, IOException {
            return (DigestMethodType)getTypeLoader().parse(inputStream, DigestMethodType.type, (XmlOptions)null);
        }
        
        public static DigestMethodType parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (DigestMethodType)getTypeLoader().parse(inputStream, DigestMethodType.type, xmlOptions);
        }
        
        public static DigestMethodType parse(final Reader reader) throws XmlException, IOException {
            return (DigestMethodType)getTypeLoader().parse(reader, DigestMethodType.type, (XmlOptions)null);
        }
        
        public static DigestMethodType parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (DigestMethodType)getTypeLoader().parse(reader, DigestMethodType.type, xmlOptions);
        }
        
        public static DigestMethodType parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (DigestMethodType)getTypeLoader().parse(xmlStreamReader, DigestMethodType.type, (XmlOptions)null);
        }
        
        public static DigestMethodType parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (DigestMethodType)getTypeLoader().parse(xmlStreamReader, DigestMethodType.type, xmlOptions);
        }
        
        public static DigestMethodType parse(final Node node) throws XmlException {
            return (DigestMethodType)getTypeLoader().parse(node, DigestMethodType.type, (XmlOptions)null);
        }
        
        public static DigestMethodType parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (DigestMethodType)getTypeLoader().parse(node, DigestMethodType.type, xmlOptions);
        }
        
        @Deprecated
        public static DigestMethodType parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (DigestMethodType)getTypeLoader().parse(xmlInputStream, DigestMethodType.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static DigestMethodType parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (DigestMethodType)getTypeLoader().parse(xmlInputStream, DigestMethodType.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, DigestMethodType.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, DigestMethodType.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
