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
import org.w3.x2000.x09.xmldsig.DigestValueType;
import org.w3.x2000.x09.xmldsig.DigestMethodType;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface DigestAlgAndValueType extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(DigestAlgAndValueType.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.s8C3F193EE11A2F798ACF65489B9E6078").resolveHandle("digestalgandvaluetype234etype");
    
    DigestMethodType getDigestMethod();
    
    void setDigestMethod(final DigestMethodType p0);
    
    DigestMethodType addNewDigestMethod();
    
    byte[] getDigestValue();
    
    DigestValueType xgetDigestValue();
    
    void setDigestValue(final byte[] p0);
    
    void xsetDigestValue(final DigestValueType p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(DigestAlgAndValueType.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static DigestAlgAndValueType newInstance() {
            return (DigestAlgAndValueType)getTypeLoader().newInstance(DigestAlgAndValueType.type, (XmlOptions)null);
        }
        
        public static DigestAlgAndValueType newInstance(final XmlOptions xmlOptions) {
            return (DigestAlgAndValueType)getTypeLoader().newInstance(DigestAlgAndValueType.type, xmlOptions);
        }
        
        public static DigestAlgAndValueType parse(final String s) throws XmlException {
            return (DigestAlgAndValueType)getTypeLoader().parse(s, DigestAlgAndValueType.type, (XmlOptions)null);
        }
        
        public static DigestAlgAndValueType parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (DigestAlgAndValueType)getTypeLoader().parse(s, DigestAlgAndValueType.type, xmlOptions);
        }
        
        public static DigestAlgAndValueType parse(final File file) throws XmlException, IOException {
            return (DigestAlgAndValueType)getTypeLoader().parse(file, DigestAlgAndValueType.type, (XmlOptions)null);
        }
        
        public static DigestAlgAndValueType parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (DigestAlgAndValueType)getTypeLoader().parse(file, DigestAlgAndValueType.type, xmlOptions);
        }
        
        public static DigestAlgAndValueType parse(final URL url) throws XmlException, IOException {
            return (DigestAlgAndValueType)getTypeLoader().parse(url, DigestAlgAndValueType.type, (XmlOptions)null);
        }
        
        public static DigestAlgAndValueType parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (DigestAlgAndValueType)getTypeLoader().parse(url, DigestAlgAndValueType.type, xmlOptions);
        }
        
        public static DigestAlgAndValueType parse(final InputStream inputStream) throws XmlException, IOException {
            return (DigestAlgAndValueType)getTypeLoader().parse(inputStream, DigestAlgAndValueType.type, (XmlOptions)null);
        }
        
        public static DigestAlgAndValueType parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (DigestAlgAndValueType)getTypeLoader().parse(inputStream, DigestAlgAndValueType.type, xmlOptions);
        }
        
        public static DigestAlgAndValueType parse(final Reader reader) throws XmlException, IOException {
            return (DigestAlgAndValueType)getTypeLoader().parse(reader, DigestAlgAndValueType.type, (XmlOptions)null);
        }
        
        public static DigestAlgAndValueType parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (DigestAlgAndValueType)getTypeLoader().parse(reader, DigestAlgAndValueType.type, xmlOptions);
        }
        
        public static DigestAlgAndValueType parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (DigestAlgAndValueType)getTypeLoader().parse(xmlStreamReader, DigestAlgAndValueType.type, (XmlOptions)null);
        }
        
        public static DigestAlgAndValueType parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (DigestAlgAndValueType)getTypeLoader().parse(xmlStreamReader, DigestAlgAndValueType.type, xmlOptions);
        }
        
        public static DigestAlgAndValueType parse(final Node node) throws XmlException {
            return (DigestAlgAndValueType)getTypeLoader().parse(node, DigestAlgAndValueType.type, (XmlOptions)null);
        }
        
        public static DigestAlgAndValueType parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (DigestAlgAndValueType)getTypeLoader().parse(node, DigestAlgAndValueType.type, xmlOptions);
        }
        
        @Deprecated
        public static DigestAlgAndValueType parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (DigestAlgAndValueType)getTypeLoader().parse(xmlInputStream, DigestAlgAndValueType.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static DigestAlgAndValueType parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (DigestAlgAndValueType)getTypeLoader().parse(xmlInputStream, DigestAlgAndValueType.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, DigestAlgAndValueType.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, DigestAlgAndValueType.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
