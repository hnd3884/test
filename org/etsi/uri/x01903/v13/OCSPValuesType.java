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
import java.util.List;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface OCSPValuesType extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(OCSPValuesType.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.s8C3F193EE11A2F798ACF65489B9E6078").resolveHandle("ocspvaluestypeb421type");
    
    List<EncapsulatedPKIDataType> getEncapsulatedOCSPValueList();
    
    @Deprecated
    EncapsulatedPKIDataType[] getEncapsulatedOCSPValueArray();
    
    EncapsulatedPKIDataType getEncapsulatedOCSPValueArray(final int p0);
    
    int sizeOfEncapsulatedOCSPValueArray();
    
    void setEncapsulatedOCSPValueArray(final EncapsulatedPKIDataType[] p0);
    
    void setEncapsulatedOCSPValueArray(final int p0, final EncapsulatedPKIDataType p1);
    
    EncapsulatedPKIDataType insertNewEncapsulatedOCSPValue(final int p0);
    
    EncapsulatedPKIDataType addNewEncapsulatedOCSPValue();
    
    void removeEncapsulatedOCSPValue(final int p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(OCSPValuesType.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static OCSPValuesType newInstance() {
            return (OCSPValuesType)getTypeLoader().newInstance(OCSPValuesType.type, (XmlOptions)null);
        }
        
        public static OCSPValuesType newInstance(final XmlOptions xmlOptions) {
            return (OCSPValuesType)getTypeLoader().newInstance(OCSPValuesType.type, xmlOptions);
        }
        
        public static OCSPValuesType parse(final String s) throws XmlException {
            return (OCSPValuesType)getTypeLoader().parse(s, OCSPValuesType.type, (XmlOptions)null);
        }
        
        public static OCSPValuesType parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (OCSPValuesType)getTypeLoader().parse(s, OCSPValuesType.type, xmlOptions);
        }
        
        public static OCSPValuesType parse(final File file) throws XmlException, IOException {
            return (OCSPValuesType)getTypeLoader().parse(file, OCSPValuesType.type, (XmlOptions)null);
        }
        
        public static OCSPValuesType parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (OCSPValuesType)getTypeLoader().parse(file, OCSPValuesType.type, xmlOptions);
        }
        
        public static OCSPValuesType parse(final URL url) throws XmlException, IOException {
            return (OCSPValuesType)getTypeLoader().parse(url, OCSPValuesType.type, (XmlOptions)null);
        }
        
        public static OCSPValuesType parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (OCSPValuesType)getTypeLoader().parse(url, OCSPValuesType.type, xmlOptions);
        }
        
        public static OCSPValuesType parse(final InputStream inputStream) throws XmlException, IOException {
            return (OCSPValuesType)getTypeLoader().parse(inputStream, OCSPValuesType.type, (XmlOptions)null);
        }
        
        public static OCSPValuesType parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (OCSPValuesType)getTypeLoader().parse(inputStream, OCSPValuesType.type, xmlOptions);
        }
        
        public static OCSPValuesType parse(final Reader reader) throws XmlException, IOException {
            return (OCSPValuesType)getTypeLoader().parse(reader, OCSPValuesType.type, (XmlOptions)null);
        }
        
        public static OCSPValuesType parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (OCSPValuesType)getTypeLoader().parse(reader, OCSPValuesType.type, xmlOptions);
        }
        
        public static OCSPValuesType parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (OCSPValuesType)getTypeLoader().parse(xmlStreamReader, OCSPValuesType.type, (XmlOptions)null);
        }
        
        public static OCSPValuesType parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (OCSPValuesType)getTypeLoader().parse(xmlStreamReader, OCSPValuesType.type, xmlOptions);
        }
        
        public static OCSPValuesType parse(final Node node) throws XmlException {
            return (OCSPValuesType)getTypeLoader().parse(node, OCSPValuesType.type, (XmlOptions)null);
        }
        
        public static OCSPValuesType parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (OCSPValuesType)getTypeLoader().parse(node, OCSPValuesType.type, xmlOptions);
        }
        
        @Deprecated
        public static OCSPValuesType parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (OCSPValuesType)getTypeLoader().parse(xmlInputStream, OCSPValuesType.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static OCSPValuesType parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (OCSPValuesType)getTypeLoader().parse(xmlInputStream, OCSPValuesType.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, OCSPValuesType.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, OCSPValuesType.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
