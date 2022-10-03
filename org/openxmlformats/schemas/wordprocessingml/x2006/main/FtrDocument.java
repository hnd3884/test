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

public interface FtrDocument extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(FtrDocument.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ftre182doctype");
    
    CTHdrFtr getFtr();
    
    void setFtr(final CTHdrFtr p0);
    
    CTHdrFtr addNewFtr();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(FtrDocument.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static FtrDocument newInstance() {
            return (FtrDocument)getTypeLoader().newInstance(FtrDocument.type, (XmlOptions)null);
        }
        
        public static FtrDocument newInstance(final XmlOptions xmlOptions) {
            return (FtrDocument)getTypeLoader().newInstance(FtrDocument.type, xmlOptions);
        }
        
        public static FtrDocument parse(final String s) throws XmlException {
            return (FtrDocument)getTypeLoader().parse(s, FtrDocument.type, (XmlOptions)null);
        }
        
        public static FtrDocument parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (FtrDocument)getTypeLoader().parse(s, FtrDocument.type, xmlOptions);
        }
        
        public static FtrDocument parse(final File file) throws XmlException, IOException {
            return (FtrDocument)getTypeLoader().parse(file, FtrDocument.type, (XmlOptions)null);
        }
        
        public static FtrDocument parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (FtrDocument)getTypeLoader().parse(file, FtrDocument.type, xmlOptions);
        }
        
        public static FtrDocument parse(final URL url) throws XmlException, IOException {
            return (FtrDocument)getTypeLoader().parse(url, FtrDocument.type, (XmlOptions)null);
        }
        
        public static FtrDocument parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (FtrDocument)getTypeLoader().parse(url, FtrDocument.type, xmlOptions);
        }
        
        public static FtrDocument parse(final InputStream inputStream) throws XmlException, IOException {
            return (FtrDocument)getTypeLoader().parse(inputStream, FtrDocument.type, (XmlOptions)null);
        }
        
        public static FtrDocument parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (FtrDocument)getTypeLoader().parse(inputStream, FtrDocument.type, xmlOptions);
        }
        
        public static FtrDocument parse(final Reader reader) throws XmlException, IOException {
            return (FtrDocument)getTypeLoader().parse(reader, FtrDocument.type, (XmlOptions)null);
        }
        
        public static FtrDocument parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (FtrDocument)getTypeLoader().parse(reader, FtrDocument.type, xmlOptions);
        }
        
        public static FtrDocument parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (FtrDocument)getTypeLoader().parse(xmlStreamReader, FtrDocument.type, (XmlOptions)null);
        }
        
        public static FtrDocument parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (FtrDocument)getTypeLoader().parse(xmlStreamReader, FtrDocument.type, xmlOptions);
        }
        
        public static FtrDocument parse(final Node node) throws XmlException {
            return (FtrDocument)getTypeLoader().parse(node, FtrDocument.type, (XmlOptions)null);
        }
        
        public static FtrDocument parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (FtrDocument)getTypeLoader().parse(node, FtrDocument.type, xmlOptions);
        }
        
        @Deprecated
        public static FtrDocument parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (FtrDocument)getTypeLoader().parse(xmlInputStream, FtrDocument.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static FtrDocument parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (FtrDocument)getTypeLoader().parse(xmlInputStream, FtrDocument.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, FtrDocument.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, FtrDocument.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
