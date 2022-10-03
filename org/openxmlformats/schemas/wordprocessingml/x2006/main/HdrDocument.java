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

public interface HdrDocument extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(HdrDocument.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("hdra530doctype");
    
    CTHdrFtr getHdr();
    
    void setHdr(final CTHdrFtr p0);
    
    CTHdrFtr addNewHdr();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(HdrDocument.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static HdrDocument newInstance() {
            return (HdrDocument)getTypeLoader().newInstance(HdrDocument.type, (XmlOptions)null);
        }
        
        public static HdrDocument newInstance(final XmlOptions xmlOptions) {
            return (HdrDocument)getTypeLoader().newInstance(HdrDocument.type, xmlOptions);
        }
        
        public static HdrDocument parse(final String s) throws XmlException {
            return (HdrDocument)getTypeLoader().parse(s, HdrDocument.type, (XmlOptions)null);
        }
        
        public static HdrDocument parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (HdrDocument)getTypeLoader().parse(s, HdrDocument.type, xmlOptions);
        }
        
        public static HdrDocument parse(final File file) throws XmlException, IOException {
            return (HdrDocument)getTypeLoader().parse(file, HdrDocument.type, (XmlOptions)null);
        }
        
        public static HdrDocument parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (HdrDocument)getTypeLoader().parse(file, HdrDocument.type, xmlOptions);
        }
        
        public static HdrDocument parse(final URL url) throws XmlException, IOException {
            return (HdrDocument)getTypeLoader().parse(url, HdrDocument.type, (XmlOptions)null);
        }
        
        public static HdrDocument parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (HdrDocument)getTypeLoader().parse(url, HdrDocument.type, xmlOptions);
        }
        
        public static HdrDocument parse(final InputStream inputStream) throws XmlException, IOException {
            return (HdrDocument)getTypeLoader().parse(inputStream, HdrDocument.type, (XmlOptions)null);
        }
        
        public static HdrDocument parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (HdrDocument)getTypeLoader().parse(inputStream, HdrDocument.type, xmlOptions);
        }
        
        public static HdrDocument parse(final Reader reader) throws XmlException, IOException {
            return (HdrDocument)getTypeLoader().parse(reader, HdrDocument.type, (XmlOptions)null);
        }
        
        public static HdrDocument parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (HdrDocument)getTypeLoader().parse(reader, HdrDocument.type, xmlOptions);
        }
        
        public static HdrDocument parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (HdrDocument)getTypeLoader().parse(xmlStreamReader, HdrDocument.type, (XmlOptions)null);
        }
        
        public static HdrDocument parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (HdrDocument)getTypeLoader().parse(xmlStreamReader, HdrDocument.type, xmlOptions);
        }
        
        public static HdrDocument parse(final Node node) throws XmlException {
            return (HdrDocument)getTypeLoader().parse(node, HdrDocument.type, (XmlOptions)null);
        }
        
        public static HdrDocument parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (HdrDocument)getTypeLoader().parse(node, HdrDocument.type, xmlOptions);
        }
        
        @Deprecated
        public static HdrDocument parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (HdrDocument)getTypeLoader().parse(xmlInputStream, HdrDocument.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static HdrDocument parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (HdrDocument)getTypeLoader().parse(xmlInputStream, HdrDocument.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, HdrDocument.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, HdrDocument.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
