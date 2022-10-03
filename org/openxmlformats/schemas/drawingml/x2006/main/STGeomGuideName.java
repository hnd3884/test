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
import org.apache.xmlbeans.XmlToken;

public interface STGeomGuideName extends XmlToken
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(STGeomGuideName.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("stgeomguidename366ctype");
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        public static STGeomGuideName newValue(final Object o) {
            return (STGeomGuideName)STGeomGuideName.type.newValue(o);
        }
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(STGeomGuideName.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static STGeomGuideName newInstance() {
            return (STGeomGuideName)getTypeLoader().newInstance(STGeomGuideName.type, (XmlOptions)null);
        }
        
        public static STGeomGuideName newInstance(final XmlOptions xmlOptions) {
            return (STGeomGuideName)getTypeLoader().newInstance(STGeomGuideName.type, xmlOptions);
        }
        
        public static STGeomGuideName parse(final String s) throws XmlException {
            return (STGeomGuideName)getTypeLoader().parse(s, STGeomGuideName.type, (XmlOptions)null);
        }
        
        public static STGeomGuideName parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (STGeomGuideName)getTypeLoader().parse(s, STGeomGuideName.type, xmlOptions);
        }
        
        public static STGeomGuideName parse(final File file) throws XmlException, IOException {
            return (STGeomGuideName)getTypeLoader().parse(file, STGeomGuideName.type, (XmlOptions)null);
        }
        
        public static STGeomGuideName parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STGeomGuideName)getTypeLoader().parse(file, STGeomGuideName.type, xmlOptions);
        }
        
        public static STGeomGuideName parse(final URL url) throws XmlException, IOException {
            return (STGeomGuideName)getTypeLoader().parse(url, STGeomGuideName.type, (XmlOptions)null);
        }
        
        public static STGeomGuideName parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STGeomGuideName)getTypeLoader().parse(url, STGeomGuideName.type, xmlOptions);
        }
        
        public static STGeomGuideName parse(final InputStream inputStream) throws XmlException, IOException {
            return (STGeomGuideName)getTypeLoader().parse(inputStream, STGeomGuideName.type, (XmlOptions)null);
        }
        
        public static STGeomGuideName parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STGeomGuideName)getTypeLoader().parse(inputStream, STGeomGuideName.type, xmlOptions);
        }
        
        public static STGeomGuideName parse(final Reader reader) throws XmlException, IOException {
            return (STGeomGuideName)getTypeLoader().parse(reader, STGeomGuideName.type, (XmlOptions)null);
        }
        
        public static STGeomGuideName parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STGeomGuideName)getTypeLoader().parse(reader, STGeomGuideName.type, xmlOptions);
        }
        
        public static STGeomGuideName parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (STGeomGuideName)getTypeLoader().parse(xmlStreamReader, STGeomGuideName.type, (XmlOptions)null);
        }
        
        public static STGeomGuideName parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (STGeomGuideName)getTypeLoader().parse(xmlStreamReader, STGeomGuideName.type, xmlOptions);
        }
        
        public static STGeomGuideName parse(final Node node) throws XmlException {
            return (STGeomGuideName)getTypeLoader().parse(node, STGeomGuideName.type, (XmlOptions)null);
        }
        
        public static STGeomGuideName parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (STGeomGuideName)getTypeLoader().parse(node, STGeomGuideName.type, xmlOptions);
        }
        
        @Deprecated
        public static STGeomGuideName parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (STGeomGuideName)getTypeLoader().parse(xmlInputStream, STGeomGuideName.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static STGeomGuideName parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (STGeomGuideName)getTypeLoader().parse(xmlInputStream, STGeomGuideName.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STGeomGuideName.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STGeomGuideName.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
