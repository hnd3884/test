package com.adventnet.client.components.chart.web;

import java.text.FieldPosition;
import java.text.MessageFormat;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Connection;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.util.SortOrder;
import org.jfree.chart.plot.PlotOrientation;
import java.awt.Stroke;
import org.jfree.chart.title.TextTitle;
import java.awt.Paint;
import org.jfree.chart.labels.CategoryToolTipGenerator;
import org.jfree.chart.labels.PieToolTipGenerator;
import java.util.LinkedList;
import com.adventnet.mfw.bean.BeanUtil;
import com.adventnet.persistence.ReadOnlyPersistence;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.urls.PieURLGenerator;
import org.jfree.chart.renderer.category.AbstractCategoryItemRenderer;
import org.jfree.chart.imagemap.ToolTipTagFragmentGenerator;
import org.jfree.chart.servlet.ServletUtilities;
import org.jfree.chart.imagemap.URLTagFragmentGenerator;
import org.jfree.chart.imagemap.ImageMapUtilities;
import org.jfree.chart.imagemap.StandardURLTagFragmentGenerator;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.entity.StandardEntityCollection;
import com.adventnet.client.components.chart.table.IntervalXYModelAdapter;
import com.adventnet.client.components.chart.table.XYModelAdapter;
import com.adventnet.client.components.chart.table.CustomXYDataset;
import com.adventnet.client.components.chart.table.internal.FilterUtil;
import com.adventnet.client.components.chart.table.CategoryModelAdapter;
import com.adventnet.client.components.chart.table.PieModelAdapter;
import org.jfree.data.general.Dataset;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.labels.XYToolTipGenerator;
import org.jfree.chart.labels.CategoryItemLabelGenerator;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import java.text.NumberFormat;
import org.jfree.chart.urls.CategoryURLGenerator;
import java.awt.Font;
import org.jfree.chart.plot.DrawingSupplier;
import com.adventnet.client.components.chart.table.ChartDrawingSupplier;
import com.adventnet.client.components.chart.table.MCKDataset;
import org.jfree.chart.renderer.xy.ClusteredXYBarRenderer;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.chart.renderer.xy.YIntervalRenderer;
import org.jfree.chart.axis.DateAxis;
import org.jfree.data.xy.IntervalXYDataset;
import org.jfree.chart.labels.XYItemLabelGenerator;
import org.jfree.chart.renderer.xy.XYStepRenderer;
import org.jfree.chart.renderer.xy.StackedXYAreaRenderer;
import org.jfree.chart.renderer.xy.XYAreaRenderer;
import org.jfree.chart.urls.XYURLGenerator;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.chart.urls.StandardXYURLGenerator;
import org.jfree.chart.renderer.xy.CandlestickRenderer;
import org.jfree.chart.renderer.xy.HighLowRenderer;
import org.jfree.chart.renderer.xy.XYBubbleRenderer;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.PolarItemRenderer;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.PolarPlot;
import org.jfree.chart.renderer.DefaultPolarItemRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.chart.plot.SpiderWebPlot;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.chart.renderer.category.BarRenderer3D;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.StackedBarRenderer3D;
import org.jfree.chart.renderer.category.StackedBarRenderer;
import org.jfree.chart.renderer.category.StackedAreaRenderer;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.AreaRenderer;
import org.jfree.chart.axis.NumberAxis3D;
import org.jfree.chart.axis.CategoryAxis3D;
import org.jfree.chart.axis.Axis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.data.category.CategoryDataset;
import org.jfree.chart.urls.StandardCategoryURLGenerator;
import org.jfree.ui.RectangleInsets;
import org.jfree.chart.labels.PieSectionLabelGenerator;
import org.jfree.chart.plot.PiePlot3D;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.PieDataset;
import com.adventnet.client.components.chart.table.GraphData;
import com.adventnet.client.components.chart.table.internal.MultiYSeriesTableAdapter;
import com.adventnet.client.components.chart.table.internal.SameXModelAdapter;
import org.jfree.chart.plot.Plot;
import com.adventnet.persistence.DataObject;
import com.adventnet.i18n.I18N;
import com.adventnet.client.components.chart.util.ChartUtil;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import com.adventnet.persistence.Row;
import com.adventnet.client.components.table.web.TableDatasetModel;
import com.adventnet.client.components.sql.SQLQueryAPI;
import org.jfree.chart.JFreeChart;
import javax.swing.table.TableModel;
import java.util.logging.Level;
import com.adventnet.client.view.web.ViewContext;
import java.util.logging.Logger;
import com.adventnet.client.tpl.TemplateAPI;
import com.adventnet.client.util.web.WebConstants;
import com.adventnet.client.view.web.DefaultViewController;

public class ChartController extends DefaultViewController implements WebConstants, ChartConstants, TemplateAPI.VariableHandler
{
    Logger logger;
    
    public ChartController() {
        this.logger = Logger.getLogger(ChartController.class.getName());
    }
    
    public void updateViewModel(final ViewContext viewCtx) throws Exception {
        this.logger.logp(Level.FINE, "ChartController", "updateViewModel", "Associated Dataobject with the view : {0}", viewCtx.getModel().getViewConfiguration());
        final TableModel tm = this.getTableModel(viewCtx);
        final JFreeChart chart = this.getChart(tm, viewCtx);
        if (viewCtx.getRenderType() == 3) {
            viewCtx.setViewModel((Object)chart);
        }
        else {
            final String imgURL = this.getImage(chart, viewCtx);
            viewCtx.setViewModel((Object)imgURL);
        }
    }
    
    protected TableModel getTableModel(final ViewContext ctxt) throws Exception {
        final String sql = this.getSQLString(ctxt);
        this.logger.logp(Level.FINE, "ChartController", "getTableModel", "SQL to fetch graph table model : {0}", sql);
        final TableDatasetModel tdm = SQLQueryAPI.getAsTableModel(sql, null);
        return (TableModel)tdm;
    }
    
    protected String getGraphType(final ViewContext viewCtx, final Row chartConfigRow) {
        String graphTypeVal = viewCtx.getRequest().getParameter("GRAPHTYPE");
        if (graphTypeVal == null) {
            graphTypeVal = (String)chartConfigRow.get(3);
        }
        this.logger.logp(Level.FINE, "ChartController", "getGraphType", "GRAPH typpe {0}", graphTypeVal);
        return graphTypeVal;
    }
    
    protected Map getAxisMap(final ViewContext viewCtx, final TableModel tm) throws Exception {
        final Map axisMap = new HashMap();
        Iterator iterator = viewCtx.getModel().getViewConfiguration().getRows("AxisColumn");
        while (iterator.hasNext()) {
            final Row row = iterator.next();
            row.set(2, (Object)((String)row.get(2)).intern());
            final String typeVal = (String)row.get(2);
            final int type = typeVal.hashCode();
            final String column = (String)row.get(3);
            if (type == ChartController.XAXIS) {
                axisMap.put("X", column);
            }
            else if (type == ChartController.YAXIS) {
                axisMap.put("Y", column);
            }
            else if (type == ChartController.ZAXIS) {
                axisMap.put("Z", column);
            }
            else if (type == ChartController.SERIES) {
                axisMap.put("SERIES", column);
            }
            else if (type == ChartController.XRANGESTART) {
                axisMap.put("X_START", column);
            }
            else if (type == ChartController.XRANGEEND) {
                axisMap.put("X_END", column);
            }
            else if (type == ChartController.YRANGESTART) {
                axisMap.put("Y_START", column);
            }
            else {
                if (type != ChartController.YRANGEEND) {
                    continue;
                }
                axisMap.put("Y_END", column);
            }
        }
        iterator = viewCtx.getModel().getViewConfiguration().getRows("YSeriesColumn");
        final List yColumns = new ArrayList();
        while (iterator.hasNext()) {
            final Row row2 = iterator.next();
            yColumns.add(row2.get("DATACOLUMN"));
        }
        if (yColumns.size() > 1) {
            this.logger.logp(Level.FINE, "ChartController", "getAxisMap", "multi Y series columns are {0}", yColumns);
            String[] arr = new String[yColumns.size()];
            arr = yColumns.toArray(arr);
            axisMap.put("Y", arr);
        }
        this.logger.logp(Level.FINE, "ChartController", "getAxisMap", "AxisMap {0}", axisMap);
        return axisMap;
    }
    
    public JFreeChart getChart(TableModel tm, final ViewContext viewCtx) throws Exception {
        final DataObject viewConfigDO = viewCtx.getModel().getViewConfiguration();
        final Row chartConfigRow = viewConfigDO.getRow("ChartViewConfig");
        final String graphTypeVal = this.getGraphType(viewCtx, chartConfigRow);
        final Map chartProperties = this.retrieveChartProperties(viewCtx, chartConfigRow, graphTypeVal);
        this.logger.logp(Level.FINE, "ChartController", "getChart", "retrieved ChartProperties are {0}", chartProperties);
        viewCtx.setTransientState("CCCHART_PROPERTIESL", (Object)chartProperties);
        final int graphType = graphTypeVal.hashCode();
        final Map axisMap = this.getAxisMap(viewCtx, tm);
        final Object col = axisMap.get("Y");
        if (!(col instanceof String[]) && graphType == ChartController.XYSTACKEDAREA && tm.getRowCount() > 0) {
            tm = this.getStackedXYTableModel(tm, axisMap);
        }
        if (axisMap.containsKey("X_START") && axisMap.containsKey("X_END") && !axisMap.containsKey("Y_START") && axisMap.containsKey("Y")) {
            axisMap.put("Y_START", axisMap.get("Y"));
            axisMap.remove("Y");
        }
        String title = ChartUtil.getPropValue(viewCtx, graphTypeVal, "CHART_TITLE");
        if ((title == null || title.equals("")) && viewCtx.getRenderType() == 3) {
            final String temp = ChartUtil.getPropValue(viewCtx, graphTypeVal, "HEAD_LABEL");
            if (temp != null) {
                title = temp;
            }
        }
        I18N.getMsg(title, new Object[0]);
        this.fillI18nValues(tm);
        final Plot plot = this.getPlot(viewCtx, tm, axisMap, graphTypeVal);
        final JFreeChart chart = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT, plot, !"false".equals(ChartUtil.getPropValue(viewCtx, graphTypeVal, "SHOW_LEGEND")));
        chart.getPlot().setForegroundAlpha(0.5f);
        final String url = this.getURL(viewCtx, chartConfigRow);
        if (url != null) {
            viewCtx.setTransientState("GRAPHCCURL", (Object)url);
            this.setBaseURL(url, chart.getPlot(), axisMap, graphType);
        }
        this.applyComponentProperties(graphTypeVal, chartProperties, chart, viewCtx);
        return chart;
    }
    
    protected TableModel getStackedXYTableModel(TableModel tm, final Map axisMap) throws Exception {
        this.logger.logp(Level.FINE, "ChartController", "getChart", "Assigning SAME X point fori all yvalues in stacked X Area");
        final SameXModelAdapter sameModel = new SameXModelAdapter(tm, axisMap.get("X"), axisMap.get("Y"), axisMap.get("SERIES"));
        final int cc = sameModel.getColumnCount();
        final String[] yColumns = new String[cc - 1];
        for (int i = 1; i < cc; ++i) {
            yColumns[i - 1] = sameModel.getColumnName(i);
        }
        this.logger.logp(Level.FINE, "ChartController", "getChart", "SameXModelAdapter {0}", sameModel);
        this.logger.logp(Level.FINE, "ChartController", "getChart", "Assigning multi y  series table model when graph is STACKED Area");
        tm = new MultiYSeriesTableAdapter(sameModel, yColumns);
        this.logger.logp(Level.FINE, "ChartController", "getChart", "MultiYSeriesTableAdapter in stacked area {0}", tm);
        axisMap.put("Y", tm.getColumnName(0));
        axisMap.put("SERIES", tm.getColumnName(1));
        return tm;
    }
    
    public Plot getPlot(final ViewContext viewCtx, final TableModel tm, final Map axisMap, final String graphType) throws Exception {
        final DataObject viewConfigDO = viewCtx.getModel().getViewConfiguration();
        final Row chartConfigRow = viewConfigDO.getRow("ChartViewConfig");
        final int graphTypeHCode = graphType.hashCode();
        final boolean isXTime = (boolean)chartConfigRow.get(5);
        String xLabel = ChartUtil.getPropValue(viewCtx, graphType, "CHART_X_LABEL");
        xLabel = I18N.getMsg(xLabel, new Object[0]);
        String yLabel = ChartUtil.getPropValue(viewCtx, graphType, "CHART_Y_LABEL");
        yLabel = I18N.getMsg(yLabel, new Object[0]);
        Plot plot = null;
        final GraphData graphData = new GraphData(tm, axisMap);
        Object mAdapter = null;
        viewCtx.setTransientState("GRAPHCCDATA", (Object)graphData);
        if (graphTypeHCode == ChartController.PIE || graphTypeHCode == ChartController.PIE3D || graphTypeHCode == ChartController.AREA || graphTypeHCode == ChartController.BAR || graphTypeHCode == ChartController.BAR3D || graphTypeHCode == ChartController.CATLINE || graphTypeHCode == ChartController.STACKEDAREA || graphTypeHCode == ChartController.STACKEDBAR || graphTypeHCode == ChartController.STACKEDBAR3D || graphTypeHCode == ChartController.LINE3D || graphTypeHCode == ChartController.LINE) {
            if (graphTypeHCode == ChartController.PIE || graphTypeHCode == ChartController.PIE3D) {
                final PieDataset data = (PieDataset)this.getDataSet(viewCtx, graphData, graphTypeHCode);
                PiePlot piePlot = null;
                if (graphTypeHCode == ChartController.PIE) {
                    piePlot = new PiePlot(data);
                    piePlot.setShadowXOffset(0.0);
                    piePlot.setShadowYOffset(0.0);
                }
                else {
                    piePlot = (PiePlot)new PiePlot3D(data);
                }
                plot = (Plot)piePlot;
                mAdapter = data;
                piePlot.setIgnoreZeroValues(true);
                piePlot.setIgnoreNullValues(true);
                piePlot.setLabelGenerator((PieSectionLabelGenerator)this.getPieLabelFormat(viewCtx, graphType));
                final String enableTooltip = ChartUtil.getPropValue(viewCtx, graphType, "SHOW_TOOLTIP");
                if (enableTooltip != null && "true".equals(enableTooltip)) {
                    piePlot.setToolTipGenerator(this.getPieToolTipGenerator(axisMap));
                }
                final String value = ChartUtil.getPropValue(viewCtx, graphType, "AXIS_LABEL_FONT");
                if (value != null) {
                    final Font f = ChartUtil.getFont(value);
                    piePlot.setLabelFont(f);
                }
                plot.setInsets(new RectangleInsets(0.0, 5.0, 5.0, 5.0));
            }
            else {
                final CategoryURLGenerator urlGenerator = (CategoryURLGenerator)new StandardCategoryURLGenerator(this.getBaseURL(), "seriesName", "X");
                final CategoryDataset modelAdapter = (CategoryDataset)(mAdapter = this.getDataSet(viewCtx, graphData, graphTypeHCode));
                final CategoryAxis domainAxis = new CategoryAxis(xLabel);
                final ValueAxis rangeAxis = (ValueAxis)new NumberAxis(yLabel);
                this.setAxisFonts(viewCtx, (Axis)domainAxis, graphType, true);
                this.setAxisFonts(viewCtx, (Axis)rangeAxis, graphType, false);
                final NumberFormat formatter = this.getFormatForRangeAxis(viewCtx, graphType);
                ((NumberAxis)rangeAxis).setNumberFormatOverride(formatter);
                final CategoryAxis domainAxis3D = (CategoryAxis)new CategoryAxis3D(xLabel);
                final ValueAxis rangeAxis3D = (ValueAxis)new NumberAxis3D(yLabel);
                ((NumberAxis)rangeAxis3D).setNumberFormatOverride(formatter);
                CategoryItemRenderer renderer = null;
                if (graphTypeHCode == ChartController.AREA) {
                    renderer = (CategoryItemRenderer)new AreaRenderer();
                    plot = (Plot)new CategoryPlot(modelAdapter, domainAxis, rangeAxis, renderer);
                }
                else if (graphTypeHCode == ChartController.STACKEDAREA) {
                    renderer = (CategoryItemRenderer)new StackedAreaRenderer();
                    plot = (Plot)new CategoryPlot(modelAdapter, domainAxis, rangeAxis, renderer);
                }
                else if (graphTypeHCode == ChartController.STACKEDBAR) {
                    renderer = (CategoryItemRenderer)new StackedBarRenderer();
                    plot = (Plot)new CategoryPlot(modelAdapter, domainAxis, rangeAxis, renderer);
                    ((StackedBarRenderer)renderer).setMaximumBarWidth(0.07);
                }
                else if (graphTypeHCode == ChartController.STACKEDBAR3D) {
                    renderer = (CategoryItemRenderer)new StackedBarRenderer3D();
                    plot = (Plot)new CategoryPlot(modelAdapter, domainAxis3D, rangeAxis3D, renderer);
                    ((StackedBarRenderer3D)renderer).setMaximumBarWidth(0.07);
                }
                else if (graphTypeHCode == ChartController.BAR) {
                    renderer = (CategoryItemRenderer)new BarRenderer();
                    plot = (Plot)new CategoryPlot(modelAdapter, domainAxis, rangeAxis, renderer);
                    ((BarRenderer)renderer).setMaximumBarWidth(0.07);
                }
                else if (graphTypeHCode == ChartController.BAR3D) {
                    renderer = (CategoryItemRenderer)new BarRenderer3D();
                    plot = (Plot)new CategoryPlot(modelAdapter, domainAxis3D, rangeAxis3D, renderer);
                    ((BarRenderer3D)renderer).setMaximumBarWidth(0.07);
                }
                else if (graphTypeHCode == ChartController.CATLINE || graphTypeHCode == ChartController.LINE || graphTypeHCode == ChartController.LINE3D) {
                    renderer = (CategoryItemRenderer)new LineAndShapeRenderer(true, true);
                    plot = (Plot)new CategoryPlot(modelAdapter, domainAxis, rangeAxis, renderer);
                }
                final CategoryItemLabelGenerator labelGenerator = this.getCategoryLabelGenerator(viewCtx, graphType);
                if (labelGenerator != null) {
                    renderer.setItemLabelsVisible(true);
                    renderer.setItemLabelGenerator(labelGenerator);
                }
                else {
                    renderer.setItemLabelsVisible(false);
                }
                final String enableTooltip2 = ChartUtil.getPropValue(viewCtx, graphType, "SHOW_TOOLTIP");
                if (enableTooltip2 != null && "true".equals(enableTooltip2)) {
                    renderer.setToolTipGenerator(this.getCategoryToolTipGenerator(axisMap));
                }
            }
        }
        else if (graphTypeHCode == ChartController.SPIDERWEB) {
            final CategoryURLGenerator urlGenerator = (CategoryURLGenerator)new StandardCategoryURLGenerator(this.getBaseURL(), "seriesName", "X");
            final CategoryDataset modelAdapter = (CategoryDataset)(mAdapter = this.getDataSet(viewCtx, graphData, graphTypeHCode));
            final SpiderWebPlot webPlot = new SpiderWebPlot(modelAdapter);
            webPlot.setInteriorGap(0.4);
            final CategoryItemLabelGenerator labelGenerator2 = this.getCategoryLabelGenerator(viewCtx, graphType);
            if (labelGenerator2 != null) {
                webPlot.setLabelGenerator(labelGenerator2);
            }
            final String enableTooltip3 = ChartUtil.getPropValue(viewCtx, graphType, "SHOW_TOOLTIP");
            if (enableTooltip3 != null && "true".equals(enableTooltip3)) {
                webPlot.setToolTipGenerator(this.getCategoryToolTipGenerator(axisMap));
            }
            plot = (Plot)webPlot;
        }
        else if (graphTypeHCode == ChartController.POLAR) {
            final XYDataset modelAdapter2 = (XYDataset)this.getDataSet(viewCtx, graphData, graphTypeHCode);
            final NumberAxis yAxis = this.getNumberAxis(viewCtx, graphData, graphTypeHCode, yLabel, modelAdapter2);
            this.setAxisFonts(viewCtx, (Axis)yAxis, graphType, false);
            final NumberFormat formatter2 = this.getFormatForRangeAxis(viewCtx, graphType);
            yAxis.setNumberFormatOverride(formatter2);
            mAdapter = modelAdapter2;
            plot = (Plot)new PolarPlot(modelAdapter2, (ValueAxis)yAxis, (PolarItemRenderer)new DefaultPolarItemRenderer());
            if (graphTypeHCode == ChartController.XYSTACKEDAREA) {
                ((XYPlot)plot).setForegroundAlpha((float)new Float(0.5));
            }
        }
        else if (graphTypeHCode == ChartController.CANDLESTICK || graphTypeHCode == ChartController.OHLCCHART || graphTypeHCode == ChartController.BUBBLECHART || graphTypeHCode == ChartController.XYAREA || graphTypeHCode == ChartController.XYLINE || graphTypeHCode == ChartController.SCATTER || graphTypeHCode == ChartController.STEPCHART || graphTypeHCode == ChartController.XYSTACKEDAREA) {
            final XYToolTipGenerator tooltipGenerator = (XYToolTipGenerator)new ChartXYToolTipGenerator();
            XYItemRenderer renderer2 = null;
            final XYDataset modelAdapter3 = (XYDataset)this.getDataSet(viewCtx, graphData, graphTypeHCode);
            final ValueAxis xAxis = this.getValueAxis(viewCtx, graphData, graphTypeHCode, xLabel, isXTime, modelAdapter3);
            this.setAxisFonts(viewCtx, (Axis)xAxis, graphType, true);
            final NumberAxis yAxis2 = this.getNumberAxis(viewCtx, graphData, graphTypeHCode, yLabel, modelAdapter3);
            this.setAxisFonts(viewCtx, (Axis)yAxis2, graphType, false);
            final NumberFormat formatter3 = this.getFormatForRangeAxis(viewCtx, graphType);
            yAxis2.setNumberFormatOverride(formatter3);
            if (graphTypeHCode == ChartController.BUBBLECHART) {
                renderer2 = (XYItemRenderer)new XYBubbleRenderer(2);
            }
            else if (graphTypeHCode == ChartController.OHLCCHART) {
                renderer2 = (XYItemRenderer)new HighLowRenderer();
            }
            else if (graphTypeHCode == ChartController.CANDLESTICK) {
                renderer2 = (XYItemRenderer)new CandlestickRenderer();
                ((CandlestickRenderer)renderer2).setDrawVolume(false);
            }
            else if (graphTypeHCode == ChartController.XYLINE) {
                renderer2 = (XYItemRenderer)new StandardXYItemRenderer(3, tooltipGenerator, (XYURLGenerator)new StandardXYURLGenerator(this.getBaseURL(), "seriesName", "item"));
            }
            else if (graphTypeHCode == ChartController.XYAREA) {
                renderer2 = (XYItemRenderer)new XYAreaRenderer(4, tooltipGenerator, (XYURLGenerator)new StandardXYURLGenerator(this.getBaseURL(), "seriesName", "item"));
            }
            else if (graphTypeHCode == ChartController.XYSTACKEDAREA) {
                renderer2 = (XYItemRenderer)new StackedXYAreaRenderer(4, tooltipGenerator, (XYURLGenerator)new StandardXYURLGenerator(this.getBaseURL(), "seriesName", "item"));
            }
            else if (graphTypeHCode == ChartController.SCATTER) {
                renderer2 = (XYItemRenderer)new StandardXYItemRenderer(1, tooltipGenerator, (XYURLGenerator)new StandardXYURLGenerator(this.getBaseURL(), "seriesName", "item"));
                final StandardXYItemRenderer r = (StandardXYItemRenderer)renderer2;
                r.setBaseShapesVisible(true);
                r.setShapesFilled(true);
            }
            else if (graphTypeHCode == ChartController.STEPCHART) {
                renderer2 = (XYItemRenderer)new XYStepRenderer(tooltipGenerator, (XYURLGenerator)new StandardXYURLGenerator(this.getBaseURL(), "seriesName", "item"));
            }
            final String enableTooltip4 = ChartUtil.getPropValue(viewCtx, graphType, "SHOW_TOOLTIP");
            if (enableTooltip4 != null && "true".equals(enableTooltip4)) {
                renderer2.setToolTipGenerator(this.getXYTooltipGenerator(axisMap, isXTime));
            }
            mAdapter = modelAdapter3;
            plot = (Plot)new XYPlot(modelAdapter3, xAxis, (ValueAxis)yAxis2, renderer2);
            if (graphTypeHCode == ChartController.XYSTACKEDAREA) {
                ((XYPlot)plot).setForegroundAlpha((float)new Float(0.5));
            }
            renderer2.setItemLabelGenerator((XYItemLabelGenerator)new ChartXYLabelGenerator());
        }
        else if (graphTypeHCode == ChartController.BARCLUSTER || graphTypeHCode == ChartController.BAROVERLAID || graphTypeHCode == ChartController.YRANGELINE) {
            final IntervalXYDataset modelAdapter4 = (IntervalXYDataset)(mAdapter = this.getDataSet(viewCtx, graphData, graphTypeHCode));
            ValueAxis domainAxis2 = null;
            if (isXTime) {
                domainAxis2 = (ValueAxis)new DateAxis(xLabel);
            }
            else {
                domainAxis2 = (ValueAxis)new NumberAxis(xLabel);
            }
            final ValueAxis rangeAxis2 = (ValueAxis)new NumberAxis(yLabel);
            XYItemRenderer renderer3 = null;
            if (graphTypeHCode == ChartController.YRANGELINE) {
                renderer3 = (XYItemRenderer)new YIntervalRenderer();
            }
            else if (graphTypeHCode == ChartController.BAROVERLAID) {
                renderer3 = (XYItemRenderer)new XYBarRenderer(0.0);
            }
            else if (graphTypeHCode == ChartController.BARCLUSTER) {
                renderer3 = (XYItemRenderer)new ClusteredXYBarRenderer();
            }
            plot = (Plot)new XYPlot((XYDataset)modelAdapter4, domainAxis2, rangeAxis2, renderer3);
            ((XYPlot)plot).setRangeGridlinesVisible(false);
            final String enableTooltip3 = ChartUtil.getPropValue(viewCtx, graphType, "SHOW_TOOLTIP");
            if (enableTooltip3 != null && "true".equals(enableTooltip3)) {
                renderer3.setToolTipGenerator(this.getXYIntervalTooltipGenerator(axisMap));
            }
        }
        if (mAdapter instanceof MCKDataset) {
            plot.setDrawingSupplier((DrawingSupplier)new ChartDrawingSupplier(viewCtx, (MCKDataset)mAdapter, graphType));
        }
        return plot;
    }
    
    protected void setAxisFonts(final ViewContext viewCtx, final Axis axis, final String graphType, final boolean isXAxis) throws Exception {
        String value = ChartUtil.getPropValue(viewCtx, graphType, "AXIS_LABEL_FONT");
        if (value != null) {
            final Font f = ChartUtil.getFont(value);
            axis.setLabelFont(f);
        }
        value = ChartUtil.getPropValue(viewCtx, graphType, "AXIS_TICK_LABEL_FONT");
        if (value != null) {
            final Font f = ChartUtil.getFont(value);
            axis.setTickLabelFont(f);
        }
        if (isXAxis) {
            ChartUtil.getPropValue(viewCtx, graphType, "XAXIS_LABEL_ANGLE");
            if (value != null) {
                axis.setLabelAngle(Integer.parseInt(value) * 0.0174533);
            }
        }
    }
    
    protected NumberFormat getFormatForRangeAxis(final ViewContext viewCtx, final String graphTypeVal) {
        final String format = ChartUtil.getPropValue(viewCtx, graphTypeVal, "RANGE_AXIS_FORMAT");
        if ("CURRENCY".equals(format)) {
            return NumberFormat.getCurrencyInstance();
        }
        if ("PERCENT".equals(format)) {
            return NumberFormat.getPercentInstance();
        }
        return NumberFormat.getNumberInstance();
    }
    
    protected ValueAxis getValueAxis(final ViewContext viewCtx, final GraphData graphData, final int graphTypeHCode, final String xLabel, final boolean isXTime, final XYDataset modelAdapter) {
        ValueAxis xAxis = null;
        if (isXTime) {
            xAxis = (ValueAxis)new DateAxis(xLabel);
        }
        else {
            xAxis = (ValueAxis)new NumberAxis(xLabel);
            ((NumberAxis)xAxis).setAutoRangeIncludesZero(false);
        }
        return xAxis;
    }
    
    protected NumberAxis getNumberAxis(final ViewContext viewCtx, final GraphData graphData, final int graphTypeHCode, final String yLabel, final XYDataset modelAdapter) {
        final NumberAxis yAxis = new NumberAxis(yLabel);
        yAxis.setAutoRangeIncludesZero(false);
        return yAxis;
    }
    
    protected Dataset getDataSet(final ViewContext viewCtx, final GraphData graphData, final int graphTypeHCode) throws Exception {
        final Map axisMap = graphData.getAxisColumns();
        final Object yCol = axisMap.get("Y");
        if (graphTypeHCode == ChartController.PIE || graphTypeHCode == ChartController.PIE3D || graphTypeHCode == ChartController.AREA || graphTypeHCode == ChartController.BAR || graphTypeHCode == ChartController.BAR3D || graphTypeHCode == ChartController.CATLINE || graphTypeHCode == ChartController.STACKEDAREA || graphTypeHCode == ChartController.STACKEDBAR || graphTypeHCode == ChartController.STACKEDBAR3D || graphTypeHCode == ChartController.SPIDERWEB) {
            if (yCol instanceof String[]) {
                TableModel tm = graphData.getData();
                this.logger.logp(Level.FINE, "ChartController", "getChart", "Assigning multi y series table model");
                tm = new MultiYSeriesTableAdapter(tm, (String[])yCol);
                axisMap.put("Y", tm.getColumnName(0));
                axisMap.put("SERIES", tm.getColumnName(1));
            }
            if (graphTypeHCode == ChartController.PIE || graphTypeHCode == ChartController.PIE3D) {
                return (Dataset)new PieModelAdapter(graphData);
            }
            if (graphData.getAxisColumns().get("SERIES") == null) {
                graphData.getAxisColumns().put("SERIES", graphData.getAxisColumns().get("X"));
            }
            return (Dataset)new CategoryModelAdapter(graphData);
        }
        else if (graphTypeHCode == ChartController.XYAREA || graphTypeHCode == ChartController.XYLINE || graphTypeHCode == ChartController.SCATTER || graphTypeHCode == ChartController.STEPCHART || graphTypeHCode == ChartController.XYSTACKEDAREA || graphTypeHCode == ChartController.POLAR || graphTypeHCode == ChartController.BUBBLECHART) {
            if (yCol instanceof String[]) {
                final TableModel tm = graphData.getData();
                final String[] yCols = (String[])yCol;
                final int[] seriesIdx = new int[yCols.length];
                final int xIdx = FilterUtil.getFirstColumnIndex(tm, axisMap.get("X"));
                int zIdx = -1;
                if (axisMap.get("Z") != null) {
                    zIdx = FilterUtil.getFirstColumnIndex(tm, axisMap.get("Z"));
                }
                for (int serSize = yCols.length, i = 0; i < serSize; ++i) {
                    seriesIdx[i] = FilterUtil.getFirstColumnIndex(tm, yCols[i]);
                }
                return (Dataset)new CustomXYDataset(tm, seriesIdx, xIdx, zIdx, false);
            }
            return (Dataset)new XYModelAdapter(graphData);
        }
        else {
            if (graphTypeHCode == ChartController.BARCLUSTER || graphTypeHCode == ChartController.BAROVERLAID || graphTypeHCode == ChartController.YRANGELINE) {
                return (Dataset)new IntervalXYModelAdapter(graphData);
            }
            String graphType = graphTypeHCode + "";
            if (graphTypeHCode == ChartController.CONTOURCHART) {
                graphType = "CONTOURCHART";
            }
            else if (graphTypeHCode == ChartController.BOXANDWHISKER) {
                graphType = "BOXANDWHISKER";
            }
            else if (graphTypeHCode == ChartController.CANDLESTICK) {
                graphType = "CANDLESTICK";
            }
            else if (graphTypeHCode == ChartController.OHLCCHART) {
                graphType = "OHLCCHART";
            }
            throw new RuntimeException("Graphtype : " + graphType + " is not supported in default implementation. You need top supply this data set by custom controller class");
        }
    }
    
    public String getImage(final JFreeChart chart, final ViewContext viewCtx) throws Exception {
        final DataObject viewConfigDO = viewCtx.getModel().getViewConfiguration();
        final Row chartConfigRow = viewConfigDO.getRow("ChartViewConfig");
        final String graphTypeVal = this.getGraphType(viewCtx, chartConfigRow);
        final String strWidth = ChartUtil.getPropValue(viewCtx, graphTypeVal, "WIDTH");
        final String strHeight = ChartUtil.getPropValue(viewCtx, graphTypeVal, "HEIGHT");
        int width = 300;
        int height = 200;
        if (strWidth != null) {
            width = Integer.parseInt(strWidth);
        }
        if (strHeight != null) {
            height = Integer.parseInt(strHeight);
        }
        final ChartRenderingInfo info = new ChartRenderingInfo((EntityCollection)new StandardEntityCollection());
        final String fileName = this.saveGraphImage(viewCtx, chart, width, height, info);
        final String url = this.getURL(viewCtx, chartConfigRow);
        String imageMap = null;
        if (url != null) {
            imageMap = ImageMapUtilities.getImageMap(fileName, info, this.getToolTipTagFragmentGenerator(), (URLTagFragmentGenerator)new StandardURLTagFragmentGenerator());
        }
        else {
            imageMap = ImageMapUtilities.getImageMap(fileName, info, this.getToolTipTagFragmentGenerator(), (URLTagFragmentGenerator)null);
        }
        viewCtx.setTransientState("CCIMAGE_MAP", (Object)imageMap);
        return fileName;
    }
    
    protected String saveGraphImage(final ViewContext viewCtx, final JFreeChart chart, final int width, final int height, final ChartRenderingInfo info) throws Exception {
        return ServletUtilities.saveChartAsPNG(chart, width, height, info, viewCtx.getRequest().getSession());
    }
    
    private void fillI18nValues(final TableModel tm) throws Exception {
        final int noOfRow = tm.getRowCount();
        final int noOfColumn = tm.getColumnCount();
        String colData = "";
        for (int row_idx = 0; row_idx < noOfRow; ++row_idx) {
            for (int col_idx = 0; col_idx < noOfColumn; ++col_idx) {
                final Object value = tm.getValueAt(row_idx, col_idx);
                if (value instanceof String) {
                    colData = I18N.getMsg(value.toString(), new Object[0]);
                    tm.setValueAt(colData, row_idx, col_idx);
                }
            }
        }
    }
    
    protected ToolTipTagFragmentGenerator getToolTipTagFragmentGenerator() {
        return (ToolTipTagFragmentGenerator)new DefaultMCKTooltipFragGenerator();
    }
    
    public void setBaseURL(final String baseURL, final Plot plot, final Map axisMap, final int graphType) {
        if (baseURL != null) {
            if (plot instanceof PiePlot) {
                final PieURLGenerator urlGenerator = (PieURLGenerator)new ChartPieURLGenerator(baseURL, axisMap.get("X"));
                ((PiePlot)plot).setURLGenerator(urlGenerator);
            }
            else if (plot instanceof CategoryPlot) {
                final CategoryURLGenerator urlGenerator2 = (CategoryURLGenerator)new ChartCategoryURLGenerator(baseURL, axisMap.get("SERIES"), axisMap.get("X"), axisMap.get("Y"));
                final AbstractCategoryItemRenderer renderer = (AbstractCategoryItemRenderer)((CategoryPlot)plot).getRenderer();
                renderer.setBaseItemURLGenerator(urlGenerator2);
            }
            else if (plot instanceof XYPlot) {
                final XYItemRenderer renderer2 = ((XYPlot)plot).getRenderer();
                XYURLGenerator urlGen = null;
                if (graphType == ChartController.BARCLUSTER || graphType == ChartController.BAROVERLAID || graphType == ChartController.YRANGELINE) {
                    urlGen = (XYURLGenerator)new ChartIntervalURLGenerator(baseURL, axisMap.get("SERIES"), axisMap.get("X_START"), axisMap.get("X_END"), axisMap.get("Y_START"), axisMap.get("Y_END"));
                }
                else {
                    final Object yCol = axisMap.get("Y");
                    if (yCol instanceof String[]) {
                        urlGen = (XYURLGenerator)new ChartXYURLGenerator(false, baseURL, axisMap.get("X"), axisMap.get("Z"), true);
                    }
                    else {
                        urlGen = (XYURLGenerator)new ChartXYURLGenerator(false, baseURL, axisMap.get("SERIES"), axisMap.get("X"), axisMap.get("Y"), axisMap.get("Z"));
                    }
                }
                renderer2.setURLGenerator(urlGen);
            }
        }
    }
    
    protected StandardPieSectionLabelGenerator getPieLabelFormat(final ViewContext viewCtx, final String graphType) {
        final String formatType = ChartUtil.getPropValue(viewCtx, graphType, "LABEL_FORMAT");
        String format = "{0}";
        if ("L_P".equals(formatType)) {
            format = "{0} = {2}";
        }
        else if ("L_V_P".equals(formatType)) {
            format = "{0} = {1} ({2})";
        }
        else if ("V_P".equals(formatType)) {
            format = "{1} ({2})";
        }
        else if ("V".equals(formatType)) {
            format = "{1}";
        }
        else if ("P".equals(formatType)) {
            format = "{2}";
        }
        else {
            if ("N".equals(formatType)) {
                return null;
            }
            if ("L_V".equals(formatType)) {
                format = "{0} = {1}";
            }
        }
        return new StandardPieSectionLabelGenerator(format, this.getFormatForRangeAxis(viewCtx, graphType), NumberFormat.getPercentInstance());
    }
    
    protected CategoryItemLabelGenerator getCategoryLabelGenerator(final ViewContext viewCtx, final String graphType) {
        final String formatType = ChartUtil.getPropValue(viewCtx, graphType, "LABEL_FORMAT");
        String format = "{2}";
        if ("L_V".equals(formatType)) {
            format = "{1} = {2}";
        }
        else if ("S_V".equals(formatType)) {
            format = "{0} = {2}";
        }
        else if ("V".equals(formatType)) {
            format = "{2}";
        }
        else if ("N".equals(formatType)) {
            return null;
        }
        return (CategoryItemLabelGenerator)new StandardCategoryItemLabelGenerator(format, this.getFormatForRangeAxis(viewCtx, graphType));
    }
    
    protected Map retrieveChartProperties(final ViewContext viewCtx, final Row chartViewRow, final String graphTypeVal) throws Exception {
        final HashMap propMap = new HashMap();
        final HashMap graphMap = new HashMap();
        final Object propSet = chartViewRow.get(4);
        if (propSet != null) {
            final List tables = new ArrayList();
            tables.add("ChartPropertySet");
            tables.add("ChartProperty");
            tables.add("ChartPropKey");
            final ReadOnlyPersistence persistence = (ReadOnlyPersistence)BeanUtil.lookup("CachedPersistence");
            final LinkedList que = new LinkedList();
            this.fetchAndAdd(propSet, que, persistence, tables);
            for (int size = que.size(), i = 0; i < size; ++i) {
                final DataObject data = que.removeLast();
                final Iterator iterator = data.getRows("ChartProperty");
                while (iterator.hasNext()) {
                    final Row propRow = iterator.next();
                    Object key = propRow.get("NAME");
                    final Object gType = propRow.get("GRAPHTYPE");
                    final Row pkRow = new Row("ChartPropKey");
                    pkRow.set(1, key);
                    final Row keyRow = data.getRow("ChartPropKey", pkRow);
                    key = keyRow.get(2);
                    if (((String)gType).equals(graphTypeVal)) {
                        graphMap.put(key, propRow);
                    }
                    propMap.put(key, propRow);
                }
            }
        }
        this.setBaseProperties(propMap);
        return propMap;
    }
    
    protected XYToolTipGenerator getXYTooltipGenerator(final Map axisMap, final boolean isXTime) {
        XYToolTipGenerator toolTipGen = null;
        final Object yCol = axisMap.get("Y");
        if (yCol instanceof String[]) {
            toolTipGen = (XYToolTipGenerator)new ChartXYURLGenerator(isXTime, null, axisMap.get("X"), axisMap.get("Z"), true);
        }
        else {
            toolTipGen = (XYToolTipGenerator)new ChartXYURLGenerator(isXTime, null, axisMap.get("SERIES"), axisMap.get("X"), axisMap.get("Y"), axisMap.get("Z"));
        }
        return toolTipGen;
    }
    
    protected XYToolTipGenerator getXYIntervalTooltipGenerator(final Map axisMap) {
        final XYToolTipGenerator toolTipGen = (XYToolTipGenerator)new ChartIntervalURLGenerator(null, axisMap.get("SERIES"), axisMap.get("X_START"), axisMap.get("X_END"), axisMap.get("Y_START"), axisMap.get("Y_END"));
        return toolTipGen;
    }
    
    protected PieToolTipGenerator getPieToolTipGenerator(final Map axisMap) {
        final PieToolTipGenerator toolTipGen = (PieToolTipGenerator)new ChartPieURLGenerator(null, axisMap.get("X"), axisMap.get("Y"));
        return toolTipGen;
    }
    
    protected CategoryToolTipGenerator getCategoryToolTipGenerator(final Map axisMap) {
        final CategoryToolTipGenerator toolTipGen = (CategoryToolTipGenerator)new ChartCategoryURLGenerator(null, axisMap.get("SERIES"), axisMap.get("X"), axisMap.get("Y"));
        return toolTipGen;
    }
    
    protected void fetchAndAdd(final Object set, final List que, final ReadOnlyPersistence persistence, final List tables) throws Exception {
        final Row pkRow = new Row("ChartPropertySet");
        pkRow.set(1, set);
        pkRow.set(1, set);
        final DataObject data = persistence.get(tables, pkRow);
        que.add(data);
        final Object parentSet = data.getFirstValue("ChartPropertySet", "PARENTSET");
        if (parentSet != null) {
            this.fetchAndAdd(parentSet, que, persistence, tables);
        }
    }
    
    protected String getURL(final ViewContext viewCtxt, final Row chartViewRow) {
        final String url = (String)chartViewRow.get(6);
        final int scope = chartViewRow.get(7).hashCode();
        if (scope == ChartController.STATIC) {
            return url;
        }
        String value = null;
        if (scope == ChartController.STATE) {
            value = (String)viewCtxt.getStateParameter(url);
        }
        else if (scope == ChartController.REQUEST) {
            value = viewCtxt.getRequest().getParameter(url);
            if (value == null) {
                final Object temp = viewCtxt.getRequest().getAttribute(url);
                if (temp != null) {
                    value = temp.toString();
                }
            }
        }
        else if (scope == ChartConstants.SESSION) {
            value = (String)viewCtxt.getRequest().getSession().getAttribute(url);
        }
        return value;
    }
    
    protected void applyComponentProperties(final String graphType, final Map ht, final JFreeChart chart, final ViewContext viewCtxt) {
        if (chart == null) {
            return;
        }
        try {
            final Plot plot = chart.getPlot();
            final int graphTypeHCode = graphType.hashCode();
            final String bgColor = ChartUtil.getPropValue(viewCtxt, graphType, "CHART_BG_COLOR");
            if (bgColor != null) {
                chart.setBackgroundPaint((Paint)ChartUtil.getColorFromHex(bgColor));
            }
            String title = ChartUtil.getPropValue(viewCtxt, graphType, "CHART_TITLE");
            if (title != null) {
                title = I18N.getMsg(title, new Object[0]);
                chart.setTitle(new TextTitle(title));
            }
            String value = ChartUtil.getPropValue(viewCtxt, graphType, "PLOT_BG_COLOR");
            if (value != null) {
                plot.setBackgroundPaint((Paint)ChartUtil.getColorFromHex(value));
            }
            value = ChartUtil.getPropValue(viewCtxt, graphType, "PLOT_FG_ALPHA");
            if (value != null) {
                plot.setForegroundAlpha((float)new Float(value));
            }
            value = ChartUtil.getPropValue(viewCtxt, graphType, "PLOT_OUTLINE_COLOR");
            if (value != null) {
                plot.setOutlinePaint((Paint)ChartUtil.getColorFromHex(value));
            }
            value = ChartUtil.getPropValue(viewCtxt, graphType, "PLOT_OUTLINE_STROKE");
            if (value != null) {
                plot.setOutlineStroke((Stroke)ChartUtil.getStroke(value));
            }
            value = ChartUtil.getPropValue(viewCtxt, graphType, "PLOT_ORIENTATION");
            if (value != null) {
                PlotOrientation orientation = null;
                final String strVal = value;
                if (strVal.trim().equalsIgnoreCase("x")) {
                    orientation = PlotOrientation.HORIZONTAL;
                }
                else if (strVal.trim().equalsIgnoreCase("y")) {
                    orientation = PlotOrientation.VERTICAL;
                }
                if (orientation != null) {
                    if (plot instanceof CategoryPlot) {
                        final CategoryPlot catPlot = (CategoryPlot)plot;
                        catPlot.setOrientation(orientation);
                        if (orientation == PlotOrientation.HORIZONTAL) {
                            catPlot.setRowRenderingOrder(SortOrder.DESCENDING);
                            catPlot.setColumnRenderingOrder(SortOrder.DESCENDING);
                        }
                    }
                    else if (plot instanceof XYPlot) {
                        ((XYPlot)plot).setOrientation(orientation);
                    }
                }
            }
            value = ChartUtil.getPropValue(viewCtxt, graphType, "NODATA_MESSAGE");
            if (value != null) {
                plot.setNoDataMessage(I18N.getMsg(value, new Object[0]));
            }
            value = ChartUtil.getPropValue(viewCtxt, graphType, "NODATA_FONT");
            if (value != null) {
                plot.setNoDataMessageFont(ChartUtil.getFont(value));
            }
            value = ChartUtil.getPropValue(viewCtxt, graphType, "NODATA_PAINT");
            if (value != null) {
                plot.setNoDataMessagePaint((Paint)ChartUtil.getColorFromHex(value));
            }
            value = ChartUtil.getPropValue(viewCtxt, graphType, "CATEGORY_LABEL_POSITION");
            if (value != null && plot instanceof CategoryPlot) {
                final CategoryPlot catPlot2 = (CategoryPlot)plot;
                final CategoryAxis domainAxis = catPlot2.getDomainAxis();
                double ang = Double.parseDouble(value);
                if (ang > 180.0) {
                    ang %= 180.0;
                }
                final double angleToPi = 57.29577951308232;
                if (ang > 90.0 && ang <= 180.0) {
                    ang -= 90.0;
                    final double inPi = ang / angleToPi;
                    domainAxis.setCategoryLabelPositions(CategoryLabelPositions.createDownRotationLabelPositions(inPi));
                }
                else if (ang <= 90.0 && ang > 0.0) {
                    final double inPi = ang / angleToPi;
                    domainAxis.setCategoryLabelPositions(CategoryLabelPositions.createUpRotationLabelPositions(inPi));
                }
            }
            if (graphTypeHCode == ChartController.XYLINE || graphTypeHCode == ChartController.CATLINE) {
                value = ChartUtil.getPropValue(viewCtxt, graphType, "SHOW_SHAPES");
                if (value != null) {
                    if (graphTypeHCode == ChartController.XYLINE) {
                        final boolean show = value.equals("true");
                        final StandardXYItemRenderer renderer = (StandardXYItemRenderer)((XYPlot)plot).getRenderer();
                        renderer.setBaseShapesVisible(show);
                        renderer.setBaseShapesFilled(show);
                        renderer.setShapesFilled(show);
                    }
                    else if (graphTypeHCode == ChartController.CATLINE) {
                        final boolean show = value.equals("true");
                        final LineAndShapeRenderer renderer2 = (LineAndShapeRenderer)((CategoryPlot)plot).getRenderer();
                        renderer2.setBaseShapesVisible(show);
                        renderer2.setBaseShapesFilled(show);
                        renderer2.setShapesFilled(show);
                        renderer2.setShapesVisible(show);
                        renderer2.setLinesVisible(show);
                    }
                }
            }
            else if (graphTypeHCode == ChartController.SPIDERWEB) {
                value = ChartUtil.getPropValue(viewCtxt, graphType, "FILL_SPIDER_WEB");
                if (value != null) {
                    ((SpiderWebPlot)plot).setWebFilled(value.equals("true"));
                }
            }
            if (plot instanceof XYPlot) {
                final XYItemRenderer renderer3 = ((XYPlot)plot).getRenderer();
                if (graphTypeHCode == ChartController.OHLCCHART) {
                    final HighLowRenderer ohlcRenderer = (HighLowRenderer)renderer3;
                    value = ChartUtil.getPropValue(viewCtxt, graphType, "SHOW_OHLC_CLOSE");
                    if (value != null) {
                        ohlcRenderer.setDrawCloseTicks("true".equals(value));
                    }
                    value = ChartUtil.getPropValue(viewCtxt, graphType, "SHOW_OHLC_OPEN");
                    if (value != null) {
                        ohlcRenderer.setDrawOpenTicks("true".equals(value));
                    }
                }
                else if (graphTypeHCode == ChartController.CANDLESTICK) {
                    final CandlestickRenderer candleRenderer = (CandlestickRenderer)renderer3;
                    value = ChartUtil.getPropValue(viewCtxt, graphType, "SHOW_CANDLESTICK_VOLUME");
                    if (value != null) {
                        candleRenderer.setDrawVolume("true".equals(value));
                    }
                }
            }
            value = ChartUtil.getPropValue(viewCtxt, graphType, "DRAW_BARPIE_OUTLINE");
            if (value != null) {
                if (plot instanceof PiePlot) {
                    ((PiePlot)plot).setSectionOutlinesVisible("true".equals(value));
                }
                else if (plot instanceof CategoryPlot) {
                    final CategoryItemRenderer renderer4 = ((CategoryPlot)plot).getRenderer();
                    if (renderer4 instanceof BarRenderer) {
                        ((BarRenderer)renderer4).setDrawBarOutline("true".equals(value));
                    }
                }
                else if (plot instanceof XYPlot) {
                    final XYItemRenderer renderer3 = ((XYPlot)plot).getRenderer();
                    if (renderer3 instanceof XYBarRenderer) {
                        ((XYBarRenderer)renderer3).setDrawBarOutline("true".equals(value));
                    }
                }
            }
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
    }
    
    protected void setBaseProperties(final Map prop) {
        if (prop != null) {}
    }
    
    public String getBaseURL() {
        return "/chart/test.jsp";
    }
    
    public String getSQLString(final ViewContext viewCtx) throws Exception {
        final Object cvId = viewCtx.getModel().getViewConfiguration().getFirstValue("ChartViewConfig", 2);
        return SQLQueryAPI.getSQLString(cvId, (TemplateAPI.VariableHandler)this, viewCtx);
    }
    
    public String getVariableValue(final String variableName, final int variablePosition, final Object handlerContext) {
        return this.getVariableValue((ViewContext)handlerContext, variableName);
    }
    
    public String getVariableValue(final ViewContext viewCtx, final String variableName) {
        String reqValue = viewCtx.getRequest().getParameter(variableName);
        if (reqValue == null) {
            reqValue = "";
        }
        return reqValue;
    }
    
    public static final void closeAll(final Connection conn, final Statement stmt, final ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            }
            catch (final Exception ex) {}
        }
        if (stmt != null) {
            try {
                stmt.close();
            }
            catch (final Exception ex2) {}
        }
        if (conn != null) {
            try {
                conn.close();
            }
            catch (final Exception ex3) {}
        }
    }
    
    public static String getTableModelAsString(final TableModel tm) {
        final StringBuffer sbf = new StringBuffer();
        final int rc = tm.getRowCount();
        final int cc = tm.getColumnCount();
        sbf.append("\n Rowcount :" + rc + " columnCount " + cc + " ColumnNames \n");
        for (int i = 0; i < cc; ++i) {
            sbf.append(tm.getColumnName(i) + "\t");
        }
        for (int i = 0; i < rc; ++i) {
            sbf.append("\n");
            for (int j = 0; j < cc; ++j) {
                sbf.append(tm.getValueAt(i, j) + "\t");
            }
        }
        return sbf.toString();
    }
    
    class DefaultMCKTooltipFragGenerator implements ToolTipTagFragmentGenerator
    {
        MessageFormat mapFormat;
        
        DefaultMCKTooltipFragGenerator() {
            this.mapFormat = new MessageFormat(" onmouseover=\"ToolTip.show(event,this);\" value=\"{0}\"");
        }
        
        public String generateToolTipFragment(final String toolTipText) {
            final String mapText = this.mapFormat.format(new String[] { toolTipText }, new StringBuffer(), null).toString();
            return mapText;
        }
    }
}
