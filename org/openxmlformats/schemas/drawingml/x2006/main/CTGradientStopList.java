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

public interface CTGradientStopList extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTGradientStopList.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctgradientstoplist7eabtype");
    
    List<CTGradientStop> getGsList();
    
    @Deprecated
    CTGradientStop[] getGsArray();
    
    CTGradientStop getGsArray(final int p0);
    
    int sizeOfGsArray();
    
    void setGsArray(final CTGradientStop[] p0);
    
    void setGsArray(final int p0, final CTGradientStop p1);
    
    CTGradientStop insertNewGs(final int p0);
    
    CTGradientStop addNewGs();
    
    void removeGs(final int p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTGradientStopList.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTGradientStopList newInstance() {
            return (CTGradientStopList)getTypeLoader().newInstance(CTGradientStopList.type, (XmlOptions)null);
        }
        
        public static CTGradientStopList newInstance(final XmlOptions xmlOptions) {
            return (CTGradientStopList)getTypeLoader().newInstance(CTGradientStopList.type, xmlOptions);
        }
        
        public static CTGradientStopList parse(final String s) throws XmlException {
            return (CTGradientStopList)getTypeLoader().parse(s, CTGradientStopList.type, (XmlOptions)null);
        }
        
        public static CTGradientStopList parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTGradientStopList)getTypeLoader().parse(s, CTGradientStopList.type, xmlOptions);
        }
        
        public static CTGradientStopList parse(final File file) throws XmlException, IOException {
            return (CTGradientStopList)getTypeLoader().parse(file, CTGradientStopList.type, (XmlOptions)null);
        }
        
        public static CTGradientStopList parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTGradientStopList)getTypeLoader().parse(file, CTGradientStopList.type, xmlOptions);
        }
        
        public static CTGradientStopList parse(final URL url) throws XmlException, IOException {
            return (CTGradientStopList)getTypeLoader().parse(url, CTGradientStopList.type, (XmlOptions)null);
        }
        
        public static CTGradientStopList parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTGradientStopList)getTypeLoader().parse(url, CTGradientStopList.type, xmlOptions);
        }
        
        public static CTGradientStopList parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTGradientStopList)getTypeLoader().parse(inputStream, CTGradientStopList.type, (XmlOptions)null);
        }
        
        public static CTGradientStopList parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTGradientStopList)getTypeLoader().parse(inputStream, CTGradientStopList.type, xmlOptions);
        }
        
        public static CTGradientStopList parse(final Reader reader) throws XmlException, IOException {
            return (CTGradientStopList)getTypeLoader().parse(reader, CTGradientStopList.type, (XmlOptions)null);
        }
        
        public static CTGradientStopList parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTGradientStopList)getTypeLoader().parse(reader, CTGradientStopList.type, xmlOptions);
        }
        
        public static CTGradientStopList parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTGradientStopList)getTypeLoader().parse(xmlStreamReader, CTGradientStopList.type, (XmlOptions)null);
        }
        
        public static CTGradientStopList parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTGradientStopList)getTypeLoader().parse(xmlStreamReader, CTGradientStopList.type, xmlOptions);
        }
        
        public static CTGradientStopList parse(final Node node) throws XmlException {
            return (CTGradientStopList)getTypeLoader().parse(node, CTGradientStopList.type, (XmlOptions)null);
        }
        
        public static CTGradientStopList parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTGradientStopList)getTypeLoader().parse(node, CTGradientStopList.type, xmlOptions);
        }
        
        @Deprecated
        public static CTGradientStopList parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTGradientStopList)getTypeLoader().parse(xmlInputStream, CTGradientStopList.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTGradientStopList parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTGradientStopList)getTypeLoader().parse(xmlInputStream, CTGradientStopList.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTGradientStopList.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTGradientStopList.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
