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
import org.apache.xmlbeans.XmlByte;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTTextFont extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTTextFont.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("cttextfont92b7type");
    
    String getTypeface();
    
    STTextTypeface xgetTypeface();
    
    boolean isSetTypeface();
    
    void setTypeface(final String p0);
    
    void xsetTypeface(final STTextTypeface p0);
    
    void unsetTypeface();
    
    byte[] getPanose();
    
    STPanose xgetPanose();
    
    boolean isSetPanose();
    
    void setPanose(final byte[] p0);
    
    void xsetPanose(final STPanose p0);
    
    void unsetPanose();
    
    byte getPitchFamily();
    
    XmlByte xgetPitchFamily();
    
    boolean isSetPitchFamily();
    
    void setPitchFamily(final byte p0);
    
    void xsetPitchFamily(final XmlByte p0);
    
    void unsetPitchFamily();
    
    byte getCharset();
    
    XmlByte xgetCharset();
    
    boolean isSetCharset();
    
    void setCharset(final byte p0);
    
    void xsetCharset(final XmlByte p0);
    
    void unsetCharset();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTTextFont.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTTextFont newInstance() {
            return (CTTextFont)getTypeLoader().newInstance(CTTextFont.type, (XmlOptions)null);
        }
        
        public static CTTextFont newInstance(final XmlOptions xmlOptions) {
            return (CTTextFont)getTypeLoader().newInstance(CTTextFont.type, xmlOptions);
        }
        
        public static CTTextFont parse(final String s) throws XmlException {
            return (CTTextFont)getTypeLoader().parse(s, CTTextFont.type, (XmlOptions)null);
        }
        
        public static CTTextFont parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTTextFont)getTypeLoader().parse(s, CTTextFont.type, xmlOptions);
        }
        
        public static CTTextFont parse(final File file) throws XmlException, IOException {
            return (CTTextFont)getTypeLoader().parse(file, CTTextFont.type, (XmlOptions)null);
        }
        
        public static CTTextFont parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTextFont)getTypeLoader().parse(file, CTTextFont.type, xmlOptions);
        }
        
        public static CTTextFont parse(final URL url) throws XmlException, IOException {
            return (CTTextFont)getTypeLoader().parse(url, CTTextFont.type, (XmlOptions)null);
        }
        
        public static CTTextFont parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTextFont)getTypeLoader().parse(url, CTTextFont.type, xmlOptions);
        }
        
        public static CTTextFont parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTTextFont)getTypeLoader().parse(inputStream, CTTextFont.type, (XmlOptions)null);
        }
        
        public static CTTextFont parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTextFont)getTypeLoader().parse(inputStream, CTTextFont.type, xmlOptions);
        }
        
        public static CTTextFont parse(final Reader reader) throws XmlException, IOException {
            return (CTTextFont)getTypeLoader().parse(reader, CTTextFont.type, (XmlOptions)null);
        }
        
        public static CTTextFont parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTextFont)getTypeLoader().parse(reader, CTTextFont.type, xmlOptions);
        }
        
        public static CTTextFont parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTTextFont)getTypeLoader().parse(xmlStreamReader, CTTextFont.type, (XmlOptions)null);
        }
        
        public static CTTextFont parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTTextFont)getTypeLoader().parse(xmlStreamReader, CTTextFont.type, xmlOptions);
        }
        
        public static CTTextFont parse(final Node node) throws XmlException {
            return (CTTextFont)getTypeLoader().parse(node, CTTextFont.type, (XmlOptions)null);
        }
        
        public static CTTextFont parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTTextFont)getTypeLoader().parse(node, CTTextFont.type, xmlOptions);
        }
        
        @Deprecated
        public static CTTextFont parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTTextFont)getTypeLoader().parse(xmlInputStream, CTTextFont.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTTextFont parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTTextFont)getTypeLoader().parse(xmlInputStream, CTTextFont.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTTextFont.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTTextFont.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
