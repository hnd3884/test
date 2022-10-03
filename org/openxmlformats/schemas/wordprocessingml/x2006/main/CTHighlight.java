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

public interface CTHighlight extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTHighlight.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("cthighlight071etype");
    
    STHighlightColor.Enum getVal();
    
    STHighlightColor xgetVal();
    
    void setVal(final STHighlightColor.Enum p0);
    
    void xsetVal(final STHighlightColor p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTHighlight.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTHighlight newInstance() {
            return (CTHighlight)getTypeLoader().newInstance(CTHighlight.type, (XmlOptions)null);
        }
        
        public static CTHighlight newInstance(final XmlOptions xmlOptions) {
            return (CTHighlight)getTypeLoader().newInstance(CTHighlight.type, xmlOptions);
        }
        
        public static CTHighlight parse(final String s) throws XmlException {
            return (CTHighlight)getTypeLoader().parse(s, CTHighlight.type, (XmlOptions)null);
        }
        
        public static CTHighlight parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTHighlight)getTypeLoader().parse(s, CTHighlight.type, xmlOptions);
        }
        
        public static CTHighlight parse(final File file) throws XmlException, IOException {
            return (CTHighlight)getTypeLoader().parse(file, CTHighlight.type, (XmlOptions)null);
        }
        
        public static CTHighlight parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTHighlight)getTypeLoader().parse(file, CTHighlight.type, xmlOptions);
        }
        
        public static CTHighlight parse(final URL url) throws XmlException, IOException {
            return (CTHighlight)getTypeLoader().parse(url, CTHighlight.type, (XmlOptions)null);
        }
        
        public static CTHighlight parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTHighlight)getTypeLoader().parse(url, CTHighlight.type, xmlOptions);
        }
        
        public static CTHighlight parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTHighlight)getTypeLoader().parse(inputStream, CTHighlight.type, (XmlOptions)null);
        }
        
        public static CTHighlight parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTHighlight)getTypeLoader().parse(inputStream, CTHighlight.type, xmlOptions);
        }
        
        public static CTHighlight parse(final Reader reader) throws XmlException, IOException {
            return (CTHighlight)getTypeLoader().parse(reader, CTHighlight.type, (XmlOptions)null);
        }
        
        public static CTHighlight parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTHighlight)getTypeLoader().parse(reader, CTHighlight.type, xmlOptions);
        }
        
        public static CTHighlight parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTHighlight)getTypeLoader().parse(xmlStreamReader, CTHighlight.type, (XmlOptions)null);
        }
        
        public static CTHighlight parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTHighlight)getTypeLoader().parse(xmlStreamReader, CTHighlight.type, xmlOptions);
        }
        
        public static CTHighlight parse(final Node node) throws XmlException {
            return (CTHighlight)getTypeLoader().parse(node, CTHighlight.type, (XmlOptions)null);
        }
        
        public static CTHighlight parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTHighlight)getTypeLoader().parse(node, CTHighlight.type, xmlOptions);
        }
        
        @Deprecated
        public static CTHighlight parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTHighlight)getTypeLoader().parse(xmlInputStream, CTHighlight.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTHighlight parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTHighlight)getTypeLoader().parse(xmlInputStream, CTHighlight.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTHighlight.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTHighlight.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
