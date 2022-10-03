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

public interface CTUnderline extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTUnderline.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctunderline8406type");
    
    STUnderline.Enum getVal();
    
    STUnderline xgetVal();
    
    boolean isSetVal();
    
    void setVal(final STUnderline.Enum p0);
    
    void xsetVal(final STUnderline p0);
    
    void unsetVal();
    
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
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTUnderline.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTUnderline newInstance() {
            return (CTUnderline)getTypeLoader().newInstance(CTUnderline.type, (XmlOptions)null);
        }
        
        public static CTUnderline newInstance(final XmlOptions xmlOptions) {
            return (CTUnderline)getTypeLoader().newInstance(CTUnderline.type, xmlOptions);
        }
        
        public static CTUnderline parse(final String s) throws XmlException {
            return (CTUnderline)getTypeLoader().parse(s, CTUnderline.type, (XmlOptions)null);
        }
        
        public static CTUnderline parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTUnderline)getTypeLoader().parse(s, CTUnderline.type, xmlOptions);
        }
        
        public static CTUnderline parse(final File file) throws XmlException, IOException {
            return (CTUnderline)getTypeLoader().parse(file, CTUnderline.type, (XmlOptions)null);
        }
        
        public static CTUnderline parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTUnderline)getTypeLoader().parse(file, CTUnderline.type, xmlOptions);
        }
        
        public static CTUnderline parse(final URL url) throws XmlException, IOException {
            return (CTUnderline)getTypeLoader().parse(url, CTUnderline.type, (XmlOptions)null);
        }
        
        public static CTUnderline parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTUnderline)getTypeLoader().parse(url, CTUnderline.type, xmlOptions);
        }
        
        public static CTUnderline parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTUnderline)getTypeLoader().parse(inputStream, CTUnderline.type, (XmlOptions)null);
        }
        
        public static CTUnderline parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTUnderline)getTypeLoader().parse(inputStream, CTUnderline.type, xmlOptions);
        }
        
        public static CTUnderline parse(final Reader reader) throws XmlException, IOException {
            return (CTUnderline)getTypeLoader().parse(reader, CTUnderline.type, (XmlOptions)null);
        }
        
        public static CTUnderline parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTUnderline)getTypeLoader().parse(reader, CTUnderline.type, xmlOptions);
        }
        
        public static CTUnderline parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTUnderline)getTypeLoader().parse(xmlStreamReader, CTUnderline.type, (XmlOptions)null);
        }
        
        public static CTUnderline parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTUnderline)getTypeLoader().parse(xmlStreamReader, CTUnderline.type, xmlOptions);
        }
        
        public static CTUnderline parse(final Node node) throws XmlException {
            return (CTUnderline)getTypeLoader().parse(node, CTUnderline.type, (XmlOptions)null);
        }
        
        public static CTUnderline parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTUnderline)getTypeLoader().parse(node, CTUnderline.type, xmlOptions);
        }
        
        @Deprecated
        public static CTUnderline parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTUnderline)getTypeLoader().parse(xmlInputStream, CTUnderline.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTUnderline parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTUnderline)getTypeLoader().parse(xmlInputStream, CTUnderline.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTUnderline.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTUnderline.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
