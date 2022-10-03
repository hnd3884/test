package org.jfree.chart.renderer.category;

import org.jfree.util.ObjectUtilities;
import org.jfree.chart.labels.ItemLabelAnchor;
import java.awt.geom.Point2D;
import java.awt.Font;
import org.jfree.text.TextUtilities;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.labels.CategoryItemLabelGenerator;
import org.jfree.ui.RectangleEdge;
import java.awt.GradientPaint;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.Shape;
import java.awt.Color;
import java.awt.BasicStroke;
import java.awt.geom.Line2D;
import org.jfree.chart.LegendItem;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.data.Range;
import org.jfree.data.category.CategoryDataset;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.CategoryPlot;
import java.awt.geom.Rectangle2D;
import java.awt.Graphics2D;
import org.jfree.chart.event.RendererChangeEvent;
import org.jfree.ui.StandardGradientPaintTransformer;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.ui.GradientPaintTransformer;
import java.io.Serializable;
import org.jfree.util.PublicCloneable;

public class BarRenderer extends AbstractCategoryItemRenderer implements Cloneable, PublicCloneable, Serializable
{
    private static final long serialVersionUID = 6000649414965887481L;
    public static final double DEFAULT_ITEM_MARGIN = 0.2;
    public static final double BAR_OUTLINE_WIDTH_THRESHOLD = 3.0;
    private double itemMargin;
    private boolean drawBarOutline;
    private double maximumBarWidth;
    private double minimumBarLength;
    private GradientPaintTransformer gradientPaintTransformer;
    private ItemLabelPosition positiveItemLabelPositionFallback;
    private ItemLabelPosition negativeItemLabelPositionFallback;
    private double upperClip;
    private double lowerClip;
    private double base;
    private boolean includeBaseInRange;
    
    public BarRenderer() {
        this.base = 0.0;
        this.includeBaseInRange = true;
        this.itemMargin = 0.2;
        this.drawBarOutline = true;
        this.maximumBarWidth = 1.0;
        this.positiveItemLabelPositionFallback = null;
        this.negativeItemLabelPositionFallback = null;
        this.gradientPaintTransformer = (GradientPaintTransformer)new StandardGradientPaintTransformer();
        this.minimumBarLength = 0.0;
    }
    
    public double getBase() {
        return this.base;
    }
    
    public void setBase(final double base) {
        this.base = base;
        this.notifyListeners(new RendererChangeEvent(this));
    }
    
    public double getItemMargin() {
        return this.itemMargin;
    }
    
    public void setItemMargin(final double percent) {
        this.itemMargin = percent;
        this.notifyListeners(new RendererChangeEvent(this));
    }
    
    public boolean isDrawBarOutline() {
        return this.drawBarOutline;
    }
    
    public void setDrawBarOutline(final boolean draw) {
        this.drawBarOutline = draw;
        this.notifyListeners(new RendererChangeEvent(this));
    }
    
    public double getMaximumBarWidth() {
        return this.maximumBarWidth;
    }
    
    public void setMaximumBarWidth(final double percent) {
        this.maximumBarWidth = percent;
        this.notifyListeners(new RendererChangeEvent(this));
    }
    
    public double getMinimumBarLength() {
        return this.minimumBarLength;
    }
    
    public void setMinimumBarLength(final double min) {
        this.minimumBarLength = min;
        this.notifyListeners(new RendererChangeEvent(this));
    }
    
    public GradientPaintTransformer getGradientPaintTransformer() {
        return this.gradientPaintTransformer;
    }
    
    public void setGradientPaintTransformer(final GradientPaintTransformer transformer) {
        this.gradientPaintTransformer = transformer;
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
    
    public boolean getIncludeBaseInRange() {
        return this.includeBaseInRange;
    }
    
    public void setIncludeBaseInRange(final boolean include) {
        if (this.includeBaseInRange != include) {
            this.includeBaseInRange = include;
            this.notifyListeners(new RendererChangeEvent(this));
        }
    }
    
    public double getLowerClip() {
        return this.lowerClip;
    }
    
    public double getUpperClip() {
        return this.upperClip;
    }
    
    public CategoryItemRendererState initialise(final Graphics2D g2, final Rectangle2D dataArea, final CategoryPlot plot, final int rendererIndex, final PlotRenderingInfo info) {
        final CategoryItemRendererState state = super.initialise(g2, dataArea, plot, rendererIndex, info);
        final ValueAxis rangeAxis = this.getRangeAxis(plot, rendererIndex);
        this.lowerClip = rangeAxis.getRange().getLowerBound();
        this.upperClip = rangeAxis.getRange().getUpperBound();
        this.calculateBarWidth(plot, dataArea, rendererIndex, state);
        return state;
    }
    
    protected void calculateBarWidth(final CategoryPlot plot, final Rectangle2D dataArea, final int rendererIndex, final CategoryItemRendererState state) {
        final CategoryAxis domainAxis = this.getDomainAxis(plot, rendererIndex);
        final CategoryDataset dataset = plot.getDataset(rendererIndex);
        if (dataset != null) {
            final int columns = dataset.getColumnCount();
            final int rows = dataset.getRowCount();
            double space = 0.0;
            final PlotOrientation orientation = plot.getOrientation();
            if (orientation == PlotOrientation.HORIZONTAL) {
                space = dataArea.getHeight();
            }
            else if (orientation == PlotOrientation.VERTICAL) {
                space = dataArea.getWidth();
            }
            final double maxWidth = space * this.getMaximumBarWidth();
            double categoryMargin = 0.0;
            double currentItemMargin = 0.0;
            if (columns > 1) {
                categoryMargin = domainAxis.getCategoryMargin();
            }
            if (rows > 1) {
                currentItemMargin = this.getItemMargin();
            }
            final double used = space * (1.0 - domainAxis.getLowerMargin() - domainAxis.getUpperMargin() - categoryMargin - currentItemMargin);
            if (rows * columns > 0) {
                state.setBarWidth(Math.min(used / (rows * columns), maxWidth));
            }
            else {
                state.setBarWidth(Math.min(used, maxWidth));
            }
        }
    }
    
    protected double calculateBarW0(final CategoryPlot plot, final PlotOrientation orientation, final Rectangle2D dataArea, final CategoryAxis domainAxis, final CategoryItemRendererState state, final int row, final int column) {
        double space = 0.0;
        if (orientation == PlotOrientation.HORIZONTAL) {
            space = dataArea.getHeight();
        }
        else {
            space = dataArea.getWidth();
        }
        double barW0 = domainAxis.getCategoryStart(column, this.getColumnCount(), dataArea, plot.getDomainAxisEdge());
        final int seriesCount = this.getRowCount();
        final int categoryCount = this.getColumnCount();
        if (seriesCount > 1) {
            final double seriesGap = space * this.getItemMargin() / (categoryCount * (seriesCount - 1));
            final double seriesW = this.calculateSeriesWidth(space, domainAxis, categoryCount, seriesCount);
            barW0 = barW0 + row * (seriesW + seriesGap) + seriesW / 2.0 - state.getBarWidth() / 2.0;
        }
        else {
            barW0 = domainAxis.getCategoryMiddle(column, this.getColumnCount(), dataArea, plot.getDomainAxisEdge()) - state.getBarWidth() / 2.0;
        }
        return barW0;
    }
    
    protected double[] calculateBarL0L1(final double value) {
        final double lclip = this.getLowerClip();
        final double uclip = this.getUpperClip();
        double barLow = Math.min(this.base, value);
        double barHigh = Math.max(this.base, value);
        if (barHigh < lclip) {
            return null;
        }
        if (barLow > uclip) {
            return null;
        }
        barLow = Math.max(barLow, lclip);
        barHigh = Math.min(barHigh, uclip);
        return new double[] { barLow, barHigh };
    }
    
    public Range findRangeBounds(final CategoryDataset dataset) {
        Range result = DatasetUtilities.findRangeBounds(dataset);
        if (result != null && this.includeBaseInRange) {
            result = Range.expandToInclude(result, this.base);
        }
        return result;
    }
    
    public LegendItem getLegendItem(final int datasetIndex, final int series) {
        final CategoryPlot cp = this.getPlot();
        if (cp == null) {
            return null;
        }
        final CategoryDataset dataset = cp.getDataset(datasetIndex);
        final String description;
        final String label = description = this.getLegendItemLabelGenerator().generateLabel(dataset, series);
        String toolTipText = null;
        if (this.getLegendItemToolTipGenerator() != null) {
            toolTipText = this.getLegendItemToolTipGenerator().generateLabel(dataset, series);
        }
        String urlText = null;
        if (this.getLegendItemURLGenerator() != null) {
            urlText = this.getLegendItemURLGenerator().generateLabel(dataset, series);
        }
        final Shape shape = new Rectangle2D.Double(-4.0, -4.0, 8.0, 8.0);
        final Paint paint = this.getSeriesPaint(series);
        final Paint outlinePaint = this.getSeriesOutlinePaint(series);
        final Stroke outlineStroke = this.getSeriesOutlineStroke(series);
        return new LegendItem(label, description, toolTipText, urlText, true, shape, true, paint, this.isDrawBarOutline(), outlinePaint, outlineStroke, false, new Line2D.Float(), new BasicStroke(1.0f), Color.black);
    }
    
    public void drawItem(final Graphics2D g2, final CategoryItemRendererState state, final Rectangle2D dataArea, final CategoryPlot plot, final CategoryAxis domainAxis, final ValueAxis rangeAxis, final CategoryDataset dataset, final int row, final int column, final int pass) {
        final Number dataValue = dataset.getValue(row, column);
        if (dataValue == null) {
            return;
        }
        final double value = dataValue.doubleValue();
        final PlotOrientation orientation = plot.getOrientation();
        final double barW0 = this.calculateBarW0(plot, orientation, dataArea, domainAxis, state, row, column);
        final double[] barL0L1 = this.calculateBarL0L1(value);
        if (barL0L1 == null) {
            return;
        }
        final RectangleEdge edge = plot.getRangeAxisEdge();
        final double transL0 = rangeAxis.valueToJava2D(barL0L1[0], dataArea, edge);
        final double transL2 = rangeAxis.valueToJava2D(barL0L1[1], dataArea, edge);
        final double barL0 = Math.min(transL0, transL2);
        final double barLength = Math.max(Math.abs(transL2 - transL0), this.getMinimumBarLength());
        Rectangle2D bar = null;
        if (orientation == PlotOrientation.HORIZONTAL) {
            bar = new Rectangle2D.Double(barL0, barW0, barLength, state.getBarWidth());
        }
        else {
            bar = new Rectangle2D.Double(barW0, barL0, state.getBarWidth(), barLength);
        }
        Paint itemPaint = this.getItemPaint(row, column);
        final GradientPaintTransformer t = this.getGradientPaintTransformer();
        if (t != null && itemPaint instanceof GradientPaint) {
            itemPaint = t.transform((GradientPaint)itemPaint, (Shape)bar);
        }
        g2.setPaint(itemPaint);
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
            this.drawItemLabel(g2, dataset, row, column, plot, generator, bar, value < 0.0);
        }
        final EntityCollection entities = state.getEntityCollection();
        if (entities != null) {
            this.addItemEntity(entities, dataset, row, column, bar);
        }
    }
    
    protected double calculateSeriesWidth(final double space, final CategoryAxis axis, final int categories, final int series) {
        double factor = 1.0 - this.getItemMargin() - axis.getLowerMargin() - axis.getUpperMargin();
        if (categories > 1) {
            factor -= axis.getCategoryMargin();
        }
        return space * factor / (categories * series);
    }
    
    protected void drawItemLabel(final Graphics2D g2, final CategoryDataset data, final int row, final int column, final CategoryPlot plot, final CategoryItemLabelGenerator generator, final Rectangle2D bar, final boolean negative) {
        final String label = generator.generateLabel(data, row, column);
        if (label == null) {
            return;
        }
        final Font labelFont = this.getItemLabelFont(row, column);
        g2.setFont(labelFont);
        final Paint paint = this.getItemLabelPaint(row, column);
        g2.setPaint(paint);
        ItemLabelPosition position = null;
        if (!negative) {
            position = this.getPositiveItemLabelPosition(row, column);
        }
        else {
            position = this.getNegativeItemLabelPosition(row, column);
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
    
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof BarRenderer)) {
            return false;
        }
        if (!super.equals(obj)) {
            return false;
        }
        final BarRenderer that = (BarRenderer)obj;
        return this.base == that.base && this.itemMargin == that.itemMargin && this.drawBarOutline == that.drawBarOutline && this.maximumBarWidth == that.maximumBarWidth && this.minimumBarLength == that.minimumBarLength && ObjectUtilities.equal((Object)this.gradientPaintTransformer, (Object)that.gradientPaintTransformer) && ObjectUtilities.equal((Object)this.positiveItemLabelPositionFallback, (Object)that.positiveItemLabelPositionFallback) && ObjectUtilities.equal((Object)this.negativeItemLabelPositionFallback, (Object)that.negativeItemLabelPositionFallback);
    }
}
