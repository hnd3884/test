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

public interface CTAdjPoint2D extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTAdjPoint2D.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctadjpoint2d1656type");
    
    Object getX();
    
    STAdjCoordinate xgetX();
    
    void setX(final Object p0);
    
    void xsetX(final STAdjCoordinate p0);
    
    Object getY();
    
    STAdjCoordinate xgetY();
    
    void setY(final Object p0);
    
    void xsetY(final STAdjCoordinate p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTAdjPoint2D.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTAdjPoint2D newInstance() {
            return (CTAdjPoint2D)getTypeLoader().newInstance(CTAdjPoint2D.type, (XmlOptions)null);
        }
        
        public static CTAdjPoint2D newInstance(final XmlOptions xmlOptions) {
            return (CTAdjPoint2D)getTypeLoader().newInstance(CTAdjPoint2D.type, xmlOptions);
        }
        
        public static CTAdjPoint2D parse(final String s) throws XmlException {
            return (CTAdjPoint2D)getTypeLoader().parse(s, CTAdjPoint2D.type, (XmlOptions)null);
        }
        
        public static CTAdjPoint2D parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTAdjPoint2D)getTypeLoader().parse(s, CTAdjPoint2D.type, xmlOptions);
        }
        
        public static CTAdjPoint2D parse(final File file) throws XmlException, IOException {
            return (CTAdjPoint2D)getTypeLoader().parse(file, CTAdjPoint2D.type, (XmlOptions)null);
        }
        
        public static CTAdjPoint2D parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTAdjPoint2D)getTypeLoader().parse(file, CTAdjPoint2D.type, xmlOptions);
        }
        
        public static CTAdjPoint2D parse(final URL url) throws XmlException, IOException {
            return (CTAdjPoint2D)getTypeLoader().parse(url, CTAdjPoint2D.type, (XmlOptions)null);
        }
        
        public static CTAdjPoint2D parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTAdjPoint2D)getTypeLoader().parse(url, CTAdjPoint2D.type, xmlOptions);
        }
        
        public static CTAdjPoint2D parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTAdjPoint2D)getTypeLoader().parse(inputStream, CTAdjPoint2D.type, (XmlOptions)null);
        }
        
        public static CTAdjPoint2D parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTAdjPoint2D)getTypeLoader().parse(inputStream, CTAdjPoint2D.type, xmlOptions);
        }
        
        public static CTAdjPoint2D parse(final Reader reader) throws XmlException, IOException {
            return (CTAdjPoint2D)getTypeLoader().parse(reader, CTAdjPoint2D.type, (XmlOptions)null);
        }
        
        public static CTAdjPoint2D parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTAdjPoint2D)getTypeLoader().parse(reader, CTAdjPoint2D.type, xmlOptions);
        }
        
        public static CTAdjPoint2D parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTAdjPoint2D)getTypeLoader().parse(xmlStreamReader, CTAdjPoint2D.type, (XmlOptions)null);
        }
        
        public static CTAdjPoint2D parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTAdjPoint2D)getTypeLoader().parse(xmlStreamReader, CTAdjPoint2D.type, xmlOptions);
        }
        
        public static CTAdjPoint2D parse(final Node node) throws XmlException {
            return (CTAdjPoint2D)getTypeLoader().parse(node, CTAdjPoint2D.type, (XmlOptions)null);
        }
        
        public static CTAdjPoint2D parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTAdjPoint2D)getTypeLoader().parse(node, CTAdjPoint2D.type, xmlOptions);
        }
        
        @Deprecated
        public static CTAdjPoint2D parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTAdjPoint2D)getTypeLoader().parse(xmlInputStream, CTAdjPoint2D.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTAdjPoint2D parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTAdjPoint2D)getTypeLoader().parse(xmlInputStream, CTAdjPoint2D.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTAdjPoint2D.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTAdjPoint2D.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
