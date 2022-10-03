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

public interface CTPath2DClose extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTPath2DClose.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctpath2dclose09f2type");
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTPath2DClose.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTPath2DClose newInstance() {
            return (CTPath2DClose)getTypeLoader().newInstance(CTPath2DClose.type, (XmlOptions)null);
        }
        
        public static CTPath2DClose newInstance(final XmlOptions xmlOptions) {
            return (CTPath2DClose)getTypeLoader().newInstance(CTPath2DClose.type, xmlOptions);
        }
        
        public static CTPath2DClose parse(final String s) throws XmlException {
            return (CTPath2DClose)getTypeLoader().parse(s, CTPath2DClose.type, (XmlOptions)null);
        }
        
        public static CTPath2DClose parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTPath2DClose)getTypeLoader().parse(s, CTPath2DClose.type, xmlOptions);
        }
        
        public static CTPath2DClose parse(final File file) throws XmlException, IOException {
            return (CTPath2DClose)getTypeLoader().parse(file, CTPath2DClose.type, (XmlOptions)null);
        }
        
        public static CTPath2DClose parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTPath2DClose)getTypeLoader().parse(file, CTPath2DClose.type, xmlOptions);
        }
        
        public static CTPath2DClose parse(final URL url) throws XmlException, IOException {
            return (CTPath2DClose)getTypeLoader().parse(url, CTPath2DClose.type, (XmlOptions)null);
        }
        
        public static CTPath2DClose parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTPath2DClose)getTypeLoader().parse(url, CTPath2DClose.type, xmlOptions);
        }
        
        public static CTPath2DClose parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTPath2DClose)getTypeLoader().parse(inputStream, CTPath2DClose.type, (XmlOptions)null);
        }
        
        public static CTPath2DClose parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTPath2DClose)getTypeLoader().parse(inputStream, CTPath2DClose.type, xmlOptions);
        }
        
        public static CTPath2DClose parse(final Reader reader) throws XmlException, IOException {
            return (CTPath2DClose)getTypeLoader().parse(reader, CTPath2DClose.type, (XmlOptions)null);
        }
        
        public static CTPath2DClose parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTPath2DClose)getTypeLoader().parse(reader, CTPath2DClose.type, xmlOptions);
        }
        
        public static CTPath2DClose parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTPath2DClose)getTypeLoader().parse(xmlStreamReader, CTPath2DClose.type, (XmlOptions)null);
        }
        
        public static CTPath2DClose parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTPath2DClose)getTypeLoader().parse(xmlStreamReader, CTPath2DClose.type, xmlOptions);
        }
        
        public static CTPath2DClose parse(final Node node) throws XmlException {
            return (CTPath2DClose)getTypeLoader().parse(node, CTPath2DClose.type, (XmlOptions)null);
        }
        
        public static CTPath2DClose parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTPath2DClose)getTypeLoader().parse(node, CTPath2DClose.type, xmlOptions);
        }
        
        @Deprecated
        public static CTPath2DClose parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTPath2DClose)getTypeLoader().parse(xmlInputStream, CTPath2DClose.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTPath2DClose parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTPath2DClose)getTypeLoader().parse(xmlInputStream, CTPath2DClose.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTPath2DClose.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTPath2DClose.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
