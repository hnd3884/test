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
import org.apache.xmlbeans.XmlAnyURI;
import org.apache.xmlbeans.XmlDateTime;
import java.util.Calendar;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface OCSPIdentifierType extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(OCSPIdentifierType.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.s8C3F193EE11A2F798ACF65489B9E6078").resolveHandle("ocspidentifiertype3968type");
    
    ResponderIDType getResponderID();
    
    void setResponderID(final ResponderIDType p0);
    
    ResponderIDType addNewResponderID();
    
    Calendar getProducedAt();
    
    XmlDateTime xgetProducedAt();
    
    void setProducedAt(final Calendar p0);
    
    void xsetProducedAt(final XmlDateTime p0);
    
    String getURI();
    
    XmlAnyURI xgetURI();
    
    boolean isSetURI();
    
    void setURI(final String p0);
    
    void xsetURI(final XmlAnyURI p0);
    
    void unsetURI();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(OCSPIdentifierType.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static OCSPIdentifierType newInstance() {
            return (OCSPIdentifierType)getTypeLoader().newInstance(OCSPIdentifierType.type, (XmlOptions)null);
        }
        
        public static OCSPIdentifierType newInstance(final XmlOptions xmlOptions) {
            return (OCSPIdentifierType)getTypeLoader().newInstance(OCSPIdentifierType.type, xmlOptions);
        }
        
        public static OCSPIdentifierType parse(final String s) throws XmlException {
            return (OCSPIdentifierType)getTypeLoader().parse(s, OCSPIdentifierType.type, (XmlOptions)null);
        }
        
        public static OCSPIdentifierType parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (OCSPIdentifierType)getTypeLoader().parse(s, OCSPIdentifierType.type, xmlOptions);
        }
        
        public static OCSPIdentifierType parse(final File file) throws XmlException, IOException {
            return (OCSPIdentifierType)getTypeLoader().parse(file, OCSPIdentifierType.type, (XmlOptions)null);
        }
        
        public static OCSPIdentifierType parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (OCSPIdentifierType)getTypeLoader().parse(file, OCSPIdentifierType.type, xmlOptions);
        }
        
        public static OCSPIdentifierType parse(final URL url) throws XmlException, IOException {
            return (OCSPIdentifierType)getTypeLoader().parse(url, OCSPIdentifierType.type, (XmlOptions)null);
        }
        
        public static OCSPIdentifierType parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (OCSPIdentifierType)getTypeLoader().parse(url, OCSPIdentifierType.type, xmlOptions);
        }
        
        public static OCSPIdentifierType parse(final InputStream inputStream) throws XmlException, IOException {
            return (OCSPIdentifierType)getTypeLoader().parse(inputStream, OCSPIdentifierType.type, (XmlOptions)null);
        }
        
        public static OCSPIdentifierType parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (OCSPIdentifierType)getTypeLoader().parse(inputStream, OCSPIdentifierType.type, xmlOptions);
        }
        
        public static OCSPIdentifierType parse(final Reader reader) throws XmlException, IOException {
            return (OCSPIdentifierType)getTypeLoader().parse(reader, OCSPIdentifierType.type, (XmlOptions)null);
        }
        
        public static OCSPIdentifierType parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (OCSPIdentifierType)getTypeLoader().parse(reader, OCSPIdentifierType.type, xmlOptions);
        }
        
        public static OCSPIdentifierType parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (OCSPIdentifierType)getTypeLoader().parse(xmlStreamReader, OCSPIdentifierType.type, (XmlOptions)null);
        }
        
        public static OCSPIdentifierType parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (OCSPIdentifierType)getTypeLoader().parse(xmlStreamReader, OCSPIdentifierType.type, xmlOptions);
        }
        
        public static OCSPIdentifierType parse(final Node node) throws XmlException {
            return (OCSPIdentifierType)getTypeLoader().parse(node, OCSPIdentifierType.type, (XmlOptions)null);
        }
        
        public static OCSPIdentifierType parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (OCSPIdentifierType)getTypeLoader().parse(node, OCSPIdentifierType.type, xmlOptions);
        }
        
        @Deprecated
        public static OCSPIdentifierType parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (OCSPIdentifierType)getTypeLoader().parse(xmlInputStream, OCSPIdentifierType.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static OCSPIdentifierType parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (OCSPIdentifierType)getTypeLoader().parse(xmlInputStream, OCSPIdentifierType.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, OCSPIdentifierType.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, OCSPIdentifierType.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
