package org.jfree.chart.title;

import java.awt.Color;
import java.io.ObjectInputStream;
import java.io.IOException;
import org.jfree.io.SerialUtilities;
import java.io.ObjectOutputStream;
import org.jfree.util.PaintUtilities;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import org.jfree.ui.Size2D;
import org.jfree.chart.block.RectangleConstraint;
import java.awt.Graphics2D;
import org.jfree.chart.block.CenterArrangement;
import org.jfree.chart.block.LabelBlock;
import org.jfree.chart.block.BorderArrangement;
import org.jfree.chart.block.Block;
import org.jfree.chart.LegendItem;
import org.jfree.chart.LegendItemCollection;
import org.jfree.chart.event.TitleChangeEvent;
import org.jfree.chart.block.ColumnArrangement;
import org.jfree.chart.block.FlowArrangement;
import org.jfree.chart.block.Arrangement;
import org.jfree.chart.block.BlockContainer;
import org.jfree.ui.RectangleInsets;
import org.jfree.ui.RectangleAnchor;
import org.jfree.ui.RectangleEdge;
import org.jfree.chart.LegendItemSource;
import java.awt.Paint;
import java.awt.Font;
import java.io.Serializable;
import org.jfree.util.PublicCloneable;

public class LegendTitle extends Title implements Cloneable, PublicCloneable, Serializable
{
    private static final long serialVersionUID = 2644010518533854633L;
    public static final Font DEFAULT_ITEM_FONT;
    public static final Paint DEFAULT_ITEM_PAINT;
    private LegendItemSource[] sources;
    private transient Paint backgroundPaint;
    private RectangleEdge legendItemGraphicEdge;
    private RectangleAnchor legendItemGraphicAnchor;
    private RectangleAnchor legendItemGraphicLocation;
    private RectangleInsets legendItemGraphicPadding;
    private Font itemFont;
    private transient Paint itemPaint;
    private RectangleInsets itemLabelPadding;
    private BlockContainer items;
    private Arrangement hLayout;
    private Arrangement vLayout;
    private BlockContainer wrapper;
    
    public LegendTitle(final LegendItemSource source) {
        this(source, new FlowArrangement(), new ColumnArrangement());
    }
    
    public LegendTitle(final LegendItemSource source, final Arrangement hLayout, final Arrangement vLayout) {
        this.sources = new LegendItemSource[] { source };
        this.items = new BlockContainer(hLayout);
        this.hLayout = hLayout;
        this.vLayout = vLayout;
        this.backgroundPaint = null;
        this.legendItemGraphicEdge = RectangleEdge.LEFT;
        this.legendItemGraphicAnchor = RectangleAnchor.CENTER;
        this.legendItemGraphicLocation = RectangleAnchor.CENTER;
        this.legendItemGraphicPadding = new RectangleInsets(2.0, 2.0, 2.0, 2.0);
        this.itemFont = LegendTitle.DEFAULT_ITEM_FONT;
        this.itemPaint = LegendTitle.DEFAULT_ITEM_PAINT;
        this.itemLabelPadding = new RectangleInsets(2.0, 2.0, 2.0, 2.0);
    }
    
    public LegendItemSource[] getSources() {
        return this.sources;
    }
    
    public void setSources(final LegendItemSource[] sources) {
        if (sources == null) {
            throw new IllegalArgumentException("Null 'sources' argument.");
        }
        this.sources = sources;
        this.notifyListeners(new TitleChangeEvent(this));
    }
    
    public Paint getBackgroundPaint() {
        return this.backgroundPaint;
    }
    
    public void setBackgroundPaint(final Paint paint) {
        this.backgroundPaint = paint;
        this.notifyListeners(new TitleChangeEvent(this));
    }
    
    public RectangleEdge getLegendItemGraphicEdge() {
        return this.legendItemGraphicEdge;
    }
    
    public void setLegendItemGraphicEdge(final RectangleEdge edge) {
        if (edge == null) {
            throw new IllegalArgumentException("Null 'edge' argument.");
        }
        this.legendItemGraphicEdge = edge;
        this.notifyListeners(new TitleChangeEvent(this));
    }
    
    public RectangleAnchor getLegendItemGraphicAnchor() {
        return this.legendItemGraphicAnchor;
    }
    
    public void setLegendItemGraphicAnchor(final RectangleAnchor anchor) {
        if (anchor == null) {
            throw new IllegalArgumentException("Null 'anchor' point.");
        }
        this.legendItemGraphicAnchor = anchor;
    }
    
    public RectangleAnchor getLegendItemGraphicLocation() {
        return this.legendItemGraphicLocation;
    }
    
    public void setLegendItemGraphicLocation(final RectangleAnchor anchor) {
        this.legendItemGraphicLocation = anchor;
    }
    
    public RectangleInsets getLegendItemGraphicPadding() {
        return this.legendItemGraphicPadding;
    }
    
    public void setLegendItemGraphicPadding(final RectangleInsets padding) {
        if (padding == null) {
            throw new IllegalArgumentException("Null 'padding' argument.");
        }
        this.legendItemGraphicPadding = padding;
        this.notifyListeners(new TitleChangeEvent(this));
    }
    
    public Font getItemFont() {
        return this.itemFont;
    }
    
    public void setItemFont(final Font font) {
        if (font == null) {
            throw new IllegalArgumentException("Null 'font' argument.");
        }
        this.itemFont = font;
        this.notifyListeners(new TitleChangeEvent(this));
    }
    
    public Paint getItemPaint() {
        return this.itemPaint;
    }
    
    public void setItemPaint(final Paint paint) {
        if (paint == null) {
            throw new IllegalArgumentException("Null 'paint' argument.");
        }
        this.itemPaint = paint;
        this.notifyListeners(new TitleChangeEvent(this));
    }
    
    public RectangleInsets getItemLabelPadding() {
        return this.itemLabelPadding;
    }
    
    public void setItemLabelPadding(final RectangleInsets padding) {
        if (padding == null) {
            throw new IllegalArgumentException("Null 'padding' argument.");
        }
        this.itemLabelPadding = padding;
        this.notifyListeners(new TitleChangeEvent(this));
    }
    
    protected void fetchLegendItems() {
        this.items.clear();
        final RectangleEdge p = this.getPosition();
        if (RectangleEdge.isTopOrBottom(p)) {
            this.items.setArrangement(this.hLayout);
        }
        else {
            this.items.setArrangement(this.vLayout);
        }
        for (int s = 0; s < this.sources.length; ++s) {
            final LegendItemCollection legendItems = this.sources[s].getLegendItems();
            if (legendItems != null) {
                for (int i = 0; i < legendItems.getItemCount(); ++i) {
                    final LegendItem item = legendItems.get(i);
                    final Block block = this.createLegendItemBlock(item);
                    this.items.add(block);
                }
            }
        }
    }
    
    protected Block createLegendItemBlock(final LegendItem item) {
        BlockContainer result = null;
        final LegendGraphic lg = new LegendGraphic(item.getShape(), item.getFillPaint());
        lg.setShapeFilled(item.isShapeFilled());
        lg.setLine(item.getLine());
        lg.setLineStroke(item.getLineStroke());
        lg.setLinePaint(item.getLinePaint());
        lg.setLineVisible(item.isLineVisible());
        lg.setShapeVisible(item.isShapeVisible());
        lg.setShapeOutlineVisible(item.isShapeOutlineVisible());
        lg.setOutlinePaint(item.getOutlinePaint());
        lg.setOutlineStroke(item.getOutlineStroke());
        lg.setPadding(this.legendItemGraphicPadding);
        final LegendItemBlockContainer legendItem = new LegendItemBlockContainer(new BorderArrangement(), item.getDatasetIndex(), item.getSeriesIndex());
        lg.setShapeAnchor(this.getLegendItemGraphicAnchor());
        lg.setShapeLocation(this.getLegendItemGraphicLocation());
        legendItem.add(lg, this.legendItemGraphicEdge);
        final LabelBlock labelBlock = new LabelBlock(item.getLabel(), this.itemFont, this.itemPaint);
        labelBlock.setPadding(this.itemLabelPadding);
        labelBlock.setToolTipText(item.getToolTipText());
        legendItem.add(labelBlock);
        result = new BlockContainer(new CenterArrangement());
        result.add(legendItem);
        return result;
    }
    
    public BlockContainer getItemContainer() {
        return this.items;
    }
    
    public Size2D arrange(final Graphics2D g2, final RectangleConstraint constraint) {
        final Size2D result = new Size2D();
        this.fetchLegendItems();
        if (this.items.isEmpty()) {
            return result;
        }
        BlockContainer container = this.wrapper;
        if (container == null) {
            container = this.items;
        }
        final RectangleConstraint c = this.toContentConstraint(constraint);
        final Size2D size = container.arrange(g2, c);
        result.height = this.calculateTotalHeight(size.height);
        result.width = this.calculateTotalWidth(size.width);
        return result;
    }
    
    public void draw(final Graphics2D g2, final Rectangle2D area) {
        this.draw(g2, area, null);
    }
    
    public Object draw(final Graphics2D g2, final Rectangle2D area, final Object params) {
        Rectangle2D target = (Rectangle2D)area.clone();
        target = this.trimMargin(target);
        if (this.backgroundPaint != null) {
            g2.setPaint(this.backgroundPaint);
            g2.fill(target);
        }
        this.getBorder().draw(g2, target);
        this.getBorder().getInsets().trim(target);
        BlockContainer container = this.wrapper;
        if (container == null) {
            container = this.items;
        }
        target = this.trimPadding(target);
        return container.draw(g2, target, params);
    }
    
    public void setWrapper(final BlockContainer wrapper) {
        this.wrapper = wrapper;
    }
    
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof LegendTitle)) {
            return false;
        }
        if (!super.equals(obj)) {
            return false;
        }
        final LegendTitle that = (LegendTitle)obj;
        return PaintUtilities.equal(this.backgroundPaint, that.backgroundPaint) && this.legendItemGraphicEdge == that.legendItemGraphicEdge && this.legendItemGraphicAnchor == that.legendItemGraphicAnchor && this.legendItemGraphicLocation == that.legendItemGraphicLocation && this.itemFont.equals(that.itemFont) && this.itemPaint.equals(that.itemPaint) && this.hLayout.equals(that.hLayout) && this.vLayout.equals(that.vLayout);
    }
    
    private void writeObject(final ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        SerialUtilities.writePaint(this.backgroundPaint, stream);
        SerialUtilities.writePaint(this.itemPaint, stream);
    }
    
    private void readObject(final ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        this.backgroundPaint = SerialUtilities.readPaint(stream);
        this.itemPaint = SerialUtilities.readPaint(stream);
    }
    
    static {
        DEFAULT_ITEM_FONT = new Font("SansSerif", 0, 12);
        DEFAULT_ITEM_PAINT = Color.black;
    }
}
