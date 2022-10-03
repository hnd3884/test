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

public interface CTNoFillProperties extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTNoFillProperties.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctnofillpropertiesbf92type");
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTNoFillProperties.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTNoFillProperties newInstance() {
            return (CTNoFillProperties)getTypeLoader().newInstance(CTNoFillProperties.type, (XmlOptions)null);
        }
        
        public static CTNoFillProperties newInstance(final XmlOptions xmlOptions) {
            return (CTNoFillProperties)getTypeLoader().newInstance(CTNoFillProperties.type, xmlOptions);
        }
        
        public static CTNoFillProperties parse(final String s) throws XmlException {
            return (CTNoFillProperties)getTypeLoader().parse(s, CTNoFillProperties.type, (XmlOptions)null);
        }
        
        public static CTNoFillProperties parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTNoFillProperties)getTypeLoader().parse(s, CTNoFillProperties.type, xmlOptions);
        }
        
        public static CTNoFillProperties parse(final File file) throws XmlException, IOException {
            return (CTNoFillProperties)getTypeLoader().parse(file, CTNoFillProperties.type, (XmlOptions)null);
        }
        
        public static CTNoFillProperties parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTNoFillProperties)getTypeLoader().parse(file, CTNoFillProperties.type, xmlOptions);
        }
        
        public static CTNoFillProperties parse(final URL url) throws XmlException, IOException {
            return (CTNoFillProperties)getTypeLoader().parse(url, CTNoFillProperties.type, (XmlOptions)null);
        }
        
        public static CTNoFillProperties parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTNoFillProperties)getTypeLoader().parse(url, CTNoFillProperties.type, xmlOptions);
        }
        
        public static CTNoFillProperties parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTNoFillProperties)getTypeLoader().parse(inputStream, CTNoFillProperties.type, (XmlOptions)null);
        }
        
        public static CTNoFillProperties parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTNoFillProperties)getTypeLoader().parse(inputStream, CTNoFillProperties.type, xmlOptions);
        }
        
        public static CTNoFillProperties parse(final Reader reader) throws XmlException, IOException {
            return (CTNoFillProperties)getTypeLoader().parse(reader, CTNoFillProperties.type, (XmlOptions)null);
        }
        
        public static CTNoFillProperties parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTNoFillProperties)getTypeLoader().parse(reader, CTNoFillProperties.type, xmlOptions);
        }
        
        public static CTNoFillProperties parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTNoFillProperties)getTypeLoader().parse(xmlStreamReader, CTNoFillProperties.type, (XmlOptions)null);
        }
        
        public static CTNoFillProperties parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTNoFillProperties)getTypeLoader().parse(xmlStreamReader, CTNoFillProperties.type, xmlOptions);
        }
        
        public static CTNoFillProperties parse(final Node node) throws XmlException {
            return (CTNoFillProperties)getTypeLoader().parse(node, CTNoFillProperties.type, (XmlOptions)null);
        }
        
        public static CTNoFillProperties parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTNoFillProperties)getTypeLoader().parse(node, CTNoFillProperties.type, xmlOptions);
        }
        
        @Deprecated
        public static CTNoFillProperties parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTNoFillProperties)getTypeLoader().parse(xmlInputStream, CTNoFillProperties.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTNoFillProperties parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTNoFillProperties)getTypeLoader().parse(xmlInputStream, CTNoFillProperties.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTNoFillProperties.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTNoFillProperties.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
