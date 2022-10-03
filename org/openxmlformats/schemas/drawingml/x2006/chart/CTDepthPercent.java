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

public interface CTDepthPercent extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTDepthPercent.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctdepthpercent117atype");
    
    int getVal();
    
    STDepthPercent xgetVal();
    
    boolean isSetVal();
    
    void setVal(final int p0);
    
    void xsetVal(final STDepthPercent p0);
    
    void unsetVal();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTDepthPercent.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTDepthPercent newInstance() {
            return (CTDepthPercent)getTypeLoader().newInstance(CTDepthPercent.type, (XmlOptions)null);
        }
        
        public static CTDepthPercent newInstance(final XmlOptions xmlOptions) {
            return (CTDepthPercent)getTypeLoader().newInstance(CTDepthPercent.type, xmlOptions);
        }
        
        public static CTDepthPercent parse(final String s) throws XmlException {
            return (CTDepthPercent)getTypeLoader().parse(s, CTDepthPercent.type, (XmlOptions)null);
        }
        
        public static CTDepthPercent parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTDepthPercent)getTypeLoader().parse(s, CTDepthPercent.type, xmlOptions);
        }
        
        public static CTDepthPercent parse(final File file) throws XmlException, IOException {
            return (CTDepthPercent)getTypeLoader().parse(file, CTDepthPercent.type, (XmlOptions)null);
        }
        
        public static CTDepthPercent parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTDepthPercent)getTypeLoader().parse(file, CTDepthPercent.type, xmlOptions);
        }
        
        public static CTDepthPercent parse(final URL url) throws XmlException, IOException {
            return (CTDepthPercent)getTypeLoader().parse(url, CTDepthPercent.type, (XmlOptions)null);
        }
        
        public static CTDepthPercent parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTDepthPercent)getTypeLoader().parse(url, CTDepthPercent.type, xmlOptions);
        }
        
        public static CTDepthPercent parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTDepthPercent)getTypeLoader().parse(inputStream, CTDepthPercent.type, (XmlOptions)null);
        }
        
        public static CTDepthPercent parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTDepthPercent)getTypeLoader().parse(inputStream, CTDepthPercent.type, xmlOptions);
        }
        
        public static CTDepthPercent parse(final Reader reader) throws XmlException, IOException {
            return (CTDepthPercent)getTypeLoader().parse(reader, CTDepthPercent.type, (XmlOptions)null);
        }
        
        public static CTDepthPercent parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTDepthPercent)getTypeLoader().parse(reader, CTDepthPercent.type, xmlOptions);
        }
        
        public static CTDepthPercent parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTDepthPercent)getTypeLoader().parse(xmlStreamReader, CTDepthPercent.type, (XmlOptions)null);
        }
        
        public static CTDepthPercent parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTDepthPercent)getTypeLoader().parse(xmlStreamReader, CTDepthPercent.type, xmlOptions);
        }
        
        public static CTDepthPercent parse(final Node node) throws XmlException {
            return (CTDepthPercent)getTypeLoader().parse(node, CTDepthPercent.type, (XmlOptions)null);
        }
        
        public static CTDepthPercent parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTDepthPercent)getTypeLoader().parse(node, CTDepthPercent.type, xmlOptions);
        }
        
        @Deprecated
        public static CTDepthPercent parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTDepthPercent)getTypeLoader().parse(xmlInputStream, CTDepthPercent.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTDepthPercent parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTDepthPercent)getTypeLoader().parse(xmlInputStream, CTDepthPercent.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTDepthPercent.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTDepthPercent.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
