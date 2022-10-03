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

public interface ChartsheetDocument extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(ChartsheetDocument.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("chartsheet99dedoctype");
    
    CTChartsheet getChartsheet();
    
    void setChartsheet(final CTChartsheet p0);
    
    CTChartsheet addNewChartsheet();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(ChartsheetDocument.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static ChartsheetDocument newInstance() {
            return (ChartsheetDocument)getTypeLoader().newInstance(ChartsheetDocument.type, (XmlOptions)null);
        }
        
        public static ChartsheetDocument newInstance(final XmlOptions xmlOptions) {
            return (ChartsheetDocument)getTypeLoader().newInstance(ChartsheetDocument.type, xmlOptions);
        }
        
        public static ChartsheetDocument parse(final String s) throws XmlException {
            return (ChartsheetDocument)getTypeLoader().parse(s, ChartsheetDocument.type, (XmlOptions)null);
        }
        
        public static ChartsheetDocument parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (ChartsheetDocument)getTypeLoader().parse(s, ChartsheetDocument.type, xmlOptions);
        }
        
        public static ChartsheetDocument parse(final File file) throws XmlException, IOException {
            return (ChartsheetDocument)getTypeLoader().parse(file, ChartsheetDocument.type, (XmlOptions)null);
        }
        
        public static ChartsheetDocument parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (ChartsheetDocument)getTypeLoader().parse(file, ChartsheetDocument.type, xmlOptions);
        }
        
        public static ChartsheetDocument parse(final URL url) throws XmlException, IOException {
            return (ChartsheetDocument)getTypeLoader().parse(url, ChartsheetDocument.type, (XmlOptions)null);
        }
        
        public static ChartsheetDocument parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (ChartsheetDocument)getTypeLoader().parse(url, ChartsheetDocument.type, xmlOptions);
        }
        
        public static ChartsheetDocument parse(final InputStream inputStream) throws XmlException, IOException {
            return (ChartsheetDocument)getTypeLoader().parse(inputStream, ChartsheetDocument.type, (XmlOptions)null);
        }
        
        public static ChartsheetDocument parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (ChartsheetDocument)getTypeLoader().parse(inputStream, ChartsheetDocument.type, xmlOptions);
        }
        
        public static ChartsheetDocument parse(final Reader reader) throws XmlException, IOException {
            return (ChartsheetDocument)getTypeLoader().parse(reader, ChartsheetDocument.type, (XmlOptions)null);
        }
        
        public static ChartsheetDocument parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (ChartsheetDocument)getTypeLoader().parse(reader, ChartsheetDocument.type, xmlOptions);
        }
        
        public static ChartsheetDocument parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (ChartsheetDocument)getTypeLoader().parse(xmlStreamReader, ChartsheetDocument.type, (XmlOptions)null);
        }
        
        public static ChartsheetDocument parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (ChartsheetDocument)getTypeLoader().parse(xmlStreamReader, ChartsheetDocument.type, xmlOptions);
        }
        
        public static ChartsheetDocument parse(final Node node) throws XmlException {
            return (ChartsheetDocument)getTypeLoader().parse(node, ChartsheetDocument.type, (XmlOptions)null);
        }
        
        public static ChartsheetDocument parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (ChartsheetDocument)getTypeLoader().parse(node, ChartsheetDocument.type, xmlOptions);
        }
        
        @Deprecated
        public static ChartsheetDocument parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (ChartsheetDocument)getTypeLoader().parse(xmlInputStream, ChartsheetDocument.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static ChartsheetDocument parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (ChartsheetDocument)getTypeLoader().parse(xmlInputStream, ChartsheetDocument.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, ChartsheetDocument.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, ChartsheetDocument.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
