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

public interface CTLanguage extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTLanguage.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctlanguage7b90type");
    
    Object getVal();
    
    STLang xgetVal();
    
    boolean isSetVal();
    
    void setVal(final Object p0);
    
    void xsetVal(final STLang p0);
    
    void unsetVal();
    
    Object getEastAsia();
    
    STLang xgetEastAsia();
    
    boolean isSetEastAsia();
    
    void setEastAsia(final Object p0);
    
    void xsetEastAsia(final STLang p0);
    
    void unsetEastAsia();
    
    Object getBidi();
    
    STLang xgetBidi();
    
    boolean isSetBidi();
    
    void setBidi(final Object p0);
    
    void xsetBidi(final STLang p0);
    
    void unsetBidi();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTLanguage.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTLanguage newInstance() {
            return (CTLanguage)getTypeLoader().newInstance(CTLanguage.type, (XmlOptions)null);
        }
        
        public static CTLanguage newInstance(final XmlOptions xmlOptions) {
            return (CTLanguage)getTypeLoader().newInstance(CTLanguage.type, xmlOptions);
        }
        
        public static CTLanguage parse(final String s) throws XmlException {
            return (CTLanguage)getTypeLoader().parse(s, CTLanguage.type, (XmlOptions)null);
        }
        
        public static CTLanguage parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTLanguage)getTypeLoader().parse(s, CTLanguage.type, xmlOptions);
        }
        
        public static CTLanguage parse(final File file) throws XmlException, IOException {
            return (CTLanguage)getTypeLoader().parse(file, CTLanguage.type, (XmlOptions)null);
        }
        
        public static CTLanguage parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTLanguage)getTypeLoader().parse(file, CTLanguage.type, xmlOptions);
        }
        
        public static CTLanguage parse(final URL url) throws XmlException, IOException {
            return (CTLanguage)getTypeLoader().parse(url, CTLanguage.type, (XmlOptions)null);
        }
        
        public static CTLanguage parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTLanguage)getTypeLoader().parse(url, CTLanguage.type, xmlOptions);
        }
        
        public static CTLanguage parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTLanguage)getTypeLoader().parse(inputStream, CTLanguage.type, (XmlOptions)null);
        }
        
        public static CTLanguage parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTLanguage)getTypeLoader().parse(inputStream, CTLanguage.type, xmlOptions);
        }
        
        public static CTLanguage parse(final Reader reader) throws XmlException, IOException {
            return (CTLanguage)getTypeLoader().parse(reader, CTLanguage.type, (XmlOptions)null);
        }
        
        public static CTLanguage parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTLanguage)getTypeLoader().parse(reader, CTLanguage.type, xmlOptions);
        }
        
        public static CTLanguage parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTLanguage)getTypeLoader().parse(xmlStreamReader, CTLanguage.type, (XmlOptions)null);
        }
        
        public static CTLanguage parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTLanguage)getTypeLoader().parse(xmlStreamReader, CTLanguage.type, xmlOptions);
        }
        
        public static CTLanguage parse(final Node node) throws XmlException {
            return (CTLanguage)getTypeLoader().parse(node, CTLanguage.type, (XmlOptions)null);
        }
        
        public static CTLanguage parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTLanguage)getTypeLoader().parse(node, CTLanguage.type, xmlOptions);
        }
        
        @Deprecated
        public static CTLanguage parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTLanguage)getTypeLoader().parse(xmlInputStream, CTLanguage.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTLanguage parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTLanguage)getTypeLoader().parse(xmlInputStream, CTLanguage.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTLanguage.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTLanguage.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
