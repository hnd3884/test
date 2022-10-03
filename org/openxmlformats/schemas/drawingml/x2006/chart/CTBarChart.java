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

public interface CTBarChart extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTBarChart.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctbarchart4151type");
    
    CTBarDir getBarDir();
    
    void setBarDir(final CTBarDir p0);
    
    CTBarDir addNewBarDir();
    
    CTBarGrouping getGrouping();
    
    boolean isSetGrouping();
    
    void setGrouping(final CTBarGrouping p0);
    
    CTBarGrouping addNewGrouping();
    
    void unsetGrouping();
    
    CTBoolean getVaryColors();
    
    boolean isSetVaryColors();
    
    void setVaryColors(final CTBoolean p0);
    
    CTBoolean addNewVaryColors();
    
    void unsetVaryColors();
    
    List<CTBarSer> getSerList();
    
    @Deprecated
    CTBarSer[] getSerArray();
    
    CTBarSer getSerArray(final int p0);
    
    int sizeOfSerArray();
    
    void setSerArray(final CTBarSer[] p0);
    
    void setSerArray(final int p0, final CTBarSer p1);
    
    CTBarSer insertNewSer(final int p0);
    
    CTBarSer addNewSer();
    
    void removeSer(final int p0);
    
    CTDLbls getDLbls();
    
    boolean isSetDLbls();
    
    void setDLbls(final CTDLbls p0);
    
    CTDLbls addNewDLbls();
    
    void unsetDLbls();
    
    CTGapAmount getGapWidth();
    
    boolean isSetGapWidth();
    
    void setGapWidth(final CTGapAmount p0);
    
    CTGapAmount addNewGapWidth();
    
    void unsetGapWidth();
    
    CTOverlap getOverlap();
    
    boolean isSetOverlap();
    
    void setOverlap(final CTOverlap p0);
    
    CTOverlap addNewOverlap();
    
    void unsetOverlap();
    
    List<CTChartLines> getSerLinesList();
    
    @Deprecated
    CTChartLines[] getSerLinesArray();
    
    CTChartLines getSerLinesArray(final int p0);
    
    int sizeOfSerLinesArray();
    
    void setSerLinesArray(final CTChartLines[] p0);
    
    void setSerLinesArray(final int p0, final CTChartLines p1);
    
    CTChartLines insertNewSerLines(final int p0);
    
    CTChartLines addNewSerLines();
    
    void removeSerLines(final int p0);
    
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
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTBarChart.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTBarChart newInstance() {
            return (CTBarChart)getTypeLoader().newInstance(CTBarChart.type, (XmlOptions)null);
        }
        
        public static CTBarChart newInstance(final XmlOptions xmlOptions) {
            return (CTBarChart)getTypeLoader().newInstance(CTBarChart.type, xmlOptions);
        }
        
        public static CTBarChart parse(final String s) throws XmlException {
            return (CTBarChart)getTypeLoader().parse(s, CTBarChart.type, (XmlOptions)null);
        }
        
        public static CTBarChart parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTBarChart)getTypeLoader().parse(s, CTBarChart.type, xmlOptions);
        }
        
        public static CTBarChart parse(final File file) throws XmlException, IOException {
            return (CTBarChart)getTypeLoader().parse(file, CTBarChart.type, (XmlOptions)null);
        }
        
        public static CTBarChart parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTBarChart)getTypeLoader().parse(file, CTBarChart.type, xmlOptions);
        }
        
        public static CTBarChart parse(final URL url) throws XmlException, IOException {
            return (CTBarChart)getTypeLoader().parse(url, CTBarChart.type, (XmlOptions)null);
        }
        
        public static CTBarChart parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTBarChart)getTypeLoader().parse(url, CTBarChart.type, xmlOptions);
        }
        
        public static CTBarChart parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTBarChart)getTypeLoader().parse(inputStream, CTBarChart.type, (XmlOptions)null);
        }
        
        public static CTBarChart parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTBarChart)getTypeLoader().parse(inputStream, CTBarChart.type, xmlOptions);
        }
        
        public static CTBarChart parse(final Reader reader) throws XmlException, IOException {
            return (CTBarChart)getTypeLoader().parse(reader, CTBarChart.type, (XmlOptions)null);
        }
        
        public static CTBarChart parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTBarChart)getTypeLoader().parse(reader, CTBarChart.type, xmlOptions);
        }
        
        public static CTBarChart parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTBarChart)getTypeLoader().parse(xmlStreamReader, CTBarChart.type, (XmlOptions)null);
        }
        
        public static CTBarChart parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTBarChart)getTypeLoader().parse(xmlStreamReader, CTBarChart.type, xmlOptions);
        }
        
        public static CTBarChart parse(final Node node) throws XmlException {
            return (CTBarChart)getTypeLoader().parse(node, CTBarChart.type, (XmlOptions)null);
        }
        
        public static CTBarChart parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTBarChart)getTypeLoader().parse(node, CTBarChart.type, xmlOptions);
        }
        
        @Deprecated
        public static CTBarChart parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTBarChart)getTypeLoader().parse(xmlInputStream, CTBarChart.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTBarChart parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTBarChart)getTypeLoader().parse(xmlInputStream, CTBarChart.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTBarChart.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTBarChart.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
