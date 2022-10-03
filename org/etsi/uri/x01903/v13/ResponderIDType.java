package org.etsi.uri.x01903.v13;

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
import org.apache.xmlbeans.XmlBase64Binary;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface ResponderIDType extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(ResponderIDType.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.s8C3F193EE11A2F798ACF65489B9E6078").resolveHandle("responderidtype55b9type");
    
    String getByName();
    
    XmlString xgetByName();
    
    boolean isSetByName();
    
    void setByName(final String p0);
    
    void xsetByName(final XmlString p0);
    
    void unsetByName();
    
    byte[] getByKey();
    
    XmlBase64Binary xgetByKey();
    
    boolean isSetByKey();
    
    void setByKey(final byte[] p0);
    
    void xsetByKey(final XmlBase64Binary p0);
    
    void unsetByKey();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(ResponderIDType.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static ResponderIDType newInstance() {
            return (ResponderIDType)getTypeLoader().newInstance(ResponderIDType.type, (XmlOptions)null);
        }
        
        public static ResponderIDType newInstance(final XmlOptions xmlOptions) {
            return (ResponderIDType)getTypeLoader().newInstance(ResponderIDType.type, xmlOptions);
        }
        
        public static ResponderIDType parse(final String s) throws XmlException {
            return (ResponderIDType)getTypeLoader().parse(s, ResponderIDType.type, (XmlOptions)null);
        }
        
        public static ResponderIDType parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (ResponderIDType)getTypeLoader().parse(s, ResponderIDType.type, xmlOptions);
        }
        
        public static ResponderIDType parse(final File file) throws XmlException, IOException {
            return (ResponderIDType)getTypeLoader().parse(file, ResponderIDType.type, (XmlOptions)null);
        }
        
        public static ResponderIDType parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (ResponderIDType)getTypeLoader().parse(file, ResponderIDType.type, xmlOptions);
        }
        
        public static ResponderIDType parse(final URL url) throws XmlException, IOException {
            return (ResponderIDType)getTypeLoader().parse(url, ResponderIDType.type, (XmlOptions)null);
        }
        
        public static ResponderIDType parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (ResponderIDType)getTypeLoader().parse(url, ResponderIDType.type, xmlOptions);
        }
        
        public static ResponderIDType parse(final InputStream inputStream) throws XmlException, IOException {
            return (ResponderIDType)getTypeLoader().parse(inputStream, ResponderIDType.type, (XmlOptions)null);
        }
        
        public static ResponderIDType parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (ResponderIDType)getTypeLoader().parse(inputStream, ResponderIDType.type, xmlOptions);
        }
        
        public static ResponderIDType parse(final Reader reader) throws XmlException, IOException {
            return (ResponderIDType)getTypeLoader().parse(reader, ResponderIDType.type, (XmlOptions)null);
        }
        
        public static ResponderIDType parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (ResponderIDType)getTypeLoader().parse(reader, ResponderIDType.type, xmlOptions);
        }
        
        public static ResponderIDType parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (ResponderIDType)getTypeLoader().parse(xmlStreamReader, ResponderIDType.type, (XmlOptions)null);
        }
        
        public static ResponderIDType parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (ResponderIDType)getTypeLoader().parse(xmlStreamReader, ResponderIDType.type, xmlOptions);
        }
        
        public static ResponderIDType parse(final Node node) throws XmlException {
            return (ResponderIDType)getTypeLoader().parse(node, ResponderIDType.type, (XmlOptions)null);
        }
        
        public static ResponderIDType parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (ResponderIDType)getTypeLoader().parse(node, ResponderIDType.type, xmlOptions);
        }
        
        @Deprecated
        public static ResponderIDType parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (ResponderIDType)getTypeLoader().parse(xmlInputStream, ResponderIDType.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static ResponderIDType parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (ResponderIDType)getTypeLoader().parse(xmlInputStream, ResponderIDType.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, ResponderIDType.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, ResponderIDType.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
