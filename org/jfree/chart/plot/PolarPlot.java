package org.jfree.chart.plot;

import java.awt.BasicStroke;
import java.awt.Point;
import java.io.ObjectInputStream;
import java.io.IOException;
import org.jfree.io.SerialUtilities;
import java.io.ObjectOutputStream;
import org.jfree.util.PaintUtilities;
import org.jfree.util.ObjectUtilities;
import org.jfree.chart.LegendItem;
import org.jfree.chart.LegendItemCollection;
import org.jfree.chart.event.RendererChangeEvent;
import org.jfree.data.Range;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.ui.RectangleEdge;
import java.awt.FontMetrics;
import java.util.Iterator;
import org.jfree.text.TextUtilities;
import org.jfree.chart.axis.AxisState;
import org.jfree.ui.RectangleInsets;
import java.awt.Composite;
import java.awt.AlphaComposite;
import java.awt.Shape;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.Graphics2D;
import org.jfree.data.general.Dataset;
import org.jfree.data.general.DatasetChangeEvent;
import org.jfree.chart.event.PlotChangeEvent;
import org.jfree.chart.event.AxisChangeListener;
import org.jfree.chart.axis.NumberTick;
import org.jfree.ui.TextAnchor;
import org.jfree.data.general.DatasetChangeListener;
import java.util.ArrayList;
import java.awt.Color;
import java.awt.Font;
import org.jfree.chart.renderer.PolarItemRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.chart.axis.ValueAxis;
import java.util.List;
import java.util.ResourceBundle;
import java.awt.Paint;
import java.awt.Stroke;
import java.io.Serializable;
import org.jfree.chart.event.RendererChangeListener;

public class PolarPlot extends Plot implements ValueAxisPlot, Zoomable, RendererChangeListener, Cloneable, Serializable
{
    private static final long serialVersionUID = 3794383185924179525L;
    private static final int MARGIN = 20;
    private static final double ANNOTATION_MARGIN = 7.0;
    public static final Stroke DEFAULT_GRIDLINE_STROKE;
    public static final Paint DEFAULT_GRIDLINE_PAINT;
    protected static ResourceBundle localizationResources;
    private List angleTicks;
    private ValueAxis axis;
    private XYDataset dataset;
    private PolarItemRenderer renderer;
    private boolean angleLabelsVisible;
    private Font angleLabelFont;
    private Paint angleLabelPaint;
    private boolean angleGridlinesVisible;
    private transient Stroke angleGridlineStroke;
    private transient Paint angleGridlinePaint;
    private boolean radiusGridlinesVisible;
    private transient Stroke radiusGridlineStroke;
    private transient Paint radiusGridlinePaint;
    private List cornerTextItems;
    
    public PolarPlot() {
        this(null, null, null);
    }
    
    public PolarPlot(final XYDataset dataset, final ValueAxis radiusAxis, final PolarItemRenderer renderer) {
        this.angleLabelsVisible = true;
        this.angleLabelFont = new Font("SansSerif", 0, 12);
        this.angleLabelPaint = Color.black;
        this.cornerTextItems = new ArrayList();
        this.dataset = dataset;
        if (this.dataset != null) {
            this.dataset.addChangeListener(this);
        }
        (this.angleTicks = new ArrayList()).add(new NumberTick(new Double(0.0), "0", TextAnchor.CENTER, TextAnchor.CENTER, 0.0));
        this.angleTicks.add(new NumberTick(new Double(45.0), "45", TextAnchor.CENTER, TextAnchor.CENTER, 0.0));
        this.angleTicks.add(new NumberTick(new Double(90.0), "90", TextAnchor.CENTER, TextAnchor.CENTER, 0.0));
        this.angleTicks.add(new NumberTick(new Double(135.0), "135", TextAnchor.CENTER, TextAnchor.CENTER, 0.0));
        this.angleTicks.add(new NumberTick(new Double(180.0), "180", TextAnchor.CENTER, TextAnchor.CENTER, 0.0));
        this.angleTicks.add(new NumberTick(new Double(225.0), "225", TextAnchor.CENTER, TextAnchor.CENTER, 0.0));
        this.angleTicks.add(new NumberTick(new Double(270.0), "270", TextAnchor.CENTER, TextAnchor.CENTER, 0.0));
        this.angleTicks.add(new NumberTick(new Double(315.0), "315", TextAnchor.CENTER, TextAnchor.CENTER, 0.0));
        this.axis = radiusAxis;
        if (this.axis != null) {
            this.axis.setPlot(this);
            this.axis.addChangeListener(this);
        }
        this.renderer = renderer;
        if (this.renderer != null) {
            this.renderer.setPlot(this);
            this.renderer.addChangeListener(this);
        }
        this.angleGridlinesVisible = true;
        this.angleGridlineStroke = PolarPlot.DEFAULT_GRIDLINE_STROKE;
        this.angleGridlinePaint = PolarPlot.DEFAULT_GRIDLINE_PAINT;
        this.radiusGridlinesVisible = true;
        this.radiusGridlineStroke = PolarPlot.DEFAULT_GRIDLINE_STROKE;
        this.radiusGridlinePaint = PolarPlot.DEFAULT_GRIDLINE_PAINT;
    }
    
    public void addCornerTextItem(final String text) {
        if (text == null) {
            throw new IllegalArgumentException("Null 'text' argument.");
        }
        this.cornerTextItems.add(text);
        this.notifyListeners(new PlotChangeEvent(this));
    }
    
    public void removeCornerTextItem(final String text) {
        final boolean removed = this.cornerTextItems.remove(text);
        if (removed) {
            this.notifyListeners(new PlotChangeEvent(this));
        }
    }
    
    public void clearCornerTextItems() {
        if (this.cornerTextItems.size() > 0) {
            this.cornerTextItems.clear();
            this.notifyListeners(new PlotChangeEvent(this));
        }
    }
    
    public String getPlotType() {
        return PolarPlot.localizationResources.getString("Polar_Plot");
    }
    
    public ValueAxis getAxis() {
        return this.axis;
    }
    
    public void setAxis(final ValueAxis axis) {
        if (axis != null) {
            axis.setPlot(this);
        }
        if (this.axis != null) {
            this.axis.removeChangeListener(this);
        }
        this.axis = axis;
        if (this.axis != null) {
            this.axis.configure();
            this.axis.addChangeListener(this);
        }
        this.notifyListeners(new PlotChangeEvent(this));
    }
    
    public XYDataset getDataset() {
        return this.dataset;
    }
    
    public void setDataset(final XYDataset dataset) {
        final XYDataset existing = this.dataset;
        if (existing != null) {
            existing.removeChangeListener(this);
        }
        this.dataset = dataset;
        if (this.dataset != null) {
            this.setDatasetGroup(this.dataset.getGroup());
            this.dataset.addChangeListener(this);
        }
        final DatasetChangeEvent event = new DatasetChangeEvent(this, this.dataset);
        this.datasetChanged(event);
    }
    
    public PolarItemRenderer getRenderer() {
        return this.renderer;
    }
    
    public void setRenderer(final PolarItemRenderer renderer) {
        if (this.renderer != null) {
            this.renderer.removeChangeListener(this);
        }
        this.renderer = renderer;
        if (this.renderer != null) {
            this.renderer.setPlot(this);
        }
        this.notifyListeners(new PlotChangeEvent(this));
    }
    
    public boolean isAngleLabelsVisible() {
        return this.angleLabelsVisible;
    }
    
    public void setAngleLabelsVisible(final boolean visible) {
        if (this.angleLabelsVisible != visible) {
            this.angleLabelsVisible = visible;
            this.notifyListeners(new PlotChangeEvent(this));
        }
    }
    
    public Font getAngleLabelFont() {
        return this.angleLabelFont;
    }
    
    public void setAngleLabelFont(final Font font) {
        if (font == null) {
            throw new IllegalArgumentException("Null 'font' argument.");
        }
        this.angleLabelFont = font;
        this.notifyListeners(new PlotChangeEvent(this));
    }
    
    public Paint getAngleLabelPaint() {
        return this.angleLabelPaint;
    }
    
    public void setAngleLabelPaint(final Paint paint) {
        this.angleLabelPaint = paint;
        this.notifyListeners(new PlotChangeEvent(this));
    }
    
    public boolean isAngleGridlinesVisible() {
        return this.angleGridlinesVisible;
    }
    
    public void setAngleGridlinesVisible(final boolean visible) {
        if (this.angleGridlinesVisible != visible) {
            this.angleGridlinesVisible = visible;
            this.notifyListeners(new PlotChangeEvent(this));
        }
    }
    
    public Stroke getAngleGridlineStroke() {
        return this.angleGridlineStroke;
    }
    
    public void setAngleGridlineStroke(final Stroke stroke) {
        this.angleGridlineStroke = stroke;
        this.notifyListeners(new PlotChangeEvent(this));
    }
    
    public Paint getAngleGridlinePaint() {
        return this.angleGridlinePaint;
    }
    
    public void setAngleGridlinePaint(final Paint paint) {
        this.angleGridlinePaint = paint;
        this.notifyListeners(new PlotChangeEvent(this));
    }
    
    public boolean isRadiusGridlinesVisible() {
        return this.radiusGridlinesVisible;
    }
    
    public void setRadiusGridlinesVisible(final boolean visible) {
        if (this.radiusGridlinesVisible != visible) {
            this.radiusGridlinesVisible = visible;
            this.notifyListeners(new PlotChangeEvent(this));
        }
    }
    
    public Stroke getRadiusGridlineStroke() {
        return this.radiusGridlineStroke;
    }
    
    public void setRadiusGridlineStroke(final Stroke stroke) {
        this.radiusGridlineStroke = stroke;
        this.notifyListeners(new PlotChangeEvent(this));
    }
    
    public Paint getRadiusGridlinePaint() {
        return this.radiusGridlinePaint;
    }
    
    public void setRadiusGridlinePaint(final Paint paint) {
        this.radiusGridlinePaint = paint;
        this.notifyListeners(new PlotChangeEvent(this));
    }
    
    public void draw(final Graphics2D g2, final Rectangle2D area, final Point2D anchor, final PlotState parentState, final PlotRenderingInfo info) {
        final boolean b1 = area.getWidth() <= 10.0;
        final boolean b2 = area.getHeight() <= 10.0;
        if (b1 || b2) {
            return;
        }
        if (info != null) {
            info.setPlotArea(area);
        }
        final RectangleInsets insets = this.getInsets();
        insets.trim(area);
        final Rectangle2D dataArea = area;
        if (info != null) {
            info.setDataArea(dataArea);
        }
        this.drawBackground(g2, dataArea);
        final double h = Math.min(dataArea.getWidth() / 2.0, dataArea.getHeight() / 2.0) - 20.0;
        final Rectangle2D quadrant = new Rectangle2D.Double(dataArea.getCenterX(), dataArea.getCenterY(), h, h);
        final AxisState state = this.drawAxis(g2, area, quadrant);
        if (this.renderer != null) {
            final Shape originalClip = g2.getClip();
            final Composite originalComposite = g2.getComposite();
            g2.clip(dataArea);
            g2.setComposite(AlphaComposite.getInstance(3, this.getForegroundAlpha()));
            this.drawGridlines(g2, dataArea, this.angleTicks, state.getTicks());
            this.render(g2, dataArea, info);
            g2.setClip(originalClip);
            g2.setComposite(originalComposite);
        }
        this.drawOutline(g2, dataArea);
        this.drawCornerTextItems(g2, dataArea);
    }
    
    protected void drawCornerTextItems(final Graphics2D g2, final Rectangle2D area) {
        if (this.cornerTextItems.isEmpty()) {
            return;
        }
        g2.setColor(Color.black);
        double width = 0.0;
        double height = 0.0;
        final Iterator it = this.cornerTextItems.iterator();
        while (it.hasNext()) {
            final String msg = it.next();
            final FontMetrics fm = g2.getFontMetrics();
            final Rectangle2D bounds = TextUtilities.getTextBounds(msg, g2, fm);
            width = Math.max(width, bounds.getWidth());
            height += bounds.getHeight();
        }
        final double xadj = 14.0;
        final double yadj = 7.0;
        width += xadj;
        height += yadj;
        double x = area.getMaxX() - width;
        double y = area.getMaxY() - height;
        g2.drawRect((int)x, (int)y, (int)width, (int)height);
        x += 7.0;
        final Iterator it2 = this.cornerTextItems.iterator();
        while (it2.hasNext()) {
            final String msg2 = it2.next();
            final Rectangle2D bounds2 = TextUtilities.getTextBounds(msg2, g2, g2.getFontMetrics());
            y += bounds2.getHeight();
            g2.drawString(msg2, (int)x, (int)y);
        }
    }
    
    protected AxisState drawAxis(final Graphics2D g2, final Rectangle2D plotArea, final Rectangle2D dataArea) {
        return this.axis.draw(g2, dataArea.getMinY(), plotArea, dataArea, RectangleEdge.TOP, null);
    }
    
    protected void render(final Graphics2D g2, final Rectangle2D dataArea, final PlotRenderingInfo info) {
        if (!DatasetUtilities.isEmptyOrNull(this.dataset)) {
            for (int seriesCount = this.dataset.getSeriesCount(), series = 0; series < seriesCount; ++series) {
                this.renderer.drawSeries(g2, dataArea, info, this, this.dataset, series);
            }
        }
        else {
            this.drawNoDataMessage(g2, dataArea);
        }
    }
    
    protected void drawGridlines(final Graphics2D g2, final Rectangle2D dataArea, final List angularTicks, final List radialTicks) {
        if (this.renderer == null) {
            return;
        }
        if (this.isAngleGridlinesVisible()) {
            final Stroke gridStroke = this.getAngleGridlineStroke();
            final Paint gridPaint = this.getAngleGridlinePaint();
            if (gridStroke != null && gridPaint != null) {
                this.renderer.drawAngularGridLines(g2, this, angularTicks, dataArea);
            }
        }
        if (this.isRadiusGridlinesVisible()) {
            final Stroke gridStroke = this.getRadiusGridlineStroke();
            final Paint gridPaint = this.getRadiusGridlinePaint();
            if (gridStroke != null && gridPaint != null) {
                this.renderer.drawRadialGridLines(g2, this, this.axis, radialTicks, dataArea);
            }
        }
    }
    
    public void zoom(final double percent) {
        if (percent > 0.0) {
            final double radius = this.getMaxRadius();
            final double scaledRadius = radius * percent;
            this.axis.setUpperBound(scaledRadius);
            this.getAxis().setAutoRange(false);
        }
        else {
            this.getAxis().setAutoRange(true);
        }
    }
    
    public Range getDataRange(final ValueAxis axis) {
        Range result = null;
        if (this.dataset != null) {
            result = Range.combine(result, DatasetUtilities.findRangeBounds(this.dataset));
        }
        return result;
    }
    
    public void datasetChanged(final DatasetChangeEvent event) {
        if (this.axis != null) {
            this.axis.configure();
        }
        if (this.getParent() != null) {
            this.getParent().datasetChanged(event);
        }
        else {
            super.datasetChanged(event);
        }
    }
    
    public void rendererChanged(final RendererChangeEvent event) {
        this.notifyListeners(new PlotChangeEvent(this));
    }
    
    public int getSeriesCount() {
        int result = 0;
        if (this.dataset != null) {
            result = this.dataset.getSeriesCount();
        }
        return result;
    }
    
    public LegendItemCollection getLegendItems() {
        final LegendItemCollection result = new LegendItemCollection();
        if (this.dataset != null && this.renderer != null) {
            for (int seriesCount = this.dataset.getSeriesCount(), i = 0; i < seriesCount; ++i) {
                final LegendItem item = this.renderer.getLegendItem(i);
                result.add(item);
            }
        }
        return result;
    }
    
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof PolarPlot)) {
            return false;
        }
        if (!super.equals(obj)) {
            return false;
        }
        final PolarPlot that = (PolarPlot)obj;
        return ObjectUtilities.equal((Object)this.axis, (Object)that.axis) && ObjectUtilities.equal((Object)this.renderer, (Object)that.renderer) && this.angleGridlinesVisible == that.angleGridlinesVisible && this.angleLabelsVisible == that.angleLabelsVisible && this.angleLabelFont.equals(that.angleLabelFont) && PaintUtilities.equal(this.angleLabelPaint, that.angleLabelPaint) && ObjectUtilities.equal((Object)this.angleGridlineStroke, (Object)that.angleGridlineStroke) && PaintUtilities.equal(this.angleGridlinePaint, that.angleGridlinePaint) && this.radiusGridlinesVisible == that.radiusGridlinesVisible && ObjectUtilities.equal((Object)this.radiusGridlineStroke, (Object)that.radiusGridlineStroke) && PaintUtilities.equal(this.radiusGridlinePaint, that.radiusGridlinePaint);
    }
    
    public Object clone() throws CloneNotSupportedException {
        final PolarPlot clone = (PolarPlot)super.clone();
        if (this.axis != null) {
            (clone.axis = (ValueAxis)ObjectUtilities.clone((Object)this.axis)).setPlot(clone);
            clone.axis.addChangeListener(clone);
        }
        if (clone.dataset != null) {
            clone.dataset.addChangeListener(clone);
        }
        if (this.renderer != null) {
            clone.renderer = (PolarItemRenderer)ObjectUtilities.clone((Object)this.renderer);
        }
        return clone;
    }
    
    private void writeObject(final ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        SerialUtilities.writeStroke(this.angleGridlineStroke, stream);
        SerialUtilities.writePaint(this.angleGridlinePaint, stream);
        SerialUtilities.writeStroke(this.radiusGridlineStroke, stream);
        SerialUtilities.writePaint(this.radiusGridlinePaint, stream);
    }
    
    private void readObject(final ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        this.angleGridlineStroke = SerialUtilities.readStroke(stream);
        this.angleGridlinePaint = SerialUtilities.readPaint(stream);
        this.radiusGridlineStroke = SerialUtilities.readStroke(stream);
        this.radiusGridlinePaint = SerialUtilities.readPaint(stream);
        if (this.axis != null) {
            this.axis.setPlot(this);
            this.axis.addChangeListener(this);
        }
        if (this.dataset != null) {
            this.dataset.addChangeListener(this);
        }
    }
    
    public void zoomDomainAxes(final double factor, final PlotRenderingInfo state, final Point2D source) {
    }
    
    public void zoomDomainAxes(final double lowerPercent, final double upperPercent, final PlotRenderingInfo state, final Point2D source) {
    }
    
    public void zoomRangeAxes(final double factor, final PlotRenderingInfo state, final Point2D source) {
        this.zoom(factor);
    }
    
    public void zoomRangeAxes(final double lowerPercent, final double upperPercent, final PlotRenderingInfo state, final Point2D source) {
        this.zoom((upperPercent + lowerPercent) / 2.0);
    }
    
    public boolean isDomainZoomable() {
        return false;
    }
    
    public boolean isRangeZoomable() {
        return true;
    }
    
    public PlotOrientation getOrientation() {
        return PlotOrientation.HORIZONTAL;
    }
    
    public double getMaxRadius() {
        return this.axis.getUpperBound();
    }
    
    public Point translateValueThetaRadiusToJava2D(final double angleDegrees, final double radius, final Rectangle2D dataArea) {
        final double radians = Math.toRadians(angleDegrees - 90.0);
        final double minx = dataArea.getMinX() + 20.0;
        final double maxx = dataArea.getMaxX() - 20.0;
        final double miny = dataArea.getMinY() + 20.0;
        final double maxy = dataArea.getMaxY() - 20.0;
        final double lengthX = maxx - minx;
        final double lengthY = maxy - miny;
        final double length = Math.min(lengthX, lengthY);
        final double midX = minx + lengthX / 2.0;
        final double midY = miny + lengthY / 2.0;
        final double axisMin = this.axis.getLowerBound();
        final double axisMax = this.getMaxRadius();
        final double xv = length / 2.0 * Math.cos(radians);
        final double yv = length / 2.0 * Math.sin(radians);
        final float x = (float)(midX + xv * (radius - axisMin) / (axisMax - axisMin));
        final float y = (float)(midY + yv * (radius - axisMin) / (axisMax - axisMin));
        final int ix = Math.round(x);
        final int iy = Math.round(y);
        final Point p = new Point(ix, iy);
        return p;
    }
    
    static {
        DEFAULT_GRIDLINE_STROKE = new BasicStroke(0.5f, 0, 2, 0.0f, new float[] { 2.0f, 2.0f }, 0.0f);
        DEFAULT_GRIDLINE_PAINT = Color.gray;
        PolarPlot.localizationResources = ResourceBundle.getBundle("org.jfree.chart.plot.LocalizationBundle");
    }
}
