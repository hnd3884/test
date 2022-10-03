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

public interface OCSPRefType extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(OCSPRefType.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.s8C3F193EE11A2F798ACF65489B9E6078").resolveHandle("ocspreftype089etype");
    
    OCSPIdentifierType getOCSPIdentifier();
    
    void setOCSPIdentifier(final OCSPIdentifierType p0);
    
    OCSPIdentifierType addNewOCSPIdentifier();
    
    DigestAlgAndValueType getDigestAlgAndValue();
    
    boolean isSetDigestAlgAndValue();
    
    void setDigestAlgAndValue(final DigestAlgAndValueType p0);
    
    DigestAlgAndValueType addNewDigestAlgAndValue();
    
    void unsetDigestAlgAndValue();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(OCSPRefType.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static OCSPRefType newInstance() {
            return (OCSPRefType)getTypeLoader().newInstance(OCSPRefType.type, (XmlOptions)null);
        }
        
        public static OCSPRefType newInstance(final XmlOptions xmlOptions) {
            return (OCSPRefType)getTypeLoader().newInstance(OCSPRefType.type, xmlOptions);
        }
        
        public static OCSPRefType parse(final String s) throws XmlException {
            return (OCSPRefType)getTypeLoader().parse(s, OCSPRefType.type, (XmlOptions)null);
        }
        
        public static OCSPRefType parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (OCSPRefType)getTypeLoader().parse(s, OCSPRefType.type, xmlOptions);
        }
        
        public static OCSPRefType parse(final File file) throws XmlException, IOException {
            return (OCSPRefType)getTypeLoader().parse(file, OCSPRefType.type, (XmlOptions)null);
        }
        
        public static OCSPRefType parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (OCSPRefType)getTypeLoader().parse(file, OCSPRefType.type, xmlOptions);
        }
        
        public static OCSPRefType parse(final URL url) throws XmlException, IOException {
            return (OCSPRefType)getTypeLoader().parse(url, OCSPRefType.type, (XmlOptions)null);
        }
        
        public static OCSPRefType parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (OCSPRefType)getTypeLoader().parse(url, OCSPRefType.type, xmlOptions);
        }
        
        public static OCSPRefType parse(final InputStream inputStream) throws XmlException, IOException {
            return (OCSPRefType)getTypeLoader().parse(inputStream, OCSPRefType.type, (XmlOptions)null);
        }
        
        public static OCSPRefType parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (OCSPRefType)getTypeLoader().parse(inputStream, OCSPRefType.type, xmlOptions);
        }
        
        public static OCSPRefType parse(final Reader reader) throws XmlException, IOException {
            return (OCSPRefType)getTypeLoader().parse(reader, OCSPRefType.type, (XmlOptions)null);
        }
        
        public static OCSPRefType parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (OCSPRefType)getTypeLoader().parse(reader, OCSPRefType.type, xmlOptions);
        }
        
        public static OCSPRefType parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (OCSPRefType)getTypeLoader().parse(xmlStreamReader, OCSPRefType.type, (XmlOptions)null);
        }
        
        public static OCSPRefType parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (OCSPRefType)getTypeLoader().parse(xmlStreamReader, OCSPRefType.type, xmlOptions);
        }
        
        public static OCSPRefType parse(final Node node) throws XmlException {
            return (OCSPRefType)getTypeLoader().parse(node, OCSPRefType.type, (XmlOptions)null);
        }
        
        public static OCSPRefType parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (OCSPRefType)getTypeLoader().parse(node, OCSPRefType.type, xmlOptions);
        }
        
        @Deprecated
        public static OCSPRefType parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (OCSPRefType)getTypeLoader().parse(xmlInputStream, OCSPRefType.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static OCSPRefType parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (OCSPRefType)getTypeLoader().parse(xmlInputStream, OCSPRefType.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, OCSPRefType.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, OCSPRefType.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
