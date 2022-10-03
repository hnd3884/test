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

public interface CTPoint2D extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTPoint2D.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctpoint2d8193type");
    
    long getX();
    
    STCoordinate xgetX();
    
    void setX(final long p0);
    
    void xsetX(final STCoordinate p0);
    
    long getY();
    
    STCoordinate xgetY();
    
    void setY(final long p0);
    
    void xsetY(final STCoordinate p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTPoint2D.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTPoint2D newInstance() {
            return (CTPoint2D)getTypeLoader().newInstance(CTPoint2D.type, (XmlOptions)null);
        }
        
        public static CTPoint2D newInstance(final XmlOptions xmlOptions) {
            return (CTPoint2D)getTypeLoader().newInstance(CTPoint2D.type, xmlOptions);
        }
        
        public static CTPoint2D parse(final String s) throws XmlException {
            return (CTPoint2D)getTypeLoader().parse(s, CTPoint2D.type, (XmlOptions)null);
        }
        
        public static CTPoint2D parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTPoint2D)getTypeLoader().parse(s, CTPoint2D.type, xmlOptions);
        }
        
        public static CTPoint2D parse(final File file) throws XmlException, IOException {
            return (CTPoint2D)getTypeLoader().parse(file, CTPoint2D.type, (XmlOptions)null);
        }
        
        public static CTPoint2D parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTPoint2D)getTypeLoader().parse(file, CTPoint2D.type, xmlOptions);
        }
        
        public static CTPoint2D parse(final URL url) throws XmlException, IOException {
            return (CTPoint2D)getTypeLoader().parse(url, CTPoint2D.type, (XmlOptions)null);
        }
        
        public static CTPoint2D parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTPoint2D)getTypeLoader().parse(url, CTPoint2D.type, xmlOptions);
        }
        
        public static CTPoint2D parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTPoint2D)getTypeLoader().parse(inputStream, CTPoint2D.type, (XmlOptions)null);
        }
        
        public static CTPoint2D parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTPoint2D)getTypeLoader().parse(inputStream, CTPoint2D.type, xmlOptions);
        }
        
        public static CTPoint2D parse(final Reader reader) throws XmlException, IOException {
            return (CTPoint2D)getTypeLoader().parse(reader, CTPoint2D.type, (XmlOptions)null);
        }
        
        public static CTPoint2D parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTPoint2D)getTypeLoader().parse(reader, CTPoint2D.type, xmlOptions);
        }
        
        public static CTPoint2D parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTPoint2D)getTypeLoader().parse(xmlStreamReader, CTPoint2D.type, (XmlOptions)null);
        }
        
        public static CTPoint2D parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTPoint2D)getTypeLoader().parse(xmlStreamReader, CTPoint2D.type, xmlOptions);
        }
        
        public static CTPoint2D parse(final Node node) throws XmlException {
            return (CTPoint2D)getTypeLoader().parse(node, CTPoint2D.type, (XmlOptions)null);
        }
        
        public static CTPoint2D parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTPoint2D)getTypeLoader().parse(node, CTPoint2D.type, xmlOptions);
        }
        
        @Deprecated
        public static CTPoint2D parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTPoint2D)getTypeLoader().parse(xmlInputStream, CTPoint2D.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTPoint2D parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTPoint2D)getTypeLoader().parse(xmlInputStream, CTPoint2D.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTPoint2D.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTPoint2D.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
