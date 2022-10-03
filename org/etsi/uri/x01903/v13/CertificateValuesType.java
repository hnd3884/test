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
import java.util.List;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CertificateValuesType extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CertificateValuesType.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.s8C3F193EE11A2F798ACF65489B9E6078").resolveHandle("certificatevaluestype5c75type");
    
    List<EncapsulatedPKIDataType> getEncapsulatedX509CertificateList();
    
    @Deprecated
    EncapsulatedPKIDataType[] getEncapsulatedX509CertificateArray();
    
    EncapsulatedPKIDataType getEncapsulatedX509CertificateArray(final int p0);
    
    int sizeOfEncapsulatedX509CertificateArray();
    
    void setEncapsulatedX509CertificateArray(final EncapsulatedPKIDataType[] p0);
    
    void setEncapsulatedX509CertificateArray(final int p0, final EncapsulatedPKIDataType p1);
    
    EncapsulatedPKIDataType insertNewEncapsulatedX509Certificate(final int p0);
    
    EncapsulatedPKIDataType addNewEncapsulatedX509Certificate();
    
    void removeEncapsulatedX509Certificate(final int p0);
    
    List<AnyType> getOtherCertificateList();
    
    @Deprecated
    AnyType[] getOtherCertificateArray();
    
    AnyType getOtherCertificateArray(final int p0);
    
    int sizeOfOtherCertificateArray();
    
    void setOtherCertificateArray(final AnyType[] p0);
    
    void setOtherCertificateArray(final int p0, final AnyType p1);
    
    AnyType insertNewOtherCertificate(final int p0);
    
    AnyType addNewOtherCertificate();
    
    void removeOtherCertificate(final int p0);
    
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
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CertificateValuesType.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CertificateValuesType newInstance() {
            return (CertificateValuesType)getTypeLoader().newInstance(CertificateValuesType.type, (XmlOptions)null);
        }
        
        public static CertificateValuesType newInstance(final XmlOptions xmlOptions) {
            return (CertificateValuesType)getTypeLoader().newInstance(CertificateValuesType.type, xmlOptions);
        }
        
        public static CertificateValuesType parse(final String s) throws XmlException {
            return (CertificateValuesType)getTypeLoader().parse(s, CertificateValuesType.type, (XmlOptions)null);
        }
        
        public static CertificateValuesType parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CertificateValuesType)getTypeLoader().parse(s, CertificateValuesType.type, xmlOptions);
        }
        
        public static CertificateValuesType parse(final File file) throws XmlException, IOException {
            return (CertificateValuesType)getTypeLoader().parse(file, CertificateValuesType.type, (XmlOptions)null);
        }
        
        public static CertificateValuesType parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CertificateValuesType)getTypeLoader().parse(file, CertificateValuesType.type, xmlOptions);
        }
        
        public static CertificateValuesType parse(final URL url) throws XmlException, IOException {
            return (CertificateValuesType)getTypeLoader().parse(url, CertificateValuesType.type, (XmlOptions)null);
        }
        
        public static CertificateValuesType parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CertificateValuesType)getTypeLoader().parse(url, CertificateValuesType.type, xmlOptions);
        }
        
        public static CertificateValuesType parse(final InputStream inputStream) throws XmlException, IOException {
            return (CertificateValuesType)getTypeLoader().parse(inputStream, CertificateValuesType.type, (XmlOptions)null);
        }
        
        public static CertificateValuesType parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CertificateValuesType)getTypeLoader().parse(inputStream, CertificateValuesType.type, xmlOptions);
        }
        
        public static CertificateValuesType parse(final Reader reader) throws XmlException, IOException {
            return (CertificateValuesType)getTypeLoader().parse(reader, CertificateValuesType.type, (XmlOptions)null);
        }
        
        public static CertificateValuesType parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CertificateValuesType)getTypeLoader().parse(reader, CertificateValuesType.type, xmlOptions);
        }
        
        public static CertificateValuesType parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CertificateValuesType)getTypeLoader().parse(xmlStreamReader, CertificateValuesType.type, (XmlOptions)null);
        }
        
        public static CertificateValuesType parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CertificateValuesType)getTypeLoader().parse(xmlStreamReader, CertificateValuesType.type, xmlOptions);
        }
        
        public static CertificateValuesType parse(final Node node) throws XmlException {
            return (CertificateValuesType)getTypeLoader().parse(node, CertificateValuesType.type, (XmlOptions)null);
        }
        
        public static CertificateValuesType parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CertificateValuesType)getTypeLoader().parse(node, CertificateValuesType.type, xmlOptions);
        }
        
        @Deprecated
        public static CertificateValuesType parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CertificateValuesType)getTypeLoader().parse(xmlInputStream, CertificateValuesType.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CertificateValuesType parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CertificateValuesType)getTypeLoader().parse(xmlInputStream, CertificateValuesType.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CertificateValuesType.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CertificateValuesType.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
