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
import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTPageSetUpPr extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTPageSetUpPr.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctpagesetuppr24cftype");
    
    boolean getAutoPageBreaks();
    
    XmlBoolean xgetAutoPageBreaks();
    
    boolean isSetAutoPageBreaks();
    
    void setAutoPageBreaks(final boolean p0);
    
    void xsetAutoPageBreaks(final XmlBoolean p0);
    
    void unsetAutoPageBreaks();
    
    boolean getFitToPage();
    
    XmlBoolean xgetFitToPage();
    
    boolean isSetFitToPage();
    
    void setFitToPage(final boolean p0);
    
    void xsetFitToPage(final XmlBoolean p0);
    
    void unsetFitToPage();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTPageSetUpPr.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTPageSetUpPr newInstance() {
            return (CTPageSetUpPr)getTypeLoader().newInstance(CTPageSetUpPr.type, (XmlOptions)null);
        }
        
        public static CTPageSetUpPr newInstance(final XmlOptions xmlOptions) {
            return (CTPageSetUpPr)getTypeLoader().newInstance(CTPageSetUpPr.type, xmlOptions);
        }
        
        public static CTPageSetUpPr parse(final String s) throws XmlException {
            return (CTPageSetUpPr)getTypeLoader().parse(s, CTPageSetUpPr.type, (XmlOptions)null);
        }
        
        public static CTPageSetUpPr parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTPageSetUpPr)getTypeLoader().parse(s, CTPageSetUpPr.type, xmlOptions);
        }
        
        public static CTPageSetUpPr parse(final File file) throws XmlException, IOException {
            return (CTPageSetUpPr)getTypeLoader().parse(file, CTPageSetUpPr.type, (XmlOptions)null);
        }
        
        public static CTPageSetUpPr parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTPageSetUpPr)getTypeLoader().parse(file, CTPageSetUpPr.type, xmlOptions);
        }
        
        public static CTPageSetUpPr parse(final URL url) throws XmlException, IOException {
            return (CTPageSetUpPr)getTypeLoader().parse(url, CTPageSetUpPr.type, (XmlOptions)null);
        }
        
        public static CTPageSetUpPr parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTPageSetUpPr)getTypeLoader().parse(url, CTPageSetUpPr.type, xmlOptions);
        }
        
        public static CTPageSetUpPr parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTPageSetUpPr)getTypeLoader().parse(inputStream, CTPageSetUpPr.type, (XmlOptions)null);
        }
        
        public static CTPageSetUpPr parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTPageSetUpPr)getTypeLoader().parse(inputStream, CTPageSetUpPr.type, xmlOptions);
        }
        
        public static CTPageSetUpPr parse(final Reader reader) throws XmlException, IOException {
            return (CTPageSetUpPr)getTypeLoader().parse(reader, CTPageSetUpPr.type, (XmlOptions)null);
        }
        
        public static CTPageSetUpPr parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTPageSetUpPr)getTypeLoader().parse(reader, CTPageSetUpPr.type, xmlOptions);
        }
        
        public static CTPageSetUpPr parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTPageSetUpPr)getTypeLoader().parse(xmlStreamReader, CTPageSetUpPr.type, (XmlOptions)null);
        }
        
        public static CTPageSetUpPr parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTPageSetUpPr)getTypeLoader().parse(xmlStreamReader, CTPageSetUpPr.type, xmlOptions);
        }
        
        public static CTPageSetUpPr parse(final Node node) throws XmlException {
            return (CTPageSetUpPr)getTypeLoader().parse(node, CTPageSetUpPr.type, (XmlOptions)null);
        }
        
        public static CTPageSetUpPr parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTPageSetUpPr)getTypeLoader().parse(node, CTPageSetUpPr.type, xmlOptions);
        }
        
        @Deprecated
        public static CTPageSetUpPr parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTPageSetUpPr)getTypeLoader().parse(xmlInputStream, CTPageSetUpPr.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTPageSetUpPr parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTPageSetUpPr)getTypeLoader().parse(xmlInputStream, CTPageSetUpPr.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTPageSetUpPr.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTPageSetUpPr.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
