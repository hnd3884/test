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

public interface CTMarkup extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTMarkup.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctmarkup2d80type");
    
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
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTMarkup.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTMarkup newInstance() {
            return (CTMarkup)getTypeLoader().newInstance(CTMarkup.type, (XmlOptions)null);
        }
        
        public static CTMarkup newInstance(final XmlOptions xmlOptions) {
            return (CTMarkup)getTypeLoader().newInstance(CTMarkup.type, xmlOptions);
        }
        
        public static CTMarkup parse(final String s) throws XmlException {
            return (CTMarkup)getTypeLoader().parse(s, CTMarkup.type, (XmlOptions)null);
        }
        
        public static CTMarkup parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTMarkup)getTypeLoader().parse(s, CTMarkup.type, xmlOptions);
        }
        
        public static CTMarkup parse(final File file) throws XmlException, IOException {
            return (CTMarkup)getTypeLoader().parse(file, CTMarkup.type, (XmlOptions)null);
        }
        
        public static CTMarkup parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTMarkup)getTypeLoader().parse(file, CTMarkup.type, xmlOptions);
        }
        
        public static CTMarkup parse(final URL url) throws XmlException, IOException {
            return (CTMarkup)getTypeLoader().parse(url, CTMarkup.type, (XmlOptions)null);
        }
        
        public static CTMarkup parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTMarkup)getTypeLoader().parse(url, CTMarkup.type, xmlOptions);
        }
        
        public static CTMarkup parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTMarkup)getTypeLoader().parse(inputStream, CTMarkup.type, (XmlOptions)null);
        }
        
        public static CTMarkup parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTMarkup)getTypeLoader().parse(inputStream, CTMarkup.type, xmlOptions);
        }
        
        public static CTMarkup parse(final Reader reader) throws XmlException, IOException {
            return (CTMarkup)getTypeLoader().parse(reader, CTMarkup.type, (XmlOptions)null);
        }
        
        public static CTMarkup parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTMarkup)getTypeLoader().parse(reader, CTMarkup.type, xmlOptions);
        }
        
        public static CTMarkup parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTMarkup)getTypeLoader().parse(xmlStreamReader, CTMarkup.type, (XmlOptions)null);
        }
        
        public static CTMarkup parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTMarkup)getTypeLoader().parse(xmlStreamReader, CTMarkup.type, xmlOptions);
        }
        
        public static CTMarkup parse(final Node node) throws XmlException {
            return (CTMarkup)getTypeLoader().parse(node, CTMarkup.type, (XmlOptions)null);
        }
        
        public static CTMarkup parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTMarkup)getTypeLoader().parse(node, CTMarkup.type, xmlOptions);
        }
        
        @Deprecated
        public static CTMarkup parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTMarkup)getTypeLoader().parse(xmlInputStream, CTMarkup.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTMarkup parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTMarkup)getTypeLoader().parse(xmlInputStream, CTMarkup.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTMarkup.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTMarkup.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
