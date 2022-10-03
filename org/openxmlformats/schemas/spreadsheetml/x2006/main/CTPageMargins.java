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

public interface CTPageMargins extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTPageMargins.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctpagemargins5455type");
    
    double getLeft();
    
    XmlDouble xgetLeft();
    
    void setLeft(final double p0);
    
    void xsetLeft(final XmlDouble p0);
    
    double getRight();
    
    XmlDouble xgetRight();
    
    void setRight(final double p0);
    
    void xsetRight(final XmlDouble p0);
    
    double getTop();
    
    XmlDouble xgetTop();
    
    void setTop(final double p0);
    
    void xsetTop(final XmlDouble p0);
    
    double getBottom();
    
    XmlDouble xgetBottom();
    
    void setBottom(final double p0);
    
    void xsetBottom(final XmlDouble p0);
    
    double getHeader();
    
    XmlDouble xgetHeader();
    
    void setHeader(final double p0);
    
    void xsetHeader(final XmlDouble p0);
    
    double getFooter();
    
    XmlDouble xgetFooter();
    
    void setFooter(final double p0);
    
    void xsetFooter(final XmlDouble p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTPageMargins.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTPageMargins newInstance() {
            return (CTPageMargins)getTypeLoader().newInstance(CTPageMargins.type, (XmlOptions)null);
        }
        
        public static CTPageMargins newInstance(final XmlOptions xmlOptions) {
            return (CTPageMargins)getTypeLoader().newInstance(CTPageMargins.type, xmlOptions);
        }
        
        public static CTPageMargins parse(final String s) throws XmlException {
            return (CTPageMargins)getTypeLoader().parse(s, CTPageMargins.type, (XmlOptions)null);
        }
        
        public static CTPageMargins parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTPageMargins)getTypeLoader().parse(s, CTPageMargins.type, xmlOptions);
        }
        
        public static CTPageMargins parse(final File file) throws XmlException, IOException {
            return (CTPageMargins)getTypeLoader().parse(file, CTPageMargins.type, (XmlOptions)null);
        }
        
        public static CTPageMargins parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTPageMargins)getTypeLoader().parse(file, CTPageMargins.type, xmlOptions);
        }
        
        public static CTPageMargins parse(final URL url) throws XmlException, IOException {
            return (CTPageMargins)getTypeLoader().parse(url, CTPageMargins.type, (XmlOptions)null);
        }
        
        public static CTPageMargins parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTPageMargins)getTypeLoader().parse(url, CTPageMargins.type, xmlOptions);
        }
        
        public static CTPageMargins parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTPageMargins)getTypeLoader().parse(inputStream, CTPageMargins.type, (XmlOptions)null);
        }
        
        public static CTPageMargins parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTPageMargins)getTypeLoader().parse(inputStream, CTPageMargins.type, xmlOptions);
        }
        
        public static CTPageMargins parse(final Reader reader) throws XmlException, IOException {
            return (CTPageMargins)getTypeLoader().parse(reader, CTPageMargins.type, (XmlOptions)null);
        }
        
        public static CTPageMargins parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTPageMargins)getTypeLoader().parse(reader, CTPageMargins.type, xmlOptions);
        }
        
        public static CTPageMargins parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTPageMargins)getTypeLoader().parse(xmlStreamReader, CTPageMargins.type, (XmlOptions)null);
        }
        
        public static CTPageMargins parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTPageMargins)getTypeLoader().parse(xmlStreamReader, CTPageMargins.type, xmlOptions);
        }
        
        public static CTPageMargins parse(final Node node) throws XmlException {
            return (CTPageMargins)getTypeLoader().parse(node, CTPageMargins.type, (XmlOptions)null);
        }
        
        public static CTPageMargins parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTPageMargins)getTypeLoader().parse(node, CTPageMargins.type, xmlOptions);
        }
        
        @Deprecated
        public static CTPageMargins parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTPageMargins)getTypeLoader().parse(xmlInputStream, CTPageMargins.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTPageMargins parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTPageMargins)getTypeLoader().parse(xmlInputStream, CTPageMargins.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTPageMargins.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTPageMargins.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
