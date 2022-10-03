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

public interface CTFonts extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTFonts.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctfonts124etype");
    
    STHint.Enum getHint();
    
    STHint xgetHint();
    
    boolean isSetHint();
    
    void setHint(final STHint.Enum p0);
    
    void xsetHint(final STHint p0);
    
    void unsetHint();
    
    String getAscii();
    
    STString xgetAscii();
    
    boolean isSetAscii();
    
    void setAscii(final String p0);
    
    void xsetAscii(final STString p0);
    
    void unsetAscii();
    
    String getHAnsi();
    
    STString xgetHAnsi();
    
    boolean isSetHAnsi();
    
    void setHAnsi(final String p0);
    
    void xsetHAnsi(final STString p0);
    
    void unsetHAnsi();
    
    String getEastAsia();
    
    STString xgetEastAsia();
    
    boolean isSetEastAsia();
    
    void setEastAsia(final String p0);
    
    void xsetEastAsia(final STString p0);
    
    void unsetEastAsia();
    
    String getCs();
    
    STString xgetCs();
    
    boolean isSetCs();
    
    void setCs(final String p0);
    
    void xsetCs(final STString p0);
    
    void unsetCs();
    
    STTheme.Enum getAsciiTheme();
    
    STTheme xgetAsciiTheme();
    
    boolean isSetAsciiTheme();
    
    void setAsciiTheme(final STTheme.Enum p0);
    
    void xsetAsciiTheme(final STTheme p0);
    
    void unsetAsciiTheme();
    
    STTheme.Enum getHAnsiTheme();
    
    STTheme xgetHAnsiTheme();
    
    boolean isSetHAnsiTheme();
    
    void setHAnsiTheme(final STTheme.Enum p0);
    
    void xsetHAnsiTheme(final STTheme p0);
    
    void unsetHAnsiTheme();
    
    STTheme.Enum getEastAsiaTheme();
    
    STTheme xgetEastAsiaTheme();
    
    boolean isSetEastAsiaTheme();
    
    void setEastAsiaTheme(final STTheme.Enum p0);
    
    void xsetEastAsiaTheme(final STTheme p0);
    
    void unsetEastAsiaTheme();
    
    STTheme.Enum getCstheme();
    
    STTheme xgetCstheme();
    
    boolean isSetCstheme();
    
    void setCstheme(final STTheme.Enum p0);
    
    void xsetCstheme(final STTheme p0);
    
    void unsetCstheme();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTFonts.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTFonts newInstance() {
            return (CTFonts)getTypeLoader().newInstance(CTFonts.type, (XmlOptions)null);
        }
        
        public static CTFonts newInstance(final XmlOptions xmlOptions) {
            return (CTFonts)getTypeLoader().newInstance(CTFonts.type, xmlOptions);
        }
        
        public static CTFonts parse(final String s) throws XmlException {
            return (CTFonts)getTypeLoader().parse(s, CTFonts.type, (XmlOptions)null);
        }
        
        public static CTFonts parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTFonts)getTypeLoader().parse(s, CTFonts.type, xmlOptions);
        }
        
        public static CTFonts parse(final File file) throws XmlException, IOException {
            return (CTFonts)getTypeLoader().parse(file, CTFonts.type, (XmlOptions)null);
        }
        
        public static CTFonts parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTFonts)getTypeLoader().parse(file, CTFonts.type, xmlOptions);
        }
        
        public static CTFonts parse(final URL url) throws XmlException, IOException {
            return (CTFonts)getTypeLoader().parse(url, CTFonts.type, (XmlOptions)null);
        }
        
        public static CTFonts parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTFonts)getTypeLoader().parse(url, CTFonts.type, xmlOptions);
        }
        
        public static CTFonts parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTFonts)getTypeLoader().parse(inputStream, CTFonts.type, (XmlOptions)null);
        }
        
        public static CTFonts parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTFonts)getTypeLoader().parse(inputStream, CTFonts.type, xmlOptions);
        }
        
        public static CTFonts parse(final Reader reader) throws XmlException, IOException {
            return (CTFonts)getTypeLoader().parse(reader, CTFonts.type, (XmlOptions)null);
        }
        
        public static CTFonts parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTFonts)getTypeLoader().parse(reader, CTFonts.type, xmlOptions);
        }
        
        public static CTFonts parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTFonts)getTypeLoader().parse(xmlStreamReader, CTFonts.type, (XmlOptions)null);
        }
        
        public static CTFonts parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTFonts)getTypeLoader().parse(xmlStreamReader, CTFonts.type, xmlOptions);
        }
        
        public static CTFonts parse(final Node node) throws XmlException {
            return (CTFonts)getTypeLoader().parse(node, CTFonts.type, (XmlOptions)null);
        }
        
        public static CTFonts parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTFonts)getTypeLoader().parse(node, CTFonts.type, xmlOptions);
        }
        
        @Deprecated
        public static CTFonts parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTFonts)getTypeLoader().parse(xmlInputStream, CTFonts.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTFonts parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTFonts)getTypeLoader().parse(xmlInputStream, CTFonts.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTFonts.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTFonts.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
