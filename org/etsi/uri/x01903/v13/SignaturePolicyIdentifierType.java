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

public interface SignaturePolicyIdentifierType extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(SignaturePolicyIdentifierType.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.s8C3F193EE11A2F798ACF65489B9E6078").resolveHandle("signaturepolicyidentifiertype80aftype");
    
    SignaturePolicyIdType getSignaturePolicyId();
    
    boolean isSetSignaturePolicyId();
    
    void setSignaturePolicyId(final SignaturePolicyIdType p0);
    
    SignaturePolicyIdType addNewSignaturePolicyId();
    
    void unsetSignaturePolicyId();
    
    XmlObject getSignaturePolicyImplied();
    
    boolean isSetSignaturePolicyImplied();
    
    void setSignaturePolicyImplied(final XmlObject p0);
    
    XmlObject addNewSignaturePolicyImplied();
    
    void unsetSignaturePolicyImplied();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(SignaturePolicyIdentifierType.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static SignaturePolicyIdentifierType newInstance() {
            return (SignaturePolicyIdentifierType)getTypeLoader().newInstance(SignaturePolicyIdentifierType.type, (XmlOptions)null);
        }
        
        public static SignaturePolicyIdentifierType newInstance(final XmlOptions xmlOptions) {
            return (SignaturePolicyIdentifierType)getTypeLoader().newInstance(SignaturePolicyIdentifierType.type, xmlOptions);
        }
        
        public static SignaturePolicyIdentifierType parse(final String s) throws XmlException {
            return (SignaturePolicyIdentifierType)getTypeLoader().parse(s, SignaturePolicyIdentifierType.type, (XmlOptions)null);
        }
        
        public static SignaturePolicyIdentifierType parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (SignaturePolicyIdentifierType)getTypeLoader().parse(s, SignaturePolicyIdentifierType.type, xmlOptions);
        }
        
        public static SignaturePolicyIdentifierType parse(final File file) throws XmlException, IOException {
            return (SignaturePolicyIdentifierType)getTypeLoader().parse(file, SignaturePolicyIdentifierType.type, (XmlOptions)null);
        }
        
        public static SignaturePolicyIdentifierType parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (SignaturePolicyIdentifierType)getTypeLoader().parse(file, SignaturePolicyIdentifierType.type, xmlOptions);
        }
        
        public static SignaturePolicyIdentifierType parse(final URL url) throws XmlException, IOException {
            return (SignaturePolicyIdentifierType)getTypeLoader().parse(url, SignaturePolicyIdentifierType.type, (XmlOptions)null);
        }
        
        public static SignaturePolicyIdentifierType parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (SignaturePolicyIdentifierType)getTypeLoader().parse(url, SignaturePolicyIdentifierType.type, xmlOptions);
        }
        
        public static SignaturePolicyIdentifierType parse(final InputStream inputStream) throws XmlException, IOException {
            return (SignaturePolicyIdentifierType)getTypeLoader().parse(inputStream, SignaturePolicyIdentifierType.type, (XmlOptions)null);
        }
        
        public static SignaturePolicyIdentifierType parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (SignaturePolicyIdentifierType)getTypeLoader().parse(inputStream, SignaturePolicyIdentifierType.type, xmlOptions);
        }
        
        public static SignaturePolicyIdentifierType parse(final Reader reader) throws XmlException, IOException {
            return (SignaturePolicyIdentifierType)getTypeLoader().parse(reader, SignaturePolicyIdentifierType.type, (XmlOptions)null);
        }
        
        public static SignaturePolicyIdentifierType parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (SignaturePolicyIdentifierType)getTypeLoader().parse(reader, SignaturePolicyIdentifierType.type, xmlOptions);
        }
        
        public static SignaturePolicyIdentifierType parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (SignaturePolicyIdentifierType)getTypeLoader().parse(xmlStreamReader, SignaturePolicyIdentifierType.type, (XmlOptions)null);
        }
        
        public static SignaturePolicyIdentifierType parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (SignaturePolicyIdentifierType)getTypeLoader().parse(xmlStreamReader, SignaturePolicyIdentifierType.type, xmlOptions);
        }
        
        public static SignaturePolicyIdentifierType parse(final Node node) throws XmlException {
            return (SignaturePolicyIdentifierType)getTypeLoader().parse(node, SignaturePolicyIdentifierType.type, (XmlOptions)null);
        }
        
        public static SignaturePolicyIdentifierType parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (SignaturePolicyIdentifierType)getTypeLoader().parse(node, SignaturePolicyIdentifierType.type, xmlOptions);
        }
        
        @Deprecated
        public static SignaturePolicyIdentifierType parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (SignaturePolicyIdentifierType)getTypeLoader().parse(xmlInputStream, SignaturePolicyIdentifierType.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static SignaturePolicyIdentifierType parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (SignaturePolicyIdentifierType)getTypeLoader().parse(xmlInputStream, SignaturePolicyIdentifierType.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, SignaturePolicyIdentifierType.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, SignaturePolicyIdentifierType.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
