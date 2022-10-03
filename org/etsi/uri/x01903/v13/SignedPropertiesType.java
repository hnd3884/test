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

public interface SignedPropertiesType extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(SignedPropertiesType.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.s8C3F193EE11A2F798ACF65489B9E6078").resolveHandle("signedpropertiestype163dtype");
    
    SignedSignaturePropertiesType getSignedSignatureProperties();
    
    boolean isSetSignedSignatureProperties();
    
    void setSignedSignatureProperties(final SignedSignaturePropertiesType p0);
    
    SignedSignaturePropertiesType addNewSignedSignatureProperties();
    
    void unsetSignedSignatureProperties();
    
    SignedDataObjectPropertiesType getSignedDataObjectProperties();
    
    boolean isSetSignedDataObjectProperties();
    
    void setSignedDataObjectProperties(final SignedDataObjectPropertiesType p0);
    
    SignedDataObjectPropertiesType addNewSignedDataObjectProperties();
    
    void unsetSignedDataObjectProperties();
    
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
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(SignedPropertiesType.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static SignedPropertiesType newInstance() {
            return (SignedPropertiesType)getTypeLoader().newInstance(SignedPropertiesType.type, (XmlOptions)null);
        }
        
        public static SignedPropertiesType newInstance(final XmlOptions xmlOptions) {
            return (SignedPropertiesType)getTypeLoader().newInstance(SignedPropertiesType.type, xmlOptions);
        }
        
        public static SignedPropertiesType parse(final String s) throws XmlException {
            return (SignedPropertiesType)getTypeLoader().parse(s, SignedPropertiesType.type, (XmlOptions)null);
        }
        
        public static SignedPropertiesType parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (SignedPropertiesType)getTypeLoader().parse(s, SignedPropertiesType.type, xmlOptions);
        }
        
        public static SignedPropertiesType parse(final File file) throws XmlException, IOException {
            return (SignedPropertiesType)getTypeLoader().parse(file, SignedPropertiesType.type, (XmlOptions)null);
        }
        
        public static SignedPropertiesType parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (SignedPropertiesType)getTypeLoader().parse(file, SignedPropertiesType.type, xmlOptions);
        }
        
        public static SignedPropertiesType parse(final URL url) throws XmlException, IOException {
            return (SignedPropertiesType)getTypeLoader().parse(url, SignedPropertiesType.type, (XmlOptions)null);
        }
        
        public static SignedPropertiesType parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (SignedPropertiesType)getTypeLoader().parse(url, SignedPropertiesType.type, xmlOptions);
        }
        
        public static SignedPropertiesType parse(final InputStream inputStream) throws XmlException, IOException {
            return (SignedPropertiesType)getTypeLoader().parse(inputStream, SignedPropertiesType.type, (XmlOptions)null);
        }
        
        public static SignedPropertiesType parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (SignedPropertiesType)getTypeLoader().parse(inputStream, SignedPropertiesType.type, xmlOptions);
        }
        
        public static SignedPropertiesType parse(final Reader reader) throws XmlException, IOException {
            return (SignedPropertiesType)getTypeLoader().parse(reader, SignedPropertiesType.type, (XmlOptions)null);
        }
        
        public static SignedPropertiesType parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (SignedPropertiesType)getTypeLoader().parse(reader, SignedPropertiesType.type, xmlOptions);
        }
        
        public static SignedPropertiesType parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (SignedPropertiesType)getTypeLoader().parse(xmlStreamReader, SignedPropertiesType.type, (XmlOptions)null);
        }
        
        public static SignedPropertiesType parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (SignedPropertiesType)getTypeLoader().parse(xmlStreamReader, SignedPropertiesType.type, xmlOptions);
        }
        
        public static SignedPropertiesType parse(final Node node) throws XmlException {
            return (SignedPropertiesType)getTypeLoader().parse(node, SignedPropertiesType.type, (XmlOptions)null);
        }
        
        public static SignedPropertiesType parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (SignedPropertiesType)getTypeLoader().parse(node, SignedPropertiesType.type, xmlOptions);
        }
        
        @Deprecated
        public static SignedPropertiesType parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (SignedPropertiesType)getTypeLoader().parse(xmlInputStream, SignedPropertiesType.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static SignedPropertiesType parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (SignedPropertiesType)getTypeLoader().parse(xmlInputStream, SignedPropertiesType.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, SignedPropertiesType.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, SignedPropertiesType.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
