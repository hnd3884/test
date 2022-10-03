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
import org.apache.xmlbeans.XmlDouble;
import org.apache.xmlbeans.XmlUnsignedInt;
import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTColor extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTColor.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctcolord2c2type");
    
    boolean getAuto();
    
    XmlBoolean xgetAuto();
    
    boolean isSetAuto();
    
    void setAuto(final boolean p0);
    
    void xsetAuto(final XmlBoolean p0);
    
    void unsetAuto();
    
    long getIndexed();
    
    XmlUnsignedInt xgetIndexed();
    
    boolean isSetIndexed();
    
    void setIndexed(final long p0);
    
    void xsetIndexed(final XmlUnsignedInt p0);
    
    void unsetIndexed();
    
    byte[] getRgb();
    
    STUnsignedIntHex xgetRgb();
    
    boolean isSetRgb();
    
    void setRgb(final byte[] p0);
    
    void xsetRgb(final STUnsignedIntHex p0);
    
    void unsetRgb();
    
    long getTheme();
    
    XmlUnsignedInt xgetTheme();
    
    boolean isSetTheme();
    
    void setTheme(final long p0);
    
    void xsetTheme(final XmlUnsignedInt p0);
    
    void unsetTheme();
    
    double getTint();
    
    XmlDouble xgetTint();
    
    boolean isSetTint();
    
    void setTint(final double p0);
    
    void xsetTint(final XmlDouble p0);
    
    void unsetTint();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTColor.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTColor newInstance() {
            return (CTColor)getTypeLoader().newInstance(CTColor.type, (XmlOptions)null);
        }
        
        public static CTColor newInstance(final XmlOptions xmlOptions) {
            return (CTColor)getTypeLoader().newInstance(CTColor.type, xmlOptions);
        }
        
        public static CTColor parse(final String s) throws XmlException {
            return (CTColor)getTypeLoader().parse(s, CTColor.type, (XmlOptions)null);
        }
        
        public static CTColor parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTColor)getTypeLoader().parse(s, CTColor.type, xmlOptions);
        }
        
        public static CTColor parse(final File file) throws XmlException, IOException {
            return (CTColor)getTypeLoader().parse(file, CTColor.type, (XmlOptions)null);
        }
        
        public static CTColor parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTColor)getTypeLoader().parse(file, CTColor.type, xmlOptions);
        }
        
        public static CTColor parse(final URL url) throws XmlException, IOException {
            return (CTColor)getTypeLoader().parse(url, CTColor.type, (XmlOptions)null);
        }
        
        public static CTColor parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTColor)getTypeLoader().parse(url, CTColor.type, xmlOptions);
        }
        
        public static CTColor parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTColor)getTypeLoader().parse(inputStream, CTColor.type, (XmlOptions)null);
        }
        
        public static CTColor parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTColor)getTypeLoader().parse(inputStream, CTColor.type, xmlOptions);
        }
        
        public static CTColor parse(final Reader reader) throws XmlException, IOException {
            return (CTColor)getTypeLoader().parse(reader, CTColor.type, (XmlOptions)null);
        }
        
        public static CTColor parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTColor)getTypeLoader().parse(reader, CTColor.type, xmlOptions);
        }
        
        public static CTColor parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTColor)getTypeLoader().parse(xmlStreamReader, CTColor.type, (XmlOptions)null);
        }
        
        public static CTColor parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTColor)getTypeLoader().parse(xmlStreamReader, CTColor.type, xmlOptions);
        }
        
        public static CTColor parse(final Node node) throws XmlException {
            return (CTColor)getTypeLoader().parse(node, CTColor.type, (XmlOptions)null);
        }
        
        public static CTColor parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTColor)getTypeLoader().parse(node, CTColor.type, xmlOptions);
        }
        
        @Deprecated
        public static CTColor parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTColor)getTypeLoader().parse(xmlInputStream, CTColor.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTColor parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTColor)getTypeLoader().parse(xmlInputStream, CTColor.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTColor.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTColor.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
