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

public interface CTLineChart extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTLineChart.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctlinechart249ctype");
    
    CTGrouping getGrouping();
    
    void setGrouping(final CTGrouping p0);
    
    CTGrouping addNewGrouping();
    
    CTBoolean getVaryColors();
    
    boolean isSetVaryColors();
    
    void setVaryColors(final CTBoolean p0);
    
    CTBoolean addNewVaryColors();
    
    void unsetVaryColors();
    
    List<CTLineSer> getSerList();
    
    @Deprecated
    CTLineSer[] getSerArray();
    
    CTLineSer getSerArray(final int p0);
    
    int sizeOfSerArray();
    
    void setSerArray(final CTLineSer[] p0);
    
    void setSerArray(final int p0, final CTLineSer p1);
    
    CTLineSer insertNewSer(final int p0);
    
    CTLineSer addNewSer();
    
    void removeSer(final int p0);
    
    CTDLbls getDLbls();
    
    boolean isSetDLbls();
    
    void setDLbls(final CTDLbls p0);
    
    CTDLbls addNewDLbls();
    
    void unsetDLbls();
    
    CTChartLines getDropLines();
    
    boolean isSetDropLines();
    
    void setDropLines(final CTChartLines p0);
    
    CTChartLines addNewDropLines();
    
    void unsetDropLines();
    
    CTChartLines getHiLowLines();
    
    boolean isSetHiLowLines();
    
    void setHiLowLines(final CTChartLines p0);
    
    CTChartLines addNewHiLowLines();
    
    void unsetHiLowLines();
    
    CTUpDownBars getUpDownBars();
    
    boolean isSetUpDownBars();
    
    void setUpDownBars(final CTUpDownBars p0);
    
    CTUpDownBars addNewUpDownBars();
    
    void unsetUpDownBars();
    
    CTBoolean getMarker();
    
    boolean isSetMarker();
    
    void setMarker(final CTBoolean p0);
    
    CTBoolean addNewMarker();
    
    void unsetMarker();
    
    CTBoolean getSmooth();
    
    boolean isSetSmooth();
    
    void setSmooth(final CTBoolean p0);
    
    CTBoolean addNewSmooth();
    
    void unsetSmooth();
    
    List<CTUnsignedInt> getAxIdList();
    
    @Deprecated
    CTUnsignedInt[] getAxIdArray();
    
    CTUnsignedInt getAxIdArray(final int p0);
    
    int sizeOfAxIdArray();
    
    void setAxIdArray(final CTUnsignedInt[] p0);
    
    void setAxIdArray(final int p0, final CTUnsignedInt p1);
    
    CTUnsignedInt insertNewAxId(final int p0);
    
    CTUnsignedInt addNewAxId();
    
    void removeAxId(final int p0);
    
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
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTLineChart.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTLineChart newInstance() {
            return (CTLineChart)getTypeLoader().newInstance(CTLineChart.type, (XmlOptions)null);
        }
        
        public static CTLineChart newInstance(final XmlOptions xmlOptions) {
            return (CTLineChart)getTypeLoader().newInstance(CTLineChart.type, xmlOptions);
        }
        
        public static CTLineChart parse(final String s) throws XmlException {
            return (CTLineChart)getTypeLoader().parse(s, CTLineChart.type, (XmlOptions)null);
        }
        
        public static CTLineChart parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTLineChart)getTypeLoader().parse(s, CTLineChart.type, xmlOptions);
        }
        
        public static CTLineChart parse(final File file) throws XmlException, IOException {
            return (CTLineChart)getTypeLoader().parse(file, CTLineChart.type, (XmlOptions)null);
        }
        
        public static CTLineChart parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTLineChart)getTypeLoader().parse(file, CTLineChart.type, xmlOptions);
        }
        
        public static CTLineChart parse(final URL url) throws XmlException, IOException {
            return (CTLineChart)getTypeLoader().parse(url, CTLineChart.type, (XmlOptions)null);
        }
        
        public static CTLineChart parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTLineChart)getTypeLoader().parse(url, CTLineChart.type, xmlOptions);
        }
        
        public static CTLineChart parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTLineChart)getTypeLoader().parse(inputStream, CTLineChart.type, (XmlOptions)null);
        }
        
        public static CTLineChart parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTLineChart)getTypeLoader().parse(inputStream, CTLineChart.type, xmlOptions);
        }
        
        public static CTLineChart parse(final Reader reader) throws XmlException, IOException {
            return (CTLineChart)getTypeLoader().parse(reader, CTLineChart.type, (XmlOptions)null);
        }
        
        public static CTLineChart parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTLineChart)getTypeLoader().parse(reader, CTLineChart.type, xmlOptions);
        }
        
        public static CTLineChart parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTLineChart)getTypeLoader().parse(xmlStreamReader, CTLineChart.type, (XmlOptions)null);
        }
        
        public static CTLineChart parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTLineChart)getTypeLoader().parse(xmlStreamReader, CTLineChart.type, xmlOptions);
        }
        
        public static CTLineChart parse(final Node node) throws XmlException {
            return (CTLineChart)getTypeLoader().parse(node, CTLineChart.type, (XmlOptions)null);
        }
        
        public static CTLineChart parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTLineChart)getTypeLoader().parse(node, CTLineChart.type, xmlOptions);
        }
        
        @Deprecated
        public static CTLineChart parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTLineChart)getTypeLoader().parse(xmlInputStream, CTLineChart.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTLineChart parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTLineChart)getTypeLoader().parse(xmlInputStream, CTLineChart.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTLineChart.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTLineChart.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
