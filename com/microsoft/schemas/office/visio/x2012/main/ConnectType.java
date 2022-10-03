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
import org.apache.xmlbeans.XmlInt;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.XmlUnsignedInt;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface ConnectType extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(ConnectType.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("connecttypeea41type");
    
    long getFromSheet();
    
    XmlUnsignedInt xgetFromSheet();
    
    void setFromSheet(final long p0);
    
    void xsetFromSheet(final XmlUnsignedInt p0);
    
    String getFromCell();
    
    XmlString xgetFromCell();
    
    boolean isSetFromCell();
    
    void setFromCell(final String p0);
    
    void xsetFromCell(final XmlString p0);
    
    void unsetFromCell();
    
    int getFromPart();
    
    XmlInt xgetFromPart();
    
    boolean isSetFromPart();
    
    void setFromPart(final int p0);
    
    void xsetFromPart(final XmlInt p0);
    
    void unsetFromPart();
    
    long getToSheet();
    
    XmlUnsignedInt xgetToSheet();
    
    void setToSheet(final long p0);
    
    void xsetToSheet(final XmlUnsignedInt p0);
    
    String getToCell();
    
    XmlString xgetToCell();
    
    boolean isSetToCell();
    
    void setToCell(final String p0);
    
    void xsetToCell(final XmlString p0);
    
    void unsetToCell();
    
    int getToPart();
    
    XmlInt xgetToPart();
    
    boolean isSetToPart();
    
    void setToPart(final int p0);
    
    void xsetToPart(final XmlInt p0);
    
    void unsetToPart();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(ConnectType.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static ConnectType newInstance() {
            return (ConnectType)getTypeLoader().newInstance(ConnectType.type, (XmlOptions)null);
        }
        
        public static ConnectType newInstance(final XmlOptions xmlOptions) {
            return (ConnectType)getTypeLoader().newInstance(ConnectType.type, xmlOptions);
        }
        
        public static ConnectType parse(final String s) throws XmlException {
            return (ConnectType)getTypeLoader().parse(s, ConnectType.type, (XmlOptions)null);
        }
        
        public static ConnectType parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (ConnectType)getTypeLoader().parse(s, ConnectType.type, xmlOptions);
        }
        
        public static ConnectType parse(final File file) throws XmlException, IOException {
            return (ConnectType)getTypeLoader().parse(file, ConnectType.type, (XmlOptions)null);
        }
        
        public static ConnectType parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (ConnectType)getTypeLoader().parse(file, ConnectType.type, xmlOptions);
        }
        
        public static ConnectType parse(final URL url) throws XmlException, IOException {
            return (ConnectType)getTypeLoader().parse(url, ConnectType.type, (XmlOptions)null);
        }
        
        public static ConnectType parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (ConnectType)getTypeLoader().parse(url, ConnectType.type, xmlOptions);
        }
        
        public static ConnectType parse(final InputStream inputStream) throws XmlException, IOException {
            return (ConnectType)getTypeLoader().parse(inputStream, ConnectType.type, (XmlOptions)null);
        }
        
        public static ConnectType parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (ConnectType)getTypeLoader().parse(inputStream, ConnectType.type, xmlOptions);
        }
        
        public static ConnectType parse(final Reader reader) throws XmlException, IOException {
            return (ConnectType)getTypeLoader().parse(reader, ConnectType.type, (XmlOptions)null);
        }
        
        public static ConnectType parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (ConnectType)getTypeLoader().parse(reader, ConnectType.type, xmlOptions);
        }
        
        public static ConnectType parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (ConnectType)getTypeLoader().parse(xmlStreamReader, ConnectType.type, (XmlOptions)null);
        }
        
        public static ConnectType parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (ConnectType)getTypeLoader().parse(xmlStreamReader, ConnectType.type, xmlOptions);
        }
        
        public static ConnectType parse(final Node node) throws XmlException {
            return (ConnectType)getTypeLoader().parse(node, ConnectType.type, (XmlOptions)null);
        }
        
        public static ConnectType parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (ConnectType)getTypeLoader().parse(node, ConnectType.type, xmlOptions);
        }
        
        @Deprecated
        public static ConnectType parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (ConnectType)getTypeLoader().parse(xmlInputStream, ConnectType.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static ConnectType parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (ConnectType)getTypeLoader().parse(xmlInputStream, ConnectType.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, ConnectType.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, ConnectType.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
