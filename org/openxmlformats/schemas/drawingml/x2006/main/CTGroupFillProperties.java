package org.openxmlformats.schemas.drawingml.x2006.main;

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

public interface CTGroupFillProperties extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTGroupFillProperties.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctgroupfillpropertiesec66type");
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTGroupFillProperties.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTGroupFillProperties newInstance() {
            return (CTGroupFillProperties)getTypeLoader().newInstance(CTGroupFillProperties.type, (XmlOptions)null);
        }
        
        public static CTGroupFillProperties newInstance(final XmlOptions xmlOptions) {
            return (CTGroupFillProperties)getTypeLoader().newInstance(CTGroupFillProperties.type, xmlOptions);
        }
        
        public static CTGroupFillProperties parse(final String s) throws XmlException {
            return (CTGroupFillProperties)getTypeLoader().parse(s, CTGroupFillProperties.type, (XmlOptions)null);
        }
        
        public static CTGroupFillProperties parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTGroupFillProperties)getTypeLoader().parse(s, CTGroupFillProperties.type, xmlOptions);
        }
        
        public static CTGroupFillProperties parse(final File file) throws XmlException, IOException {
            return (CTGroupFillProperties)getTypeLoader().parse(file, CTGroupFillProperties.type, (XmlOptions)null);
        }
        
        public static CTGroupFillProperties parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTGroupFillProperties)getTypeLoader().parse(file, CTGroupFillProperties.type, xmlOptions);
        }
        
        public static CTGroupFillProperties parse(final URL url) throws XmlException, IOException {
            return (CTGroupFillProperties)getTypeLoader().parse(url, CTGroupFillProperties.type, (XmlOptions)null);
        }
        
        public static CTGroupFillProperties parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTGroupFillProperties)getTypeLoader().parse(url, CTGroupFillProperties.type, xmlOptions);
        }
        
        public static CTGroupFillProperties parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTGroupFillProperties)getTypeLoader().parse(inputStream, CTGroupFillProperties.type, (XmlOptions)null);
        }
        
        public static CTGroupFillProperties parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTGroupFillProperties)getTypeLoader().parse(inputStream, CTGroupFillProperties.type, xmlOptions);
        }
        
        public static CTGroupFillProperties parse(final Reader reader) throws XmlException, IOException {
            return (CTGroupFillProperties)getTypeLoader().parse(reader, CTGroupFillProperties.type, (XmlOptions)null);
        }
        
        public static CTGroupFillProperties parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTGroupFillProperties)getTypeLoader().parse(reader, CTGroupFillProperties.type, xmlOptions);
        }
        
        public static CTGroupFillProperties parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTGroupFillProperties)getTypeLoader().parse(xmlStreamReader, CTGroupFillProperties.type, (XmlOptions)null);
        }
        
        public static CTGroupFillProperties parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTGroupFillProperties)getTypeLoader().parse(xmlStreamReader, CTGroupFillProperties.type, xmlOptions);
        }
        
        public static CTGroupFillProperties parse(final Node node) throws XmlException {
            return (CTGroupFillProperties)getTypeLoader().parse(node, CTGroupFillProperties.type, (XmlOptions)null);
        }
        
        public static CTGroupFillProperties parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTGroupFillProperties)getTypeLoader().parse(node, CTGroupFillProperties.type, xmlOptions);
        }
        
        @Deprecated
        public static CTGroupFillProperties parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTGroupFillProperties)getTypeLoader().parse(xmlInputStream, CTGroupFillProperties.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTGroupFillProperties parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTGroupFillProperties)getTypeLoader().parse(xmlInputStream, CTGroupFillProperties.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTGroupFillProperties.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTGroupFillProperties.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
