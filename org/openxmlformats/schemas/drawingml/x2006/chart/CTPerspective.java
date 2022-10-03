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

public interface CTPerspective extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTPerspective.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctperspectivefd2atype");
    
    short getVal();
    
    STPerspective xgetVal();
    
    boolean isSetVal();
    
    void setVal(final short p0);
    
    void xsetVal(final STPerspective p0);
    
    void unsetVal();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTPerspective.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTPerspective newInstance() {
            return (CTPerspective)getTypeLoader().newInstance(CTPerspective.type, (XmlOptions)null);
        }
        
        public static CTPerspective newInstance(final XmlOptions xmlOptions) {
            return (CTPerspective)getTypeLoader().newInstance(CTPerspective.type, xmlOptions);
        }
        
        public static CTPerspective parse(final String s) throws XmlException {
            return (CTPerspective)getTypeLoader().parse(s, CTPerspective.type, (XmlOptions)null);
        }
        
        public static CTPerspective parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTPerspective)getTypeLoader().parse(s, CTPerspective.type, xmlOptions);
        }
        
        public static CTPerspective parse(final File file) throws XmlException, IOException {
            return (CTPerspective)getTypeLoader().parse(file, CTPerspective.type, (XmlOptions)null);
        }
        
        public static CTPerspective parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTPerspective)getTypeLoader().parse(file, CTPerspective.type, xmlOptions);
        }
        
        public static CTPerspective parse(final URL url) throws XmlException, IOException {
            return (CTPerspective)getTypeLoader().parse(url, CTPerspective.type, (XmlOptions)null);
        }
        
        public static CTPerspective parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTPerspective)getTypeLoader().parse(url, CTPerspective.type, xmlOptions);
        }
        
        public static CTPerspective parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTPerspective)getTypeLoader().parse(inputStream, CTPerspective.type, (XmlOptions)null);
        }
        
        public static CTPerspective parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTPerspective)getTypeLoader().parse(inputStream, CTPerspective.type, xmlOptions);
        }
        
        public static CTPerspective parse(final Reader reader) throws XmlException, IOException {
            return (CTPerspective)getTypeLoader().parse(reader, CTPerspective.type, (XmlOptions)null);
        }
        
        public static CTPerspective parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTPerspective)getTypeLoader().parse(reader, CTPerspective.type, xmlOptions);
        }
        
        public static CTPerspective parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTPerspective)getTypeLoader().parse(xmlStreamReader, CTPerspective.type, (XmlOptions)null);
        }
        
        public static CTPerspective parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTPerspective)getTypeLoader().parse(xmlStreamReader, CTPerspective.type, xmlOptions);
        }
        
        public static CTPerspective parse(final Node node) throws XmlException {
            return (CTPerspective)getTypeLoader().parse(node, CTPerspective.type, (XmlOptions)null);
        }
        
        public static CTPerspective parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTPerspective)getTypeLoader().parse(node, CTPerspective.type, xmlOptions);
        }
        
        @Deprecated
        public static CTPerspective parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTPerspective)getTypeLoader().parse(xmlInputStream, CTPerspective.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTPerspective parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTPerspective)getTypeLoader().parse(xmlInputStream, CTPerspective.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTPerspective.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTPerspective.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
