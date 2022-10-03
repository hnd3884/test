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

public interface CTTextNoAutofit extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTTextNoAutofit.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("cttextnoautofit1045type");
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTTextNoAutofit.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTTextNoAutofit newInstance() {
            return (CTTextNoAutofit)getTypeLoader().newInstance(CTTextNoAutofit.type, (XmlOptions)null);
        }
        
        public static CTTextNoAutofit newInstance(final XmlOptions xmlOptions) {
            return (CTTextNoAutofit)getTypeLoader().newInstance(CTTextNoAutofit.type, xmlOptions);
        }
        
        public static CTTextNoAutofit parse(final String s) throws XmlException {
            return (CTTextNoAutofit)getTypeLoader().parse(s, CTTextNoAutofit.type, (XmlOptions)null);
        }
        
        public static CTTextNoAutofit parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTTextNoAutofit)getTypeLoader().parse(s, CTTextNoAutofit.type, xmlOptions);
        }
        
        public static CTTextNoAutofit parse(final File file) throws XmlException, IOException {
            return (CTTextNoAutofit)getTypeLoader().parse(file, CTTextNoAutofit.type, (XmlOptions)null);
        }
        
        public static CTTextNoAutofit parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTextNoAutofit)getTypeLoader().parse(file, CTTextNoAutofit.type, xmlOptions);
        }
        
        public static CTTextNoAutofit parse(final URL url) throws XmlException, IOException {
            return (CTTextNoAutofit)getTypeLoader().parse(url, CTTextNoAutofit.type, (XmlOptions)null);
        }
        
        public static CTTextNoAutofit parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTextNoAutofit)getTypeLoader().parse(url, CTTextNoAutofit.type, xmlOptions);
        }
        
        public static CTTextNoAutofit parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTTextNoAutofit)getTypeLoader().parse(inputStream, CTTextNoAutofit.type, (XmlOptions)null);
        }
        
        public static CTTextNoAutofit parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTextNoAutofit)getTypeLoader().parse(inputStream, CTTextNoAutofit.type, xmlOptions);
        }
        
        public static CTTextNoAutofit parse(final Reader reader) throws XmlException, IOException {
            return (CTTextNoAutofit)getTypeLoader().parse(reader, CTTextNoAutofit.type, (XmlOptions)null);
        }
        
        public static CTTextNoAutofit parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTextNoAutofit)getTypeLoader().parse(reader, CTTextNoAutofit.type, xmlOptions);
        }
        
        public static CTTextNoAutofit parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTTextNoAutofit)getTypeLoader().parse(xmlStreamReader, CTTextNoAutofit.type, (XmlOptions)null);
        }
        
        public static CTTextNoAutofit parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTTextNoAutofit)getTypeLoader().parse(xmlStreamReader, CTTextNoAutofit.type, xmlOptions);
        }
        
        public static CTTextNoAutofit parse(final Node node) throws XmlException {
            return (CTTextNoAutofit)getTypeLoader().parse(node, CTTextNoAutofit.type, (XmlOptions)null);
        }
        
        public static CTTextNoAutofit parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTTextNoAutofit)getTypeLoader().parse(node, CTTextNoAutofit.type, xmlOptions);
        }
        
        @Deprecated
        public static CTTextNoAutofit parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTTextNoAutofit)getTypeLoader().parse(xmlInputStream, CTTextNoAutofit.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTTextNoAutofit parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTTextNoAutofit)getTypeLoader().parse(xmlInputStream, CTTextNoAutofit.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTTextNoAutofit.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTTextNoAutofit.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
