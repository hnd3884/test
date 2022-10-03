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

public interface STPointMeasure extends STUnsignedDecimalNumber
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(STPointMeasure.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("stpointmeasurea96atype");
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        public static STPointMeasure newValue(final Object o) {
            return (STPointMeasure)STPointMeasure.type.newValue(o);
        }
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(STPointMeasure.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static STPointMeasure newInstance() {
            return (STPointMeasure)getTypeLoader().newInstance(STPointMeasure.type, (XmlOptions)null);
        }
        
        public static STPointMeasure newInstance(final XmlOptions xmlOptions) {
            return (STPointMeasure)getTypeLoader().newInstance(STPointMeasure.type, xmlOptions);
        }
        
        public static STPointMeasure parse(final String s) throws XmlException {
            return (STPointMeasure)getTypeLoader().parse(s, STPointMeasure.type, (XmlOptions)null);
        }
        
        public static STPointMeasure parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (STPointMeasure)getTypeLoader().parse(s, STPointMeasure.type, xmlOptions);
        }
        
        public static STPointMeasure parse(final File file) throws XmlException, IOException {
            return (STPointMeasure)getTypeLoader().parse(file, STPointMeasure.type, (XmlOptions)null);
        }
        
        public static STPointMeasure parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STPointMeasure)getTypeLoader().parse(file, STPointMeasure.type, xmlOptions);
        }
        
        public static STPointMeasure parse(final URL url) throws XmlException, IOException {
            return (STPointMeasure)getTypeLoader().parse(url, STPointMeasure.type, (XmlOptions)null);
        }
        
        public static STPointMeasure parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STPointMeasure)getTypeLoader().parse(url, STPointMeasure.type, xmlOptions);
        }
        
        public static STPointMeasure parse(final InputStream inputStream) throws XmlException, IOException {
            return (STPointMeasure)getTypeLoader().parse(inputStream, STPointMeasure.type, (XmlOptions)null);
        }
        
        public static STPointMeasure parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STPointMeasure)getTypeLoader().parse(inputStream, STPointMeasure.type, xmlOptions);
        }
        
        public static STPointMeasure parse(final Reader reader) throws XmlException, IOException {
            return (STPointMeasure)getTypeLoader().parse(reader, STPointMeasure.type, (XmlOptions)null);
        }
        
        public static STPointMeasure parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STPointMeasure)getTypeLoader().parse(reader, STPointMeasure.type, xmlOptions);
        }
        
        public static STPointMeasure parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (STPointMeasure)getTypeLoader().parse(xmlStreamReader, STPointMeasure.type, (XmlOptions)null);
        }
        
        public static STPointMeasure parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (STPointMeasure)getTypeLoader().parse(xmlStreamReader, STPointMeasure.type, xmlOptions);
        }
        
        public static STPointMeasure parse(final Node node) throws XmlException {
            return (STPointMeasure)getTypeLoader().parse(node, STPointMeasure.type, (XmlOptions)null);
        }
        
        public static STPointMeasure parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (STPointMeasure)getTypeLoader().parse(node, STPointMeasure.type, xmlOptions);
        }
        
        @Deprecated
        public static STPointMeasure parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (STPointMeasure)getTypeLoader().parse(xmlInputStream, STPointMeasure.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static STPointMeasure parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (STPointMeasure)getTypeLoader().parse(xmlInputStream, STPointMeasure.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STPointMeasure.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STPointMeasure.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
