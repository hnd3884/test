package com.microsoft.schemas.office.x2006.keyEncryptor.certificate;

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
import org.apache.xmlbeans.XmlBase64Binary;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTCertificateKeyEncryptor extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTCertificateKeyEncryptor.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.s8C3F193EE11A2F798ACF65489B9E6078").resolveHandle("ctcertificatekeyencryptor1a80type");
    
    byte[] getEncryptedKeyValue();
    
    XmlBase64Binary xgetEncryptedKeyValue();
    
    void setEncryptedKeyValue(final byte[] p0);
    
    void xsetEncryptedKeyValue(final XmlBase64Binary p0);
    
    byte[] getX509Certificate();
    
    XmlBase64Binary xgetX509Certificate();
    
    void setX509Certificate(final byte[] p0);
    
    void xsetX509Certificate(final XmlBase64Binary p0);
    
    byte[] getCertVerifier();
    
    XmlBase64Binary xgetCertVerifier();
    
    void setCertVerifier(final byte[] p0);
    
    void xsetCertVerifier(final XmlBase64Binary p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTCertificateKeyEncryptor.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTCertificateKeyEncryptor newInstance() {
            return (CTCertificateKeyEncryptor)getTypeLoader().newInstance(CTCertificateKeyEncryptor.type, (XmlOptions)null);
        }
        
        public static CTCertificateKeyEncryptor newInstance(final XmlOptions xmlOptions) {
            return (CTCertificateKeyEncryptor)getTypeLoader().newInstance(CTCertificateKeyEncryptor.type, xmlOptions);
        }
        
        public static CTCertificateKeyEncryptor parse(final String s) throws XmlException {
            return (CTCertificateKeyEncryptor)getTypeLoader().parse(s, CTCertificateKeyEncryptor.type, (XmlOptions)null);
        }
        
        public static CTCertificateKeyEncryptor parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTCertificateKeyEncryptor)getTypeLoader().parse(s, CTCertificateKeyEncryptor.type, xmlOptions);
        }
        
        public static CTCertificateKeyEncryptor parse(final File file) throws XmlException, IOException {
            return (CTCertificateKeyEncryptor)getTypeLoader().parse(file, CTCertificateKeyEncryptor.type, (XmlOptions)null);
        }
        
        public static CTCertificateKeyEncryptor parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTCertificateKeyEncryptor)getTypeLoader().parse(file, CTCertificateKeyEncryptor.type, xmlOptions);
        }
        
        public static CTCertificateKeyEncryptor parse(final URL url) throws XmlException, IOException {
            return (CTCertificateKeyEncryptor)getTypeLoader().parse(url, CTCertificateKeyEncryptor.type, (XmlOptions)null);
        }
        
        public static CTCertificateKeyEncryptor parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTCertificateKeyEncryptor)getTypeLoader().parse(url, CTCertificateKeyEncryptor.type, xmlOptions);
        }
        
        public static CTCertificateKeyEncryptor parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTCertificateKeyEncryptor)getTypeLoader().parse(inputStream, CTCertificateKeyEncryptor.type, (XmlOptions)null);
        }
        
        public static CTCertificateKeyEncryptor parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTCertificateKeyEncryptor)getTypeLoader().parse(inputStream, CTCertificateKeyEncryptor.type, xmlOptions);
        }
        
        public static CTCertificateKeyEncryptor parse(final Reader reader) throws XmlException, IOException {
            return (CTCertificateKeyEncryptor)getTypeLoader().parse(reader, CTCertificateKeyEncryptor.type, (XmlOptions)null);
        }
        
        public static CTCertificateKeyEncryptor parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTCertificateKeyEncryptor)getTypeLoader().parse(reader, CTCertificateKeyEncryptor.type, xmlOptions);
        }
        
        public static CTCertificateKeyEncryptor parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTCertificateKeyEncryptor)getTypeLoader().parse(xmlStreamReader, CTCertificateKeyEncryptor.type, (XmlOptions)null);
        }
        
        public static CTCertificateKeyEncryptor parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTCertificateKeyEncryptor)getTypeLoader().parse(xmlStreamReader, CTCertificateKeyEncryptor.type, xmlOptions);
        }
        
        public static CTCertificateKeyEncryptor parse(final Node node) throws XmlException {
            return (CTCertificateKeyEncryptor)getTypeLoader().parse(node, CTCertificateKeyEncryptor.type, (XmlOptions)null);
        }
        
        public static CTCertificateKeyEncryptor parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTCertificateKeyEncryptor)getTypeLoader().parse(node, CTCertificateKeyEncryptor.type, xmlOptions);
        }
        
        @Deprecated
        public static CTCertificateKeyEncryptor parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTCertificateKeyEncryptor)getTypeLoader().parse(xmlInputStream, CTCertificateKeyEncryptor.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTCertificateKeyEncryptor parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTCertificateKeyEncryptor)getTypeLoader().parse(xmlInputStream, CTCertificateKeyEncryptor.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTCertificateKeyEncryptor.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTCertificateKeyEncryptor.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
