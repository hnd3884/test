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

public interface CTMarkerStyle extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTMarkerStyle.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctmarkerstyle1f6ftype");
    
    STMarkerStyle.Enum getVal();
    
    STMarkerStyle xgetVal();
    
    void setVal(final STMarkerStyle.Enum p0);
    
    void xsetVal(final STMarkerStyle p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTMarkerStyle.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTMarkerStyle newInstance() {
            return (CTMarkerStyle)getTypeLoader().newInstance(CTMarkerStyle.type, (XmlOptions)null);
        }
        
        public static CTMarkerStyle newInstance(final XmlOptions xmlOptions) {
            return (CTMarkerStyle)getTypeLoader().newInstance(CTMarkerStyle.type, xmlOptions);
        }
        
        public static CTMarkerStyle parse(final String s) throws XmlException {
            return (CTMarkerStyle)getTypeLoader().parse(s, CTMarkerStyle.type, (XmlOptions)null);
        }
        
        public static CTMarkerStyle parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTMarkerStyle)getTypeLoader().parse(s, CTMarkerStyle.type, xmlOptions);
        }
        
        public static CTMarkerStyle parse(final File file) throws XmlException, IOException {
            return (CTMarkerStyle)getTypeLoader().parse(file, CTMarkerStyle.type, (XmlOptions)null);
        }
        
        public static CTMarkerStyle parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTMarkerStyle)getTypeLoader().parse(file, CTMarkerStyle.type, xmlOptions);
        }
        
        public static CTMarkerStyle parse(final URL url) throws XmlException, IOException {
            return (CTMarkerStyle)getTypeLoader().parse(url, CTMarkerStyle.type, (XmlOptions)null);
        }
        
        public static CTMarkerStyle parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTMarkerStyle)getTypeLoader().parse(url, CTMarkerStyle.type, xmlOptions);
        }
        
        public static CTMarkerStyle parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTMarkerStyle)getTypeLoader().parse(inputStream, CTMarkerStyle.type, (XmlOptions)null);
        }
        
        public static CTMarkerStyle parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTMarkerStyle)getTypeLoader().parse(inputStream, CTMarkerStyle.type, xmlOptions);
        }
        
        public static CTMarkerStyle parse(final Reader reader) throws XmlException, IOException {
            return (CTMarkerStyle)getTypeLoader().parse(reader, CTMarkerStyle.type, (XmlOptions)null);
        }
        
        public static CTMarkerStyle parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTMarkerStyle)getTypeLoader().parse(reader, CTMarkerStyle.type, xmlOptions);
        }
        
        public static CTMarkerStyle parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTMarkerStyle)getTypeLoader().parse(xmlStreamReader, CTMarkerStyle.type, (XmlOptions)null);
        }
        
        public static CTMarkerStyle parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTMarkerStyle)getTypeLoader().parse(xmlStreamReader, CTMarkerStyle.type, xmlOptions);
        }
        
        public static CTMarkerStyle parse(final Node node) throws XmlException {
            return (CTMarkerStyle)getTypeLoader().parse(node, CTMarkerStyle.type, (XmlOptions)null);
        }
        
        public static CTMarkerStyle parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTMarkerStyle)getTypeLoader().parse(node, CTMarkerStyle.type, xmlOptions);
        }
        
        @Deprecated
        public static CTMarkerStyle parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTMarkerStyle)getTypeLoader().parse(xmlInputStream, CTMarkerStyle.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTMarkerStyle parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTMarkerStyle)getTypeLoader().parse(xmlInputStream, CTMarkerStyle.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTMarkerStyle.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTMarkerStyle.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
