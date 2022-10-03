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
import org.apache.xmlbeans.XmlID;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CompleteCertificateRefsType extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CompleteCertificateRefsType.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.s8C3F193EE11A2F798ACF65489B9E6078").resolveHandle("completecertificaterefstype07datype");
    
    CertIDListType getCertRefs();
    
    void setCertRefs(final CertIDListType p0);
    
    CertIDListType addNewCertRefs();
    
    String getId();
    
    XmlID xgetId();
    
    boolean isSetId();
    
    void setId(final String p0);
    
    void xsetId(final XmlID p0);
    
    void unsetId();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CompleteCertificateRefsType.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CompleteCertificateRefsType newInstance() {
            return (CompleteCertificateRefsType)getTypeLoader().newInstance(CompleteCertificateRefsType.type, (XmlOptions)null);
        }
        
        public static CompleteCertificateRefsType newInstance(final XmlOptions xmlOptions) {
            return (CompleteCertificateRefsType)getTypeLoader().newInstance(CompleteCertificateRefsType.type, xmlOptions);
        }
        
        public static CompleteCertificateRefsType parse(final String s) throws XmlException {
            return (CompleteCertificateRefsType)getTypeLoader().parse(s, CompleteCertificateRefsType.type, (XmlOptions)null);
        }
        
        public static CompleteCertificateRefsType parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CompleteCertificateRefsType)getTypeLoader().parse(s, CompleteCertificateRefsType.type, xmlOptions);
        }
        
        public static CompleteCertificateRefsType parse(final File file) throws XmlException, IOException {
            return (CompleteCertificateRefsType)getTypeLoader().parse(file, CompleteCertificateRefsType.type, (XmlOptions)null);
        }
        
        public static CompleteCertificateRefsType parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CompleteCertificateRefsType)getTypeLoader().parse(file, CompleteCertificateRefsType.type, xmlOptions);
        }
        
        public static CompleteCertificateRefsType parse(final URL url) throws XmlException, IOException {
            return (CompleteCertificateRefsType)getTypeLoader().parse(url, CompleteCertificateRefsType.type, (XmlOptions)null);
        }
        
        public static CompleteCertificateRefsType parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CompleteCertificateRefsType)getTypeLoader().parse(url, CompleteCertificateRefsType.type, xmlOptions);
        }
        
        public static CompleteCertificateRefsType parse(final InputStream inputStream) throws XmlException, IOException {
            return (CompleteCertificateRefsType)getTypeLoader().parse(inputStream, CompleteCertificateRefsType.type, (XmlOptions)null);
        }
        
        public static CompleteCertificateRefsType parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CompleteCertificateRefsType)getTypeLoader().parse(inputStream, CompleteCertificateRefsType.type, xmlOptions);
        }
        
        public static CompleteCertificateRefsType parse(final Reader reader) throws XmlException, IOException {
            return (CompleteCertificateRefsType)getTypeLoader().parse(reader, CompleteCertificateRefsType.type, (XmlOptions)null);
        }
        
        public static CompleteCertificateRefsType parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CompleteCertificateRefsType)getTypeLoader().parse(reader, CompleteCertificateRefsType.type, xmlOptions);
        }
        
        public static CompleteCertificateRefsType parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CompleteCertificateRefsType)getTypeLoader().parse(xmlStreamReader, CompleteCertificateRefsType.type, (XmlOptions)null);
        }
        
        public static CompleteCertificateRefsType parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CompleteCertificateRefsType)getTypeLoader().parse(xmlStreamReader, CompleteCertificateRefsType.type, xmlOptions);
        }
        
        public static CompleteCertificateRefsType parse(final Node node) throws XmlException {
            return (CompleteCertificateRefsType)getTypeLoader().parse(node, CompleteCertificateRefsType.type, (XmlOptions)null);
        }
        
        public static CompleteCertificateRefsType parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CompleteCertificateRefsType)getTypeLoader().parse(node, CompleteCertificateRefsType.type, xmlOptions);
        }
        
        @Deprecated
        public static CompleteCertificateRefsType parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CompleteCertificateRefsType)getTypeLoader().parse(xmlInputStream, CompleteCertificateRefsType.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CompleteCertificateRefsType parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CompleteCertificateRefsType)getTypeLoader().parse(xmlInputStream, CompleteCertificateRefsType.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CompleteCertificateRefsType.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CompleteCertificateRefsType.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
