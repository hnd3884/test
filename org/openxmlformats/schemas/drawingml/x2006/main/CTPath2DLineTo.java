package org.openxmlformats.schemas.drawingml.x2006.main;

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

public interface CTPath2DLineTo extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTPath2DLineTo.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctpath2dlineto4f41type");
    
    CTAdjPoint2D getPt();
    
    void setPt(final CTAdjPoint2D p0);
    
    CTAdjPoint2D addNewPt();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTPath2DLineTo.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTPath2DLineTo newInstance() {
            return (CTPath2DLineTo)getTypeLoader().newInstance(CTPath2DLineTo.type, (XmlOptions)null);
        }
        
        public static CTPath2DLineTo newInstance(final XmlOptions xmlOptions) {
            return (CTPath2DLineTo)getTypeLoader().newInstance(CTPath2DLineTo.type, xmlOptions);
        }
        
        public static CTPath2DLineTo parse(final String s) throws XmlException {
            return (CTPath2DLineTo)getTypeLoader().parse(s, CTPath2DLineTo.type, (XmlOptions)null);
        }
        
        public static CTPath2DLineTo parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTPath2DLineTo)getTypeLoader().parse(s, CTPath2DLineTo.type, xmlOptions);
        }
        
        public static CTPath2DLineTo parse(final File file) throws XmlException, IOException {
            return (CTPath2DLineTo)getTypeLoader().parse(file, CTPath2DLineTo.type, (XmlOptions)null);
        }
        
        public static CTPath2DLineTo parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTPath2DLineTo)getTypeLoader().parse(file, CTPath2DLineTo.type, xmlOptions);
        }
        
        public static CTPath2DLineTo parse(final URL url) throws XmlException, IOException {
            return (CTPath2DLineTo)getTypeLoader().parse(url, CTPath2DLineTo.type, (XmlOptions)null);
        }
        
        public static CTPath2DLineTo parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTPath2DLineTo)getTypeLoader().parse(url, CTPath2DLineTo.type, xmlOptions);
        }
        
        public static CTPath2DLineTo parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTPath2DLineTo)getTypeLoader().parse(inputStream, CTPath2DLineTo.type, (XmlOptions)null);
        }
        
        public static CTPath2DLineTo parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTPath2DLineTo)getTypeLoader().parse(inputStream, CTPath2DLineTo.type, xmlOptions);
        }
        
        public static CTPath2DLineTo parse(final Reader reader) throws XmlException, IOException {
            return (CTPath2DLineTo)getTypeLoader().parse(reader, CTPath2DLineTo.type, (XmlOptions)null);
        }
        
        public static CTPath2DLineTo parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTPath2DLineTo)getTypeLoader().parse(reader, CTPath2DLineTo.type, xmlOptions);
        }
        
        public static CTPath2DLineTo parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTPath2DLineTo)getTypeLoader().parse(xmlStreamReader, CTPath2DLineTo.type, (XmlOptions)null);
        }
        
        public static CTPath2DLineTo parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTPath2DLineTo)getTypeLoader().parse(xmlStreamReader, CTPath2DLineTo.type, xmlOptions);
        }
        
        public static CTPath2DLineTo parse(final Node node) throws XmlException {
            return (CTPath2DLineTo)getTypeLoader().parse(node, CTPath2DLineTo.type, (XmlOptions)null);
        }
        
        public static CTPath2DLineTo parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTPath2DLineTo)getTypeLoader().parse(node, CTPath2DLineTo.type, xmlOptions);
        }
        
        @Deprecated
        public static CTPath2DLineTo parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTPath2DLineTo)getTypeLoader().parse(xmlInputStream, CTPath2DLineTo.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTPath2DLineTo parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTPath2DLineTo)getTypeLoader().parse(xmlInputStream, CTPath2DLineTo.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTPath2DLineTo.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTPath2DLineTo.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
