package org.openxmlformats.schemas.wordprocessingml.x2006.main;

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

public interface CTDocDefaults extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTDocDefaults.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctdocdefaults2ea8type");
    
    CTRPrDefault getRPrDefault();
    
    boolean isSetRPrDefault();
    
    void setRPrDefault(final CTRPrDefault p0);
    
    CTRPrDefault addNewRPrDefault();
    
    void unsetRPrDefault();
    
    CTPPrDefault getPPrDefault();
    
    boolean isSetPPrDefault();
    
    void setPPrDefault(final CTPPrDefault p0);
    
    CTPPrDefault addNewPPrDefault();
    
    void unsetPPrDefault();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTDocDefaults.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTDocDefaults newInstance() {
            return (CTDocDefaults)getTypeLoader().newInstance(CTDocDefaults.type, (XmlOptions)null);
        }
        
        public static CTDocDefaults newInstance(final XmlOptions xmlOptions) {
            return (CTDocDefaults)getTypeLoader().newInstance(CTDocDefaults.type, xmlOptions);
        }
        
        public static CTDocDefaults parse(final String s) throws XmlException {
            return (CTDocDefaults)getTypeLoader().parse(s, CTDocDefaults.type, (XmlOptions)null);
        }
        
        public static CTDocDefaults parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTDocDefaults)getTypeLoader().parse(s, CTDocDefaults.type, xmlOptions);
        }
        
        public static CTDocDefaults parse(final File file) throws XmlException, IOException {
            return (CTDocDefaults)getTypeLoader().parse(file, CTDocDefaults.type, (XmlOptions)null);
        }
        
        public static CTDocDefaults parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTDocDefaults)getTypeLoader().parse(file, CTDocDefaults.type, xmlOptions);
        }
        
        public static CTDocDefaults parse(final URL url) throws XmlException, IOException {
            return (CTDocDefaults)getTypeLoader().parse(url, CTDocDefaults.type, (XmlOptions)null);
        }
        
        public static CTDocDefaults parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTDocDefaults)getTypeLoader().parse(url, CTDocDefaults.type, xmlOptions);
        }
        
        public static CTDocDefaults parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTDocDefaults)getTypeLoader().parse(inputStream, CTDocDefaults.type, (XmlOptions)null);
        }
        
        public static CTDocDefaults parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTDocDefaults)getTypeLoader().parse(inputStream, CTDocDefaults.type, xmlOptions);
        }
        
        public static CTDocDefaults parse(final Reader reader) throws XmlException, IOException {
            return (CTDocDefaults)getTypeLoader().parse(reader, CTDocDefaults.type, (XmlOptions)null);
        }
        
        public static CTDocDefaults parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTDocDefaults)getTypeLoader().parse(reader, CTDocDefaults.type, xmlOptions);
        }
        
        public static CTDocDefaults parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTDocDefaults)getTypeLoader().parse(xmlStreamReader, CTDocDefaults.type, (XmlOptions)null);
        }
        
        public static CTDocDefaults parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTDocDefaults)getTypeLoader().parse(xmlStreamReader, CTDocDefaults.type, xmlOptions);
        }
        
        public static CTDocDefaults parse(final Node node) throws XmlException {
            return (CTDocDefaults)getTypeLoader().parse(node, CTDocDefaults.type, (XmlOptions)null);
        }
        
        public static CTDocDefaults parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTDocDefaults)getTypeLoader().parse(node, CTDocDefaults.type, xmlOptions);
        }
        
        @Deprecated
        public static CTDocDefaults parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTDocDefaults)getTypeLoader().parse(xmlInputStream, CTDocDefaults.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTDocDefaults parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTDocDefaults)getTypeLoader().parse(xmlInputStream, CTDocDefaults.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTDocDefaults.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTDocDefaults.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
