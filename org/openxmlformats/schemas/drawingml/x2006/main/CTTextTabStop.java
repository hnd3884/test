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
import org.apache.xmlbeans.XmlObject;

public interface CTTextTabStop extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTTextTabStop.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("cttexttabstopb57btype");
    
    int getPos();
    
    STCoordinate32 xgetPos();
    
    boolean isSetPos();
    
    void setPos(final int p0);
    
    void xsetPos(final STCoordinate32 p0);
    
    void unsetPos();
    
    STTextTabAlignType.Enum getAlgn();
    
    STTextTabAlignType xgetAlgn();
    
    boolean isSetAlgn();
    
    void setAlgn(final STTextTabAlignType.Enum p0);
    
    void xsetAlgn(final STTextTabAlignType p0);
    
    void unsetAlgn();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTTextTabStop.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTTextTabStop newInstance() {
            return (CTTextTabStop)getTypeLoader().newInstance(CTTextTabStop.type, (XmlOptions)null);
        }
        
        public static CTTextTabStop newInstance(final XmlOptions xmlOptions) {
            return (CTTextTabStop)getTypeLoader().newInstance(CTTextTabStop.type, xmlOptions);
        }
        
        public static CTTextTabStop parse(final String s) throws XmlException {
            return (CTTextTabStop)getTypeLoader().parse(s, CTTextTabStop.type, (XmlOptions)null);
        }
        
        public static CTTextTabStop parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTTextTabStop)getTypeLoader().parse(s, CTTextTabStop.type, xmlOptions);
        }
        
        public static CTTextTabStop parse(final File file) throws XmlException, IOException {
            return (CTTextTabStop)getTypeLoader().parse(file, CTTextTabStop.type, (XmlOptions)null);
        }
        
        public static CTTextTabStop parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTextTabStop)getTypeLoader().parse(file, CTTextTabStop.type, xmlOptions);
        }
        
        public static CTTextTabStop parse(final URL url) throws XmlException, IOException {
            return (CTTextTabStop)getTypeLoader().parse(url, CTTextTabStop.type, (XmlOptions)null);
        }
        
        public static CTTextTabStop parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTextTabStop)getTypeLoader().parse(url, CTTextTabStop.type, xmlOptions);
        }
        
        public static CTTextTabStop parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTTextTabStop)getTypeLoader().parse(inputStream, CTTextTabStop.type, (XmlOptions)null);
        }
        
        public static CTTextTabStop parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTextTabStop)getTypeLoader().parse(inputStream, CTTextTabStop.type, xmlOptions);
        }
        
        public static CTTextTabStop parse(final Reader reader) throws XmlException, IOException {
            return (CTTextTabStop)getTypeLoader().parse(reader, CTTextTabStop.type, (XmlOptions)null);
        }
        
        public static CTTextTabStop parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTextTabStop)getTypeLoader().parse(reader, CTTextTabStop.type, xmlOptions);
        }
        
        public static CTTextTabStop parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTTextTabStop)getTypeLoader().parse(xmlStreamReader, CTTextTabStop.type, (XmlOptions)null);
        }
        
        public static CTTextTabStop parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTTextTabStop)getTypeLoader().parse(xmlStreamReader, CTTextTabStop.type, xmlOptions);
        }
        
        public static CTTextTabStop parse(final Node node) throws XmlException {
            return (CTTextTabStop)getTypeLoader().parse(node, CTTextTabStop.type, (XmlOptions)null);
        }
        
        public static CTTextTabStop parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTTextTabStop)getTypeLoader().parse(node, CTTextTabStop.type, xmlOptions);
        }
        
        @Deprecated
        public static CTTextTabStop parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTTextTabStop)getTypeLoader().parse(xmlInputStream, CTTextTabStop.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTTextTabStop parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTTextTabStop)getTypeLoader().parse(xmlInputStream, CTTextTabStop.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTTextTabStop.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTTextTabStop.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
