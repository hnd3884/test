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

public interface CRLValuesType extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CRLValuesType.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.s8C3F193EE11A2F798ACF65489B9E6078").resolveHandle("crlvaluestype0ebbtype");
    
    List<EncapsulatedPKIDataType> getEncapsulatedCRLValueList();
    
    @Deprecated
    EncapsulatedPKIDataType[] getEncapsulatedCRLValueArray();
    
    EncapsulatedPKIDataType getEncapsulatedCRLValueArray(final int p0);
    
    int sizeOfEncapsulatedCRLValueArray();
    
    void setEncapsulatedCRLValueArray(final EncapsulatedPKIDataType[] p0);
    
    void setEncapsulatedCRLValueArray(final int p0, final EncapsulatedPKIDataType p1);
    
    EncapsulatedPKIDataType insertNewEncapsulatedCRLValue(final int p0);
    
    EncapsulatedPKIDataType addNewEncapsulatedCRLValue();
    
    void removeEncapsulatedCRLValue(final int p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CRLValuesType.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CRLValuesType newInstance() {
            return (CRLValuesType)getTypeLoader().newInstance(CRLValuesType.type, (XmlOptions)null);
        }
        
        public static CRLValuesType newInstance(final XmlOptions xmlOptions) {
            return (CRLValuesType)getTypeLoader().newInstance(CRLValuesType.type, xmlOptions);
        }
        
        public static CRLValuesType parse(final String s) throws XmlException {
            return (CRLValuesType)getTypeLoader().parse(s, CRLValuesType.type, (XmlOptions)null);
        }
        
        public static CRLValuesType parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CRLValuesType)getTypeLoader().parse(s, CRLValuesType.type, xmlOptions);
        }
        
        public static CRLValuesType parse(final File file) throws XmlException, IOException {
            return (CRLValuesType)getTypeLoader().parse(file, CRLValuesType.type, (XmlOptions)null);
        }
        
        public static CRLValuesType parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CRLValuesType)getTypeLoader().parse(file, CRLValuesType.type, xmlOptions);
        }
        
        public static CRLValuesType parse(final URL url) throws XmlException, IOException {
            return (CRLValuesType)getTypeLoader().parse(url, CRLValuesType.type, (XmlOptions)null);
        }
        
        public static CRLValuesType parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CRLValuesType)getTypeLoader().parse(url, CRLValuesType.type, xmlOptions);
        }
        
        public static CRLValuesType parse(final InputStream inputStream) throws XmlException, IOException {
            return (CRLValuesType)getTypeLoader().parse(inputStream, CRLValuesType.type, (XmlOptions)null);
        }
        
        public static CRLValuesType parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CRLValuesType)getTypeLoader().parse(inputStream, CRLValuesType.type, xmlOptions);
        }
        
        public static CRLValuesType parse(final Reader reader) throws XmlException, IOException {
            return (CRLValuesType)getTypeLoader().parse(reader, CRLValuesType.type, (XmlOptions)null);
        }
        
        public static CRLValuesType parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CRLValuesType)getTypeLoader().parse(reader, CRLValuesType.type, xmlOptions);
        }
        
        public static CRLValuesType parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CRLValuesType)getTypeLoader().parse(xmlStreamReader, CRLValuesType.type, (XmlOptions)null);
        }
        
        public static CRLValuesType parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CRLValuesType)getTypeLoader().parse(xmlStreamReader, CRLValuesType.type, xmlOptions);
        }
        
        public static CRLValuesType parse(final Node node) throws XmlException {
            return (CRLValuesType)getTypeLoader().parse(node, CRLValuesType.type, (XmlOptions)null);
        }
        
        public static CRLValuesType parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CRLValuesType)getTypeLoader().parse(node, CRLValuesType.type, xmlOptions);
        }
        
        @Deprecated
        public static CRLValuesType parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CRLValuesType)getTypeLoader().parse(xmlInputStream, CRLValuesType.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CRLValuesType parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CRLValuesType)getTypeLoader().parse(xmlInputStream, CRLValuesType.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CRLValuesType.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CRLValuesType.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
