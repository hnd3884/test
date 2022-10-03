package org.jfree.chart.renderer.category;

import org.jfree.chart.entity.ChartEntity;
import org.jfree.chart.entity.CategoryItemEntity;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.LegendItemCollection;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.plot.DrawingSupplier;
import org.jfree.util.ObjectUtilities;
import org.jfree.chart.LegendItem;
import org.jfree.ui.RectangleInsets;
import org.jfree.ui.GradientPaintTransformer;
import java.awt.GradientPaint;
import org.jfree.chart.plot.IntervalMarker;
import org.jfree.ui.LengthAdjustmentType;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.Marker;
import java.awt.geom.Point2D;
import java.awt.Font;
import org.jfree.ui.RectangleAnchor;
import org.jfree.text.TextUtilities;
import org.jfree.chart.plot.CategoryMarker;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.ValueAxis;
import java.awt.Stroke;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.Line2D;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.data.Range;
import org.jfree.data.category.CategoryDataset;
import org.jfree.chart.plot.PlotRenderingInfo;
import java.awt.geom.Rectangle2D;
import java.awt.Graphics2D;
import org.jfree.chart.event.RendererChangeEvent;
import org.jfree.chart.labels.StandardCategorySeriesLabelGenerator;
import org.jfree.chart.labels.CategorySeriesLabelGenerator;
import org.jfree.chart.urls.CategoryURLGenerator;
import org.jfree.chart.labels.CategoryToolTipGenerator;
import org.jfree.util.ObjectList;
import org.jfree.chart.labels.CategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import java.io.Serializable;
import org.jfree.util.PublicCloneable;
import org.jfree.chart.renderer.AbstractRenderer;

public abstract class AbstractCategoryItemRenderer extends AbstractRenderer implements CategoryItemRenderer, Cloneable, PublicCloneable, Serializable
{
    private static final long serialVersionUID = 1247553218442497391L;
    private CategoryPlot plot;
    private CategoryItemLabelGenerator itemLabelGenerator;
    private ObjectList itemLabelGeneratorList;
    private CategoryItemLabelGenerator baseItemLabelGenerator;
    private CategoryToolTipGenerator toolTipGenerator;
    private ObjectList toolTipGeneratorList;
    private CategoryToolTipGenerator baseToolTipGenerator;
    private CategoryURLGenerator itemURLGenerator;
    private ObjectList itemURLGeneratorList;
    private CategoryURLGenerator baseItemURLGenerator;
    private CategorySeriesLabelGenerator legendItemLabelGenerator;
    private CategorySeriesLabelGenerator legendItemToolTipGenerator;
    private CategorySeriesLabelGenerator legendItemURLGenerator;
    private transient int rowCount;
    private transient int columnCount;
    
    protected AbstractCategoryItemRenderer() {
        this.itemLabelGenerator = null;
        this.itemLabelGeneratorList = new ObjectList();
        this.toolTipGenerator = null;
        this.toolTipGeneratorList = new ObjectList();
        this.itemURLGenerator = null;
        this.itemURLGeneratorList = new ObjectList();
        this.legendItemLabelGenerator = new StandardCategorySeriesLabelGenerator();
    }
    
    public int getPassCount() {
        return 1;
    }
    
    public CategoryPlot getPlot() {
        return this.plot;
    }
    
    public void setPlot(final CategoryPlot plot) {
        if (plot == null) {
            throw new IllegalArgumentException("Null 'plot' argument.");
        }
        this.plot = plot;
    }
    
    public CategoryItemLabelGenerator getItemLabelGenerator(final int row, final int column) {
        return this.getSeriesItemLabelGenerator(row);
    }
    
    public CategoryItemLabelGenerator getSeriesItemLabelGenerator(final int series) {
        if (this.itemLabelGenerator != null) {
            return this.itemLabelGenerator;
        }
        CategoryItemLabelGenerator generator = (CategoryItemLabelGenerator)this.itemLabelGeneratorList.get(series);
        if (generator == null) {
            generator = this.baseItemLabelGenerator;
        }
        return generator;
    }
    
    public void setItemLabelGenerator(final CategoryItemLabelGenerator generator) {
        this.itemLabelGenerator = generator;
        this.notifyListeners(new RendererChangeEvent(this));
    }
    
    public void setSeriesItemLabelGenerator(final int series, final CategoryItemLabelGenerator generator) {
        this.itemLabelGeneratorList.set(series, (Object)generator);
        this.notifyListeners(new RendererChangeEvent(this));
    }
    
    public CategoryItemLabelGenerator getBaseItemLabelGenerator() {
        return this.baseItemLabelGenerator;
    }
    
    public void setBaseItemLabelGenerator(final CategoryItemLabelGenerator generator) {
        this.baseItemLabelGenerator = generator;
        this.notifyListeners(new RendererChangeEvent(this));
    }
    
    public CategoryToolTipGenerator getToolTipGenerator(final int row, final int column) {
        CategoryToolTipGenerator result = null;
        if (this.toolTipGenerator != null) {
            result = this.toolTipGenerator;
        }
        else {
            result = this.getSeriesToolTipGenerator(row);
            if (result == null) {
                result = this.baseToolTipGenerator;
            }
        }
        return result;
    }
    
    public CategoryToolTipGenerator getToolTipGenerator() {
        return this.toolTipGenerator;
    }
    
    public void setToolTipGenerator(final CategoryToolTipGenerator generator) {
        this.toolTipGenerator = generator;
        this.notifyListeners(new RendererChangeEvent(this));
    }
    
    public CategoryToolTipGenerator getSeriesToolTipGenerator(final int series) {
        return (CategoryToolTipGenerator)this.toolTipGeneratorList.get(series);
    }
    
    public void setSeriesToolTipGenerator(final int series, final CategoryToolTipGenerator generator) {
        this.toolTipGeneratorList.set(series, (Object)generator);
        this.notifyListeners(new RendererChangeEvent(this));
    }
    
    public CategoryToolTipGenerator getBaseToolTipGenerator() {
        return this.baseToolTipGenerator;
    }
    
    public void setBaseToolTipGenerator(final CategoryToolTipGenerator generator) {
        this.baseToolTipGenerator = generator;
        this.notifyListeners(new RendererChangeEvent(this));
    }
    
    public CategoryURLGenerator getItemURLGenerator(final int row, final int column) {
        return this.getSeriesItemURLGenerator(row);
    }
    
    public CategoryURLGenerator getSeriesItemURLGenerator(final int series) {
        if (this.itemURLGenerator != null) {
            return this.itemURLGenerator;
        }
        CategoryURLGenerator generator = (CategoryURLGenerator)this.itemURLGeneratorList.get(series);
        if (generator == null) {
            generator = this.baseItemURLGenerator;
        }
        return generator;
    }
    
    public void setItemURLGenerator(final CategoryURLGenerator generator) {
        this.itemURLGenerator = generator;
    }
    
    public void setSeriesItemURLGenerator(final int series, final CategoryURLGenerator generator) {
        this.itemURLGeneratorList.set(series, (Object)generator);
    }
    
    public CategoryURLGenerator getBaseItemURLGenerator() {
        return this.baseItemURLGenerator;
    }
    
    public void setBaseItemURLGenerator(final CategoryURLGenerator generator) {
        this.baseItemURLGenerator = generator;
    }
    
    public int getRowCount() {
        return this.rowCount;
    }
    
    public int getColumnCount() {
        return this.columnCount;
    }
    
    public CategoryItemRendererState initialise(final Graphics2D g2, final Rectangle2D dataArea, final CategoryPlot plot, final int rendererIndex, final PlotRenderingInfo info) {
        this.setPlot(plot);
        final CategoryDataset data = plot.getDataset(rendererIndex);
        if (data != null) {
            this.rowCount = data.getRowCount();
            this.columnCount = data.getColumnCount();
        }
        else {
            this.rowCount = 0;
            this.columnCount = 0;
        }
        return new CategoryItemRendererState(info);
    }
    
    public Range findRangeBounds(final CategoryDataset dataset) {
        return DatasetUtilities.findRangeBounds(dataset);
    }
    
    public void drawBackground(final Graphics2D g2, final CategoryPlot plot, final Rectangle2D dataArea) {
        plot.drawBackground(g2, dataArea);
    }
    
    public void drawOutline(final Graphics2D g2, final CategoryPlot plot, final Rectangle2D dataArea) {
        plot.drawOutline(g2, dataArea);
    }
    
    public void drawDomainGridline(final Graphics2D g2, final CategoryPlot plot, final Rectangle2D dataArea, final double value) {
        Line2D line = null;
        final PlotOrientation orientation = plot.getOrientation();
        if (orientation == PlotOrientation.HORIZONTAL) {
            line = new Line2D.Double(dataArea.getMinX(), value, dataArea.getMaxX(), value);
        }
        else if (orientation == PlotOrientation.VERTICAL) {
            line = new Line2D.Double(value, dataArea.getMinY(), value, dataArea.getMaxY());
        }
        Paint paint = plot.getDomainGridlinePaint();
        if (paint == null) {
            paint = CategoryPlot.DEFAULT_GRIDLINE_PAINT;
        }
        g2.setPaint(paint);
        Stroke stroke = plot.getDomainGridlineStroke();
        if (stroke == null) {
            stroke = CategoryPlot.DEFAULT_GRIDLINE_STROKE;
        }
        g2.setStroke(stroke);
        g2.draw(line);
    }
    
    public void drawRangeGridline(final Graphics2D g2, final CategoryPlot plot, final ValueAxis axis, final Rectangle2D dataArea, final double value) {
        final Range range = axis.getRange();
        if (!range.contains(value)) {
            return;
        }
        final PlotOrientation orientation = plot.getOrientation();
        final double v = axis.valueToJava2D(value, dataArea, plot.getRangeAxisEdge());
        Line2D line = null;
        if (orientation == PlotOrientation.HORIZONTAL) {
            line = new Line2D.Double(v, dataArea.getMinY(), v, dataArea.getMaxY());
        }
        else if (orientation == PlotOrientation.VERTICAL) {
            line = new Line2D.Double(dataArea.getMinX(), v, dataArea.getMaxX(), v);
        }
        Paint paint = plot.getRangeGridlinePaint();
        if (paint == null) {
            paint = CategoryPlot.DEFAULT_GRIDLINE_PAINT;
        }
        g2.setPaint(paint);
        Stroke stroke = plot.getRangeGridlineStroke();
        if (stroke == null) {
            stroke = CategoryPlot.DEFAULT_GRIDLINE_STROKE;
        }
        g2.setStroke(stroke);
        g2.draw(line);
    }
    
    public void drawDomainMarker(final Graphics2D g2, final CategoryPlot plot, final CategoryAxis axis, final CategoryMarker marker, final Rectangle2D dataArea) {
        final Comparable category = marker.getKey();
        final CategoryDataset dataset = plot.getDataset(plot.getIndexOf(this));
        final int columnIndex = dataset.getColumnIndex(category);
        if (columnIndex < 0) {
            return;
        }
        final PlotOrientation orientation = plot.getOrientation();
        Rectangle2D bounds = null;
        if (marker.getDrawAsLine()) {
            final double v = axis.getCategoryMiddle(columnIndex, dataset.getColumnCount(), dataArea, plot.getDomainAxisEdge());
            Line2D line = null;
            if (orientation == PlotOrientation.HORIZONTAL) {
                line = new Line2D.Double(dataArea.getMinX(), v, dataArea.getMaxX(), v);
            }
            else if (orientation == PlotOrientation.VERTICAL) {
                line = new Line2D.Double(v, dataArea.getMinY(), v, dataArea.getMaxY());
            }
            g2.setPaint(marker.getPaint());
            g2.setStroke(marker.getStroke());
            g2.draw(line);
            bounds = line.getBounds2D();
        }
        else {
            final double v2 = axis.getCategoryStart(columnIndex, dataset.getColumnCount(), dataArea, plot.getDomainAxisEdge());
            final double v3 = axis.getCategoryEnd(columnIndex, dataset.getColumnCount(), dataArea, plot.getDomainAxisEdge());
            Rectangle2D area = null;
            if (orientation == PlotOrientation.HORIZONTAL) {
                area = new Rectangle2D.Double(dataArea.getMinX(), v2, dataArea.getWidth(), v3 - v2);
            }
            else if (orientation == PlotOrientation.VERTICAL) {
                area = new Rectangle2D.Double(v2, dataArea.getMinY(), v3 - v2, dataArea.getHeight());
            }
            g2.setPaint(marker.getPaint());
            g2.fill(area);
            bounds = area;
        }
        final String label = marker.getLabel();
        final RectangleAnchor anchor = marker.getLabelAnchor();
        if (label != null) {
            final Font labelFont = marker.getLabelFont();
            g2.setFont(labelFont);
            g2.setPaint(marker.getLabelPaint());
            final Point2D coordinates = this.calculateDomainMarkerTextAnchorPoint(g2, orientation, dataArea, bounds, marker.getLabelOffset(), marker.getLabelOffsetType(), anchor);
            TextUtilities.drawAlignedString(label, g2, (float)coordinates.getX(), (float)coordinates.getY(), marker.getLabelTextAnchor());
        }
    }
    
    public void drawRangeMarker(final Graphics2D g2, final CategoryPlot plot, final ValueAxis axis, final Marker marker, final Rectangle2D dataArea) {
        if (marker instanceof ValueMarker) {
            final ValueMarker vm = (ValueMarker)marker;
            final double value = vm.getValue();
            final Range range = axis.getRange();
            if (!range.contains(value)) {
                return;
            }
            final PlotOrientation orientation = plot.getOrientation();
            final double v = axis.valueToJava2D(value, dataArea, plot.getRangeAxisEdge());
            Line2D line = null;
            if (orientation == PlotOrientation.HORIZONTAL) {
                line = new Line2D.Double(v, dataArea.getMinY(), v, dataArea.getMaxY());
            }
            else if (orientation == PlotOrientation.VERTICAL) {
                line = new Line2D.Double(dataArea.getMinX(), v, dataArea.getMaxX(), v);
            }
            g2.setPaint(marker.getPaint());
            g2.setStroke(marker.getStroke());
            g2.draw(line);
            final String label = marker.getLabel();
            final RectangleAnchor anchor = marker.getLabelAnchor();
            if (label != null) {
                final Font labelFont = marker.getLabelFont();
                g2.setFont(labelFont);
                g2.setPaint(marker.getLabelPaint());
                final Point2D coordinates = this.calculateRangeMarkerTextAnchorPoint(g2, orientation, dataArea, line.getBounds2D(), marker.getLabelOffset(), LengthAdjustmentType.EXPAND, anchor);
                TextUtilities.drawAlignedString(label, g2, (float)coordinates.getX(), (float)coordinates.getY(), marker.getLabelTextAnchor());
            }
        }
        else if (marker instanceof IntervalMarker) {
            final IntervalMarker im = (IntervalMarker)marker;
            double start = im.getStartValue();
            double end = im.getEndValue();
            final Range range2 = axis.getRange();
            if (!range2.intersects(start, end)) {
                return;
            }
            start = range2.constrain(start);
            end = range2.constrain(end);
            final double v2 = axis.valueToJava2D(start, dataArea, plot.getRangeAxisEdge());
            final double v3 = axis.valueToJava2D(end, dataArea, plot.getRangeAxisEdge());
            final PlotOrientation orientation2 = plot.getOrientation();
            Rectangle2D rect = null;
            if (orientation2 == PlotOrientation.HORIZONTAL) {
                rect = new Rectangle2D.Double(v2, dataArea.getMinY(), v3 - v2, dataArea.getHeight());
            }
            else if (orientation2 == PlotOrientation.VERTICAL) {
                rect = new Rectangle2D.Double(dataArea.getMinX(), Math.min(v2, v3), dataArea.getWidth(), Math.abs(v3 - v2));
            }
            final Paint p = marker.getPaint();
            if (p instanceof GradientPaint) {
                GradientPaint gp = (GradientPaint)p;
                final GradientPaintTransformer t = im.getGradientPaintTransformer();
                if (t != null) {
                    gp = t.transform(gp, (Shape)rect);
                }
                g2.setPaint(gp);
            }
            else {
                g2.setPaint(p);
            }
            g2.fill(rect);
            final String label2 = marker.getLabel();
            final RectangleAnchor anchor2 = marker.getLabelAnchor();
            if (label2 != null) {
                final Font labelFont2 = marker.getLabelFont();
                g2.setFont(labelFont2);
                g2.setPaint(marker.getLabelPaint());
                final Point2D coordinates2 = this.calculateRangeMarkerTextAnchorPoint(g2, orientation2, dataArea, rect, marker.getLabelOffset(), marker.getLabelOffsetType(), anchor2);
                TextUtilities.drawAlignedString(label2, g2, (float)coordinates2.getX(), (float)coordinates2.getY(), marker.getLabelTextAnchor());
            }
        }
    }
    
    protected Point2D calculateDomainMarkerTextAnchorPoint(final Graphics2D g2, final PlotOrientation orientation, final Rectangle2D dataArea, final Rectangle2D markerArea, final RectangleInsets markerOffset, final LengthAdjustmentType labelOffsetType, final RectangleAnchor anchor) {
        Rectangle2D anchorRect = null;
        if (orientation == PlotOrientation.HORIZONTAL) {
            anchorRect = markerOffset.createAdjustedRectangle(markerArea, LengthAdjustmentType.CONTRACT, labelOffsetType);
        }
        else if (orientation == PlotOrientation.VERTICAL) {
            anchorRect = markerOffset.createAdjustedRectangle(markerArea, labelOffsetType, LengthAdjustmentType.CONTRACT);
        }
        return RectangleAnchor.coordinates(anchorRect, anchor);
    }
    
    protected Point2D calculateRangeMarkerTextAnchorPoint(final Graphics2D g2, final PlotOrientation orientation, final Rectangle2D dataArea, final Rectangle2D markerArea, final RectangleInsets markerOffset, final LengthAdjustmentType labelOffsetType, final RectangleAnchor anchor) {
        Rectangle2D anchorRect = null;
        if (orientation == PlotOrientation.HORIZONTAL) {
            anchorRect = markerOffset.createAdjustedRectangle(markerArea, labelOffsetType, LengthAdjustmentType.CONTRACT);
        }
        else if (orientation == PlotOrientation.VERTICAL) {
            anchorRect = markerOffset.createAdjustedRectangle(markerArea, LengthAdjustmentType.CONTRACT, labelOffsetType);
        }
        return RectangleAnchor.coordinates(anchorRect, anchor);
    }
    
    public LegendItem getLegendItem(final int datasetIndex, final int series) {
        final CategoryPlot p = this.getPlot();
        if (p == null) {
            return null;
        }
        final CategoryDataset dataset = p.getDataset(datasetIndex);
        final String description;
        final String label = description = this.legendItemLabelGenerator.generateLabel(dataset, series);
        String toolTipText = null;
        if (this.legendItemToolTipGenerator != null) {
            toolTipText = this.legendItemToolTipGenerator.generateLabel(dataset, series);
        }
        String urlText = null;
        if (this.legendItemURLGenerator != null) {
            urlText = this.legendItemURLGenerator.generateLabel(dataset, series);
        }
        final Shape shape = this.getSeriesShape(series);
        final Paint paint = this.getSeriesPaint(series);
        final Paint outlinePaint = this.getSeriesOutlinePaint(series);
        final Stroke outlineStroke = this.getSeriesOutlineStroke(series);
        final LegendItem item = new LegendItem(label, description, toolTipText, urlText, shape, paint, outlineStroke, outlinePaint);
        item.setSeriesIndex(series);
        item.setDatasetIndex(datasetIndex);
        return item;
    }
    
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof AbstractCategoryItemRenderer)) {
            return false;
        }
        if (!super.equals(obj)) {
            return false;
        }
        final AbstractCategoryItemRenderer that = (AbstractCategoryItemRenderer)obj;
        return ObjectUtilities.equal((Object)this.itemLabelGenerator, (Object)that.itemLabelGenerator) && ObjectUtilities.equal((Object)this.itemLabelGeneratorList, (Object)that.itemLabelGeneratorList) && ObjectUtilities.equal((Object)this.baseItemLabelGenerator, (Object)that.baseItemLabelGenerator) && ObjectUtilities.equal((Object)this.toolTipGenerator, (Object)that.toolTipGenerator) && ObjectUtilities.equal((Object)this.toolTipGeneratorList, (Object)that.toolTipGeneratorList) && ObjectUtilities.equal((Object)this.baseToolTipGenerator, (Object)that.baseToolTipGenerator) && ObjectUtilities.equal((Object)this.itemURLGenerator, (Object)that.itemURLGenerator) && ObjectUtilities.equal((Object)this.itemURLGeneratorList, (Object)that.itemURLGeneratorList) && ObjectUtilities.equal((Object)this.baseItemURLGenerator, (Object)that.baseItemURLGenerator);
    }
    
    public int hashCode() {
        final int result = super.hashCode();
        return result;
    }
    
    public DrawingSupplier getDrawingSupplier() {
        DrawingSupplier result = null;
        final CategoryPlot cp = this.getPlot();
        if (cp != null) {
            result = cp.getDrawingSupplier();
        }
        return result;
    }
    
    protected void drawItemLabel(final Graphics2D g2, final PlotOrientation orientation, final CategoryDataset dataset, final int row, final int column, final double x, final double y, final boolean negative) {
        final CategoryItemLabelGenerator generator = this.getItemLabelGenerator(row, column);
        if (generator != null) {
            final Font labelFont = this.getItemLabelFont(row, column);
            final Paint paint = this.getItemLabelPaint(row, column);
            g2.setFont(labelFont);
            g2.setPaint(paint);
            final String label = generator.generateLabel(dataset, row, column);
            ItemLabelPosition position = null;
            if (!negative) {
                position = this.getPositiveItemLabelPosition(row, column);
            }
            else {
                position = this.getNegativeItemLabelPosition(row, column);
            }
            final Point2D anchorPoint = this.calculateLabelAnchorPoint(position.getItemLabelAnchor(), x, y, orientation);
            TextUtilities.drawRotatedString(label, g2, (float)anchorPoint.getX(), (float)anchorPoint.getY(), position.getTextAnchor(), position.getAngle(), position.getRotationAnchor());
        }
    }
    
    public Object clone() throws CloneNotSupportedException {
        final AbstractCategoryItemRenderer clone = (AbstractCategoryItemRenderer)super.clone();
        if (this.itemLabelGenerator != null) {
            if (!(this.itemLabelGenerator instanceof PublicCloneable)) {
                throw new CloneNotSupportedException("ItemLabelGenerator not cloneable.");
            }
            final PublicCloneable pc = (PublicCloneable)this.itemLabelGenerator;
            clone.itemLabelGenerator = (CategoryItemLabelGenerator)pc.clone();
        }
        if (this.itemLabelGeneratorList != null) {
            clone.itemLabelGeneratorList = (ObjectList)this.itemLabelGeneratorList.clone();
        }
        if (this.baseItemLabelGenerator != null) {
            if (!(this.baseItemLabelGenerator instanceof PublicCloneable)) {
                throw new CloneNotSupportedException("ItemLabelGenerator not cloneable.");
            }
            final PublicCloneable pc = (PublicCloneable)this.baseItemLabelGenerator;
            clone.baseItemLabelGenerator = (CategoryItemLabelGenerator)pc.clone();
        }
        if (this.toolTipGenerator != null) {
            if (!(this.toolTipGenerator instanceof PublicCloneable)) {
                throw new CloneNotSupportedException("Tool tip generator not cloneable.");
            }
            final PublicCloneable pc = (PublicCloneable)this.toolTipGenerator;
            clone.toolTipGenerator = (CategoryToolTipGenerator)pc.clone();
        }
        if (this.toolTipGeneratorList != null) {
            clone.toolTipGeneratorList = (ObjectList)this.toolTipGeneratorList.clone();
        }
        if (this.baseToolTipGenerator != null) {
            if (!(this.baseToolTipGenerator instanceof PublicCloneable)) {
                throw new CloneNotSupportedException("Base tool tip generator not cloneable.");
            }
            final PublicCloneable pc = (PublicCloneable)this.baseToolTipGenerator;
            clone.baseToolTipGenerator = (CategoryToolTipGenerator)pc.clone();
        }
        if (this.itemURLGenerator != null) {
            if (!(this.itemURLGenerator instanceof PublicCloneable)) {
                throw new CloneNotSupportedException("Item URL generator not cloneable.");
            }
            final PublicCloneable pc = (PublicCloneable)this.itemURLGenerator;
            clone.itemURLGenerator = (CategoryURLGenerator)pc.clone();
        }
        if (this.itemURLGeneratorList != null) {
            clone.itemURLGeneratorList = (ObjectList)this.itemURLGeneratorList.clone();
        }
        if (this.baseItemURLGenerator != null) {
            if (!(this.baseItemURLGenerator instanceof PublicCloneable)) {
                throw new CloneNotSupportedException("Base item URL generator not cloneable.");
            }
            final PublicCloneable pc = (PublicCloneable)this.baseItemURLGenerator;
            clone.baseItemURLGenerator = (CategoryURLGenerator)pc.clone();
        }
        return clone;
    }
    
    protected CategoryAxis getDomainAxis(final CategoryPlot plot, final int index) {
        CategoryAxis result = plot.getDomainAxis(index);
        if (result == null) {
            result = plot.getDomainAxis();
        }
        return result;
    }
    
    protected ValueAxis getRangeAxis(final CategoryPlot plot, final int index) {
        ValueAxis result = plot.getRangeAxis(index);
        if (result == null) {
            result = plot.getRangeAxis();
        }
        return result;
    }
    
    public LegendItemCollection getLegendItems() {
        if (this.plot == null) {
            return new LegendItemCollection();
        }
        final LegendItemCollection result = new LegendItemCollection();
        final int index = this.plot.getIndexOf(this);
        final CategoryDataset dataset = this.plot.getDataset(index);
        if (dataset != null) {
            for (int seriesCount = dataset.getRowCount(), i = 0; i < seriesCount; ++i) {
                if (this.isSeriesVisibleInLegend(i)) {
                    final LegendItem item = this.getLegendItem(index, i);
                    if (item != null) {
                        result.add(item);
                    }
                }
            }
        }
        return result;
    }
    
    public CategorySeriesLabelGenerator getLegendItemLabelGenerator() {
        return this.legendItemLabelGenerator;
    }
    
    public void setLegendItemLabelGenerator(final CategorySeriesLabelGenerator generator) {
        if (generator == null) {
            throw new IllegalArgumentException("Null 'generator' argument.");
        }
        this.legendItemLabelGenerator = generator;
    }
    
    public CategorySeriesLabelGenerator getLegendItemToolTipGenerator() {
        return this.legendItemToolTipGenerator;
    }
    
    public void setLegendItemToolTipGenerator(final CategorySeriesLabelGenerator generator) {
        this.legendItemToolTipGenerator = generator;
    }
    
    public CategorySeriesLabelGenerator getLegendItemURLGenerator() {
        return this.legendItemURLGenerator;
    }
    
    public void setLegendItemURLGenerator(final CategorySeriesLabelGenerator generator) {
        this.legendItemURLGenerator = generator;
    }
    
    protected void addItemEntity(final EntityCollection entities, final CategoryDataset dataset, final int row, final int column, final Shape hotspot) {
        String tip = null;
        final CategoryToolTipGenerator tipster = this.getToolTipGenerator(row, column);
        if (tipster != null) {
            tip = tipster.generateToolTip(dataset, row, column);
        }
        String url = null;
        final CategoryURLGenerator urlster = this.getItemURLGenerator(row, column);
        if (urlster != null) {
            url = urlster.generateURL(dataset, row, column);
        }
        final CategoryItemEntity entity = new CategoryItemEntity(hotspot, tip, url, dataset, row, dataset.getColumnKey(column), column);
        entities.add(entity);
    }
}
