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

public interface CTTblWidth extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTTblWidth.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("cttblwidthec40type");
    
    BigInteger getW();
    
    STDecimalNumber xgetW();
    
    boolean isSetW();
    
    void setW(final BigInteger p0);
    
    void xsetW(final STDecimalNumber p0);
    
    void unsetW();
    
    STTblWidth.Enum getType();
    
    STTblWidth xgetType();
    
    boolean isSetType();
    
    void setType(final STTblWidth.Enum p0);
    
    void xsetType(final STTblWidth p0);
    
    void unsetType();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTTblWidth.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTTblWidth newInstance() {
            return (CTTblWidth)getTypeLoader().newInstance(CTTblWidth.type, (XmlOptions)null);
        }
        
        public static CTTblWidth newInstance(final XmlOptions xmlOptions) {
            return (CTTblWidth)getTypeLoader().newInstance(CTTblWidth.type, xmlOptions);
        }
        
        public static CTTblWidth parse(final String s) throws XmlException {
            return (CTTblWidth)getTypeLoader().parse(s, CTTblWidth.type, (XmlOptions)null);
        }
        
        public static CTTblWidth parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTTblWidth)getTypeLoader().parse(s, CTTblWidth.type, xmlOptions);
        }
        
        public static CTTblWidth parse(final File file) throws XmlException, IOException {
            return (CTTblWidth)getTypeLoader().parse(file, CTTblWidth.type, (XmlOptions)null);
        }
        
        public static CTTblWidth parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTblWidth)getTypeLoader().parse(file, CTTblWidth.type, xmlOptions);
        }
        
        public static CTTblWidth parse(final URL url) throws XmlException, IOException {
            return (CTTblWidth)getTypeLoader().parse(url, CTTblWidth.type, (XmlOptions)null);
        }
        
        public static CTTblWidth parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTblWidth)getTypeLoader().parse(url, CTTblWidth.type, xmlOptions);
        }
        
        public static CTTblWidth parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTTblWidth)getTypeLoader().parse(inputStream, CTTblWidth.type, (XmlOptions)null);
        }
        
        public static CTTblWidth parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTblWidth)getTypeLoader().parse(inputStream, CTTblWidth.type, xmlOptions);
        }
        
        public static CTTblWidth parse(final Reader reader) throws XmlException, IOException {
            return (CTTblWidth)getTypeLoader().parse(reader, CTTblWidth.type, (XmlOptions)null);
        }
        
        public static CTTblWidth parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTblWidth)getTypeLoader().parse(reader, CTTblWidth.type, xmlOptions);
        }
        
        public static CTTblWidth parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTTblWidth)getTypeLoader().parse(xmlStreamReader, CTTblWidth.type, (XmlOptions)null);
        }
        
        public static CTTblWidth parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTTblWidth)getTypeLoader().parse(xmlStreamReader, CTTblWidth.type, xmlOptions);
        }
        
        public static CTTblWidth parse(final Node node) throws XmlException {
            return (CTTblWidth)getTypeLoader().parse(node, CTTblWidth.type, (XmlOptions)null);
        }
        
        public static CTTblWidth parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTTblWidth)getTypeLoader().parse(node, CTTblWidth.type, xmlOptions);
        }
        
        @Deprecated
        public static CTTblWidth parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTTblWidth)getTypeLoader().parse(xmlInputStream, CTTblWidth.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTTblWidth parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTTblWidth)getTypeLoader().parse(xmlInputStream, CTTblWidth.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTTblWidth.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTTblWidth.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
