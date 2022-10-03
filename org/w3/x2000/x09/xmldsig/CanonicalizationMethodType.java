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
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CanonicalizationMethodType extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CanonicalizationMethodType.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.s8C3F193EE11A2F798ACF65489B9E6078").resolveHandle("canonicalizationmethodtypeec74type");
    
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
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CanonicalizationMethodType.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CanonicalizationMethodType newInstance() {
            return (CanonicalizationMethodType)getTypeLoader().newInstance(CanonicalizationMethodType.type, (XmlOptions)null);
        }
        
        public static CanonicalizationMethodType newInstance(final XmlOptions xmlOptions) {
            return (CanonicalizationMethodType)getTypeLoader().newInstance(CanonicalizationMethodType.type, xmlOptions);
        }
        
        public static CanonicalizationMethodType parse(final String s) throws XmlException {
            return (CanonicalizationMethodType)getTypeLoader().parse(s, CanonicalizationMethodType.type, (XmlOptions)null);
        }
        
        public static CanonicalizationMethodType parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CanonicalizationMethodType)getTypeLoader().parse(s, CanonicalizationMethodType.type, xmlOptions);
        }
        
        public static CanonicalizationMethodType parse(final File file) throws XmlException, IOException {
            return (CanonicalizationMethodType)getTypeLoader().parse(file, CanonicalizationMethodType.type, (XmlOptions)null);
        }
        
        public static CanonicalizationMethodType parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CanonicalizationMethodType)getTypeLoader().parse(file, CanonicalizationMethodType.type, xmlOptions);
        }
        
        public static CanonicalizationMethodType parse(final URL url) throws XmlException, IOException {
            return (CanonicalizationMethodType)getTypeLoader().parse(url, CanonicalizationMethodType.type, (XmlOptions)null);
        }
        
        public static CanonicalizationMethodType parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CanonicalizationMethodType)getTypeLoader().parse(url, CanonicalizationMethodType.type, xmlOptions);
        }
        
        public static CanonicalizationMethodType parse(final InputStream inputStream) throws XmlException, IOException {
            return (CanonicalizationMethodType)getTypeLoader().parse(inputStream, CanonicalizationMethodType.type, (XmlOptions)null);
        }
        
        public static CanonicalizationMethodType parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CanonicalizationMethodType)getTypeLoader().parse(inputStream, CanonicalizationMethodType.type, xmlOptions);
        }
        
        public static CanonicalizationMethodType parse(final Reader reader) throws XmlException, IOException {
            return (CanonicalizationMethodType)getTypeLoader().parse(reader, CanonicalizationMethodType.type, (XmlOptions)null);
        }
        
        public static CanonicalizationMethodType parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CanonicalizationMethodType)getTypeLoader().parse(reader, CanonicalizationMethodType.type, xmlOptions);
        }
        
        public static CanonicalizationMethodType parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CanonicalizationMethodType)getTypeLoader().parse(xmlStreamReader, CanonicalizationMethodType.type, (XmlOptions)null);
        }
        
        public static CanonicalizationMethodType parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CanonicalizationMethodType)getTypeLoader().parse(xmlStreamReader, CanonicalizationMethodType.type, xmlOptions);
        }
        
        public static CanonicalizationMethodType parse(final Node node) throws XmlException {
            return (CanonicalizationMethodType)getTypeLoader().parse(node, CanonicalizationMethodType.type, (XmlOptions)null);
        }
        
        public static CanonicalizationMethodType parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CanonicalizationMethodType)getTypeLoader().parse(node, CanonicalizationMethodType.type, xmlOptions);
        }
        
        @Deprecated
        public static CanonicalizationMethodType parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CanonicalizationMethodType)getTypeLoader().parse(xmlInputStream, CanonicalizationMethodType.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CanonicalizationMethodType parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CanonicalizationMethodType)getTypeLoader().parse(xmlInputStream, CanonicalizationMethodType.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CanonicalizationMethodType.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CanonicalizationMethodType.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
