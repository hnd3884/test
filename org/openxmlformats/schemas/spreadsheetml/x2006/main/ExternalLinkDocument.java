package org.openxmlformats.schemas.spreadsheetml.x2006.main;

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

public interface ExternalLinkDocument extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(ExternalLinkDocument.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("externallinkb4c2doctype");
    
    CTExternalLink getExternalLink();
    
    void setExternalLink(final CTExternalLink p0);
    
    CTExternalLink addNewExternalLink();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(ExternalLinkDocument.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static ExternalLinkDocument newInstance() {
            return (ExternalLinkDocument)getTypeLoader().newInstance(ExternalLinkDocument.type, (XmlOptions)null);
        }
        
        public static ExternalLinkDocument newInstance(final XmlOptions xmlOptions) {
            return (ExternalLinkDocument)getTypeLoader().newInstance(ExternalLinkDocument.type, xmlOptions);
        }
        
        public static ExternalLinkDocument parse(final String s) throws XmlException {
            return (ExternalLinkDocument)getTypeLoader().parse(s, ExternalLinkDocument.type, (XmlOptions)null);
        }
        
        public static ExternalLinkDocument parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (ExternalLinkDocument)getTypeLoader().parse(s, ExternalLinkDocument.type, xmlOptions);
        }
        
        public static ExternalLinkDocument parse(final File file) throws XmlException, IOException {
            return (ExternalLinkDocument)getTypeLoader().parse(file, ExternalLinkDocument.type, (XmlOptions)null);
        }
        
        public static ExternalLinkDocument parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (ExternalLinkDocument)getTypeLoader().parse(file, ExternalLinkDocument.type, xmlOptions);
        }
        
        public static ExternalLinkDocument parse(final URL url) throws XmlException, IOException {
            return (ExternalLinkDocument)getTypeLoader().parse(url, ExternalLinkDocument.type, (XmlOptions)null);
        }
        
        public static ExternalLinkDocument parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (ExternalLinkDocument)getTypeLoader().parse(url, ExternalLinkDocument.type, xmlOptions);
        }
        
        public static ExternalLinkDocument parse(final InputStream inputStream) throws XmlException, IOException {
            return (ExternalLinkDocument)getTypeLoader().parse(inputStream, ExternalLinkDocument.type, (XmlOptions)null);
        }
        
        public static ExternalLinkDocument parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (ExternalLinkDocument)getTypeLoader().parse(inputStream, ExternalLinkDocument.type, xmlOptions);
        }
        
        public static ExternalLinkDocument parse(final Reader reader) throws XmlException, IOException {
            return (ExternalLinkDocument)getTypeLoader().parse(reader, ExternalLinkDocument.type, (XmlOptions)null);
        }
        
        public static ExternalLinkDocument parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (ExternalLinkDocument)getTypeLoader().parse(reader, ExternalLinkDocument.type, xmlOptions);
        }
        
        public static ExternalLinkDocument parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (ExternalLinkDocument)getTypeLoader().parse(xmlStreamReader, ExternalLinkDocument.type, (XmlOptions)null);
        }
        
        public static ExternalLinkDocument parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (ExternalLinkDocument)getTypeLoader().parse(xmlStreamReader, ExternalLinkDocument.type, xmlOptions);
        }
        
        public static ExternalLinkDocument parse(final Node node) throws XmlException {
            return (ExternalLinkDocument)getTypeLoader().parse(node, ExternalLinkDocument.type, (XmlOptions)null);
        }
        
        public static ExternalLinkDocument parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (ExternalLinkDocument)getTypeLoader().parse(node, ExternalLinkDocument.type, xmlOptions);
        }
        
        @Deprecated
        public static ExternalLinkDocument parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (ExternalLinkDocument)getTypeLoader().parse(xmlInputStream, ExternalLinkDocument.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static ExternalLinkDocument parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (ExternalLinkDocument)getTypeLoader().parse(xmlInputStream, ExternalLinkDocument.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, ExternalLinkDocument.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, ExternalLinkDocument.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
