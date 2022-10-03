package org.jfree.chart.renderer.category;

import java.io.ObjectInputStream;
import java.io.IOException;
import org.jfree.io.SerialUtilities;
import java.io.ObjectOutputStream;
import org.jfree.util.PaintUtilities;
import org.jfree.chart.labels.CategoryToolTipGenerator;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.ui.RectangleEdge;
import org.jfree.chart.entity.ChartEntity;
import org.jfree.chart.entity.CategoryItemEntity;
import java.awt.Shape;
import java.awt.geom.Line2D;
import org.jfree.util.ShapeUtilities;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.statistics.StatisticalCategoryDataset;
import org.jfree.data.category.CategoryDataset;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.plot.CategoryPlot;
import java.awt.geom.Rectangle2D;
import java.awt.Graphics2D;
import org.jfree.chart.event.RendererChangeEvent;
import java.awt.Paint;
import java.io.Serializable;
import org.jfree.util.PublicCloneable;

public class StatisticalLineAndShapeRenderer extends LineAndShapeRenderer implements Cloneable, PublicCloneable, Serializable
{
    private static final long serialVersionUID = -3557517173697777579L;
    private transient Paint errorIndicatorPaint;
    
    public StatisticalLineAndShapeRenderer() {
        this(true, true);
    }
    
    public StatisticalLineAndShapeRenderer(final boolean linesVisible, final boolean shapesVisible) {
        super(true, true);
        this.errorIndicatorPaint = null;
    }
    
    public Paint getErrorIndicatorPaint() {
        return this.errorIndicatorPaint;
    }
    
    public void setErrorIndicatorPaint(final Paint paint) {
        this.errorIndicatorPaint = paint;
        this.notifyListeners(new RendererChangeEvent(this));
    }
    
    public void drawItem(final Graphics2D g2, final CategoryItemRendererState state, final Rectangle2D dataArea, final CategoryPlot plot, final CategoryAxis domainAxis, final ValueAxis rangeAxis, final CategoryDataset dataset, final int row, final int column, final int pass) {
        final Number v = dataset.getValue(row, column);
        if (v == null) {
            return;
        }
        final StatisticalCategoryDataset statData = (StatisticalCategoryDataset)dataset;
        final Number meanValue = statData.getMeanValue(row, column);
        final PlotOrientation orientation = plot.getOrientation();
        final double x1 = domainAxis.getCategoryMiddle(column, this.getColumnCount(), dataArea, plot.getDomainAxisEdge());
        final double y1 = rangeAxis.valueToJava2D(meanValue.doubleValue(), dataArea, plot.getRangeAxisEdge());
        Shape shape = this.getItemShape(row, column);
        if (orientation == PlotOrientation.HORIZONTAL) {
            shape = ShapeUtilities.createTranslatedShape(shape, y1, x1);
        }
        else if (orientation == PlotOrientation.VERTICAL) {
            shape = ShapeUtilities.createTranslatedShape(shape, x1, y1);
        }
        if (this.getItemShapeVisible(row, column)) {
            if (this.getItemShapeFilled(row, column)) {
                g2.setPaint(this.getItemPaint(row, column));
                g2.fill(shape);
            }
            else {
                if (this.getUseOutlinePaint()) {
                    g2.setPaint(this.getItemOutlinePaint(row, column));
                }
                else {
                    g2.setPaint(this.getItemPaint(row, column));
                }
                g2.setStroke(this.getItemOutlineStroke(row, column));
                g2.draw(shape);
            }
        }
        if (this.getItemLineVisible(row, column) && column != 0) {
            final Number previousValue = statData.getValue(row, column - 1);
            if (previousValue != null) {
                final double previous = previousValue.doubleValue();
                final double x2 = domainAxis.getCategoryMiddle(column - 1, this.getColumnCount(), dataArea, plot.getDomainAxisEdge());
                final double y2 = rangeAxis.valueToJava2D(previous, dataArea, plot.getRangeAxisEdge());
                Line2D line = null;
                if (orientation == PlotOrientation.HORIZONTAL) {
                    line = new Line2D.Double(y2, x2, y1, x1);
                }
                else if (orientation == PlotOrientation.VERTICAL) {
                    line = new Line2D.Double(x2, y2, x1, y1);
                }
                g2.setPaint(this.getItemPaint(row, column));
                g2.setStroke(this.getItemStroke(row, column));
                g2.draw(line);
            }
        }
        final RectangleEdge yAxisLocation = plot.getRangeAxisEdge();
        final RectangleEdge xAxisLocation = plot.getDomainAxisEdge();
        double rectX = domainAxis.getCategoryStart(column, this.getColumnCount(), dataArea, xAxisLocation);
        rectX += row * state.getBarWidth();
        g2.setPaint(this.getItemPaint(row, column));
        final double valueDelta = statData.getStdDevValue(row, column).doubleValue();
        double highVal;
        if (meanValue.doubleValue() + valueDelta > rangeAxis.getRange().getUpperBound()) {
            highVal = rangeAxis.valueToJava2D(rangeAxis.getRange().getUpperBound(), dataArea, yAxisLocation);
        }
        else {
            highVal = rangeAxis.valueToJava2D(meanValue.doubleValue() + valueDelta, dataArea, yAxisLocation);
        }
        double lowVal;
        if (meanValue.doubleValue() + valueDelta < rangeAxis.getRange().getLowerBound()) {
            lowVal = rangeAxis.valueToJava2D(rangeAxis.getRange().getLowerBound(), dataArea, yAxisLocation);
        }
        else {
            lowVal = rangeAxis.valueToJava2D(meanValue.doubleValue() - valueDelta, dataArea, yAxisLocation);
        }
        if (this.errorIndicatorPaint != null) {
            g2.setPaint(this.errorIndicatorPaint);
        }
        else {
            g2.setPaint(this.getItemPaint(row, column));
        }
        final Line2D line2 = new Line2D.Double();
        if (orientation == PlotOrientation.HORIZONTAL) {
            line2.setLine(lowVal, x1, highVal, x1);
            g2.draw(line2);
            line2.setLine(lowVal, x1 - 5.0, lowVal, x1 + 5.0);
            g2.draw(line2);
            line2.setLine(highVal, x1 - 5.0, highVal, x1 + 5.0);
            g2.draw(line2);
        }
        else {
            line2.setLine(x1, lowVal, x1, highVal);
            g2.draw(line2);
            line2.setLine(x1 - 5.0, highVal, x1 + 5.0, highVal);
            g2.draw(line2);
            line2.setLine(x1 - 5.0, lowVal, x1 + 5.0, lowVal);
            g2.draw(line2);
        }
        if (this.isItemLabelVisible(row, column)) {
            if (orientation == PlotOrientation.HORIZONTAL) {
                this.drawItemLabel(g2, orientation, dataset, row, column, y1, x1, meanValue.doubleValue() < 0.0);
            }
            else if (orientation == PlotOrientation.VERTICAL) {
                this.drawItemLabel(g2, orientation, dataset, row, column, x1, y1, meanValue.doubleValue() < 0.0);
            }
        }
        if (state.getInfo() != null) {
            final EntityCollection entities = state.getEntityCollection();
            if (entities != null && shape != null) {
                String tip = null;
                final CategoryToolTipGenerator tipster = this.getToolTipGenerator(row, column);
                if (tipster != null) {
                    tip = tipster.generateToolTip(dataset, row, column);
                }
                String url = null;
                if (this.getItemURLGenerator(row, column) != null) {
                    url = this.getItemURLGenerator(row, column).generateURL(dataset, row, column);
                }
                final CategoryItemEntity entity = new CategoryItemEntity(shape, tip, url, dataset, row, dataset.getColumnKey(column), column);
                entities.add(entity);
            }
        }
    }
    
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof StatisticalLineAndShapeRenderer)) {
            return false;
        }
        if (!super.equals(obj)) {
            return false;
        }
        final StatisticalLineAndShapeRenderer that = (StatisticalLineAndShapeRenderer)obj;
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
