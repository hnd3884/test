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

public interface RevocationValuesType extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(RevocationValuesType.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.s8C3F193EE11A2F798ACF65489B9E6078").resolveHandle("revocationvaluestype9a6etype");
    
    CRLValuesType getCRLValues();
    
    boolean isSetCRLValues();
    
    void setCRLValues(final CRLValuesType p0);
    
    CRLValuesType addNewCRLValues();
    
    void unsetCRLValues();
    
    OCSPValuesType getOCSPValues();
    
    boolean isSetOCSPValues();
    
    void setOCSPValues(final OCSPValuesType p0);
    
    OCSPValuesType addNewOCSPValues();
    
    void unsetOCSPValues();
    
    OtherCertStatusValuesType getOtherValues();
    
    boolean isSetOtherValues();
    
    void setOtherValues(final OtherCertStatusValuesType p0);
    
    OtherCertStatusValuesType addNewOtherValues();
    
    void unsetOtherValues();
    
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
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(RevocationValuesType.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static RevocationValuesType newInstance() {
            return (RevocationValuesType)getTypeLoader().newInstance(RevocationValuesType.type, (XmlOptions)null);
        }
        
        public static RevocationValuesType newInstance(final XmlOptions xmlOptions) {
            return (RevocationValuesType)getTypeLoader().newInstance(RevocationValuesType.type, xmlOptions);
        }
        
        public static RevocationValuesType parse(final String s) throws XmlException {
            return (RevocationValuesType)getTypeLoader().parse(s, RevocationValuesType.type, (XmlOptions)null);
        }
        
        public static RevocationValuesType parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (RevocationValuesType)getTypeLoader().parse(s, RevocationValuesType.type, xmlOptions);
        }
        
        public static RevocationValuesType parse(final File file) throws XmlException, IOException {
            return (RevocationValuesType)getTypeLoader().parse(file, RevocationValuesType.type, (XmlOptions)null);
        }
        
        public static RevocationValuesType parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (RevocationValuesType)getTypeLoader().parse(file, RevocationValuesType.type, xmlOptions);
        }
        
        public static RevocationValuesType parse(final URL url) throws XmlException, IOException {
            return (RevocationValuesType)getTypeLoader().parse(url, RevocationValuesType.type, (XmlOptions)null);
        }
        
        public static RevocationValuesType parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (RevocationValuesType)getTypeLoader().parse(url, RevocationValuesType.type, xmlOptions);
        }
        
        public static RevocationValuesType parse(final InputStream inputStream) throws XmlException, IOException {
            return (RevocationValuesType)getTypeLoader().parse(inputStream, RevocationValuesType.type, (XmlOptions)null);
        }
        
        public static RevocationValuesType parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (RevocationValuesType)getTypeLoader().parse(inputStream, RevocationValuesType.type, xmlOptions);
        }
        
        public static RevocationValuesType parse(final Reader reader) throws XmlException, IOException {
            return (RevocationValuesType)getTypeLoader().parse(reader, RevocationValuesType.type, (XmlOptions)null);
        }
        
        public static RevocationValuesType parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (RevocationValuesType)getTypeLoader().parse(reader, RevocationValuesType.type, xmlOptions);
        }
        
        public static RevocationValuesType parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (RevocationValuesType)getTypeLoader().parse(xmlStreamReader, RevocationValuesType.type, (XmlOptions)null);
        }
        
        public static RevocationValuesType parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (RevocationValuesType)getTypeLoader().parse(xmlStreamReader, RevocationValuesType.type, xmlOptions);
        }
        
        public static RevocationValuesType parse(final Node node) throws XmlException {
            return (RevocationValuesType)getTypeLoader().parse(node, RevocationValuesType.type, (XmlOptions)null);
        }
        
        public static RevocationValuesType parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (RevocationValuesType)getTypeLoader().parse(node, RevocationValuesType.type, xmlOptions);
        }
        
        @Deprecated
        public static RevocationValuesType parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (RevocationValuesType)getTypeLoader().parse(xmlInputStream, RevocationValuesType.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static RevocationValuesType parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (RevocationValuesType)getTypeLoader().parse(xmlInputStream, RevocationValuesType.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, RevocationValuesType.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, RevocationValuesType.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
