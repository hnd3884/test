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
import org.apache.xmlbeans.XmlAnyURI;
import org.apache.xmlbeans.XmlID;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlBase64Binary;

public interface EncapsulatedPKIDataType extends XmlBase64Binary
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(EncapsulatedPKIDataType.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.s8C3F193EE11A2F798ACF65489B9E6078").resolveHandle("encapsulatedpkidatatype4081type");
    
    String getId();
    
    XmlID xgetId();
    
    boolean isSetId();
    
    void setId(final String p0);
    
    void xsetId(final XmlID p0);
    
    void unsetId();
    
    String getEncoding();
    
    XmlAnyURI xgetEncoding();
    
    boolean isSetEncoding();
    
    void setEncoding(final String p0);
    
    void xsetEncoding(final XmlAnyURI p0);
    
    void unsetEncoding();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(EncapsulatedPKIDataType.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static EncapsulatedPKIDataType newInstance() {
            return (EncapsulatedPKIDataType)getTypeLoader().newInstance(EncapsulatedPKIDataType.type, (XmlOptions)null);
        }
        
        public static EncapsulatedPKIDataType newInstance(final XmlOptions xmlOptions) {
            return (EncapsulatedPKIDataType)getTypeLoader().newInstance(EncapsulatedPKIDataType.type, xmlOptions);
        }
        
        public static EncapsulatedPKIDataType parse(final String s) throws XmlException {
            return (EncapsulatedPKIDataType)getTypeLoader().parse(s, EncapsulatedPKIDataType.type, (XmlOptions)null);
        }
        
        public static EncapsulatedPKIDataType parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (EncapsulatedPKIDataType)getTypeLoader().parse(s, EncapsulatedPKIDataType.type, xmlOptions);
        }
        
        public static EncapsulatedPKIDataType parse(final File file) throws XmlException, IOException {
            return (EncapsulatedPKIDataType)getTypeLoader().parse(file, EncapsulatedPKIDataType.type, (XmlOptions)null);
        }
        
        public static EncapsulatedPKIDataType parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (EncapsulatedPKIDataType)getTypeLoader().parse(file, EncapsulatedPKIDataType.type, xmlOptions);
        }
        
        public static EncapsulatedPKIDataType parse(final URL url) throws XmlException, IOException {
            return (EncapsulatedPKIDataType)getTypeLoader().parse(url, EncapsulatedPKIDataType.type, (XmlOptions)null);
        }
        
        public static EncapsulatedPKIDataType parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (EncapsulatedPKIDataType)getTypeLoader().parse(url, EncapsulatedPKIDataType.type, xmlOptions);
        }
        
        public static EncapsulatedPKIDataType parse(final InputStream inputStream) throws XmlException, IOException {
            return (EncapsulatedPKIDataType)getTypeLoader().parse(inputStream, EncapsulatedPKIDataType.type, (XmlOptions)null);
        }
        
        public static EncapsulatedPKIDataType parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (EncapsulatedPKIDataType)getTypeLoader().parse(inputStream, EncapsulatedPKIDataType.type, xmlOptions);
        }
        
        public static EncapsulatedPKIDataType parse(final Reader reader) throws XmlException, IOException {
            return (EncapsulatedPKIDataType)getTypeLoader().parse(reader, EncapsulatedPKIDataType.type, (XmlOptions)null);
        }
        
        public static EncapsulatedPKIDataType parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (EncapsulatedPKIDataType)getTypeLoader().parse(reader, EncapsulatedPKIDataType.type, xmlOptions);
        }
        
        public static EncapsulatedPKIDataType parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (EncapsulatedPKIDataType)getTypeLoader().parse(xmlStreamReader, EncapsulatedPKIDataType.type, (XmlOptions)null);
        }
        
        public static EncapsulatedPKIDataType parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (EncapsulatedPKIDataType)getTypeLoader().parse(xmlStreamReader, EncapsulatedPKIDataType.type, xmlOptions);
        }
        
        public static EncapsulatedPKIDataType parse(final Node node) throws XmlException {
            return (EncapsulatedPKIDataType)getTypeLoader().parse(node, EncapsulatedPKIDataType.type, (XmlOptions)null);
        }
        
        public static EncapsulatedPKIDataType parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (EncapsulatedPKIDataType)getTypeLoader().parse(node, EncapsulatedPKIDataType.type, xmlOptions);
        }
        
        @Deprecated
        public static EncapsulatedPKIDataType parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (EncapsulatedPKIDataType)getTypeLoader().parse(xmlInputStream, EncapsulatedPKIDataType.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static EncapsulatedPKIDataType parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (EncapsulatedPKIDataType)getTypeLoader().parse(xmlInputStream, EncapsulatedPKIDataType.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, EncapsulatedPKIDataType.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, EncapsulatedPKIDataType.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
