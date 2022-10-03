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
import org.apache.xmlbeans.XmlAnyURI;
import org.w3.x2000.x09.xmldsig.X509IssuerSerialType;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CertIDType extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CertIDType.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.s8C3F193EE11A2F798ACF65489B9E6078").resolveHandle("certidtypee64dtype");
    
    DigestAlgAndValueType getCertDigest();
    
    void setCertDigest(final DigestAlgAndValueType p0);
    
    DigestAlgAndValueType addNewCertDigest();
    
    X509IssuerSerialType getIssuerSerial();
    
    void setIssuerSerial(final X509IssuerSerialType p0);
    
    X509IssuerSerialType addNewIssuerSerial();
    
    String getURI();
    
    XmlAnyURI xgetURI();
    
    boolean isSetURI();
    
    void setURI(final String p0);
    
    void xsetURI(final XmlAnyURI p0);
    
    void unsetURI();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CertIDType.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CertIDType newInstance() {
            return (CertIDType)getTypeLoader().newInstance(CertIDType.type, (XmlOptions)null);
        }
        
        public static CertIDType newInstance(final XmlOptions xmlOptions) {
            return (CertIDType)getTypeLoader().newInstance(CertIDType.type, xmlOptions);
        }
        
        public static CertIDType parse(final String s) throws XmlException {
            return (CertIDType)getTypeLoader().parse(s, CertIDType.type, (XmlOptions)null);
        }
        
        public static CertIDType parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CertIDType)getTypeLoader().parse(s, CertIDType.type, xmlOptions);
        }
        
        public static CertIDType parse(final File file) throws XmlException, IOException {
            return (CertIDType)getTypeLoader().parse(file, CertIDType.type, (XmlOptions)null);
        }
        
        public static CertIDType parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CertIDType)getTypeLoader().parse(file, CertIDType.type, xmlOptions);
        }
        
        public static CertIDType parse(final URL url) throws XmlException, IOException {
            return (CertIDType)getTypeLoader().parse(url, CertIDType.type, (XmlOptions)null);
        }
        
        public static CertIDType parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CertIDType)getTypeLoader().parse(url, CertIDType.type, xmlOptions);
        }
        
        public static CertIDType parse(final InputStream inputStream) throws XmlException, IOException {
            return (CertIDType)getTypeLoader().parse(inputStream, CertIDType.type, (XmlOptions)null);
        }
        
        public static CertIDType parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CertIDType)getTypeLoader().parse(inputStream, CertIDType.type, xmlOptions);
        }
        
        public static CertIDType parse(final Reader reader) throws XmlException, IOException {
            return (CertIDType)getTypeLoader().parse(reader, CertIDType.type, (XmlOptions)null);
        }
        
        public static CertIDType parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CertIDType)getTypeLoader().parse(reader, CertIDType.type, xmlOptions);
        }
        
        public static CertIDType parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CertIDType)getTypeLoader().parse(xmlStreamReader, CertIDType.type, (XmlOptions)null);
        }
        
        public static CertIDType parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CertIDType)getTypeLoader().parse(xmlStreamReader, CertIDType.type, xmlOptions);
        }
        
        public static CertIDType parse(final Node node) throws XmlException {
            return (CertIDType)getTypeLoader().parse(node, CertIDType.type, (XmlOptions)null);
        }
        
        public static CertIDType parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CertIDType)getTypeLoader().parse(node, CertIDType.type, xmlOptions);
        }
        
        @Deprecated
        public static CertIDType parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CertIDType)getTypeLoader().parse(xmlInputStream, CertIDType.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CertIDType parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CertIDType)getTypeLoader().parse(xmlInputStream, CertIDType.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CertIDType.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CertIDType.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
