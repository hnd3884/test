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

public interface UnsignedPropertiesType extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(UnsignedPropertiesType.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.s8C3F193EE11A2F798ACF65489B9E6078").resolveHandle("unsignedpropertiestype49d6type");
    
    UnsignedSignaturePropertiesType getUnsignedSignatureProperties();
    
    boolean isSetUnsignedSignatureProperties();
    
    void setUnsignedSignatureProperties(final UnsignedSignaturePropertiesType p0);
    
    UnsignedSignaturePropertiesType addNewUnsignedSignatureProperties();
    
    void unsetUnsignedSignatureProperties();
    
    UnsignedDataObjectPropertiesType getUnsignedDataObjectProperties();
    
    boolean isSetUnsignedDataObjectProperties();
    
    void setUnsignedDataObjectProperties(final UnsignedDataObjectPropertiesType p0);
    
    UnsignedDataObjectPropertiesType addNewUnsignedDataObjectProperties();
    
    void unsetUnsignedDataObjectProperties();
    
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
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(UnsignedPropertiesType.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static UnsignedPropertiesType newInstance() {
            return (UnsignedPropertiesType)getTypeLoader().newInstance(UnsignedPropertiesType.type, (XmlOptions)null);
        }
        
        public static UnsignedPropertiesType newInstance(final XmlOptions xmlOptions) {
            return (UnsignedPropertiesType)getTypeLoader().newInstance(UnsignedPropertiesType.type, xmlOptions);
        }
        
        public static UnsignedPropertiesType parse(final String s) throws XmlException {
            return (UnsignedPropertiesType)getTypeLoader().parse(s, UnsignedPropertiesType.type, (XmlOptions)null);
        }
        
        public static UnsignedPropertiesType parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (UnsignedPropertiesType)getTypeLoader().parse(s, UnsignedPropertiesType.type, xmlOptions);
        }
        
        public static UnsignedPropertiesType parse(final File file) throws XmlException, IOException {
            return (UnsignedPropertiesType)getTypeLoader().parse(file, UnsignedPropertiesType.type, (XmlOptions)null);
        }
        
        public static UnsignedPropertiesType parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (UnsignedPropertiesType)getTypeLoader().parse(file, UnsignedPropertiesType.type, xmlOptions);
        }
        
        public static UnsignedPropertiesType parse(final URL url) throws XmlException, IOException {
            return (UnsignedPropertiesType)getTypeLoader().parse(url, UnsignedPropertiesType.type, (XmlOptions)null);
        }
        
        public static UnsignedPropertiesType parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (UnsignedPropertiesType)getTypeLoader().parse(url, UnsignedPropertiesType.type, xmlOptions);
        }
        
        public static UnsignedPropertiesType parse(final InputStream inputStream) throws XmlException, IOException {
            return (UnsignedPropertiesType)getTypeLoader().parse(inputStream, UnsignedPropertiesType.type, (XmlOptions)null);
        }
        
        public static UnsignedPropertiesType parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (UnsignedPropertiesType)getTypeLoader().parse(inputStream, UnsignedPropertiesType.type, xmlOptions);
        }
        
        public static UnsignedPropertiesType parse(final Reader reader) throws XmlException, IOException {
            return (UnsignedPropertiesType)getTypeLoader().parse(reader, UnsignedPropertiesType.type, (XmlOptions)null);
        }
        
        public static UnsignedPropertiesType parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (UnsignedPropertiesType)getTypeLoader().parse(reader, UnsignedPropertiesType.type, xmlOptions);
        }
        
        public static UnsignedPropertiesType parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (UnsignedPropertiesType)getTypeLoader().parse(xmlStreamReader, UnsignedPropertiesType.type, (XmlOptions)null);
        }
        
        public static UnsignedPropertiesType parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (UnsignedPropertiesType)getTypeLoader().parse(xmlStreamReader, UnsignedPropertiesType.type, xmlOptions);
        }
        
        public static UnsignedPropertiesType parse(final Node node) throws XmlException {
            return (UnsignedPropertiesType)getTypeLoader().parse(node, UnsignedPropertiesType.type, (XmlOptions)null);
        }
        
        public static UnsignedPropertiesType parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (UnsignedPropertiesType)getTypeLoader().parse(node, UnsignedPropertiesType.type, xmlOptions);
        }
        
        @Deprecated
        public static UnsignedPropertiesType parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (UnsignedPropertiesType)getTypeLoader().parse(xmlInputStream, UnsignedPropertiesType.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static UnsignedPropertiesType parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (UnsignedPropertiesType)getTypeLoader().parse(xmlInputStream, UnsignedPropertiesType.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, UnsignedPropertiesType.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, UnsignedPropertiesType.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
