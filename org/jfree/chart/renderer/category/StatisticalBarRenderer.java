package org.jfree.chart.renderer.category;

import java.io.ObjectInputStream;
import java.io.IOException;
import org.jfree.io.SerialUtilities;
import java.io.ObjectOutputStream;
import org.jfree.util.PaintUtilities;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.labels.CategoryItemLabelGenerator;
import org.jfree.ui.RectangleEdge;
import java.awt.geom.Line2D;
import java.awt.Shape;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.statistics.StatisticalCategoryDataset;
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
import org.jfree.util.PublicCloneable;

public class StatisticalBarRenderer extends BarRenderer implements CategoryItemRenderer, Cloneable, PublicCloneable, Serializable
{
    private static final long serialVersionUID = -4986038395414039117L;
    private transient Paint errorIndicatorPaint;
    
    public StatisticalBarRenderer() {
        this.errorIndicatorPaint = Color.gray;
    }
    
    public Paint getErrorIndicatorPaint() {
        return this.errorIndicatorPaint;
    }
    
    public void setErrorIndicatorPaint(final Paint paint) {
        this.errorIndicatorPaint = paint;
        this.notifyListeners(new RendererChangeEvent(this));
    }
    
    public void drawItem(final Graphics2D g2, final CategoryItemRendererState state, final Rectangle2D dataArea, final CategoryPlot plot, final CategoryAxis domainAxis, final ValueAxis rangeAxis, final CategoryDataset data, final int row, final int column, final int pass) {
        if (!(data instanceof StatisticalCategoryDataset)) {
            throw new IllegalArgumentException("Requires StatisticalCategoryDataset.");
        }
        final StatisticalCategoryDataset statData = (StatisticalCategoryDataset)data;
        final PlotOrientation orientation = plot.getOrientation();
        if (orientation == PlotOrientation.HORIZONTAL) {
            this.drawHorizontalItem(g2, state, dataArea, plot, domainAxis, rangeAxis, statData, row, column);
        }
        else if (orientation == PlotOrientation.VERTICAL) {
            this.drawVerticalItem(g2, state, dataArea, plot, domainAxis, rangeAxis, statData, row, column);
        }
    }
    
    protected void drawHorizontalItem(final Graphics2D g2, final CategoryItemRendererState state, final Rectangle2D dataArea, final CategoryPlot plot, final CategoryAxis domainAxis, final ValueAxis rangeAxis, final StatisticalCategoryDataset dataset, final int row, final int column) {
        final RectangleEdge xAxisLocation = plot.getDomainAxisEdge();
        double rectY = domainAxis.getCategoryStart(column, this.getColumnCount(), dataArea, xAxisLocation);
        final int seriesCount = this.getRowCount();
        final int categoryCount = this.getColumnCount();
        if (seriesCount > 1) {
            final double seriesGap = dataArea.getHeight() * this.getItemMargin() / (categoryCount * (seriesCount - 1));
            rectY += row * (state.getBarWidth() + seriesGap);
        }
        else {
            rectY += row * state.getBarWidth();
        }
        final Number meanValue = dataset.getMeanValue(row, column);
        double value = meanValue.doubleValue();
        double base = 0.0;
        final double lclip = this.getLowerClip();
        final double uclip = this.getUpperClip();
        if (uclip <= 0.0) {
            if (value >= uclip) {
                return;
            }
            base = uclip;
            if (value <= lclip) {
                value = lclip;
            }
        }
        else if (lclip <= 0.0) {
            if (value >= uclip) {
                value = uclip;
            }
            else if (value <= lclip) {
                value = lclip;
            }
        }
        else {
            if (value <= lclip) {
                return;
            }
            base = this.getLowerClip();
            if (value >= uclip) {
                value = uclip;
            }
        }
        final RectangleEdge yAxisLocation = plot.getRangeAxisEdge();
        final double transY1 = rangeAxis.valueToJava2D(base, dataArea, yAxisLocation);
        final double transY2 = rangeAxis.valueToJava2D(value, dataArea, yAxisLocation);
        final double rectX = Math.min(transY2, transY1);
        final double rectHeight = state.getBarWidth();
        final double rectWidth = Math.abs(transY2 - transY1);
        final Rectangle2D bar = new Rectangle2D.Double(rectX, rectY, rectWidth, rectHeight);
        final Paint seriesPaint = this.getItemPaint(row, column);
        g2.setPaint(seriesPaint);
        g2.fill(bar);
        if (state.getBarWidth() > 3.0) {
            g2.setStroke(this.getItemStroke(row, column));
            g2.setPaint(this.getItemOutlinePaint(row, column));
            g2.draw(bar);
        }
        final double valueDelta = dataset.getStdDevValue(row, column).doubleValue();
        final double highVal = rangeAxis.valueToJava2D(meanValue.doubleValue() + valueDelta, dataArea, yAxisLocation);
        final double lowVal = rangeAxis.valueToJava2D(meanValue.doubleValue() - valueDelta, dataArea, yAxisLocation);
        if (this.errorIndicatorPaint != null) {
            g2.setPaint(this.errorIndicatorPaint);
        }
        else {
            g2.setPaint(this.getItemOutlinePaint(row, column));
        }
        Line2D line = null;
        line = new Line2D.Double(lowVal, rectY + rectHeight / 2.0, highVal, rectY + rectHeight / 2.0);
        g2.draw(line);
        line = new Line2D.Double(highVal, rectY + rectHeight * 0.25, highVal, rectY + rectHeight * 0.75);
        g2.draw(line);
        line = new Line2D.Double(lowVal, rectY + rectHeight * 0.25, lowVal, rectY + rectHeight * 0.75);
        g2.draw(line);
        final CategoryItemLabelGenerator generator = this.getItemLabelGenerator(row, column);
        if (generator != null && this.isItemLabelVisible(row, column)) {
            this.drawItemLabel(g2, dataset, row, column, plot, generator, bar, value < 0.0);
        }
        final EntityCollection entities = state.getEntityCollection();
        if (entities != null) {
            this.addItemEntity(entities, dataset, row, column, bar);
        }
    }
    
    protected void drawVerticalItem(final Graphics2D g2, final CategoryItemRendererState state, final Rectangle2D dataArea, final CategoryPlot plot, final CategoryAxis domainAxis, final ValueAxis rangeAxis, final StatisticalCategoryDataset dataset, final int row, final int column) {
        final RectangleEdge xAxisLocation = plot.getDomainAxisEdge();
        double rectX = domainAxis.getCategoryStart(column, this.getColumnCount(), dataArea, xAxisLocation);
        final int seriesCount = this.getRowCount();
        final int categoryCount = this.getColumnCount();
        if (seriesCount > 1) {
            final double seriesGap = dataArea.getWidth() * this.getItemMargin() / (categoryCount * (seriesCount - 1));
            rectX += row * (state.getBarWidth() + seriesGap);
        }
        else {
            rectX += row * state.getBarWidth();
        }
        final Number meanValue = dataset.getMeanValue(row, column);
        double value = meanValue.doubleValue();
        double base = 0.0;
        final double lclip = this.getLowerClip();
        final double uclip = this.getUpperClip();
        if (uclip <= 0.0) {
            if (value >= uclip) {
                return;
            }
            base = uclip;
            if (value <= lclip) {
                value = lclip;
            }
        }
        else if (lclip <= 0.0) {
            if (value >= uclip) {
                value = uclip;
            }
            else if (value <= lclip) {
                value = lclip;
            }
        }
        else {
            if (value <= lclip) {
                return;
            }
            base = this.getLowerClip();
            if (value >= uclip) {
                value = uclip;
            }
        }
        final RectangleEdge yAxisLocation = plot.getRangeAxisEdge();
        final double transY1 = rangeAxis.valueToJava2D(base, dataArea, yAxisLocation);
        final double transY2 = rangeAxis.valueToJava2D(value, dataArea, yAxisLocation);
        final double rectY = Math.min(transY2, transY1);
        final double rectWidth = state.getBarWidth();
        final double rectHeight = Math.abs(transY2 - transY1);
        final Rectangle2D bar = new Rectangle2D.Double(rectX, rectY, rectWidth, rectHeight);
        final Paint seriesPaint = this.getItemPaint(row, column);
        g2.setPaint(seriesPaint);
        g2.fill(bar);
        if (state.getBarWidth() > 3.0) {
            g2.setStroke(this.getItemStroke(row, column));
            g2.setPaint(this.getItemOutlinePaint(row, column));
            g2.draw(bar);
        }
        final double valueDelta = dataset.getStdDevValue(row, column).doubleValue();
        final double highVal = rangeAxis.valueToJava2D(meanValue.doubleValue() + valueDelta, dataArea, yAxisLocation);
        final double lowVal = rangeAxis.valueToJava2D(meanValue.doubleValue() - valueDelta, dataArea, yAxisLocation);
        if (this.errorIndicatorPaint != null) {
            g2.setPaint(this.errorIndicatorPaint);
        }
        else {
            g2.setPaint(this.getItemOutlinePaint(row, column));
        }
        Line2D line = null;
        line = new Line2D.Double(rectX + rectWidth / 2.0, lowVal, rectX + rectWidth / 2.0, highVal);
        g2.draw(line);
        line = new Line2D.Double(rectX + rectWidth / 2.0 - 5.0, highVal, rectX + rectWidth / 2.0 + 5.0, highVal);
        g2.draw(line);
        line = new Line2D.Double(rectX + rectWidth / 2.0 - 5.0, lowVal, rectX + rectWidth / 2.0 + 5.0, lowVal);
        g2.draw(line);
        final CategoryItemLabelGenerator generator = this.getItemLabelGenerator(row, column);
        if (generator != null && this.isItemLabelVisible(row, column)) {
            this.drawItemLabel(g2, dataset, row, column, plot, generator, bar, value < 0.0);
        }
        final EntityCollection entities = state.getEntityCollection();
        if (entities != null) {
            this.addItemEntity(entities, dataset, row, column, bar);
        }
    }
    
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof StatisticalBarRenderer)) {
            return false;
        }
        if (!super.equals(obj)) {
            return false;
        }
        final StatisticalBarRenderer that = (StatisticalBarRenderer)obj;
        return PaintUtilities.equal(this.errorIndicatorPaint, that.errorIndicatorPaint);
    }
    
    private void writeObject(final ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        SerialUtilities.writePaint(this.errorIndicatorPaint, stream);
    }
    
    private void readObject(final ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        this.errorIndicatorPaint = SerialUtilities.readPaint(stream);
    }
}
