package org.w3.x2000.x09.xmldsig;

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

public interface SignedInfoType extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(SignedInfoType.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.s8C3F193EE11A2F798ACF65489B9E6078").resolveHandle("signedinfotype54dbtype");
    
    CanonicalizationMethodType getCanonicalizationMethod();
    
    void setCanonicalizationMethod(final CanonicalizationMethodType p0);
    
    CanonicalizationMethodType addNewCanonicalizationMethod();
    
    SignatureMethodType getSignatureMethod();
    
    void setSignatureMethod(final SignatureMethodType p0);
    
    SignatureMethodType addNewSignatureMethod();
    
    List<ReferenceType> getReferenceList();
    
    @Deprecated
    ReferenceType[] getReferenceArray();
    
    ReferenceType getReferenceArray(final int p0);
    
    int sizeOfReferenceArray();
    
    void setReferenceArray(final ReferenceType[] p0);
    
    void setReferenceArray(final int p0, final ReferenceType p1);
    
    ReferenceType insertNewReference(final int p0);
    
    ReferenceType addNewReference();
    
    void removeReference(final int p0);
    
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
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(SignedInfoType.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static SignedInfoType newInstance() {
            return (SignedInfoType)getTypeLoader().newInstance(SignedInfoType.type, (XmlOptions)null);
        }
        
        public static SignedInfoType newInstance(final XmlOptions xmlOptions) {
            return (SignedInfoType)getTypeLoader().newInstance(SignedInfoType.type, xmlOptions);
        }
        
        public static SignedInfoType parse(final String s) throws XmlException {
            return (SignedInfoType)getTypeLoader().parse(s, SignedInfoType.type, (XmlOptions)null);
        }
        
        public static SignedInfoType parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (SignedInfoType)getTypeLoader().parse(s, SignedInfoType.type, xmlOptions);
        }
        
        public static SignedInfoType parse(final File file) throws XmlException, IOException {
            return (SignedInfoType)getTypeLoader().parse(file, SignedInfoType.type, (XmlOptions)null);
        }
        
        public static SignedInfoType parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (SignedInfoType)getTypeLoader().parse(file, SignedInfoType.type, xmlOptions);
        }
        
        public static SignedInfoType parse(final URL url) throws XmlException, IOException {
            return (SignedInfoType)getTypeLoader().parse(url, SignedInfoType.type, (XmlOptions)null);
        }
        
        public static SignedInfoType parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (SignedInfoType)getTypeLoader().parse(url, SignedInfoType.type, xmlOptions);
        }
        
        public static SignedInfoType parse(final InputStream inputStream) throws XmlException, IOException {
            return (SignedInfoType)getTypeLoader().parse(inputStream, SignedInfoType.type, (XmlOptions)null);
        }
        
        public static SignedInfoType parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (SignedInfoType)getTypeLoader().parse(inputStream, SignedInfoType.type, xmlOptions);
        }
        
        public static SignedInfoType parse(final Reader reader) throws XmlException, IOException {
            return (SignedInfoType)getTypeLoader().parse(reader, SignedInfoType.type, (XmlOptions)null);
        }
        
        public static SignedInfoType parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (SignedInfoType)getTypeLoader().parse(reader, SignedInfoType.type, xmlOptions);
        }
        
        public static SignedInfoType parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (SignedInfoType)getTypeLoader().parse(xmlStreamReader, SignedInfoType.type, (XmlOptions)null);
        }
        
        public static SignedInfoType parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (SignedInfoType)getTypeLoader().parse(xmlStreamReader, SignedInfoType.type, xmlOptions);
        }
        
        public static SignedInfoType parse(final Node node) throws XmlException {
            return (SignedInfoType)getTypeLoader().parse(node, SignedInfoType.type, (XmlOptions)null);
        }
        
        public static SignedInfoType parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (SignedInfoType)getTypeLoader().parse(node, SignedInfoType.type, xmlOptions);
        }
        
        @Deprecated
        public static SignedInfoType parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (SignedInfoType)getTypeLoader().parse(xmlInputStream, SignedInfoType.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static SignedInfoType parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (SignedInfoType)getTypeLoader().parse(xmlInputStream, SignedInfoType.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, SignedInfoType.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, SignedInfoType.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
