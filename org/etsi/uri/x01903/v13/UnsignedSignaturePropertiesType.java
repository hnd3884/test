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

public interface UnsignedSignaturePropertiesType extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(UnsignedSignaturePropertiesType.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.s8C3F193EE11A2F798ACF65489B9E6078").resolveHandle("unsignedsignaturepropertiestypecf32type");
    
    List<CounterSignatureType> getCounterSignatureList();
    
    @Deprecated
    CounterSignatureType[] getCounterSignatureArray();
    
    CounterSignatureType getCounterSignatureArray(final int p0);
    
    int sizeOfCounterSignatureArray();
    
    void setCounterSignatureArray(final CounterSignatureType[] p0);
    
    void setCounterSignatureArray(final int p0, final CounterSignatureType p1);
    
    CounterSignatureType insertNewCounterSignature(final int p0);
    
    CounterSignatureType addNewCounterSignature();
    
    void removeCounterSignature(final int p0);
    
    List<XAdESTimeStampType> getSignatureTimeStampList();
    
    @Deprecated
    XAdESTimeStampType[] getSignatureTimeStampArray();
    
    XAdESTimeStampType getSignatureTimeStampArray(final int p0);
    
    int sizeOfSignatureTimeStampArray();
    
    void setSignatureTimeStampArray(final XAdESTimeStampType[] p0);
    
    void setSignatureTimeStampArray(final int p0, final XAdESTimeStampType p1);
    
    XAdESTimeStampType insertNewSignatureTimeStamp(final int p0);
    
    XAdESTimeStampType addNewSignatureTimeStamp();
    
    void removeSignatureTimeStamp(final int p0);
    
    List<CompleteCertificateRefsType> getCompleteCertificateRefsList();
    
    @Deprecated
    CompleteCertificateRefsType[] getCompleteCertificateRefsArray();
    
    CompleteCertificateRefsType getCompleteCertificateRefsArray(final int p0);
    
    int sizeOfCompleteCertificateRefsArray();
    
    void setCompleteCertificateRefsArray(final CompleteCertificateRefsType[] p0);
    
    void setCompleteCertificateRefsArray(final int p0, final CompleteCertificateRefsType p1);
    
    CompleteCertificateRefsType insertNewCompleteCertificateRefs(final int p0);
    
    CompleteCertificateRefsType addNewCompleteCertificateRefs();
    
    void removeCompleteCertificateRefs(final int p0);
    
    List<CompleteRevocationRefsType> getCompleteRevocationRefsList();
    
    @Deprecated
    CompleteRevocationRefsType[] getCompleteRevocationRefsArray();
    
    CompleteRevocationRefsType getCompleteRevocationRefsArray(final int p0);
    
    int sizeOfCompleteRevocationRefsArray();
    
    void setCompleteRevocationRefsArray(final CompleteRevocationRefsType[] p0);
    
    void setCompleteRevocationRefsArray(final int p0, final CompleteRevocationRefsType p1);
    
    CompleteRevocationRefsType insertNewCompleteRevocationRefs(final int p0);
    
    CompleteRevocationRefsType addNewCompleteRevocationRefs();
    
    void removeCompleteRevocationRefs(final int p0);
    
    List<CompleteCertificateRefsType> getAttributeCertificateRefsList();
    
    @Deprecated
    CompleteCertificateRefsType[] getAttributeCertificateRefsArray();
    
    CompleteCertificateRefsType getAttributeCertificateRefsArray(final int p0);
    
    int sizeOfAttributeCertificateRefsArray();
    
    void setAttributeCertificateRefsArray(final CompleteCertificateRefsType[] p0);
    
    void setAttributeCertificateRefsArray(final int p0, final CompleteCertificateRefsType p1);
    
    CompleteCertificateRefsType insertNewAttributeCertificateRefs(final int p0);
    
    CompleteCertificateRefsType addNewAttributeCertificateRefs();
    
    void removeAttributeCertificateRefs(final int p0);
    
    List<CompleteRevocationRefsType> getAttributeRevocationRefsList();
    
    @Deprecated
    CompleteRevocationRefsType[] getAttributeRevocationRefsArray();
    
    CompleteRevocationRefsType getAttributeRevocationRefsArray(final int p0);
    
    int sizeOfAttributeRevocationRefsArray();
    
    void setAttributeRevocationRefsArray(final CompleteRevocationRefsType[] p0);
    
    void setAttributeRevocationRefsArray(final int p0, final CompleteRevocationRefsType p1);
    
    CompleteRevocationRefsType insertNewAttributeRevocationRefs(final int p0);
    
    CompleteRevocationRefsType addNewAttributeRevocationRefs();
    
    void removeAttributeRevocationRefs(final int p0);
    
    List<XAdESTimeStampType> getSigAndRefsTimeStampList();
    
    @Deprecated
    XAdESTimeStampType[] getSigAndRefsTimeStampArray();
    
    XAdESTimeStampType getSigAndRefsTimeStampArray(final int p0);
    
    int sizeOfSigAndRefsTimeStampArray();
    
    void setSigAndRefsTimeStampArray(final XAdESTimeStampType[] p0);
    
    void setSigAndRefsTimeStampArray(final int p0, final XAdESTimeStampType p1);
    
    XAdESTimeStampType insertNewSigAndRefsTimeStamp(final int p0);
    
    XAdESTimeStampType addNewSigAndRefsTimeStamp();
    
    void removeSigAndRefsTimeStamp(final int p0);
    
    List<XAdESTimeStampType> getRefsOnlyTimeStampList();
    
    @Deprecated
    XAdESTimeStampType[] getRefsOnlyTimeStampArray();
    
    XAdESTimeStampType getRefsOnlyTimeStampArray(final int p0);
    
    int sizeOfRefsOnlyTimeStampArray();
    
    void setRefsOnlyTimeStampArray(final XAdESTimeStampType[] p0);
    
    void setRefsOnlyTimeStampArray(final int p0, final XAdESTimeStampType p1);
    
    XAdESTimeStampType insertNewRefsOnlyTimeStamp(final int p0);
    
    XAdESTimeStampType addNewRefsOnlyTimeStamp();
    
    void removeRefsOnlyTimeStamp(final int p0);
    
    List<CertificateValuesType> getCertificateValuesList();
    
    @Deprecated
    CertificateValuesType[] getCertificateValuesArray();
    
    CertificateValuesType getCertificateValuesArray(final int p0);
    
    int sizeOfCertificateValuesArray();
    
    void setCertificateValuesArray(final CertificateValuesType[] p0);
    
    void setCertificateValuesArray(final int p0, final CertificateValuesType p1);
    
    CertificateValuesType insertNewCertificateValues(final int p0);
    
    CertificateValuesType addNewCertificateValues();
    
    void removeCertificateValues(final int p0);
    
    List<RevocationValuesType> getRevocationValuesList();
    
    @Deprecated
    RevocationValuesType[] getRevocationValuesArray();
    
    RevocationValuesType getRevocationValuesArray(final int p0);
    
    int sizeOfRevocationValuesArray();
    
    void setRevocationValuesArray(final RevocationValuesType[] p0);
    
    void setRevocationValuesArray(final int p0, final RevocationValuesType p1);
    
    RevocationValuesType insertNewRevocationValues(final int p0);
    
    RevocationValuesType addNewRevocationValues();
    
    void removeRevocationValues(final int p0);
    
    List<CertificateValuesType> getAttrAuthoritiesCertValuesList();
    
    @Deprecated
    CertificateValuesType[] getAttrAuthoritiesCertValuesArray();
    
    CertificateValuesType getAttrAuthoritiesCertValuesArray(final int p0);
    
    int sizeOfAttrAuthoritiesCertValuesArray();
    
    void setAttrAuthoritiesCertValuesArray(final CertificateValuesType[] p0);
    
    void setAttrAuthoritiesCertValuesArray(final int p0, final CertificateValuesType p1);
    
    CertificateValuesType insertNewAttrAuthoritiesCertValues(final int p0);
    
    CertificateValuesType addNewAttrAuthoritiesCertValues();
    
    void removeAttrAuthoritiesCertValues(final int p0);
    
    List<RevocationValuesType> getAttributeRevocationValuesList();
    
    @Deprecated
    RevocationValuesType[] getAttributeRevocationValuesArray();
    
    RevocationValuesType getAttributeRevocationValuesArray(final int p0);
    
    int sizeOfAttributeRevocationValuesArray();
    
    void setAttributeRevocationValuesArray(final RevocationValuesType[] p0);
    
    void setAttributeRevocationValuesArray(final int p0, final RevocationValuesType p1);
    
    RevocationValuesType insertNewAttributeRevocationValues(final int p0);
    
    RevocationValuesType addNewAttributeRevocationValues();
    
    void removeAttributeRevocationValues(final int p0);
    
    List<XAdESTimeStampType> getArchiveTimeStampList();
    
    @Deprecated
    XAdESTimeStampType[] getArchiveTimeStampArray();
    
    XAdESTimeStampType getArchiveTimeStampArray(final int p0);
    
    int sizeOfArchiveTimeStampArray();
    
    void setArchiveTimeStampArray(final XAdESTimeStampType[] p0);
    
    void setArchiveTimeStampArray(final int p0, final XAdESTimeStampType p1);
    
    XAdESTimeStampType insertNewArchiveTimeStamp(final int p0);
    
    XAdESTimeStampType addNewArchiveTimeStamp();
    
    void removeArchiveTimeStamp(final int p0);
    
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
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(UnsignedSignaturePropertiesType.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static UnsignedSignaturePropertiesType newInstance() {
            return (UnsignedSignaturePropertiesType)getTypeLoader().newInstance(UnsignedSignaturePropertiesType.type, (XmlOptions)null);
        }
        
        public static UnsignedSignaturePropertiesType newInstance(final XmlOptions xmlOptions) {
            return (UnsignedSignaturePropertiesType)getTypeLoader().newInstance(UnsignedSignaturePropertiesType.type, xmlOptions);
        }
        
        public static UnsignedSignaturePropertiesType parse(final String s) throws XmlException {
            return (UnsignedSignaturePropertiesType)getTypeLoader().parse(s, UnsignedSignaturePropertiesType.type, (XmlOptions)null);
        }
        
        public static UnsignedSignaturePropertiesType parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (UnsignedSignaturePropertiesType)getTypeLoader().parse(s, UnsignedSignaturePropertiesType.type, xmlOptions);
        }
        
        public static UnsignedSignaturePropertiesType parse(final File file) throws XmlException, IOException {
            return (UnsignedSignaturePropertiesType)getTypeLoader().parse(file, UnsignedSignaturePropertiesType.type, (XmlOptions)null);
        }
        
        public static UnsignedSignaturePropertiesType parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (UnsignedSignaturePropertiesType)getTypeLoader().parse(file, UnsignedSignaturePropertiesType.type, xmlOptions);
        }
        
        public static UnsignedSignaturePropertiesType parse(final URL url) throws XmlException, IOException {
            return (UnsignedSignaturePropertiesType)getTypeLoader().parse(url, UnsignedSignaturePropertiesType.type, (XmlOptions)null);
        }
        
        public static UnsignedSignaturePropertiesType parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (UnsignedSignaturePropertiesType)getTypeLoader().parse(url, UnsignedSignaturePropertiesType.type, xmlOptions);
        }
        
        public static UnsignedSignaturePropertiesType parse(final InputStream inputStream) throws XmlException, IOException {
            return (UnsignedSignaturePropertiesType)getTypeLoader().parse(inputStream, UnsignedSignaturePropertiesType.type, (XmlOptions)null);
        }
        
        public static UnsignedSignaturePropertiesType parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (UnsignedSignaturePropertiesType)getTypeLoader().parse(inputStream, UnsignedSignaturePropertiesType.type, xmlOptions);
        }
        
        public static UnsignedSignaturePropertiesType parse(final Reader reader) throws XmlException, IOException {
            return (UnsignedSignaturePropertiesType)getTypeLoader().parse(reader, UnsignedSignaturePropertiesType.type, (XmlOptions)null);
        }
        
        public static UnsignedSignaturePropertiesType parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (UnsignedSignaturePropertiesType)getTypeLoader().parse(reader, UnsignedSignaturePropertiesType.type, xmlOptions);
        }
        
        public static UnsignedSignaturePropertiesType parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (UnsignedSignaturePropertiesType)getTypeLoader().parse(xmlStreamReader, UnsignedSignaturePropertiesType.type, (XmlOptions)null);
        }
        
        public static UnsignedSignaturePropertiesType parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (UnsignedSignaturePropertiesType)getTypeLoader().parse(xmlStreamReader, UnsignedSignaturePropertiesType.type, xmlOptions);
        }
        
        public static UnsignedSignaturePropertiesType parse(final Node node) throws XmlException {
            return (UnsignedSignaturePropertiesType)getTypeLoader().parse(node, UnsignedSignaturePropertiesType.type, (XmlOptions)null);
        }
        
        public static UnsignedSignaturePropertiesType parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (UnsignedSignaturePropertiesType)getTypeLoader().parse(node, UnsignedSignaturePropertiesType.type, xmlOptions);
        }
        
        @Deprecated
        public static UnsignedSignaturePropertiesType parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (UnsignedSignaturePropertiesType)getTypeLoader().parse(xmlInputStream, UnsignedSignaturePropertiesType.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static UnsignedSignaturePropertiesType parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (UnsignedSignaturePropertiesType)getTypeLoader().parse(xmlInputStream, UnsignedSignaturePropertiesType.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, UnsignedSignaturePropertiesType.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, UnsignedSignaturePropertiesType.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
