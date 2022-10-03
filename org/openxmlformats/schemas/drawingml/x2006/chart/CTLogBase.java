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

public interface CTLogBase extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTLogBase.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctlogbase9191type");
    
    double getVal();
    
    STLogBase xgetVal();
    
    void setVal(final double p0);
    
    void xsetVal(final STLogBase p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTLogBase.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTLogBase newInstance() {
            return (CTLogBase)getTypeLoader().newInstance(CTLogBase.type, (XmlOptions)null);
        }
        
        public static CTLogBase newInstance(final XmlOptions xmlOptions) {
            return (CTLogBase)getTypeLoader().newInstance(CTLogBase.type, xmlOptions);
        }
        
        public static CTLogBase parse(final String s) throws XmlException {
            return (CTLogBase)getTypeLoader().parse(s, CTLogBase.type, (XmlOptions)null);
        }
        
        public static CTLogBase parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTLogBase)getTypeLoader().parse(s, CTLogBase.type, xmlOptions);
        }
        
        public static CTLogBase parse(final File file) throws XmlException, IOException {
            return (CTLogBase)getTypeLoader().parse(file, CTLogBase.type, (XmlOptions)null);
        }
        
        public static CTLogBase parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTLogBase)getTypeLoader().parse(file, CTLogBase.type, xmlOptions);
        }
        
        public static CTLogBase parse(final URL url) throws XmlException, IOException {
            return (CTLogBase)getTypeLoader().parse(url, CTLogBase.type, (XmlOptions)null);
        }
        
        public static CTLogBase parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTLogBase)getTypeLoader().parse(url, CTLogBase.type, xmlOptions);
        }
        
        public static CTLogBase parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTLogBase)getTypeLoader().parse(inputStream, CTLogBase.type, (XmlOptions)null);
        }
        
        public static CTLogBase parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTLogBase)getTypeLoader().parse(inputStream, CTLogBase.type, xmlOptions);
        }
        
        public static CTLogBase parse(final Reader reader) throws XmlException, IOException {
            return (CTLogBase)getTypeLoader().parse(reader, CTLogBase.type, (XmlOptions)null);
        }
        
        public static CTLogBase parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTLogBase)getTypeLoader().parse(reader, CTLogBase.type, xmlOptions);
        }
        
        public static CTLogBase parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTLogBase)getTypeLoader().parse(xmlStreamReader, CTLogBase.type, (XmlOptions)null);
        }
        
        public static CTLogBase parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTLogBase)getTypeLoader().parse(xmlStreamReader, CTLogBase.type, xmlOptions);
        }
        
        public static CTLogBase parse(final Node node) throws XmlException {
            return (CTLogBase)getTypeLoader().parse(node, CTLogBase.type, (XmlOptions)null);
        }
        
        public static CTLogBase parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTLogBase)getTypeLoader().parse(node, CTLogBase.type, xmlOptions);
        }
        
        @Deprecated
        public static CTLogBase parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTLogBase)getTypeLoader().parse(xmlInputStream, CTLogBase.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTLogBase parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTLogBase)getTypeLoader().parse(xmlInputStream, CTLogBase.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTLogBase.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTLogBase.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
