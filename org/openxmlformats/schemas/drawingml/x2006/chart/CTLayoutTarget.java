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

public interface CTLayoutTarget extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTLayoutTarget.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctlayouttarget1001type");
    
    STLayoutTarget.Enum getVal();
    
    STLayoutTarget xgetVal();
    
    boolean isSetVal();
    
    void setVal(final STLayoutTarget.Enum p0);
    
    void xsetVal(final STLayoutTarget p0);
    
    void unsetVal();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTLayoutTarget.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTLayoutTarget newInstance() {
            return (CTLayoutTarget)getTypeLoader().newInstance(CTLayoutTarget.type, (XmlOptions)null);
        }
        
        public static CTLayoutTarget newInstance(final XmlOptions xmlOptions) {
            return (CTLayoutTarget)getTypeLoader().newInstance(CTLayoutTarget.type, xmlOptions);
        }
        
        public static CTLayoutTarget parse(final String s) throws XmlException {
            return (CTLayoutTarget)getTypeLoader().parse(s, CTLayoutTarget.type, (XmlOptions)null);
        }
        
        public static CTLayoutTarget parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTLayoutTarget)getTypeLoader().parse(s, CTLayoutTarget.type, xmlOptions);
        }
        
        public static CTLayoutTarget parse(final File file) throws XmlException, IOException {
            return (CTLayoutTarget)getTypeLoader().parse(file, CTLayoutTarget.type, (XmlOptions)null);
        }
        
        public static CTLayoutTarget parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTLayoutTarget)getTypeLoader().parse(file, CTLayoutTarget.type, xmlOptions);
        }
        
        public static CTLayoutTarget parse(final URL url) throws XmlException, IOException {
            return (CTLayoutTarget)getTypeLoader().parse(url, CTLayoutTarget.type, (XmlOptions)null);
        }
        
        public static CTLayoutTarget parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTLayoutTarget)getTypeLoader().parse(url, CTLayoutTarget.type, xmlOptions);
        }
        
        public static CTLayoutTarget parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTLayoutTarget)getTypeLoader().parse(inputStream, CTLayoutTarget.type, (XmlOptions)null);
        }
        
        public static CTLayoutTarget parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTLayoutTarget)getTypeLoader().parse(inputStream, CTLayoutTarget.type, xmlOptions);
        }
        
        public static CTLayoutTarget parse(final Reader reader) throws XmlException, IOException {
            return (CTLayoutTarget)getTypeLoader().parse(reader, CTLayoutTarget.type, (XmlOptions)null);
        }
        
        public static CTLayoutTarget parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTLayoutTarget)getTypeLoader().parse(reader, CTLayoutTarget.type, xmlOptions);
        }
        
        public static CTLayoutTarget parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTLayoutTarget)getTypeLoader().parse(xmlStreamReader, CTLayoutTarget.type, (XmlOptions)null);
        }
        
        public static CTLayoutTarget parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTLayoutTarget)getTypeLoader().parse(xmlStreamReader, CTLayoutTarget.type, xmlOptions);
        }
        
        public static CTLayoutTarget parse(final Node node) throws XmlException {
            return (CTLayoutTarget)getTypeLoader().parse(node, CTLayoutTarget.type, (XmlOptions)null);
        }
        
        public static CTLayoutTarget parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTLayoutTarget)getTypeLoader().parse(node, CTLayoutTarget.type, xmlOptions);
        }
        
        @Deprecated
        public static CTLayoutTarget parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTLayoutTarget)getTypeLoader().parse(xmlInputStream, CTLayoutTarget.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTLayoutTarget parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTLayoutTarget)getTypeLoader().parse(xmlInputStream, CTLayoutTarget.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTLayoutTarget.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTLayoutTarget.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
