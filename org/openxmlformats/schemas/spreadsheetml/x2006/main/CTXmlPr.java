package org.openxmlformats.schemas.spreadsheetml.x2006.main;

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
import org.apache.xmlbeans.XmlUnsignedInt;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTXmlPr extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTXmlPr.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctxmlpr2c58type");
    
    CTExtensionList getExtLst();
    
    boolean isSetExtLst();
    
    void setExtLst(final CTExtensionList p0);
    
    CTExtensionList addNewExtLst();
    
    void unsetExtLst();
    
    long getMapId();
    
    XmlUnsignedInt xgetMapId();
    
    void setMapId(final long p0);
    
    void xsetMapId(final XmlUnsignedInt p0);
    
    String getXpath();
    
    STXstring xgetXpath();
    
    void setXpath(final String p0);
    
    void xsetXpath(final STXstring p0);
    
    STXmlDataType.Enum getXmlDataType();
    
    STXmlDataType xgetXmlDataType();
    
    void setXmlDataType(final STXmlDataType.Enum p0);
    
    void xsetXmlDataType(final STXmlDataType p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTXmlPr.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTXmlPr newInstance() {
            return (CTXmlPr)getTypeLoader().newInstance(CTXmlPr.type, (XmlOptions)null);
        }
        
        public static CTXmlPr newInstance(final XmlOptions xmlOptions) {
            return (CTXmlPr)getTypeLoader().newInstance(CTXmlPr.type, xmlOptions);
        }
        
        public static CTXmlPr parse(final String s) throws XmlException {
            return (CTXmlPr)getTypeLoader().parse(s, CTXmlPr.type, (XmlOptions)null);
        }
        
        public static CTXmlPr parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTXmlPr)getTypeLoader().parse(s, CTXmlPr.type, xmlOptions);
        }
        
        public static CTXmlPr parse(final File file) throws XmlException, IOException {
            return (CTXmlPr)getTypeLoader().parse(file, CTXmlPr.type, (XmlOptions)null);
        }
        
        public static CTXmlPr parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTXmlPr)getTypeLoader().parse(file, CTXmlPr.type, xmlOptions);
        }
        
        public static CTXmlPr parse(final URL url) throws XmlException, IOException {
            return (CTXmlPr)getTypeLoader().parse(url, CTXmlPr.type, (XmlOptions)null);
        }
        
        public static CTXmlPr parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTXmlPr)getTypeLoader().parse(url, CTXmlPr.type, xmlOptions);
        }
        
        public static CTXmlPr parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTXmlPr)getTypeLoader().parse(inputStream, CTXmlPr.type, (XmlOptions)null);
        }
        
        public static CTXmlPr parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTXmlPr)getTypeLoader().parse(inputStream, CTXmlPr.type, xmlOptions);
        }
        
        public static CTXmlPr parse(final Reader reader) throws XmlException, IOException {
            return (CTXmlPr)getTypeLoader().parse(reader, CTXmlPr.type, (XmlOptions)null);
        }
        
        public static CTXmlPr parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTXmlPr)getTypeLoader().parse(reader, CTXmlPr.type, xmlOptions);
        }
        
        public static CTXmlPr parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTXmlPr)getTypeLoader().parse(xmlStreamReader, CTXmlPr.type, (XmlOptions)null);
        }
        
        public static CTXmlPr parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTXmlPr)getTypeLoader().parse(xmlStreamReader, CTXmlPr.type, xmlOptions);
        }
        
        public static CTXmlPr parse(final Node node) throws XmlException {
            return (CTXmlPr)getTypeLoader().parse(node, CTXmlPr.type, (XmlOptions)null);
        }
        
        public static CTXmlPr parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTXmlPr)getTypeLoader().parse(node, CTXmlPr.type, xmlOptions);
        }
        
        @Deprecated
        public static CTXmlPr parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTXmlPr)getTypeLoader().parse(xmlInputStream, CTXmlPr.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTXmlPr parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTXmlPr)getTypeLoader().parse(xmlInputStream, CTXmlPr.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTXmlPr.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTXmlPr.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
