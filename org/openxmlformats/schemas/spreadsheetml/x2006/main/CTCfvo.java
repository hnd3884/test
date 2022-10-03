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
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTCfvo extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTCfvo.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctcfvo7ca5type");
    
    CTExtensionList getExtLst();
    
    boolean isSetExtLst();
    
    void setExtLst(final CTExtensionList p0);
    
    CTExtensionList addNewExtLst();
    
    void unsetExtLst();
    
    STCfvoType.Enum getType();
    
    STCfvoType xgetType();
    
    void setType(final STCfvoType.Enum p0);
    
    void xsetType(final STCfvoType p0);
    
    String getVal();
    
    STXstring xgetVal();
    
    boolean isSetVal();
    
    void setVal(final String p0);
    
    void xsetVal(final STXstring p0);
    
    void unsetVal();
    
    boolean getGte();
    
    XmlBoolean xgetGte();
    
    boolean isSetGte();
    
    void setGte(final boolean p0);
    
    void xsetGte(final XmlBoolean p0);
    
    void unsetGte();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTCfvo.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTCfvo newInstance() {
            return (CTCfvo)getTypeLoader().newInstance(CTCfvo.type, (XmlOptions)null);
        }
        
        public static CTCfvo newInstance(final XmlOptions xmlOptions) {
            return (CTCfvo)getTypeLoader().newInstance(CTCfvo.type, xmlOptions);
        }
        
        public static CTCfvo parse(final String s) throws XmlException {
            return (CTCfvo)getTypeLoader().parse(s, CTCfvo.type, (XmlOptions)null);
        }
        
        public static CTCfvo parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTCfvo)getTypeLoader().parse(s, CTCfvo.type, xmlOptions);
        }
        
        public static CTCfvo parse(final File file) throws XmlException, IOException {
            return (CTCfvo)getTypeLoader().parse(file, CTCfvo.type, (XmlOptions)null);
        }
        
        public static CTCfvo parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTCfvo)getTypeLoader().parse(file, CTCfvo.type, xmlOptions);
        }
        
        public static CTCfvo parse(final URL url) throws XmlException, IOException {
            return (CTCfvo)getTypeLoader().parse(url, CTCfvo.type, (XmlOptions)null);
        }
        
        public static CTCfvo parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTCfvo)getTypeLoader().parse(url, CTCfvo.type, xmlOptions);
        }
        
        public static CTCfvo parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTCfvo)getTypeLoader().parse(inputStream, CTCfvo.type, (XmlOptions)null);
        }
        
        public static CTCfvo parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTCfvo)getTypeLoader().parse(inputStream, CTCfvo.type, xmlOptions);
        }
        
        public static CTCfvo parse(final Reader reader) throws XmlException, IOException {
            return (CTCfvo)getTypeLoader().parse(reader, CTCfvo.type, (XmlOptions)null);
        }
        
        public static CTCfvo parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTCfvo)getTypeLoader().parse(reader, CTCfvo.type, xmlOptions);
        }
        
        public static CTCfvo parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTCfvo)getTypeLoader().parse(xmlStreamReader, CTCfvo.type, (XmlOptions)null);
        }
        
        public static CTCfvo parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTCfvo)getTypeLoader().parse(xmlStreamReader, CTCfvo.type, xmlOptions);
        }
        
        public static CTCfvo parse(final Node node) throws XmlException {
            return (CTCfvo)getTypeLoader().parse(node, CTCfvo.type, (XmlOptions)null);
        }
        
        public static CTCfvo parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTCfvo)getTypeLoader().parse(node, CTCfvo.type, xmlOptions);
        }
        
        @Deprecated
        public static CTCfvo parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTCfvo)getTypeLoader().parse(xmlInputStream, CTCfvo.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTCfvo parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTCfvo)getTypeLoader().parse(xmlInputStream, CTCfvo.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTCfvo.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTCfvo.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
