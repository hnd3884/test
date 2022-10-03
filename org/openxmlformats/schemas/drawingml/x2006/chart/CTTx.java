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
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextBody;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTTx extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTTx.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("cttx9678type");
    
    CTStrRef getStrRef();
    
    boolean isSetStrRef();
    
    void setStrRef(final CTStrRef p0);
    
    CTStrRef addNewStrRef();
    
    void unsetStrRef();
    
    CTTextBody getRich();
    
    boolean isSetRich();
    
    void setRich(final CTTextBody p0);
    
    CTTextBody addNewRich();
    
    void unsetRich();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTTx.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTTx newInstance() {
            return (CTTx)getTypeLoader().newInstance(CTTx.type, (XmlOptions)null);
        }
        
        public static CTTx newInstance(final XmlOptions xmlOptions) {
            return (CTTx)getTypeLoader().newInstance(CTTx.type, xmlOptions);
        }
        
        public static CTTx parse(final String s) throws XmlException {
            return (CTTx)getTypeLoader().parse(s, CTTx.type, (XmlOptions)null);
        }
        
        public static CTTx parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTTx)getTypeLoader().parse(s, CTTx.type, xmlOptions);
        }
        
        public static CTTx parse(final File file) throws XmlException, IOException {
            return (CTTx)getTypeLoader().parse(file, CTTx.type, (XmlOptions)null);
        }
        
        public static CTTx parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTx)getTypeLoader().parse(file, CTTx.type, xmlOptions);
        }
        
        public static CTTx parse(final URL url) throws XmlException, IOException {
            return (CTTx)getTypeLoader().parse(url, CTTx.type, (XmlOptions)null);
        }
        
        public static CTTx parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTx)getTypeLoader().parse(url, CTTx.type, xmlOptions);
        }
        
        public static CTTx parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTTx)getTypeLoader().parse(inputStream, CTTx.type, (XmlOptions)null);
        }
        
        public static CTTx parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTx)getTypeLoader().parse(inputStream, CTTx.type, xmlOptions);
        }
        
        public static CTTx parse(final Reader reader) throws XmlException, IOException {
            return (CTTx)getTypeLoader().parse(reader, CTTx.type, (XmlOptions)null);
        }
        
        public static CTTx parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTx)getTypeLoader().parse(reader, CTTx.type, xmlOptions);
        }
        
        public static CTTx parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTTx)getTypeLoader().parse(xmlStreamReader, CTTx.type, (XmlOptions)null);
        }
        
        public static CTTx parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTTx)getTypeLoader().parse(xmlStreamReader, CTTx.type, xmlOptions);
        }
        
        public static CTTx parse(final Node node) throws XmlException {
            return (CTTx)getTypeLoader().parse(node, CTTx.type, (XmlOptions)null);
        }
        
        public static CTTx parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTTx)getTypeLoader().parse(node, CTTx.type, xmlOptions);
        }
        
        @Deprecated
        public static CTTx parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTTx)getTypeLoader().parse(xmlInputStream, CTTx.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTTx parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTTx)getTypeLoader().parse(xmlInputStream, CTTx.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTTx.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTTx.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
