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

public interface CTConnectionSite extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTConnectionSite.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctconnectionsite6660type");
    
    CTAdjPoint2D getPos();
    
    void setPos(final CTAdjPoint2D p0);
    
    CTAdjPoint2D addNewPos();
    
    Object getAng();
    
    STAdjAngle xgetAng();
    
    void setAng(final Object p0);
    
    void xsetAng(final STAdjAngle p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTConnectionSite.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTConnectionSite newInstance() {
            return (CTConnectionSite)getTypeLoader().newInstance(CTConnectionSite.type, (XmlOptions)null);
        }
        
        public static CTConnectionSite newInstance(final XmlOptions xmlOptions) {
            return (CTConnectionSite)getTypeLoader().newInstance(CTConnectionSite.type, xmlOptions);
        }
        
        public static CTConnectionSite parse(final String s) throws XmlException {
            return (CTConnectionSite)getTypeLoader().parse(s, CTConnectionSite.type, (XmlOptions)null);
        }
        
        public static CTConnectionSite parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTConnectionSite)getTypeLoader().parse(s, CTConnectionSite.type, xmlOptions);
        }
        
        public static CTConnectionSite parse(final File file) throws XmlException, IOException {
            return (CTConnectionSite)getTypeLoader().parse(file, CTConnectionSite.type, (XmlOptions)null);
        }
        
        public static CTConnectionSite parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTConnectionSite)getTypeLoader().parse(file, CTConnectionSite.type, xmlOptions);
        }
        
        public static CTConnectionSite parse(final URL url) throws XmlException, IOException {
            return (CTConnectionSite)getTypeLoader().parse(url, CTConnectionSite.type, (XmlOptions)null);
        }
        
        public static CTConnectionSite parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTConnectionSite)getTypeLoader().parse(url, CTConnectionSite.type, xmlOptions);
        }
        
        public static CTConnectionSite parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTConnectionSite)getTypeLoader().parse(inputStream, CTConnectionSite.type, (XmlOptions)null);
        }
        
        public static CTConnectionSite parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTConnectionSite)getTypeLoader().parse(inputStream, CTConnectionSite.type, xmlOptions);
        }
        
        public static CTConnectionSite parse(final Reader reader) throws XmlException, IOException {
            return (CTConnectionSite)getTypeLoader().parse(reader, CTConnectionSite.type, (XmlOptions)null);
        }
        
        public static CTConnectionSite parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTConnectionSite)getTypeLoader().parse(reader, CTConnectionSite.type, xmlOptions);
        }
        
        public static CTConnectionSite parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTConnectionSite)getTypeLoader().parse(xmlStreamReader, CTConnectionSite.type, (XmlOptions)null);
        }
        
        public static CTConnectionSite parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTConnectionSite)getTypeLoader().parse(xmlStreamReader, CTConnectionSite.type, xmlOptions);
        }
        
        public static CTConnectionSite parse(final Node node) throws XmlException {
            return (CTConnectionSite)getTypeLoader().parse(node, CTConnectionSite.type, (XmlOptions)null);
        }
        
        public static CTConnectionSite parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTConnectionSite)getTypeLoader().parse(node, CTConnectionSite.type, xmlOptions);
        }
        
        @Deprecated
        public static CTConnectionSite parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTConnectionSite)getTypeLoader().parse(xmlInputStream, CTConnectionSite.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTConnectionSite parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTConnectionSite)getTypeLoader().parse(xmlInputStream, CTConnectionSite.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTConnectionSite.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTConnectionSite.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
