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

public interface CTShd extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTShd.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctshd58c3type");
    
    STShd.Enum getVal();
    
    STShd xgetVal();
    
    void setVal(final STShd.Enum p0);
    
    void xsetVal(final STShd p0);
    
    Object getColor();
    
    STHexColor xgetColor();
    
    boolean isSetColor();
    
    void setColor(final Object p0);
    
    void xsetColor(final STHexColor p0);
    
    void unsetColor();
    
    STThemeColor.Enum getThemeColor();
    
    STThemeColor xgetThemeColor();
    
    boolean isSetThemeColor();
    
    void setThemeColor(final STThemeColor.Enum p0);
    
    void xsetThemeColor(final STThemeColor p0);
    
    void unsetThemeColor();
    
    byte[] getThemeTint();
    
    STUcharHexNumber xgetThemeTint();
    
    boolean isSetThemeTint();
    
    void setThemeTint(final byte[] p0);
    
    void xsetThemeTint(final STUcharHexNumber p0);
    
    void unsetThemeTint();
    
    byte[] getThemeShade();
    
    STUcharHexNumber xgetThemeShade();
    
    boolean isSetThemeShade();
    
    void setThemeShade(final byte[] p0);
    
    void xsetThemeShade(final STUcharHexNumber p0);
    
    void unsetThemeShade();
    
    Object getFill();
    
    STHexColor xgetFill();
    
    boolean isSetFill();
    
    void setFill(final Object p0);
    
    void xsetFill(final STHexColor p0);
    
    void unsetFill();
    
    STThemeColor.Enum getThemeFill();
    
    STThemeColor xgetThemeFill();
    
    boolean isSetThemeFill();
    
    void setThemeFill(final STThemeColor.Enum p0);
    
    void xsetThemeFill(final STThemeColor p0);
    
    void unsetThemeFill();
    
    byte[] getThemeFillTint();
    
    STUcharHexNumber xgetThemeFillTint();
    
    boolean isSetThemeFillTint();
    
    void setThemeFillTint(final byte[] p0);
    
    void xsetThemeFillTint(final STUcharHexNumber p0);
    
    void unsetThemeFillTint();
    
    byte[] getThemeFillShade();
    
    STUcharHexNumber xgetThemeFillShade();
    
    boolean isSetThemeFillShade();
    
    void setThemeFillShade(final byte[] p0);
    
    void xsetThemeFillShade(final STUcharHexNumber p0);
    
    void unsetThemeFillShade();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTShd.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTShd newInstance() {
            return (CTShd)getTypeLoader().newInstance(CTShd.type, (XmlOptions)null);
        }
        
        public static CTShd newInstance(final XmlOptions xmlOptions) {
            return (CTShd)getTypeLoader().newInstance(CTShd.type, xmlOptions);
        }
        
        public static CTShd parse(final String s) throws XmlException {
            return (CTShd)getTypeLoader().parse(s, CTShd.type, (XmlOptions)null);
        }
        
        public static CTShd parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTShd)getTypeLoader().parse(s, CTShd.type, xmlOptions);
        }
        
        public static CTShd parse(final File file) throws XmlException, IOException {
            return (CTShd)getTypeLoader().parse(file, CTShd.type, (XmlOptions)null);
        }
        
        public static CTShd parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTShd)getTypeLoader().parse(file, CTShd.type, xmlOptions);
        }
        
        public static CTShd parse(final URL url) throws XmlException, IOException {
            return (CTShd)getTypeLoader().parse(url, CTShd.type, (XmlOptions)null);
        }
        
        public static CTShd parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTShd)getTypeLoader().parse(url, CTShd.type, xmlOptions);
        }
        
        public static CTShd parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTShd)getTypeLoader().parse(inputStream, CTShd.type, (XmlOptions)null);
        }
        
        public static CTShd parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTShd)getTypeLoader().parse(inputStream, CTShd.type, xmlOptions);
        }
        
        public static CTShd parse(final Reader reader) throws XmlException, IOException {
            return (CTShd)getTypeLoader().parse(reader, CTShd.type, (XmlOptions)null);
        }
        
        public static CTShd parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTShd)getTypeLoader().parse(reader, CTShd.type, xmlOptions);
        }
        
        public static CTShd parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTShd)getTypeLoader().parse(xmlStreamReader, CTShd.type, (XmlOptions)null);
        }
        
        public static CTShd parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTShd)getTypeLoader().parse(xmlStreamReader, CTShd.type, xmlOptions);
        }
        
        public static CTShd parse(final Node node) throws XmlException {
            return (CTShd)getTypeLoader().parse(node, CTShd.type, (XmlOptions)null);
        }
        
        public static CTShd parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTShd)getTypeLoader().parse(node, CTShd.type, xmlOptions);
        }
        
        @Deprecated
        public static CTShd parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTShd)getTypeLoader().parse(xmlInputStream, CTShd.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTShd parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTShd)getTypeLoader().parse(xmlInputStream, CTShd.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTShd.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTShd.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
