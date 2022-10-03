package org.etsi.uri.x01903.v13;

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

public interface XAdESTimeStampType extends GenericTimeStampType
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(XAdESTimeStampType.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.s8C3F193EE11A2F798ACF65489B9E6078").resolveHandle("xadestimestamptypeaedbtype");
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(XAdESTimeStampType.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static XAdESTimeStampType newInstance() {
            return (XAdESTimeStampType)getTypeLoader().newInstance(XAdESTimeStampType.type, (XmlOptions)null);
        }
        
        public static XAdESTimeStampType newInstance(final XmlOptions xmlOptions) {
            return (XAdESTimeStampType)getTypeLoader().newInstance(XAdESTimeStampType.type, xmlOptions);
        }
        
        public static XAdESTimeStampType parse(final String s) throws XmlException {
            return (XAdESTimeStampType)getTypeLoader().parse(s, XAdESTimeStampType.type, (XmlOptions)null);
        }
        
        public static XAdESTimeStampType parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (XAdESTimeStampType)getTypeLoader().parse(s, XAdESTimeStampType.type, xmlOptions);
        }
        
        public static XAdESTimeStampType parse(final File file) throws XmlException, IOException {
            return (XAdESTimeStampType)getTypeLoader().parse(file, XAdESTimeStampType.type, (XmlOptions)null);
        }
        
        public static XAdESTimeStampType parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (XAdESTimeStampType)getTypeLoader().parse(file, XAdESTimeStampType.type, xmlOptions);
        }
        
        public static XAdESTimeStampType parse(final URL url) throws XmlException, IOException {
            return (XAdESTimeStampType)getTypeLoader().parse(url, XAdESTimeStampType.type, (XmlOptions)null);
        }
        
        public static XAdESTimeStampType parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (XAdESTimeStampType)getTypeLoader().parse(url, XAdESTimeStampType.type, xmlOptions);
        }
        
        public static XAdESTimeStampType parse(final InputStream inputStream) throws XmlException, IOException {
            return (XAdESTimeStampType)getTypeLoader().parse(inputStream, XAdESTimeStampType.type, (XmlOptions)null);
        }
        
        public static XAdESTimeStampType parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (XAdESTimeStampType)getTypeLoader().parse(inputStream, XAdESTimeStampType.type, xmlOptions);
        }
        
        public static XAdESTimeStampType parse(final Reader reader) throws XmlException, IOException {
            return (XAdESTimeStampType)getTypeLoader().parse(reader, XAdESTimeStampType.type, (XmlOptions)null);
        }
        
        public static XAdESTimeStampType parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (XAdESTimeStampType)getTypeLoader().parse(reader, XAdESTimeStampType.type, xmlOptions);
        }
        
        public static XAdESTimeStampType parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (XAdESTimeStampType)getTypeLoader().parse(xmlStreamReader, XAdESTimeStampType.type, (XmlOptions)null);
        }
        
        public static XAdESTimeStampType parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (XAdESTimeStampType)getTypeLoader().parse(xmlStreamReader, XAdESTimeStampType.type, xmlOptions);
        }
        
        public static XAdESTimeStampType parse(final Node node) throws XmlException {
            return (XAdESTimeStampType)getTypeLoader().parse(node, XAdESTimeStampType.type, (XmlOptions)null);
        }
        
        public static XAdESTimeStampType parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (XAdESTimeStampType)getTypeLoader().parse(node, XAdESTimeStampType.type, xmlOptions);
        }
        
        @Deprecated
        public static XAdESTimeStampType parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (XAdESTimeStampType)getTypeLoader().parse(xmlInputStream, XAdESTimeStampType.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XAdESTimeStampType parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (XAdESTimeStampType)getTypeLoader().parse(xmlInputStream, XAdESTimeStampType.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, XAdESTimeStampType.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, XAdESTimeStampType.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
