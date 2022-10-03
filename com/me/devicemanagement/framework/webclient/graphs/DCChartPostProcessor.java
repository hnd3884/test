package com.me.devicemanagement.framework.webclient.graphs;

import org.jfree.data.category.CategoryDataset;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import java.awt.Font;
import java.awt.Stroke;
import java.awt.BasicStroke;
import org.jfree.chart.plot.PiePlot3D;
import org.jfree.chart.renderer.category.BarRenderer;
import java.util.Iterator;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.ui.TextAnchor;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.CategoryItemLabelGenerator;
import org.jfree.chart.renderer.category.BarRenderer3D;
import java.util.LinkedList;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import java.awt.GradientPaint;
import java.awt.Paint;
import java.awt.Color;
import com.adventnet.i18n.I18N;
import org.jfree.chart.plot.Plot;
import java.util.logging.Level;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.CategoryPlot;
import java.util.Map;
import org.jfree.chart.JFreeChart;
import java.util.logging.Logger;

public class DCChartPostProcessor
{
    Logger logger;
    
    public DCChartPostProcessor() {
        this.logger = Logger.getLogger(DCChartPostProcessor.class.getName());
    }
    
    public synchronized void processChart(final JFreeChart chart, final Map params) {
        try {
            final JFreeChart jfc = chart;
            final Plot plot = jfc.getPlot();
            this.setCommonPlotProps(plot);
            final String chartType = params.get("chartType");
            if (chartType != null && chartType.equalsIgnoreCase("Pie")) {
                this.setPiePlotProps(jfc, params);
            }
            else if (chartType != null && chartType.equalsIgnoreCase("Bar")) {
                this.setBarPlotProps(jfc, params);
            }
            else if (plot instanceof CategoryPlot) {
                this.setCategoryPlotProps(jfc, params);
            }
            else if (plot instanceof PiePlot) {
                this.setPie3DPlotProps(jfc, params);
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.WARNING, "Exception in processChart of DCChartPostProcessor ::", e);
        }
    }
    
    private void setCommonPlotProps(final Plot plot) {
        try {
            plot.setNoDataMessage(I18N.getMsg("dc.graph.data_not_available", new Object[0]));
            plot.setNoDataMessageFont(this.getFont());
            plot.setNoDataMessagePaint((Paint)Color.GRAY);
            plot.setBackgroundAlpha(0.5f);
            plot.setForegroundAlpha(0.9f);
        }
        catch (final Exception e) {
            this.logger.log(Level.WARNING, "Exception while setting common plot properties", e);
        }
    }
    
    private void setCategoryPlotProps(final JFreeChart jfc, final Map params) {
        try {
            final CategoryPlot cPlot = jfc.getCategoryPlot();
            cPlot.setRangeGridlinesVisible(true);
            cPlot.setRangeGridlinePaint((Paint)new Color(10132122));
            cPlot.setBackgroundPaint((Paint)new GradientPaint(200.0f, 50.0f, Color.WHITE, 5.0f, 1.0f, new Color(15066597)));
            final CategoryAxis domainAxis = cPlot.getDomainAxis();
            domainAxis.setCategoryMargin(0.08);
            domainAxis.setLowerMargin(0.1);
            domainAxis.setUpperMargin(0.1);
            domainAxis.setTickLabelsVisible(true);
            domainAxis.setMaximumCategoryLabelWidthRatio(1.0f);
            domainAxis.setCategoryLabelPositions(CategoryLabelPositions.createUpRotationLabelPositions(0.7853981633974483));
            domainAxis.setTickLabelFont(this.getFont());
            domainAxis.setCategoryMargin(0.1);
            domainAxis.setVisible(true);
            domainAxis.setLabelFont(this.getFont());
            final NumberAxis rangeAxis = (NumberAxis)cPlot.getRangeAxis();
            rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
            final double rmax = rangeAxis.getUpperBound();
            rangeAxis.setAutoRange(true);
            rangeAxis.setUpperBound(rmax);
            rangeAxis.setLowerBound(0.0);
            rangeAxis.setLabelFont(this.getFont());
            final LinkedList<GraphEntry> graphEntries = params.get("graphData");
            if (graphEntries != null) {
                final BarRenderer3D ciRenderer = (BarRenderer3D)cPlot.getRenderer();
                GradientPaint gp = null;
                Color color = null;
                int count = 0;
                for (final GraphEntry entry : graphEntries) {
                    if (!entry.getName().equalsIgnoreCase("TOTAL")) {
                        final int intValue = Integer.parseInt(entry.getColor(), 16);
                        color = new Color(intValue);
                        gp = new GradientPaint(0.0f, 0.0f, color, 0.0f, 0.0f, color);
                        ciRenderer.setSeriesOutlinePaint(count, (Paint)color);
                        ciRenderer.setSeriesPaint(count++, (Paint)gp);
                    }
                }
                ciRenderer.setBasePaint((Paint)new Color(6790095));
                ciRenderer.setMaximumBarWidth(0.30000001192092896);
                ciRenderer.setBaseItemLabelGenerator((CategoryItemLabelGenerator)new CustomLabelGeneratorforBar());
                ciRenderer.setItemLabelsVisible(true);
                ciRenderer.setPositiveItemLabelPositionFallback(new ItemLabelPosition(ItemLabelAnchor.OUTSIDE12, TextAnchor.BASELINE_CENTER));
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.WARNING, "Exception while setting category plot properties :: ", e);
        }
    }
    
    private void setBarPlotProps(final JFreeChart jfc, final Map params) {
        try {
            final CategoryPlot cPlot = jfc.getCategoryPlot();
            cPlot.setRangeGridlinesVisible(true);
            cPlot.setRangeGridlinePaint((Paint)new Color(15198183));
            cPlot.setBackgroundPaint((Paint)Color.white);
            cPlot.setOutlinePaint((Paint)null);
            cPlot.setDomainGridlinePaint((Paint)Color.white);
            final CategoryAxis domainAxis = cPlot.getDomainAxis();
            domainAxis.setLowerMargin(0.1);
            domainAxis.setUpperMargin(0.1);
            domainAxis.setTickLabelsVisible(true);
            domainAxis.setCategoryLabelPositions(CategoryLabelPositions.createUpRotationLabelPositions(0.7853981633974483));
            domainAxis.setCategoryMargin(0.1);
            domainAxis.setVisible(true);
            domainAxis.setLabelFont(this.getFont());
            domainAxis.setLabelPaint((Paint)Color.gray);
            domainAxis.setAxisLineVisible(false);
            final NumberAxis rangeAxis = (NumberAxis)cPlot.getRangeAxis();
            rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
            rangeAxis.setAxisLinePaint((Paint)Color.WHITE);
            final double rmax = rangeAxis.getUpperBound();
            rangeAxis.setAutoRange(true);
            rangeAxis.setUpperBound(rmax);
            rangeAxis.setLowerBound(0.0);
            rangeAxis.setLabelFont(this.getFont());
            rangeAxis.setLabel((String)null);
            rangeAxis.setLabelPaint((Paint)Color.gray);
            rangeAxis.setAxisLineVisible(false);
            rangeAxis.setTickLabelPaint((Paint)Color.gray);
            final LinkedList<GraphEntry> graphEntries = params.get("graphData");
            if (graphEntries != null) {
                final BarRenderer ciRenderer = (BarRenderer)cPlot.getRenderer();
                GradientPaint gp = null;
                Color color = null;
                int count = 0;
                for (final GraphEntry entry : graphEntries) {
                    if (!entry.getName().equalsIgnoreCase("TOTAL")) {
                        final int intValue = Integer.parseInt(entry.getColor(), 16);
                        color = new Color(intValue);
                        gp = new GradientPaint(0.0f, 0.0f, color, 0.0f, 0.0f, color);
                        ciRenderer.setSeriesOutlinePaint(count, (Paint)color);
                        ciRenderer.setSeriesPaint(count++, (Paint)gp);
                    }
                }
                ciRenderer.setBasePaint((Paint)new Color(6790095));
                ciRenderer.setMaximumBarWidth(0.12999999523162842);
                ciRenderer.setItemLabelsVisible(true);
                ciRenderer.setBaseOutlinePaint((Paint)Color.WHITE);
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.WARNING, "Exception while setting category plot properties :: ", e);
        }
    }
    
    private void setPie3DPlotProps(final JFreeChart jfc, final Map params) {
        try {
            final JFreeChart jfree = jfc;
            jfree.setAntiAlias(true);
            jfree.setBorderPaint((Paint)Color.WHITE);
            jfree.setBackgroundPaint((Paint)Color.WHITE);
            final PiePlot pPlot = (PiePlot)jfc.getPlot();
            pPlot.setSectionOutlinePaint((Paint)null);
            pPlot.setOutlinePaint((Paint)Color.WHITE);
            ((PiePlot3D)pPlot).setDepthFactor(0.12);
            final LinkedList<GraphEntry> graphEntries = params.get("graphData");
            if (graphEntries != null) {
                final Iterator itr = graphEntries.iterator();
                Color color = null;
                int i = 0;
                while (itr.hasNext()) {
                    final GraphEntry entry = itr.next();
                    if (!entry.getName().equalsIgnoreCase("TOTAL")) {
                        final int intValue = Integer.parseInt(entry.getColor(), 16);
                        color = new Color(intValue);
                        final GradientPaint gp0 = new GradientPaint(0.0f, 0.0f, color, -1.0f, 250.0f, new Color(16119285));
                        final GradientPaint gp2 = new GradientPaint(0.0f, 0.0f, new Color(16119285), -1.0f, 50.0f, color);
                        ((PiePlot3D)pPlot).setSectionPaint(i, (Paint)gp2);
                        ((PiePlot3D)pPlot).setSectionOutlinePaint(i, (Paint)gp0);
                        ++i;
                    }
                }
                pPlot.setStartAngle(271.0);
                pPlot.setInteriorGap(0.2);
                pPlot.setLabelGap(0.001);
                pPlot.setLabelOutlinePaint((Paint)Color.WHITE);
                pPlot.setLabelShadowPaint((Paint)Color.WHITE);
                pPlot.setLabelBackgroundPaint((Paint)Color.WHITE);
                pPlot.setLabelLinkPaint((Paint)new Color(10263708));
                pPlot.setMaximumLabelWidth(0.2);
                pPlot.setLabelFont(this.getFont());
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.WARNING, "Exception while setting pie plot properties :: ", e);
        }
    }
    
    private void setPiePlotProps(final JFreeChart jfc, final Map params) {
        try {
            final JFreeChart jfree = jfc;
            jfree.setAntiAlias(true);
            jfree.setBorderPaint((Paint)Color.WHITE);
            jfree.setBackgroundPaint((Paint)Color.WHITE);
            final PiePlot pPlot = (PiePlot)jfc.getPlot();
            pPlot.setSectionOutlinePaint((Paint)Color.WHITE);
            pPlot.setOutlinePaint((Paint)Color.WHITE);
            final Stroke s = new BasicStroke(0.5f);
            pPlot.setSectionOutlineStroke(s);
            final LinkedList<GraphEntry> graphEntries = params.get("graphData");
            if (graphEntries != null) {
                final Iterator itr = graphEntries.iterator();
                Color color = null;
                int i = 0;
                while (itr.hasNext()) {
                    final GraphEntry entry = itr.next();
                    if (!entry.getName().equalsIgnoreCase("TOTAL")) {
                        final int intValue = Integer.parseInt(entry.getColor(), 16);
                        color = new Color(intValue);
                        pPlot.setSectionPaint(i, (Paint)color);
                        ++i;
                    }
                }
                pPlot.setInteriorGap(0.2);
                pPlot.setLabelGap(0.001);
                pPlot.setShadowPaint((Paint)null);
                pPlot.setLabelOutlinePaint((Paint)Color.WHITE);
                pPlot.setLabelShadowPaint((Paint)Color.WHITE);
                pPlot.setLabelBackgroundPaint((Paint)Color.WHITE);
                pPlot.setLabelLinkPaint((Paint)Color.gray);
                pPlot.setLabelPaint((Paint)Color.gray);
                pPlot.setMaximumLabelWidth(0.2);
                pPlot.setLabelFont(this.getFont());
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.WARNING, "Exception while setting pie plot properties :: ", e);
        }
    }
    
    private Font getFont() {
        return new Font("Lato", 0, 12);
    }
    
    class CustomLabelGeneratorforBar extends StandardCategoryItemLabelGenerator
    {
        public String generateLabel(final CategoryDataset dataset, final int series, final int category) {
            if (dataset.getValue(series, category) != null && !dataset.getValue(series, category).toString().equalsIgnoreCase("0")) {
                return "(" + dataset.getValue(series, category) + ")";
            }
            return "";
        }
    }
}
