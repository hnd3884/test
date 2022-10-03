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
import org.apache.xmlbeans.XmlUnsignedInt;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTConnection extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTConnection.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctconnection7fb9type");
    
    long getId();
    
    STDrawingElementId xgetId();
    
    void setId(final long p0);
    
    void xsetId(final STDrawingElementId p0);
    
    long getIdx();
    
    XmlUnsignedInt xgetIdx();
    
    void setIdx(final long p0);
    
    void xsetIdx(final XmlUnsignedInt p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTConnection.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTConnection newInstance() {
            return (CTConnection)getTypeLoader().newInstance(CTConnection.type, (XmlOptions)null);
        }
        
        public static CTConnection newInstance(final XmlOptions xmlOptions) {
            return (CTConnection)getTypeLoader().newInstance(CTConnection.type, xmlOptions);
        }
        
        public static CTConnection parse(final String s) throws XmlException {
            return (CTConnection)getTypeLoader().parse(s, CTConnection.type, (XmlOptions)null);
        }
        
        public static CTConnection parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTConnection)getTypeLoader().parse(s, CTConnection.type, xmlOptions);
        }
        
        public static CTConnection parse(final File file) throws XmlException, IOException {
            return (CTConnection)getTypeLoader().parse(file, CTConnection.type, (XmlOptions)null);
        }
        
        public static CTConnection parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTConnection)getTypeLoader().parse(file, CTConnection.type, xmlOptions);
        }
        
        public static CTConnection parse(final URL url) throws XmlException, IOException {
            return (CTConnection)getTypeLoader().parse(url, CTConnection.type, (XmlOptions)null);
        }
        
        public static CTConnection parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTConnection)getTypeLoader().parse(url, CTConnection.type, xmlOptions);
        }
        
        public static CTConnection parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTConnection)getTypeLoader().parse(inputStream, CTConnection.type, (XmlOptions)null);
        }
        
        public static CTConnection parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTConnection)getTypeLoader().parse(inputStream, CTConnection.type, xmlOptions);
        }
        
        public static CTConnection parse(final Reader reader) throws XmlException, IOException {
            return (CTConnection)getTypeLoader().parse(reader, CTConnection.type, (XmlOptions)null);
        }
        
        public static CTConnection parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTConnection)getTypeLoader().parse(reader, CTConnection.type, xmlOptions);
        }
        
        public static CTConnection parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTConnection)getTypeLoader().parse(xmlStreamReader, CTConnection.type, (XmlOptions)null);
        }
        
        public static CTConnection parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTConnection)getTypeLoader().parse(xmlStreamReader, CTConnection.type, xmlOptions);
        }
        
        public static CTConnection parse(final Node node) throws XmlException {
            return (CTConnection)getTypeLoader().parse(node, CTConnection.type, (XmlOptions)null);
        }
        
        public static CTConnection parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTConnection)getTypeLoader().parse(node, CTConnection.type, xmlOptions);
        }
        
        @Deprecated
        public static CTConnection parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTConnection)getTypeLoader().parse(xmlInputStream, CTConnection.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTConnection parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTConnection)getTypeLoader().parse(xmlInputStream, CTConnection.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTConnection.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTConnection.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
