package org.jfree.chart.renderer.category;

import java.io.ObjectInputStream;
import java.io.IOException;
import org.jfree.io.SerialUtilities;
import java.io.ObjectOutputStream;
import org.jfree.util.PaintUtilities;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.labels.CategoryItemLabelGenerator;
import java.awt.Stroke;
import org.jfree.ui.RectangleEdge;
import java.awt.Shape;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.plot.CategoryPlot;
import java.awt.geom.Rectangle2D;
import java.awt.Graphics2D;
import org.jfree.chart.event.RendererChangeEvent;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.data.Range;
import org.jfree.data.category.CategoryDataset;
import org.jfree.ui.GradientPaintTransformer;
import org.jfree.ui.StandardGradientPaintTransformer;
import org.jfree.ui.GradientPaintTransformType;
import java.awt.GradientPaint;
import java.awt.Color;
import java.awt.Paint;
import java.io.Serializable;
import org.jfree.util.PublicCloneable;

public class WaterfallBarRenderer extends BarRenderer implements Cloneable, PublicCloneable, Serializable
{
    private static final long serialVersionUID = -2482910643727230911L;
    private transient Paint firstBarPaint;
    private transient Paint lastBarPaint;
    private transient Paint positiveBarPaint;
    private transient Paint negativeBarPaint;
    
    public WaterfallBarRenderer() {
        this(new GradientPaint(0.0f, 0.0f, new Color(34, 34, 255), 0.0f, 0.0f, new Color(102, 102, 255)), new GradientPaint(0.0f, 0.0f, new Color(34, 255, 34), 0.0f, 0.0f, new Color(102, 255, 102)), new GradientPaint(0.0f, 0.0f, new Color(255, 34, 34), 0.0f, 0.0f, new Color(255, 102, 102)), new GradientPaint(0.0f, 0.0f, new Color(255, 255, 34), 0.0f, 0.0f, new Color(255, 255, 102)));
    }
    
    public WaterfallBarRenderer(final Paint firstBarPaint, final Paint positiveBarPaint, final Paint negativeBarPaint, final Paint lastBarPaint) {
        if (firstBarPaint == null) {
            throw new IllegalArgumentException("Null 'firstBarPaint' argument");
        }
        if (positiveBarPaint == null) {
            throw new IllegalArgumentException("Null 'positiveBarPaint' argument");
        }
        if (negativeBarPaint == null) {
            throw new IllegalArgumentException("Null 'negativeBarPaint' argument");
        }
        if (lastBarPaint == null) {
            throw new IllegalArgumentException("Null 'lastBarPaint' argument");
        }
        this.firstBarPaint = firstBarPaint;
        this.lastBarPaint = lastBarPaint;
        this.positiveBarPaint = positiveBarPaint;
        this.negativeBarPaint = negativeBarPaint;
        this.setGradientPaintTransformer((GradientPaintTransformer)new StandardGradientPaintTransformer(GradientPaintTransformType.CENTER_VERTICAL));
        this.setMinimumBarLength(1.0);
    }
    
    public Range findRangeBounds(final CategoryDataset dataset) {
        return DatasetUtilities.findCumulativeRangeBounds(dataset);
    }
    
    public Paint getFirstBarPaint() {
        return this.firstBarPaint;
    }
    
    public void setFirstBarPaint(final Paint paint) {
        if (paint == null) {
            throw new IllegalArgumentException("Null 'paint' argument");
        }
        this.firstBarPaint = paint;
        this.notifyListeners(new RendererChangeEvent(this));
    }
    
    public Paint getLastBarPaint() {
        return this.lastBarPaint;
    }
    
    public void setLastBarPaint(final Paint paint) {
        if (paint == null) {
            throw new IllegalArgumentException("Null 'paint' argument");
        }
        this.lastBarPaint = paint;
        this.notifyListeners(new RendererChangeEvent(this));
    }
    
    public Paint getPositiveBarPaint() {
        return this.positiveBarPaint;
    }
    
    public void setPositiveBarPaint(final Paint paint) {
        if (paint == null) {
            throw new IllegalArgumentException("Null 'paint' argument");
        }
        this.positiveBarPaint = paint;
        this.notifyListeners(new RendererChangeEvent(this));
    }
    
    public Paint getNegativeBarPaint() {
        return this.negativeBarPaint;
    }
    
    public void setNegativeBarPaint(final Paint paint) {
        if (paint == null) {
            throw new IllegalArgumentException("Null 'paint' argument");
        }
        this.negativeBarPaint = paint;
        this.notifyListeners(new RendererChangeEvent(this));
    }
    
    public void drawItem(final Graphics2D g2, final CategoryItemRendererState state, final Rectangle2D dataArea, final CategoryPlot plot, final CategoryAxis domainAxis, final ValueAxis rangeAxis, final CategoryDataset dataset, final int row, final int column, final int pass) {
        double previous = state.getSeriesRunningTotal();
        if (column == dataset.getColumnCount() - 1) {
            previous = 0.0;
        }
        double current = 0.0;
        final Number n = dataset.getValue(row, column);
        if (n != null) {
            current = previous + n.doubleValue();
        }
        state.setSeriesRunningTotal(current);
        final int seriesCount = this.getRowCount();
        final int categoryCount = this.getColumnCount();
        final PlotOrientation orientation = plot.getOrientation();
        double rectX = 0.0;
        double rectY = 0.0;
        final RectangleEdge domainAxisLocation = plot.getDomainAxisEdge();
        final RectangleEdge rangeAxisLocation = plot.getRangeAxisEdge();
        double j2dy0 = rangeAxis.valueToJava2D(previous, dataArea, rangeAxisLocation);
        double j2dy2 = rangeAxis.valueToJava2D(current, dataArea, rangeAxisLocation);
        final double valDiff = current - previous;
        if (j2dy2 < j2dy0) {
            final double temp = j2dy2;
            j2dy2 = j2dy0;
            j2dy0 = temp;
        }
        double rectWidth = state.getBarWidth();
        double rectHeight = Math.max(this.getMinimumBarLength(), Math.abs(j2dy2 - j2dy0));
        if (orientation == PlotOrientation.HORIZONTAL) {
            rectY = domainAxis.getCategoryStart(column, this.getColumnCount(), dataArea, domainAxisLocation);
            if (seriesCount > 1) {
                final double seriesGap = dataArea.getHeight() * this.getItemMargin() / (categoryCount * (seriesCount - 1));
                rectY += row * (state.getBarWidth() + seriesGap);
            }
            else {
                rectY += row * state.getBarWidth();
            }
            rectX = j2dy0;
            rectHeight = state.getBarWidth();
            rectWidth = Math.max(this.getMinimumBarLength(), Math.abs(j2dy2 - j2dy0));
        }
        else if (orientation == PlotOrientation.VERTICAL) {
            rectX = domainAxis.getCategoryStart(column, this.getColumnCount(), dataArea, domainAxisLocation);
            if (seriesCount > 1) {
                final double seriesGap = dataArea.getWidth() * this.getItemMargin() / (categoryCount * (seriesCount - 1));
                rectX += row * (state.getBarWidth() + seriesGap);
            }
            else {
                rectX += row * state.getBarWidth();
            }
            rectY = j2dy0;
        }
        final Rectangle2D bar = new Rectangle2D.Double(rectX, rectY, rectWidth, rectHeight);
        Paint seriesPaint = this.getFirstBarPaint();
        if (column == 0) {
            seriesPaint = this.getFirstBarPaint();
        }
        else if (column == categoryCount - 1) {
            seriesPaint = this.getLastBarPaint();
        }
        else if (valDiff < 0.0) {
            seriesPaint = this.getNegativeBarPaint();
        }
        else if (valDiff > 0.0) {
            seriesPaint = this.getPositiveBarPaint();
        }
        else {
            seriesPaint = this.getLastBarPaint();
        }
        if (this.getGradientPaintTransformer() != null && seriesPaint instanceof GradientPaint) {
            final GradientPaint gp = (GradientPaint)seriesPaint;
            seriesPaint = this.getGradientPaintTransformer().transform(gp, (Shape)bar);
        }
        g2.setPaint(seriesPaint);
        g2.fill(bar);
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
            this.drawItemLabel(g2, dataset, row, column, plot, generator, bar, valDiff < 0.0);
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
        if (!super.equals(obj)) {
            return false;
        }
        if (!(obj instanceof WaterfallBarRenderer)) {
            return false;
        }
        final WaterfallBarRenderer that = (WaterfallBarRenderer)obj;
        return PaintUtilities.equal(this.firstBarPaint, that.firstBarPaint) && PaintUtilities.equal(this.lastBarPaint, that.lastBarPaint) && PaintUtilities.equal(this.positiveBarPaint, that.positiveBarPaint) && PaintUtilities.equal(this.negativeBarPaint, that.negativeBarPaint);
    }
    
    private void writeObject(final ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        SerialUtilities.writePaint(this.firstBarPaint, stream);
        SerialUtilities.writePaint(this.lastBarPaint, stream);
        SerialUtilities.writePaint(this.positiveBarPaint, stream);
        SerialUtilities.writePaint(this.negativeBarPaint, stream);
    }
    
    private void readObject(final ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        this.firstBarPaint = SerialUtilities.readPaint(stream);
        this.lastBarPaint = SerialUtilities.readPaint(stream);
        this.positiveBarPaint = SerialUtilities.readPaint(stream);
        this.negativeBarPaint = SerialUtilities.readPaint(stream);
    }
}
