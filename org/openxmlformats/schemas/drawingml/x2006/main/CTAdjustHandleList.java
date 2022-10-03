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
import java.util.List;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTAdjustHandleList extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTAdjustHandleList.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctadjusthandlelistfdb0type");
    
    List<CTXYAdjustHandle> getAhXYList();
    
    @Deprecated
    CTXYAdjustHandle[] getAhXYArray();
    
    CTXYAdjustHandle getAhXYArray(final int p0);
    
    int sizeOfAhXYArray();
    
    void setAhXYArray(final CTXYAdjustHandle[] p0);
    
    void setAhXYArray(final int p0, final CTXYAdjustHandle p1);
    
    CTXYAdjustHandle insertNewAhXY(final int p0);
    
    CTXYAdjustHandle addNewAhXY();
    
    void removeAhXY(final int p0);
    
    List<CTPolarAdjustHandle> getAhPolarList();
    
    @Deprecated
    CTPolarAdjustHandle[] getAhPolarArray();
    
    CTPolarAdjustHandle getAhPolarArray(final int p0);
    
    int sizeOfAhPolarArray();
    
    void setAhPolarArray(final CTPolarAdjustHandle[] p0);
    
    void setAhPolarArray(final int p0, final CTPolarAdjustHandle p1);
    
    CTPolarAdjustHandle insertNewAhPolar(final int p0);
    
    CTPolarAdjustHandle addNewAhPolar();
    
    void removeAhPolar(final int p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTAdjustHandleList.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTAdjustHandleList newInstance() {
            return (CTAdjustHandleList)getTypeLoader().newInstance(CTAdjustHandleList.type, (XmlOptions)null);
        }
        
        public static CTAdjustHandleList newInstance(final XmlOptions xmlOptions) {
            return (CTAdjustHandleList)getTypeLoader().newInstance(CTAdjustHandleList.type, xmlOptions);
        }
        
        public static CTAdjustHandleList parse(final String s) throws XmlException {
            return (CTAdjustHandleList)getTypeLoader().parse(s, CTAdjustHandleList.type, (XmlOptions)null);
        }
        
        public static CTAdjustHandleList parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTAdjustHandleList)getTypeLoader().parse(s, CTAdjustHandleList.type, xmlOptions);
        }
        
        public static CTAdjustHandleList parse(final File file) throws XmlException, IOException {
            return (CTAdjustHandleList)getTypeLoader().parse(file, CTAdjustHandleList.type, (XmlOptions)null);
        }
        
        public static CTAdjustHandleList parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTAdjustHandleList)getTypeLoader().parse(file, CTAdjustHandleList.type, xmlOptions);
        }
        
        public static CTAdjustHandleList parse(final URL url) throws XmlException, IOException {
            return (CTAdjustHandleList)getTypeLoader().parse(url, CTAdjustHandleList.type, (XmlOptions)null);
        }
        
        public static CTAdjustHandleList parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTAdjustHandleList)getTypeLoader().parse(url, CTAdjustHandleList.type, xmlOptions);
        }
        
        public static CTAdjustHandleList parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTAdjustHandleList)getTypeLoader().parse(inputStream, CTAdjustHandleList.type, (XmlOptions)null);
        }
        
        public static CTAdjustHandleList parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTAdjustHandleList)getTypeLoader().parse(inputStream, CTAdjustHandleList.type, xmlOptions);
        }
        
        public static CTAdjustHandleList parse(final Reader reader) throws XmlException, IOException {
            return (CTAdjustHandleList)getTypeLoader().parse(reader, CTAdjustHandleList.type, (XmlOptions)null);
        }
        
        public static CTAdjustHandleList parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTAdjustHandleList)getTypeLoader().parse(reader, CTAdjustHandleList.type, xmlOptions);
        }
        
        public static CTAdjustHandleList parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTAdjustHandleList)getTypeLoader().parse(xmlStreamReader, CTAdjustHandleList.type, (XmlOptions)null);
        }
        
        public static CTAdjustHandleList parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTAdjustHandleList)getTypeLoader().parse(xmlStreamReader, CTAdjustHandleList.type, xmlOptions);
        }
        
        public static CTAdjustHandleList parse(final Node node) throws XmlException {
            return (CTAdjustHandleList)getTypeLoader().parse(node, CTAdjustHandleList.type, (XmlOptions)null);
        }
        
        public static CTAdjustHandleList parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTAdjustHandleList)getTypeLoader().parse(node, CTAdjustHandleList.type, xmlOptions);
        }
        
        @Deprecated
        public static CTAdjustHandleList parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTAdjustHandleList)getTypeLoader().parse(xmlInputStream, CTAdjustHandleList.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTAdjustHandleList parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTAdjustHandleList)getTypeLoader().parse(xmlInputStream, CTAdjustHandleList.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTAdjustHandleList.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTAdjustHandleList.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
