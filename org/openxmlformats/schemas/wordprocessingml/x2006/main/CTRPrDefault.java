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

public interface CTRPrDefault extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTRPrDefault.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctrprdefault5ebbtype");
    
    CTRPr getRPr();
    
    boolean isSetRPr();
    
    void setRPr(final CTRPr p0);
    
    CTRPr addNewRPr();
    
    void unsetRPr();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTRPrDefault.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTRPrDefault newInstance() {
            return (CTRPrDefault)getTypeLoader().newInstance(CTRPrDefault.type, (XmlOptions)null);
        }
        
        public static CTRPrDefault newInstance(final XmlOptions xmlOptions) {
            return (CTRPrDefault)getTypeLoader().newInstance(CTRPrDefault.type, xmlOptions);
        }
        
        public static CTRPrDefault parse(final String s) throws XmlException {
            return (CTRPrDefault)getTypeLoader().parse(s, CTRPrDefault.type, (XmlOptions)null);
        }
        
        public static CTRPrDefault parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTRPrDefault)getTypeLoader().parse(s, CTRPrDefault.type, xmlOptions);
        }
        
        public static CTRPrDefault parse(final File file) throws XmlException, IOException {
            return (CTRPrDefault)getTypeLoader().parse(file, CTRPrDefault.type, (XmlOptions)null);
        }
        
        public static CTRPrDefault parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTRPrDefault)getTypeLoader().parse(file, CTRPrDefault.type, xmlOptions);
        }
        
        public static CTRPrDefault parse(final URL url) throws XmlException, IOException {
            return (CTRPrDefault)getTypeLoader().parse(url, CTRPrDefault.type, (XmlOptions)null);
        }
        
        public static CTRPrDefault parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTRPrDefault)getTypeLoader().parse(url, CTRPrDefault.type, xmlOptions);
        }
        
        public static CTRPrDefault parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTRPrDefault)getTypeLoader().parse(inputStream, CTRPrDefault.type, (XmlOptions)null);
        }
        
        public static CTRPrDefault parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTRPrDefault)getTypeLoader().parse(inputStream, CTRPrDefault.type, xmlOptions);
        }
        
        public static CTRPrDefault parse(final Reader reader) throws XmlException, IOException {
            return (CTRPrDefault)getTypeLoader().parse(reader, CTRPrDefault.type, (XmlOptions)null);
        }
        
        public static CTRPrDefault parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTRPrDefault)getTypeLoader().parse(reader, CTRPrDefault.type, xmlOptions);
        }
        
        public static CTRPrDefault parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTRPrDefault)getTypeLoader().parse(xmlStreamReader, CTRPrDefault.type, (XmlOptions)null);
        }
        
        public static CTRPrDefault parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTRPrDefault)getTypeLoader().parse(xmlStreamReader, CTRPrDefault.type, xmlOptions);
        }
        
        public static CTRPrDefault parse(final Node node) throws XmlException {
            return (CTRPrDefault)getTypeLoader().parse(node, CTRPrDefault.type, (XmlOptions)null);
        }
        
        public static CTRPrDefault parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTRPrDefault)getTypeLoader().parse(node, CTRPrDefault.type, xmlOptions);
        }
        
        @Deprecated
        public static CTRPrDefault parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTRPrDefault)getTypeLoader().parse(xmlInputStream, CTRPrDefault.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTRPrDefault parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTRPrDefault)getTypeLoader().parse(xmlInputStream, CTRPrDefault.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTRPrDefault.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTRPrDefault.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
