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

public interface CTAxPos extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTAxPos.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctaxposff69type");
    
    STAxPos.Enum getVal();
    
    STAxPos xgetVal();
    
    void setVal(final STAxPos.Enum p0);
    
    void xsetVal(final STAxPos p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTAxPos.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTAxPos newInstance() {
            return (CTAxPos)getTypeLoader().newInstance(CTAxPos.type, (XmlOptions)null);
        }
        
        public static CTAxPos newInstance(final XmlOptions xmlOptions) {
            return (CTAxPos)getTypeLoader().newInstance(CTAxPos.type, xmlOptions);
        }
        
        public static CTAxPos parse(final String s) throws XmlException {
            return (CTAxPos)getTypeLoader().parse(s, CTAxPos.type, (XmlOptions)null);
        }
        
        public static CTAxPos parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTAxPos)getTypeLoader().parse(s, CTAxPos.type, xmlOptions);
        }
        
        public static CTAxPos parse(final File file) throws XmlException, IOException {
            return (CTAxPos)getTypeLoader().parse(file, CTAxPos.type, (XmlOptions)null);
        }
        
        public static CTAxPos parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTAxPos)getTypeLoader().parse(file, CTAxPos.type, xmlOptions);
        }
        
        public static CTAxPos parse(final URL url) throws XmlException, IOException {
            return (CTAxPos)getTypeLoader().parse(url, CTAxPos.type, (XmlOptions)null);
        }
        
        public static CTAxPos parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTAxPos)getTypeLoader().parse(url, CTAxPos.type, xmlOptions);
        }
        
        public static CTAxPos parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTAxPos)getTypeLoader().parse(inputStream, CTAxPos.type, (XmlOptions)null);
        }
        
        public static CTAxPos parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTAxPos)getTypeLoader().parse(inputStream, CTAxPos.type, xmlOptions);
        }
        
        public static CTAxPos parse(final Reader reader) throws XmlException, IOException {
            return (CTAxPos)getTypeLoader().parse(reader, CTAxPos.type, (XmlOptions)null);
        }
        
        public static CTAxPos parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTAxPos)getTypeLoader().parse(reader, CTAxPos.type, xmlOptions);
        }
        
        public static CTAxPos parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTAxPos)getTypeLoader().parse(xmlStreamReader, CTAxPos.type, (XmlOptions)null);
        }
        
        public static CTAxPos parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTAxPos)getTypeLoader().parse(xmlStreamReader, CTAxPos.type, xmlOptions);
        }
        
        public static CTAxPos parse(final Node node) throws XmlException {
            return (CTAxPos)getTypeLoader().parse(node, CTAxPos.type, (XmlOptions)null);
        }
        
        public static CTAxPos parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTAxPos)getTypeLoader().parse(node, CTAxPos.type, xmlOptions);
        }
        
        @Deprecated
        public static CTAxPos parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTAxPos)getTypeLoader().parse(xmlInputStream, CTAxPos.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTAxPos parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTAxPos)getTypeLoader().parse(xmlInputStream, CTAxPos.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTAxPos.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTAxPos.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
