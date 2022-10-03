package org.openxmlformats.schemas.spreadsheetml.x2006.main;

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

public interface CTFill extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTFill.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctfill550ctype");
    
    CTPatternFill getPatternFill();
    
    boolean isSetPatternFill();
    
    void setPatternFill(final CTPatternFill p0);
    
    CTPatternFill addNewPatternFill();
    
    void unsetPatternFill();
    
    CTGradientFill getGradientFill();
    
    boolean isSetGradientFill();
    
    void setGradientFill(final CTGradientFill p0);
    
    CTGradientFill addNewGradientFill();
    
    void unsetGradientFill();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTFill.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTFill newInstance() {
            return (CTFill)getTypeLoader().newInstance(CTFill.type, (XmlOptions)null);
        }
        
        public static CTFill newInstance(final XmlOptions xmlOptions) {
            return (CTFill)getTypeLoader().newInstance(CTFill.type, xmlOptions);
        }
        
        public static CTFill parse(final String s) throws XmlException {
            return (CTFill)getTypeLoader().parse(s, CTFill.type, (XmlOptions)null);
        }
        
        public static CTFill parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTFill)getTypeLoader().parse(s, CTFill.type, xmlOptions);
        }
        
        public static CTFill parse(final File file) throws XmlException, IOException {
            return (CTFill)getTypeLoader().parse(file, CTFill.type, (XmlOptions)null);
        }
        
        public static CTFill parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTFill)getTypeLoader().parse(file, CTFill.type, xmlOptions);
        }
        
        public static CTFill parse(final URL url) throws XmlException, IOException {
            return (CTFill)getTypeLoader().parse(url, CTFill.type, (XmlOptions)null);
        }
        
        public static CTFill parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTFill)getTypeLoader().parse(url, CTFill.type, xmlOptions);
        }
        
        public static CTFill parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTFill)getTypeLoader().parse(inputStream, CTFill.type, (XmlOptions)null);
        }
        
        public static CTFill parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTFill)getTypeLoader().parse(inputStream, CTFill.type, xmlOptions);
        }
        
        public static CTFill parse(final Reader reader) throws XmlException, IOException {
            return (CTFill)getTypeLoader().parse(reader, CTFill.type, (XmlOptions)null);
        }
        
        public static CTFill parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTFill)getTypeLoader().parse(reader, CTFill.type, xmlOptions);
        }
        
        public static CTFill parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTFill)getTypeLoader().parse(xmlStreamReader, CTFill.type, (XmlOptions)null);
        }
        
        public static CTFill parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTFill)getTypeLoader().parse(xmlStreamReader, CTFill.type, xmlOptions);
        }
        
        public static CTFill parse(final Node node) throws XmlException {
            return (CTFill)getTypeLoader().parse(node, CTFill.type, (XmlOptions)null);
        }
        
        public static CTFill parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTFill)getTypeLoader().parse(node, CTFill.type, xmlOptions);
        }
        
        @Deprecated
        public static CTFill parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTFill)getTypeLoader().parse(xmlInputStream, CTFill.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTFill parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTFill)getTypeLoader().parse(xmlInputStream, CTFill.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTFill.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTFill.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
