package com.microsoft.schemas.vml;

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

public interface CTF extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTF.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctfbc3atype");
    
    String getEqn();
    
    XmlString xgetEqn();
    
    boolean isSetEqn();
    
    void setEqn(final String p0);
    
    void xsetEqn(final XmlString p0);
    
    void unsetEqn();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTF.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTF newInstance() {
            return (CTF)getTypeLoader().newInstance(CTF.type, (XmlOptions)null);
        }
        
        public static CTF newInstance(final XmlOptions xmlOptions) {
            return (CTF)getTypeLoader().newInstance(CTF.type, xmlOptions);
        }
        
        public static CTF parse(final String s) throws XmlException {
            return (CTF)getTypeLoader().parse(s, CTF.type, (XmlOptions)null);
        }
        
        public static CTF parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTF)getTypeLoader().parse(s, CTF.type, xmlOptions);
        }
        
        public static CTF parse(final File file) throws XmlException, IOException {
            return (CTF)getTypeLoader().parse(file, CTF.type, (XmlOptions)null);
        }
        
        public static CTF parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTF)getTypeLoader().parse(file, CTF.type, xmlOptions);
        }
        
        public static CTF parse(final URL url) throws XmlException, IOException {
            return (CTF)getTypeLoader().parse(url, CTF.type, (XmlOptions)null);
        }
        
        public static CTF parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTF)getTypeLoader().parse(url, CTF.type, xmlOptions);
        }
        
        public static CTF parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTF)getTypeLoader().parse(inputStream, CTF.type, (XmlOptions)null);
        }
        
        public static CTF parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTF)getTypeLoader().parse(inputStream, CTF.type, xmlOptions);
        }
        
        public static CTF parse(final Reader reader) throws XmlException, IOException {
            return (CTF)getTypeLoader().parse(reader, CTF.type, (XmlOptions)null);
        }
        
        public static CTF parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTF)getTypeLoader().parse(reader, CTF.type, xmlOptions);
        }
        
        public static CTF parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTF)getTypeLoader().parse(xmlStreamReader, CTF.type, (XmlOptions)null);
        }
        
        public static CTF parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTF)getTypeLoader().parse(xmlStreamReader, CTF.type, xmlOptions);
        }
        
        public static CTF parse(final Node node) throws XmlException {
            return (CTF)getTypeLoader().parse(node, CTF.type, (XmlOptions)null);
        }
        
        public static CTF parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTF)getTypeLoader().parse(node, CTF.type, xmlOptions);
        }
        
        @Deprecated
        public static CTF parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTF)getTypeLoader().parse(xmlInputStream, CTF.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTF parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTF)getTypeLoader().parse(xmlInputStream, CTF.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTF.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTF.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
