package org.openxmlformats.schemas.drawingml.x2006.chart;

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

public interface CTDispBlanksAs extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTDispBlanksAs.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctdispblanksas3069type");
    
    STDispBlanksAs.Enum getVal();
    
    STDispBlanksAs xgetVal();
    
    boolean isSetVal();
    
    void setVal(final STDispBlanksAs.Enum p0);
    
    void xsetVal(final STDispBlanksAs p0);
    
    void unsetVal();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTDispBlanksAs.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTDispBlanksAs newInstance() {
            return (CTDispBlanksAs)getTypeLoader().newInstance(CTDispBlanksAs.type, (XmlOptions)null);
        }
        
        public static CTDispBlanksAs newInstance(final XmlOptions xmlOptions) {
            return (CTDispBlanksAs)getTypeLoader().newInstance(CTDispBlanksAs.type, xmlOptions);
        }
        
        public static CTDispBlanksAs parse(final String s) throws XmlException {
            return (CTDispBlanksAs)getTypeLoader().parse(s, CTDispBlanksAs.type, (XmlOptions)null);
        }
        
        public static CTDispBlanksAs parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTDispBlanksAs)getTypeLoader().parse(s, CTDispBlanksAs.type, xmlOptions);
        }
        
        public static CTDispBlanksAs parse(final File file) throws XmlException, IOException {
            return (CTDispBlanksAs)getTypeLoader().parse(file, CTDispBlanksAs.type, (XmlOptions)null);
        }
        
        public static CTDispBlanksAs parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTDispBlanksAs)getTypeLoader().parse(file, CTDispBlanksAs.type, xmlOptions);
        }
        
        public static CTDispBlanksAs parse(final URL url) throws XmlException, IOException {
            return (CTDispBlanksAs)getTypeLoader().parse(url, CTDispBlanksAs.type, (XmlOptions)null);
        }
        
        public static CTDispBlanksAs parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTDispBlanksAs)getTypeLoader().parse(url, CTDispBlanksAs.type, xmlOptions);
        }
        
        public static CTDispBlanksAs parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTDispBlanksAs)getTypeLoader().parse(inputStream, CTDispBlanksAs.type, (XmlOptions)null);
        }
        
        public static CTDispBlanksAs parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTDispBlanksAs)getTypeLoader().parse(inputStream, CTDispBlanksAs.type, xmlOptions);
        }
        
        public static CTDispBlanksAs parse(final Reader reader) throws XmlException, IOException {
            return (CTDispBlanksAs)getTypeLoader().parse(reader, CTDispBlanksAs.type, (XmlOptions)null);
        }
        
        public static CTDispBlanksAs parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTDispBlanksAs)getTypeLoader().parse(reader, CTDispBlanksAs.type, xmlOptions);
        }
        
        public static CTDispBlanksAs parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTDispBlanksAs)getTypeLoader().parse(xmlStreamReader, CTDispBlanksAs.type, (XmlOptions)null);
        }
        
        public static CTDispBlanksAs parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTDispBlanksAs)getTypeLoader().parse(xmlStreamReader, CTDispBlanksAs.type, xmlOptions);
        }
        
        public static CTDispBlanksAs parse(final Node node) throws XmlException {
            return (CTDispBlanksAs)getTypeLoader().parse(node, CTDispBlanksAs.type, (XmlOptions)null);
        }
        
        public static CTDispBlanksAs parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTDispBlanksAs)getTypeLoader().parse(node, CTDispBlanksAs.type, xmlOptions);
        }
        
        @Deprecated
        public static CTDispBlanksAs parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTDispBlanksAs)getTypeLoader().parse(xmlInputStream, CTDispBlanksAs.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTDispBlanksAs parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTDispBlanksAs)getTypeLoader().parse(xmlInputStream, CTDispBlanksAs.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTDispBlanksAs.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTDispBlanksAs.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
