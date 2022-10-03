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

public interface CTSerTx extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTSerTx.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctsertxd722type");
    
    CTStrRef getStrRef();
    
    boolean isSetStrRef();
    
    void setStrRef(final CTStrRef p0);
    
    CTStrRef addNewStrRef();
    
    void unsetStrRef();
    
    String getV();
    
    STXstring xgetV();
    
    boolean isSetV();
    
    void setV(final String p0);
    
    void xsetV(final STXstring p0);
    
    void unsetV();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTSerTx.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTSerTx newInstance() {
            return (CTSerTx)getTypeLoader().newInstance(CTSerTx.type, (XmlOptions)null);
        }
        
        public static CTSerTx newInstance(final XmlOptions xmlOptions) {
            return (CTSerTx)getTypeLoader().newInstance(CTSerTx.type, xmlOptions);
        }
        
        public static CTSerTx parse(final String s) throws XmlException {
            return (CTSerTx)getTypeLoader().parse(s, CTSerTx.type, (XmlOptions)null);
        }
        
        public static CTSerTx parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTSerTx)getTypeLoader().parse(s, CTSerTx.type, xmlOptions);
        }
        
        public static CTSerTx parse(final File file) throws XmlException, IOException {
            return (CTSerTx)getTypeLoader().parse(file, CTSerTx.type, (XmlOptions)null);
        }
        
        public static CTSerTx parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTSerTx)getTypeLoader().parse(file, CTSerTx.type, xmlOptions);
        }
        
        public static CTSerTx parse(final URL url) throws XmlException, IOException {
            return (CTSerTx)getTypeLoader().parse(url, CTSerTx.type, (XmlOptions)null);
        }
        
        public static CTSerTx parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTSerTx)getTypeLoader().parse(url, CTSerTx.type, xmlOptions);
        }
        
        public static CTSerTx parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTSerTx)getTypeLoader().parse(inputStream, CTSerTx.type, (XmlOptions)null);
        }
        
        public static CTSerTx parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTSerTx)getTypeLoader().parse(inputStream, CTSerTx.type, xmlOptions);
        }
        
        public static CTSerTx parse(final Reader reader) throws XmlException, IOException {
            return (CTSerTx)getTypeLoader().parse(reader, CTSerTx.type, (XmlOptions)null);
        }
        
        public static CTSerTx parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTSerTx)getTypeLoader().parse(reader, CTSerTx.type, xmlOptions);
        }
        
        public static CTSerTx parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTSerTx)getTypeLoader().parse(xmlStreamReader, CTSerTx.type, (XmlOptions)null);
        }
        
        public static CTSerTx parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTSerTx)getTypeLoader().parse(xmlStreamReader, CTSerTx.type, xmlOptions);
        }
        
        public static CTSerTx parse(final Node node) throws XmlException {
            return (CTSerTx)getTypeLoader().parse(node, CTSerTx.type, (XmlOptions)null);
        }
        
        public static CTSerTx parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTSerTx)getTypeLoader().parse(node, CTSerTx.type, xmlOptions);
        }
        
        @Deprecated
        public static CTSerTx parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTSerTx)getTypeLoader().parse(xmlInputStream, CTSerTx.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTSerTx parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTSerTx)getTypeLoader().parse(xmlInputStream, CTSerTx.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTSerTx.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTSerTx.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
