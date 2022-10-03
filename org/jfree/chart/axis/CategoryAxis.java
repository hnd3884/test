package org.jfree.chart.axis;

import org.jfree.util.PaintUtilities;
import java.util.Set;
import org.jfree.io.SerialUtilities;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import org.jfree.util.ObjectUtilities;
import org.jfree.ui.Size2D;
import org.jfree.ui.RectangleInsets;
import org.jfree.util.ShapeUtilities;
import org.jfree.text.TextMeasurer;
import org.jfree.text.TextUtilities;
import org.jfree.text.G2TextMeasurer;
import org.jfree.chart.plot.CategoryPlot;
import java.util.ArrayList;
import org.jfree.chart.entity.EntityCollection;
import java.awt.Shape;
import org.jfree.text.TextBlock;
import java.awt.geom.Point2D;
import java.util.Iterator;
import java.util.List;
import org.jfree.chart.entity.ChartEntity;
import org.jfree.chart.entity.TickLabelEntity;
import org.jfree.ui.RectangleAnchor;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.Plot;
import java.awt.Graphics2D;
import org.jfree.ui.RectangleEdge;
import java.awt.geom.Rectangle2D;
import java.awt.Paint;
import java.awt.Font;
import org.jfree.chart.event.AxisChangeEvent;
import java.util.HashMap;
import java.util.Map;
import java.io.Serializable;

public class CategoryAxis extends Axis implements Cloneable, Serializable
{
    private static final long serialVersionUID = 5886554608114265863L;
    public static final double DEFAULT_AXIS_MARGIN = 0.05;
    public static final double DEFAULT_CATEGORY_MARGIN = 0.2;
    private double lowerMargin;
    private double upperMargin;
    private double categoryMargin;
    private int maximumCategoryLabelLines;
    private float maximumCategoryLabelWidthRatio;
    private int categoryLabelPositionOffset;
    private CategoryLabelPositions categoryLabelPositions;
    private Map tickLabelFontMap;
    private transient Map tickLabelPaintMap;
    private Map categoryLabelToolTips;
    
    public CategoryAxis() {
        this(null);
    }
    
    public CategoryAxis(final String label) {
        super(label);
        this.lowerMargin = 0.05;
        this.upperMargin = 0.05;
        this.categoryMargin = 0.2;
        this.maximumCategoryLabelLines = 1;
        this.maximumCategoryLabelWidthRatio = 0.0f;
        this.setTickMarksVisible(false);
        this.categoryLabelPositionOffset = 4;
        this.categoryLabelPositions = CategoryLabelPositions.STANDARD;
        this.tickLabelFontMap = new HashMap();
        this.tickLabelPaintMap = new HashMap();
        this.categoryLabelToolTips = new HashMap();
    }
    
    public double getLowerMargin() {
        return this.lowerMargin;
    }
    
    public void setLowerMargin(final double margin) {
        this.lowerMargin = margin;
        this.notifyListeners(new AxisChangeEvent(this));
    }
    
    public double getUpperMargin() {
        return this.upperMargin;
    }
    
    public void setUpperMargin(final double margin) {
        this.upperMargin = margin;
        this.notifyListeners(new AxisChangeEvent(this));
    }
    
    public double getCategoryMargin() {
        return this.categoryMargin;
    }
    
    public void setCategoryMargin(final double margin) {
        this.categoryMargin = margin;
        this.notifyListeners(new AxisChangeEvent(this));
    }
    
    public int getMaximumCategoryLabelLines() {
        return this.maximumCategoryLabelLines;
    }
    
    public void setMaximumCategoryLabelLines(final int lines) {
        this.maximumCategoryLabelLines = lines;
        this.notifyListeners(new AxisChangeEvent(this));
    }
    
    public float getMaximumCategoryLabelWidthRatio() {
        return this.maximumCategoryLabelWidthRatio;
    }
    
    public void setMaximumCategoryLabelWidthRatio(final float ratio) {
        this.maximumCategoryLabelWidthRatio = ratio;
        this.notifyListeners(new AxisChangeEvent(this));
    }
    
    public int getCategoryLabelPositionOffset() {
        return this.categoryLabelPositionOffset;
    }
    
    public void setCategoryLabelPositionOffset(final int offset) {
        this.categoryLabelPositionOffset = offset;
        this.notifyListeners(new AxisChangeEvent(this));
    }
    
    public CategoryLabelPositions getCategoryLabelPositions() {
        return this.categoryLabelPositions;
    }
    
    public void setCategoryLabelPositions(final CategoryLabelPositions positions) {
        if (positions == null) {
            throw new IllegalArgumentException("Null 'positions' argument.");
        }
        this.categoryLabelPositions = positions;
        this.notifyListeners(new AxisChangeEvent(this));
    }
    
    public Font getTickLabelFont(final Comparable category) {
        if (category == null) {
            throw new IllegalArgumentException("Null 'category' argument.");
        }
        Font result = this.tickLabelFontMap.get(category);
        if (result == null) {
            result = this.getTickLabelFont();
        }
        return result;
    }
    
    public void setTickLabelFont(final Comparable category, final Font font) {
        if (category == null) {
            throw new IllegalArgumentException("Null 'category' argument.");
        }
        if (font == null) {
            this.tickLabelFontMap.remove(category);
        }
        else {
            this.tickLabelFontMap.put(category, font);
        }
        this.notifyListeners(new AxisChangeEvent(this));
    }
    
    public Paint getTickLabelPaint(final Comparable category) {
        if (category == null) {
            throw new IllegalArgumentException("Null 'category' argument.");
        }
        Paint result = this.tickLabelPaintMap.get(category);
        if (result == null) {
            result = this.getTickLabelPaint();
        }
        return result;
    }
    
    public void setTickLabelPaint(final Comparable category, final Paint paint) {
        if (category == null) {
            throw new IllegalArgumentException("Null 'category' argument.");
        }
        if (paint == null) {
            this.tickLabelPaintMap.remove(category);
        }
        else {
            this.tickLabelPaintMap.put(category, paint);
        }
        this.notifyListeners(new AxisChangeEvent(this));
    }
    
    public void addCategoryLabelToolTip(final Comparable category, final String tooltip) {
        if (category == null) {
            throw new IllegalArgumentException("Null 'category' argument.");
        }
        this.categoryLabelToolTips.put(category, tooltip);
        this.notifyListeners(new AxisChangeEvent(this));
    }
    
    public String getCategoryLabelToolTip(final Comparable category) {
        if (category == null) {
            throw new IllegalArgumentException("Null 'category' argument.");
        }
        return this.categoryLabelToolTips.get(category);
    }
    
    public void removeCategoryLabelToolTip(final Comparable category) {
        if (category == null) {
            throw new IllegalArgumentException("Null 'category' argument.");
        }
        this.categoryLabelToolTips.remove(category);
        this.notifyListeners(new AxisChangeEvent(this));
    }
    
    public void clearCategoryLabelToolTips() {
        this.categoryLabelToolTips.clear();
        this.notifyListeners(new AxisChangeEvent(this));
    }
    
    public double getCategoryJava2DCoordinate(final CategoryAnchor anchor, final int category, final int categoryCount, final Rectangle2D area, final RectangleEdge edge) {
        double result = 0.0;
        if (anchor == CategoryAnchor.START) {
            result = this.getCategoryStart(category, categoryCount, area, edge);
        }
        else if (anchor == CategoryAnchor.MIDDLE) {
            result = this.getCategoryMiddle(category, categoryCount, area, edge);
        }
        else if (anchor == CategoryAnchor.END) {
            result = this.getCategoryEnd(category, categoryCount, area, edge);
        }
        return result;
    }
    
    public double getCategoryStart(final int category, final int categoryCount, final Rectangle2D area, final RectangleEdge edge) {
        double result = 0.0;
        if (edge == RectangleEdge.TOP || edge == RectangleEdge.BOTTOM) {
            result = area.getX() + area.getWidth() * this.getLowerMargin();
        }
        else if (edge == RectangleEdge.LEFT || edge == RectangleEdge.RIGHT) {
            result = area.getMinY() + area.getHeight() * this.getLowerMargin();
        }
        final double categorySize = this.calculateCategorySize(categoryCount, area, edge);
        final double categoryGapWidth = this.calculateCategoryGapSize(categoryCount, area, edge);
        result += category * (categorySize + categoryGapWidth);
        return result;
    }
    
    public double getCategoryMiddle(final int category, final int categoryCount, final Rectangle2D area, final RectangleEdge edge) {
        return this.getCategoryStart(category, categoryCount, area, edge) + this.calculateCategorySize(categoryCount, area, edge) / 2.0;
    }
    
    public double getCategoryEnd(final int category, final int categoryCount, final Rectangle2D area, final RectangleEdge edge) {
        return this.getCategoryStart(category, categoryCount, area, edge) + this.calculateCategorySize(categoryCount, area, edge);
    }
    
    protected double calculateCategorySize(final int categoryCount, final Rectangle2D area, final RectangleEdge edge) {
        double result = 0.0;
        double available = 0.0;
        if (edge == RectangleEdge.TOP || edge == RectangleEdge.BOTTOM) {
            available = area.getWidth();
        }
        else if (edge == RectangleEdge.LEFT || edge == RectangleEdge.RIGHT) {
            available = area.getHeight();
        }
        if (categoryCount > 1) {
            result = available * (1.0 - this.getLowerMargin() - this.getUpperMargin() - this.getCategoryMargin());
            result /= categoryCount;
        }
        else {
            result = available * (1.0 - this.getLowerMargin() - this.getUpperMargin());
        }
        return result;
    }
    
    protected double calculateCategoryGapSize(final int categoryCount, final Rectangle2D area, final RectangleEdge edge) {
        double result = 0.0;
        double available = 0.0;
        if (edge == RectangleEdge.TOP || edge == RectangleEdge.BOTTOM) {
            available = area.getWidth();
        }
        else if (edge == RectangleEdge.LEFT || edge == RectangleEdge.RIGHT) {
            available = area.getHeight();
        }
        if (categoryCount > 1) {
            result = available * this.getCategoryMargin() / (categoryCount - 1);
        }
        return result;
    }
    
    public AxisSpace reserveSpace(final Graphics2D g2, final Plot plot, final Rectangle2D plotArea, final RectangleEdge edge, AxisSpace space) {
        if (space == null) {
            space = new AxisSpace();
        }
        if (!this.isVisible()) {
            return space;
        }
        double tickLabelHeight = 0.0;
        double tickLabelWidth = 0.0;
        if (this.isTickLabelsVisible()) {
            g2.setFont(this.getTickLabelFont());
            final AxisState state = new AxisState();
            this.refreshTicks(g2, state, plotArea, edge);
            if (edge == RectangleEdge.TOP) {
                tickLabelHeight = state.getMax();
            }
            else if (edge == RectangleEdge.BOTTOM) {
                tickLabelHeight = state.getMax();
            }
            else if (edge == RectangleEdge.LEFT) {
                tickLabelWidth = state.getMax();
            }
            else if (edge == RectangleEdge.RIGHT) {
                tickLabelWidth = state.getMax();
            }
        }
        final Rectangle2D labelEnclosure = this.getLabelEnclosure(g2, edge);
        double labelHeight = 0.0;
        double labelWidth = 0.0;
        if (RectangleEdge.isTopOrBottom(edge)) {
            labelHeight = labelEnclosure.getHeight();
            space.add(labelHeight + tickLabelHeight + this.categoryLabelPositionOffset, edge);
        }
        else if (RectangleEdge.isLeftOrRight(edge)) {
            labelWidth = labelEnclosure.getWidth();
            space.add(labelWidth + tickLabelWidth + this.categoryLabelPositionOffset, edge);
        }
        return space;
    }
    
    public void configure() {
    }
    
    public AxisState draw(final Graphics2D g2, final double cursor, final Rectangle2D plotArea, final Rectangle2D dataArea, final RectangleEdge edge, final PlotRenderingInfo plotState) {
        if (!this.isVisible()) {
            return new AxisState(cursor);
        }
        if (this.isAxisLineVisible()) {
            this.drawAxisLine(g2, cursor, dataArea, edge);
        }
        AxisState state = new AxisState(cursor);
        state = this.drawCategoryLabels(g2, dataArea, edge, state, plotState);
        state = this.drawLabel(this.getLabel(), g2, plotArea, dataArea, edge, state);
        return state;
    }
    
    protected AxisState drawCategoryLabels(final Graphics2D g2, final Rectangle2D dataArea, final RectangleEdge edge, final AxisState state, final PlotRenderingInfo plotState) {
        return this.drawCategoryLabels(g2, dataArea, dataArea, edge, state, plotState);
    }
    
    protected AxisState drawCategoryLabels(final Graphics2D g2, final Rectangle2D plotArea, final Rectangle2D dataArea, final RectangleEdge edge, final AxisState state, final PlotRenderingInfo plotState) {
        if (state == null) {
            throw new IllegalArgumentException("Null 'state' argument.");
        }
        if (this.isTickLabelsVisible()) {
            final List ticks = this.refreshTicks(g2, state, plotArea, edge);
            state.setTicks(ticks);
            int categoryIndex = 0;
            final Iterator iterator = ticks.iterator();
            while (iterator.hasNext()) {
                final CategoryTick tick = iterator.next();
                g2.setFont(this.getTickLabelFont(tick.getCategory()));
                g2.setPaint(this.getTickLabelPaint(tick.getCategory()));
                final CategoryLabelPosition position = this.categoryLabelPositions.getLabelPosition(edge);
                double x0 = 0.0;
                double x2 = 0.0;
                double y0 = 0.0;
                double y2 = 0.0;
                if (edge == RectangleEdge.TOP) {
                    x0 = this.getCategoryStart(categoryIndex, ticks.size(), dataArea, edge);
                    x2 = this.getCategoryEnd(categoryIndex, ticks.size(), dataArea, edge);
                    y2 = state.getCursor() - this.categoryLabelPositionOffset;
                    y0 = y2 - state.getMax();
                }
                else if (edge == RectangleEdge.BOTTOM) {
                    x0 = this.getCategoryStart(categoryIndex, ticks.size(), dataArea, edge);
                    x2 = this.getCategoryEnd(categoryIndex, ticks.size(), dataArea, edge);
                    y0 = state.getCursor() + this.categoryLabelPositionOffset;
                    y2 = y0 + state.getMax();
                }
                else if (edge == RectangleEdge.LEFT) {
                    y0 = this.getCategoryStart(categoryIndex, ticks.size(), dataArea, edge);
                    y2 = this.getCategoryEnd(categoryIndex, ticks.size(), dataArea, edge);
                    x2 = state.getCursor() - this.categoryLabelPositionOffset;
                    x0 = x2 - state.getMax();
                }
                else if (edge == RectangleEdge.RIGHT) {
                    y0 = this.getCategoryStart(categoryIndex, ticks.size(), dataArea, edge);
                    y2 = this.getCategoryEnd(categoryIndex, ticks.size(), dataArea, edge);
                    x0 = state.getCursor() + this.categoryLabelPositionOffset;
                    x2 = x0 - state.getMax();
                }
                final Rectangle2D area = new Rectangle2D.Double(x0, y0, x2 - x0, y2 - y0);
                final Point2D anchorPoint = RectangleAnchor.coordinates(area, position.getCategoryAnchor());
                final TextBlock block = tick.getLabel();
                block.draw(g2, (float)anchorPoint.getX(), (float)anchorPoint.getY(), position.getLabelAnchor(), (float)anchorPoint.getX(), (float)anchorPoint.getY(), position.getAngle());
                final Shape bounds = block.calculateBounds(g2, (float)anchorPoint.getX(), (float)anchorPoint.getY(), position.getLabelAnchor(), (float)anchorPoint.getX(), (float)anchorPoint.getY(), position.getAngle());
                if (plotState != null && plotState.getOwner() != null) {
                    final EntityCollection entities = plotState.getOwner().getEntityCollection();
                    if (entities != null) {
                        final String tooltip = this.getCategoryLabelToolTip(tick.getCategory());
                        entities.add(new TickLabelEntity(bounds, tooltip, null));
                    }
                }
                ++categoryIndex;
            }
            if (edge.equals((Object)RectangleEdge.TOP)) {
                final double h = state.getMax();
                state.cursorUp(h);
            }
            else if (edge.equals((Object)RectangleEdge.BOTTOM)) {
                final double h = state.getMax();
                state.cursorDown(h);
            }
            else if (edge == RectangleEdge.LEFT) {
                final double w = state.getMax();
                state.cursorLeft(w);
            }
            else if (edge == RectangleEdge.RIGHT) {
                final double w = state.getMax();
                state.cursorRight(w);
            }
        }
        return state;
    }
    
    public List refreshTicks(final Graphics2D g2, final AxisState state, final Rectangle2D dataArea, final RectangleEdge edge) {
        final List ticks = new ArrayList();
        if (dataArea.getHeight() <= 0.0 || dataArea.getWidth() < 0.0) {
            return ticks;
        }
        final CategoryPlot plot = (CategoryPlot)this.getPlot();
        final List categories = plot.getCategories();
        double max = 0.0;
        if (categories != null) {
            final CategoryLabelPosition position = this.categoryLabelPositions.getLabelPosition(edge);
            float r = this.maximumCategoryLabelWidthRatio;
            if (r <= 0.0) {
                r = position.getWidthRatio();
            }
            float l = 0.0f;
            if (position.getWidthType() == CategoryLabelWidthType.CATEGORY) {
                l = (float)this.calculateCategorySize(categories.size(), dataArea, edge);
            }
            else if (RectangleEdge.isLeftOrRight(edge)) {
                l = (float)dataArea.getWidth();
            }
            else {
                l = (float)dataArea.getHeight();
            }
            int categoryIndex = 0;
            final Iterator iterator = categories.iterator();
            while (iterator.hasNext()) {
                final Comparable category = iterator.next();
                final TextBlock label = this.createLabel(category, l * r, edge, g2);
                if (edge == RectangleEdge.TOP || edge == RectangleEdge.BOTTOM) {
                    max = Math.max(max, this.calculateTextBlockHeight(label, position, g2));
                }
                else if (edge == RectangleEdge.LEFT || edge == RectangleEdge.RIGHT) {
                    max = Math.max(max, this.calculateTextBlockWidth(label, position, g2));
                }
                final Tick tick = new CategoryTick(category, label, position.getLabelAnchor(), position.getRotationAnchor(), position.getAngle());
                ticks.add(tick);
                ++categoryIndex;
            }
        }
        state.setMax(max);
        return ticks;
    }
    
    protected TextBlock createLabel(final Comparable category, final float width, final RectangleEdge edge, final Graphics2D g2) {
        final TextBlock label = TextUtilities.createTextBlock(category.toString(), this.getTickLabelFont(category), this.getTickLabelPaint(category), width, this.maximumCategoryLabelLines, (TextMeasurer)new G2TextMeasurer(g2));
        return label;
    }
    
    protected double calculateTextBlockWidth(final TextBlock block, final CategoryLabelPosition position, final Graphics2D g2) {
        final RectangleInsets insets = this.getTickLabelInsets();
        final Size2D size = block.calculateDimensions(g2);
        final Rectangle2D box = new Rectangle2D.Double(0.0, 0.0, size.getWidth(), size.getHeight());
        final Shape rotatedBox = ShapeUtilities.rotateShape((Shape)box, position.getAngle(), 0.0f, 0.0f);
        final double w = rotatedBox.getBounds2D().getWidth() + insets.getTop() + insets.getBottom();
        return w;
    }
    
    protected double calculateTextBlockHeight(final TextBlock block, final CategoryLabelPosition position, final Graphics2D g2) {
        final RectangleInsets insets = this.getTickLabelInsets();
        final Size2D size = block.calculateDimensions(g2);
        final Rectangle2D box = new Rectangle2D.Double(0.0, 0.0, size.getWidth(), size.getHeight());
        final Shape rotatedBox = ShapeUtilities.rotateShape((Shape)box, position.getAngle(), 0.0f, 0.0f);
        final double h = rotatedBox.getBounds2D().getHeight() + insets.getTop() + insets.getBottom();
        return h;
    }
    
    public Object clone() throws CloneNotSupportedException {
        final CategoryAxis clone = (CategoryAxis)super.clone();
        clone.tickLabelFontMap = new HashMap(this.tickLabelFontMap);
        clone.tickLabelPaintMap = new HashMap(this.tickLabelPaintMap);
        clone.categoryLabelToolTips = new HashMap(this.categoryLabelToolTips);
        return clone;
    }
    
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof CategoryAxis)) {
            return false;
        }
        if (!super.equals(obj)) {
            return false;
        }
        final CategoryAxis that = (CategoryAxis)obj;
        return that.lowerMargin == this.lowerMargin && that.upperMargin == this.upperMargin && that.categoryMargin == this.categoryMargin && that.maximumCategoryLabelWidthRatio == this.maximumCategoryLabelWidthRatio && that.categoryLabelPositionOffset == this.categoryLabelPositionOffset && ObjectUtilities.equal((Object)that.categoryLabelPositions, (Object)this.categoryLabelPositions) && ObjectUtilities.equal((Object)that.categoryLabelToolTips, (Object)this.categoryLabelToolTips) && ObjectUtilities.equal((Object)this.tickLabelFontMap, (Object)that.tickLabelFontMap) && this.equalPaintMaps(this.tickLabelPaintMap, that.tickLabelPaintMap);
    }
    
    public int hashCode() {
        if (this.getLabel() != null) {
            return this.getLabel().hashCode();
        }
        return 0;
    }
    
    private void writeObject(final ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        this.writePaintMap(this.tickLabelPaintMap, stream);
    }
    
    private void readObject(final ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        this.tickLabelPaintMap = this.readPaintMap(stream);
    }
    
    private Map readPaintMap(final ObjectInputStream in) throws IOException, ClassNotFoundException {
        final boolean isNull = in.readBoolean();
        if (isNull) {
            return null;
        }
        final Map result = new HashMap();
        for (int count = in.readInt(), i = 0; i < count; ++i) {
            final Comparable category = (Comparable)in.readObject();
            final Paint paint = SerialUtilities.readPaint(in);
            result.put(category, paint);
        }
        return result;
    }
    
    private void writePaintMap(final Map map, final ObjectOutputStream out) throws IOException {
        if (map == null) {
            out.writeBoolean(true);
        }
        else {
            out.writeBoolean(false);
            final Set keys = map.keySet();
            final int count = keys.size();
            out.writeInt(count);
            final Iterator iterator = keys.iterator();
            while (iterator.hasNext()) {
                final Comparable key = iterator.next();
                out.writeObject(key);
                SerialUtilities.writePaint((Paint)map.get(key), out);
            }
        }
    }
    
    private boolean equalPaintMaps(final Map map1, final Map map2) {
        if (map1.size() != map2.size()) {
            return false;
        }
        final Set keys = map1.keySet();
        final Iterator iterator = keys.iterator();
        while (iterator.hasNext()) {
            final Comparable key = iterator.next();
            final Paint p1 = map1.get(key);
            final Paint p2 = map2.get(key);
            if (!PaintUtilities.equal(p1, p2)) {
                return false;
            }
        }
        return true;
    }
}
