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
import java.math.BigInteger;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTBorder extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTBorder.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctbordercdfctype");
    
    STBorder.Enum getVal();
    
    STBorder xgetVal();
    
    void setVal(final STBorder.Enum p0);
    
    void xsetVal(final STBorder p0);
    
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
    
    BigInteger getSz();
    
    STEighthPointMeasure xgetSz();
    
    boolean isSetSz();
    
    void setSz(final BigInteger p0);
    
    void xsetSz(final STEighthPointMeasure p0);
    
    void unsetSz();
    
    BigInteger getSpace();
    
    STPointMeasure xgetSpace();
    
    boolean isSetSpace();
    
    void setSpace(final BigInteger p0);
    
    void xsetSpace(final STPointMeasure p0);
    
    void unsetSpace();
    
    STOnOff.Enum getShadow();
    
    STOnOff xgetShadow();
    
    boolean isSetShadow();
    
    void setShadow(final STOnOff.Enum p0);
    
    void xsetShadow(final STOnOff p0);
    
    void unsetShadow();
    
    STOnOff.Enum getFrame();
    
    STOnOff xgetFrame();
    
    boolean isSetFrame();
    
    void setFrame(final STOnOff.Enum p0);
    
    void xsetFrame(final STOnOff p0);
    
    void unsetFrame();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTBorder.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTBorder newInstance() {
            return (CTBorder)getTypeLoader().newInstance(CTBorder.type, (XmlOptions)null);
        }
        
        public static CTBorder newInstance(final XmlOptions xmlOptions) {
            return (CTBorder)getTypeLoader().newInstance(CTBorder.type, xmlOptions);
        }
        
        public static CTBorder parse(final String s) throws XmlException {
            return (CTBorder)getTypeLoader().parse(s, CTBorder.type, (XmlOptions)null);
        }
        
        public static CTBorder parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTBorder)getTypeLoader().parse(s, CTBorder.type, xmlOptions);
        }
        
        public static CTBorder parse(final File file) throws XmlException, IOException {
            return (CTBorder)getTypeLoader().parse(file, CTBorder.type, (XmlOptions)null);
        }
        
        public static CTBorder parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTBorder)getTypeLoader().parse(file, CTBorder.type, xmlOptions);
        }
        
        public static CTBorder parse(final URL url) throws XmlException, IOException {
            return (CTBorder)getTypeLoader().parse(url, CTBorder.type, (XmlOptions)null);
        }
        
        public static CTBorder parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTBorder)getTypeLoader().parse(url, CTBorder.type, xmlOptions);
        }
        
        public static CTBorder parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTBorder)getTypeLoader().parse(inputStream, CTBorder.type, (XmlOptions)null);
        }
        
        public static CTBorder parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTBorder)getTypeLoader().parse(inputStream, CTBorder.type, xmlOptions);
        }
        
        public static CTBorder parse(final Reader reader) throws XmlException, IOException {
            return (CTBorder)getTypeLoader().parse(reader, CTBorder.type, (XmlOptions)null);
        }
        
        public static CTBorder parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTBorder)getTypeLoader().parse(reader, CTBorder.type, xmlOptions);
        }
        
        public static CTBorder parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTBorder)getTypeLoader().parse(xmlStreamReader, CTBorder.type, (XmlOptions)null);
        }
        
        public static CTBorder parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTBorder)getTypeLoader().parse(xmlStreamReader, CTBorder.type, xmlOptions);
        }
        
        public static CTBorder parse(final Node node) throws XmlException {
            return (CTBorder)getTypeLoader().parse(node, CTBorder.type, (XmlOptions)null);
        }
        
        public static CTBorder parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTBorder)getTypeLoader().parse(node, CTBorder.type, xmlOptions);
        }
        
        @Deprecated
        public static CTBorder parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTBorder)getTypeLoader().parse(xmlInputStream, CTBorder.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTBorder parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTBorder)getTypeLoader().parse(xmlInputStream, CTBorder.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTBorder.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTBorder.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
