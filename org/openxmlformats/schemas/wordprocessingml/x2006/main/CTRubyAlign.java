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

public interface CTRubyAlign extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTRubyAlign.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctrubyalign41e7type");
    
    STRubyAlign.Enum getVal();
    
    STRubyAlign xgetVal();
    
    void setVal(final STRubyAlign.Enum p0);
    
    void xsetVal(final STRubyAlign p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTRubyAlign.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTRubyAlign newInstance() {
            return (CTRubyAlign)getTypeLoader().newInstance(CTRubyAlign.type, (XmlOptions)null);
        }
        
        public static CTRubyAlign newInstance(final XmlOptions xmlOptions) {
            return (CTRubyAlign)getTypeLoader().newInstance(CTRubyAlign.type, xmlOptions);
        }
        
        public static CTRubyAlign parse(final String s) throws XmlException {
            return (CTRubyAlign)getTypeLoader().parse(s, CTRubyAlign.type, (XmlOptions)null);
        }
        
        public static CTRubyAlign parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTRubyAlign)getTypeLoader().parse(s, CTRubyAlign.type, xmlOptions);
        }
        
        public static CTRubyAlign parse(final File file) throws XmlException, IOException {
            return (CTRubyAlign)getTypeLoader().parse(file, CTRubyAlign.type, (XmlOptions)null);
        }
        
        public static CTRubyAlign parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTRubyAlign)getTypeLoader().parse(file, CTRubyAlign.type, xmlOptions);
        }
        
        public static CTRubyAlign parse(final URL url) throws XmlException, IOException {
            return (CTRubyAlign)getTypeLoader().parse(url, CTRubyAlign.type, (XmlOptions)null);
        }
        
        public static CTRubyAlign parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTRubyAlign)getTypeLoader().parse(url, CTRubyAlign.type, xmlOptions);
        }
        
        public static CTRubyAlign parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTRubyAlign)getTypeLoader().parse(inputStream, CTRubyAlign.type, (XmlOptions)null);
        }
        
        public static CTRubyAlign parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTRubyAlign)getTypeLoader().parse(inputStream, CTRubyAlign.type, xmlOptions);
        }
        
        public static CTRubyAlign parse(final Reader reader) throws XmlException, IOException {
            return (CTRubyAlign)getTypeLoader().parse(reader, CTRubyAlign.type, (XmlOptions)null);
        }
        
        public static CTRubyAlign parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTRubyAlign)getTypeLoader().parse(reader, CTRubyAlign.type, xmlOptions);
        }
        
        public static CTRubyAlign parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTRubyAlign)getTypeLoader().parse(xmlStreamReader, CTRubyAlign.type, (XmlOptions)null);
        }
        
        public static CTRubyAlign parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTRubyAlign)getTypeLoader().parse(xmlStreamReader, CTRubyAlign.type, xmlOptions);
        }
        
        public static CTRubyAlign parse(final Node node) throws XmlException {
            return (CTRubyAlign)getTypeLoader().parse(node, CTRubyAlign.type, (XmlOptions)null);
        }
        
        public static CTRubyAlign parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTRubyAlign)getTypeLoader().parse(node, CTRubyAlign.type, xmlOptions);
        }
        
        @Deprecated
        public static CTRubyAlign parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTRubyAlign)getTypeLoader().parse(xmlInputStream, CTRubyAlign.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTRubyAlign parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTRubyAlign)getTypeLoader().parse(xmlInputStream, CTRubyAlign.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTRubyAlign.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTRubyAlign.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
