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

public interface STEighthPointMeasure extends STUnsignedDecimalNumber
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(STEighthPointMeasure.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("steighthpointmeasure3371type");
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        public static STEighthPointMeasure newValue(final Object o) {
            return (STEighthPointMeasure)STEighthPointMeasure.type.newValue(o);
        }
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(STEighthPointMeasure.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static STEighthPointMeasure newInstance() {
            return (STEighthPointMeasure)getTypeLoader().newInstance(STEighthPointMeasure.type, (XmlOptions)null);
        }
        
        public static STEighthPointMeasure newInstance(final XmlOptions xmlOptions) {
            return (STEighthPointMeasure)getTypeLoader().newInstance(STEighthPointMeasure.type, xmlOptions);
        }
        
        public static STEighthPointMeasure parse(final String s) throws XmlException {
            return (STEighthPointMeasure)getTypeLoader().parse(s, STEighthPointMeasure.type, (XmlOptions)null);
        }
        
        public static STEighthPointMeasure parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (STEighthPointMeasure)getTypeLoader().parse(s, STEighthPointMeasure.type, xmlOptions);
        }
        
        public static STEighthPointMeasure parse(final File file) throws XmlException, IOException {
            return (STEighthPointMeasure)getTypeLoader().parse(file, STEighthPointMeasure.type, (XmlOptions)null);
        }
        
        public static STEighthPointMeasure parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STEighthPointMeasure)getTypeLoader().parse(file, STEighthPointMeasure.type, xmlOptions);
        }
        
        public static STEighthPointMeasure parse(final URL url) throws XmlException, IOException {
            return (STEighthPointMeasure)getTypeLoader().parse(url, STEighthPointMeasure.type, (XmlOptions)null);
        }
        
        public static STEighthPointMeasure parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STEighthPointMeasure)getTypeLoader().parse(url, STEighthPointMeasure.type, xmlOptions);
        }
        
        public static STEighthPointMeasure parse(final InputStream inputStream) throws XmlException, IOException {
            return (STEighthPointMeasure)getTypeLoader().parse(inputStream, STEighthPointMeasure.type, (XmlOptions)null);
        }
        
        public static STEighthPointMeasure parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STEighthPointMeasure)getTypeLoader().parse(inputStream, STEighthPointMeasure.type, xmlOptions);
        }
        
        public static STEighthPointMeasure parse(final Reader reader) throws XmlException, IOException {
            return (STEighthPointMeasure)getTypeLoader().parse(reader, STEighthPointMeasure.type, (XmlOptions)null);
        }
        
        public static STEighthPointMeasure parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STEighthPointMeasure)getTypeLoader().parse(reader, STEighthPointMeasure.type, xmlOptions);
        }
        
        public static STEighthPointMeasure parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (STEighthPointMeasure)getTypeLoader().parse(xmlStreamReader, STEighthPointMeasure.type, (XmlOptions)null);
        }
        
        public static STEighthPointMeasure parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (STEighthPointMeasure)getTypeLoader().parse(xmlStreamReader, STEighthPointMeasure.type, xmlOptions);
        }
        
        public static STEighthPointMeasure parse(final Node node) throws XmlException {
            return (STEighthPointMeasure)getTypeLoader().parse(node, STEighthPointMeasure.type, (XmlOptions)null);
        }
        
        public static STEighthPointMeasure parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (STEighthPointMeasure)getTypeLoader().parse(node, STEighthPointMeasure.type, xmlOptions);
        }
        
        @Deprecated
        public static STEighthPointMeasure parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (STEighthPointMeasure)getTypeLoader().parse(xmlInputStream, STEighthPointMeasure.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static STEighthPointMeasure parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (STEighthPointMeasure)getTypeLoader().parse(xmlInputStream, STEighthPointMeasure.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STEighthPointMeasure.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STEighthPointMeasure.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
