package org.openxmlformats.schemas.spreadsheetml.x2006.main;

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

public interface CTVerticalAlignFontProperty extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTVerticalAlignFontProperty.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctverticalalignfontproperty89f2type");
    
    STVerticalAlignRun.Enum getVal();
    
    STVerticalAlignRun xgetVal();
    
    void setVal(final STVerticalAlignRun.Enum p0);
    
    void xsetVal(final STVerticalAlignRun p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTVerticalAlignFontProperty.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTVerticalAlignFontProperty newInstance() {
            return (CTVerticalAlignFontProperty)getTypeLoader().newInstance(CTVerticalAlignFontProperty.type, (XmlOptions)null);
        }
        
        public static CTVerticalAlignFontProperty newInstance(final XmlOptions xmlOptions) {
            return (CTVerticalAlignFontProperty)getTypeLoader().newInstance(CTVerticalAlignFontProperty.type, xmlOptions);
        }
        
        public static CTVerticalAlignFontProperty parse(final String s) throws XmlException {
            return (CTVerticalAlignFontProperty)getTypeLoader().parse(s, CTVerticalAlignFontProperty.type, (XmlOptions)null);
        }
        
        public static CTVerticalAlignFontProperty parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTVerticalAlignFontProperty)getTypeLoader().parse(s, CTVerticalAlignFontProperty.type, xmlOptions);
        }
        
        public static CTVerticalAlignFontProperty parse(final File file) throws XmlException, IOException {
            return (CTVerticalAlignFontProperty)getTypeLoader().parse(file, CTVerticalAlignFontProperty.type, (XmlOptions)null);
        }
        
        public static CTVerticalAlignFontProperty parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTVerticalAlignFontProperty)getTypeLoader().parse(file, CTVerticalAlignFontProperty.type, xmlOptions);
        }
        
        public static CTVerticalAlignFontProperty parse(final URL url) throws XmlException, IOException {
            return (CTVerticalAlignFontProperty)getTypeLoader().parse(url, CTVerticalAlignFontProperty.type, (XmlOptions)null);
        }
        
        public static CTVerticalAlignFontProperty parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTVerticalAlignFontProperty)getTypeLoader().parse(url, CTVerticalAlignFontProperty.type, xmlOptions);
        }
        
        public static CTVerticalAlignFontProperty parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTVerticalAlignFontProperty)getTypeLoader().parse(inputStream, CTVerticalAlignFontProperty.type, (XmlOptions)null);
        }
        
        public static CTVerticalAlignFontProperty parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTVerticalAlignFontProperty)getTypeLoader().parse(inputStream, CTVerticalAlignFontProperty.type, xmlOptions);
        }
        
        public static CTVerticalAlignFontProperty parse(final Reader reader) throws XmlException, IOException {
            return (CTVerticalAlignFontProperty)getTypeLoader().parse(reader, CTVerticalAlignFontProperty.type, (XmlOptions)null);
        }
        
        public static CTVerticalAlignFontProperty parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTVerticalAlignFontProperty)getTypeLoader().parse(reader, CTVerticalAlignFontProperty.type, xmlOptions);
        }
        
        public static CTVerticalAlignFontProperty parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTVerticalAlignFontProperty)getTypeLoader().parse(xmlStreamReader, CTVerticalAlignFontProperty.type, (XmlOptions)null);
        }
        
        public static CTVerticalAlignFontProperty parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTVerticalAlignFontProperty)getTypeLoader().parse(xmlStreamReader, CTVerticalAlignFontProperty.type, xmlOptions);
        }
        
        public static CTVerticalAlignFontProperty parse(final Node node) throws XmlException {
            return (CTVerticalAlignFontProperty)getTypeLoader().parse(node, CTVerticalAlignFontProperty.type, (XmlOptions)null);
        }
        
        public static CTVerticalAlignFontProperty parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTVerticalAlignFontProperty)getTypeLoader().parse(node, CTVerticalAlignFontProperty.type, xmlOptions);
        }
        
        @Deprecated
        public static CTVerticalAlignFontProperty parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTVerticalAlignFontProperty)getTypeLoader().parse(xmlInputStream, CTVerticalAlignFontProperty.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTVerticalAlignFontProperty parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTVerticalAlignFontProperty)getTypeLoader().parse(xmlInputStream, CTVerticalAlignFontProperty.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTVerticalAlignFontProperty.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTVerticalAlignFontProperty.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
