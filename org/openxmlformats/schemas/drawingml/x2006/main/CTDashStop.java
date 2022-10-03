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

public interface CTDashStop extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTDashStop.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctdashstopdc4ftype");
    
    int getD();
    
    STPositivePercentage xgetD();
    
    void setD(final int p0);
    
    void xsetD(final STPositivePercentage p0);
    
    int getSp();
    
    STPositivePercentage xgetSp();
    
    void setSp(final int p0);
    
    void xsetSp(final STPositivePercentage p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTDashStop.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTDashStop newInstance() {
            return (CTDashStop)getTypeLoader().newInstance(CTDashStop.type, (XmlOptions)null);
        }
        
        public static CTDashStop newInstance(final XmlOptions xmlOptions) {
            return (CTDashStop)getTypeLoader().newInstance(CTDashStop.type, xmlOptions);
        }
        
        public static CTDashStop parse(final String s) throws XmlException {
            return (CTDashStop)getTypeLoader().parse(s, CTDashStop.type, (XmlOptions)null);
        }
        
        public static CTDashStop parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTDashStop)getTypeLoader().parse(s, CTDashStop.type, xmlOptions);
        }
        
        public static CTDashStop parse(final File file) throws XmlException, IOException {
            return (CTDashStop)getTypeLoader().parse(file, CTDashStop.type, (XmlOptions)null);
        }
        
        public static CTDashStop parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTDashStop)getTypeLoader().parse(file, CTDashStop.type, xmlOptions);
        }
        
        public static CTDashStop parse(final URL url) throws XmlException, IOException {
            return (CTDashStop)getTypeLoader().parse(url, CTDashStop.type, (XmlOptions)null);
        }
        
        public static CTDashStop parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTDashStop)getTypeLoader().parse(url, CTDashStop.type, xmlOptions);
        }
        
        public static CTDashStop parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTDashStop)getTypeLoader().parse(inputStream, CTDashStop.type, (XmlOptions)null);
        }
        
        public static CTDashStop parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTDashStop)getTypeLoader().parse(inputStream, CTDashStop.type, xmlOptions);
        }
        
        public static CTDashStop parse(final Reader reader) throws XmlException, IOException {
            return (CTDashStop)getTypeLoader().parse(reader, CTDashStop.type, (XmlOptions)null);
        }
        
        public static CTDashStop parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTDashStop)getTypeLoader().parse(reader, CTDashStop.type, xmlOptions);
        }
        
        public static CTDashStop parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTDashStop)getTypeLoader().parse(xmlStreamReader, CTDashStop.type, (XmlOptions)null);
        }
        
        public static CTDashStop parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTDashStop)getTypeLoader().parse(xmlStreamReader, CTDashStop.type, xmlOptions);
        }
        
        public static CTDashStop parse(final Node node) throws XmlException {
            return (CTDashStop)getTypeLoader().parse(node, CTDashStop.type, (XmlOptions)null);
        }
        
        public static CTDashStop parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTDashStop)getTypeLoader().parse(node, CTDashStop.type, xmlOptions);
        }
        
        @Deprecated
        public static CTDashStop parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTDashStop)getTypeLoader().parse(xmlInputStream, CTDashStop.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTDashStop parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTDashStop)getTypeLoader().parse(xmlInputStream, CTDashStop.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTDashStop.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTDashStop.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
