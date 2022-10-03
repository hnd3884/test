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

public interface CTLegendPos extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTLegendPos.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctlegendpos053ftype");
    
    STLegendPos.Enum getVal();
    
    STLegendPos xgetVal();
    
    boolean isSetVal();
    
    void setVal(final STLegendPos.Enum p0);
    
    void xsetVal(final STLegendPos p0);
    
    void unsetVal();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTLegendPos.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTLegendPos newInstance() {
            return (CTLegendPos)getTypeLoader().newInstance(CTLegendPos.type, (XmlOptions)null);
        }
        
        public static CTLegendPos newInstance(final XmlOptions xmlOptions) {
            return (CTLegendPos)getTypeLoader().newInstance(CTLegendPos.type, xmlOptions);
        }
        
        public static CTLegendPos parse(final String s) throws XmlException {
            return (CTLegendPos)getTypeLoader().parse(s, CTLegendPos.type, (XmlOptions)null);
        }
        
        public static CTLegendPos parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTLegendPos)getTypeLoader().parse(s, CTLegendPos.type, xmlOptions);
        }
        
        public static CTLegendPos parse(final File file) throws XmlException, IOException {
            return (CTLegendPos)getTypeLoader().parse(file, CTLegendPos.type, (XmlOptions)null);
        }
        
        public static CTLegendPos parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTLegendPos)getTypeLoader().parse(file, CTLegendPos.type, xmlOptions);
        }
        
        public static CTLegendPos parse(final URL url) throws XmlException, IOException {
            return (CTLegendPos)getTypeLoader().parse(url, CTLegendPos.type, (XmlOptions)null);
        }
        
        public static CTLegendPos parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTLegendPos)getTypeLoader().parse(url, CTLegendPos.type, xmlOptions);
        }
        
        public static CTLegendPos parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTLegendPos)getTypeLoader().parse(inputStream, CTLegendPos.type, (XmlOptions)null);
        }
        
        public static CTLegendPos parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTLegendPos)getTypeLoader().parse(inputStream, CTLegendPos.type, xmlOptions);
        }
        
        public static CTLegendPos parse(final Reader reader) throws XmlException, IOException {
            return (CTLegendPos)getTypeLoader().parse(reader, CTLegendPos.type, (XmlOptions)null);
        }
        
        public static CTLegendPos parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTLegendPos)getTypeLoader().parse(reader, CTLegendPos.type, xmlOptions);
        }
        
        public static CTLegendPos parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTLegendPos)getTypeLoader().parse(xmlStreamReader, CTLegendPos.type, (XmlOptions)null);
        }
        
        public static CTLegendPos parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTLegendPos)getTypeLoader().parse(xmlStreamReader, CTLegendPos.type, xmlOptions);
        }
        
        public static CTLegendPos parse(final Node node) throws XmlException {
            return (CTLegendPos)getTypeLoader().parse(node, CTLegendPos.type, (XmlOptions)null);
        }
        
        public static CTLegendPos parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTLegendPos)getTypeLoader().parse(node, CTLegendPos.type, xmlOptions);
        }
        
        @Deprecated
        public static CTLegendPos parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTLegendPos)getTypeLoader().parse(xmlInputStream, CTLegendPos.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTLegendPos parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTLegendPos)getTypeLoader().parse(xmlInputStream, CTLegendPos.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTLegendPos.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTLegendPos.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
