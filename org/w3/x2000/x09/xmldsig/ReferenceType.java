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
import org.apache.xmlbeans.XmlAnyURI;
import org.apache.xmlbeans.XmlID;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface ReferenceType extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(ReferenceType.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.s8C3F193EE11A2F798ACF65489B9E6078").resolveHandle("referencetypef44ctype");
    
    TransformsType getTransforms();
    
    boolean isSetTransforms();
    
    void setTransforms(final TransformsType p0);
    
    TransformsType addNewTransforms();
    
    void unsetTransforms();
    
    DigestMethodType getDigestMethod();
    
    void setDigestMethod(final DigestMethodType p0);
    
    DigestMethodType addNewDigestMethod();
    
    byte[] getDigestValue();
    
    DigestValueType xgetDigestValue();
    
    void setDigestValue(final byte[] p0);
    
    void xsetDigestValue(final DigestValueType p0);
    
    String getId();
    
    XmlID xgetId();
    
    boolean isSetId();
    
    void setId(final String p0);
    
    void xsetId(final XmlID p0);
    
    void unsetId();
    
    String getURI();
    
    XmlAnyURI xgetURI();
    
    boolean isSetURI();
    
    void setURI(final String p0);
    
    void xsetURI(final XmlAnyURI p0);
    
    void unsetURI();
    
    String getType();
    
    XmlAnyURI xgetType();
    
    boolean isSetType();
    
    void setType(final String p0);
    
    void xsetType(final XmlAnyURI p0);
    
    void unsetType();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(ReferenceType.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static ReferenceType newInstance() {
            return (ReferenceType)getTypeLoader().newInstance(ReferenceType.type, (XmlOptions)null);
        }
        
        public static ReferenceType newInstance(final XmlOptions xmlOptions) {
            return (ReferenceType)getTypeLoader().newInstance(ReferenceType.type, xmlOptions);
        }
        
        public static ReferenceType parse(final String s) throws XmlException {
            return (ReferenceType)getTypeLoader().parse(s, ReferenceType.type, (XmlOptions)null);
        }
        
        public static ReferenceType parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (ReferenceType)getTypeLoader().parse(s, ReferenceType.type, xmlOptions);
        }
        
        public static ReferenceType parse(final File file) throws XmlException, IOException {
            return (ReferenceType)getTypeLoader().parse(file, ReferenceType.type, (XmlOptions)null);
        }
        
        public static ReferenceType parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (ReferenceType)getTypeLoader().parse(file, ReferenceType.type, xmlOptions);
        }
        
        public static ReferenceType parse(final URL url) throws XmlException, IOException {
            return (ReferenceType)getTypeLoader().parse(url, ReferenceType.type, (XmlOptions)null);
        }
        
        public static ReferenceType parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (ReferenceType)getTypeLoader().parse(url, ReferenceType.type, xmlOptions);
        }
        
        public static ReferenceType parse(final InputStream inputStream) throws XmlException, IOException {
            return (ReferenceType)getTypeLoader().parse(inputStream, ReferenceType.type, (XmlOptions)null);
        }
        
        public static ReferenceType parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (ReferenceType)getTypeLoader().parse(inputStream, ReferenceType.type, xmlOptions);
        }
        
        public static ReferenceType parse(final Reader reader) throws XmlException, IOException {
            return (ReferenceType)getTypeLoader().parse(reader, ReferenceType.type, (XmlOptions)null);
        }
        
        public static ReferenceType parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (ReferenceType)getTypeLoader().parse(reader, ReferenceType.type, xmlOptions);
        }
        
        public static ReferenceType parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (ReferenceType)getTypeLoader().parse(xmlStreamReader, ReferenceType.type, (XmlOptions)null);
        }
        
        public static ReferenceType parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (ReferenceType)getTypeLoader().parse(xmlStreamReader, ReferenceType.type, xmlOptions);
        }
        
        public static ReferenceType parse(final Node node) throws XmlException {
            return (ReferenceType)getTypeLoader().parse(node, ReferenceType.type, (XmlOptions)null);
        }
        
        public static ReferenceType parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (ReferenceType)getTypeLoader().parse(node, ReferenceType.type, xmlOptions);
        }
        
        @Deprecated
        public static ReferenceType parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (ReferenceType)getTypeLoader().parse(xmlInputStream, ReferenceType.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static ReferenceType parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (ReferenceType)getTypeLoader().parse(xmlInputStream, ReferenceType.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, ReferenceType.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, ReferenceType.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
