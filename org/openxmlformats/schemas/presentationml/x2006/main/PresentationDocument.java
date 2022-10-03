package org.openxmlformats.schemas.presentationml.x2006.main;

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

public interface PresentationDocument extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(PresentationDocument.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("presentation02f7doctype");
    
    CTPresentation getPresentation();
    
    void setPresentation(final CTPresentation p0);
    
    CTPresentation addNewPresentation();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(PresentationDocument.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static PresentationDocument newInstance() {
            return (PresentationDocument)getTypeLoader().newInstance(PresentationDocument.type, (XmlOptions)null);
        }
        
        public static PresentationDocument newInstance(final XmlOptions xmlOptions) {
            return (PresentationDocument)getTypeLoader().newInstance(PresentationDocument.type, xmlOptions);
        }
        
        public static PresentationDocument parse(final String s) throws XmlException {
            return (PresentationDocument)getTypeLoader().parse(s, PresentationDocument.type, (XmlOptions)null);
        }
        
        public static PresentationDocument parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (PresentationDocument)getTypeLoader().parse(s, PresentationDocument.type, xmlOptions);
        }
        
        public static PresentationDocument parse(final File file) throws XmlException, IOException {
            return (PresentationDocument)getTypeLoader().parse(file, PresentationDocument.type, (XmlOptions)null);
        }
        
        public static PresentationDocument parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (PresentationDocument)getTypeLoader().parse(file, PresentationDocument.type, xmlOptions);
        }
        
        public static PresentationDocument parse(final URL url) throws XmlException, IOException {
            return (PresentationDocument)getTypeLoader().parse(url, PresentationDocument.type, (XmlOptions)null);
        }
        
        public static PresentationDocument parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (PresentationDocument)getTypeLoader().parse(url, PresentationDocument.type, xmlOptions);
        }
        
        public static PresentationDocument parse(final InputStream inputStream) throws XmlException, IOException {
            return (PresentationDocument)getTypeLoader().parse(inputStream, PresentationDocument.type, (XmlOptions)null);
        }
        
        public static PresentationDocument parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (PresentationDocument)getTypeLoader().parse(inputStream, PresentationDocument.type, xmlOptions);
        }
        
        public static PresentationDocument parse(final Reader reader) throws XmlException, IOException {
            return (PresentationDocument)getTypeLoader().parse(reader, PresentationDocument.type, (XmlOptions)null);
        }
        
        public static PresentationDocument parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (PresentationDocument)getTypeLoader().parse(reader, PresentationDocument.type, xmlOptions);
        }
        
        public static PresentationDocument parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (PresentationDocument)getTypeLoader().parse(xmlStreamReader, PresentationDocument.type, (XmlOptions)null);
        }
        
        public static PresentationDocument parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (PresentationDocument)getTypeLoader().parse(xmlStreamReader, PresentationDocument.type, xmlOptions);
        }
        
        public static PresentationDocument parse(final Node node) throws XmlException {
            return (PresentationDocument)getTypeLoader().parse(node, PresentationDocument.type, (XmlOptions)null);
        }
        
        public static PresentationDocument parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (PresentationDocument)getTypeLoader().parse(node, PresentationDocument.type, xmlOptions);
        }
        
        @Deprecated
        public static PresentationDocument parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (PresentationDocument)getTypeLoader().parse(xmlInputStream, PresentationDocument.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static PresentationDocument parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (PresentationDocument)getTypeLoader().parse(xmlInputStream, PresentationDocument.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, PresentationDocument.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, PresentationDocument.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
