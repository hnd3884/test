package com.microsoft.schemas.office.visio.x2012.main;

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
import java.util.List;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface ConnectsType extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(ConnectsType.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("connectstype8750type");
    
    List<ConnectType> getConnectList();
    
    @Deprecated
    ConnectType[] getConnectArray();
    
    ConnectType getConnectArray(final int p0);
    
    int sizeOfConnectArray();
    
    void setConnectArray(final ConnectType[] p0);
    
    void setConnectArray(final int p0, final ConnectType p1);
    
    ConnectType insertNewConnect(final int p0);
    
    ConnectType addNewConnect();
    
    void removeConnect(final int p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(ConnectsType.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static ConnectsType newInstance() {
            return (ConnectsType)getTypeLoader().newInstance(ConnectsType.type, (XmlOptions)null);
        }
        
        public static ConnectsType newInstance(final XmlOptions xmlOptions) {
            return (ConnectsType)getTypeLoader().newInstance(ConnectsType.type, xmlOptions);
        }
        
        public static ConnectsType parse(final String s) throws XmlException {
            return (ConnectsType)getTypeLoader().parse(s, ConnectsType.type, (XmlOptions)null);
        }
        
        public static ConnectsType parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (ConnectsType)getTypeLoader().parse(s, ConnectsType.type, xmlOptions);
        }
        
        public static ConnectsType parse(final File file) throws XmlException, IOException {
            return (ConnectsType)getTypeLoader().parse(file, ConnectsType.type, (XmlOptions)null);
        }
        
        public static ConnectsType parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (ConnectsType)getTypeLoader().parse(file, ConnectsType.type, xmlOptions);
        }
        
        public static ConnectsType parse(final URL url) throws XmlException, IOException {
            return (ConnectsType)getTypeLoader().parse(url, ConnectsType.type, (XmlOptions)null);
        }
        
        public static ConnectsType parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (ConnectsType)getTypeLoader().parse(url, ConnectsType.type, xmlOptions);
        }
        
        public static ConnectsType parse(final InputStream inputStream) throws XmlException, IOException {
            return (ConnectsType)getTypeLoader().parse(inputStream, ConnectsType.type, (XmlOptions)null);
        }
        
        public static ConnectsType parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (ConnectsType)getTypeLoader().parse(inputStream, ConnectsType.type, xmlOptions);
        }
        
        public static ConnectsType parse(final Reader reader) throws XmlException, IOException {
            return (ConnectsType)getTypeLoader().parse(reader, ConnectsType.type, (XmlOptions)null);
        }
        
        public static ConnectsType parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (ConnectsType)getTypeLoader().parse(reader, ConnectsType.type, xmlOptions);
        }
        
        public static ConnectsType parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (ConnectsType)getTypeLoader().parse(xmlStreamReader, ConnectsType.type, (XmlOptions)null);
        }
        
        public static ConnectsType parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (ConnectsType)getTypeLoader().parse(xmlStreamReader, ConnectsType.type, xmlOptions);
        }
        
        public static ConnectsType parse(final Node node) throws XmlException {
            return (ConnectsType)getTypeLoader().parse(node, ConnectsType.type, (XmlOptions)null);
        }
        
        public static ConnectsType parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (ConnectsType)getTypeLoader().parse(node, ConnectsType.type, xmlOptions);
        }
        
        @Deprecated
        public static ConnectsType parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (ConnectsType)getTypeLoader().parse(xmlInputStream, ConnectsType.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static ConnectsType parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (ConnectsType)getTypeLoader().parse(xmlInputStream, ConnectsType.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, ConnectsType.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, ConnectsType.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
