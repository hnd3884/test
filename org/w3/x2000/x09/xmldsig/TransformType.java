package org.w3.x2000.x09.xmldsig;

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
import org.apache.xmlbeans.XmlAnyURI;
import org.apache.xmlbeans.XmlString;
import java.util.List;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface TransformType extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(TransformType.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.s8C3F193EE11A2F798ACF65489B9E6078").resolveHandle("transformtype550btype");
    
    List<String> getXPathList();
    
    @Deprecated
    String[] getXPathArray();
    
    String getXPathArray(final int p0);
    
    List<XmlString> xgetXPathList();
    
    @Deprecated
    XmlString[] xgetXPathArray();
    
    XmlString xgetXPathArray(final int p0);
    
    int sizeOfXPathArray();
    
    void setXPathArray(final String[] p0);
    
    void setXPathArray(final int p0, final String p1);
    
    void xsetXPathArray(final XmlString[] p0);
    
    void xsetXPathArray(final int p0, final XmlString p1);
    
    void insertXPath(final int p0, final String p1);
    
    void addXPath(final String p0);
    
    XmlString insertNewXPath(final int p0);
    
    XmlString addNewXPath();
    
    void removeXPath(final int p0);
    
    String getAlgorithm();
    
    XmlAnyURI xgetAlgorithm();
    
    void setAlgorithm(final String p0);
    
    void xsetAlgorithm(final XmlAnyURI p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(TransformType.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static TransformType newInstance() {
            return (TransformType)getTypeLoader().newInstance(TransformType.type, (XmlOptions)null);
        }
        
        public static TransformType newInstance(final XmlOptions xmlOptions) {
            return (TransformType)getTypeLoader().newInstance(TransformType.type, xmlOptions);
        }
        
        public static TransformType parse(final String s) throws XmlException {
            return (TransformType)getTypeLoader().parse(s, TransformType.type, (XmlOptions)null);
        }
        
        public static TransformType parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (TransformType)getTypeLoader().parse(s, TransformType.type, xmlOptions);
        }
        
        public static TransformType parse(final File file) throws XmlException, IOException {
            return (TransformType)getTypeLoader().parse(file, TransformType.type, (XmlOptions)null);
        }
        
        public static TransformType parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (TransformType)getTypeLoader().parse(file, TransformType.type, xmlOptions);
        }
        
        public static TransformType parse(final URL url) throws XmlException, IOException {
            return (TransformType)getTypeLoader().parse(url, TransformType.type, (XmlOptions)null);
        }
        
        public static TransformType parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (TransformType)getTypeLoader().parse(url, TransformType.type, xmlOptions);
        }
        
        public static TransformType parse(final InputStream inputStream) throws XmlException, IOException {
            return (TransformType)getTypeLoader().parse(inputStream, TransformType.type, (XmlOptions)null);
        }
        
        public static TransformType parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (TransformType)getTypeLoader().parse(inputStream, TransformType.type, xmlOptions);
        }
        
        public static TransformType parse(final Reader reader) throws XmlException, IOException {
            return (TransformType)getTypeLoader().parse(reader, TransformType.type, (XmlOptions)null);
        }
        
        public static TransformType parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (TransformType)getTypeLoader().parse(reader, TransformType.type, xmlOptions);
        }
        
        public static TransformType parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (TransformType)getTypeLoader().parse(xmlStreamReader, TransformType.type, (XmlOptions)null);
        }
        
        public static TransformType parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (TransformType)getTypeLoader().parse(xmlStreamReader, TransformType.type, xmlOptions);
        }
        
        public static TransformType parse(final Node node) throws XmlException {
            return (TransformType)getTypeLoader().parse(node, TransformType.type, (XmlOptions)null);
        }
        
        public static TransformType parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (TransformType)getTypeLoader().parse(node, TransformType.type, xmlOptions);
        }
        
        @Deprecated
        public static TransformType parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (TransformType)getTypeLoader().parse(xmlInputStream, TransformType.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static TransformType parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (TransformType)getTypeLoader().parse(xmlInputStream, TransformType.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, TransformType.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, TransformType.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
