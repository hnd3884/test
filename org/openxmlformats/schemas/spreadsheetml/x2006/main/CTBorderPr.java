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

public interface CTBorderPr extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTBorderPr.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctborderpre497type");
    
    CTColor getColor();
    
    boolean isSetColor();
    
    void setColor(final CTColor p0);
    
    CTColor addNewColor();
    
    void unsetColor();
    
    STBorderStyle.Enum getStyle();
    
    STBorderStyle xgetStyle();
    
    boolean isSetStyle();
    
    void setStyle(final STBorderStyle.Enum p0);
    
    void xsetStyle(final STBorderStyle p0);
    
    void unsetStyle();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTBorderPr.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTBorderPr newInstance() {
            return (CTBorderPr)getTypeLoader().newInstance(CTBorderPr.type, (XmlOptions)null);
        }
        
        public static CTBorderPr newInstance(final XmlOptions xmlOptions) {
            return (CTBorderPr)getTypeLoader().newInstance(CTBorderPr.type, xmlOptions);
        }
        
        public static CTBorderPr parse(final String s) throws XmlException {
            return (CTBorderPr)getTypeLoader().parse(s, CTBorderPr.type, (XmlOptions)null);
        }
        
        public static CTBorderPr parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTBorderPr)getTypeLoader().parse(s, CTBorderPr.type, xmlOptions);
        }
        
        public static CTBorderPr parse(final File file) throws XmlException, IOException {
            return (CTBorderPr)getTypeLoader().parse(file, CTBorderPr.type, (XmlOptions)null);
        }
        
        public static CTBorderPr parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTBorderPr)getTypeLoader().parse(file, CTBorderPr.type, xmlOptions);
        }
        
        public static CTBorderPr parse(final URL url) throws XmlException, IOException {
            return (CTBorderPr)getTypeLoader().parse(url, CTBorderPr.type, (XmlOptions)null);
        }
        
        public static CTBorderPr parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTBorderPr)getTypeLoader().parse(url, CTBorderPr.type, xmlOptions);
        }
        
        public static CTBorderPr parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTBorderPr)getTypeLoader().parse(inputStream, CTBorderPr.type, (XmlOptions)null);
        }
        
        public static CTBorderPr parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTBorderPr)getTypeLoader().parse(inputStream, CTBorderPr.type, xmlOptions);
        }
        
        public static CTBorderPr parse(final Reader reader) throws XmlException, IOException {
            return (CTBorderPr)getTypeLoader().parse(reader, CTBorderPr.type, (XmlOptions)null);
        }
        
        public static CTBorderPr parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTBorderPr)getTypeLoader().parse(reader, CTBorderPr.type, xmlOptions);
        }
        
        public static CTBorderPr parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTBorderPr)getTypeLoader().parse(xmlStreamReader, CTBorderPr.type, (XmlOptions)null);
        }
        
        public static CTBorderPr parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTBorderPr)getTypeLoader().parse(xmlStreamReader, CTBorderPr.type, xmlOptions);
        }
        
        public static CTBorderPr parse(final Node node) throws XmlException {
            return (CTBorderPr)getTypeLoader().parse(node, CTBorderPr.type, (XmlOptions)null);
        }
        
        public static CTBorderPr parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTBorderPr)getTypeLoader().parse(node, CTBorderPr.type, xmlOptions);
        }
        
        @Deprecated
        public static CTBorderPr parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTBorderPr)getTypeLoader().parse(xmlInputStream, CTBorderPr.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTBorderPr parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTBorderPr)getTypeLoader().parse(xmlInputStream, CTBorderPr.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTBorderPr.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTBorderPr.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
