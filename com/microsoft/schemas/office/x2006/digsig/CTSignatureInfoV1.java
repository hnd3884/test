package com.microsoft.schemas.office.x2006.digsig;

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
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.XmlInt;
import org.apache.xmlbeans.XmlBase64Binary;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTSignatureInfoV1 extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTSignatureInfoV1.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.s8C3F193EE11A2F798ACF65489B9E6078").resolveHandle("ctsignatureinfov13a5ftype");
    
    String getSetupID();
    
    STUniqueIdentifierWithBraces xgetSetupID();
    
    void setSetupID(final String p0);
    
    void xsetSetupID(final STUniqueIdentifierWithBraces p0);
    
    String getSignatureText();
    
    STSignatureText xgetSignatureText();
    
    void setSignatureText(final String p0);
    
    void xsetSignatureText(final STSignatureText p0);
    
    byte[] getSignatureImage();
    
    XmlBase64Binary xgetSignatureImage();
    
    void setSignatureImage(final byte[] p0);
    
    void xsetSignatureImage(final XmlBase64Binary p0);
    
    String getSignatureComments();
    
    STSignatureComments xgetSignatureComments();
    
    void setSignatureComments(final String p0);
    
    void xsetSignatureComments(final STSignatureComments p0);
    
    String getWindowsVersion();
    
    STVersion xgetWindowsVersion();
    
    void setWindowsVersion(final String p0);
    
    void xsetWindowsVersion(final STVersion p0);
    
    String getOfficeVersion();
    
    STVersion xgetOfficeVersion();
    
    void setOfficeVersion(final String p0);
    
    void xsetOfficeVersion(final STVersion p0);
    
    String getApplicationVersion();
    
    STVersion xgetApplicationVersion();
    
    void setApplicationVersion(final String p0);
    
    void xsetApplicationVersion(final STVersion p0);
    
    int getMonitors();
    
    STPositiveInteger xgetMonitors();
    
    void setMonitors(final int p0);
    
    void xsetMonitors(final STPositiveInteger p0);
    
    int getHorizontalResolution();
    
    STPositiveInteger xgetHorizontalResolution();
    
    void setHorizontalResolution(final int p0);
    
    void xsetHorizontalResolution(final STPositiveInteger p0);
    
    int getVerticalResolution();
    
    STPositiveInteger xgetVerticalResolution();
    
    void setVerticalResolution(final int p0);
    
    void xsetVerticalResolution(final STPositiveInteger p0);
    
    int getColorDepth();
    
    STPositiveInteger xgetColorDepth();
    
    void setColorDepth(final int p0);
    
    void xsetColorDepth(final STPositiveInteger p0);
    
    String getSignatureProviderId();
    
    STUniqueIdentifierWithBraces xgetSignatureProviderId();
    
    void setSignatureProviderId(final String p0);
    
    void xsetSignatureProviderId(final STUniqueIdentifierWithBraces p0);
    
    String getSignatureProviderUrl();
    
    STSignatureProviderUrl xgetSignatureProviderUrl();
    
    void setSignatureProviderUrl(final String p0);
    
    void xsetSignatureProviderUrl(final STSignatureProviderUrl p0);
    
    int getSignatureProviderDetails();
    
    XmlInt xgetSignatureProviderDetails();
    
    void setSignatureProviderDetails(final int p0);
    
    void xsetSignatureProviderDetails(final XmlInt p0);
    
    int getSignatureType();
    
    STSignatureType xgetSignatureType();
    
    void setSignatureType(final int p0);
    
    void xsetSignatureType(final STSignatureType p0);
    
    String getDelegateSuggestedSigner();
    
    XmlString xgetDelegateSuggestedSigner();
    
    boolean isSetDelegateSuggestedSigner();
    
    void setDelegateSuggestedSigner(final String p0);
    
    void xsetDelegateSuggestedSigner(final XmlString p0);
    
    void unsetDelegateSuggestedSigner();
    
    String getDelegateSuggestedSigner2();
    
    XmlString xgetDelegateSuggestedSigner2();
    
    boolean isSetDelegateSuggestedSigner2();
    
    void setDelegateSuggestedSigner2(final String p0);
    
    void xsetDelegateSuggestedSigner2(final XmlString p0);
    
    void unsetDelegateSuggestedSigner2();
    
    String getDelegateSuggestedSignerEmail();
    
    XmlString xgetDelegateSuggestedSignerEmail();
    
    boolean isSetDelegateSuggestedSignerEmail();
    
    void setDelegateSuggestedSignerEmail(final String p0);
    
    void xsetDelegateSuggestedSignerEmail(final XmlString p0);
    
    void unsetDelegateSuggestedSignerEmail();
    
    String getManifestHashAlgorithm();
    
    XmlAnyURI xgetManifestHashAlgorithm();
    
    boolean isSetManifestHashAlgorithm();
    
    void setManifestHashAlgorithm(final String p0);
    
    void xsetManifestHashAlgorithm(final XmlAnyURI p0);
    
    void unsetManifestHashAlgorithm();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTSignatureInfoV1.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTSignatureInfoV1 newInstance() {
            return (CTSignatureInfoV1)getTypeLoader().newInstance(CTSignatureInfoV1.type, (XmlOptions)null);
        }
        
        public static CTSignatureInfoV1 newInstance(final XmlOptions xmlOptions) {
            return (CTSignatureInfoV1)getTypeLoader().newInstance(CTSignatureInfoV1.type, xmlOptions);
        }
        
        public static CTSignatureInfoV1 parse(final String s) throws XmlException {
            return (CTSignatureInfoV1)getTypeLoader().parse(s, CTSignatureInfoV1.type, (XmlOptions)null);
        }
        
        public static CTSignatureInfoV1 parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTSignatureInfoV1)getTypeLoader().parse(s, CTSignatureInfoV1.type, xmlOptions);
        }
        
        public static CTSignatureInfoV1 parse(final File file) throws XmlException, IOException {
            return (CTSignatureInfoV1)getTypeLoader().parse(file, CTSignatureInfoV1.type, (XmlOptions)null);
        }
        
        public static CTSignatureInfoV1 parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTSignatureInfoV1)getTypeLoader().parse(file, CTSignatureInfoV1.type, xmlOptions);
        }
        
        public static CTSignatureInfoV1 parse(final URL url) throws XmlException, IOException {
            return (CTSignatureInfoV1)getTypeLoader().parse(url, CTSignatureInfoV1.type, (XmlOptions)null);
        }
        
        public static CTSignatureInfoV1 parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTSignatureInfoV1)getTypeLoader().parse(url, CTSignatureInfoV1.type, xmlOptions);
        }
        
        public static CTSignatureInfoV1 parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTSignatureInfoV1)getTypeLoader().parse(inputStream, CTSignatureInfoV1.type, (XmlOptions)null);
        }
        
        public static CTSignatureInfoV1 parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTSignatureInfoV1)getTypeLoader().parse(inputStream, CTSignatureInfoV1.type, xmlOptions);
        }
        
        public static CTSignatureInfoV1 parse(final Reader reader) throws XmlException, IOException {
            return (CTSignatureInfoV1)getTypeLoader().parse(reader, CTSignatureInfoV1.type, (XmlOptions)null);
        }
        
        public static CTSignatureInfoV1 parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTSignatureInfoV1)getTypeLoader().parse(reader, CTSignatureInfoV1.type, xmlOptions);
        }
        
        public static CTSignatureInfoV1 parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTSignatureInfoV1)getTypeLoader().parse(xmlStreamReader, CTSignatureInfoV1.type, (XmlOptions)null);
        }
        
        public static CTSignatureInfoV1 parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTSignatureInfoV1)getTypeLoader().parse(xmlStreamReader, CTSignatureInfoV1.type, xmlOptions);
        }
        
        public static CTSignatureInfoV1 parse(final Node node) throws XmlException {
            return (CTSignatureInfoV1)getTypeLoader().parse(node, CTSignatureInfoV1.type, (XmlOptions)null);
        }
        
        public static CTSignatureInfoV1 parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTSignatureInfoV1)getTypeLoader().parse(node, CTSignatureInfoV1.type, xmlOptions);
        }
        
        @Deprecated
        public static CTSignatureInfoV1 parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTSignatureInfoV1)getTypeLoader().parse(xmlInputStream, CTSignatureInfoV1.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTSignatureInfoV1 parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTSignatureInfoV1)getTypeLoader().parse(xmlInputStream, CTSignatureInfoV1.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTSignatureInfoV1.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTSignatureInfoV1.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
