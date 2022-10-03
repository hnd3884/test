package org.openxmlformats.schemas.presentationml.x2006.main;

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

public interface CTCustomerDataList extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTCustomerDataList.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctcustomerdatalist8b7ftype");
    
    List<CTCustomerData> getCustDataList();
    
    @Deprecated
    CTCustomerData[] getCustDataArray();
    
    CTCustomerData getCustDataArray(final int p0);
    
    int sizeOfCustDataArray();
    
    void setCustDataArray(final CTCustomerData[] p0);
    
    void setCustDataArray(final int p0, final CTCustomerData p1);
    
    CTCustomerData insertNewCustData(final int p0);
    
    CTCustomerData addNewCustData();
    
    void removeCustData(final int p0);
    
    CTTagsData getTags();
    
    boolean isSetTags();
    
    void setTags(final CTTagsData p0);
    
    CTTagsData addNewTags();
    
    void unsetTags();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTCustomerDataList.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTCustomerDataList newInstance() {
            return (CTCustomerDataList)getTypeLoader().newInstance(CTCustomerDataList.type, (XmlOptions)null);
        }
        
        public static CTCustomerDataList newInstance(final XmlOptions xmlOptions) {
            return (CTCustomerDataList)getTypeLoader().newInstance(CTCustomerDataList.type, xmlOptions);
        }
        
        public static CTCustomerDataList parse(final String s) throws XmlException {
            return (CTCustomerDataList)getTypeLoader().parse(s, CTCustomerDataList.type, (XmlOptions)null);
        }
        
        public static CTCustomerDataList parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTCustomerDataList)getTypeLoader().parse(s, CTCustomerDataList.type, xmlOptions);
        }
        
        public static CTCustomerDataList parse(final File file) throws XmlException, IOException {
            return (CTCustomerDataList)getTypeLoader().parse(file, CTCustomerDataList.type, (XmlOptions)null);
        }
        
        public static CTCustomerDataList parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTCustomerDataList)getTypeLoader().parse(file, CTCustomerDataList.type, xmlOptions);
        }
        
        public static CTCustomerDataList parse(final URL url) throws XmlException, IOException {
            return (CTCustomerDataList)getTypeLoader().parse(url, CTCustomerDataList.type, (XmlOptions)null);
        }
        
        public static CTCustomerDataList parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTCustomerDataList)getTypeLoader().parse(url, CTCustomerDataList.type, xmlOptions);
        }
        
        public static CTCustomerDataList parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTCustomerDataList)getTypeLoader().parse(inputStream, CTCustomerDataList.type, (XmlOptions)null);
        }
        
        public static CTCustomerDataList parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTCustomerDataList)getTypeLoader().parse(inputStream, CTCustomerDataList.type, xmlOptions);
        }
        
        public static CTCustomerDataList parse(final Reader reader) throws XmlException, IOException {
            return (CTCustomerDataList)getTypeLoader().parse(reader, CTCustomerDataList.type, (XmlOptions)null);
        }
        
        public static CTCustomerDataList parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTCustomerDataList)getTypeLoader().parse(reader, CTCustomerDataList.type, xmlOptions);
        }
        
        public static CTCustomerDataList parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTCustomerDataList)getTypeLoader().parse(xmlStreamReader, CTCustomerDataList.type, (XmlOptions)null);
        }
        
        public static CTCustomerDataList parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTCustomerDataList)getTypeLoader().parse(xmlStreamReader, CTCustomerDataList.type, xmlOptions);
        }
        
        public static CTCustomerDataList parse(final Node node) throws XmlException {
            return (CTCustomerDataList)getTypeLoader().parse(node, CTCustomerDataList.type, (XmlOptions)null);
        }
        
        public static CTCustomerDataList parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTCustomerDataList)getTypeLoader().parse(node, CTCustomerDataList.type, xmlOptions);
        }
        
        @Deprecated
        public static CTCustomerDataList parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTCustomerDataList)getTypeLoader().parse(xmlInputStream, CTCustomerDataList.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTCustomerDataList parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTCustomerDataList)getTypeLoader().parse(xmlInputStream, CTCustomerDataList.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTCustomerDataList.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTCustomerDataList.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
