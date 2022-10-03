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
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface SignerRoleType extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(SignerRoleType.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.s8C3F193EE11A2F798ACF65489B9E6078").resolveHandle("signerroletypef58etype");
    
    ClaimedRolesListType getClaimedRoles();
    
    boolean isSetClaimedRoles();
    
    void setClaimedRoles(final ClaimedRolesListType p0);
    
    ClaimedRolesListType addNewClaimedRoles();
    
    void unsetClaimedRoles();
    
    CertifiedRolesListType getCertifiedRoles();
    
    boolean isSetCertifiedRoles();
    
    void setCertifiedRoles(final CertifiedRolesListType p0);
    
    CertifiedRolesListType addNewCertifiedRoles();
    
    void unsetCertifiedRoles();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(SignerRoleType.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static SignerRoleType newInstance() {
            return (SignerRoleType)getTypeLoader().newInstance(SignerRoleType.type, (XmlOptions)null);
        }
        
        public static SignerRoleType newInstance(final XmlOptions xmlOptions) {
            return (SignerRoleType)getTypeLoader().newInstance(SignerRoleType.type, xmlOptions);
        }
        
        public static SignerRoleType parse(final String s) throws XmlException {
            return (SignerRoleType)getTypeLoader().parse(s, SignerRoleType.type, (XmlOptions)null);
        }
        
        public static SignerRoleType parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (SignerRoleType)getTypeLoader().parse(s, SignerRoleType.type, xmlOptions);
        }
        
        public static SignerRoleType parse(final File file) throws XmlException, IOException {
            return (SignerRoleType)getTypeLoader().parse(file, SignerRoleType.type, (XmlOptions)null);
        }
        
        public static SignerRoleType parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (SignerRoleType)getTypeLoader().parse(file, SignerRoleType.type, xmlOptions);
        }
        
        public static SignerRoleType parse(final URL url) throws XmlException, IOException {
            return (SignerRoleType)getTypeLoader().parse(url, SignerRoleType.type, (XmlOptions)null);
        }
        
        public static SignerRoleType parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (SignerRoleType)getTypeLoader().parse(url, SignerRoleType.type, xmlOptions);
        }
        
        public static SignerRoleType parse(final InputStream inputStream) throws XmlException, IOException {
            return (SignerRoleType)getTypeLoader().parse(inputStream, SignerRoleType.type, (XmlOptions)null);
        }
        
        public static SignerRoleType parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (SignerRoleType)getTypeLoader().parse(inputStream, SignerRoleType.type, xmlOptions);
        }
        
        public static SignerRoleType parse(final Reader reader) throws XmlException, IOException {
            return (SignerRoleType)getTypeLoader().parse(reader, SignerRoleType.type, (XmlOptions)null);
        }
        
        public static SignerRoleType parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (SignerRoleType)getTypeLoader().parse(reader, SignerRoleType.type, xmlOptions);
        }
        
        public static SignerRoleType parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (SignerRoleType)getTypeLoader().parse(xmlStreamReader, SignerRoleType.type, (XmlOptions)null);
        }
        
        public static SignerRoleType parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (SignerRoleType)getTypeLoader().parse(xmlStreamReader, SignerRoleType.type, xmlOptions);
        }
        
        public static SignerRoleType parse(final Node node) throws XmlException {
            return (SignerRoleType)getTypeLoader().parse(node, SignerRoleType.type, (XmlOptions)null);
        }
        
        public static SignerRoleType parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (SignerRoleType)getTypeLoader().parse(node, SignerRoleType.type, xmlOptions);
        }
        
        @Deprecated
        public static SignerRoleType parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (SignerRoleType)getTypeLoader().parse(xmlInputStream, SignerRoleType.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static SignerRoleType parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (SignerRoleType)getTypeLoader().parse(xmlInputStream, SignerRoleType.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, SignerRoleType.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, SignerRoleType.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
