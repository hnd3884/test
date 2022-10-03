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

public interface CTRadarStyle extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTRadarStyle.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctradarstyle77d1type");
    
    STRadarStyle.Enum getVal();
    
    STRadarStyle xgetVal();
    
    boolean isSetVal();
    
    void setVal(final STRadarStyle.Enum p0);
    
    void xsetVal(final STRadarStyle p0);
    
    void unsetVal();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTRadarStyle.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTRadarStyle newInstance() {
            return (CTRadarStyle)getTypeLoader().newInstance(CTRadarStyle.type, (XmlOptions)null);
        }
        
        public static CTRadarStyle newInstance(final XmlOptions xmlOptions) {
            return (CTRadarStyle)getTypeLoader().newInstance(CTRadarStyle.type, xmlOptions);
        }
        
        public static CTRadarStyle parse(final String s) throws XmlException {
            return (CTRadarStyle)getTypeLoader().parse(s, CTRadarStyle.type, (XmlOptions)null);
        }
        
        public static CTRadarStyle parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTRadarStyle)getTypeLoader().parse(s, CTRadarStyle.type, xmlOptions);
        }
        
        public static CTRadarStyle parse(final File file) throws XmlException, IOException {
            return (CTRadarStyle)getTypeLoader().parse(file, CTRadarStyle.type, (XmlOptions)null);
        }
        
        public static CTRadarStyle parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTRadarStyle)getTypeLoader().parse(file, CTRadarStyle.type, xmlOptions);
        }
        
        public static CTRadarStyle parse(final URL url) throws XmlException, IOException {
            return (CTRadarStyle)getTypeLoader().parse(url, CTRadarStyle.type, (XmlOptions)null);
        }
        
        public static CTRadarStyle parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTRadarStyle)getTypeLoader().parse(url, CTRadarStyle.type, xmlOptions);
        }
        
        public static CTRadarStyle parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTRadarStyle)getTypeLoader().parse(inputStream, CTRadarStyle.type, (XmlOptions)null);
        }
        
        public static CTRadarStyle parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTRadarStyle)getTypeLoader().parse(inputStream, CTRadarStyle.type, xmlOptions);
        }
        
        public static CTRadarStyle parse(final Reader reader) throws XmlException, IOException {
            return (CTRadarStyle)getTypeLoader().parse(reader, CTRadarStyle.type, (XmlOptions)null);
        }
        
        public static CTRadarStyle parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTRadarStyle)getTypeLoader().parse(reader, CTRadarStyle.type, xmlOptions);
        }
        
        public static CTRadarStyle parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTRadarStyle)getTypeLoader().parse(xmlStreamReader, CTRadarStyle.type, (XmlOptions)null);
        }
        
        public static CTRadarStyle parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTRadarStyle)getTypeLoader().parse(xmlStreamReader, CTRadarStyle.type, xmlOptions);
        }
        
        public static CTRadarStyle parse(final Node node) throws XmlException {
            return (CTRadarStyle)getTypeLoader().parse(node, CTRadarStyle.type, (XmlOptions)null);
        }
        
        public static CTRadarStyle parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTRadarStyle)getTypeLoader().parse(node, CTRadarStyle.type, xmlOptions);
        }
        
        @Deprecated
        public static CTRadarStyle parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTRadarStyle)getTypeLoader().parse(xmlInputStream, CTRadarStyle.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTRadarStyle parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTRadarStyle)getTypeLoader().parse(xmlInputStream, CTRadarStyle.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTRadarStyle.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTRadarStyle.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
