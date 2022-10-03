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
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTColorScheme extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTColorScheme.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctcolorscheme0e99type");
    
    CTColor getDk1();
    
    void setDk1(final CTColor p0);
    
    CTColor addNewDk1();
    
    CTColor getLt1();
    
    void setLt1(final CTColor p0);
    
    CTColor addNewLt1();
    
    CTColor getDk2();
    
    void setDk2(final CTColor p0);
    
    CTColor addNewDk2();
    
    CTColor getLt2();
    
    void setLt2(final CTColor p0);
    
    CTColor addNewLt2();
    
    CTColor getAccent1();
    
    void setAccent1(final CTColor p0);
    
    CTColor addNewAccent1();
    
    CTColor getAccent2();
    
    void setAccent2(final CTColor p0);
    
    CTColor addNewAccent2();
    
    CTColor getAccent3();
    
    void setAccent3(final CTColor p0);
    
    CTColor addNewAccent3();
    
    CTColor getAccent4();
    
    void setAccent4(final CTColor p0);
    
    CTColor addNewAccent4();
    
    CTColor getAccent5();
    
    void setAccent5(final CTColor p0);
    
    CTColor addNewAccent5();
    
    CTColor getAccent6();
    
    void setAccent6(final CTColor p0);
    
    CTColor addNewAccent6();
    
    CTColor getHlink();
    
    void setHlink(final CTColor p0);
    
    CTColor addNewHlink();
    
    CTColor getFolHlink();
    
    void setFolHlink(final CTColor p0);
    
    CTColor addNewFolHlink();
    
    CTOfficeArtExtensionList getExtLst();
    
    boolean isSetExtLst();
    
    void setExtLst(final CTOfficeArtExtensionList p0);
    
    CTOfficeArtExtensionList addNewExtLst();
    
    void unsetExtLst();
    
    String getName();
    
    XmlString xgetName();
    
    void setName(final String p0);
    
    void xsetName(final XmlString p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTColorScheme.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTColorScheme newInstance() {
            return (CTColorScheme)getTypeLoader().newInstance(CTColorScheme.type, (XmlOptions)null);
        }
        
        public static CTColorScheme newInstance(final XmlOptions xmlOptions) {
            return (CTColorScheme)getTypeLoader().newInstance(CTColorScheme.type, xmlOptions);
        }
        
        public static CTColorScheme parse(final String s) throws XmlException {
            return (CTColorScheme)getTypeLoader().parse(s, CTColorScheme.type, (XmlOptions)null);
        }
        
        public static CTColorScheme parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTColorScheme)getTypeLoader().parse(s, CTColorScheme.type, xmlOptions);
        }
        
        public static CTColorScheme parse(final File file) throws XmlException, IOException {
            return (CTColorScheme)getTypeLoader().parse(file, CTColorScheme.type, (XmlOptions)null);
        }
        
        public static CTColorScheme parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTColorScheme)getTypeLoader().parse(file, CTColorScheme.type, xmlOptions);
        }
        
        public static CTColorScheme parse(final URL url) throws XmlException, IOException {
            return (CTColorScheme)getTypeLoader().parse(url, CTColorScheme.type, (XmlOptions)null);
        }
        
        public static CTColorScheme parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTColorScheme)getTypeLoader().parse(url, CTColorScheme.type, xmlOptions);
        }
        
        public static CTColorScheme parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTColorScheme)getTypeLoader().parse(inputStream, CTColorScheme.type, (XmlOptions)null);
        }
        
        public static CTColorScheme parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTColorScheme)getTypeLoader().parse(inputStream, CTColorScheme.type, xmlOptions);
        }
        
        public static CTColorScheme parse(final Reader reader) throws XmlException, IOException {
            return (CTColorScheme)getTypeLoader().parse(reader, CTColorScheme.type, (XmlOptions)null);
        }
        
        public static CTColorScheme parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTColorScheme)getTypeLoader().parse(reader, CTColorScheme.type, xmlOptions);
        }
        
        public static CTColorScheme parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTColorScheme)getTypeLoader().parse(xmlStreamReader, CTColorScheme.type, (XmlOptions)null);
        }
        
        public static CTColorScheme parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTColorScheme)getTypeLoader().parse(xmlStreamReader, CTColorScheme.type, xmlOptions);
        }
        
        public static CTColorScheme parse(final Node node) throws XmlException {
            return (CTColorScheme)getTypeLoader().parse(node, CTColorScheme.type, (XmlOptions)null);
        }
        
        public static CTColorScheme parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTColorScheme)getTypeLoader().parse(node, CTColorScheme.type, xmlOptions);
        }
        
        @Deprecated
        public static CTColorScheme parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTColorScheme)getTypeLoader().parse(xmlInputStream, CTColorScheme.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTColorScheme parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTColorScheme)getTypeLoader().parse(xmlInputStream, CTColorScheme.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTColorScheme.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTColorScheme.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
