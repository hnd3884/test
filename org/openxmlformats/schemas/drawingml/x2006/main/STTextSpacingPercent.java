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

public interface STTextSpacingPercent extends STPercentage
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(STTextSpacingPercent.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("sttextspacingpercentde3atype");
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        public static STTextSpacingPercent newValue(final Object o) {
            return (STTextSpacingPercent)STTextSpacingPercent.type.newValue(o);
        }
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(STTextSpacingPercent.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static STTextSpacingPercent newInstance() {
            return (STTextSpacingPercent)getTypeLoader().newInstance(STTextSpacingPercent.type, (XmlOptions)null);
        }
        
        public static STTextSpacingPercent newInstance(final XmlOptions xmlOptions) {
            return (STTextSpacingPercent)getTypeLoader().newInstance(STTextSpacingPercent.type, xmlOptions);
        }
        
        public static STTextSpacingPercent parse(final String s) throws XmlException {
            return (STTextSpacingPercent)getTypeLoader().parse(s, STTextSpacingPercent.type, (XmlOptions)null);
        }
        
        public static STTextSpacingPercent parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (STTextSpacingPercent)getTypeLoader().parse(s, STTextSpacingPercent.type, xmlOptions);
        }
        
        public static STTextSpacingPercent parse(final File file) throws XmlException, IOException {
            return (STTextSpacingPercent)getTypeLoader().parse(file, STTextSpacingPercent.type, (XmlOptions)null);
        }
        
        public static STTextSpacingPercent parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STTextSpacingPercent)getTypeLoader().parse(file, STTextSpacingPercent.type, xmlOptions);
        }
        
        public static STTextSpacingPercent parse(final URL url) throws XmlException, IOException {
            return (STTextSpacingPercent)getTypeLoader().parse(url, STTextSpacingPercent.type, (XmlOptions)null);
        }
        
        public static STTextSpacingPercent parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STTextSpacingPercent)getTypeLoader().parse(url, STTextSpacingPercent.type, xmlOptions);
        }
        
        public static STTextSpacingPercent parse(final InputStream inputStream) throws XmlException, IOException {
            return (STTextSpacingPercent)getTypeLoader().parse(inputStream, STTextSpacingPercent.type, (XmlOptions)null);
        }
        
        public static STTextSpacingPercent parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STTextSpacingPercent)getTypeLoader().parse(inputStream, STTextSpacingPercent.type, xmlOptions);
        }
        
        public static STTextSpacingPercent parse(final Reader reader) throws XmlException, IOException {
            return (STTextSpacingPercent)getTypeLoader().parse(reader, STTextSpacingPercent.type, (XmlOptions)null);
        }
        
        public static STTextSpacingPercent parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STTextSpacingPercent)getTypeLoader().parse(reader, STTextSpacingPercent.type, xmlOptions);
        }
        
        public static STTextSpacingPercent parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (STTextSpacingPercent)getTypeLoader().parse(xmlStreamReader, STTextSpacingPercent.type, (XmlOptions)null);
        }
        
        public static STTextSpacingPercent parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (STTextSpacingPercent)getTypeLoader().parse(xmlStreamReader, STTextSpacingPercent.type, xmlOptions);
        }
        
        public static STTextSpacingPercent parse(final Node node) throws XmlException {
            return (STTextSpacingPercent)getTypeLoader().parse(node, STTextSpacingPercent.type, (XmlOptions)null);
        }
        
        public static STTextSpacingPercent parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (STTextSpacingPercent)getTypeLoader().parse(node, STTextSpacingPercent.type, xmlOptions);
        }
        
        @Deprecated
        public static STTextSpacingPercent parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (STTextSpacingPercent)getTypeLoader().parse(xmlInputStream, STTextSpacingPercent.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static STTextSpacingPercent parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (STTextSpacingPercent)getTypeLoader().parse(xmlInputStream, STTextSpacingPercent.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STTextSpacingPercent.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STTextSpacingPercent.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
