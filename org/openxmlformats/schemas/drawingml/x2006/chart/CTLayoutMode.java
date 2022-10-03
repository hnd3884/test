package org.openxmlformats.schemas.drawingml.x2006.chart;

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

public interface CTLayoutMode extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTLayoutMode.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctlayoutmode53eftype");
    
    STLayoutMode.Enum getVal();
    
    STLayoutMode xgetVal();
    
    boolean isSetVal();
    
    void setVal(final STLayoutMode.Enum p0);
    
    void xsetVal(final STLayoutMode p0);
    
    void unsetVal();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTLayoutMode.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTLayoutMode newInstance() {
            return (CTLayoutMode)getTypeLoader().newInstance(CTLayoutMode.type, (XmlOptions)null);
        }
        
        public static CTLayoutMode newInstance(final XmlOptions xmlOptions) {
            return (CTLayoutMode)getTypeLoader().newInstance(CTLayoutMode.type, xmlOptions);
        }
        
        public static CTLayoutMode parse(final String s) throws XmlException {
            return (CTLayoutMode)getTypeLoader().parse(s, CTLayoutMode.type, (XmlOptions)null);
        }
        
        public static CTLayoutMode parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTLayoutMode)getTypeLoader().parse(s, CTLayoutMode.type, xmlOptions);
        }
        
        public static CTLayoutMode parse(final File file) throws XmlException, IOException {
            return (CTLayoutMode)getTypeLoader().parse(file, CTLayoutMode.type, (XmlOptions)null);
        }
        
        public static CTLayoutMode parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTLayoutMode)getTypeLoader().parse(file, CTLayoutMode.type, xmlOptions);
        }
        
        public static CTLayoutMode parse(final URL url) throws XmlException, IOException {
            return (CTLayoutMode)getTypeLoader().parse(url, CTLayoutMode.type, (XmlOptions)null);
        }
        
        public static CTLayoutMode parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTLayoutMode)getTypeLoader().parse(url, CTLayoutMode.type, xmlOptions);
        }
        
        public static CTLayoutMode parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTLayoutMode)getTypeLoader().parse(inputStream, CTLayoutMode.type, (XmlOptions)null);
        }
        
        public static CTLayoutMode parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTLayoutMode)getTypeLoader().parse(inputStream, CTLayoutMode.type, xmlOptions);
        }
        
        public static CTLayoutMode parse(final Reader reader) throws XmlException, IOException {
            return (CTLayoutMode)getTypeLoader().parse(reader, CTLayoutMode.type, (XmlOptions)null);
        }
        
        public static CTLayoutMode parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTLayoutMode)getTypeLoader().parse(reader, CTLayoutMode.type, xmlOptions);
        }
        
        public static CTLayoutMode parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTLayoutMode)getTypeLoader().parse(xmlStreamReader, CTLayoutMode.type, (XmlOptions)null);
        }
        
        public static CTLayoutMode parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTLayoutMode)getTypeLoader().parse(xmlStreamReader, CTLayoutMode.type, xmlOptions);
        }
        
        public static CTLayoutMode parse(final Node node) throws XmlException {
            return (CTLayoutMode)getTypeLoader().parse(node, CTLayoutMode.type, (XmlOptions)null);
        }
        
        public static CTLayoutMode parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTLayoutMode)getTypeLoader().parse(node, CTLayoutMode.type, xmlOptions);
        }
        
        @Deprecated
        public static CTLayoutMode parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTLayoutMode)getTypeLoader().parse(xmlInputStream, CTLayoutMode.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTLayoutMode parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTLayoutMode)getTypeLoader().parse(xmlInputStream, CTLayoutMode.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTLayoutMode.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTLayoutMode.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
