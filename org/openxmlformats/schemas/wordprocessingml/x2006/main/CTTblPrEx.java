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

public interface CTTblPrEx extends CTTblPrExBase
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTTblPrEx.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("cttblprex863ftype");
    
    CTTblPrExChange getTblPrExChange();
    
    boolean isSetTblPrExChange();
    
    void setTblPrExChange(final CTTblPrExChange p0);
    
    CTTblPrExChange addNewTblPrExChange();
    
    void unsetTblPrExChange();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTTblPrEx.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTTblPrEx newInstance() {
            return (CTTblPrEx)getTypeLoader().newInstance(CTTblPrEx.type, (XmlOptions)null);
        }
        
        public static CTTblPrEx newInstance(final XmlOptions xmlOptions) {
            return (CTTblPrEx)getTypeLoader().newInstance(CTTblPrEx.type, xmlOptions);
        }
        
        public static CTTblPrEx parse(final String s) throws XmlException {
            return (CTTblPrEx)getTypeLoader().parse(s, CTTblPrEx.type, (XmlOptions)null);
        }
        
        public static CTTblPrEx parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTTblPrEx)getTypeLoader().parse(s, CTTblPrEx.type, xmlOptions);
        }
        
        public static CTTblPrEx parse(final File file) throws XmlException, IOException {
            return (CTTblPrEx)getTypeLoader().parse(file, CTTblPrEx.type, (XmlOptions)null);
        }
        
        public static CTTblPrEx parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTblPrEx)getTypeLoader().parse(file, CTTblPrEx.type, xmlOptions);
        }
        
        public static CTTblPrEx parse(final URL url) throws XmlException, IOException {
            return (CTTblPrEx)getTypeLoader().parse(url, CTTblPrEx.type, (XmlOptions)null);
        }
        
        public static CTTblPrEx parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTblPrEx)getTypeLoader().parse(url, CTTblPrEx.type, xmlOptions);
        }
        
        public static CTTblPrEx parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTTblPrEx)getTypeLoader().parse(inputStream, CTTblPrEx.type, (XmlOptions)null);
        }
        
        public static CTTblPrEx parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTblPrEx)getTypeLoader().parse(inputStream, CTTblPrEx.type, xmlOptions);
        }
        
        public static CTTblPrEx parse(final Reader reader) throws XmlException, IOException {
            return (CTTblPrEx)getTypeLoader().parse(reader, CTTblPrEx.type, (XmlOptions)null);
        }
        
        public static CTTblPrEx parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTblPrEx)getTypeLoader().parse(reader, CTTblPrEx.type, xmlOptions);
        }
        
        public static CTTblPrEx parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTTblPrEx)getTypeLoader().parse(xmlStreamReader, CTTblPrEx.type, (XmlOptions)null);
        }
        
        public static CTTblPrEx parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTTblPrEx)getTypeLoader().parse(xmlStreamReader, CTTblPrEx.type, xmlOptions);
        }
        
        public static CTTblPrEx parse(final Node node) throws XmlException {
            return (CTTblPrEx)getTypeLoader().parse(node, CTTblPrEx.type, (XmlOptions)null);
        }
        
        public static CTTblPrEx parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTTblPrEx)getTypeLoader().parse(node, CTTblPrEx.type, xmlOptions);
        }
        
        @Deprecated
        public static CTTblPrEx parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTTblPrEx)getTypeLoader().parse(xmlInputStream, CTTblPrEx.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTTblPrEx parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTTblPrEx)getTypeLoader().parse(xmlInputStream, CTTblPrEx.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTTblPrEx.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTTblPrEx.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
