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
import org.apache.xmlbeans.XmlInteger;
import java.math.BigInteger;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface X509IssuerSerialType extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(X509IssuerSerialType.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.s8C3F193EE11A2F798ACF65489B9E6078").resolveHandle("x509issuerserialtype7eb2type");
    
    String getX509IssuerName();
    
    XmlString xgetX509IssuerName();
    
    void setX509IssuerName(final String p0);
    
    void xsetX509IssuerName(final XmlString p0);
    
    BigInteger getX509SerialNumber();
    
    XmlInteger xgetX509SerialNumber();
    
    void setX509SerialNumber(final BigInteger p0);
    
    void xsetX509SerialNumber(final XmlInteger p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(X509IssuerSerialType.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static X509IssuerSerialType newInstance() {
            return (X509IssuerSerialType)getTypeLoader().newInstance(X509IssuerSerialType.type, (XmlOptions)null);
        }
        
        public static X509IssuerSerialType newInstance(final XmlOptions xmlOptions) {
            return (X509IssuerSerialType)getTypeLoader().newInstance(X509IssuerSerialType.type, xmlOptions);
        }
        
        public static X509IssuerSerialType parse(final String s) throws XmlException {
            return (X509IssuerSerialType)getTypeLoader().parse(s, X509IssuerSerialType.type, (XmlOptions)null);
        }
        
        public static X509IssuerSerialType parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (X509IssuerSerialType)getTypeLoader().parse(s, X509IssuerSerialType.type, xmlOptions);
        }
        
        public static X509IssuerSerialType parse(final File file) throws XmlException, IOException {
            return (X509IssuerSerialType)getTypeLoader().parse(file, X509IssuerSerialType.type, (XmlOptions)null);
        }
        
        public static X509IssuerSerialType parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (X509IssuerSerialType)getTypeLoader().parse(file, X509IssuerSerialType.type, xmlOptions);
        }
        
        public static X509IssuerSerialType parse(final URL url) throws XmlException, IOException {
            return (X509IssuerSerialType)getTypeLoader().parse(url, X509IssuerSerialType.type, (XmlOptions)null);
        }
        
        public static X509IssuerSerialType parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (X509IssuerSerialType)getTypeLoader().parse(url, X509IssuerSerialType.type, xmlOptions);
        }
        
        public static X509IssuerSerialType parse(final InputStream inputStream) throws XmlException, IOException {
            return (X509IssuerSerialType)getTypeLoader().parse(inputStream, X509IssuerSerialType.type, (XmlOptions)null);
        }
        
        public static X509IssuerSerialType parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (X509IssuerSerialType)getTypeLoader().parse(inputStream, X509IssuerSerialType.type, xmlOptions);
        }
        
        public static X509IssuerSerialType parse(final Reader reader) throws XmlException, IOException {
            return (X509IssuerSerialType)getTypeLoader().parse(reader, X509IssuerSerialType.type, (XmlOptions)null);
        }
        
        public static X509IssuerSerialType parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (X509IssuerSerialType)getTypeLoader().parse(reader, X509IssuerSerialType.type, xmlOptions);
        }
        
        public static X509IssuerSerialType parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (X509IssuerSerialType)getTypeLoader().parse(xmlStreamReader, X509IssuerSerialType.type, (XmlOptions)null);
        }
        
        public static X509IssuerSerialType parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (X509IssuerSerialType)getTypeLoader().parse(xmlStreamReader, X509IssuerSerialType.type, xmlOptions);
        }
        
        public static X509IssuerSerialType parse(final Node node) throws XmlException {
            return (X509IssuerSerialType)getTypeLoader().parse(node, X509IssuerSerialType.type, (XmlOptions)null);
        }
        
        public static X509IssuerSerialType parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (X509IssuerSerialType)getTypeLoader().parse(node, X509IssuerSerialType.type, xmlOptions);
        }
        
        @Deprecated
        public static X509IssuerSerialType parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (X509IssuerSerialType)getTypeLoader().parse(xmlInputStream, X509IssuerSerialType.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static X509IssuerSerialType parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (X509IssuerSerialType)getTypeLoader().parse(xmlInputStream, X509IssuerSerialType.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, X509IssuerSerialType.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, X509IssuerSerialType.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
