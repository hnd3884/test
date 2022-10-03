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

public interface CTTickLblPos extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTTickLblPos.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctticklblposff61type");
    
    STTickLblPos.Enum getVal();
    
    STTickLblPos xgetVal();
    
    boolean isSetVal();
    
    void setVal(final STTickLblPos.Enum p0);
    
    void xsetVal(final STTickLblPos p0);
    
    void unsetVal();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTTickLblPos.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTTickLblPos newInstance() {
            return (CTTickLblPos)getTypeLoader().newInstance(CTTickLblPos.type, (XmlOptions)null);
        }
        
        public static CTTickLblPos newInstance(final XmlOptions xmlOptions) {
            return (CTTickLblPos)getTypeLoader().newInstance(CTTickLblPos.type, xmlOptions);
        }
        
        public static CTTickLblPos parse(final String s) throws XmlException {
            return (CTTickLblPos)getTypeLoader().parse(s, CTTickLblPos.type, (XmlOptions)null);
        }
        
        public static CTTickLblPos parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTTickLblPos)getTypeLoader().parse(s, CTTickLblPos.type, xmlOptions);
        }
        
        public static CTTickLblPos parse(final File file) throws XmlException, IOException {
            return (CTTickLblPos)getTypeLoader().parse(file, CTTickLblPos.type, (XmlOptions)null);
        }
        
        public static CTTickLblPos parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTickLblPos)getTypeLoader().parse(file, CTTickLblPos.type, xmlOptions);
        }
        
        public static CTTickLblPos parse(final URL url) throws XmlException, IOException {
            return (CTTickLblPos)getTypeLoader().parse(url, CTTickLblPos.type, (XmlOptions)null);
        }
        
        public static CTTickLblPos parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTickLblPos)getTypeLoader().parse(url, CTTickLblPos.type, xmlOptions);
        }
        
        public static CTTickLblPos parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTTickLblPos)getTypeLoader().parse(inputStream, CTTickLblPos.type, (XmlOptions)null);
        }
        
        public static CTTickLblPos parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTickLblPos)getTypeLoader().parse(inputStream, CTTickLblPos.type, xmlOptions);
        }
        
        public static CTTickLblPos parse(final Reader reader) throws XmlException, IOException {
            return (CTTickLblPos)getTypeLoader().parse(reader, CTTickLblPos.type, (XmlOptions)null);
        }
        
        public static CTTickLblPos parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTickLblPos)getTypeLoader().parse(reader, CTTickLblPos.type, xmlOptions);
        }
        
        public static CTTickLblPos parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTTickLblPos)getTypeLoader().parse(xmlStreamReader, CTTickLblPos.type, (XmlOptions)null);
        }
        
        public static CTTickLblPos parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTTickLblPos)getTypeLoader().parse(xmlStreamReader, CTTickLblPos.type, xmlOptions);
        }
        
        public static CTTickLblPos parse(final Node node) throws XmlException {
            return (CTTickLblPos)getTypeLoader().parse(node, CTTickLblPos.type, (XmlOptions)null);
        }
        
        public static CTTickLblPos parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTTickLblPos)getTypeLoader().parse(node, CTTickLblPos.type, xmlOptions);
        }
        
        @Deprecated
        public static CTTickLblPos parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTTickLblPos)getTypeLoader().parse(xmlInputStream, CTTickLblPos.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTTickLblPos parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTTickLblPos)getTypeLoader().parse(xmlInputStream, CTTickLblPos.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTTickLblPos.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTTickLblPos.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
