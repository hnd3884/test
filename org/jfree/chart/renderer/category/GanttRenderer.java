package org.jfree.chart.renderer.category;

import org.jfree.chart.labels.CategoryToolTipGenerator;
import org.jfree.chart.labels.CategoryItemLabelGenerator;
import java.awt.Stroke;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.ui.RectangleEdge;
import org.jfree.chart.entity.ChartEntity;
import org.jfree.chart.entity.CategoryItemEntity;
import java.awt.Shape;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.gantt.GanttCategoryDataset;
import org.jfree.data.category.CategoryDataset;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.plot.CategoryPlot;
import java.awt.geom.Rectangle2D;
import java.awt.Graphics2D;
import org.jfree.chart.event.RendererChangeEvent;
import java.awt.Color;
import java.awt.Paint;
import java.io.Serializable;

public class GanttRenderer extends IntervalBarRenderer implements Serializable
{
    private static final long serialVersionUID = -4010349116350119512L;
    private Paint completePaint;
    private Paint incompletePaint;
    private double startPercent;
    private double endPercent;
    
    public GanttRenderer() {
        this.setIncludeBaseInRange(false);
        this.completePaint = Color.green;
        this.incompletePaint = Color.red;
        this.startPercent = 0.35;
        this.endPercent = 0.65;
    }
    
    public Paint getCompletePaint() {
        return this.completePaint;
    }
    
    public void setCompletePaint(final Paint paint) {
        if (paint == null) {
            throw new IllegalArgumentException("Null 'paint' argument.");
        }
        this.completePaint = paint;
        this.notifyListeners(new RendererChangeEvent(this));
    }
    
    public Paint getIncompletePaint() {
        return this.incompletePaint;
    }
    
    public void setIncompletePaint(final Paint paint) {
        if (paint == null) {
            throw new IllegalArgumentException("Null 'paint' argument.");
        }
        this.incompletePaint = paint;
        this.notifyListeners(new RendererChangeEvent(this));
    }
    
    public double getStartPercent() {
        return this.startPercent;
    }
    
    public void setStartPercent(final double percent) {
        this.startPercent = percent;
        this.notifyListeners(new RendererChangeEvent(this));
    }
    
    public double getEndPercent() {
        return this.endPercent;
    }
    
    public void setEndPercent(final double percent) {
        this.endPercent = percent;
        this.notifyListeners(new RendererChangeEvent(this));
    }
    
    public void drawItem(final Graphics2D g2, final CategoryItemRendererState state, final Rectangle2D dataArea, final CategoryPlot plot, final CategoryAxis domainAxis, final ValueAxis rangeAxis, final CategoryDataset dataset, final int row, final int column, final int pass) {
        if (dataset instanceof GanttCategoryDataset) {
            final GanttCategoryDataset gcd = (GanttCategoryDataset)dataset;
            this.drawTasks(g2, state, dataArea, plot, domainAxis, rangeAxis, gcd, row, column);
        }
        else {
            super.drawItem(g2, state, dataArea, plot, domainAxis, rangeAxis, dataset, row, column, pass);
        }
    }
    
    protected void drawTasks(final Graphics2D g2, final CategoryItemRendererState state, final Rectangle2D dataArea, final CategoryPlot plot, final CategoryAxis domainAxis, final ValueAxis rangeAxis, final GanttCategoryDataset dataset, final int row, final int column) {
        final int count = dataset.getSubIntervalCount(row, column);
        if (count == 0) {
            this.drawTask(g2, state, dataArea, plot, domainAxis, rangeAxis, dataset, row, column);
        }
        for (int subinterval = 0; subinterval < count; ++subinterval) {
            final RectangleEdge rangeAxisLocation = plot.getRangeAxisEdge();
            final Number value0 = dataset.getStartValue(row, column, subinterval);
            if (value0 == null) {
                return;
            }
            double translatedValue0 = rangeAxis.valueToJava2D(value0.doubleValue(), dataArea, rangeAxisLocation);
            final Number value2 = dataset.getEndValue(row, column, subinterval);
            if (value2 == null) {
                return;
            }
            double translatedValue2 = rangeAxis.valueToJava2D(value2.doubleValue(), dataArea, rangeAxisLocation);
            if (translatedValue2 < translatedValue0) {
                final double temp = translatedValue2;
                translatedValue2 = translatedValue0;
                translatedValue0 = temp;
            }
            final double rectStart = this.calculateBarW0(plot, plot.getOrientation(), dataArea, domainAxis, state, row, column);
            final double rectLength = Math.abs(translatedValue2 - translatedValue0);
            final double rectBreadth = state.getBarWidth();
            Rectangle2D bar = null;
            if (plot.getOrientation() == PlotOrientation.HORIZONTAL) {
                bar = new Rectangle2D.Double(translatedValue0, rectStart, rectLength, rectBreadth);
            }
            else if (plot.getOrientation() == PlotOrientation.VERTICAL) {
                bar = new Rectangle2D.Double(rectStart, translatedValue0, rectBreadth, rectLength);
            }
            Rectangle2D completeBar = null;
            Rectangle2D incompleteBar = null;
            final Number percent = dataset.getPercentComplete(row, column, subinterval);
            final double start = this.getStartPercent();
            final double end = this.getEndPercent();
            if (percent != null) {
                final double p = percent.doubleValue();
                if (plot.getOrientation() == PlotOrientation.HORIZONTAL) {
                    completeBar = new Rectangle2D.Double(translatedValue0, rectStart + start * rectBreadth, rectLength * p, rectBreadth * (end - start));
                    incompleteBar = new Rectangle2D.Double(translatedValue0 + rectLength * p, rectStart + start * rectBreadth, rectLength * (1.0 - p), rectBreadth * (end - start));
                }
                else if (plot.getOrientation() == PlotOrientation.VERTICAL) {
                    completeBar = new Rectangle2D.Double(rectStart + start * rectBreadth, translatedValue0 + rectLength * (1.0 - p), rectBreadth * (end - start), rectLength * p);
                    incompleteBar = new Rectangle2D.Double(rectStart + start * rectBreadth, translatedValue0, rectBreadth * (end - start), rectLength * (1.0 - p));
                }
            }
            final Paint seriesPaint = this.getItemPaint(row, column);
            g2.setPaint(seriesPaint);
            g2.fill(bar);
            if (completeBar != null) {
                g2.setPaint(this.getCompletePaint());
                g2.fill(completeBar);
            }
            if (incompleteBar != null) {
                g2.setPaint(this.getIncompletePaint());
                g2.fill(incompleteBar);
            }
            if (this.isDrawBarOutline() && state.getBarWidth() > 3.0) {
                g2.setStroke(this.getItemStroke(row, column));
                g2.setPaint(this.getItemOutlinePaint(row, column));
                g2.draw(bar);
            }
            if (state.getInfo() != null) {
                final EntityCollection entities = state.getEntityCollection();
                if (entities != null) {
                    String tip = null;
                    if (this.getToolTipGenerator(row, column) != null) {
                        tip = this.getToolTipGenerator(row, column).generateToolTip(dataset, row, column);
                    }
                    String url = null;
                    if (this.getItemURLGenerator(row, column) != null) {
                        url = this.getItemURLGenerator(row, column).generateURL(dataset, row, column);
                    }
                    final CategoryItemEntity entity = new CategoryItemEntity(bar, tip, url, dataset, row, dataset.getColumnKey(column), column);
                    entities.add(entity);
                }
            }
        }
    }
    
    protected void drawTask(final Graphics2D g2, final CategoryItemRendererState state, final Rectangle2D dataArea, final CategoryPlot plot, final CategoryAxis domainAxis, final ValueAxis rangeAxis, final GanttCategoryDataset dataset, final int row, final int column) {
        final PlotOrientation orientation = plot.getOrientation();
        final RectangleEdge rangeAxisLocation = plot.getRangeAxisEdge();
        Number value0 = dataset.getEndValue(row, column);
        if (value0 == null) {
            return;
        }
        double java2dValue0 = rangeAxis.valueToJava2D(value0.doubleValue(), dataArea, rangeAxisLocation);
        Number value2 = dataset.getStartValue(row, column);
        if (value2 == null) {
            return;
        }
        double java2dValue2 = rangeAxis.valueToJava2D(value2.doubleValue(), dataArea, rangeAxisLocation);
        if (java2dValue2 < java2dValue0) {
            final double temp = java2dValue2;
            java2dValue2 = java2dValue0;
            java2dValue0 = temp;
            final Number tempNum = value2;
            value2 = value0;
            value0 = tempNum;
        }
        final double rectStart = this.calculateBarW0(plot, orientation, dataArea, domainAxis, state, row, column);
        final double rectBreadth = state.getBarWidth();
        final double rectLength = Math.abs(java2dValue2 - java2dValue0);
        Rectangle2D bar = null;
        if (orientation == PlotOrientation.HORIZONTAL) {
            bar = new Rectangle2D.Double(java2dValue0, rectStart, rectLength, rectBreadth);
        }
        else if (orientation == PlotOrientation.VERTICAL) {
            bar = new Rectangle2D.Double(rectStart, java2dValue2, rectBreadth, rectLength);
        }
        Rectangle2D completeBar = null;
        Rectangle2D incompleteBar = null;
        final Number percent = dataset.getPercentComplete(row, column);
        final double start = this.getStartPercent();
        final double end = this.getEndPercent();
        if (percent != null) {
            final double p = percent.doubleValue();
            if (plot.getOrientation() == PlotOrientation.HORIZONTAL) {
                completeBar = new Rectangle2D.Double(java2dValue0, rectStart + start * rectBreadth, rectLength * p, rectBreadth * (end - start));
                incompleteBar = new Rectangle2D.Double(java2dValue0 + rectLength * p, rectStart + start * rectBreadth, rectLength * (1.0 - p), rectBreadth * (end - start));
            }
            else if (plot.getOrientation() == PlotOrientation.VERTICAL) {
                completeBar = new Rectangle2D.Double(rectStart + start * rectBreadth, java2dValue2 + rectLength * (1.0 - p), rectBreadth * (end - start), rectLength * p);
                incompleteBar = new Rectangle2D.Double(rectStart + start * rectBreadth, java2dValue2, rectBreadth * (end - start), rectLength * (1.0 - p));
            }
        }
        final Paint seriesPaint = this.getItemPaint(row, column);
        g2.setPaint(seriesPaint);
        g2.fill(bar);
        if (completeBar != null) {
            g2.setPaint(this.getCompletePaint());
            g2.fill(completeBar);
        }
        if (incompleteBar != null) {
            g2.setPaint(this.getIncompletePaint());
            g2.fill(incompleteBar);
        }
        if (this.isDrawBarOutline() && state.getBarWidth() > 3.0) {
            final Stroke stroke = this.getItemOutlineStroke(row, column);
            final Paint paint = this.getItemOutlinePaint(row, column);
            if (stroke != null && paint != null) {
                g2.setStroke(stroke);
                g2.setPaint(paint);
                g2.draw(bar);
            }
        }
        final CategoryItemLabelGenerator generator = this.getItemLabelGenerator(row, column);
        if (generator != null && this.isItemLabelVisible(row, column)) {
            this.drawItemLabel(g2, dataset, row, column, plot, generator, bar, false);
        }
        if (state.getInfo() != null) {
            final EntityCollection entities = state.getEntityCollection();
            if (entities != null) {
                String tip = null;
                final CategoryToolTipGenerator tipster = this.getToolTipGenerator(row, column);
                if (tipster != null) {
                    tip = tipster.generateToolTip(dataset, row, column);
                }
                String url = null;
                if (this.getItemURLGenerator(row, column) != null) {
                    url = this.getItemURLGenerator(row, column).generateURL(dataset, row, column);
                }
                final CategoryItemEntity entity = new CategoryItemEntity(bar, tip, url, dataset, row, dataset.getColumnKey(column), column);
                entities.add(entity);
            }
        }
    }
}
