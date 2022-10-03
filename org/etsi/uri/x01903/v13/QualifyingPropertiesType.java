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
import org.apache.xmlbeans.XmlAnyURI;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface QualifyingPropertiesType extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(QualifyingPropertiesType.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.s8C3F193EE11A2F798ACF65489B9E6078").resolveHandle("qualifyingpropertiestype9e16type");
    
    SignedPropertiesType getSignedProperties();
    
    boolean isSetSignedProperties();
    
    void setSignedProperties(final SignedPropertiesType p0);
    
    SignedPropertiesType addNewSignedProperties();
    
    void unsetSignedProperties();
    
    UnsignedPropertiesType getUnsignedProperties();
    
    boolean isSetUnsignedProperties();
    
    void setUnsignedProperties(final UnsignedPropertiesType p0);
    
    UnsignedPropertiesType addNewUnsignedProperties();
    
    void unsetUnsignedProperties();
    
    String getTarget();
    
    XmlAnyURI xgetTarget();
    
    void setTarget(final String p0);
    
    void xsetTarget(final XmlAnyURI p0);
    
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
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(QualifyingPropertiesType.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static QualifyingPropertiesType newInstance() {
            return (QualifyingPropertiesType)getTypeLoader().newInstance(QualifyingPropertiesType.type, (XmlOptions)null);
        }
        
        public static QualifyingPropertiesType newInstance(final XmlOptions xmlOptions) {
            return (QualifyingPropertiesType)getTypeLoader().newInstance(QualifyingPropertiesType.type, xmlOptions);
        }
        
        public static QualifyingPropertiesType parse(final String s) throws XmlException {
            return (QualifyingPropertiesType)getTypeLoader().parse(s, QualifyingPropertiesType.type, (XmlOptions)null);
        }
        
        public static QualifyingPropertiesType parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (QualifyingPropertiesType)getTypeLoader().parse(s, QualifyingPropertiesType.type, xmlOptions);
        }
        
        public static QualifyingPropertiesType parse(final File file) throws XmlException, IOException {
            return (QualifyingPropertiesType)getTypeLoader().parse(file, QualifyingPropertiesType.type, (XmlOptions)null);
        }
        
        public static QualifyingPropertiesType parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (QualifyingPropertiesType)getTypeLoader().parse(file, QualifyingPropertiesType.type, xmlOptions);
        }
        
        public static QualifyingPropertiesType parse(final URL url) throws XmlException, IOException {
            return (QualifyingPropertiesType)getTypeLoader().parse(url, QualifyingPropertiesType.type, (XmlOptions)null);
        }
        
        public static QualifyingPropertiesType parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (QualifyingPropertiesType)getTypeLoader().parse(url, QualifyingPropertiesType.type, xmlOptions);
        }
        
        public static QualifyingPropertiesType parse(final InputStream inputStream) throws XmlException, IOException {
            return (QualifyingPropertiesType)getTypeLoader().parse(inputStream, QualifyingPropertiesType.type, (XmlOptions)null);
        }
        
        public static QualifyingPropertiesType parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (QualifyingPropertiesType)getTypeLoader().parse(inputStream, QualifyingPropertiesType.type, xmlOptions);
        }
        
        public static QualifyingPropertiesType parse(final Reader reader) throws XmlException, IOException {
            return (QualifyingPropertiesType)getTypeLoader().parse(reader, QualifyingPropertiesType.type, (XmlOptions)null);
        }
        
        public static QualifyingPropertiesType parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (QualifyingPropertiesType)getTypeLoader().parse(reader, QualifyingPropertiesType.type, xmlOptions);
        }
        
        public static QualifyingPropertiesType parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (QualifyingPropertiesType)getTypeLoader().parse(xmlStreamReader, QualifyingPropertiesType.type, (XmlOptions)null);
        }
        
        public static QualifyingPropertiesType parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (QualifyingPropertiesType)getTypeLoader().parse(xmlStreamReader, QualifyingPropertiesType.type, xmlOptions);
        }
        
        public static QualifyingPropertiesType parse(final Node node) throws XmlException {
            return (QualifyingPropertiesType)getTypeLoader().parse(node, QualifyingPropertiesType.type, (XmlOptions)null);
        }
        
        public static QualifyingPropertiesType parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (QualifyingPropertiesType)getTypeLoader().parse(node, QualifyingPropertiesType.type, xmlOptions);
        }
        
        @Deprecated
        public static QualifyingPropertiesType parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (QualifyingPropertiesType)getTypeLoader().parse(xmlInputStream, QualifyingPropertiesType.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static QualifyingPropertiesType parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (QualifyingPropertiesType)getTypeLoader().parse(xmlInputStream, QualifyingPropertiesType.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, QualifyingPropertiesType.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, QualifyingPropertiesType.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
