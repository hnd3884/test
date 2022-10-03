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

public interface STTextFontScalePercent extends STPercentage
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(STTextFontScalePercent.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("sttextfontscalepercente6c2type");
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        public static STTextFontScalePercent newValue(final Object o) {
            return (STTextFontScalePercent)STTextFontScalePercent.type.newValue(o);
        }
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(STTextFontScalePercent.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static STTextFontScalePercent newInstance() {
            return (STTextFontScalePercent)getTypeLoader().newInstance(STTextFontScalePercent.type, (XmlOptions)null);
        }
        
        public static STTextFontScalePercent newInstance(final XmlOptions xmlOptions) {
            return (STTextFontScalePercent)getTypeLoader().newInstance(STTextFontScalePercent.type, xmlOptions);
        }
        
        public static STTextFontScalePercent parse(final String s) throws XmlException {
            return (STTextFontScalePercent)getTypeLoader().parse(s, STTextFontScalePercent.type, (XmlOptions)null);
        }
        
        public static STTextFontScalePercent parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (STTextFontScalePercent)getTypeLoader().parse(s, STTextFontScalePercent.type, xmlOptions);
        }
        
        public static STTextFontScalePercent parse(final File file) throws XmlException, IOException {
            return (STTextFontScalePercent)getTypeLoader().parse(file, STTextFontScalePercent.type, (XmlOptions)null);
        }
        
        public static STTextFontScalePercent parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STTextFontScalePercent)getTypeLoader().parse(file, STTextFontScalePercent.type, xmlOptions);
        }
        
        public static STTextFontScalePercent parse(final URL url) throws XmlException, IOException {
            return (STTextFontScalePercent)getTypeLoader().parse(url, STTextFontScalePercent.type, (XmlOptions)null);
        }
        
        public static STTextFontScalePercent parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STTextFontScalePercent)getTypeLoader().parse(url, STTextFontScalePercent.type, xmlOptions);
        }
        
        public static STTextFontScalePercent parse(final InputStream inputStream) throws XmlException, IOException {
            return (STTextFontScalePercent)getTypeLoader().parse(inputStream, STTextFontScalePercent.type, (XmlOptions)null);
        }
        
        public static STTextFontScalePercent parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STTextFontScalePercent)getTypeLoader().parse(inputStream, STTextFontScalePercent.type, xmlOptions);
        }
        
        public static STTextFontScalePercent parse(final Reader reader) throws XmlException, IOException {
            return (STTextFontScalePercent)getTypeLoader().parse(reader, STTextFontScalePercent.type, (XmlOptions)null);
        }
        
        public static STTextFontScalePercent parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STTextFontScalePercent)getTypeLoader().parse(reader, STTextFontScalePercent.type, xmlOptions);
        }
        
        public static STTextFontScalePercent parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (STTextFontScalePercent)getTypeLoader().parse(xmlStreamReader, STTextFontScalePercent.type, (XmlOptions)null);
        }
        
        public static STTextFontScalePercent parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (STTextFontScalePercent)getTypeLoader().parse(xmlStreamReader, STTextFontScalePercent.type, xmlOptions);
        }
        
        public static STTextFontScalePercent parse(final Node node) throws XmlException {
            return (STTextFontScalePercent)getTypeLoader().parse(node, STTextFontScalePercent.type, (XmlOptions)null);
        }
        
        public static STTextFontScalePercent parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (STTextFontScalePercent)getTypeLoader().parse(node, STTextFontScalePercent.type, xmlOptions);
        }
        
        @Deprecated
        public static STTextFontScalePercent parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (STTextFontScalePercent)getTypeLoader().parse(xmlInputStream, STTextFontScalePercent.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static STTextFontScalePercent parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (STTextFontScalePercent)getTypeLoader().parse(xmlInputStream, STTextFontScalePercent.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STTextFontScalePercent.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STTextFontScalePercent.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
