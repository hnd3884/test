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
import org.apache.xmlbeans.XmlDateTime;
import java.util.Calendar;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface SignedSignaturePropertiesType extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(SignedSignaturePropertiesType.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.s8C3F193EE11A2F798ACF65489B9E6078").resolveHandle("signedsignaturepropertiestype06abtype");
    
    Calendar getSigningTime();
    
    XmlDateTime xgetSigningTime();
    
    boolean isSetSigningTime();
    
    void setSigningTime(final Calendar p0);
    
    void xsetSigningTime(final XmlDateTime p0);
    
    void unsetSigningTime();
    
    CertIDListType getSigningCertificate();
    
    boolean isSetSigningCertificate();
    
    void setSigningCertificate(final CertIDListType p0);
    
    CertIDListType addNewSigningCertificate();
    
    void unsetSigningCertificate();
    
    SignaturePolicyIdentifierType getSignaturePolicyIdentifier();
    
    boolean isSetSignaturePolicyIdentifier();
    
    void setSignaturePolicyIdentifier(final SignaturePolicyIdentifierType p0);
    
    SignaturePolicyIdentifierType addNewSignaturePolicyIdentifier();
    
    void unsetSignaturePolicyIdentifier();
    
    SignatureProductionPlaceType getSignatureProductionPlace();
    
    boolean isSetSignatureProductionPlace();
    
    void setSignatureProductionPlace(final SignatureProductionPlaceType p0);
    
    SignatureProductionPlaceType addNewSignatureProductionPlace();
    
    void unsetSignatureProductionPlace();
    
    SignerRoleType getSignerRole();
    
    boolean isSetSignerRole();
    
    void setSignerRole(final SignerRoleType p0);
    
    SignerRoleType addNewSignerRole();
    
    void unsetSignerRole();
    
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
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(SignedSignaturePropertiesType.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static SignedSignaturePropertiesType newInstance() {
            return (SignedSignaturePropertiesType)getTypeLoader().newInstance(SignedSignaturePropertiesType.type, (XmlOptions)null);
        }
        
        public static SignedSignaturePropertiesType newInstance(final XmlOptions xmlOptions) {
            return (SignedSignaturePropertiesType)getTypeLoader().newInstance(SignedSignaturePropertiesType.type, xmlOptions);
        }
        
        public static SignedSignaturePropertiesType parse(final String s) throws XmlException {
            return (SignedSignaturePropertiesType)getTypeLoader().parse(s, SignedSignaturePropertiesType.type, (XmlOptions)null);
        }
        
        public static SignedSignaturePropertiesType parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (SignedSignaturePropertiesType)getTypeLoader().parse(s, SignedSignaturePropertiesType.type, xmlOptions);
        }
        
        public static SignedSignaturePropertiesType parse(final File file) throws XmlException, IOException {
            return (SignedSignaturePropertiesType)getTypeLoader().parse(file, SignedSignaturePropertiesType.type, (XmlOptions)null);
        }
        
        public static SignedSignaturePropertiesType parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (SignedSignaturePropertiesType)getTypeLoader().parse(file, SignedSignaturePropertiesType.type, xmlOptions);
        }
        
        public static SignedSignaturePropertiesType parse(final URL url) throws XmlException, IOException {
            return (SignedSignaturePropertiesType)getTypeLoader().parse(url, SignedSignaturePropertiesType.type, (XmlOptions)null);
        }
        
        public static SignedSignaturePropertiesType parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (SignedSignaturePropertiesType)getTypeLoader().parse(url, SignedSignaturePropertiesType.type, xmlOptions);
        }
        
        public static SignedSignaturePropertiesType parse(final InputStream inputStream) throws XmlException, IOException {
            return (SignedSignaturePropertiesType)getTypeLoader().parse(inputStream, SignedSignaturePropertiesType.type, (XmlOptions)null);
        }
        
        public static SignedSignaturePropertiesType parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (SignedSignaturePropertiesType)getTypeLoader().parse(inputStream, SignedSignaturePropertiesType.type, xmlOptions);
        }
        
        public static SignedSignaturePropertiesType parse(final Reader reader) throws XmlException, IOException {
            return (SignedSignaturePropertiesType)getTypeLoader().parse(reader, SignedSignaturePropertiesType.type, (XmlOptions)null);
        }
        
        public static SignedSignaturePropertiesType parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (SignedSignaturePropertiesType)getTypeLoader().parse(reader, SignedSignaturePropertiesType.type, xmlOptions);
        }
        
        public static SignedSignaturePropertiesType parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (SignedSignaturePropertiesType)getTypeLoader().parse(xmlStreamReader, SignedSignaturePropertiesType.type, (XmlOptions)null);
        }
        
        public static SignedSignaturePropertiesType parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (SignedSignaturePropertiesType)getTypeLoader().parse(xmlStreamReader, SignedSignaturePropertiesType.type, xmlOptions);
        }
        
        public static SignedSignaturePropertiesType parse(final Node node) throws XmlException {
            return (SignedSignaturePropertiesType)getTypeLoader().parse(node, SignedSignaturePropertiesType.type, (XmlOptions)null);
        }
        
        public static SignedSignaturePropertiesType parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (SignedSignaturePropertiesType)getTypeLoader().parse(node, SignedSignaturePropertiesType.type, xmlOptions);
        }
        
        @Deprecated
        public static SignedSignaturePropertiesType parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (SignedSignaturePropertiesType)getTypeLoader().parse(xmlInputStream, SignedSignaturePropertiesType.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static SignedSignaturePropertiesType parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (SignedSignaturePropertiesType)getTypeLoader().parse(xmlInputStream, SignedSignaturePropertiesType.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, SignedSignaturePropertiesType.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, SignedSignaturePropertiesType.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
