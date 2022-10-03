package org.openxmlformats.schemas.drawingml.x2006.main;

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

public interface CTColorMapping extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTColorMapping.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctcolormapping5bc6type");
    
    CTOfficeArtExtensionList getExtLst();
    
    boolean isSetExtLst();
    
    void setExtLst(final CTOfficeArtExtensionList p0);
    
    CTOfficeArtExtensionList addNewExtLst();
    
    void unsetExtLst();
    
    STColorSchemeIndex.Enum getBg1();
    
    STColorSchemeIndex xgetBg1();
    
    void setBg1(final STColorSchemeIndex.Enum p0);
    
    void xsetBg1(final STColorSchemeIndex p0);
    
    STColorSchemeIndex.Enum getTx1();
    
    STColorSchemeIndex xgetTx1();
    
    void setTx1(final STColorSchemeIndex.Enum p0);
    
    void xsetTx1(final STColorSchemeIndex p0);
    
    STColorSchemeIndex.Enum getBg2();
    
    STColorSchemeIndex xgetBg2();
    
    void setBg2(final STColorSchemeIndex.Enum p0);
    
    void xsetBg2(final STColorSchemeIndex p0);
    
    STColorSchemeIndex.Enum getTx2();
    
    STColorSchemeIndex xgetTx2();
    
    void setTx2(final STColorSchemeIndex.Enum p0);
    
    void xsetTx2(final STColorSchemeIndex p0);
    
    STColorSchemeIndex.Enum getAccent1();
    
    STColorSchemeIndex xgetAccent1();
    
    void setAccent1(final STColorSchemeIndex.Enum p0);
    
    void xsetAccent1(final STColorSchemeIndex p0);
    
    STColorSchemeIndex.Enum getAccent2();
    
    STColorSchemeIndex xgetAccent2();
    
    void setAccent2(final STColorSchemeIndex.Enum p0);
    
    void xsetAccent2(final STColorSchemeIndex p0);
    
    STColorSchemeIndex.Enum getAccent3();
    
    STColorSchemeIndex xgetAccent3();
    
    void setAccent3(final STColorSchemeIndex.Enum p0);
    
    void xsetAccent3(final STColorSchemeIndex p0);
    
    STColorSchemeIndex.Enum getAccent4();
    
    STColorSchemeIndex xgetAccent4();
    
    void setAccent4(final STColorSchemeIndex.Enum p0);
    
    void xsetAccent4(final STColorSchemeIndex p0);
    
    STColorSchemeIndex.Enum getAccent5();
    
    STColorSchemeIndex xgetAccent5();
    
    void setAccent5(final STColorSchemeIndex.Enum p0);
    
    void xsetAccent5(final STColorSchemeIndex p0);
    
    STColorSchemeIndex.Enum getAccent6();
    
    STColorSchemeIndex xgetAccent6();
    
    void setAccent6(final STColorSchemeIndex.Enum p0);
    
    void xsetAccent6(final STColorSchemeIndex p0);
    
    STColorSchemeIndex.Enum getHlink();
    
    STColorSchemeIndex xgetHlink();
    
    void setHlink(final STColorSchemeIndex.Enum p0);
    
    void xsetHlink(final STColorSchemeIndex p0);
    
    STColorSchemeIndex.Enum getFolHlink();
    
    STColorSchemeIndex xgetFolHlink();
    
    void setFolHlink(final STColorSchemeIndex.Enum p0);
    
    void xsetFolHlink(final STColorSchemeIndex p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTColorMapping.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTColorMapping newInstance() {
            return (CTColorMapping)getTypeLoader().newInstance(CTColorMapping.type, (XmlOptions)null);
        }
        
        public static CTColorMapping newInstance(final XmlOptions xmlOptions) {
            return (CTColorMapping)getTypeLoader().newInstance(CTColorMapping.type, xmlOptions);
        }
        
        public static CTColorMapping parse(final String s) throws XmlException {
            return (CTColorMapping)getTypeLoader().parse(s, CTColorMapping.type, (XmlOptions)null);
        }
        
        public static CTColorMapping parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTColorMapping)getTypeLoader().parse(s, CTColorMapping.type, xmlOptions);
        }
        
        public static CTColorMapping parse(final File file) throws XmlException, IOException {
            return (CTColorMapping)getTypeLoader().parse(file, CTColorMapping.type, (XmlOptions)null);
        }
        
        public static CTColorMapping parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTColorMapping)getTypeLoader().parse(file, CTColorMapping.type, xmlOptions);
        }
        
        public static CTColorMapping parse(final URL url) throws XmlException, IOException {
            return (CTColorMapping)getTypeLoader().parse(url, CTColorMapping.type, (XmlOptions)null);
        }
        
        public static CTColorMapping parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTColorMapping)getTypeLoader().parse(url, CTColorMapping.type, xmlOptions);
        }
        
        public static CTColorMapping parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTColorMapping)getTypeLoader().parse(inputStream, CTColorMapping.type, (XmlOptions)null);
        }
        
        public static CTColorMapping parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTColorMapping)getTypeLoader().parse(inputStream, CTColorMapping.type, xmlOptions);
        }
        
        public static CTColorMapping parse(final Reader reader) throws XmlException, IOException {
            return (CTColorMapping)getTypeLoader().parse(reader, CTColorMapping.type, (XmlOptions)null);
        }
        
        public static CTColorMapping parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTColorMapping)getTypeLoader().parse(reader, CTColorMapping.type, xmlOptions);
        }
        
        public static CTColorMapping parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTColorMapping)getTypeLoader().parse(xmlStreamReader, CTColorMapping.type, (XmlOptions)null);
        }
        
        public static CTColorMapping parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTColorMapping)getTypeLoader().parse(xmlStreamReader, CTColorMapping.type, xmlOptions);
        }
        
        public static CTColorMapping parse(final Node node) throws XmlException {
            return (CTColorMapping)getTypeLoader().parse(node, CTColorMapping.type, (XmlOptions)null);
        }
        
        public static CTColorMapping parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTColorMapping)getTypeLoader().parse(node, CTColorMapping.type, xmlOptions);
        }
        
        @Deprecated
        public static CTColorMapping parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTColorMapping)getTypeLoader().parse(xmlInputStream, CTColorMapping.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTColorMapping parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTColorMapping)getTypeLoader().parse(xmlInputStream, CTColorMapping.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTColorMapping.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTColorMapping.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
