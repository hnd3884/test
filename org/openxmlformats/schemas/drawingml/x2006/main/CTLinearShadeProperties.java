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
import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTLinearShadeProperties extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTLinearShadeProperties.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctlinearshadeproperties7f0ctype");
    
    int getAng();
    
    STPositiveFixedAngle xgetAng();
    
    boolean isSetAng();
    
    void setAng(final int p0);
    
    void xsetAng(final STPositiveFixedAngle p0);
    
    void unsetAng();
    
    boolean getScaled();
    
    XmlBoolean xgetScaled();
    
    boolean isSetScaled();
    
    void setScaled(final boolean p0);
    
    void xsetScaled(final XmlBoolean p0);
    
    void unsetScaled();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTLinearShadeProperties.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTLinearShadeProperties newInstance() {
            return (CTLinearShadeProperties)getTypeLoader().newInstance(CTLinearShadeProperties.type, (XmlOptions)null);
        }
        
        public static CTLinearShadeProperties newInstance(final XmlOptions xmlOptions) {
            return (CTLinearShadeProperties)getTypeLoader().newInstance(CTLinearShadeProperties.type, xmlOptions);
        }
        
        public static CTLinearShadeProperties parse(final String s) throws XmlException {
            return (CTLinearShadeProperties)getTypeLoader().parse(s, CTLinearShadeProperties.type, (XmlOptions)null);
        }
        
        public static CTLinearShadeProperties parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTLinearShadeProperties)getTypeLoader().parse(s, CTLinearShadeProperties.type, xmlOptions);
        }
        
        public static CTLinearShadeProperties parse(final File file) throws XmlException, IOException {
            return (CTLinearShadeProperties)getTypeLoader().parse(file, CTLinearShadeProperties.type, (XmlOptions)null);
        }
        
        public static CTLinearShadeProperties parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTLinearShadeProperties)getTypeLoader().parse(file, CTLinearShadeProperties.type, xmlOptions);
        }
        
        public static CTLinearShadeProperties parse(final URL url) throws XmlException, IOException {
            return (CTLinearShadeProperties)getTypeLoader().parse(url, CTLinearShadeProperties.type, (XmlOptions)null);
        }
        
        public static CTLinearShadeProperties parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTLinearShadeProperties)getTypeLoader().parse(url, CTLinearShadeProperties.type, xmlOptions);
        }
        
        public static CTLinearShadeProperties parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTLinearShadeProperties)getTypeLoader().parse(inputStream, CTLinearShadeProperties.type, (XmlOptions)null);
        }
        
        public static CTLinearShadeProperties parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTLinearShadeProperties)getTypeLoader().parse(inputStream, CTLinearShadeProperties.type, xmlOptions);
        }
        
        public static CTLinearShadeProperties parse(final Reader reader) throws XmlException, IOException {
            return (CTLinearShadeProperties)getTypeLoader().parse(reader, CTLinearShadeProperties.type, (XmlOptions)null);
        }
        
        public static CTLinearShadeProperties parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTLinearShadeProperties)getTypeLoader().parse(reader, CTLinearShadeProperties.type, xmlOptions);
        }
        
        public static CTLinearShadeProperties parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTLinearShadeProperties)getTypeLoader().parse(xmlStreamReader, CTLinearShadeProperties.type, (XmlOptions)null);
        }
        
        public static CTLinearShadeProperties parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTLinearShadeProperties)getTypeLoader().parse(xmlStreamReader, CTLinearShadeProperties.type, xmlOptions);
        }
        
        public static CTLinearShadeProperties parse(final Node node) throws XmlException {
            return (CTLinearShadeProperties)getTypeLoader().parse(node, CTLinearShadeProperties.type, (XmlOptions)null);
        }
        
        public static CTLinearShadeProperties parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTLinearShadeProperties)getTypeLoader().parse(node, CTLinearShadeProperties.type, xmlOptions);
        }
        
        @Deprecated
        public static CTLinearShadeProperties parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTLinearShadeProperties)getTypeLoader().parse(xmlInputStream, CTLinearShadeProperties.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTLinearShadeProperties parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTLinearShadeProperties)getTypeLoader().parse(xmlInputStream, CTLinearShadeProperties.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTLinearShadeProperties.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTLinearShadeProperties.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
