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
import org.apache.xmlbeans.XmlInteger;

public interface STSignedHpsMeasure extends XmlInteger
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(STSignedHpsMeasure.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("stsignedhpsmeasure8e89type");
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        public static STSignedHpsMeasure newValue(final Object o) {
            return (STSignedHpsMeasure)STSignedHpsMeasure.type.newValue(o);
        }
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(STSignedHpsMeasure.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static STSignedHpsMeasure newInstance() {
            return (STSignedHpsMeasure)getTypeLoader().newInstance(STSignedHpsMeasure.type, (XmlOptions)null);
        }
        
        public static STSignedHpsMeasure newInstance(final XmlOptions xmlOptions) {
            return (STSignedHpsMeasure)getTypeLoader().newInstance(STSignedHpsMeasure.type, xmlOptions);
        }
        
        public static STSignedHpsMeasure parse(final String s) throws XmlException {
            return (STSignedHpsMeasure)getTypeLoader().parse(s, STSignedHpsMeasure.type, (XmlOptions)null);
        }
        
        public static STSignedHpsMeasure parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (STSignedHpsMeasure)getTypeLoader().parse(s, STSignedHpsMeasure.type, xmlOptions);
        }
        
        public static STSignedHpsMeasure parse(final File file) throws XmlException, IOException {
            return (STSignedHpsMeasure)getTypeLoader().parse(file, STSignedHpsMeasure.type, (XmlOptions)null);
        }
        
        public static STSignedHpsMeasure parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STSignedHpsMeasure)getTypeLoader().parse(file, STSignedHpsMeasure.type, xmlOptions);
        }
        
        public static STSignedHpsMeasure parse(final URL url) throws XmlException, IOException {
            return (STSignedHpsMeasure)getTypeLoader().parse(url, STSignedHpsMeasure.type, (XmlOptions)null);
        }
        
        public static STSignedHpsMeasure parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STSignedHpsMeasure)getTypeLoader().parse(url, STSignedHpsMeasure.type, xmlOptions);
        }
        
        public static STSignedHpsMeasure parse(final InputStream inputStream) throws XmlException, IOException {
            return (STSignedHpsMeasure)getTypeLoader().parse(inputStream, STSignedHpsMeasure.type, (XmlOptions)null);
        }
        
        public static STSignedHpsMeasure parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STSignedHpsMeasure)getTypeLoader().parse(inputStream, STSignedHpsMeasure.type, xmlOptions);
        }
        
        public static STSignedHpsMeasure parse(final Reader reader) throws XmlException, IOException {
            return (STSignedHpsMeasure)getTypeLoader().parse(reader, STSignedHpsMeasure.type, (XmlOptions)null);
        }
        
        public static STSignedHpsMeasure parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STSignedHpsMeasure)getTypeLoader().parse(reader, STSignedHpsMeasure.type, xmlOptions);
        }
        
        public static STSignedHpsMeasure parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (STSignedHpsMeasure)getTypeLoader().parse(xmlStreamReader, STSignedHpsMeasure.type, (XmlOptions)null);
        }
        
        public static STSignedHpsMeasure parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (STSignedHpsMeasure)getTypeLoader().parse(xmlStreamReader, STSignedHpsMeasure.type, xmlOptions);
        }
        
        public static STSignedHpsMeasure parse(final Node node) throws XmlException {
            return (STSignedHpsMeasure)getTypeLoader().parse(node, STSignedHpsMeasure.type, (XmlOptions)null);
        }
        
        public static STSignedHpsMeasure parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (STSignedHpsMeasure)getTypeLoader().parse(node, STSignedHpsMeasure.type, xmlOptions);
        }
        
        @Deprecated
        public static STSignedHpsMeasure parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (STSignedHpsMeasure)getTypeLoader().parse(xmlInputStream, STSignedHpsMeasure.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static STSignedHpsMeasure parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (STSignedHpsMeasure)getTypeLoader().parse(xmlInputStream, STSignedHpsMeasure.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STSignedHpsMeasure.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STSignedHpsMeasure.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
