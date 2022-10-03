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
import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTHeaderFooter extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTHeaderFooter.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctheaderfooterb29dtype");
    
    CTExtensionListModify getExtLst();
    
    boolean isSetExtLst();
    
    void setExtLst(final CTExtensionListModify p0);
    
    CTExtensionListModify addNewExtLst();
    
    void unsetExtLst();
    
    boolean getSldNum();
    
    XmlBoolean xgetSldNum();
    
    boolean isSetSldNum();
    
    void setSldNum(final boolean p0);
    
    void xsetSldNum(final XmlBoolean p0);
    
    void unsetSldNum();
    
    boolean getHdr();
    
    XmlBoolean xgetHdr();
    
    boolean isSetHdr();
    
    void setHdr(final boolean p0);
    
    void xsetHdr(final XmlBoolean p0);
    
    void unsetHdr();
    
    boolean getFtr();
    
    XmlBoolean xgetFtr();
    
    boolean isSetFtr();
    
    void setFtr(final boolean p0);
    
    void xsetFtr(final XmlBoolean p0);
    
    void unsetFtr();
    
    boolean getDt();
    
    XmlBoolean xgetDt();
    
    boolean isSetDt();
    
    void setDt(final boolean p0);
    
    void xsetDt(final XmlBoolean p0);
    
    void unsetDt();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTHeaderFooter.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTHeaderFooter newInstance() {
            return (CTHeaderFooter)getTypeLoader().newInstance(CTHeaderFooter.type, (XmlOptions)null);
        }
        
        public static CTHeaderFooter newInstance(final XmlOptions xmlOptions) {
            return (CTHeaderFooter)getTypeLoader().newInstance(CTHeaderFooter.type, xmlOptions);
        }
        
        public static CTHeaderFooter parse(final String s) throws XmlException {
            return (CTHeaderFooter)getTypeLoader().parse(s, CTHeaderFooter.type, (XmlOptions)null);
        }
        
        public static CTHeaderFooter parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTHeaderFooter)getTypeLoader().parse(s, CTHeaderFooter.type, xmlOptions);
        }
        
        public static CTHeaderFooter parse(final File file) throws XmlException, IOException {
            return (CTHeaderFooter)getTypeLoader().parse(file, CTHeaderFooter.type, (XmlOptions)null);
        }
        
        public static CTHeaderFooter parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTHeaderFooter)getTypeLoader().parse(file, CTHeaderFooter.type, xmlOptions);
        }
        
        public static CTHeaderFooter parse(final URL url) throws XmlException, IOException {
            return (CTHeaderFooter)getTypeLoader().parse(url, CTHeaderFooter.type, (XmlOptions)null);
        }
        
        public static CTHeaderFooter parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTHeaderFooter)getTypeLoader().parse(url, CTHeaderFooter.type, xmlOptions);
        }
        
        public static CTHeaderFooter parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTHeaderFooter)getTypeLoader().parse(inputStream, CTHeaderFooter.type, (XmlOptions)null);
        }
        
        public static CTHeaderFooter parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTHeaderFooter)getTypeLoader().parse(inputStream, CTHeaderFooter.type, xmlOptions);
        }
        
        public static CTHeaderFooter parse(final Reader reader) throws XmlException, IOException {
            return (CTHeaderFooter)getTypeLoader().parse(reader, CTHeaderFooter.type, (XmlOptions)null);
        }
        
        public static CTHeaderFooter parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTHeaderFooter)getTypeLoader().parse(reader, CTHeaderFooter.type, xmlOptions);
        }
        
        public static CTHeaderFooter parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTHeaderFooter)getTypeLoader().parse(xmlStreamReader, CTHeaderFooter.type, (XmlOptions)null);
        }
        
        public static CTHeaderFooter parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTHeaderFooter)getTypeLoader().parse(xmlStreamReader, CTHeaderFooter.type, xmlOptions);
        }
        
        public static CTHeaderFooter parse(final Node node) throws XmlException {
            return (CTHeaderFooter)getTypeLoader().parse(node, CTHeaderFooter.type, (XmlOptions)null);
        }
        
        public static CTHeaderFooter parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTHeaderFooter)getTypeLoader().parse(node, CTHeaderFooter.type, xmlOptions);
        }
        
        @Deprecated
        public static CTHeaderFooter parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTHeaderFooter)getTypeLoader().parse(xmlInputStream, CTHeaderFooter.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTHeaderFooter parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTHeaderFooter)getTypeLoader().parse(xmlInputStream, CTHeaderFooter.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTHeaderFooter.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTHeaderFooter.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
