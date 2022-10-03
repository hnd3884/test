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

public interface CTRuby extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTRuby.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctruby9dcetype");
    
    CTRubyPr getRubyPr();
    
    void setRubyPr(final CTRubyPr p0);
    
    CTRubyPr addNewRubyPr();
    
    CTRubyContent getRt();
    
    void setRt(final CTRubyContent p0);
    
    CTRubyContent addNewRt();
    
    CTRubyContent getRubyBase();
    
    void setRubyBase(final CTRubyContent p0);
    
    CTRubyContent addNewRubyBase();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTRuby.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTRuby newInstance() {
            return (CTRuby)getTypeLoader().newInstance(CTRuby.type, (XmlOptions)null);
        }
        
        public static CTRuby newInstance(final XmlOptions xmlOptions) {
            return (CTRuby)getTypeLoader().newInstance(CTRuby.type, xmlOptions);
        }
        
        public static CTRuby parse(final String s) throws XmlException {
            return (CTRuby)getTypeLoader().parse(s, CTRuby.type, (XmlOptions)null);
        }
        
        public static CTRuby parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTRuby)getTypeLoader().parse(s, CTRuby.type, xmlOptions);
        }
        
        public static CTRuby parse(final File file) throws XmlException, IOException {
            return (CTRuby)getTypeLoader().parse(file, CTRuby.type, (XmlOptions)null);
        }
        
        public static CTRuby parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTRuby)getTypeLoader().parse(file, CTRuby.type, xmlOptions);
        }
        
        public static CTRuby parse(final URL url) throws XmlException, IOException {
            return (CTRuby)getTypeLoader().parse(url, CTRuby.type, (XmlOptions)null);
        }
        
        public static CTRuby parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTRuby)getTypeLoader().parse(url, CTRuby.type, xmlOptions);
        }
        
        public static CTRuby parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTRuby)getTypeLoader().parse(inputStream, CTRuby.type, (XmlOptions)null);
        }
        
        public static CTRuby parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTRuby)getTypeLoader().parse(inputStream, CTRuby.type, xmlOptions);
        }
        
        public static CTRuby parse(final Reader reader) throws XmlException, IOException {
            return (CTRuby)getTypeLoader().parse(reader, CTRuby.type, (XmlOptions)null);
        }
        
        public static CTRuby parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTRuby)getTypeLoader().parse(reader, CTRuby.type, xmlOptions);
        }
        
        public static CTRuby parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTRuby)getTypeLoader().parse(xmlStreamReader, CTRuby.type, (XmlOptions)null);
        }
        
        public static CTRuby parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTRuby)getTypeLoader().parse(xmlStreamReader, CTRuby.type, xmlOptions);
        }
        
        public static CTRuby parse(final Node node) throws XmlException {
            return (CTRuby)getTypeLoader().parse(node, CTRuby.type, (XmlOptions)null);
        }
        
        public static CTRuby parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTRuby)getTypeLoader().parse(node, CTRuby.type, xmlOptions);
        }
        
        @Deprecated
        public static CTRuby parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTRuby)getTypeLoader().parse(xmlInputStream, CTRuby.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTRuby parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTRuby)getTypeLoader().parse(xmlInputStream, CTRuby.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTRuby.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTRuby.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
