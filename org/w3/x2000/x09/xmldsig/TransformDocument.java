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
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface TransformDocument extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(TransformDocument.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.s8C3F193EE11A2F798ACF65489B9E6078").resolveHandle("transforme335doctype");
    
    TransformType getTransform();
    
    void setTransform(final TransformType p0);
    
    TransformType addNewTransform();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(TransformDocument.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static TransformDocument newInstance() {
            return (TransformDocument)getTypeLoader().newInstance(TransformDocument.type, (XmlOptions)null);
        }
        
        public static TransformDocument newInstance(final XmlOptions xmlOptions) {
            return (TransformDocument)getTypeLoader().newInstance(TransformDocument.type, xmlOptions);
        }
        
        public static TransformDocument parse(final String s) throws XmlException {
            return (TransformDocument)getTypeLoader().parse(s, TransformDocument.type, (XmlOptions)null);
        }
        
        public static TransformDocument parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (TransformDocument)getTypeLoader().parse(s, TransformDocument.type, xmlOptions);
        }
        
        public static TransformDocument parse(final File file) throws XmlException, IOException {
            return (TransformDocument)getTypeLoader().parse(file, TransformDocument.type, (XmlOptions)null);
        }
        
        public static TransformDocument parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (TransformDocument)getTypeLoader().parse(file, TransformDocument.type, xmlOptions);
        }
        
        public static TransformDocument parse(final URL url) throws XmlException, IOException {
            return (TransformDocument)getTypeLoader().parse(url, TransformDocument.type, (XmlOptions)null);
        }
        
        public static TransformDocument parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (TransformDocument)getTypeLoader().parse(url, TransformDocument.type, xmlOptions);
        }
        
        public static TransformDocument parse(final InputStream inputStream) throws XmlException, IOException {
            return (TransformDocument)getTypeLoader().parse(inputStream, TransformDocument.type, (XmlOptions)null);
        }
        
        public static TransformDocument parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (TransformDocument)getTypeLoader().parse(inputStream, TransformDocument.type, xmlOptions);
        }
        
        public static TransformDocument parse(final Reader reader) throws XmlException, IOException {
            return (TransformDocument)getTypeLoader().parse(reader, TransformDocument.type, (XmlOptions)null);
        }
        
        public static TransformDocument parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (TransformDocument)getTypeLoader().parse(reader, TransformDocument.type, xmlOptions);
        }
        
        public static TransformDocument parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (TransformDocument)getTypeLoader().parse(xmlStreamReader, TransformDocument.type, (XmlOptions)null);
        }
        
        public static TransformDocument parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (TransformDocument)getTypeLoader().parse(xmlStreamReader, TransformDocument.type, xmlOptions);
        }
        
        public static TransformDocument parse(final Node node) throws XmlException {
            return (TransformDocument)getTypeLoader().parse(node, TransformDocument.type, (XmlOptions)null);
        }
        
        public static TransformDocument parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (TransformDocument)getTypeLoader().parse(node, TransformDocument.type, xmlOptions);
        }
        
        @Deprecated
        public static TransformDocument parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (TransformDocument)getTypeLoader().parse(xmlInputStream, TransformDocument.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static TransformDocument parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (TransformDocument)getTypeLoader().parse(xmlInputStream, TransformDocument.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, TransformDocument.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, TransformDocument.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
