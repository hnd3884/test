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

public interface STTextIndent extends STCoordinate32
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(STTextIndent.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("sttextindent16e4type");
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        public static STTextIndent newValue(final Object o) {
            return (STTextIndent)STTextIndent.type.newValue(o);
        }
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(STTextIndent.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static STTextIndent newInstance() {
            return (STTextIndent)getTypeLoader().newInstance(STTextIndent.type, (XmlOptions)null);
        }
        
        public static STTextIndent newInstance(final XmlOptions xmlOptions) {
            return (STTextIndent)getTypeLoader().newInstance(STTextIndent.type, xmlOptions);
        }
        
        public static STTextIndent parse(final String s) throws XmlException {
            return (STTextIndent)getTypeLoader().parse(s, STTextIndent.type, (XmlOptions)null);
        }
        
        public static STTextIndent parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (STTextIndent)getTypeLoader().parse(s, STTextIndent.type, xmlOptions);
        }
        
        public static STTextIndent parse(final File file) throws XmlException, IOException {
            return (STTextIndent)getTypeLoader().parse(file, STTextIndent.type, (XmlOptions)null);
        }
        
        public static STTextIndent parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STTextIndent)getTypeLoader().parse(file, STTextIndent.type, xmlOptions);
        }
        
        public static STTextIndent parse(final URL url) throws XmlException, IOException {
            return (STTextIndent)getTypeLoader().parse(url, STTextIndent.type, (XmlOptions)null);
        }
        
        public static STTextIndent parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STTextIndent)getTypeLoader().parse(url, STTextIndent.type, xmlOptions);
        }
        
        public static STTextIndent parse(final InputStream inputStream) throws XmlException, IOException {
            return (STTextIndent)getTypeLoader().parse(inputStream, STTextIndent.type, (XmlOptions)null);
        }
        
        public static STTextIndent parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STTextIndent)getTypeLoader().parse(inputStream, STTextIndent.type, xmlOptions);
        }
        
        public static STTextIndent parse(final Reader reader) throws XmlException, IOException {
            return (STTextIndent)getTypeLoader().parse(reader, STTextIndent.type, (XmlOptions)null);
        }
        
        public static STTextIndent parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STTextIndent)getTypeLoader().parse(reader, STTextIndent.type, xmlOptions);
        }
        
        public static STTextIndent parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (STTextIndent)getTypeLoader().parse(xmlStreamReader, STTextIndent.type, (XmlOptions)null);
        }
        
        public static STTextIndent parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (STTextIndent)getTypeLoader().parse(xmlStreamReader, STTextIndent.type, xmlOptions);
        }
        
        public static STTextIndent parse(final Node node) throws XmlException {
            return (STTextIndent)getTypeLoader().parse(node, STTextIndent.type, (XmlOptions)null);
        }
        
        public static STTextIndent parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (STTextIndent)getTypeLoader().parse(node, STTextIndent.type, xmlOptions);
        }
        
        @Deprecated
        public static STTextIndent parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (STTextIndent)getTypeLoader().parse(xmlInputStream, STTextIndent.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static STTextIndent parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (STTextIndent)getTypeLoader().parse(xmlInputStream, STTextIndent.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STTextIndent.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STTextIndent.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
