package com.microsoft.schemas.office.visio.x2012.main;

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

public interface MasterContentsDocument extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(MasterContentsDocument.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("mastercontentscb9edoctype");
    
    PageContentsType getMasterContents();
    
    void setMasterContents(final PageContentsType p0);
    
    PageContentsType addNewMasterContents();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(MasterContentsDocument.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static MasterContentsDocument newInstance() {
            return (MasterContentsDocument)getTypeLoader().newInstance(MasterContentsDocument.type, (XmlOptions)null);
        }
        
        public static MasterContentsDocument newInstance(final XmlOptions xmlOptions) {
            return (MasterContentsDocument)getTypeLoader().newInstance(MasterContentsDocument.type, xmlOptions);
        }
        
        public static MasterContentsDocument parse(final String s) throws XmlException {
            return (MasterContentsDocument)getTypeLoader().parse(s, MasterContentsDocument.type, (XmlOptions)null);
        }
        
        public static MasterContentsDocument parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (MasterContentsDocument)getTypeLoader().parse(s, MasterContentsDocument.type, xmlOptions);
        }
        
        public static MasterContentsDocument parse(final File file) throws XmlException, IOException {
            return (MasterContentsDocument)getTypeLoader().parse(file, MasterContentsDocument.type, (XmlOptions)null);
        }
        
        public static MasterContentsDocument parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (MasterContentsDocument)getTypeLoader().parse(file, MasterContentsDocument.type, xmlOptions);
        }
        
        public static MasterContentsDocument parse(final URL url) throws XmlException, IOException {
            return (MasterContentsDocument)getTypeLoader().parse(url, MasterContentsDocument.type, (XmlOptions)null);
        }
        
        public static MasterContentsDocument parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (MasterContentsDocument)getTypeLoader().parse(url, MasterContentsDocument.type, xmlOptions);
        }
        
        public static MasterContentsDocument parse(final InputStream inputStream) throws XmlException, IOException {
            return (MasterContentsDocument)getTypeLoader().parse(inputStream, MasterContentsDocument.type, (XmlOptions)null);
        }
        
        public static MasterContentsDocument parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (MasterContentsDocument)getTypeLoader().parse(inputStream, MasterContentsDocument.type, xmlOptions);
        }
        
        public static MasterContentsDocument parse(final Reader reader) throws XmlException, IOException {
            return (MasterContentsDocument)getTypeLoader().parse(reader, MasterContentsDocument.type, (XmlOptions)null);
        }
        
        public static MasterContentsDocument parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (MasterContentsDocument)getTypeLoader().parse(reader, MasterContentsDocument.type, xmlOptions);
        }
        
        public static MasterContentsDocument parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (MasterContentsDocument)getTypeLoader().parse(xmlStreamReader, MasterContentsDocument.type, (XmlOptions)null);
        }
        
        public static MasterContentsDocument parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (MasterContentsDocument)getTypeLoader().parse(xmlStreamReader, MasterContentsDocument.type, xmlOptions);
        }
        
        public static MasterContentsDocument parse(final Node node) throws XmlException {
            return (MasterContentsDocument)getTypeLoader().parse(node, MasterContentsDocument.type, (XmlOptions)null);
        }
        
        public static MasterContentsDocument parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (MasterContentsDocument)getTypeLoader().parse(node, MasterContentsDocument.type, xmlOptions);
        }
        
        @Deprecated
        public static MasterContentsDocument parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (MasterContentsDocument)getTypeLoader().parse(xmlInputStream, MasterContentsDocument.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static MasterContentsDocument parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (MasterContentsDocument)getTypeLoader().parse(xmlInputStream, MasterContentsDocument.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, MasterContentsDocument.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, MasterContentsDocument.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
