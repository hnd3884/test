package org.openxmlformats.schemas.presentationml.x2006.main;

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
import org.openxmlformats.schemas.drawingml.x2006.main.STPositiveCoordinate32;

public interface STSlideSizeCoordinate extends STPositiveCoordinate32
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(STSlideSizeCoordinate.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("stslidesizecoordinate24b5type");
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        public static STSlideSizeCoordinate newValue(final Object o) {
            return (STSlideSizeCoordinate)STSlideSizeCoordinate.type.newValue(o);
        }
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(STSlideSizeCoordinate.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static STSlideSizeCoordinate newInstance() {
            return (STSlideSizeCoordinate)getTypeLoader().newInstance(STSlideSizeCoordinate.type, (XmlOptions)null);
        }
        
        public static STSlideSizeCoordinate newInstance(final XmlOptions xmlOptions) {
            return (STSlideSizeCoordinate)getTypeLoader().newInstance(STSlideSizeCoordinate.type, xmlOptions);
        }
        
        public static STSlideSizeCoordinate parse(final String s) throws XmlException {
            return (STSlideSizeCoordinate)getTypeLoader().parse(s, STSlideSizeCoordinate.type, (XmlOptions)null);
        }
        
        public static STSlideSizeCoordinate parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (STSlideSizeCoordinate)getTypeLoader().parse(s, STSlideSizeCoordinate.type, xmlOptions);
        }
        
        public static STSlideSizeCoordinate parse(final File file) throws XmlException, IOException {
            return (STSlideSizeCoordinate)getTypeLoader().parse(file, STSlideSizeCoordinate.type, (XmlOptions)null);
        }
        
        public static STSlideSizeCoordinate parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STSlideSizeCoordinate)getTypeLoader().parse(file, STSlideSizeCoordinate.type, xmlOptions);
        }
        
        public static STSlideSizeCoordinate parse(final URL url) throws XmlException, IOException {
            return (STSlideSizeCoordinate)getTypeLoader().parse(url, STSlideSizeCoordinate.type, (XmlOptions)null);
        }
        
        public static STSlideSizeCoordinate parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STSlideSizeCoordinate)getTypeLoader().parse(url, STSlideSizeCoordinate.type, xmlOptions);
        }
        
        public static STSlideSizeCoordinate parse(final InputStream inputStream) throws XmlException, IOException {
            return (STSlideSizeCoordinate)getTypeLoader().parse(inputStream, STSlideSizeCoordinate.type, (XmlOptions)null);
        }
        
        public static STSlideSizeCoordinate parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STSlideSizeCoordinate)getTypeLoader().parse(inputStream, STSlideSizeCoordinate.type, xmlOptions);
        }
        
        public static STSlideSizeCoordinate parse(final Reader reader) throws XmlException, IOException {
            return (STSlideSizeCoordinate)getTypeLoader().parse(reader, STSlideSizeCoordinate.type, (XmlOptions)null);
        }
        
        public static STSlideSizeCoordinate parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STSlideSizeCoordinate)getTypeLoader().parse(reader, STSlideSizeCoordinate.type, xmlOptions);
        }
        
        public static STSlideSizeCoordinate parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (STSlideSizeCoordinate)getTypeLoader().parse(xmlStreamReader, STSlideSizeCoordinate.type, (XmlOptions)null);
        }
        
        public static STSlideSizeCoordinate parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (STSlideSizeCoordinate)getTypeLoader().parse(xmlStreamReader, STSlideSizeCoordinate.type, xmlOptions);
        }
        
        public static STSlideSizeCoordinate parse(final Node node) throws XmlException {
            return (STSlideSizeCoordinate)getTypeLoader().parse(node, STSlideSizeCoordinate.type, (XmlOptions)null);
        }
        
        public static STSlideSizeCoordinate parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (STSlideSizeCoordinate)getTypeLoader().parse(node, STSlideSizeCoordinate.type, xmlOptions);
        }
        
        @Deprecated
        public static STSlideSizeCoordinate parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (STSlideSizeCoordinate)getTypeLoader().parse(xmlInputStream, STSlideSizeCoordinate.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static STSlideSizeCoordinate parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (STSlideSizeCoordinate)getTypeLoader().parse(xmlInputStream, STSlideSizeCoordinate.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STSlideSizeCoordinate.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STSlideSizeCoordinate.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
