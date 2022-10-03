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

public interface CTSym extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTSym.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctsym0dabtype");
    
    String getFont();
    
    STString xgetFont();
    
    boolean isSetFont();
    
    void setFont(final String p0);
    
    void xsetFont(final STString p0);
    
    void unsetFont();
    
    byte[] getChar();
    
    STShortHexNumber xgetChar();
    
    boolean isSetChar();
    
    void setChar(final byte[] p0);
    
    void xsetChar(final STShortHexNumber p0);
    
    void unsetChar();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTSym.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTSym newInstance() {
            return (CTSym)getTypeLoader().newInstance(CTSym.type, (XmlOptions)null);
        }
        
        public static CTSym newInstance(final XmlOptions xmlOptions) {
            return (CTSym)getTypeLoader().newInstance(CTSym.type, xmlOptions);
        }
        
        public static CTSym parse(final String s) throws XmlException {
            return (CTSym)getTypeLoader().parse(s, CTSym.type, (XmlOptions)null);
        }
        
        public static CTSym parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTSym)getTypeLoader().parse(s, CTSym.type, xmlOptions);
        }
        
        public static CTSym parse(final File file) throws XmlException, IOException {
            return (CTSym)getTypeLoader().parse(file, CTSym.type, (XmlOptions)null);
        }
        
        public static CTSym parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTSym)getTypeLoader().parse(file, CTSym.type, xmlOptions);
        }
        
        public static CTSym parse(final URL url) throws XmlException, IOException {
            return (CTSym)getTypeLoader().parse(url, CTSym.type, (XmlOptions)null);
        }
        
        public static CTSym parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTSym)getTypeLoader().parse(url, CTSym.type, xmlOptions);
        }
        
        public static CTSym parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTSym)getTypeLoader().parse(inputStream, CTSym.type, (XmlOptions)null);
        }
        
        public static CTSym parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTSym)getTypeLoader().parse(inputStream, CTSym.type, xmlOptions);
        }
        
        public static CTSym parse(final Reader reader) throws XmlException, IOException {
            return (CTSym)getTypeLoader().parse(reader, CTSym.type, (XmlOptions)null);
        }
        
        public static CTSym parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTSym)getTypeLoader().parse(reader, CTSym.type, xmlOptions);
        }
        
        public static CTSym parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTSym)getTypeLoader().parse(xmlStreamReader, CTSym.type, (XmlOptions)null);
        }
        
        public static CTSym parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTSym)getTypeLoader().parse(xmlStreamReader, CTSym.type, xmlOptions);
        }
        
        public static CTSym parse(final Node node) throws XmlException {
            return (CTSym)getTypeLoader().parse(node, CTSym.type, (XmlOptions)null);
        }
        
        public static CTSym parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTSym)getTypeLoader().parse(node, CTSym.type, xmlOptions);
        }
        
        @Deprecated
        public static CTSym parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTSym)getTypeLoader().parse(xmlInputStream, CTSym.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTSym parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTSym)getTypeLoader().parse(xmlInputStream, CTSym.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTSym.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTSym.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
