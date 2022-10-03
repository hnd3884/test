package org.openxmlformats.schemas.drawingml.x2006.chart.impl;

import org.openxmlformats.schemas.drawingml.x2006.chart.CTExtensionList;
import org.openxmlformats.schemas.drawingml.x2006.main.CTShapeProperties;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTDTable;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTSerAx;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTDateAx;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTCatAx;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTValAx;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTBubbleChart;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTSurface3DChart;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTSurfaceChart;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTOfPieChart;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTBar3DChart;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTBarChart;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTDoughnutChart;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTPie3DChart;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTPieChart;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTScatterChart;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTRadarChart;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTStockChart;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTLine3DChart;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTLineChart;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTArea3DChart;
import java.util.ArrayList;
import java.util.AbstractList;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTAreaChart;
import java.util.List;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTLayout;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTPlotArea;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTPlotAreaImpl extends XmlComplexContentImpl implements CTPlotArea
{
    private static final long serialVersionUID = 1L;
    private static final QName LAYOUT$0;
    private static final QName AREACHART$2;
    private static final QName AREA3DCHART$4;
    private static final QName LINECHART$6;
    private static final QName LINE3DCHART$8;
    private static final QName STOCKCHART$10;
    private static final QName RADARCHART$12;
    private static final QName SCATTERCHART$14;
    private static final QName PIECHART$16;
    private static final QName PIE3DCHART$18;
    private static final QName DOUGHNUTCHART$20;
    private static final QName BARCHART$22;
    private static final QName BAR3DCHART$24;
    private static final QName OFPIECHART$26;
    private static final QName SURFACECHART$28;
    private static final QName SURFACE3DCHART$30;
    private static final QName BUBBLECHART$32;
    private static final QName VALAX$34;
    private static final QName CATAX$36;
    private static final QName DATEAX$38;
    private static final QName SERAX$40;
    private static final QName DTABLE$42;
    private static final QName SPPR$44;
    private static final QName EXTLST$46;
    
    public CTPlotAreaImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTLayout getLayout() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTLayout ctLayout = (CTLayout)this.get_store().find_element_user(CTPlotAreaImpl.LAYOUT$0, 0);
            if (ctLayout == null) {
                return null;
            }
            return ctLayout;
        }
    }
    
    public boolean isSetLayout() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTPlotAreaImpl.LAYOUT$0) != 0;
        }
    }
    
    public void setLayout(final CTLayout ctLayout) {
        this.generatedSetterHelperImpl((XmlObject)ctLayout, CTPlotAreaImpl.LAYOUT$0, 0, (short)1);
    }
    
    public CTLayout addNewLayout() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTLayout)this.get_store().add_element_user(CTPlotAreaImpl.LAYOUT$0);
        }
    }
    
    public void unsetLayout() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTPlotAreaImpl.LAYOUT$0, 0);
        }
    }
    
    public List<CTAreaChart> getAreaChartList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class AreaChartList extends AbstractList<CTAreaChart>
            {
                @Override
                public CTAreaChart get(final int n) {
                    return CTPlotAreaImpl.this.getAreaChartArray(n);
                }
                
                @Override
                public CTAreaChart set(final int n, final CTAreaChart ctAreaChart) {
                    final CTAreaChart areaChartArray = CTPlotAreaImpl.this.getAreaChartArray(n);
                    CTPlotAreaImpl.this.setAreaChartArray(n, ctAreaChart);
                    return areaChartArray;
                }
                
                @Override
                public void add(final int n, final CTAreaChart ctAreaChart) {
                    CTPlotAreaImpl.this.insertNewAreaChart(n).set((XmlObject)ctAreaChart);
                }
                
                @Override
                public CTAreaChart remove(final int n) {
                    final CTAreaChart areaChartArray = CTPlotAreaImpl.this.getAreaChartArray(n);
                    CTPlotAreaImpl.this.removeAreaChart(n);
                    return areaChartArray;
                }
                
                @Override
                public int size() {
                    return CTPlotAreaImpl.this.sizeOfAreaChartArray();
                }
            }
            return new AreaChartList();
        }
    }
    
    @Deprecated
    public CTAreaChart[] getAreaChartArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTPlotAreaImpl.AREACHART$2, (List)list);
            final CTAreaChart[] array = new CTAreaChart[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTAreaChart getAreaChartArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTAreaChart ctAreaChart = (CTAreaChart)this.get_store().find_element_user(CTPlotAreaImpl.AREACHART$2, n);
            if (ctAreaChart == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctAreaChart;
        }
    }
    
    public int sizeOfAreaChartArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTPlotAreaImpl.AREACHART$2);
        }
    }
    
    public void setAreaChartArray(final CTAreaChart[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTPlotAreaImpl.AREACHART$2);
    }
    
    public void setAreaChartArray(final int n, final CTAreaChart ctAreaChart) {
        this.generatedSetterHelperImpl((XmlObject)ctAreaChart, CTPlotAreaImpl.AREACHART$2, n, (short)2);
    }
    
    public CTAreaChart insertNewAreaChart(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTAreaChart)this.get_store().insert_element_user(CTPlotAreaImpl.AREACHART$2, n);
        }
    }
    
    public CTAreaChart addNewAreaChart() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTAreaChart)this.get_store().add_element_user(CTPlotAreaImpl.AREACHART$2);
        }
    }
    
    public void removeAreaChart(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTPlotAreaImpl.AREACHART$2, n);
        }
    }
    
    public List<CTArea3DChart> getArea3DChartList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class Area3DChartList extends AbstractList<CTArea3DChart>
            {
                @Override
                public CTArea3DChart get(final int n) {
                    return CTPlotAreaImpl.this.getArea3DChartArray(n);
                }
                
                @Override
                public CTArea3DChart set(final int n, final CTArea3DChart ctArea3DChart) {
                    final CTArea3DChart area3DChartArray = CTPlotAreaImpl.this.getArea3DChartArray(n);
                    CTPlotAreaImpl.this.setArea3DChartArray(n, ctArea3DChart);
                    return area3DChartArray;
                }
                
                @Override
                public void add(final int n, final CTArea3DChart ctArea3DChart) {
                    CTPlotAreaImpl.this.insertNewArea3DChart(n).set((XmlObject)ctArea3DChart);
                }
                
                @Override
                public CTArea3DChart remove(final int n) {
                    final CTArea3DChart area3DChartArray = CTPlotAreaImpl.this.getArea3DChartArray(n);
                    CTPlotAreaImpl.this.removeArea3DChart(n);
                    return area3DChartArray;
                }
                
                @Override
                public int size() {
                    return CTPlotAreaImpl.this.sizeOfArea3DChartArray();
                }
            }
            return new Area3DChartList();
        }
    }
    
    @Deprecated
    public CTArea3DChart[] getArea3DChartArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTPlotAreaImpl.AREA3DCHART$4, (List)list);
            final CTArea3DChart[] array = new CTArea3DChart[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTArea3DChart getArea3DChartArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTArea3DChart ctArea3DChart = (CTArea3DChart)this.get_store().find_element_user(CTPlotAreaImpl.AREA3DCHART$4, n);
            if (ctArea3DChart == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctArea3DChart;
        }
    }
    
    public int sizeOfArea3DChartArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTPlotAreaImpl.AREA3DCHART$4);
        }
    }
    
    public void setArea3DChartArray(final CTArea3DChart[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTPlotAreaImpl.AREA3DCHART$4);
    }
    
    public void setArea3DChartArray(final int n, final CTArea3DChart ctArea3DChart) {
        this.generatedSetterHelperImpl((XmlObject)ctArea3DChart, CTPlotAreaImpl.AREA3DCHART$4, n, (short)2);
    }
    
    public CTArea3DChart insertNewArea3DChart(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTArea3DChart)this.get_store().insert_element_user(CTPlotAreaImpl.AREA3DCHART$4, n);
        }
    }
    
    public CTArea3DChart addNewArea3DChart() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTArea3DChart)this.get_store().add_element_user(CTPlotAreaImpl.AREA3DCHART$4);
        }
    }
    
    public void removeArea3DChart(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTPlotAreaImpl.AREA3DCHART$4, n);
        }
    }
    
    public List<CTLineChart> getLineChartList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class LineChartList extends AbstractList<CTLineChart>
            {
                @Override
                public CTLineChart get(final int n) {
                    return CTPlotAreaImpl.this.getLineChartArray(n);
                }
                
                @Override
                public CTLineChart set(final int n, final CTLineChart ctLineChart) {
                    final CTLineChart lineChartArray = CTPlotAreaImpl.this.getLineChartArray(n);
                    CTPlotAreaImpl.this.setLineChartArray(n, ctLineChart);
                    return lineChartArray;
                }
                
                @Override
                public void add(final int n, final CTLineChart ctLineChart) {
                    CTPlotAreaImpl.this.insertNewLineChart(n).set((XmlObject)ctLineChart);
                }
                
                @Override
                public CTLineChart remove(final int n) {
                    final CTLineChart lineChartArray = CTPlotAreaImpl.this.getLineChartArray(n);
                    CTPlotAreaImpl.this.removeLineChart(n);
                    return lineChartArray;
                }
                
                @Override
                public int size() {
                    return CTPlotAreaImpl.this.sizeOfLineChartArray();
                }
            }
            return new LineChartList();
        }
    }
    
    @Deprecated
    public CTLineChart[] getLineChartArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTPlotAreaImpl.LINECHART$6, (List)list);
            final CTLineChart[] array = new CTLineChart[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTLineChart getLineChartArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTLineChart ctLineChart = (CTLineChart)this.get_store().find_element_user(CTPlotAreaImpl.LINECHART$6, n);
            if (ctLineChart == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctLineChart;
        }
    }
    
    public int sizeOfLineChartArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTPlotAreaImpl.LINECHART$6);
        }
    }
    
    public void setLineChartArray(final CTLineChart[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTPlotAreaImpl.LINECHART$6);
    }
    
    public void setLineChartArray(final int n, final CTLineChart ctLineChart) {
        this.generatedSetterHelperImpl((XmlObject)ctLineChart, CTPlotAreaImpl.LINECHART$6, n, (short)2);
    }
    
    public CTLineChart insertNewLineChart(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTLineChart)this.get_store().insert_element_user(CTPlotAreaImpl.LINECHART$6, n);
        }
    }
    
    public CTLineChart addNewLineChart() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTLineChart)this.get_store().add_element_user(CTPlotAreaImpl.LINECHART$6);
        }
    }
    
    public void removeLineChart(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTPlotAreaImpl.LINECHART$6, n);
        }
    }
    
    public List<CTLine3DChart> getLine3DChartList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class Line3DChartList extends AbstractList<CTLine3DChart>
            {
                @Override
                public CTLine3DChart get(final int n) {
                    return CTPlotAreaImpl.this.getLine3DChartArray(n);
                }
                
                @Override
                public CTLine3DChart set(final int n, final CTLine3DChart ctLine3DChart) {
                    final CTLine3DChart line3DChartArray = CTPlotAreaImpl.this.getLine3DChartArray(n);
                    CTPlotAreaImpl.this.setLine3DChartArray(n, ctLine3DChart);
                    return line3DChartArray;
                }
                
                @Override
                public void add(final int n, final CTLine3DChart ctLine3DChart) {
                    CTPlotAreaImpl.this.insertNewLine3DChart(n).set((XmlObject)ctLine3DChart);
                }
                
                @Override
                public CTLine3DChart remove(final int n) {
                    final CTLine3DChart line3DChartArray = CTPlotAreaImpl.this.getLine3DChartArray(n);
                    CTPlotAreaImpl.this.removeLine3DChart(n);
                    return line3DChartArray;
                }
                
                @Override
                public int size() {
                    return CTPlotAreaImpl.this.sizeOfLine3DChartArray();
                }
            }
            return new Line3DChartList();
        }
    }
    
    @Deprecated
    public CTLine3DChart[] getLine3DChartArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTPlotAreaImpl.LINE3DCHART$8, (List)list);
            final CTLine3DChart[] array = new CTLine3DChart[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTLine3DChart getLine3DChartArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTLine3DChart ctLine3DChart = (CTLine3DChart)this.get_store().find_element_user(CTPlotAreaImpl.LINE3DCHART$8, n);
            if (ctLine3DChart == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctLine3DChart;
        }
    }
    
    public int sizeOfLine3DChartArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTPlotAreaImpl.LINE3DCHART$8);
        }
    }
    
    public void setLine3DChartArray(final CTLine3DChart[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTPlotAreaImpl.LINE3DCHART$8);
    }
    
    public void setLine3DChartArray(final int n, final CTLine3DChart ctLine3DChart) {
        this.generatedSetterHelperImpl((XmlObject)ctLine3DChart, CTPlotAreaImpl.LINE3DCHART$8, n, (short)2);
    }
    
    public CTLine3DChart insertNewLine3DChart(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTLine3DChart)this.get_store().insert_element_user(CTPlotAreaImpl.LINE3DCHART$8, n);
        }
    }
    
    public CTLine3DChart addNewLine3DChart() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTLine3DChart)this.get_store().add_element_user(CTPlotAreaImpl.LINE3DCHART$8);
        }
    }
    
    public void removeLine3DChart(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTPlotAreaImpl.LINE3DCHART$8, n);
        }
    }
    
    public List<CTStockChart> getStockChartList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class StockChartList extends AbstractList<CTStockChart>
            {
                @Override
                public CTStockChart get(final int n) {
                    return CTPlotAreaImpl.this.getStockChartArray(n);
                }
                
                @Override
                public CTStockChart set(final int n, final CTStockChart ctStockChart) {
                    final CTStockChart stockChartArray = CTPlotAreaImpl.this.getStockChartArray(n);
                    CTPlotAreaImpl.this.setStockChartArray(n, ctStockChart);
                    return stockChartArray;
                }
                
                @Override
                public void add(final int n, final CTStockChart ctStockChart) {
                    CTPlotAreaImpl.this.insertNewStockChart(n).set((XmlObject)ctStockChart);
                }
                
                @Override
                public CTStockChart remove(final int n) {
                    final CTStockChart stockChartArray = CTPlotAreaImpl.this.getStockChartArray(n);
                    CTPlotAreaImpl.this.removeStockChart(n);
                    return stockChartArray;
                }
                
                @Override
                public int size() {
                    return CTPlotAreaImpl.this.sizeOfStockChartArray();
                }
            }
            return new StockChartList();
        }
    }
    
    @Deprecated
    public CTStockChart[] getStockChartArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTPlotAreaImpl.STOCKCHART$10, (List)list);
            final CTStockChart[] array = new CTStockChart[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTStockChart getStockChartArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTStockChart ctStockChart = (CTStockChart)this.get_store().find_element_user(CTPlotAreaImpl.STOCKCHART$10, n);
            if (ctStockChart == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctStockChart;
        }
    }
    
    public int sizeOfStockChartArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTPlotAreaImpl.STOCKCHART$10);
        }
    }
    
    public void setStockChartArray(final CTStockChart[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTPlotAreaImpl.STOCKCHART$10);
    }
    
    public void setStockChartArray(final int n, final CTStockChart ctStockChart) {
        this.generatedSetterHelperImpl((XmlObject)ctStockChart, CTPlotAreaImpl.STOCKCHART$10, n, (short)2);
    }
    
    public CTStockChart insertNewStockChart(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTStockChart)this.get_store().insert_element_user(CTPlotAreaImpl.STOCKCHART$10, n);
        }
    }
    
    public CTStockChart addNewStockChart() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTStockChart)this.get_store().add_element_user(CTPlotAreaImpl.STOCKCHART$10);
        }
    }
    
    public void removeStockChart(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTPlotAreaImpl.STOCKCHART$10, n);
        }
    }
    
    public List<CTRadarChart> getRadarChartList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class RadarChartList extends AbstractList<CTRadarChart>
            {
                @Override
                public CTRadarChart get(final int n) {
                    return CTPlotAreaImpl.this.getRadarChartArray(n);
                }
                
                @Override
                public CTRadarChart set(final int n, final CTRadarChart ctRadarChart) {
                    final CTRadarChart radarChartArray = CTPlotAreaImpl.this.getRadarChartArray(n);
                    CTPlotAreaImpl.this.setRadarChartArray(n, ctRadarChart);
                    return radarChartArray;
                }
                
                @Override
                public void add(final int n, final CTRadarChart ctRadarChart) {
                    CTPlotAreaImpl.this.insertNewRadarChart(n).set((XmlObject)ctRadarChart);
                }
                
                @Override
                public CTRadarChart remove(final int n) {
                    final CTRadarChart radarChartArray = CTPlotAreaImpl.this.getRadarChartArray(n);
                    CTPlotAreaImpl.this.removeRadarChart(n);
                    return radarChartArray;
                }
                
                @Override
                public int size() {
                    return CTPlotAreaImpl.this.sizeOfRadarChartArray();
                }
            }
            return new RadarChartList();
        }
    }
    
    @Deprecated
    public CTRadarChart[] getRadarChartArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTPlotAreaImpl.RADARCHART$12, (List)list);
            final CTRadarChart[] array = new CTRadarChart[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTRadarChart getRadarChartArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTRadarChart ctRadarChart = (CTRadarChart)this.get_store().find_element_user(CTPlotAreaImpl.RADARCHART$12, n);
            if (ctRadarChart == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctRadarChart;
        }
    }
    
    public int sizeOfRadarChartArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTPlotAreaImpl.RADARCHART$12);
        }
    }
    
    public void setRadarChartArray(final CTRadarChart[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTPlotAreaImpl.RADARCHART$12);
    }
    
    public void setRadarChartArray(final int n, final CTRadarChart ctRadarChart) {
        this.generatedSetterHelperImpl((XmlObject)ctRadarChart, CTPlotAreaImpl.RADARCHART$12, n, (short)2);
    }
    
    public CTRadarChart insertNewRadarChart(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTRadarChart)this.get_store().insert_element_user(CTPlotAreaImpl.RADARCHART$12, n);
        }
    }
    
    public CTRadarChart addNewRadarChart() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTRadarChart)this.get_store().add_element_user(CTPlotAreaImpl.RADARCHART$12);
        }
    }
    
    public void removeRadarChart(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTPlotAreaImpl.RADARCHART$12, n);
        }
    }
    
    public List<CTScatterChart> getScatterChartList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class ScatterChartList extends AbstractList<CTScatterChart>
            {
                @Override
                public CTScatterChart get(final int n) {
                    return CTPlotAreaImpl.this.getScatterChartArray(n);
                }
                
                @Override
                public CTScatterChart set(final int n, final CTScatterChart ctScatterChart) {
                    final CTScatterChart scatterChartArray = CTPlotAreaImpl.this.getScatterChartArray(n);
                    CTPlotAreaImpl.this.setScatterChartArray(n, ctScatterChart);
                    return scatterChartArray;
                }
                
                @Override
                public void add(final int n, final CTScatterChart ctScatterChart) {
                    CTPlotAreaImpl.this.insertNewScatterChart(n).set((XmlObject)ctScatterChart);
                }
                
                @Override
                public CTScatterChart remove(final int n) {
                    final CTScatterChart scatterChartArray = CTPlotAreaImpl.this.getScatterChartArray(n);
                    CTPlotAreaImpl.this.removeScatterChart(n);
                    return scatterChartArray;
                }
                
                @Override
                public int size() {
                    return CTPlotAreaImpl.this.sizeOfScatterChartArray();
                }
            }
            return new ScatterChartList();
        }
    }
    
    @Deprecated
    public CTScatterChart[] getScatterChartArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTPlotAreaImpl.SCATTERCHART$14, (List)list);
            final CTScatterChart[] array = new CTScatterChart[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTScatterChart getScatterChartArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTScatterChart ctScatterChart = (CTScatterChart)this.get_store().find_element_user(CTPlotAreaImpl.SCATTERCHART$14, n);
            if (ctScatterChart == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctScatterChart;
        }
    }
    
    public int sizeOfScatterChartArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTPlotAreaImpl.SCATTERCHART$14);
        }
    }
    
    public void setScatterChartArray(final CTScatterChart[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTPlotAreaImpl.SCATTERCHART$14);
    }
    
    public void setScatterChartArray(final int n, final CTScatterChart ctScatterChart) {
        this.generatedSetterHelperImpl((XmlObject)ctScatterChart, CTPlotAreaImpl.SCATTERCHART$14, n, (short)2);
    }
    
    public CTScatterChart insertNewScatterChart(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTScatterChart)this.get_store().insert_element_user(CTPlotAreaImpl.SCATTERCHART$14, n);
        }
    }
    
    public CTScatterChart addNewScatterChart() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTScatterChart)this.get_store().add_element_user(CTPlotAreaImpl.SCATTERCHART$14);
        }
    }
    
    public void removeScatterChart(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTPlotAreaImpl.SCATTERCHART$14, n);
        }
    }
    
    public List<CTPieChart> getPieChartList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class PieChartList extends AbstractList<CTPieChart>
            {
                @Override
                public CTPieChart get(final int n) {
                    return CTPlotAreaImpl.this.getPieChartArray(n);
                }
                
                @Override
                public CTPieChart set(final int n, final CTPieChart ctPieChart) {
                    final CTPieChart pieChartArray = CTPlotAreaImpl.this.getPieChartArray(n);
                    CTPlotAreaImpl.this.setPieChartArray(n, ctPieChart);
                    return pieChartArray;
                }
                
                @Override
                public void add(final int n, final CTPieChart ctPieChart) {
                    CTPlotAreaImpl.this.insertNewPieChart(n).set((XmlObject)ctPieChart);
                }
                
                @Override
                public CTPieChart remove(final int n) {
                    final CTPieChart pieChartArray = CTPlotAreaImpl.this.getPieChartArray(n);
                    CTPlotAreaImpl.this.removePieChart(n);
                    return pieChartArray;
                }
                
                @Override
                public int size() {
                    return CTPlotAreaImpl.this.sizeOfPieChartArray();
                }
            }
            return new PieChartList();
        }
    }
    
    @Deprecated
    public CTPieChart[] getPieChartArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTPlotAreaImpl.PIECHART$16, (List)list);
            final CTPieChart[] array = new CTPieChart[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTPieChart getPieChartArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTPieChart ctPieChart = (CTPieChart)this.get_store().find_element_user(CTPlotAreaImpl.PIECHART$16, n);
            if (ctPieChart == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctPieChart;
        }
    }
    
    public int sizeOfPieChartArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTPlotAreaImpl.PIECHART$16);
        }
    }
    
    public void setPieChartArray(final CTPieChart[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTPlotAreaImpl.PIECHART$16);
    }
    
    public void setPieChartArray(final int n, final CTPieChart ctPieChart) {
        this.generatedSetterHelperImpl((XmlObject)ctPieChart, CTPlotAreaImpl.PIECHART$16, n, (short)2);
    }
    
    public CTPieChart insertNewPieChart(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPieChart)this.get_store().insert_element_user(CTPlotAreaImpl.PIECHART$16, n);
        }
    }
    
    public CTPieChart addNewPieChart() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPieChart)this.get_store().add_element_user(CTPlotAreaImpl.PIECHART$16);
        }
    }
    
    public void removePieChart(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTPlotAreaImpl.PIECHART$16, n);
        }
    }
    
    public List<CTPie3DChart> getPie3DChartList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class Pie3DChartList extends AbstractList<CTPie3DChart>
            {
                @Override
                public CTPie3DChart get(final int n) {
                    return CTPlotAreaImpl.this.getPie3DChartArray(n);
                }
                
                @Override
                public CTPie3DChart set(final int n, final CTPie3DChart ctPie3DChart) {
                    final CTPie3DChart pie3DChartArray = CTPlotAreaImpl.this.getPie3DChartArray(n);
                    CTPlotAreaImpl.this.setPie3DChartArray(n, ctPie3DChart);
                    return pie3DChartArray;
                }
                
                @Override
                public void add(final int n, final CTPie3DChart ctPie3DChart) {
                    CTPlotAreaImpl.this.insertNewPie3DChart(n).set((XmlObject)ctPie3DChart);
                }
                
                @Override
                public CTPie3DChart remove(final int n) {
                    final CTPie3DChart pie3DChartArray = CTPlotAreaImpl.this.getPie3DChartArray(n);
                    CTPlotAreaImpl.this.removePie3DChart(n);
                    return pie3DChartArray;
                }
                
                @Override
                public int size() {
                    return CTPlotAreaImpl.this.sizeOfPie3DChartArray();
                }
            }
            return new Pie3DChartList();
        }
    }
    
    @Deprecated
    public CTPie3DChart[] getPie3DChartArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTPlotAreaImpl.PIE3DCHART$18, (List)list);
            final CTPie3DChart[] array = new CTPie3DChart[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTPie3DChart getPie3DChartArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTPie3DChart ctPie3DChart = (CTPie3DChart)this.get_store().find_element_user(CTPlotAreaImpl.PIE3DCHART$18, n);
            if (ctPie3DChart == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctPie3DChart;
        }
    }
    
    public int sizeOfPie3DChartArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTPlotAreaImpl.PIE3DCHART$18);
        }
    }
    
    public void setPie3DChartArray(final CTPie3DChart[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTPlotAreaImpl.PIE3DCHART$18);
    }
    
    public void setPie3DChartArray(final int n, final CTPie3DChart ctPie3DChart) {
        this.generatedSetterHelperImpl((XmlObject)ctPie3DChart, CTPlotAreaImpl.PIE3DCHART$18, n, (short)2);
    }
    
    public CTPie3DChart insertNewPie3DChart(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPie3DChart)this.get_store().insert_element_user(CTPlotAreaImpl.PIE3DCHART$18, n);
        }
    }
    
    public CTPie3DChart addNewPie3DChart() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPie3DChart)this.get_store().add_element_user(CTPlotAreaImpl.PIE3DCHART$18);
        }
    }
    
    public void removePie3DChart(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTPlotAreaImpl.PIE3DCHART$18, n);
        }
    }
    
    public List<CTDoughnutChart> getDoughnutChartList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class DoughnutChartList extends AbstractList<CTDoughnutChart>
            {
                @Override
                public CTDoughnutChart get(final int n) {
                    return CTPlotAreaImpl.this.getDoughnutChartArray(n);
                }
                
                @Override
                public CTDoughnutChart set(final int n, final CTDoughnutChart ctDoughnutChart) {
                    final CTDoughnutChart doughnutChartArray = CTPlotAreaImpl.this.getDoughnutChartArray(n);
                    CTPlotAreaImpl.this.setDoughnutChartArray(n, ctDoughnutChart);
                    return doughnutChartArray;
                }
                
                @Override
                public void add(final int n, final CTDoughnutChart ctDoughnutChart) {
                    CTPlotAreaImpl.this.insertNewDoughnutChart(n).set((XmlObject)ctDoughnutChart);
                }
                
                @Override
                public CTDoughnutChart remove(final int n) {
                    final CTDoughnutChart doughnutChartArray = CTPlotAreaImpl.this.getDoughnutChartArray(n);
                    CTPlotAreaImpl.this.removeDoughnutChart(n);
                    return doughnutChartArray;
                }
                
                @Override
                public int size() {
                    return CTPlotAreaImpl.this.sizeOfDoughnutChartArray();
                }
            }
            return new DoughnutChartList();
        }
    }
    
    @Deprecated
    public CTDoughnutChart[] getDoughnutChartArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTPlotAreaImpl.DOUGHNUTCHART$20, (List)list);
            final CTDoughnutChart[] array = new CTDoughnutChart[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTDoughnutChart getDoughnutChartArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTDoughnutChart ctDoughnutChart = (CTDoughnutChart)this.get_store().find_element_user(CTPlotAreaImpl.DOUGHNUTCHART$20, n);
            if (ctDoughnutChart == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctDoughnutChart;
        }
    }
    
    public int sizeOfDoughnutChartArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTPlotAreaImpl.DOUGHNUTCHART$20);
        }
    }
    
    public void setDoughnutChartArray(final CTDoughnutChart[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTPlotAreaImpl.DOUGHNUTCHART$20);
    }
    
    public void setDoughnutChartArray(final int n, final CTDoughnutChart ctDoughnutChart) {
        this.generatedSetterHelperImpl((XmlObject)ctDoughnutChart, CTPlotAreaImpl.DOUGHNUTCHART$20, n, (short)2);
    }
    
    public CTDoughnutChart insertNewDoughnutChart(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTDoughnutChart)this.get_store().insert_element_user(CTPlotAreaImpl.DOUGHNUTCHART$20, n);
        }
    }
    
    public CTDoughnutChart addNewDoughnutChart() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTDoughnutChart)this.get_store().add_element_user(CTPlotAreaImpl.DOUGHNUTCHART$20);
        }
    }
    
    public void removeDoughnutChart(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTPlotAreaImpl.DOUGHNUTCHART$20, n);
        }
    }
    
    public List<CTBarChart> getBarChartList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class BarChartList extends AbstractList<CTBarChart>
            {
                @Override
                public CTBarChart get(final int n) {
                    return CTPlotAreaImpl.this.getBarChartArray(n);
                }
                
                @Override
                public CTBarChart set(final int n, final CTBarChart ctBarChart) {
                    final CTBarChart barChartArray = CTPlotAreaImpl.this.getBarChartArray(n);
                    CTPlotAreaImpl.this.setBarChartArray(n, ctBarChart);
                    return barChartArray;
                }
                
                @Override
                public void add(final int n, final CTBarChart ctBarChart) {
                    CTPlotAreaImpl.this.insertNewBarChart(n).set((XmlObject)ctBarChart);
                }
                
                @Override
                public CTBarChart remove(final int n) {
                    final CTBarChart barChartArray = CTPlotAreaImpl.this.getBarChartArray(n);
                    CTPlotAreaImpl.this.removeBarChart(n);
                    return barChartArray;
                }
                
                @Override
                public int size() {
                    return CTPlotAreaImpl.this.sizeOfBarChartArray();
                }
            }
            return new BarChartList();
        }
    }
    
    @Deprecated
    public CTBarChart[] getBarChartArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTPlotAreaImpl.BARCHART$22, (List)list);
            final CTBarChart[] array = new CTBarChart[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTBarChart getBarChartArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTBarChart ctBarChart = (CTBarChart)this.get_store().find_element_user(CTPlotAreaImpl.BARCHART$22, n);
            if (ctBarChart == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctBarChart;
        }
    }
    
    public int sizeOfBarChartArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTPlotAreaImpl.BARCHART$22);
        }
    }
    
    public void setBarChartArray(final CTBarChart[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTPlotAreaImpl.BARCHART$22);
    }
    
    public void setBarChartArray(final int n, final CTBarChart ctBarChart) {
        this.generatedSetterHelperImpl((XmlObject)ctBarChart, CTPlotAreaImpl.BARCHART$22, n, (short)2);
    }
    
    public CTBarChart insertNewBarChart(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTBarChart)this.get_store().insert_element_user(CTPlotAreaImpl.BARCHART$22, n);
        }
    }
    
    public CTBarChart addNewBarChart() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTBarChart)this.get_store().add_element_user(CTPlotAreaImpl.BARCHART$22);
        }
    }
    
    public void removeBarChart(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTPlotAreaImpl.BARCHART$22, n);
        }
    }
    
    public List<CTBar3DChart> getBar3DChartList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class Bar3DChartList extends AbstractList<CTBar3DChart>
            {
                @Override
                public CTBar3DChart get(final int n) {
                    return CTPlotAreaImpl.this.getBar3DChartArray(n);
                }
                
                @Override
                public CTBar3DChart set(final int n, final CTBar3DChart ctBar3DChart) {
                    final CTBar3DChart bar3DChartArray = CTPlotAreaImpl.this.getBar3DChartArray(n);
                    CTPlotAreaImpl.this.setBar3DChartArray(n, ctBar3DChart);
                    return bar3DChartArray;
                }
                
                @Override
                public void add(final int n, final CTBar3DChart ctBar3DChart) {
                    CTPlotAreaImpl.this.insertNewBar3DChart(n).set((XmlObject)ctBar3DChart);
                }
                
                @Override
                public CTBar3DChart remove(final int n) {
                    final CTBar3DChart bar3DChartArray = CTPlotAreaImpl.this.getBar3DChartArray(n);
                    CTPlotAreaImpl.this.removeBar3DChart(n);
                    return bar3DChartArray;
                }
                
                @Override
                public int size() {
                    return CTPlotAreaImpl.this.sizeOfBar3DChartArray();
                }
            }
            return new Bar3DChartList();
        }
    }
    
    @Deprecated
    public CTBar3DChart[] getBar3DChartArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTPlotAreaImpl.BAR3DCHART$24, (List)list);
            final CTBar3DChart[] array = new CTBar3DChart[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTBar3DChart getBar3DChartArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTBar3DChart ctBar3DChart = (CTBar3DChart)this.get_store().find_element_user(CTPlotAreaImpl.BAR3DCHART$24, n);
            if (ctBar3DChart == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctBar3DChart;
        }
    }
    
    public int sizeOfBar3DChartArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTPlotAreaImpl.BAR3DCHART$24);
        }
    }
    
    public void setBar3DChartArray(final CTBar3DChart[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTPlotAreaImpl.BAR3DCHART$24);
    }
    
    public void setBar3DChartArray(final int n, final CTBar3DChart ctBar3DChart) {
        this.generatedSetterHelperImpl((XmlObject)ctBar3DChart, CTPlotAreaImpl.BAR3DCHART$24, n, (short)2);
    }
    
    public CTBar3DChart insertNewBar3DChart(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTBar3DChart)this.get_store().insert_element_user(CTPlotAreaImpl.BAR3DCHART$24, n);
        }
    }
    
    public CTBar3DChart addNewBar3DChart() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTBar3DChart)this.get_store().add_element_user(CTPlotAreaImpl.BAR3DCHART$24);
        }
    }
    
    public void removeBar3DChart(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTPlotAreaImpl.BAR3DCHART$24, n);
        }
    }
    
    public List<CTOfPieChart> getOfPieChartList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class OfPieChartList extends AbstractList<CTOfPieChart>
            {
                @Override
                public CTOfPieChart get(final int n) {
                    return CTPlotAreaImpl.this.getOfPieChartArray(n);
                }
                
                @Override
                public CTOfPieChart set(final int n, final CTOfPieChart ctOfPieChart) {
                    final CTOfPieChart ofPieChartArray = CTPlotAreaImpl.this.getOfPieChartArray(n);
                    CTPlotAreaImpl.this.setOfPieChartArray(n, ctOfPieChart);
                    return ofPieChartArray;
                }
                
                @Override
                public void add(final int n, final CTOfPieChart ctOfPieChart) {
                    CTPlotAreaImpl.this.insertNewOfPieChart(n).set((XmlObject)ctOfPieChart);
                }
                
                @Override
                public CTOfPieChart remove(final int n) {
                    final CTOfPieChart ofPieChartArray = CTPlotAreaImpl.this.getOfPieChartArray(n);
                    CTPlotAreaImpl.this.removeOfPieChart(n);
                    return ofPieChartArray;
                }
                
                @Override
                public int size() {
                    return CTPlotAreaImpl.this.sizeOfOfPieChartArray();
                }
            }
            return new OfPieChartList();
        }
    }
    
    @Deprecated
    public CTOfPieChart[] getOfPieChartArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTPlotAreaImpl.OFPIECHART$26, (List)list);
            final CTOfPieChart[] array = new CTOfPieChart[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTOfPieChart getOfPieChartArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTOfPieChart ctOfPieChart = (CTOfPieChart)this.get_store().find_element_user(CTPlotAreaImpl.OFPIECHART$26, n);
            if (ctOfPieChart == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctOfPieChart;
        }
    }
    
    public int sizeOfOfPieChartArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTPlotAreaImpl.OFPIECHART$26);
        }
    }
    
    public void setOfPieChartArray(final CTOfPieChart[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTPlotAreaImpl.OFPIECHART$26);
    }
    
    public void setOfPieChartArray(final int n, final CTOfPieChart ctOfPieChart) {
        this.generatedSetterHelperImpl((XmlObject)ctOfPieChart, CTPlotAreaImpl.OFPIECHART$26, n, (short)2);
    }
    
    public CTOfPieChart insertNewOfPieChart(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOfPieChart)this.get_store().insert_element_user(CTPlotAreaImpl.OFPIECHART$26, n);
        }
    }
    
    public CTOfPieChart addNewOfPieChart() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOfPieChart)this.get_store().add_element_user(CTPlotAreaImpl.OFPIECHART$26);
        }
    }
    
    public void removeOfPieChart(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTPlotAreaImpl.OFPIECHART$26, n);
        }
    }
    
    public List<CTSurfaceChart> getSurfaceChartList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class SurfaceChartList extends AbstractList<CTSurfaceChart>
            {
                @Override
                public CTSurfaceChart get(final int n) {
                    return CTPlotAreaImpl.this.getSurfaceChartArray(n);
                }
                
                @Override
                public CTSurfaceChart set(final int n, final CTSurfaceChart ctSurfaceChart) {
                    final CTSurfaceChart surfaceChartArray = CTPlotAreaImpl.this.getSurfaceChartArray(n);
                    CTPlotAreaImpl.this.setSurfaceChartArray(n, ctSurfaceChart);
                    return surfaceChartArray;
                }
                
                @Override
                public void add(final int n, final CTSurfaceChart ctSurfaceChart) {
                    CTPlotAreaImpl.this.insertNewSurfaceChart(n).set((XmlObject)ctSurfaceChart);
                }
                
                @Override
                public CTSurfaceChart remove(final int n) {
                    final CTSurfaceChart surfaceChartArray = CTPlotAreaImpl.this.getSurfaceChartArray(n);
                    CTPlotAreaImpl.this.removeSurfaceChart(n);
                    return surfaceChartArray;
                }
                
                @Override
                public int size() {
                    return CTPlotAreaImpl.this.sizeOfSurfaceChartArray();
                }
            }
            return new SurfaceChartList();
        }
    }
    
    @Deprecated
    public CTSurfaceChart[] getSurfaceChartArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTPlotAreaImpl.SURFACECHART$28, (List)list);
            final CTSurfaceChart[] array = new CTSurfaceChart[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTSurfaceChart getSurfaceChartArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTSurfaceChart ctSurfaceChart = (CTSurfaceChart)this.get_store().find_element_user(CTPlotAreaImpl.SURFACECHART$28, n);
            if (ctSurfaceChart == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctSurfaceChart;
        }
    }
    
    public int sizeOfSurfaceChartArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTPlotAreaImpl.SURFACECHART$28);
        }
    }
    
    public void setSurfaceChartArray(final CTSurfaceChart[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTPlotAreaImpl.SURFACECHART$28);
    }
    
    public void setSurfaceChartArray(final int n, final CTSurfaceChart ctSurfaceChart) {
        this.generatedSetterHelperImpl((XmlObject)ctSurfaceChart, CTPlotAreaImpl.SURFACECHART$28, n, (short)2);
    }
    
    public CTSurfaceChart insertNewSurfaceChart(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTSurfaceChart)this.get_store().insert_element_user(CTPlotAreaImpl.SURFACECHART$28, n);
        }
    }
    
    public CTSurfaceChart addNewSurfaceChart() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTSurfaceChart)this.get_store().add_element_user(CTPlotAreaImpl.SURFACECHART$28);
        }
    }
    
    public void removeSurfaceChart(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTPlotAreaImpl.SURFACECHART$28, n);
        }
    }
    
    public List<CTSurface3DChart> getSurface3DChartList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class Surface3DChartList extends AbstractList<CTSurface3DChart>
            {
                @Override
                public CTSurface3DChart get(final int n) {
                    return CTPlotAreaImpl.this.getSurface3DChartArray(n);
                }
                
                @Override
                public CTSurface3DChart set(final int n, final CTSurface3DChart ctSurface3DChart) {
                    final CTSurface3DChart surface3DChartArray = CTPlotAreaImpl.this.getSurface3DChartArray(n);
                    CTPlotAreaImpl.this.setSurface3DChartArray(n, ctSurface3DChart);
                    return surface3DChartArray;
                }
                
                @Override
                public void add(final int n, final CTSurface3DChart ctSurface3DChart) {
                    CTPlotAreaImpl.this.insertNewSurface3DChart(n).set((XmlObject)ctSurface3DChart);
                }
                
                @Override
                public CTSurface3DChart remove(final int n) {
                    final CTSurface3DChart surface3DChartArray = CTPlotAreaImpl.this.getSurface3DChartArray(n);
                    CTPlotAreaImpl.this.removeSurface3DChart(n);
                    return surface3DChartArray;
                }
                
                @Override
                public int size() {
                    return CTPlotAreaImpl.this.sizeOfSurface3DChartArray();
                }
            }
            return new Surface3DChartList();
        }
    }
    
    @Deprecated
    public CTSurface3DChart[] getSurface3DChartArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTPlotAreaImpl.SURFACE3DCHART$30, (List)list);
            final CTSurface3DChart[] array = new CTSurface3DChart[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTSurface3DChart getSurface3DChartArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTSurface3DChart ctSurface3DChart = (CTSurface3DChart)this.get_store().find_element_user(CTPlotAreaImpl.SURFACE3DCHART$30, n);
            if (ctSurface3DChart == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctSurface3DChart;
        }
    }
    
    public int sizeOfSurface3DChartArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTPlotAreaImpl.SURFACE3DCHART$30);
        }
    }
    
    public void setSurface3DChartArray(final CTSurface3DChart[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTPlotAreaImpl.SURFACE3DCHART$30);
    }
    
    public void setSurface3DChartArray(final int n, final CTSurface3DChart ctSurface3DChart) {
        this.generatedSetterHelperImpl((XmlObject)ctSurface3DChart, CTPlotAreaImpl.SURFACE3DCHART$30, n, (short)2);
    }
    
    public CTSurface3DChart insertNewSurface3DChart(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTSurface3DChart)this.get_store().insert_element_user(CTPlotAreaImpl.SURFACE3DCHART$30, n);
        }
    }
    
    public CTSurface3DChart addNewSurface3DChart() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTSurface3DChart)this.get_store().add_element_user(CTPlotAreaImpl.SURFACE3DCHART$30);
        }
    }
    
    public void removeSurface3DChart(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTPlotAreaImpl.SURFACE3DCHART$30, n);
        }
    }
    
    public List<CTBubbleChart> getBubbleChartList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class BubbleChartList extends AbstractList<CTBubbleChart>
            {
                @Override
                public CTBubbleChart get(final int n) {
                    return CTPlotAreaImpl.this.getBubbleChartArray(n);
                }
                
                @Override
                public CTBubbleChart set(final int n, final CTBubbleChart ctBubbleChart) {
                    final CTBubbleChart bubbleChartArray = CTPlotAreaImpl.this.getBubbleChartArray(n);
                    CTPlotAreaImpl.this.setBubbleChartArray(n, ctBubbleChart);
                    return bubbleChartArray;
                }
                
                @Override
                public void add(final int n, final CTBubbleChart ctBubbleChart) {
                    CTPlotAreaImpl.this.insertNewBubbleChart(n).set((XmlObject)ctBubbleChart);
                }
                
                @Override
                public CTBubbleChart remove(final int n) {
                    final CTBubbleChart bubbleChartArray = CTPlotAreaImpl.this.getBubbleChartArray(n);
                    CTPlotAreaImpl.this.removeBubbleChart(n);
                    return bubbleChartArray;
                }
                
                @Override
                public int size() {
                    return CTPlotAreaImpl.this.sizeOfBubbleChartArray();
                }
            }
            return new BubbleChartList();
        }
    }
    
    @Deprecated
    public CTBubbleChart[] getBubbleChartArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTPlotAreaImpl.BUBBLECHART$32, (List)list);
            final CTBubbleChart[] array = new CTBubbleChart[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTBubbleChart getBubbleChartArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTBubbleChart ctBubbleChart = (CTBubbleChart)this.get_store().find_element_user(CTPlotAreaImpl.BUBBLECHART$32, n);
            if (ctBubbleChart == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctBubbleChart;
        }
    }
    
    public int sizeOfBubbleChartArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTPlotAreaImpl.BUBBLECHART$32);
        }
    }
    
    public void setBubbleChartArray(final CTBubbleChart[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTPlotAreaImpl.BUBBLECHART$32);
    }
    
    public void setBubbleChartArray(final int n, final CTBubbleChart ctBubbleChart) {
        this.generatedSetterHelperImpl((XmlObject)ctBubbleChart, CTPlotAreaImpl.BUBBLECHART$32, n, (short)2);
    }
    
    public CTBubbleChart insertNewBubbleChart(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTBubbleChart)this.get_store().insert_element_user(CTPlotAreaImpl.BUBBLECHART$32, n);
        }
    }
    
    public CTBubbleChart addNewBubbleChart() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTBubbleChart)this.get_store().add_element_user(CTPlotAreaImpl.BUBBLECHART$32);
        }
    }
    
    public void removeBubbleChart(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTPlotAreaImpl.BUBBLECHART$32, n);
        }
    }
    
    public List<CTValAx> getValAxList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class ValAxList extends AbstractList<CTValAx>
            {
                @Override
                public CTValAx get(final int n) {
                    return CTPlotAreaImpl.this.getValAxArray(n);
                }
                
                @Override
                public CTValAx set(final int n, final CTValAx ctValAx) {
                    final CTValAx valAxArray = CTPlotAreaImpl.this.getValAxArray(n);
                    CTPlotAreaImpl.this.setValAxArray(n, ctValAx);
                    return valAxArray;
                }
                
                @Override
                public void add(final int n, final CTValAx ctValAx) {
                    CTPlotAreaImpl.this.insertNewValAx(n).set((XmlObject)ctValAx);
                }
                
                @Override
                public CTValAx remove(final int n) {
                    final CTValAx valAxArray = CTPlotAreaImpl.this.getValAxArray(n);
                    CTPlotAreaImpl.this.removeValAx(n);
                    return valAxArray;
                }
                
                @Override
                public int size() {
                    return CTPlotAreaImpl.this.sizeOfValAxArray();
                }
            }
            return new ValAxList();
        }
    }
    
    @Deprecated
    public CTValAx[] getValAxArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTPlotAreaImpl.VALAX$34, (List)list);
            final CTValAx[] array = new CTValAx[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTValAx getValAxArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTValAx ctValAx = (CTValAx)this.get_store().find_element_user(CTPlotAreaImpl.VALAX$34, n);
            if (ctValAx == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctValAx;
        }
    }
    
    public int sizeOfValAxArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTPlotAreaImpl.VALAX$34);
        }
    }
    
    public void setValAxArray(final CTValAx[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTPlotAreaImpl.VALAX$34);
    }
    
    public void setValAxArray(final int n, final CTValAx ctValAx) {
        this.generatedSetterHelperImpl((XmlObject)ctValAx, CTPlotAreaImpl.VALAX$34, n, (short)2);
    }
    
    public CTValAx insertNewValAx(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTValAx)this.get_store().insert_element_user(CTPlotAreaImpl.VALAX$34, n);
        }
    }
    
    public CTValAx addNewValAx() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTValAx)this.get_store().add_element_user(CTPlotAreaImpl.VALAX$34);
        }
    }
    
    public void removeValAx(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTPlotAreaImpl.VALAX$34, n);
        }
    }
    
    public List<CTCatAx> getCatAxList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class CatAxList extends AbstractList<CTCatAx>
            {
                @Override
                public CTCatAx get(final int n) {
                    return CTPlotAreaImpl.this.getCatAxArray(n);
                }
                
                @Override
                public CTCatAx set(final int n, final CTCatAx ctCatAx) {
                    final CTCatAx catAxArray = CTPlotAreaImpl.this.getCatAxArray(n);
                    CTPlotAreaImpl.this.setCatAxArray(n, ctCatAx);
                    return catAxArray;
                }
                
                @Override
                public void add(final int n, final CTCatAx ctCatAx) {
                    CTPlotAreaImpl.this.insertNewCatAx(n).set((XmlObject)ctCatAx);
                }
                
                @Override
                public CTCatAx remove(final int n) {
                    final CTCatAx catAxArray = CTPlotAreaImpl.this.getCatAxArray(n);
                    CTPlotAreaImpl.this.removeCatAx(n);
                    return catAxArray;
                }
                
                @Override
                public int size() {
                    return CTPlotAreaImpl.this.sizeOfCatAxArray();
                }
            }
            return new CatAxList();
        }
    }
    
    @Deprecated
    public CTCatAx[] getCatAxArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTPlotAreaImpl.CATAX$36, (List)list);
            final CTCatAx[] array = new CTCatAx[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTCatAx getCatAxArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTCatAx ctCatAx = (CTCatAx)this.get_store().find_element_user(CTPlotAreaImpl.CATAX$36, n);
            if (ctCatAx == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctCatAx;
        }
    }
    
    public int sizeOfCatAxArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTPlotAreaImpl.CATAX$36);
        }
    }
    
    public void setCatAxArray(final CTCatAx[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTPlotAreaImpl.CATAX$36);
    }
    
    public void setCatAxArray(final int n, final CTCatAx ctCatAx) {
        this.generatedSetterHelperImpl((XmlObject)ctCatAx, CTPlotAreaImpl.CATAX$36, n, (short)2);
    }
    
    public CTCatAx insertNewCatAx(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTCatAx)this.get_store().insert_element_user(CTPlotAreaImpl.CATAX$36, n);
        }
    }
    
    public CTCatAx addNewCatAx() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTCatAx)this.get_store().add_element_user(CTPlotAreaImpl.CATAX$36);
        }
    }
    
    public void removeCatAx(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTPlotAreaImpl.CATAX$36, n);
        }
    }
    
    public List<CTDateAx> getDateAxList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class DateAxList extends AbstractList<CTDateAx>
            {
                @Override
                public CTDateAx get(final int n) {
                    return CTPlotAreaImpl.this.getDateAxArray(n);
                }
                
                @Override
                public CTDateAx set(final int n, final CTDateAx ctDateAx) {
                    final CTDateAx dateAxArray = CTPlotAreaImpl.this.getDateAxArray(n);
                    CTPlotAreaImpl.this.setDateAxArray(n, ctDateAx);
                    return dateAxArray;
                }
                
                @Override
                public void add(final int n, final CTDateAx ctDateAx) {
                    CTPlotAreaImpl.this.insertNewDateAx(n).set((XmlObject)ctDateAx);
                }
                
                @Override
                public CTDateAx remove(final int n) {
                    final CTDateAx dateAxArray = CTPlotAreaImpl.this.getDateAxArray(n);
                    CTPlotAreaImpl.this.removeDateAx(n);
                    return dateAxArray;
                }
                
                @Override
                public int size() {
                    return CTPlotAreaImpl.this.sizeOfDateAxArray();
                }
            }
            return new DateAxList();
        }
    }
    
    @Deprecated
    public CTDateAx[] getDateAxArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTPlotAreaImpl.DATEAX$38, (List)list);
            final CTDateAx[] array = new CTDateAx[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTDateAx getDateAxArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTDateAx ctDateAx = (CTDateAx)this.get_store().find_element_user(CTPlotAreaImpl.DATEAX$38, n);
            if (ctDateAx == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctDateAx;
        }
    }
    
    public int sizeOfDateAxArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTPlotAreaImpl.DATEAX$38);
        }
    }
    
    public void setDateAxArray(final CTDateAx[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTPlotAreaImpl.DATEAX$38);
    }
    
    public void setDateAxArray(final int n, final CTDateAx ctDateAx) {
        this.generatedSetterHelperImpl((XmlObject)ctDateAx, CTPlotAreaImpl.DATEAX$38, n, (short)2);
    }
    
    public CTDateAx insertNewDateAx(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTDateAx)this.get_store().insert_element_user(CTPlotAreaImpl.DATEAX$38, n);
        }
    }
    
    public CTDateAx addNewDateAx() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTDateAx)this.get_store().add_element_user(CTPlotAreaImpl.DATEAX$38);
        }
    }
    
    public void removeDateAx(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTPlotAreaImpl.DATEAX$38, n);
        }
    }
    
    public List<CTSerAx> getSerAxList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class SerAxList extends AbstractList<CTSerAx>
            {
                @Override
                public CTSerAx get(final int n) {
                    return CTPlotAreaImpl.this.getSerAxArray(n);
                }
                
                @Override
                public CTSerAx set(final int n, final CTSerAx ctSerAx) {
                    final CTSerAx serAxArray = CTPlotAreaImpl.this.getSerAxArray(n);
                    CTPlotAreaImpl.this.setSerAxArray(n, ctSerAx);
                    return serAxArray;
                }
                
                @Override
                public void add(final int n, final CTSerAx ctSerAx) {
                    CTPlotAreaImpl.this.insertNewSerAx(n).set((XmlObject)ctSerAx);
                }
                
                @Override
                public CTSerAx remove(final int n) {
                    final CTSerAx serAxArray = CTPlotAreaImpl.this.getSerAxArray(n);
                    CTPlotAreaImpl.this.removeSerAx(n);
                    return serAxArray;
                }
                
                @Override
                public int size() {
                    return CTPlotAreaImpl.this.sizeOfSerAxArray();
                }
            }
            return new SerAxList();
        }
    }
    
    @Deprecated
    public CTSerAx[] getSerAxArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTPlotAreaImpl.SERAX$40, (List)list);
            final CTSerAx[] array = new CTSerAx[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTSerAx getSerAxArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTSerAx ctSerAx = (CTSerAx)this.get_store().find_element_user(CTPlotAreaImpl.SERAX$40, n);
            if (ctSerAx == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctSerAx;
        }
    }
    
    public int sizeOfSerAxArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTPlotAreaImpl.SERAX$40);
        }
    }
    
    public void setSerAxArray(final CTSerAx[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTPlotAreaImpl.SERAX$40);
    }
    
    public void setSerAxArray(final int n, final CTSerAx ctSerAx) {
        this.generatedSetterHelperImpl((XmlObject)ctSerAx, CTPlotAreaImpl.SERAX$40, n, (short)2);
    }
    
    public CTSerAx insertNewSerAx(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTSerAx)this.get_store().insert_element_user(CTPlotAreaImpl.SERAX$40, n);
        }
    }
    
    public CTSerAx addNewSerAx() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTSerAx)this.get_store().add_element_user(CTPlotAreaImpl.SERAX$40);
        }
    }
    
    public void removeSerAx(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTPlotAreaImpl.SERAX$40, n);
        }
    }
    
    public CTDTable getDTable() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTDTable ctdTable = (CTDTable)this.get_store().find_element_user(CTPlotAreaImpl.DTABLE$42, 0);
            if (ctdTable == null) {
                return null;
            }
            return ctdTable;
        }
    }
    
    public boolean isSetDTable() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTPlotAreaImpl.DTABLE$42) != 0;
        }
    }
    
    public void setDTable(final CTDTable ctdTable) {
        this.generatedSetterHelperImpl((XmlObject)ctdTable, CTPlotAreaImpl.DTABLE$42, 0, (short)1);
    }
    
    public CTDTable addNewDTable() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTDTable)this.get_store().add_element_user(CTPlotAreaImpl.DTABLE$42);
        }
    }
    
    public void unsetDTable() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTPlotAreaImpl.DTABLE$42, 0);
        }
    }
    
    public CTShapeProperties getSpPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTShapeProperties ctShapeProperties = (CTShapeProperties)this.get_store().find_element_user(CTPlotAreaImpl.SPPR$44, 0);
            if (ctShapeProperties == null) {
                return null;
            }
            return ctShapeProperties;
        }
    }
    
    public boolean isSetSpPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTPlotAreaImpl.SPPR$44) != 0;
        }
    }
    
    public void setSpPr(final CTShapeProperties ctShapeProperties) {
        this.generatedSetterHelperImpl((XmlObject)ctShapeProperties, CTPlotAreaImpl.SPPR$44, 0, (short)1);
    }
    
    public CTShapeProperties addNewSpPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTShapeProperties)this.get_store().add_element_user(CTPlotAreaImpl.SPPR$44);
        }
    }
    
    public void unsetSpPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTPlotAreaImpl.SPPR$44, 0);
        }
    }
    
    public CTExtensionList getExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTExtensionList list = (CTExtensionList)this.get_store().find_element_user(CTPlotAreaImpl.EXTLST$46, 0);
            if (list == null) {
                return null;
            }
            return list;
        }
    }
    
    public boolean isSetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTPlotAreaImpl.EXTLST$46) != 0;
        }
    }
    
    public void setExtLst(final CTExtensionList list) {
        this.generatedSetterHelperImpl((XmlObject)list, CTPlotAreaImpl.EXTLST$46, 0, (short)1);
    }
    
    public CTExtensionList addNewExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTExtensionList)this.get_store().add_element_user(CTPlotAreaImpl.EXTLST$46);
        }
    }
    
    public void unsetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTPlotAreaImpl.EXTLST$46, 0);
        }
    }
    
    static {
        LAYOUT$0 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "layout");
        AREACHART$2 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "areaChart");
        AREA3DCHART$4 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "area3DChart");
        LINECHART$6 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "lineChart");
        LINE3DCHART$8 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "line3DChart");
        STOCKCHART$10 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "stockChart");
        RADARCHART$12 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "radarChart");
        SCATTERCHART$14 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "scatterChart");
        PIECHART$16 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "pieChart");
        PIE3DCHART$18 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "pie3DChart");
        DOUGHNUTCHART$20 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "doughnutChart");
        BARCHART$22 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "barChart");
        BAR3DCHART$24 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "bar3DChart");
        OFPIECHART$26 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "ofPieChart");
        SURFACECHART$28 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "surfaceChart");
        SURFACE3DCHART$30 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "surface3DChart");
        BUBBLECHART$32 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "bubbleChart");
        VALAX$34 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "valAx");
        CATAX$36 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "catAx");
        DATEAX$38 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "dateAx");
        SERAX$40 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "serAx");
        DTABLE$42 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "dTable");
        SPPR$44 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "spPr");
        EXTLST$46 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "extLst");
    }
}
