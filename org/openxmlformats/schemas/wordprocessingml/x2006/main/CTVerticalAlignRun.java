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
import org.apache.xmlbeans.XmlObject;

public interface CTVerticalAlignRun extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTVerticalAlignRun.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctverticalalignruncb8ctype");
    
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
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTVerticalAlignRun.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTVerticalAlignRun newInstance() {
            return (CTVerticalAlignRun)getTypeLoader().newInstance(CTVerticalAlignRun.type, (XmlOptions)null);
        }
        
        public static CTVerticalAlignRun newInstance(final XmlOptions xmlOptions) {
            return (CTVerticalAlignRun)getTypeLoader().newInstance(CTVerticalAlignRun.type, xmlOptions);
        }
        
        public static CTVerticalAlignRun parse(final String s) throws XmlException {
            return (CTVerticalAlignRun)getTypeLoader().parse(s, CTVerticalAlignRun.type, (XmlOptions)null);
        }
        
        public static CTVerticalAlignRun parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTVerticalAlignRun)getTypeLoader().parse(s, CTVerticalAlignRun.type, xmlOptions);
        }
        
        public static CTVerticalAlignRun parse(final File file) throws XmlException, IOException {
            return (CTVerticalAlignRun)getTypeLoader().parse(file, CTVerticalAlignRun.type, (XmlOptions)null);
        }
        
        public static CTVerticalAlignRun parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTVerticalAlignRun)getTypeLoader().parse(file, CTVerticalAlignRun.type, xmlOptions);
        }
        
        public static CTVerticalAlignRun parse(final URL url) throws XmlException, IOException {
            return (CTVerticalAlignRun)getTypeLoader().parse(url, CTVerticalAlignRun.type, (XmlOptions)null);
        }
        
        public static CTVerticalAlignRun parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTVerticalAlignRun)getTypeLoader().parse(url, CTVerticalAlignRun.type, xmlOptions);
        }
        
        public static CTVerticalAlignRun parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTVerticalAlignRun)getTypeLoader().parse(inputStream, CTVerticalAlignRun.type, (XmlOptions)null);
        }
        
        public static CTVerticalAlignRun parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTVerticalAlignRun)getTypeLoader().parse(inputStream, CTVerticalAlignRun.type, xmlOptions);
        }
        
        public static CTVerticalAlignRun parse(final Reader reader) throws XmlException, IOException {
            return (CTVerticalAlignRun)getTypeLoader().parse(reader, CTVerticalAlignRun.type, (XmlOptions)null);
        }
        
        public static CTVerticalAlignRun parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTVerticalAlignRun)getTypeLoader().parse(reader, CTVerticalAlignRun.type, xmlOptions);
        }
        
        public static CTVerticalAlignRun parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTVerticalAlignRun)getTypeLoader().parse(xmlStreamReader, CTVerticalAlignRun.type, (XmlOptions)null);
        }
        
        public static CTVerticalAlignRun parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTVerticalAlignRun)getTypeLoader().parse(xmlStreamReader, CTVerticalAlignRun.type, xmlOptions);
        }
        
        public static CTVerticalAlignRun parse(final Node node) throws XmlException {
            return (CTVerticalAlignRun)getTypeLoader().parse(node, CTVerticalAlignRun.type, (XmlOptions)null);
        }
        
        public static CTVerticalAlignRun parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTVerticalAlignRun)getTypeLoader().parse(node, CTVerticalAlignRun.type, xmlOptions);
        }
        
        @Deprecated
        public static CTVerticalAlignRun parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTVerticalAlignRun)getTypeLoader().parse(xmlInputStream, CTVerticalAlignRun.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTVerticalAlignRun parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTVerticalAlignRun)getTypeLoader().parse(xmlInputStream, CTVerticalAlignRun.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTVerticalAlignRun.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTVerticalAlignRun.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
