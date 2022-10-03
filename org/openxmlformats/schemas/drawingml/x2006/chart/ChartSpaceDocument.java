package org.openxmlformats.schemas.drawingml.x2006.chart;

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

public interface ChartSpaceDocument extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(ChartSpaceDocument.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("chartspace36e0doctype");
    
    CTChartSpace getChartSpace();
    
    void setChartSpace(final CTChartSpace p0);
    
    CTChartSpace addNewChartSpace();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(ChartSpaceDocument.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static ChartSpaceDocument newInstance() {
            return (ChartSpaceDocument)getTypeLoader().newInstance(ChartSpaceDocument.type, (XmlOptions)null);
        }
        
        public static ChartSpaceDocument newInstance(final XmlOptions xmlOptions) {
            return (ChartSpaceDocument)getTypeLoader().newInstance(ChartSpaceDocument.type, xmlOptions);
        }
        
        public static ChartSpaceDocument parse(final String s) throws XmlException {
            return (ChartSpaceDocument)getTypeLoader().parse(s, ChartSpaceDocument.type, (XmlOptions)null);
        }
        
        public static ChartSpaceDocument parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (ChartSpaceDocument)getTypeLoader().parse(s, ChartSpaceDocument.type, xmlOptions);
        }
        
        public static ChartSpaceDocument parse(final File file) throws XmlException, IOException {
            return (ChartSpaceDocument)getTypeLoader().parse(file, ChartSpaceDocument.type, (XmlOptions)null);
        }
        
        public static ChartSpaceDocument parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (ChartSpaceDocument)getTypeLoader().parse(file, ChartSpaceDocument.type, xmlOptions);
        }
        
        public static ChartSpaceDocument parse(final URL url) throws XmlException, IOException {
            return (ChartSpaceDocument)getTypeLoader().parse(url, ChartSpaceDocument.type, (XmlOptions)null);
        }
        
        public static ChartSpaceDocument parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (ChartSpaceDocument)getTypeLoader().parse(url, ChartSpaceDocument.type, xmlOptions);
        }
        
        public static ChartSpaceDocument parse(final InputStream inputStream) throws XmlException, IOException {
            return (ChartSpaceDocument)getTypeLoader().parse(inputStream, ChartSpaceDocument.type, (XmlOptions)null);
        }
        
        public static ChartSpaceDocument parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (ChartSpaceDocument)getTypeLoader().parse(inputStream, ChartSpaceDocument.type, xmlOptions);
        }
        
        public static ChartSpaceDocument parse(final Reader reader) throws XmlException, IOException {
            return (ChartSpaceDocument)getTypeLoader().parse(reader, ChartSpaceDocument.type, (XmlOptions)null);
        }
        
        public static ChartSpaceDocument parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (ChartSpaceDocument)getTypeLoader().parse(reader, ChartSpaceDocument.type, xmlOptions);
        }
        
        public static ChartSpaceDocument parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (ChartSpaceDocument)getTypeLoader().parse(xmlStreamReader, ChartSpaceDocument.type, (XmlOptions)null);
        }
        
        public static ChartSpaceDocument parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (ChartSpaceDocument)getTypeLoader().parse(xmlStreamReader, ChartSpaceDocument.type, xmlOptions);
        }
        
        public static ChartSpaceDocument parse(final Node node) throws XmlException {
            return (ChartSpaceDocument)getTypeLoader().parse(node, ChartSpaceDocument.type, (XmlOptions)null);
        }
        
        public static ChartSpaceDocument parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (ChartSpaceDocument)getTypeLoader().parse(node, ChartSpaceDocument.type, xmlOptions);
        }
        
        @Deprecated
        public static ChartSpaceDocument parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (ChartSpaceDocument)getTypeLoader().parse(xmlInputStream, ChartSpaceDocument.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static ChartSpaceDocument parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (ChartSpaceDocument)getTypeLoader().parse(xmlInputStream, ChartSpaceDocument.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, ChartSpaceDocument.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, ChartSpaceDocument.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
