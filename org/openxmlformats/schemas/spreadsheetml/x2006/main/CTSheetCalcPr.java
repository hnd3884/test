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

public interface CTSheetCalcPr extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTSheetCalcPr.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctsheetcalcprc6d5type");
    
    boolean getFullCalcOnLoad();
    
    XmlBoolean xgetFullCalcOnLoad();
    
    boolean isSetFullCalcOnLoad();
    
    void setFullCalcOnLoad(final boolean p0);
    
    void xsetFullCalcOnLoad(final XmlBoolean p0);
    
    void unsetFullCalcOnLoad();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTSheetCalcPr.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTSheetCalcPr newInstance() {
            return (CTSheetCalcPr)getTypeLoader().newInstance(CTSheetCalcPr.type, (XmlOptions)null);
        }
        
        public static CTSheetCalcPr newInstance(final XmlOptions xmlOptions) {
            return (CTSheetCalcPr)getTypeLoader().newInstance(CTSheetCalcPr.type, xmlOptions);
        }
        
        public static CTSheetCalcPr parse(final String s) throws XmlException {
            return (CTSheetCalcPr)getTypeLoader().parse(s, CTSheetCalcPr.type, (XmlOptions)null);
        }
        
        public static CTSheetCalcPr parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTSheetCalcPr)getTypeLoader().parse(s, CTSheetCalcPr.type, xmlOptions);
        }
        
        public static CTSheetCalcPr parse(final File file) throws XmlException, IOException {
            return (CTSheetCalcPr)getTypeLoader().parse(file, CTSheetCalcPr.type, (XmlOptions)null);
        }
        
        public static CTSheetCalcPr parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTSheetCalcPr)getTypeLoader().parse(file, CTSheetCalcPr.type, xmlOptions);
        }
        
        public static CTSheetCalcPr parse(final URL url) throws XmlException, IOException {
            return (CTSheetCalcPr)getTypeLoader().parse(url, CTSheetCalcPr.type, (XmlOptions)null);
        }
        
        public static CTSheetCalcPr parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTSheetCalcPr)getTypeLoader().parse(url, CTSheetCalcPr.type, xmlOptions);
        }
        
        public static CTSheetCalcPr parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTSheetCalcPr)getTypeLoader().parse(inputStream, CTSheetCalcPr.type, (XmlOptions)null);
        }
        
        public static CTSheetCalcPr parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTSheetCalcPr)getTypeLoader().parse(inputStream, CTSheetCalcPr.type, xmlOptions);
        }
        
        public static CTSheetCalcPr parse(final Reader reader) throws XmlException, IOException {
            return (CTSheetCalcPr)getTypeLoader().parse(reader, CTSheetCalcPr.type, (XmlOptions)null);
        }
        
        public static CTSheetCalcPr parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTSheetCalcPr)getTypeLoader().parse(reader, CTSheetCalcPr.type, xmlOptions);
        }
        
        public static CTSheetCalcPr parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTSheetCalcPr)getTypeLoader().parse(xmlStreamReader, CTSheetCalcPr.type, (XmlOptions)null);
        }
        
        public static CTSheetCalcPr parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTSheetCalcPr)getTypeLoader().parse(xmlStreamReader, CTSheetCalcPr.type, xmlOptions);
        }
        
        public static CTSheetCalcPr parse(final Node node) throws XmlException {
            return (CTSheetCalcPr)getTypeLoader().parse(node, CTSheetCalcPr.type, (XmlOptions)null);
        }
        
        public static CTSheetCalcPr parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTSheetCalcPr)getTypeLoader().parse(node, CTSheetCalcPr.type, xmlOptions);
        }
        
        @Deprecated
        public static CTSheetCalcPr parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTSheetCalcPr)getTypeLoader().parse(xmlInputStream, CTSheetCalcPr.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTSheetCalcPr parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTSheetCalcPr)getTypeLoader().parse(xmlInputStream, CTSheetCalcPr.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTSheetCalcPr.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTSheetCalcPr.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
