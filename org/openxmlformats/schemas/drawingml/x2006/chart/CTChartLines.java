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
import org.openxmlformats.schemas.drawingml.x2006.main.CTShapeProperties;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTChartLines extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTChartLines.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctchartlines979btype");
    
    CTShapeProperties getSpPr();
    
    boolean isSetSpPr();
    
    void setSpPr(final CTShapeProperties p0);
    
    CTShapeProperties addNewSpPr();
    
    void unsetSpPr();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTChartLines.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTChartLines newInstance() {
            return (CTChartLines)getTypeLoader().newInstance(CTChartLines.type, (XmlOptions)null);
        }
        
        public static CTChartLines newInstance(final XmlOptions xmlOptions) {
            return (CTChartLines)getTypeLoader().newInstance(CTChartLines.type, xmlOptions);
        }
        
        public static CTChartLines parse(final String s) throws XmlException {
            return (CTChartLines)getTypeLoader().parse(s, CTChartLines.type, (XmlOptions)null);
        }
        
        public static CTChartLines parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTChartLines)getTypeLoader().parse(s, CTChartLines.type, xmlOptions);
        }
        
        public static CTChartLines parse(final File file) throws XmlException, IOException {
            return (CTChartLines)getTypeLoader().parse(file, CTChartLines.type, (XmlOptions)null);
        }
        
        public static CTChartLines parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTChartLines)getTypeLoader().parse(file, CTChartLines.type, xmlOptions);
        }
        
        public static CTChartLines parse(final URL url) throws XmlException, IOException {
            return (CTChartLines)getTypeLoader().parse(url, CTChartLines.type, (XmlOptions)null);
        }
        
        public static CTChartLines parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTChartLines)getTypeLoader().parse(url, CTChartLines.type, xmlOptions);
        }
        
        public static CTChartLines parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTChartLines)getTypeLoader().parse(inputStream, CTChartLines.type, (XmlOptions)null);
        }
        
        public static CTChartLines parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTChartLines)getTypeLoader().parse(inputStream, CTChartLines.type, xmlOptions);
        }
        
        public static CTChartLines parse(final Reader reader) throws XmlException, IOException {
            return (CTChartLines)getTypeLoader().parse(reader, CTChartLines.type, (XmlOptions)null);
        }
        
        public static CTChartLines parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTChartLines)getTypeLoader().parse(reader, CTChartLines.type, xmlOptions);
        }
        
        public static CTChartLines parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTChartLines)getTypeLoader().parse(xmlStreamReader, CTChartLines.type, (XmlOptions)null);
        }
        
        public static CTChartLines parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTChartLines)getTypeLoader().parse(xmlStreamReader, CTChartLines.type, xmlOptions);
        }
        
        public static CTChartLines parse(final Node node) throws XmlException {
            return (CTChartLines)getTypeLoader().parse(node, CTChartLines.type, (XmlOptions)null);
        }
        
        public static CTChartLines parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTChartLines)getTypeLoader().parse(node, CTChartLines.type, xmlOptions);
        }
        
        @Deprecated
        public static CTChartLines parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTChartLines)getTypeLoader().parse(xmlInputStream, CTChartLines.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTChartLines parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTChartLines)getTypeLoader().parse(xmlInputStream, CTChartLines.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTChartLines.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTChartLines.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
