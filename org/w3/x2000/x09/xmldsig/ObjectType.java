package org.w3.x2000.x09.xmldsig;

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
import org.apache.xmlbeans.XmlAnyURI;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.XmlID;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface ObjectType extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(ObjectType.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.s8C3F193EE11A2F798ACF65489B9E6078").resolveHandle("objecttypec966type");
    
    String getId();
    
    XmlID xgetId();
    
    boolean isSetId();
    
    void setId(final String p0);
    
    void xsetId(final XmlID p0);
    
    void unsetId();
    
    String getMimeType();
    
    XmlString xgetMimeType();
    
    boolean isSetMimeType();
    
    void setMimeType(final String p0);
    
    void xsetMimeType(final XmlString p0);
    
    void unsetMimeType();
    
    String getEncoding();
    
    XmlAnyURI xgetEncoding();
    
    boolean isSetEncoding();
    
    void setEncoding(final String p0);
    
    void xsetEncoding(final XmlAnyURI p0);
    
    void unsetEncoding();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(ObjectType.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static ObjectType newInstance() {
            return (ObjectType)getTypeLoader().newInstance(ObjectType.type, (XmlOptions)null);
        }
        
        public static ObjectType newInstance(final XmlOptions xmlOptions) {
            return (ObjectType)getTypeLoader().newInstance(ObjectType.type, xmlOptions);
        }
        
        public static ObjectType parse(final String s) throws XmlException {
            return (ObjectType)getTypeLoader().parse(s, ObjectType.type, (XmlOptions)null);
        }
        
        public static ObjectType parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (ObjectType)getTypeLoader().parse(s, ObjectType.type, xmlOptions);
        }
        
        public static ObjectType parse(final File file) throws XmlException, IOException {
            return (ObjectType)getTypeLoader().parse(file, ObjectType.type, (XmlOptions)null);
        }
        
        public static ObjectType parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (ObjectType)getTypeLoader().parse(file, ObjectType.type, xmlOptions);
        }
        
        public static ObjectType parse(final URL url) throws XmlException, IOException {
            return (ObjectType)getTypeLoader().parse(url, ObjectType.type, (XmlOptions)null);
        }
        
        public static ObjectType parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (ObjectType)getTypeLoader().parse(url, ObjectType.type, xmlOptions);
        }
        
        public static ObjectType parse(final InputStream inputStream) throws XmlException, IOException {
            return (ObjectType)getTypeLoader().parse(inputStream, ObjectType.type, (XmlOptions)null);
        }
        
        public static ObjectType parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (ObjectType)getTypeLoader().parse(inputStream, ObjectType.type, xmlOptions);
        }
        
        public static ObjectType parse(final Reader reader) throws XmlException, IOException {
            return (ObjectType)getTypeLoader().parse(reader, ObjectType.type, (XmlOptions)null);
        }
        
        public static ObjectType parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (ObjectType)getTypeLoader().parse(reader, ObjectType.type, xmlOptions);
        }
        
        public static ObjectType parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (ObjectType)getTypeLoader().parse(xmlStreamReader, ObjectType.type, (XmlOptions)null);
        }
        
        public static ObjectType parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (ObjectType)getTypeLoader().parse(xmlStreamReader, ObjectType.type, xmlOptions);
        }
        
        public static ObjectType parse(final Node node) throws XmlException {
            return (ObjectType)getTypeLoader().parse(node, ObjectType.type, (XmlOptions)null);
        }
        
        public static ObjectType parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (ObjectType)getTypeLoader().parse(node, ObjectType.type, xmlOptions);
        }
        
        @Deprecated
        public static ObjectType parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (ObjectType)getTypeLoader().parse(xmlInputStream, ObjectType.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static ObjectType parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (ObjectType)getTypeLoader().parse(xmlInputStream, ObjectType.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, ObjectType.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, ObjectType.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
