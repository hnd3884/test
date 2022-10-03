package org.openxmlformats.schemas.wordprocessingml.x2006.main;

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
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTFldChar extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTFldChar.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctfldchare83etype");
    
    CTText getFldData();
    
    boolean isSetFldData();
    
    void setFldData(final CTText p0);
    
    CTText addNewFldData();
    
    void unsetFldData();
    
    CTFFData getFfData();
    
    boolean isSetFfData();
    
    void setFfData(final CTFFData p0);
    
    CTFFData addNewFfData();
    
    void unsetFfData();
    
    CTTrackChangeNumbering getNumberingChange();
    
    boolean isSetNumberingChange();
    
    void setNumberingChange(final CTTrackChangeNumbering p0);
    
    CTTrackChangeNumbering addNewNumberingChange();
    
    void unsetNumberingChange();
    
    STFldCharType.Enum getFldCharType();
    
    STFldCharType xgetFldCharType();
    
    void setFldCharType(final STFldCharType.Enum p0);
    
    void xsetFldCharType(final STFldCharType p0);
    
    STOnOff.Enum getFldLock();
    
    STOnOff xgetFldLock();
    
    boolean isSetFldLock();
    
    void setFldLock(final STOnOff.Enum p0);
    
    void xsetFldLock(final STOnOff p0);
    
    void unsetFldLock();
    
    STOnOff.Enum getDirty();
    
    STOnOff xgetDirty();
    
    boolean isSetDirty();
    
    void setDirty(final STOnOff.Enum p0);
    
    void xsetDirty(final STOnOff p0);
    
    void unsetDirty();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTFldChar.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTFldChar newInstance() {
            return (CTFldChar)getTypeLoader().newInstance(CTFldChar.type, (XmlOptions)null);
        }
        
        public static CTFldChar newInstance(final XmlOptions xmlOptions) {
            return (CTFldChar)getTypeLoader().newInstance(CTFldChar.type, xmlOptions);
        }
        
        public static CTFldChar parse(final String s) throws XmlException {
            return (CTFldChar)getTypeLoader().parse(s, CTFldChar.type, (XmlOptions)null);
        }
        
        public static CTFldChar parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTFldChar)getTypeLoader().parse(s, CTFldChar.type, xmlOptions);
        }
        
        public static CTFldChar parse(final File file) throws XmlException, IOException {
            return (CTFldChar)getTypeLoader().parse(file, CTFldChar.type, (XmlOptions)null);
        }
        
        public static CTFldChar parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTFldChar)getTypeLoader().parse(file, CTFldChar.type, xmlOptions);
        }
        
        public static CTFldChar parse(final URL url) throws XmlException, IOException {
            return (CTFldChar)getTypeLoader().parse(url, CTFldChar.type, (XmlOptions)null);
        }
        
        public static CTFldChar parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTFldChar)getTypeLoader().parse(url, CTFldChar.type, xmlOptions);
        }
        
        public static CTFldChar parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTFldChar)getTypeLoader().parse(inputStream, CTFldChar.type, (XmlOptions)null);
        }
        
        public static CTFldChar parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTFldChar)getTypeLoader().parse(inputStream, CTFldChar.type, xmlOptions);
        }
        
        public static CTFldChar parse(final Reader reader) throws XmlException, IOException {
            return (CTFldChar)getTypeLoader().parse(reader, CTFldChar.type, (XmlOptions)null);
        }
        
        public static CTFldChar parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTFldChar)getTypeLoader().parse(reader, CTFldChar.type, xmlOptions);
        }
        
        public static CTFldChar parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTFldChar)getTypeLoader().parse(xmlStreamReader, CTFldChar.type, (XmlOptions)null);
        }
        
        public static CTFldChar parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTFldChar)getTypeLoader().parse(xmlStreamReader, CTFldChar.type, xmlOptions);
        }
        
        public static CTFldChar parse(final Node node) throws XmlException {
            return (CTFldChar)getTypeLoader().parse(node, CTFldChar.type, (XmlOptions)null);
        }
        
        public static CTFldChar parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTFldChar)getTypeLoader().parse(node, CTFldChar.type, xmlOptions);
        }
        
        @Deprecated
        public static CTFldChar parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTFldChar)getTypeLoader().parse(xmlInputStream, CTFldChar.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTFldChar parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTFldChar)getTypeLoader().parse(xmlInputStream, CTFldChar.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTFldChar.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTFldChar.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
