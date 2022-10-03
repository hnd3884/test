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

public interface CTTcPr extends CTTcPrInner
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTTcPr.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("cttcpree37type");
    
    CTTcPrChange getTcPrChange();
    
    boolean isSetTcPrChange();
    
    void setTcPrChange(final CTTcPrChange p0);
    
    CTTcPrChange addNewTcPrChange();
    
    void unsetTcPrChange();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTTcPr.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTTcPr newInstance() {
            return (CTTcPr)getTypeLoader().newInstance(CTTcPr.type, (XmlOptions)null);
        }
        
        public static CTTcPr newInstance(final XmlOptions xmlOptions) {
            return (CTTcPr)getTypeLoader().newInstance(CTTcPr.type, xmlOptions);
        }
        
        public static CTTcPr parse(final String s) throws XmlException {
            return (CTTcPr)getTypeLoader().parse(s, CTTcPr.type, (XmlOptions)null);
        }
        
        public static CTTcPr parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTTcPr)getTypeLoader().parse(s, CTTcPr.type, xmlOptions);
        }
        
        public static CTTcPr parse(final File file) throws XmlException, IOException {
            return (CTTcPr)getTypeLoader().parse(file, CTTcPr.type, (XmlOptions)null);
        }
        
        public static CTTcPr parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTcPr)getTypeLoader().parse(file, CTTcPr.type, xmlOptions);
        }
        
        public static CTTcPr parse(final URL url) throws XmlException, IOException {
            return (CTTcPr)getTypeLoader().parse(url, CTTcPr.type, (XmlOptions)null);
        }
        
        public static CTTcPr parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTcPr)getTypeLoader().parse(url, CTTcPr.type, xmlOptions);
        }
        
        public static CTTcPr parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTTcPr)getTypeLoader().parse(inputStream, CTTcPr.type, (XmlOptions)null);
        }
        
        public static CTTcPr parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTcPr)getTypeLoader().parse(inputStream, CTTcPr.type, xmlOptions);
        }
        
        public static CTTcPr parse(final Reader reader) throws XmlException, IOException {
            return (CTTcPr)getTypeLoader().parse(reader, CTTcPr.type, (XmlOptions)null);
        }
        
        public static CTTcPr parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTcPr)getTypeLoader().parse(reader, CTTcPr.type, xmlOptions);
        }
        
        public static CTTcPr parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTTcPr)getTypeLoader().parse(xmlStreamReader, CTTcPr.type, (XmlOptions)null);
        }
        
        public static CTTcPr parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTTcPr)getTypeLoader().parse(xmlStreamReader, CTTcPr.type, xmlOptions);
        }
        
        public static CTTcPr parse(final Node node) throws XmlException {
            return (CTTcPr)getTypeLoader().parse(node, CTTcPr.type, (XmlOptions)null);
        }
        
        public static CTTcPr parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTTcPr)getTypeLoader().parse(node, CTTcPr.type, xmlOptions);
        }
        
        @Deprecated
        public static CTTcPr parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTTcPr)getTypeLoader().parse(xmlInputStream, CTTcPr.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTTcPr parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTTcPr)getTypeLoader().parse(xmlInputStream, CTTcPr.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTTcPr.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTTcPr.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
