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
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTRgbColor extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTRgbColor.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctrgbcolor95dftype");
    
    byte[] getRgb();
    
    STUnsignedIntHex xgetRgb();
    
    boolean isSetRgb();
    
    void setRgb(final byte[] p0);
    
    void xsetRgb(final STUnsignedIntHex p0);
    
    void unsetRgb();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTRgbColor.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTRgbColor newInstance() {
            return (CTRgbColor)getTypeLoader().newInstance(CTRgbColor.type, (XmlOptions)null);
        }
        
        public static CTRgbColor newInstance(final XmlOptions xmlOptions) {
            return (CTRgbColor)getTypeLoader().newInstance(CTRgbColor.type, xmlOptions);
        }
        
        public static CTRgbColor parse(final String s) throws XmlException {
            return (CTRgbColor)getTypeLoader().parse(s, CTRgbColor.type, (XmlOptions)null);
        }
        
        public static CTRgbColor parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTRgbColor)getTypeLoader().parse(s, CTRgbColor.type, xmlOptions);
        }
        
        public static CTRgbColor parse(final File file) throws XmlException, IOException {
            return (CTRgbColor)getTypeLoader().parse(file, CTRgbColor.type, (XmlOptions)null);
        }
        
        public static CTRgbColor parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTRgbColor)getTypeLoader().parse(file, CTRgbColor.type, xmlOptions);
        }
        
        public static CTRgbColor parse(final URL url) throws XmlException, IOException {
            return (CTRgbColor)getTypeLoader().parse(url, CTRgbColor.type, (XmlOptions)null);
        }
        
        public static CTRgbColor parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTRgbColor)getTypeLoader().parse(url, CTRgbColor.type, xmlOptions);
        }
        
        public static CTRgbColor parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTRgbColor)getTypeLoader().parse(inputStream, CTRgbColor.type, (XmlOptions)null);
        }
        
        public static CTRgbColor parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTRgbColor)getTypeLoader().parse(inputStream, CTRgbColor.type, xmlOptions);
        }
        
        public static CTRgbColor parse(final Reader reader) throws XmlException, IOException {
            return (CTRgbColor)getTypeLoader().parse(reader, CTRgbColor.type, (XmlOptions)null);
        }
        
        public static CTRgbColor parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTRgbColor)getTypeLoader().parse(reader, CTRgbColor.type, xmlOptions);
        }
        
        public static CTRgbColor parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTRgbColor)getTypeLoader().parse(xmlStreamReader, CTRgbColor.type, (XmlOptions)null);
        }
        
        public static CTRgbColor parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTRgbColor)getTypeLoader().parse(xmlStreamReader, CTRgbColor.type, xmlOptions);
        }
        
        public static CTRgbColor parse(final Node node) throws XmlException {
            return (CTRgbColor)getTypeLoader().parse(node, CTRgbColor.type, (XmlOptions)null);
        }
        
        public static CTRgbColor parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTRgbColor)getTypeLoader().parse(node, CTRgbColor.type, xmlOptions);
        }
        
        @Deprecated
        public static CTRgbColor parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTRgbColor)getTypeLoader().parse(xmlInputStream, CTRgbColor.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTRgbColor parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTRgbColor)getTypeLoader().parse(xmlInputStream, CTRgbColor.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTRgbColor.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTRgbColor.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
