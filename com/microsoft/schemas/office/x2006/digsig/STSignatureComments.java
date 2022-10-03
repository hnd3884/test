package com.microsoft.schemas.office.x2006.digsig;

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
import org.apache.xmlbeans.XmlString;

public interface STSignatureComments extends XmlString
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(STSignatureComments.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.s8C3F193EE11A2F798ACF65489B9E6078").resolveHandle("stsignaturecomments47batype");
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        public static STSignatureComments newValue(final Object o) {
            return (STSignatureComments)STSignatureComments.type.newValue(o);
        }
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(STSignatureComments.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static STSignatureComments newInstance() {
            return (STSignatureComments)getTypeLoader().newInstance(STSignatureComments.type, (XmlOptions)null);
        }
        
        public static STSignatureComments newInstance(final XmlOptions xmlOptions) {
            return (STSignatureComments)getTypeLoader().newInstance(STSignatureComments.type, xmlOptions);
        }
        
        public static STSignatureComments parse(final String s) throws XmlException {
            return (STSignatureComments)getTypeLoader().parse(s, STSignatureComments.type, (XmlOptions)null);
        }
        
        public static STSignatureComments parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (STSignatureComments)getTypeLoader().parse(s, STSignatureComments.type, xmlOptions);
        }
        
        public static STSignatureComments parse(final File file) throws XmlException, IOException {
            return (STSignatureComments)getTypeLoader().parse(file, STSignatureComments.type, (XmlOptions)null);
        }
        
        public static STSignatureComments parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STSignatureComments)getTypeLoader().parse(file, STSignatureComments.type, xmlOptions);
        }
        
        public static STSignatureComments parse(final URL url) throws XmlException, IOException {
            return (STSignatureComments)getTypeLoader().parse(url, STSignatureComments.type, (XmlOptions)null);
        }
        
        public static STSignatureComments parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STSignatureComments)getTypeLoader().parse(url, STSignatureComments.type, xmlOptions);
        }
        
        public static STSignatureComments parse(final InputStream inputStream) throws XmlException, IOException {
            return (STSignatureComments)getTypeLoader().parse(inputStream, STSignatureComments.type, (XmlOptions)null);
        }
        
        public static STSignatureComments parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STSignatureComments)getTypeLoader().parse(inputStream, STSignatureComments.type, xmlOptions);
        }
        
        public static STSignatureComments parse(final Reader reader) throws XmlException, IOException {
            return (STSignatureComments)getTypeLoader().parse(reader, STSignatureComments.type, (XmlOptions)null);
        }
        
        public static STSignatureComments parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STSignatureComments)getTypeLoader().parse(reader, STSignatureComments.type, xmlOptions);
        }
        
        public static STSignatureComments parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (STSignatureComments)getTypeLoader().parse(xmlStreamReader, STSignatureComments.type, (XmlOptions)null);
        }
        
        public static STSignatureComments parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (STSignatureComments)getTypeLoader().parse(xmlStreamReader, STSignatureComments.type, xmlOptions);
        }
        
        public static STSignatureComments parse(final Node node) throws XmlException {
            return (STSignatureComments)getTypeLoader().parse(node, STSignatureComments.type, (XmlOptions)null);
        }
        
        public static STSignatureComments parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (STSignatureComments)getTypeLoader().parse(node, STSignatureComments.type, xmlOptions);
        }
        
        @Deprecated
        public static STSignatureComments parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (STSignatureComments)getTypeLoader().parse(xmlInputStream, STSignatureComments.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static STSignatureComments parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (STSignatureComments)getTypeLoader().parse(xmlInputStream, STSignatureComments.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STSignatureComments.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STSignatureComments.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
