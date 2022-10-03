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
import org.w3.x2000.x09.xmldsig.CanonicalizationMethodType;
import java.util.List;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface GenericTimeStampType extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(GenericTimeStampType.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.s8C3F193EE11A2F798ACF65489B9E6078").resolveHandle("generictimestamptypecdadtype");
    
    List<IncludeType> getIncludeList();
    
    @Deprecated
    IncludeType[] getIncludeArray();
    
    IncludeType getIncludeArray(final int p0);
    
    int sizeOfIncludeArray();
    
    void setIncludeArray(final IncludeType[] p0);
    
    void setIncludeArray(final int p0, final IncludeType p1);
    
    IncludeType insertNewInclude(final int p0);
    
    IncludeType addNewInclude();
    
    void removeInclude(final int p0);
    
    List<ReferenceInfoType> getReferenceInfoList();
    
    @Deprecated
    ReferenceInfoType[] getReferenceInfoArray();
    
    ReferenceInfoType getReferenceInfoArray(final int p0);
    
    int sizeOfReferenceInfoArray();
    
    void setReferenceInfoArray(final ReferenceInfoType[] p0);
    
    void setReferenceInfoArray(final int p0, final ReferenceInfoType p1);
    
    ReferenceInfoType insertNewReferenceInfo(final int p0);
    
    ReferenceInfoType addNewReferenceInfo();
    
    void removeReferenceInfo(final int p0);
    
    CanonicalizationMethodType getCanonicalizationMethod();
    
    boolean isSetCanonicalizationMethod();
    
    void setCanonicalizationMethod(final CanonicalizationMethodType p0);
    
    CanonicalizationMethodType addNewCanonicalizationMethod();
    
    void unsetCanonicalizationMethod();
    
    List<EncapsulatedPKIDataType> getEncapsulatedTimeStampList();
    
    @Deprecated
    EncapsulatedPKIDataType[] getEncapsulatedTimeStampArray();
    
    EncapsulatedPKIDataType getEncapsulatedTimeStampArray(final int p0);
    
    int sizeOfEncapsulatedTimeStampArray();
    
    void setEncapsulatedTimeStampArray(final EncapsulatedPKIDataType[] p0);
    
    void setEncapsulatedTimeStampArray(final int p0, final EncapsulatedPKIDataType p1);
    
    EncapsulatedPKIDataType insertNewEncapsulatedTimeStamp(final int p0);
    
    EncapsulatedPKIDataType addNewEncapsulatedTimeStamp();
    
    void removeEncapsulatedTimeStamp(final int p0);
    
    List<AnyType> getXMLTimeStampList();
    
    @Deprecated
    AnyType[] getXMLTimeStampArray();
    
    AnyType getXMLTimeStampArray(final int p0);
    
    int sizeOfXMLTimeStampArray();
    
    void setXMLTimeStampArray(final AnyType[] p0);
    
    void setXMLTimeStampArray(final int p0, final AnyType p1);
    
    AnyType insertNewXMLTimeStamp(final int p0);
    
    AnyType addNewXMLTimeStamp();
    
    void removeXMLTimeStamp(final int p0);
    
    String getId();
    
    XmlID xgetId();
    
    boolean isSetId();
    
    void setId(final String p0);
    
    void xsetId(final XmlID p0);
    
    void unsetId();
    
    public static final class Factory
    {
        @Deprecated
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(GenericTimeStampType.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static GenericTimeStampType newInstance() {
            return (GenericTimeStampType)getTypeLoader().newInstance(GenericTimeStampType.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static GenericTimeStampType newInstance(final XmlOptions xmlOptions) {
            return (GenericTimeStampType)getTypeLoader().newInstance(GenericTimeStampType.type, xmlOptions);
        }
        
        public static GenericTimeStampType parse(final String s) throws XmlException {
            return (GenericTimeStampType)getTypeLoader().parse(s, GenericTimeStampType.type, (XmlOptions)null);
        }
        
        public static GenericTimeStampType parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (GenericTimeStampType)getTypeLoader().parse(s, GenericTimeStampType.type, xmlOptions);
        }
        
        public static GenericTimeStampType parse(final File file) throws XmlException, IOException {
            return (GenericTimeStampType)getTypeLoader().parse(file, GenericTimeStampType.type, (XmlOptions)null);
        }
        
        public static GenericTimeStampType parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (GenericTimeStampType)getTypeLoader().parse(file, GenericTimeStampType.type, xmlOptions);
        }
        
        public static GenericTimeStampType parse(final URL url) throws XmlException, IOException {
            return (GenericTimeStampType)getTypeLoader().parse(url, GenericTimeStampType.type, (XmlOptions)null);
        }
        
        public static GenericTimeStampType parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (GenericTimeStampType)getTypeLoader().parse(url, GenericTimeStampType.type, xmlOptions);
        }
        
        public static GenericTimeStampType parse(final InputStream inputStream) throws XmlException, IOException {
            return (GenericTimeStampType)getTypeLoader().parse(inputStream, GenericTimeStampType.type, (XmlOptions)null);
        }
        
        public static GenericTimeStampType parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (GenericTimeStampType)getTypeLoader().parse(inputStream, GenericTimeStampType.type, xmlOptions);
        }
        
        public static GenericTimeStampType parse(final Reader reader) throws XmlException, IOException {
            return (GenericTimeStampType)getTypeLoader().parse(reader, GenericTimeStampType.type, (XmlOptions)null);
        }
        
        public static GenericTimeStampType parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (GenericTimeStampType)getTypeLoader().parse(reader, GenericTimeStampType.type, xmlOptions);
        }
        
        public static GenericTimeStampType parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (GenericTimeStampType)getTypeLoader().parse(xmlStreamReader, GenericTimeStampType.type, (XmlOptions)null);
        }
        
        public static GenericTimeStampType parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (GenericTimeStampType)getTypeLoader().parse(xmlStreamReader, GenericTimeStampType.type, xmlOptions);
        }
        
        public static GenericTimeStampType parse(final Node node) throws XmlException {
            return (GenericTimeStampType)getTypeLoader().parse(node, GenericTimeStampType.type, (XmlOptions)null);
        }
        
        public static GenericTimeStampType parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (GenericTimeStampType)getTypeLoader().parse(node, GenericTimeStampType.type, xmlOptions);
        }
        
        @Deprecated
        public static GenericTimeStampType parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (GenericTimeStampType)getTypeLoader().parse(xmlInputStream, GenericTimeStampType.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static GenericTimeStampType parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (GenericTimeStampType)getTypeLoader().parse(xmlInputStream, GenericTimeStampType.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, GenericTimeStampType.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, GenericTimeStampType.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
