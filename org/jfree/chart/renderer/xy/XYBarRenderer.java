package org.jfree.chart.renderer.xy;

import java.io.ObjectOutputStream;
import java.io.IOException;
import org.jfree.io.SerialUtilities;
import java.io.ObjectInputStream;
import org.jfree.util.ShapeUtilities;
import org.jfree.util.ObjectUtilities;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.data.Range;
import org.jfree.chart.labels.ItemLabelAnchor;
import java.awt.geom.Point2D;
import java.awt.Font;
import org.jfree.text.TextUtilities;
import org.jfree.chart.labels.XYToolTipGenerator;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.labels.XYItemLabelGenerator;
import org.jfree.ui.RectangleEdge;
import org.jfree.chart.entity.ChartEntity;
import org.jfree.chart.entity.XYItemEntity;
import java.awt.GradientPaint;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.IntervalXYDataset;
import org.jfree.chart.plot.CrosshairState;
import java.awt.Stroke;
import java.awt.Paint;
import org.jfree.chart.labels.XYSeriesLabelGenerator;
import org.jfree.chart.LegendItem;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.data.xy.XYDataset;
import org.jfree.chart.plot.XYPlot;
import java.awt.Graphics2D;
import org.jfree.chart.event.RendererChangeEvent;
import java.awt.geom.Rectangle2D;
import org.jfree.ui.StandardGradientPaintTransformer;
import org.jfree.chart.labels.ItemLabelPosition;
import java.awt.Shape;
import org.jfree.ui.GradientPaintTransformer;
import java.io.Serializable;
import org.jfree.util.PublicCloneable;

public class XYBarRenderer extends AbstractXYItemRenderer implements XYItemRenderer, Cloneable, PublicCloneable, Serializable
{
    private static final long serialVersionUID = 770559577251370036L;
    private double base;
    private boolean useYInterval;
    private double margin;
    private boolean drawBarOutline;
    private GradientPaintTransformer gradientPaintTransformer;
    private transient Shape legendBar;
    private ItemLabelPosition positiveItemLabelPositionFallback;
    private ItemLabelPosition negativeItemLabelPositionFallback;
    
    public XYBarRenderer() {
        this(0.0);
    }
    
    public XYBarRenderer(final double margin) {
        this.margin = margin;
        this.base = 0.0;
        this.useYInterval = false;
        this.gradientPaintTransformer = (GradientPaintTransformer)new StandardGradientPaintTransformer();
        this.drawBarOutline = true;
        this.legendBar = new Rectangle2D.Double(-3.0, -5.0, 6.0, 10.0);
    }
    
    public double getBase() {
        return this.base;
    }
    
    public void setBase(final double base) {
        this.base = base;
        this.notifyListeners(new RendererChangeEvent(this));
    }
    
    public boolean getUseYInterval() {
        return this.useYInterval;
    }
    
    public void setUseYInterval(final boolean use) {
        this.useYInterval = use;
        this.notifyListeners(new RendererChangeEvent(this));
    }
    
    public double getMargin() {
        return this.margin;
    }
    
    public void setMargin(final double margin) {
        this.margin = margin;
        this.notifyListeners(new RendererChangeEvent(this));
    }
    
    public boolean isDrawBarOutline() {
        return this.drawBarOutline;
    }
    
    public void setDrawBarOutline(final boolean draw) {
        this.drawBarOutline = draw;
        this.notifyListeners(new RendererChangeEvent(this));
    }
    
    public GradientPaintTransformer getGradientPaintTransformer() {
        return this.gradientPaintTransformer;
    }
    
    public void setGradientPaintTransformer(final GradientPaintTransformer transformer) {
        this.gradientPaintTransformer = transformer;
        this.notifyListeners(new RendererChangeEvent(this));
    }
    
    public Shape getLegendBar() {
        return this.legendBar;
    }
    
    public void setLegendBar(final Shape bar) {
        if (bar == null) {
            throw new IllegalArgumentException("Null 'bar' argument.");
        }
        this.legendBar = bar;
        this.notifyListeners(new RendererChangeEvent(this));
    }
    
    public ItemLabelPosition getPositiveItemLabelPositionFallback() {
        return this.positiveItemLabelPositionFallback;
    }
    
    public void setPositiveItemLabelPositionFallback(final ItemLabelPosition position) {
        this.positiveItemLabelPositionFallback = position;
        this.notifyListeners(new RendererChangeEvent(this));
    }
    
    public ItemLabelPosition getNegativeItemLabelPositionFallback() {
        return this.negativeItemLabelPositionFallback;
    }
    
    public void setNegativeItemLabelPositionFallback(final ItemLabelPosition position) {
        this.negativeItemLabelPositionFallback = position;
        this.notifyListeners(new RendererChangeEvent(this));
    }
    
    public XYItemRendererState initialise(final Graphics2D g2, final Rectangle2D dataArea, final XYPlot plot, final XYDataset dataset, final PlotRenderingInfo info) {
        final XYBarRendererState state = new XYBarRendererState(info);
        final ValueAxis rangeAxis = plot.getRangeAxisForDataset(plot.indexOf(dataset));
        state.setG2Base(rangeAxis.valueToJava2D(this.base, dataArea, plot.getRangeAxisEdge()));
        return state;
    }
    
    public LegendItem getLegendItem(final int datasetIndex, final int series) {
        LegendItem result = null;
        final XYPlot xyplot = this.getPlot();
        if (xyplot != null) {
            final XYDataset dataset = xyplot.getDataset(datasetIndex);
            if (dataset != null) {
                final XYSeriesLabelGenerator lg = this.getLegendItemLabelGenerator();
                final String description;
                final String label = description = lg.generateLabel(dataset, series);
                String toolTipText = null;
                if (this.getLegendItemToolTipGenerator() != null) {
                    toolTipText = this.getLegendItemToolTipGenerator().generateLabel(dataset, series);
                }
                String urlText = null;
                if (this.getLegendItemURLGenerator() != null) {
                    urlText = this.getLegendItemURLGenerator().generateLabel(dataset, series);
                }
                final Shape shape = this.legendBar;
                final Paint paint = this.getSeriesPaint(series);
                final Paint outlinePaint = this.getSeriesOutlinePaint(series);
                final Stroke outlineStroke = this.getSeriesOutlineStroke(series);
                result = new LegendItem(label, description, toolTipText, urlText, shape, paint, outlineStroke, outlinePaint);
            }
        }
        return result;
    }
    
    public void drawItem(final Graphics2D g2, final XYItemRendererState state, final Rectangle2D dataArea, final PlotRenderingInfo info, final XYPlot plot, final ValueAxis domainAxis, final ValueAxis rangeAxis, final XYDataset dataset, final int series, final int item, final CrosshairState crosshairState, final int pass) {
        if (!this.getItemVisible(series, item)) {
            return;
        }
        final IntervalXYDataset intervalDataset = (IntervalXYDataset)dataset;
        double value0;
        double value2;
        if (this.useYInterval) {
            value0 = intervalDataset.getStartYValue(series, item);
            value2 = intervalDataset.getEndYValue(series, item);
        }
        else {
            value0 = this.base;
            value2 = intervalDataset.getYValue(series, item);
        }
        if (Double.isNaN(value0) || Double.isNaN(value2)) {
            return;
        }
        final double translatedValue0 = rangeAxis.valueToJava2D(value0, dataArea, plot.getRangeAxisEdge());
        final double translatedValue2 = rangeAxis.valueToJava2D(value2, dataArea, plot.getRangeAxisEdge());
        final RectangleEdge location = plot.getDomainAxisEdge();
        final double startX = intervalDataset.getStartXValue(series, item);
        if (Double.isNaN(startX)) {
            return;
        }
        double translatedStartX = domainAxis.valueToJava2D(startX, dataArea, location);
        final double endX = intervalDataset.getEndXValue(series, item);
        if (Double.isNaN(endX)) {
            return;
        }
        final double translatedEndX = domainAxis.valueToJava2D(endX, dataArea, location);
        double translatedWidth = Math.max(1.0, Math.abs(translatedEndX - translatedStartX));
        final double translatedHeight = Math.abs(translatedValue2 - translatedValue0);
        if (this.getMargin() > 0.0) {
            final double cut = translatedWidth * this.getMargin();
            translatedWidth -= cut;
            translatedStartX += cut / 2.0;
        }
        Rectangle2D bar = null;
        final PlotOrientation orientation = plot.getOrientation();
        if (orientation == PlotOrientation.HORIZONTAL) {
            bar = new Rectangle2D.Double(Math.min(translatedValue0, translatedValue2), Math.min(translatedStartX, translatedEndX), translatedHeight, translatedWidth);
        }
        else if (orientation == PlotOrientation.VERTICAL) {
            bar = new Rectangle2D.Double(Math.min(translatedStartX, translatedEndX), Math.min(translatedValue0, translatedValue2), translatedWidth, translatedHeight);
        }
        Paint itemPaint = this.getItemPaint(series, item);
        if (this.getGradientPaintTransformer() != null && itemPaint instanceof GradientPaint) {
            final GradientPaint gp = (GradientPaint)itemPaint;
            itemPaint = this.getGradientPaintTransformer().transform(gp, (Shape)bar);
        }
        g2.setPaint(itemPaint);
        g2.fill(bar);
        if (this.isDrawBarOutline() && Math.abs(translatedEndX - translatedStartX) > 3.0) {
            final Stroke stroke = this.getItemOutlineStroke(series, item);
            final Paint paint = this.getItemOutlinePaint(series, item);
            if (stroke != null && paint != null) {
                g2.setStroke(stroke);
                g2.setPaint(paint);
                g2.draw(bar);
            }
        }
        if (this.isItemLabelVisible(series, item)) {
            final XYItemLabelGenerator generator = this.getItemLabelGenerator(series, item);
            this.drawItemLabel(g2, dataset, series, item, plot, generator, bar, value2 < 0.0);
        }
        final double x1 = (startX + endX) / 2.0;
        final double y1 = dataset.getYValue(series, item);
        final double transX1 = domainAxis.valueToJava2D(x1, dataArea, location);
        final double transY1 = rangeAxis.valueToJava2D(y1, dataArea, plot.getRangeAxisEdge());
        this.updateCrosshairValues(crosshairState, x1, y1, transX1, transY1, plot.getOrientation());
        if (info != null) {
            final EntityCollection entities = info.getOwner().getEntityCollection();
            if (entities != null) {
                String tip = null;
                final XYToolTipGenerator generator2 = this.getToolTipGenerator(series, item);
                if (generator2 != null) {
                    tip = generator2.generateToolTip(dataset, series, item);
                }
                String url = null;
                if (this.getURLGenerator() != null) {
                    url = this.getURLGenerator().generateURL(dataset, series, item);
                }
                final XYItemEntity entity = new XYItemEntity(bar, dataset, series, item, tip, url);
                entities.add(entity);
            }
        }
    }
    
    protected void drawItemLabel(final Graphics2D g2, final XYDataset dataset, final int series, final int item, final XYPlot plot, final XYItemLabelGenerator generator, final Rectangle2D bar, final boolean negative) {
        final String label = generator.generateLabel(dataset, series, item);
        if (label == null) {
            return;
        }
        final Font labelFont = this.getItemLabelFont(series, item);
        g2.setFont(labelFont);
        final Paint paint = this.getItemLabelPaint(series, item);
        g2.setPaint(paint);
        ItemLabelPosition position = null;
        if (!negative) {
            position = this.getPositiveItemLabelPosition(series, item);
        }
        else {
            position = this.getNegativeItemLabelPosition(series, item);
        }
        Point2D anchorPoint = this.calculateLabelAnchorPoint(position.getItemLabelAnchor(), bar, plot.getOrientation());
        if (this.isInternalAnchor(position.getItemLabelAnchor())) {
            final Shape bounds = TextUtilities.calculateRotatedStringBounds(label, g2, (float)anchorPoint.getX(), (float)anchorPoint.getY(), position.getTextAnchor(), position.getAngle(), position.getRotationAnchor());
            if (bounds != null && !bar.contains(bounds.getBounds2D())) {
                if (!negative) {
                    position = this.getPositiveItemLabelPositionFallback();
                }
                else {
                    position = this.getNegativeItemLabelPositionFallback();
                }
                if (position != null) {
                    anchorPoint = this.calculateLabelAnchorPoint(position.getItemLabelAnchor(), bar, plot.getOrientation());
                }
            }
        }
        if (position != null) {
            TextUtilities.drawRotatedString(label, g2, (float)anchorPoint.getX(), (float)anchorPoint.getY(), position.getTextAnchor(), position.getAngle(), position.getRotationAnchor());
        }
    }
    
    private Point2D calculateLabelAnchorPoint(final ItemLabelAnchor anchor, final Rectangle2D bar, final PlotOrientation orientation) {
        Point2D result = null;
        final double offset = this.getItemLabelAnchorOffset();
        final double x0 = bar.getX() - offset;
        final double x2 = bar.getX();
        final double x3 = bar.getX() + offset;
        final double x4 = bar.getCenterX();
        final double x5 = bar.getMaxX() - offset;
        final double x6 = bar.getMaxX();
        final double x7 = bar.getMaxX() + offset;
        final double y0 = bar.getMaxY() + offset;
        final double y2 = bar.getMaxY();
        final double y3 = bar.getMaxY() - offset;
        final double y4 = bar.getCenterY();
        final double y5 = bar.getMinY() + offset;
        final double y6 = bar.getMinY();
        final double y7 = bar.getMinY() - offset;
        if (anchor == ItemLabelAnchor.CENTER) {
            result = new Point2D.Double(x4, y4);
        }
        else if (anchor == ItemLabelAnchor.INSIDE1) {
            result = new Point2D.Double(x5, y5);
        }
        else if (anchor == ItemLabelAnchor.INSIDE2) {
            result = new Point2D.Double(x5, y5);
        }
        else if (anchor == ItemLabelAnchor.INSIDE3) {
            result = new Point2D.Double(x5, y4);
        }
        else if (anchor == ItemLabelAnchor.INSIDE4) {
            result = new Point2D.Double(x5, y3);
        }
        else if (anchor == ItemLabelAnchor.INSIDE5) {
            result = new Point2D.Double(x5, y3);
        }
        else if (anchor == ItemLabelAnchor.INSIDE6) {
            result = new Point2D.Double(x4, y3);
        }
        else if (anchor == ItemLabelAnchor.INSIDE7) {
            result = new Point2D.Double(x3, y3);
        }
        else if (anchor == ItemLabelAnchor.INSIDE8) {
            result = new Point2D.Double(x3, y3);
        }
        else if (anchor == ItemLabelAnchor.INSIDE9) {
            result = new Point2D.Double(x3, y4);
        }
        else if (anchor == ItemLabelAnchor.INSIDE10) {
            result = new Point2D.Double(x3, y5);
        }
        else if (anchor == ItemLabelAnchor.INSIDE11) {
            result = new Point2D.Double(x3, y5);
        }
        else if (anchor == ItemLabelAnchor.INSIDE12) {
            result = new Point2D.Double(x4, y5);
        }
        else if (anchor == ItemLabelAnchor.OUTSIDE1) {
            result = new Point2D.Double(x6, y7);
        }
        else if (anchor == ItemLabelAnchor.OUTSIDE2) {
            result = new Point2D.Double(x7, y6);
        }
        else if (anchor == ItemLabelAnchor.OUTSIDE3) {
            result = new Point2D.Double(x7, y4);
        }
        else if (anchor == ItemLabelAnchor.OUTSIDE4) {
            result = new Point2D.Double(x7, y2);
        }
        else if (anchor == ItemLabelAnchor.OUTSIDE5) {
            result = new Point2D.Double(x6, y0);
        }
        else if (anchor == ItemLabelAnchor.OUTSIDE6) {
            result = new Point2D.Double(x4, y0);
        }
        else if (anchor == ItemLabelAnchor.OUTSIDE7) {
            result = new Point2D.Double(x2, y0);
        }
        else if (anchor == ItemLabelAnchor.OUTSIDE8) {
            result = new Point2D.Double(x0, y2);
        }
        else if (anchor == ItemLabelAnchor.OUTSIDE9) {
            result = new Point2D.Double(x0, y4);
        }
        else if (anchor == ItemLabelAnchor.OUTSIDE10) {
            result = new Point2D.Double(x0, y6);
        }
        else if (anchor == ItemLabelAnchor.OUTSIDE11) {
            result = new Point2D.Double(x2, y7);
        }
        else if (anchor == ItemLabelAnchor.OUTSIDE12) {
            result = new Point2D.Double(x4, y7);
        }
        return result;
    }
    
    private boolean isInternalAnchor(final ItemLabelAnchor anchor) {
        return anchor == ItemLabelAnchor.CENTER || anchor == ItemLabelAnchor.INSIDE1 || anchor == ItemLabelAnchor.INSIDE2 || anchor == ItemLabelAnchor.INSIDE3 || anchor == ItemLabelAnchor.INSIDE4 || anchor == ItemLabelAnchor.INSIDE5 || anchor == ItemLabelAnchor.INSIDE6 || anchor == ItemLabelAnchor.INSIDE7 || anchor == ItemLabelAnchor.INSIDE8 || anchor == ItemLabelAnchor.INSIDE9 || anchor == ItemLabelAnchor.INSIDE10 || anchor == ItemLabelAnchor.INSIDE11 || anchor == ItemLabelAnchor.INSIDE12;
    }
    
    public Range findDomainBounds(final XYDataset dataset) {
        if (dataset != null) {
            return DatasetUtilities.findDomainBounds(dataset, true);
        }
        return null;
    }
    
    public Object clone() throws CloneNotSupportedException {
        final XYBarRenderer result = (XYBarRenderer)super.clone();
        if (this.gradientPaintTransformer != null) {
            result.gradientPaintTransformer = (GradientPaintTransformer)ObjectUtilities.clone((Object)this.gradientPaintTransformer);
        }
        return result;
    }
    
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof XYBarRenderer)) {
            return false;
        }
        if (!super.equals(obj)) {
            return false;
        }
        final XYBarRenderer that = (XYBarRenderer)obj;
        return this.base == that.base && this.drawBarOutline == that.drawBarOutline && this.margin == that.margin && this.useYInterval == that.useYInterval && ObjectUtilities.equal((Object)this.gradientPaintTransformer, (Object)that.gradientPaintTransformer) && ShapeUtilities.equal(this.legendBar, that.legendBar) && ObjectUtilities.equal((Object)this.positiveItemLabelPositionFallback, (Object)that.positiveItemLabelPositionFallback) && ObjectUtilities.equal((Object)this.negativeItemLabelPositionFallback, (Object)that.negativeItemLabelPositionFallback);
    }
    
    private void readObject(final ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        this.legendBar = SerialUtilities.readShape(stream);
    }
    
    private void writeObject(final ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        SerialUtilities.writeShape(this.legendBar, stream);
    }
    
    protected class XYBarRendererState extends XYItemRendererState
    {
        private double g2Base;
        
        public XYBarRendererState(final PlotRenderingInfo info) {
            super(info);
        }
        
        public double getG2Base() {
            return this.g2Base;
        }
        
        public void setG2Base(final double value) {
            this.g2Base = value;
        }
    }
}
