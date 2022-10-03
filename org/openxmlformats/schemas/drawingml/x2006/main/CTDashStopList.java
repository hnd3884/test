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

public interface CTDashStopList extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTDashStopList.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctdashstoplist920dtype");
    
    List<CTDashStop> getDsList();
    
    @Deprecated
    CTDashStop[] getDsArray();
    
    CTDashStop getDsArray(final int p0);
    
    int sizeOfDsArray();
    
    void setDsArray(final CTDashStop[] p0);
    
    void setDsArray(final int p0, final CTDashStop p1);
    
    CTDashStop insertNewDs(final int p0);
    
    CTDashStop addNewDs();
    
    void removeDs(final int p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTDashStopList.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTDashStopList newInstance() {
            return (CTDashStopList)getTypeLoader().newInstance(CTDashStopList.type, (XmlOptions)null);
        }
        
        public static CTDashStopList newInstance(final XmlOptions xmlOptions) {
            return (CTDashStopList)getTypeLoader().newInstance(CTDashStopList.type, xmlOptions);
        }
        
        public static CTDashStopList parse(final String s) throws XmlException {
            return (CTDashStopList)getTypeLoader().parse(s, CTDashStopList.type, (XmlOptions)null);
        }
        
        public static CTDashStopList parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTDashStopList)getTypeLoader().parse(s, CTDashStopList.type, xmlOptions);
        }
        
        public static CTDashStopList parse(final File file) throws XmlException, IOException {
            return (CTDashStopList)getTypeLoader().parse(file, CTDashStopList.type, (XmlOptions)null);
        }
        
        public static CTDashStopList parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTDashStopList)getTypeLoader().parse(file, CTDashStopList.type, xmlOptions);
        }
        
        public static CTDashStopList parse(final URL url) throws XmlException, IOException {
            return (CTDashStopList)getTypeLoader().parse(url, CTDashStopList.type, (XmlOptions)null);
        }
        
        public static CTDashStopList parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTDashStopList)getTypeLoader().parse(url, CTDashStopList.type, xmlOptions);
        }
        
        public static CTDashStopList parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTDashStopList)getTypeLoader().parse(inputStream, CTDashStopList.type, (XmlOptions)null);
        }
        
        public static CTDashStopList parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTDashStopList)getTypeLoader().parse(inputStream, CTDashStopList.type, xmlOptions);
        }
        
        public static CTDashStopList parse(final Reader reader) throws XmlException, IOException {
            return (CTDashStopList)getTypeLoader().parse(reader, CTDashStopList.type, (XmlOptions)null);
        }
        
        public static CTDashStopList parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTDashStopList)getTypeLoader().parse(reader, CTDashStopList.type, xmlOptions);
        }
        
        public static CTDashStopList parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTDashStopList)getTypeLoader().parse(xmlStreamReader, CTDashStopList.type, (XmlOptions)null);
        }
        
        public static CTDashStopList parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTDashStopList)getTypeLoader().parse(xmlStreamReader, CTDashStopList.type, xmlOptions);
        }
        
        public static CTDashStopList parse(final Node node) throws XmlException {
            return (CTDashStopList)getTypeLoader().parse(node, CTDashStopList.type, (XmlOptions)null);
        }
        
        public static CTDashStopList parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTDashStopList)getTypeLoader().parse(node, CTDashStopList.type, xmlOptions);
        }
        
        @Deprecated
        public static CTDashStopList parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTDashStopList)getTypeLoader().parse(xmlInputStream, CTDashStopList.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTDashStopList parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTDashStopList)getTypeLoader().parse(xmlInputStream, CTDashStopList.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTDashStopList.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTDashStopList.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
