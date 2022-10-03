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

public interface CompleteRevocationRefsType extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CompleteRevocationRefsType.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.s8C3F193EE11A2F798ACF65489B9E6078").resolveHandle("completerevocationrefstyped8a5type");
    
    CRLRefsType getCRLRefs();
    
    boolean isSetCRLRefs();
    
    void setCRLRefs(final CRLRefsType p0);
    
    CRLRefsType addNewCRLRefs();
    
    void unsetCRLRefs();
    
    OCSPRefsType getOCSPRefs();
    
    boolean isSetOCSPRefs();
    
    void setOCSPRefs(final OCSPRefsType p0);
    
    OCSPRefsType addNewOCSPRefs();
    
    void unsetOCSPRefs();
    
    OtherCertStatusRefsType getOtherRefs();
    
    boolean isSetOtherRefs();
    
    void setOtherRefs(final OtherCertStatusRefsType p0);
    
    OtherCertStatusRefsType addNewOtherRefs();
    
    void unsetOtherRefs();
    
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
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CompleteRevocationRefsType.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CompleteRevocationRefsType newInstance() {
            return (CompleteRevocationRefsType)getTypeLoader().newInstance(CompleteRevocationRefsType.type, (XmlOptions)null);
        }
        
        public static CompleteRevocationRefsType newInstance(final XmlOptions xmlOptions) {
            return (CompleteRevocationRefsType)getTypeLoader().newInstance(CompleteRevocationRefsType.type, xmlOptions);
        }
        
        public static CompleteRevocationRefsType parse(final String s) throws XmlException {
            return (CompleteRevocationRefsType)getTypeLoader().parse(s, CompleteRevocationRefsType.type, (XmlOptions)null);
        }
        
        public static CompleteRevocationRefsType parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CompleteRevocationRefsType)getTypeLoader().parse(s, CompleteRevocationRefsType.type, xmlOptions);
        }
        
        public static CompleteRevocationRefsType parse(final File file) throws XmlException, IOException {
            return (CompleteRevocationRefsType)getTypeLoader().parse(file, CompleteRevocationRefsType.type, (XmlOptions)null);
        }
        
        public static CompleteRevocationRefsType parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CompleteRevocationRefsType)getTypeLoader().parse(file, CompleteRevocationRefsType.type, xmlOptions);
        }
        
        public static CompleteRevocationRefsType parse(final URL url) throws XmlException, IOException {
            return (CompleteRevocationRefsType)getTypeLoader().parse(url, CompleteRevocationRefsType.type, (XmlOptions)null);
        }
        
        public static CompleteRevocationRefsType parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CompleteRevocationRefsType)getTypeLoader().parse(url, CompleteRevocationRefsType.type, xmlOptions);
        }
        
        public static CompleteRevocationRefsType parse(final InputStream inputStream) throws XmlException, IOException {
            return (CompleteRevocationRefsType)getTypeLoader().parse(inputStream, CompleteRevocationRefsType.type, (XmlOptions)null);
        }
        
        public static CompleteRevocationRefsType parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CompleteRevocationRefsType)getTypeLoader().parse(inputStream, CompleteRevocationRefsType.type, xmlOptions);
        }
        
        public static CompleteRevocationRefsType parse(final Reader reader) throws XmlException, IOException {
            return (CompleteRevocationRefsType)getTypeLoader().parse(reader, CompleteRevocationRefsType.type, (XmlOptions)null);
        }
        
        public static CompleteRevocationRefsType parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CompleteRevocationRefsType)getTypeLoader().parse(reader, CompleteRevocationRefsType.type, xmlOptions);
        }
        
        public static CompleteRevocationRefsType parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CompleteRevocationRefsType)getTypeLoader().parse(xmlStreamReader, CompleteRevocationRefsType.type, (XmlOptions)null);
        }
        
        public static CompleteRevocationRefsType parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CompleteRevocationRefsType)getTypeLoader().parse(xmlStreamReader, CompleteRevocationRefsType.type, xmlOptions);
        }
        
        public static CompleteRevocationRefsType parse(final Node node) throws XmlException {
            return (CompleteRevocationRefsType)getTypeLoader().parse(node, CompleteRevocationRefsType.type, (XmlOptions)null);
        }
        
        public static CompleteRevocationRefsType parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CompleteRevocationRefsType)getTypeLoader().parse(node, CompleteRevocationRefsType.type, xmlOptions);
        }
        
        @Deprecated
        public static CompleteRevocationRefsType parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CompleteRevocationRefsType)getTypeLoader().parse(xmlInputStream, CompleteRevocationRefsType.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CompleteRevocationRefsType parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CompleteRevocationRefsType)getTypeLoader().parse(xmlInputStream, CompleteRevocationRefsType.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CompleteRevocationRefsType.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CompleteRevocationRefsType.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
