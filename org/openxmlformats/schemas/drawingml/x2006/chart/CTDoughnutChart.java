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
import java.util.List;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTDoughnutChart extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTDoughnutChart.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctdoughnutchartc12atype");
    
    CTBoolean getVaryColors();
    
    boolean isSetVaryColors();
    
    void setVaryColors(final CTBoolean p0);
    
    CTBoolean addNewVaryColors();
    
    void unsetVaryColors();
    
    List<CTPieSer> getSerList();
    
    @Deprecated
    CTPieSer[] getSerArray();
    
    CTPieSer getSerArray(final int p0);
    
    int sizeOfSerArray();
    
    void setSerArray(final CTPieSer[] p0);
    
    void setSerArray(final int p0, final CTPieSer p1);
    
    CTPieSer insertNewSer(final int p0);
    
    CTPieSer addNewSer();
    
    void removeSer(final int p0);
    
    CTDLbls getDLbls();
    
    boolean isSetDLbls();
    
    void setDLbls(final CTDLbls p0);
    
    CTDLbls addNewDLbls();
    
    void unsetDLbls();
    
    CTFirstSliceAng getFirstSliceAng();
    
    boolean isSetFirstSliceAng();
    
    void setFirstSliceAng(final CTFirstSliceAng p0);
    
    CTFirstSliceAng addNewFirstSliceAng();
    
    void unsetFirstSliceAng();
    
    CTHoleSize getHoleSize();
    
    boolean isSetHoleSize();
    
    void setHoleSize(final CTHoleSize p0);
    
    CTHoleSize addNewHoleSize();
    
    void unsetHoleSize();
    
    CTExtensionList getExtLst();
    
    boolean isSetExtLst();
    
    void setExtLst(final CTExtensionList p0);
    
    CTExtensionList addNewExtLst();
    
    void unsetExtLst();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTDoughnutChart.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTDoughnutChart newInstance() {
            return (CTDoughnutChart)getTypeLoader().newInstance(CTDoughnutChart.type, (XmlOptions)null);
        }
        
        public static CTDoughnutChart newInstance(final XmlOptions xmlOptions) {
            return (CTDoughnutChart)getTypeLoader().newInstance(CTDoughnutChart.type, xmlOptions);
        }
        
        public static CTDoughnutChart parse(final String s) throws XmlException {
            return (CTDoughnutChart)getTypeLoader().parse(s, CTDoughnutChart.type, (XmlOptions)null);
        }
        
        public static CTDoughnutChart parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTDoughnutChart)getTypeLoader().parse(s, CTDoughnutChart.type, xmlOptions);
        }
        
        public static CTDoughnutChart parse(final File file) throws XmlException, IOException {
            return (CTDoughnutChart)getTypeLoader().parse(file, CTDoughnutChart.type, (XmlOptions)null);
        }
        
        public static CTDoughnutChart parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTDoughnutChart)getTypeLoader().parse(file, CTDoughnutChart.type, xmlOptions);
        }
        
        public static CTDoughnutChart parse(final URL url) throws XmlException, IOException {
            return (CTDoughnutChart)getTypeLoader().parse(url, CTDoughnutChart.type, (XmlOptions)null);
        }
        
        public static CTDoughnutChart parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTDoughnutChart)getTypeLoader().parse(url, CTDoughnutChart.type, xmlOptions);
        }
        
        public static CTDoughnutChart parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTDoughnutChart)getTypeLoader().parse(inputStream, CTDoughnutChart.type, (XmlOptions)null);
        }
        
        public static CTDoughnutChart parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTDoughnutChart)getTypeLoader().parse(inputStream, CTDoughnutChart.type, xmlOptions);
        }
        
        public static CTDoughnutChart parse(final Reader reader) throws XmlException, IOException {
            return (CTDoughnutChart)getTypeLoader().parse(reader, CTDoughnutChart.type, (XmlOptions)null);
        }
        
        public static CTDoughnutChart parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTDoughnutChart)getTypeLoader().parse(reader, CTDoughnutChart.type, xmlOptions);
        }
        
        public static CTDoughnutChart parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTDoughnutChart)getTypeLoader().parse(xmlStreamReader, CTDoughnutChart.type, (XmlOptions)null);
        }
        
        public static CTDoughnutChart parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTDoughnutChart)getTypeLoader().parse(xmlStreamReader, CTDoughnutChart.type, xmlOptions);
        }
        
        public static CTDoughnutChart parse(final Node node) throws XmlException {
            return (CTDoughnutChart)getTypeLoader().parse(node, CTDoughnutChart.type, (XmlOptions)null);
        }
        
        public static CTDoughnutChart parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTDoughnutChart)getTypeLoader().parse(node, CTDoughnutChart.type, xmlOptions);
        }
        
        @Deprecated
        public static CTDoughnutChart parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTDoughnutChart)getTypeLoader().parse(xmlInputStream, CTDoughnutChart.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTDoughnutChart parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTDoughnutChart)getTypeLoader().parse(xmlInputStream, CTDoughnutChart.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTDoughnutChart.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTDoughnutChart.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
