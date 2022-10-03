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
import java.util.List;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTFormulas extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTFormulas.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctformulas808btype");
    
    List<CTF> getFList();
    
    @Deprecated
    CTF[] getFArray();
    
    CTF getFArray(final int p0);
    
    int sizeOfFArray();
    
    void setFArray(final CTF[] p0);
    
    void setFArray(final int p0, final CTF p1);
    
    CTF insertNewF(final int p0);
    
    CTF addNewF();
    
    void removeF(final int p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTFormulas.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTFormulas newInstance() {
            return (CTFormulas)getTypeLoader().newInstance(CTFormulas.type, (XmlOptions)null);
        }
        
        public static CTFormulas newInstance(final XmlOptions xmlOptions) {
            return (CTFormulas)getTypeLoader().newInstance(CTFormulas.type, xmlOptions);
        }
        
        public static CTFormulas parse(final String s) throws XmlException {
            return (CTFormulas)getTypeLoader().parse(s, CTFormulas.type, (XmlOptions)null);
        }
        
        public static CTFormulas parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTFormulas)getTypeLoader().parse(s, CTFormulas.type, xmlOptions);
        }
        
        public static CTFormulas parse(final File file) throws XmlException, IOException {
            return (CTFormulas)getTypeLoader().parse(file, CTFormulas.type, (XmlOptions)null);
        }
        
        public static CTFormulas parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTFormulas)getTypeLoader().parse(file, CTFormulas.type, xmlOptions);
        }
        
        public static CTFormulas parse(final URL url) throws XmlException, IOException {
            return (CTFormulas)getTypeLoader().parse(url, CTFormulas.type, (XmlOptions)null);
        }
        
        public static CTFormulas parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTFormulas)getTypeLoader().parse(url, CTFormulas.type, xmlOptions);
        }
        
        public static CTFormulas parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTFormulas)getTypeLoader().parse(inputStream, CTFormulas.type, (XmlOptions)null);
        }
        
        public static CTFormulas parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTFormulas)getTypeLoader().parse(inputStream, CTFormulas.type, xmlOptions);
        }
        
        public static CTFormulas parse(final Reader reader) throws XmlException, IOException {
            return (CTFormulas)getTypeLoader().parse(reader, CTFormulas.type, (XmlOptions)null);
        }
        
        public static CTFormulas parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTFormulas)getTypeLoader().parse(reader, CTFormulas.type, xmlOptions);
        }
        
        public static CTFormulas parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTFormulas)getTypeLoader().parse(xmlStreamReader, CTFormulas.type, (XmlOptions)null);
        }
        
        public static CTFormulas parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTFormulas)getTypeLoader().parse(xmlStreamReader, CTFormulas.type, xmlOptions);
        }
        
        public static CTFormulas parse(final Node node) throws XmlException {
            return (CTFormulas)getTypeLoader().parse(node, CTFormulas.type, (XmlOptions)null);
        }
        
        public static CTFormulas parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTFormulas)getTypeLoader().parse(node, CTFormulas.type, xmlOptions);
        }
        
        @Deprecated
        public static CTFormulas parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTFormulas)getTypeLoader().parse(xmlInputStream, CTFormulas.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTFormulas parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTFormulas)getTypeLoader().parse(xmlInputStream, CTFormulas.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTFormulas.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTFormulas.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
