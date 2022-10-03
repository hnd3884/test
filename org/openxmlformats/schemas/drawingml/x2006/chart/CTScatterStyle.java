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

public interface CTScatterStyle extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTScatterStyle.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctscatterstyle94c9type");
    
    STScatterStyle.Enum getVal();
    
    STScatterStyle xgetVal();
    
    boolean isSetVal();
    
    void setVal(final STScatterStyle.Enum p0);
    
    void xsetVal(final STScatterStyle p0);
    
    void unsetVal();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTScatterStyle.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTScatterStyle newInstance() {
            return (CTScatterStyle)getTypeLoader().newInstance(CTScatterStyle.type, (XmlOptions)null);
        }
        
        public static CTScatterStyle newInstance(final XmlOptions xmlOptions) {
            return (CTScatterStyle)getTypeLoader().newInstance(CTScatterStyle.type, xmlOptions);
        }
        
        public static CTScatterStyle parse(final String s) throws XmlException {
            return (CTScatterStyle)getTypeLoader().parse(s, CTScatterStyle.type, (XmlOptions)null);
        }
        
        public static CTScatterStyle parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTScatterStyle)getTypeLoader().parse(s, CTScatterStyle.type, xmlOptions);
        }
        
        public static CTScatterStyle parse(final File file) throws XmlException, IOException {
            return (CTScatterStyle)getTypeLoader().parse(file, CTScatterStyle.type, (XmlOptions)null);
        }
        
        public static CTScatterStyle parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTScatterStyle)getTypeLoader().parse(file, CTScatterStyle.type, xmlOptions);
        }
        
        public static CTScatterStyle parse(final URL url) throws XmlException, IOException {
            return (CTScatterStyle)getTypeLoader().parse(url, CTScatterStyle.type, (XmlOptions)null);
        }
        
        public static CTScatterStyle parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTScatterStyle)getTypeLoader().parse(url, CTScatterStyle.type, xmlOptions);
        }
        
        public static CTScatterStyle parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTScatterStyle)getTypeLoader().parse(inputStream, CTScatterStyle.type, (XmlOptions)null);
        }
        
        public static CTScatterStyle parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTScatterStyle)getTypeLoader().parse(inputStream, CTScatterStyle.type, xmlOptions);
        }
        
        public static CTScatterStyle parse(final Reader reader) throws XmlException, IOException {
            return (CTScatterStyle)getTypeLoader().parse(reader, CTScatterStyle.type, (XmlOptions)null);
        }
        
        public static CTScatterStyle parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTScatterStyle)getTypeLoader().parse(reader, CTScatterStyle.type, xmlOptions);
        }
        
        public static CTScatterStyle parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTScatterStyle)getTypeLoader().parse(xmlStreamReader, CTScatterStyle.type, (XmlOptions)null);
        }
        
        public static CTScatterStyle parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTScatterStyle)getTypeLoader().parse(xmlStreamReader, CTScatterStyle.type, xmlOptions);
        }
        
        public static CTScatterStyle parse(final Node node) throws XmlException {
            return (CTScatterStyle)getTypeLoader().parse(node, CTScatterStyle.type, (XmlOptions)null);
        }
        
        public static CTScatterStyle parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTScatterStyle)getTypeLoader().parse(node, CTScatterStyle.type, xmlOptions);
        }
        
        @Deprecated
        public static CTScatterStyle parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTScatterStyle)getTypeLoader().parse(xmlInputStream, CTScatterStyle.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTScatterStyle parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTScatterStyle)getTypeLoader().parse(xmlInputStream, CTScatterStyle.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTScatterStyle.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTScatterStyle.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
