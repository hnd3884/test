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
import org.apache.xmlbeans.XmlInt;

public interface STTextSpacingPoint extends XmlInt
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(STTextSpacingPoint.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("sttextspacingpointdd05type");
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        public static STTextSpacingPoint newValue(final Object o) {
            return (STTextSpacingPoint)STTextSpacingPoint.type.newValue(o);
        }
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(STTextSpacingPoint.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static STTextSpacingPoint newInstance() {
            return (STTextSpacingPoint)getTypeLoader().newInstance(STTextSpacingPoint.type, (XmlOptions)null);
        }
        
        public static STTextSpacingPoint newInstance(final XmlOptions xmlOptions) {
            return (STTextSpacingPoint)getTypeLoader().newInstance(STTextSpacingPoint.type, xmlOptions);
        }
        
        public static STTextSpacingPoint parse(final String s) throws XmlException {
            return (STTextSpacingPoint)getTypeLoader().parse(s, STTextSpacingPoint.type, (XmlOptions)null);
        }
        
        public static STTextSpacingPoint parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (STTextSpacingPoint)getTypeLoader().parse(s, STTextSpacingPoint.type, xmlOptions);
        }
        
        public static STTextSpacingPoint parse(final File file) throws XmlException, IOException {
            return (STTextSpacingPoint)getTypeLoader().parse(file, STTextSpacingPoint.type, (XmlOptions)null);
        }
        
        public static STTextSpacingPoint parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STTextSpacingPoint)getTypeLoader().parse(file, STTextSpacingPoint.type, xmlOptions);
        }
        
        public static STTextSpacingPoint parse(final URL url) throws XmlException, IOException {
            return (STTextSpacingPoint)getTypeLoader().parse(url, STTextSpacingPoint.type, (XmlOptions)null);
        }
        
        public static STTextSpacingPoint parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STTextSpacingPoint)getTypeLoader().parse(url, STTextSpacingPoint.type, xmlOptions);
        }
        
        public static STTextSpacingPoint parse(final InputStream inputStream) throws XmlException, IOException {
            return (STTextSpacingPoint)getTypeLoader().parse(inputStream, STTextSpacingPoint.type, (XmlOptions)null);
        }
        
        public static STTextSpacingPoint parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STTextSpacingPoint)getTypeLoader().parse(inputStream, STTextSpacingPoint.type, xmlOptions);
        }
        
        public static STTextSpacingPoint parse(final Reader reader) throws XmlException, IOException {
            return (STTextSpacingPoint)getTypeLoader().parse(reader, STTextSpacingPoint.type, (XmlOptions)null);
        }
        
        public static STTextSpacingPoint parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STTextSpacingPoint)getTypeLoader().parse(reader, STTextSpacingPoint.type, xmlOptions);
        }
        
        public static STTextSpacingPoint parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (STTextSpacingPoint)getTypeLoader().parse(xmlStreamReader, STTextSpacingPoint.type, (XmlOptions)null);
        }
        
        public static STTextSpacingPoint parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (STTextSpacingPoint)getTypeLoader().parse(xmlStreamReader, STTextSpacingPoint.type, xmlOptions);
        }
        
        public static STTextSpacingPoint parse(final Node node) throws XmlException {
            return (STTextSpacingPoint)getTypeLoader().parse(node, STTextSpacingPoint.type, (XmlOptions)null);
        }
        
        public static STTextSpacingPoint parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (STTextSpacingPoint)getTypeLoader().parse(node, STTextSpacingPoint.type, xmlOptions);
        }
        
        @Deprecated
        public static STTextSpacingPoint parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (STTextSpacingPoint)getTypeLoader().parse(xmlInputStream, STTextSpacingPoint.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static STTextSpacingPoint parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (STTextSpacingPoint)getTypeLoader().parse(xmlInputStream, STTextSpacingPoint.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STTextSpacingPoint.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STTextSpacingPoint.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
