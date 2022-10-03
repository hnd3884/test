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
import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.XmlUnsignedInt;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTXmlColumnPr extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTXmlColumnPr.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctxmlcolumnprc14etype");
    
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
    
    boolean getDenormalized();
    
    XmlBoolean xgetDenormalized();
    
    boolean isSetDenormalized();
    
    void setDenormalized(final boolean p0);
    
    void xsetDenormalized(final XmlBoolean p0);
    
    void unsetDenormalized();
    
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
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTXmlColumnPr.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTXmlColumnPr newInstance() {
            return (CTXmlColumnPr)getTypeLoader().newInstance(CTXmlColumnPr.type, (XmlOptions)null);
        }
        
        public static CTXmlColumnPr newInstance(final XmlOptions xmlOptions) {
            return (CTXmlColumnPr)getTypeLoader().newInstance(CTXmlColumnPr.type, xmlOptions);
        }
        
        public static CTXmlColumnPr parse(final String s) throws XmlException {
            return (CTXmlColumnPr)getTypeLoader().parse(s, CTXmlColumnPr.type, (XmlOptions)null);
        }
        
        public static CTXmlColumnPr parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTXmlColumnPr)getTypeLoader().parse(s, CTXmlColumnPr.type, xmlOptions);
        }
        
        public static CTXmlColumnPr parse(final File file) throws XmlException, IOException {
            return (CTXmlColumnPr)getTypeLoader().parse(file, CTXmlColumnPr.type, (XmlOptions)null);
        }
        
        public static CTXmlColumnPr parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTXmlColumnPr)getTypeLoader().parse(file, CTXmlColumnPr.type, xmlOptions);
        }
        
        public static CTXmlColumnPr parse(final URL url) throws XmlException, IOException {
            return (CTXmlColumnPr)getTypeLoader().parse(url, CTXmlColumnPr.type, (XmlOptions)null);
        }
        
        public static CTXmlColumnPr parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTXmlColumnPr)getTypeLoader().parse(url, CTXmlColumnPr.type, xmlOptions);
        }
        
        public static CTXmlColumnPr parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTXmlColumnPr)getTypeLoader().parse(inputStream, CTXmlColumnPr.type, (XmlOptions)null);
        }
        
        public static CTXmlColumnPr parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTXmlColumnPr)getTypeLoader().parse(inputStream, CTXmlColumnPr.type, xmlOptions);
        }
        
        public static CTXmlColumnPr parse(final Reader reader) throws XmlException, IOException {
            return (CTXmlColumnPr)getTypeLoader().parse(reader, CTXmlColumnPr.type, (XmlOptions)null);
        }
        
        public static CTXmlColumnPr parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTXmlColumnPr)getTypeLoader().parse(reader, CTXmlColumnPr.type, xmlOptions);
        }
        
        public static CTXmlColumnPr parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTXmlColumnPr)getTypeLoader().parse(xmlStreamReader, CTXmlColumnPr.type, (XmlOptions)null);
        }
        
        public static CTXmlColumnPr parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTXmlColumnPr)getTypeLoader().parse(xmlStreamReader, CTXmlColumnPr.type, xmlOptions);
        }
        
        public static CTXmlColumnPr parse(final Node node) throws XmlException {
            return (CTXmlColumnPr)getTypeLoader().parse(node, CTXmlColumnPr.type, (XmlOptions)null);
        }
        
        public static CTXmlColumnPr parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTXmlColumnPr)getTypeLoader().parse(node, CTXmlColumnPr.type, xmlOptions);
        }
        
        @Deprecated
        public static CTXmlColumnPr parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTXmlColumnPr)getTypeLoader().parse(xmlInputStream, CTXmlColumnPr.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTXmlColumnPr parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTXmlColumnPr)getTypeLoader().parse(xmlInputStream, CTXmlColumnPr.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTXmlColumnPr.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTXmlColumnPr.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
