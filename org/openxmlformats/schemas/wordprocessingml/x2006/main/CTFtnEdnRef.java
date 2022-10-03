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
import java.math.BigInteger;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTFtnEdnRef extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTFtnEdnRef.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctftnednref89eetype");
    
    STOnOff.Enum getCustomMarkFollows();
    
    STOnOff xgetCustomMarkFollows();
    
    boolean isSetCustomMarkFollows();
    
    void setCustomMarkFollows(final STOnOff.Enum p0);
    
    void xsetCustomMarkFollows(final STOnOff p0);
    
    void unsetCustomMarkFollows();
    
    BigInteger getId();
    
    STDecimalNumber xgetId();
    
    void setId(final BigInteger p0);
    
    void xsetId(final STDecimalNumber p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTFtnEdnRef.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTFtnEdnRef newInstance() {
            return (CTFtnEdnRef)getTypeLoader().newInstance(CTFtnEdnRef.type, (XmlOptions)null);
        }
        
        public static CTFtnEdnRef newInstance(final XmlOptions xmlOptions) {
            return (CTFtnEdnRef)getTypeLoader().newInstance(CTFtnEdnRef.type, xmlOptions);
        }
        
        public static CTFtnEdnRef parse(final String s) throws XmlException {
            return (CTFtnEdnRef)getTypeLoader().parse(s, CTFtnEdnRef.type, (XmlOptions)null);
        }
        
        public static CTFtnEdnRef parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTFtnEdnRef)getTypeLoader().parse(s, CTFtnEdnRef.type, xmlOptions);
        }
        
        public static CTFtnEdnRef parse(final File file) throws XmlException, IOException {
            return (CTFtnEdnRef)getTypeLoader().parse(file, CTFtnEdnRef.type, (XmlOptions)null);
        }
        
        public static CTFtnEdnRef parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTFtnEdnRef)getTypeLoader().parse(file, CTFtnEdnRef.type, xmlOptions);
        }
        
        public static CTFtnEdnRef parse(final URL url) throws XmlException, IOException {
            return (CTFtnEdnRef)getTypeLoader().parse(url, CTFtnEdnRef.type, (XmlOptions)null);
        }
        
        public static CTFtnEdnRef parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTFtnEdnRef)getTypeLoader().parse(url, CTFtnEdnRef.type, xmlOptions);
        }
        
        public static CTFtnEdnRef parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTFtnEdnRef)getTypeLoader().parse(inputStream, CTFtnEdnRef.type, (XmlOptions)null);
        }
        
        public static CTFtnEdnRef parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTFtnEdnRef)getTypeLoader().parse(inputStream, CTFtnEdnRef.type, xmlOptions);
        }
        
        public static CTFtnEdnRef parse(final Reader reader) throws XmlException, IOException {
            return (CTFtnEdnRef)getTypeLoader().parse(reader, CTFtnEdnRef.type, (XmlOptions)null);
        }
        
        public static CTFtnEdnRef parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTFtnEdnRef)getTypeLoader().parse(reader, CTFtnEdnRef.type, xmlOptions);
        }
        
        public static CTFtnEdnRef parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTFtnEdnRef)getTypeLoader().parse(xmlStreamReader, CTFtnEdnRef.type, (XmlOptions)null);
        }
        
        public static CTFtnEdnRef parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTFtnEdnRef)getTypeLoader().parse(xmlStreamReader, CTFtnEdnRef.type, xmlOptions);
        }
        
        public static CTFtnEdnRef parse(final Node node) throws XmlException {
            return (CTFtnEdnRef)getTypeLoader().parse(node, CTFtnEdnRef.type, (XmlOptions)null);
        }
        
        public static CTFtnEdnRef parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTFtnEdnRef)getTypeLoader().parse(node, CTFtnEdnRef.type, xmlOptions);
        }
        
        @Deprecated
        public static CTFtnEdnRef parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTFtnEdnRef)getTypeLoader().parse(xmlInputStream, CTFtnEdnRef.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTFtnEdnRef parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTFtnEdnRef)getTypeLoader().parse(xmlInputStream, CTFtnEdnRef.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTFtnEdnRef.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTFtnEdnRef.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
