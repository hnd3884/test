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
import java.util.List;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CertIDListType extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CertIDListType.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.s8C3F193EE11A2F798ACF65489B9E6078").resolveHandle("certidlisttype488btype");
    
    List<CertIDType> getCertList();
    
    @Deprecated
    CertIDType[] getCertArray();
    
    CertIDType getCertArray(final int p0);
    
    int sizeOfCertArray();
    
    void setCertArray(final CertIDType[] p0);
    
    void setCertArray(final int p0, final CertIDType p1);
    
    CertIDType insertNewCert(final int p0);
    
    CertIDType addNewCert();
    
    void removeCert(final int p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CertIDListType.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CertIDListType newInstance() {
            return (CertIDListType)getTypeLoader().newInstance(CertIDListType.type, (XmlOptions)null);
        }
        
        public static CertIDListType newInstance(final XmlOptions xmlOptions) {
            return (CertIDListType)getTypeLoader().newInstance(CertIDListType.type, xmlOptions);
        }
        
        public static CertIDListType parse(final String s) throws XmlException {
            return (CertIDListType)getTypeLoader().parse(s, CertIDListType.type, (XmlOptions)null);
        }
        
        public static CertIDListType parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CertIDListType)getTypeLoader().parse(s, CertIDListType.type, xmlOptions);
        }
        
        public static CertIDListType parse(final File file) throws XmlException, IOException {
            return (CertIDListType)getTypeLoader().parse(file, CertIDListType.type, (XmlOptions)null);
        }
        
        public static CertIDListType parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CertIDListType)getTypeLoader().parse(file, CertIDListType.type, xmlOptions);
        }
        
        public static CertIDListType parse(final URL url) throws XmlException, IOException {
            return (CertIDListType)getTypeLoader().parse(url, CertIDListType.type, (XmlOptions)null);
        }
        
        public static CertIDListType parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CertIDListType)getTypeLoader().parse(url, CertIDListType.type, xmlOptions);
        }
        
        public static CertIDListType parse(final InputStream inputStream) throws XmlException, IOException {
            return (CertIDListType)getTypeLoader().parse(inputStream, CertIDListType.type, (XmlOptions)null);
        }
        
        public static CertIDListType parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CertIDListType)getTypeLoader().parse(inputStream, CertIDListType.type, xmlOptions);
        }
        
        public static CertIDListType parse(final Reader reader) throws XmlException, IOException {
            return (CertIDListType)getTypeLoader().parse(reader, CertIDListType.type, (XmlOptions)null);
        }
        
        public static CertIDListType parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CertIDListType)getTypeLoader().parse(reader, CertIDListType.type, xmlOptions);
        }
        
        public static CertIDListType parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CertIDListType)getTypeLoader().parse(xmlStreamReader, CertIDListType.type, (XmlOptions)null);
        }
        
        public static CertIDListType parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CertIDListType)getTypeLoader().parse(xmlStreamReader, CertIDListType.type, xmlOptions);
        }
        
        public static CertIDListType parse(final Node node) throws XmlException {
            return (CertIDListType)getTypeLoader().parse(node, CertIDListType.type, (XmlOptions)null);
        }
        
        public static CertIDListType parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CertIDListType)getTypeLoader().parse(node, CertIDListType.type, xmlOptions);
        }
        
        @Deprecated
        public static CertIDListType parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CertIDListType)getTypeLoader().parse(xmlInputStream, CertIDListType.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CertIDListType parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CertIDListType)getTypeLoader().parse(xmlInputStream, CertIDListType.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CertIDListType.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CertIDListType.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
