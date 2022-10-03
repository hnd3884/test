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

public interface CTFontName extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTFontName.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctfontname2dc3type");
    
    String getVal();
    
    STXstring xgetVal();
    
    void setVal(final String p0);
    
    void xsetVal(final STXstring p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTFontName.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTFontName newInstance() {
            return (CTFontName)getTypeLoader().newInstance(CTFontName.type, (XmlOptions)null);
        }
        
        public static CTFontName newInstance(final XmlOptions xmlOptions) {
            return (CTFontName)getTypeLoader().newInstance(CTFontName.type, xmlOptions);
        }
        
        public static CTFontName parse(final String s) throws XmlException {
            return (CTFontName)getTypeLoader().parse(s, CTFontName.type, (XmlOptions)null);
        }
        
        public static CTFontName parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTFontName)getTypeLoader().parse(s, CTFontName.type, xmlOptions);
        }
        
        public static CTFontName parse(final File file) throws XmlException, IOException {
            return (CTFontName)getTypeLoader().parse(file, CTFontName.type, (XmlOptions)null);
        }
        
        public static CTFontName parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTFontName)getTypeLoader().parse(file, CTFontName.type, xmlOptions);
        }
        
        public static CTFontName parse(final URL url) throws XmlException, IOException {
            return (CTFontName)getTypeLoader().parse(url, CTFontName.type, (XmlOptions)null);
        }
        
        public static CTFontName parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTFontName)getTypeLoader().parse(url, CTFontName.type, xmlOptions);
        }
        
        public static CTFontName parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTFontName)getTypeLoader().parse(inputStream, CTFontName.type, (XmlOptions)null);
        }
        
        public static CTFontName parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTFontName)getTypeLoader().parse(inputStream, CTFontName.type, xmlOptions);
        }
        
        public static CTFontName parse(final Reader reader) throws XmlException, IOException {
            return (CTFontName)getTypeLoader().parse(reader, CTFontName.type, (XmlOptions)null);
        }
        
        public static CTFontName parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTFontName)getTypeLoader().parse(reader, CTFontName.type, xmlOptions);
        }
        
        public static CTFontName parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTFontName)getTypeLoader().parse(xmlStreamReader, CTFontName.type, (XmlOptions)null);
        }
        
        public static CTFontName parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTFontName)getTypeLoader().parse(xmlStreamReader, CTFontName.type, xmlOptions);
        }
        
        public static CTFontName parse(final Node node) throws XmlException {
            return (CTFontName)getTypeLoader().parse(node, CTFontName.type, (XmlOptions)null);
        }
        
        public static CTFontName parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTFontName)getTypeLoader().parse(node, CTFontName.type, xmlOptions);
        }
        
        @Deprecated
        public static CTFontName parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTFontName)getTypeLoader().parse(xmlInputStream, CTFontName.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTFontName parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTFontName)getTypeLoader().parse(xmlInputStream, CTFontName.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTFontName.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTFontName.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
