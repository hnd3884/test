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
import org.apache.xmlbeans.XmlObject;

public interface QualifyingPropertiesDocument extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(QualifyingPropertiesDocument.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.s8C3F193EE11A2F798ACF65489B9E6078").resolveHandle("qualifyingproperties53ccdoctype");
    
    QualifyingPropertiesType getQualifyingProperties();
    
    void setQualifyingProperties(final QualifyingPropertiesType p0);
    
    QualifyingPropertiesType addNewQualifyingProperties();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(QualifyingPropertiesDocument.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static QualifyingPropertiesDocument newInstance() {
            return (QualifyingPropertiesDocument)getTypeLoader().newInstance(QualifyingPropertiesDocument.type, (XmlOptions)null);
        }
        
        public static QualifyingPropertiesDocument newInstance(final XmlOptions xmlOptions) {
            return (QualifyingPropertiesDocument)getTypeLoader().newInstance(QualifyingPropertiesDocument.type, xmlOptions);
        }
        
        public static QualifyingPropertiesDocument parse(final String s) throws XmlException {
            return (QualifyingPropertiesDocument)getTypeLoader().parse(s, QualifyingPropertiesDocument.type, (XmlOptions)null);
        }
        
        public static QualifyingPropertiesDocument parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (QualifyingPropertiesDocument)getTypeLoader().parse(s, QualifyingPropertiesDocument.type, xmlOptions);
        }
        
        public static QualifyingPropertiesDocument parse(final File file) throws XmlException, IOException {
            return (QualifyingPropertiesDocument)getTypeLoader().parse(file, QualifyingPropertiesDocument.type, (XmlOptions)null);
        }
        
        public static QualifyingPropertiesDocument parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (QualifyingPropertiesDocument)getTypeLoader().parse(file, QualifyingPropertiesDocument.type, xmlOptions);
        }
        
        public static QualifyingPropertiesDocument parse(final URL url) throws XmlException, IOException {
            return (QualifyingPropertiesDocument)getTypeLoader().parse(url, QualifyingPropertiesDocument.type, (XmlOptions)null);
        }
        
        public static QualifyingPropertiesDocument parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (QualifyingPropertiesDocument)getTypeLoader().parse(url, QualifyingPropertiesDocument.type, xmlOptions);
        }
        
        public static QualifyingPropertiesDocument parse(final InputStream inputStream) throws XmlException, IOException {
            return (QualifyingPropertiesDocument)getTypeLoader().parse(inputStream, QualifyingPropertiesDocument.type, (XmlOptions)null);
        }
        
        public static QualifyingPropertiesDocument parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (QualifyingPropertiesDocument)getTypeLoader().parse(inputStream, QualifyingPropertiesDocument.type, xmlOptions);
        }
        
        public static QualifyingPropertiesDocument parse(final Reader reader) throws XmlException, IOException {
            return (QualifyingPropertiesDocument)getTypeLoader().parse(reader, QualifyingPropertiesDocument.type, (XmlOptions)null);
        }
        
        public static QualifyingPropertiesDocument parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (QualifyingPropertiesDocument)getTypeLoader().parse(reader, QualifyingPropertiesDocument.type, xmlOptions);
        }
        
        public static QualifyingPropertiesDocument parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (QualifyingPropertiesDocument)getTypeLoader().parse(xmlStreamReader, QualifyingPropertiesDocument.type, (XmlOptions)null);
        }
        
        public static QualifyingPropertiesDocument parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (QualifyingPropertiesDocument)getTypeLoader().parse(xmlStreamReader, QualifyingPropertiesDocument.type, xmlOptions);
        }
        
        public static QualifyingPropertiesDocument parse(final Node node) throws XmlException {
            return (QualifyingPropertiesDocument)getTypeLoader().parse(node, QualifyingPropertiesDocument.type, (XmlOptions)null);
        }
        
        public static QualifyingPropertiesDocument parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (QualifyingPropertiesDocument)getTypeLoader().parse(node, QualifyingPropertiesDocument.type, xmlOptions);
        }
        
        @Deprecated
        public static QualifyingPropertiesDocument parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (QualifyingPropertiesDocument)getTypeLoader().parse(xmlInputStream, QualifyingPropertiesDocument.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static QualifyingPropertiesDocument parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (QualifyingPropertiesDocument)getTypeLoader().parse(xmlInputStream, QualifyingPropertiesDocument.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, QualifyingPropertiesDocument.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, QualifyingPropertiesDocument.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
