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

public interface CRLRefType extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CRLRefType.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.s8C3F193EE11A2F798ACF65489B9E6078").resolveHandle("crlreftype4444type");
    
    DigestAlgAndValueType getDigestAlgAndValue();
    
    void setDigestAlgAndValue(final DigestAlgAndValueType p0);
    
    DigestAlgAndValueType addNewDigestAlgAndValue();
    
    CRLIdentifierType getCRLIdentifier();
    
    boolean isSetCRLIdentifier();
    
    void setCRLIdentifier(final CRLIdentifierType p0);
    
    CRLIdentifierType addNewCRLIdentifier();
    
    void unsetCRLIdentifier();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CRLRefType.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CRLRefType newInstance() {
            return (CRLRefType)getTypeLoader().newInstance(CRLRefType.type, (XmlOptions)null);
        }
        
        public static CRLRefType newInstance(final XmlOptions xmlOptions) {
            return (CRLRefType)getTypeLoader().newInstance(CRLRefType.type, xmlOptions);
        }
        
        public static CRLRefType parse(final String s) throws XmlException {
            return (CRLRefType)getTypeLoader().parse(s, CRLRefType.type, (XmlOptions)null);
        }
        
        public static CRLRefType parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CRLRefType)getTypeLoader().parse(s, CRLRefType.type, xmlOptions);
        }
        
        public static CRLRefType parse(final File file) throws XmlException, IOException {
            return (CRLRefType)getTypeLoader().parse(file, CRLRefType.type, (XmlOptions)null);
        }
        
        public static CRLRefType parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CRLRefType)getTypeLoader().parse(file, CRLRefType.type, xmlOptions);
        }
        
        public static CRLRefType parse(final URL url) throws XmlException, IOException {
            return (CRLRefType)getTypeLoader().parse(url, CRLRefType.type, (XmlOptions)null);
        }
        
        public static CRLRefType parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CRLRefType)getTypeLoader().parse(url, CRLRefType.type, xmlOptions);
        }
        
        public static CRLRefType parse(final InputStream inputStream) throws XmlException, IOException {
            return (CRLRefType)getTypeLoader().parse(inputStream, CRLRefType.type, (XmlOptions)null);
        }
        
        public static CRLRefType parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CRLRefType)getTypeLoader().parse(inputStream, CRLRefType.type, xmlOptions);
        }
        
        public static CRLRefType parse(final Reader reader) throws XmlException, IOException {
            return (CRLRefType)getTypeLoader().parse(reader, CRLRefType.type, (XmlOptions)null);
        }
        
        public static CRLRefType parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CRLRefType)getTypeLoader().parse(reader, CRLRefType.type, xmlOptions);
        }
        
        public static CRLRefType parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CRLRefType)getTypeLoader().parse(xmlStreamReader, CRLRefType.type, (XmlOptions)null);
        }
        
        public static CRLRefType parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CRLRefType)getTypeLoader().parse(xmlStreamReader, CRLRefType.type, xmlOptions);
        }
        
        public static CRLRefType parse(final Node node) throws XmlException {
            return (CRLRefType)getTypeLoader().parse(node, CRLRefType.type, (XmlOptions)null);
        }
        
        public static CRLRefType parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CRLRefType)getTypeLoader().parse(node, CRLRefType.type, xmlOptions);
        }
        
        @Deprecated
        public static CRLRefType parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CRLRefType)getTypeLoader().parse(xmlInputStream, CRLRefType.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CRLRefType parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CRLRefType)getTypeLoader().parse(xmlInputStream, CRLRefType.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CRLRefType.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CRLRefType.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
