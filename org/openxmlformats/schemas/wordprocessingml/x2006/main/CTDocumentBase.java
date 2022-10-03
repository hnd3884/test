package org.openxmlformats.schemas.wordprocessingml.x2006.main;

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

public interface CTDocumentBase extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTDocumentBase.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctdocumentbasedf5ctype");
    
    CTBackground getBackground();
    
    boolean isSetBackground();
    
    void setBackground(final CTBackground p0);
    
    CTBackground addNewBackground();
    
    void unsetBackground();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTDocumentBase.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTDocumentBase newInstance() {
            return (CTDocumentBase)getTypeLoader().newInstance(CTDocumentBase.type, (XmlOptions)null);
        }
        
        public static CTDocumentBase newInstance(final XmlOptions xmlOptions) {
            return (CTDocumentBase)getTypeLoader().newInstance(CTDocumentBase.type, xmlOptions);
        }
        
        public static CTDocumentBase parse(final String s) throws XmlException {
            return (CTDocumentBase)getTypeLoader().parse(s, CTDocumentBase.type, (XmlOptions)null);
        }
        
        public static CTDocumentBase parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTDocumentBase)getTypeLoader().parse(s, CTDocumentBase.type, xmlOptions);
        }
        
        public static CTDocumentBase parse(final File file) throws XmlException, IOException {
            return (CTDocumentBase)getTypeLoader().parse(file, CTDocumentBase.type, (XmlOptions)null);
        }
        
        public static CTDocumentBase parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTDocumentBase)getTypeLoader().parse(file, CTDocumentBase.type, xmlOptions);
        }
        
        public static CTDocumentBase parse(final URL url) throws XmlException, IOException {
            return (CTDocumentBase)getTypeLoader().parse(url, CTDocumentBase.type, (XmlOptions)null);
        }
        
        public static CTDocumentBase parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTDocumentBase)getTypeLoader().parse(url, CTDocumentBase.type, xmlOptions);
        }
        
        public static CTDocumentBase parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTDocumentBase)getTypeLoader().parse(inputStream, CTDocumentBase.type, (XmlOptions)null);
        }
        
        public static CTDocumentBase parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTDocumentBase)getTypeLoader().parse(inputStream, CTDocumentBase.type, xmlOptions);
        }
        
        public static CTDocumentBase parse(final Reader reader) throws XmlException, IOException {
            return (CTDocumentBase)getTypeLoader().parse(reader, CTDocumentBase.type, (XmlOptions)null);
        }
        
        public static CTDocumentBase parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTDocumentBase)getTypeLoader().parse(reader, CTDocumentBase.type, xmlOptions);
        }
        
        public static CTDocumentBase parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTDocumentBase)getTypeLoader().parse(xmlStreamReader, CTDocumentBase.type, (XmlOptions)null);
        }
        
        public static CTDocumentBase parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTDocumentBase)getTypeLoader().parse(xmlStreamReader, CTDocumentBase.type, xmlOptions);
        }
        
        public static CTDocumentBase parse(final Node node) throws XmlException {
            return (CTDocumentBase)getTypeLoader().parse(node, CTDocumentBase.type, (XmlOptions)null);
        }
        
        public static CTDocumentBase parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTDocumentBase)getTypeLoader().parse(node, CTDocumentBase.type, xmlOptions);
        }
        
        @Deprecated
        public static CTDocumentBase parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTDocumentBase)getTypeLoader().parse(xmlInputStream, CTDocumentBase.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTDocumentBase parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTDocumentBase)getTypeLoader().parse(xmlInputStream, CTDocumentBase.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTDocumentBase.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTDocumentBase.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
