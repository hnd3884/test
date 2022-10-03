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
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTCommonSlideData extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTCommonSlideData.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctcommonslidedata8c7ftype");
    
    CTBackground getBg();
    
    boolean isSetBg();
    
    void setBg(final CTBackground p0);
    
    CTBackground addNewBg();
    
    void unsetBg();
    
    CTGroupShape getSpTree();
    
    void setSpTree(final CTGroupShape p0);
    
    CTGroupShape addNewSpTree();
    
    CTCustomerDataList getCustDataLst();
    
    boolean isSetCustDataLst();
    
    void setCustDataLst(final CTCustomerDataList p0);
    
    CTCustomerDataList addNewCustDataLst();
    
    void unsetCustDataLst();
    
    CTControlList getControls();
    
    boolean isSetControls();
    
    void setControls(final CTControlList p0);
    
    CTControlList addNewControls();
    
    void unsetControls();
    
    CTExtensionList getExtLst();
    
    boolean isSetExtLst();
    
    void setExtLst(final CTExtensionList p0);
    
    CTExtensionList addNewExtLst();
    
    void unsetExtLst();
    
    String getName();
    
    XmlString xgetName();
    
    boolean isSetName();
    
    void setName(final String p0);
    
    void xsetName(final XmlString p0);
    
    void unsetName();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTCommonSlideData.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTCommonSlideData newInstance() {
            return (CTCommonSlideData)getTypeLoader().newInstance(CTCommonSlideData.type, (XmlOptions)null);
        }
        
        public static CTCommonSlideData newInstance(final XmlOptions xmlOptions) {
            return (CTCommonSlideData)getTypeLoader().newInstance(CTCommonSlideData.type, xmlOptions);
        }
        
        public static CTCommonSlideData parse(final String s) throws XmlException {
            return (CTCommonSlideData)getTypeLoader().parse(s, CTCommonSlideData.type, (XmlOptions)null);
        }
        
        public static CTCommonSlideData parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTCommonSlideData)getTypeLoader().parse(s, CTCommonSlideData.type, xmlOptions);
        }
        
        public static CTCommonSlideData parse(final File file) throws XmlException, IOException {
            return (CTCommonSlideData)getTypeLoader().parse(file, CTCommonSlideData.type, (XmlOptions)null);
        }
        
        public static CTCommonSlideData parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTCommonSlideData)getTypeLoader().parse(file, CTCommonSlideData.type, xmlOptions);
        }
        
        public static CTCommonSlideData parse(final URL url) throws XmlException, IOException {
            return (CTCommonSlideData)getTypeLoader().parse(url, CTCommonSlideData.type, (XmlOptions)null);
        }
        
        public static CTCommonSlideData parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTCommonSlideData)getTypeLoader().parse(url, CTCommonSlideData.type, xmlOptions);
        }
        
        public static CTCommonSlideData parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTCommonSlideData)getTypeLoader().parse(inputStream, CTCommonSlideData.type, (XmlOptions)null);
        }
        
        public static CTCommonSlideData parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTCommonSlideData)getTypeLoader().parse(inputStream, CTCommonSlideData.type, xmlOptions);
        }
        
        public static CTCommonSlideData parse(final Reader reader) throws XmlException, IOException {
            return (CTCommonSlideData)getTypeLoader().parse(reader, CTCommonSlideData.type, (XmlOptions)null);
        }
        
        public static CTCommonSlideData parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTCommonSlideData)getTypeLoader().parse(reader, CTCommonSlideData.type, xmlOptions);
        }
        
        public static CTCommonSlideData parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTCommonSlideData)getTypeLoader().parse(xmlStreamReader, CTCommonSlideData.type, (XmlOptions)null);
        }
        
        public static CTCommonSlideData parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTCommonSlideData)getTypeLoader().parse(xmlStreamReader, CTCommonSlideData.type, xmlOptions);
        }
        
        public static CTCommonSlideData parse(final Node node) throws XmlException {
            return (CTCommonSlideData)getTypeLoader().parse(node, CTCommonSlideData.type, (XmlOptions)null);
        }
        
        public static CTCommonSlideData parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTCommonSlideData)getTypeLoader().parse(node, CTCommonSlideData.type, xmlOptions);
        }
        
        @Deprecated
        public static CTCommonSlideData parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTCommonSlideData)getTypeLoader().parse(xmlInputStream, CTCommonSlideData.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTCommonSlideData parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTCommonSlideData)getTypeLoader().parse(xmlInputStream, CTCommonSlideData.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTCommonSlideData.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTCommonSlideData.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
