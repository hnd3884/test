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
import java.util.List;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTPlotArea extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTPlotArea.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctplotarea106etype");
    
    CTLayout getLayout();
    
    boolean isSetLayout();
    
    void setLayout(final CTLayout p0);
    
    CTLayout addNewLayout();
    
    void unsetLayout();
    
    List<CTAreaChart> getAreaChartList();
    
    @Deprecated
    CTAreaChart[] getAreaChartArray();
    
    CTAreaChart getAreaChartArray(final int p0);
    
    int sizeOfAreaChartArray();
    
    void setAreaChartArray(final CTAreaChart[] p0);
    
    void setAreaChartArray(final int p0, final CTAreaChart p1);
    
    CTAreaChart insertNewAreaChart(final int p0);
    
    CTAreaChart addNewAreaChart();
    
    void removeAreaChart(final int p0);
    
    List<CTArea3DChart> getArea3DChartList();
    
    @Deprecated
    CTArea3DChart[] getArea3DChartArray();
    
    CTArea3DChart getArea3DChartArray(final int p0);
    
    int sizeOfArea3DChartArray();
    
    void setArea3DChartArray(final CTArea3DChart[] p0);
    
    void setArea3DChartArray(final int p0, final CTArea3DChart p1);
    
    CTArea3DChart insertNewArea3DChart(final int p0);
    
    CTArea3DChart addNewArea3DChart();
    
    void removeArea3DChart(final int p0);
    
    List<CTLineChart> getLineChartList();
    
    @Deprecated
    CTLineChart[] getLineChartArray();
    
    CTLineChart getLineChartArray(final int p0);
    
    int sizeOfLineChartArray();
    
    void setLineChartArray(final CTLineChart[] p0);
    
    void setLineChartArray(final int p0, final CTLineChart p1);
    
    CTLineChart insertNewLineChart(final int p0);
    
    CTLineChart addNewLineChart();
    
    void removeLineChart(final int p0);
    
    List<CTLine3DChart> getLine3DChartList();
    
    @Deprecated
    CTLine3DChart[] getLine3DChartArray();
    
    CTLine3DChart getLine3DChartArray(final int p0);
    
    int sizeOfLine3DChartArray();
    
    void setLine3DChartArray(final CTLine3DChart[] p0);
    
    void setLine3DChartArray(final int p0, final CTLine3DChart p1);
    
    CTLine3DChart insertNewLine3DChart(final int p0);
    
    CTLine3DChart addNewLine3DChart();
    
    void removeLine3DChart(final int p0);
    
    List<CTStockChart> getStockChartList();
    
    @Deprecated
    CTStockChart[] getStockChartArray();
    
    CTStockChart getStockChartArray(final int p0);
    
    int sizeOfStockChartArray();
    
    void setStockChartArray(final CTStockChart[] p0);
    
    void setStockChartArray(final int p0, final CTStockChart p1);
    
    CTStockChart insertNewStockChart(final int p0);
    
    CTStockChart addNewStockChart();
    
    void removeStockChart(final int p0);
    
    List<CTRadarChart> getRadarChartList();
    
    @Deprecated
    CTRadarChart[] getRadarChartArray();
    
    CTRadarChart getRadarChartArray(final int p0);
    
    int sizeOfRadarChartArray();
    
    void setRadarChartArray(final CTRadarChart[] p0);
    
    void setRadarChartArray(final int p0, final CTRadarChart p1);
    
    CTRadarChart insertNewRadarChart(final int p0);
    
    CTRadarChart addNewRadarChart();
    
    void removeRadarChart(final int p0);
    
    List<CTScatterChart> getScatterChartList();
    
    @Deprecated
    CTScatterChart[] getScatterChartArray();
    
    CTScatterChart getScatterChartArray(final int p0);
    
    int sizeOfScatterChartArray();
    
    void setScatterChartArray(final CTScatterChart[] p0);
    
    void setScatterChartArray(final int p0, final CTScatterChart p1);
    
    CTScatterChart insertNewScatterChart(final int p0);
    
    CTScatterChart addNewScatterChart();
    
    void removeScatterChart(final int p0);
    
    List<CTPieChart> getPieChartList();
    
    @Deprecated
    CTPieChart[] getPieChartArray();
    
    CTPieChart getPieChartArray(final int p0);
    
    int sizeOfPieChartArray();
    
    void setPieChartArray(final CTPieChart[] p0);
    
    void setPieChartArray(final int p0, final CTPieChart p1);
    
    CTPieChart insertNewPieChart(final int p0);
    
    CTPieChart addNewPieChart();
    
    void removePieChart(final int p0);
    
    List<CTPie3DChart> getPie3DChartList();
    
    @Deprecated
    CTPie3DChart[] getPie3DChartArray();
    
    CTPie3DChart getPie3DChartArray(final int p0);
    
    int sizeOfPie3DChartArray();
    
    void setPie3DChartArray(final CTPie3DChart[] p0);
    
    void setPie3DChartArray(final int p0, final CTPie3DChart p1);
    
    CTPie3DChart insertNewPie3DChart(final int p0);
    
    CTPie3DChart addNewPie3DChart();
    
    void removePie3DChart(final int p0);
    
    List<CTDoughnutChart> getDoughnutChartList();
    
    @Deprecated
    CTDoughnutChart[] getDoughnutChartArray();
    
    CTDoughnutChart getDoughnutChartArray(final int p0);
    
    int sizeOfDoughnutChartArray();
    
    void setDoughnutChartArray(final CTDoughnutChart[] p0);
    
    void setDoughnutChartArray(final int p0, final CTDoughnutChart p1);
    
    CTDoughnutChart insertNewDoughnutChart(final int p0);
    
    CTDoughnutChart addNewDoughnutChart();
    
    void removeDoughnutChart(final int p0);
    
    List<CTBarChart> getBarChartList();
    
    @Deprecated
    CTBarChart[] getBarChartArray();
    
    CTBarChart getBarChartArray(final int p0);
    
    int sizeOfBarChartArray();
    
    void setBarChartArray(final CTBarChart[] p0);
    
    void setBarChartArray(final int p0, final CTBarChart p1);
    
    CTBarChart insertNewBarChart(final int p0);
    
    CTBarChart addNewBarChart();
    
    void removeBarChart(final int p0);
    
    List<CTBar3DChart> getBar3DChartList();
    
    @Deprecated
    CTBar3DChart[] getBar3DChartArray();
    
    CTBar3DChart getBar3DChartArray(final int p0);
    
    int sizeOfBar3DChartArray();
    
    void setBar3DChartArray(final CTBar3DChart[] p0);
    
    void setBar3DChartArray(final int p0, final CTBar3DChart p1);
    
    CTBar3DChart insertNewBar3DChart(final int p0);
    
    CTBar3DChart addNewBar3DChart();
    
    void removeBar3DChart(final int p0);
    
    List<CTOfPieChart> getOfPieChartList();
    
    @Deprecated
    CTOfPieChart[] getOfPieChartArray();
    
    CTOfPieChart getOfPieChartArray(final int p0);
    
    int sizeOfOfPieChartArray();
    
    void setOfPieChartArray(final CTOfPieChart[] p0);
    
    void setOfPieChartArray(final int p0, final CTOfPieChart p1);
    
    CTOfPieChart insertNewOfPieChart(final int p0);
    
    CTOfPieChart addNewOfPieChart();
    
    void removeOfPieChart(final int p0);
    
    List<CTSurfaceChart> getSurfaceChartList();
    
    @Deprecated
    CTSurfaceChart[] getSurfaceChartArray();
    
    CTSurfaceChart getSurfaceChartArray(final int p0);
    
    int sizeOfSurfaceChartArray();
    
    void setSurfaceChartArray(final CTSurfaceChart[] p0);
    
    void setSurfaceChartArray(final int p0, final CTSurfaceChart p1);
    
    CTSurfaceChart insertNewSurfaceChart(final int p0);
    
    CTSurfaceChart addNewSurfaceChart();
    
    void removeSurfaceChart(final int p0);
    
    List<CTSurface3DChart> getSurface3DChartList();
    
    @Deprecated
    CTSurface3DChart[] getSurface3DChartArray();
    
    CTSurface3DChart getSurface3DChartArray(final int p0);
    
    int sizeOfSurface3DChartArray();
    
    void setSurface3DChartArray(final CTSurface3DChart[] p0);
    
    void setSurface3DChartArray(final int p0, final CTSurface3DChart p1);
    
    CTSurface3DChart insertNewSurface3DChart(final int p0);
    
    CTSurface3DChart addNewSurface3DChart();
    
    void removeSurface3DChart(final int p0);
    
    List<CTBubbleChart> getBubbleChartList();
    
    @Deprecated
    CTBubbleChart[] getBubbleChartArray();
    
    CTBubbleChart getBubbleChartArray(final int p0);
    
    int sizeOfBubbleChartArray();
    
    void setBubbleChartArray(final CTBubbleChart[] p0);
    
    void setBubbleChartArray(final int p0, final CTBubbleChart p1);
    
    CTBubbleChart insertNewBubbleChart(final int p0);
    
    CTBubbleChart addNewBubbleChart();
    
    void removeBubbleChart(final int p0);
    
    List<CTValAx> getValAxList();
    
    @Deprecated
    CTValAx[] getValAxArray();
    
    CTValAx getValAxArray(final int p0);
    
    int sizeOfValAxArray();
    
    void setValAxArray(final CTValAx[] p0);
    
    void setValAxArray(final int p0, final CTValAx p1);
    
    CTValAx insertNewValAx(final int p0);
    
    CTValAx addNewValAx();
    
    void removeValAx(final int p0);
    
    List<CTCatAx> getCatAxList();
    
    @Deprecated
    CTCatAx[] getCatAxArray();
    
    CTCatAx getCatAxArray(final int p0);
    
    int sizeOfCatAxArray();
    
    void setCatAxArray(final CTCatAx[] p0);
    
    void setCatAxArray(final int p0, final CTCatAx p1);
    
    CTCatAx insertNewCatAx(final int p0);
    
    CTCatAx addNewCatAx();
    
    void removeCatAx(final int p0);
    
    List<CTDateAx> getDateAxList();
    
    @Deprecated
    CTDateAx[] getDateAxArray();
    
    CTDateAx getDateAxArray(final int p0);
    
    int sizeOfDateAxArray();
    
    void setDateAxArray(final CTDateAx[] p0);
    
    void setDateAxArray(final int p0, final CTDateAx p1);
    
    CTDateAx insertNewDateAx(final int p0);
    
    CTDateAx addNewDateAx();
    
    void removeDateAx(final int p0);
    
    List<CTSerAx> getSerAxList();
    
    @Deprecated
    CTSerAx[] getSerAxArray();
    
    CTSerAx getSerAxArray(final int p0);
    
    int sizeOfSerAxArray();
    
    void setSerAxArray(final CTSerAx[] p0);
    
    void setSerAxArray(final int p0, final CTSerAx p1);
    
    CTSerAx insertNewSerAx(final int p0);
    
    CTSerAx addNewSerAx();
    
    void removeSerAx(final int p0);
    
    CTDTable getDTable();
    
    boolean isSetDTable();
    
    void setDTable(final CTDTable p0);
    
    CTDTable addNewDTable();
    
    void unsetDTable();
    
    CTShapeProperties getSpPr();
    
    boolean isSetSpPr();
    
    void setSpPr(final CTShapeProperties p0);
    
    CTShapeProperties addNewSpPr();
    
    void unsetSpPr();
    
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
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTPlotArea.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTPlotArea newInstance() {
            return (CTPlotArea)getTypeLoader().newInstance(CTPlotArea.type, (XmlOptions)null);
        }
        
        public static CTPlotArea newInstance(final XmlOptions xmlOptions) {
            return (CTPlotArea)getTypeLoader().newInstance(CTPlotArea.type, xmlOptions);
        }
        
        public static CTPlotArea parse(final String s) throws XmlException {
            return (CTPlotArea)getTypeLoader().parse(s, CTPlotArea.type, (XmlOptions)null);
        }
        
        public static CTPlotArea parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTPlotArea)getTypeLoader().parse(s, CTPlotArea.type, xmlOptions);
        }
        
        public static CTPlotArea parse(final File file) throws XmlException, IOException {
            return (CTPlotArea)getTypeLoader().parse(file, CTPlotArea.type, (XmlOptions)null);
        }
        
        public static CTPlotArea parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTPlotArea)getTypeLoader().parse(file, CTPlotArea.type, xmlOptions);
        }
        
        public static CTPlotArea parse(final URL url) throws XmlException, IOException {
            return (CTPlotArea)getTypeLoader().parse(url, CTPlotArea.type, (XmlOptions)null);
        }
        
        public static CTPlotArea parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTPlotArea)getTypeLoader().parse(url, CTPlotArea.type, xmlOptions);
        }
        
        public static CTPlotArea parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTPlotArea)getTypeLoader().parse(inputStream, CTPlotArea.type, (XmlOptions)null);
        }
        
        public static CTPlotArea parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTPlotArea)getTypeLoader().parse(inputStream, CTPlotArea.type, xmlOptions);
        }
        
        public static CTPlotArea parse(final Reader reader) throws XmlException, IOException {
            return (CTPlotArea)getTypeLoader().parse(reader, CTPlotArea.type, (XmlOptions)null);
        }
        
        public static CTPlotArea parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTPlotArea)getTypeLoader().parse(reader, CTPlotArea.type, xmlOptions);
        }
        
        public static CTPlotArea parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTPlotArea)getTypeLoader().parse(xmlStreamReader, CTPlotArea.type, (XmlOptions)null);
        }
        
        public static CTPlotArea parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTPlotArea)getTypeLoader().parse(xmlStreamReader, CTPlotArea.type, xmlOptions);
        }
        
        public static CTPlotArea parse(final Node node) throws XmlException {
            return (CTPlotArea)getTypeLoader().parse(node, CTPlotArea.type, (XmlOptions)null);
        }
        
        public static CTPlotArea parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTPlotArea)getTypeLoader().parse(node, CTPlotArea.type, xmlOptions);
        }
        
        @Deprecated
        public static CTPlotArea parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTPlotArea)getTypeLoader().parse(xmlInputStream, CTPlotArea.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTPlotArea parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTPlotArea)getTypeLoader().parse(xmlInputStream, CTPlotArea.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTPlotArea.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTPlotArea.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
