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
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTFontSize extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTFontSize.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctfontsizeb3b9type");
    
    double getVal();
    
    XmlDouble xgetVal();
    
    void setVal(final double p0);
    
    void xsetVal(final XmlDouble p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTFontSize.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTFontSize newInstance() {
            return (CTFontSize)getTypeLoader().newInstance(CTFontSize.type, (XmlOptions)null);
        }
        
        public static CTFontSize newInstance(final XmlOptions xmlOptions) {
            return (CTFontSize)getTypeLoader().newInstance(CTFontSize.type, xmlOptions);
        }
        
        public static CTFontSize parse(final String s) throws XmlException {
            return (CTFontSize)getTypeLoader().parse(s, CTFontSize.type, (XmlOptions)null);
        }
        
        public static CTFontSize parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTFontSize)getTypeLoader().parse(s, CTFontSize.type, xmlOptions);
        }
        
        public static CTFontSize parse(final File file) throws XmlException, IOException {
            return (CTFontSize)getTypeLoader().parse(file, CTFontSize.type, (XmlOptions)null);
        }
        
        public static CTFontSize parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTFontSize)getTypeLoader().parse(file, CTFontSize.type, xmlOptions);
        }
        
        public static CTFontSize parse(final URL url) throws XmlException, IOException {
            return (CTFontSize)getTypeLoader().parse(url, CTFontSize.type, (XmlOptions)null);
        }
        
        public static CTFontSize parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTFontSize)getTypeLoader().parse(url, CTFontSize.type, xmlOptions);
        }
        
        public static CTFontSize parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTFontSize)getTypeLoader().parse(inputStream, CTFontSize.type, (XmlOptions)null);
        }
        
        public static CTFontSize parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTFontSize)getTypeLoader().parse(inputStream, CTFontSize.type, xmlOptions);
        }
        
        public static CTFontSize parse(final Reader reader) throws XmlException, IOException {
            return (CTFontSize)getTypeLoader().parse(reader, CTFontSize.type, (XmlOptions)null);
        }
        
        public static CTFontSize parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTFontSize)getTypeLoader().parse(reader, CTFontSize.type, xmlOptions);
        }
        
        public static CTFontSize parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTFontSize)getTypeLoader().parse(xmlStreamReader, CTFontSize.type, (XmlOptions)null);
        }
        
        public static CTFontSize parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTFontSize)getTypeLoader().parse(xmlStreamReader, CTFontSize.type, xmlOptions);
        }
        
        public static CTFontSize parse(final Node node) throws XmlException {
            return (CTFontSize)getTypeLoader().parse(node, CTFontSize.type, (XmlOptions)null);
        }
        
        public static CTFontSize parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTFontSize)getTypeLoader().parse(node, CTFontSize.type, xmlOptions);
        }
        
        @Deprecated
        public static CTFontSize parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTFontSize)getTypeLoader().parse(xmlInputStream, CTFontSize.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTFontSize parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTFontSize)getTypeLoader().parse(xmlInputStream, CTFontSize.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTFontSize.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTFontSize.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
