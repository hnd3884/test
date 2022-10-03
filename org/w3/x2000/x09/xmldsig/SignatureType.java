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

public interface SignatureType extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(SignatureType.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.s8C3F193EE11A2F798ACF65489B9E6078").resolveHandle("signaturetype0a3ftype");
    
    SignedInfoType getSignedInfo();
    
    void setSignedInfo(final SignedInfoType p0);
    
    SignedInfoType addNewSignedInfo();
    
    SignatureValueType getSignatureValue();
    
    void setSignatureValue(final SignatureValueType p0);
    
    SignatureValueType addNewSignatureValue();
    
    KeyInfoType getKeyInfo();
    
    boolean isSetKeyInfo();
    
    void setKeyInfo(final KeyInfoType p0);
    
    KeyInfoType addNewKeyInfo();
    
    void unsetKeyInfo();
    
    List<ObjectType> getObjectList();
    
    @Deprecated
    ObjectType[] getObjectArray();
    
    ObjectType getObjectArray(final int p0);
    
    int sizeOfObjectArray();
    
    void setObjectArray(final ObjectType[] p0);
    
    void setObjectArray(final int p0, final ObjectType p1);
    
    ObjectType insertNewObject(final int p0);
    
    ObjectType addNewObject();
    
    void removeObject(final int p0);
    
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
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(SignatureType.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static SignatureType newInstance() {
            return (SignatureType)getTypeLoader().newInstance(SignatureType.type, (XmlOptions)null);
        }
        
        public static SignatureType newInstance(final XmlOptions xmlOptions) {
            return (SignatureType)getTypeLoader().newInstance(SignatureType.type, xmlOptions);
        }
        
        public static SignatureType parse(final String s) throws XmlException {
            return (SignatureType)getTypeLoader().parse(s, SignatureType.type, (XmlOptions)null);
        }
        
        public static SignatureType parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (SignatureType)getTypeLoader().parse(s, SignatureType.type, xmlOptions);
        }
        
        public static SignatureType parse(final File file) throws XmlException, IOException {
            return (SignatureType)getTypeLoader().parse(file, SignatureType.type, (XmlOptions)null);
        }
        
        public static SignatureType parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (SignatureType)getTypeLoader().parse(file, SignatureType.type, xmlOptions);
        }
        
        public static SignatureType parse(final URL url) throws XmlException, IOException {
            return (SignatureType)getTypeLoader().parse(url, SignatureType.type, (XmlOptions)null);
        }
        
        public static SignatureType parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (SignatureType)getTypeLoader().parse(url, SignatureType.type, xmlOptions);
        }
        
        public static SignatureType parse(final InputStream inputStream) throws XmlException, IOException {
            return (SignatureType)getTypeLoader().parse(inputStream, SignatureType.type, (XmlOptions)null);
        }
        
        public static SignatureType parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (SignatureType)getTypeLoader().parse(inputStream, SignatureType.type, xmlOptions);
        }
        
        public static SignatureType parse(final Reader reader) throws XmlException, IOException {
            return (SignatureType)getTypeLoader().parse(reader, SignatureType.type, (XmlOptions)null);
        }
        
        public static SignatureType parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (SignatureType)getTypeLoader().parse(reader, SignatureType.type, xmlOptions);
        }
        
        public static SignatureType parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (SignatureType)getTypeLoader().parse(xmlStreamReader, SignatureType.type, (XmlOptions)null);
        }
        
        public static SignatureType parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (SignatureType)getTypeLoader().parse(xmlStreamReader, SignatureType.type, xmlOptions);
        }
        
        public static SignatureType parse(final Node node) throws XmlException {
            return (SignatureType)getTypeLoader().parse(node, SignatureType.type, (XmlOptions)null);
        }
        
        public static SignatureType parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (SignatureType)getTypeLoader().parse(node, SignatureType.type, xmlOptions);
        }
        
        @Deprecated
        public static SignatureType parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (SignatureType)getTypeLoader().parse(xmlInputStream, SignatureType.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static SignatureType parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (SignatureType)getTypeLoader().parse(xmlInputStream, SignatureType.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, SignatureType.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, SignatureType.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
