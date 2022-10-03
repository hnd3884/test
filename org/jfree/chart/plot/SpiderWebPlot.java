package org.jfree.chart.plot;

import java.awt.BasicStroke;
import java.awt.Color;
import java.io.ObjectInputStream;
import java.io.IOException;
import org.jfree.io.SerialUtilities;
import java.io.ObjectOutputStream;
import org.jfree.util.ObjectUtilities;
import org.jfree.util.PaintUtilities;
import org.jfree.util.ShapeUtilities;
import java.awt.geom.Arc2D;
import java.awt.font.LineMetrics;
import java.awt.font.FontRenderContext;
import org.jfree.chart.entity.EntityCollection;
import java.awt.geom.Line2D;
import org.jfree.chart.entity.ChartEntity;
import org.jfree.chart.entity.CategoryItemEntity;
import java.awt.Rectangle;
import java.awt.geom.Ellipse2D;
import java.awt.Polygon;
import org.jfree.ui.RectangleInsets;
import org.jfree.data.general.DatasetUtilities;
import java.awt.Composite;
import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;
import java.util.List;
import org.jfree.chart.LegendItem;
import org.jfree.chart.LegendItemCollection;
import org.jfree.chart.event.PlotChangeEvent;
import org.jfree.data.general.Dataset;
import org.jfree.data.general.DatasetChangeEvent;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.data.general.DatasetChangeListener;
import org.jfree.chart.urls.CategoryURLGenerator;
import org.jfree.chart.labels.CategoryToolTipGenerator;
import org.jfree.chart.labels.CategoryItemLabelGenerator;
import org.jfree.util.StrokeList;
import org.jfree.util.PaintList;
import java.awt.Shape;
import org.jfree.util.Rotation;
import org.jfree.util.TableOrder;
import org.jfree.data.category.CategoryDataset;
import java.awt.Stroke;
import java.awt.Paint;
import java.awt.Font;
import java.io.Serializable;

public class SpiderWebPlot extends Plot implements Cloneable, Serializable
{
    private static final long serialVersionUID = -5376340422031599463L;
    public static final double DEFAULT_HEAD = 0.01;
    public static final double DEFAULT_AXIS_LABEL_GAP = 0.1;
    public static final double DEFAULT_INTERIOR_GAP = 0.25;
    public static final double MAX_INTERIOR_GAP = 0.4;
    public static final double DEFAULT_START_ANGLE = 90.0;
    public static final Font DEFAULT_LABEL_FONT;
    public static final Paint DEFAULT_LABEL_PAINT;
    public static final Paint DEFAULT_LABEL_BACKGROUND_PAINT;
    public static final Paint DEFAULT_LABEL_OUTLINE_PAINT;
    public static final Stroke DEFAULT_LABEL_OUTLINE_STROKE;
    public static final Paint DEFAULT_LABEL_SHADOW_PAINT;
    public static final double DEFAULT_MAX_VALUE = -1.0;
    protected double headPercent;
    private double interiorGap;
    private double axisLabelGap;
    private CategoryDataset dataset;
    private double maxValue;
    private TableOrder dataExtractOrder;
    private double startAngle;
    private Rotation direction;
    private transient Shape legendItemShape;
    private transient Paint seriesPaint;
    private PaintList seriesPaintList;
    private transient Paint baseSeriesPaint;
    private transient Paint seriesOutlinePaint;
    private PaintList seriesOutlinePaintList;
    private transient Paint baseSeriesOutlinePaint;
    private transient Stroke seriesOutlineStroke;
    private StrokeList seriesOutlineStrokeList;
    private transient Stroke baseSeriesOutlineStroke;
    private Font labelFont;
    private transient Paint labelPaint;
    private CategoryItemLabelGenerator labelGenerator;
    private boolean webFilled;
    private CategoryToolTipGenerator toolTipGenerator;
    private CategoryURLGenerator urlGenerator;
    
    public SpiderWebPlot() {
        this(null);
    }
    
    public SpiderWebPlot(final CategoryDataset dataset) {
        this(dataset, TableOrder.BY_ROW);
    }
    
    public SpiderWebPlot(final CategoryDataset dataset, final TableOrder extract) {
        this.webFilled = true;
        if (extract == null) {
            throw new IllegalArgumentException("Null 'extract' argument.");
        }
        if ((this.dataset = dataset) != null) {
            dataset.addChangeListener(this);
        }
        this.dataExtractOrder = extract;
        this.headPercent = 0.01;
        this.axisLabelGap = 0.1;
        this.interiorGap = 0.25;
        this.startAngle = 90.0;
        this.direction = Rotation.CLOCKWISE;
        this.maxValue = -1.0;
        this.seriesPaint = null;
        this.seriesPaintList = new PaintList();
        this.baseSeriesPaint = null;
        this.seriesOutlinePaint = null;
        this.seriesOutlinePaintList = new PaintList();
        this.baseSeriesOutlinePaint = SpiderWebPlot.DEFAULT_OUTLINE_PAINT;
        this.seriesOutlineStroke = null;
        this.seriesOutlineStrokeList = new StrokeList();
        this.baseSeriesOutlineStroke = SpiderWebPlot.DEFAULT_OUTLINE_STROKE;
        this.labelFont = SpiderWebPlot.DEFAULT_LABEL_FONT;
        this.labelPaint = SpiderWebPlot.DEFAULT_LABEL_PAINT;
        this.labelGenerator = new StandardCategoryItemLabelGenerator();
        this.legendItemShape = SpiderWebPlot.DEFAULT_LEGEND_ITEM_CIRCLE;
    }
    
    public String getPlotType() {
        return "Spider Web Plot";
    }
    
    public CategoryDataset getDataset() {
        return this.dataset;
    }
    
    public void setDataset(final CategoryDataset dataset) {
        if (this.dataset != null) {
            this.dataset.removeChangeListener(this);
        }
        if ((this.dataset = dataset) != null) {
            this.setDatasetGroup(dataset.getGroup());
            dataset.addChangeListener(this);
        }
        this.datasetChanged(new DatasetChangeEvent(this, dataset));
    }
    
    public boolean isWebFilled() {
        return this.webFilled;
    }
    
    public void setWebFilled(final boolean flag) {
        this.webFilled = flag;
        this.notifyListeners(new PlotChangeEvent(this));
    }
    
    public TableOrder getDataExtractOrder() {
        return this.dataExtractOrder;
    }
    
    public void setDataExtractOrder(final TableOrder order) {
        if (order == null) {
            throw new IllegalArgumentException("Null 'order' argument");
        }
        this.dataExtractOrder = order;
        this.notifyListeners(new PlotChangeEvent(this));
    }
    
    public double getHeadPercent() {
        return this.headPercent;
    }
    
    public void setHeadPercent(final double percent) {
        this.headPercent = percent;
        this.notifyListeners(new PlotChangeEvent(this));
    }
    
    public double getStartAngle() {
        return this.startAngle;
    }
    
    public void setStartAngle(final double angle) {
        this.startAngle = angle;
        this.notifyListeners(new PlotChangeEvent(this));
    }
    
    public double getMaxValue() {
        return this.maxValue;
    }
    
    public void setMaxValue(final double value) {
        this.maxValue = value;
        this.notifyListeners(new PlotChangeEvent(this));
    }
    
    public Rotation getDirection() {
        return this.direction;
    }
    
    public void setDirection(final Rotation direction) {
        if (direction == null) {
            throw new IllegalArgumentException("Null 'direction' argument.");
        }
        this.direction = direction;
        this.notifyListeners(new PlotChangeEvent(this));
    }
    
    public double getInteriorGap() {
        return this.interiorGap;
    }
    
    public void setInteriorGap(final double percent) {
        if (percent < 0.0 || percent > 0.4) {
            throw new IllegalArgumentException("Percentage outside valid range.");
        }
        if (this.interiorGap != percent) {
            this.interiorGap = percent;
            this.notifyListeners(new PlotChangeEvent(this));
        }
    }
    
    public double getAxisLabelGap() {
        return this.axisLabelGap;
    }
    
    public void setAxisLabelGap(final double gap) {
        this.axisLabelGap = gap;
        this.notifyListeners(new PlotChangeEvent(this));
    }
    
    public Paint getSeriesPaint() {
        return this.seriesPaint;
    }
    
    public void setSeriesPaint(final Paint paint) {
        this.seriesPaint = paint;
        this.notifyListeners(new PlotChangeEvent(this));
    }
    
    public Paint getSeriesPaint(final int series) {
        if (this.seriesPaint != null) {
            return this.seriesPaint;
        }
        Paint result = this.seriesPaintList.getPaint(series);
        if (result == null) {
            final DrawingSupplier supplier = this.getDrawingSupplier();
            if (supplier != null) {
                final Paint p = supplier.getNextPaint();
                this.seriesPaintList.setPaint(series, p);
                result = p;
            }
            else {
                result = this.baseSeriesPaint;
            }
        }
        return result;
    }
    
    public void setSeriesPaint(final int series, final Paint paint) {
        this.seriesPaintList.setPaint(series, paint);
        this.notifyListeners(new PlotChangeEvent(this));
    }
    
    public Paint getBaseSeriesPaint() {
        return this.baseSeriesPaint;
    }
    
    public void setBaseSeriesPaint(final Paint paint) {
        if (paint == null) {
            throw new IllegalArgumentException("Null 'paint' argument.");
        }
        this.baseSeriesPaint = paint;
        this.notifyListeners(new PlotChangeEvent(this));
    }
    
    public Paint getSeriesOutlinePaint() {
        return this.seriesOutlinePaint;
    }
    
    public void setSeriesOutlinePaint(final Paint paint) {
        this.seriesOutlinePaint = paint;
        this.notifyListeners(new PlotChangeEvent(this));
    }
    
    public Paint getSeriesOutlinePaint(final int series) {
        if (this.seriesOutlinePaint != null) {
            return this.seriesOutlinePaint;
        }
        Paint result = this.seriesOutlinePaintList.getPaint(series);
        if (result == null) {
            result = this.baseSeriesOutlinePaint;
        }
        return result;
    }
    
    public void setSeriesOutlinePaint(final int series, final Paint paint) {
        this.seriesOutlinePaintList.setPaint(series, paint);
        this.notifyListeners(new PlotChangeEvent(this));
    }
    
    public Paint getBaseSeriesOutlinePaint() {
        return this.baseSeriesOutlinePaint;
    }
    
    public void setBaseSeriesOutlinePaint(final Paint paint) {
        if (paint == null) {
            throw new IllegalArgumentException("Null 'paint' argument.");
        }
        this.baseSeriesOutlinePaint = paint;
        this.notifyListeners(new PlotChangeEvent(this));
    }
    
    public Stroke getSeriesOutlineStroke() {
        return this.seriesOutlineStroke;
    }
    
    public void setSeriesOutlineStroke(final Stroke stroke) {
        this.seriesOutlineStroke = stroke;
        this.notifyListeners(new PlotChangeEvent(this));
    }
    
    public Stroke getSeriesOutlineStroke(final int series) {
        if (this.seriesOutlineStroke != null) {
            return this.seriesOutlineStroke;
        }
        Stroke result = this.seriesOutlineStrokeList.getStroke(series);
        if (result == null) {
            result = this.baseSeriesOutlineStroke;
        }
        return result;
    }
    
    public void setSeriesOutlineStroke(final int series, final Stroke stroke) {
        this.seriesOutlineStrokeList.setStroke(series, stroke);
        this.notifyListeners(new PlotChangeEvent(this));
    }
    
    public Stroke getBaseSeriesOutlineStroke() {
        return this.baseSeriesOutlineStroke;
    }
    
    public void setBaseSeriesOutlineStroke(final Stroke stroke) {
        if (stroke == null) {
            throw new IllegalArgumentException("Null 'stroke' argument.");
        }
        this.baseSeriesOutlineStroke = stroke;
        this.notifyListeners(new PlotChangeEvent(this));
    }
    
    public Shape getLegendItemShape() {
        return this.legendItemShape;
    }
    
    public void setLegendItemShape(final Shape shape) {
        if (shape == null) {
            throw new IllegalArgumentException("Null 'shape' argument.");
        }
        this.legendItemShape = shape;
        this.notifyListeners(new PlotChangeEvent(this));
    }
    
    public Font getLabelFont() {
        return this.labelFont;
    }
    
    public void setLabelFont(final Font font) {
        if (font == null) {
            throw new IllegalArgumentException("Null 'font' argument.");
        }
        this.labelFont = font;
        this.notifyListeners(new PlotChangeEvent(this));
    }
    
    public Paint getLabelPaint() {
        return this.labelPaint;
    }
    
    public void setLabelPaint(final Paint paint) {
        if (paint == null) {
            throw new IllegalArgumentException("Null 'paint' argument.");
        }
        this.labelPaint = paint;
        this.notifyListeners(new PlotChangeEvent(this));
    }
    
    public CategoryItemLabelGenerator getLabelGenerator() {
        return this.labelGenerator;
    }
    
    public void setLabelGenerator(final CategoryItemLabelGenerator generator) {
        if (generator == null) {
            throw new IllegalArgumentException("Null 'generator' argument.");
        }
        this.labelGenerator = generator;
    }
    
    public CategoryToolTipGenerator getToolTipGenerator() {
        return this.toolTipGenerator;
    }
    
    public void setToolTipGenerator(final CategoryToolTipGenerator generator) {
        this.toolTipGenerator = generator;
        this.notifyListeners(new PlotChangeEvent(this));
    }
    
    public CategoryURLGenerator getURLGenerator() {
        return this.urlGenerator;
    }
    
    public void setURLGenerator(final CategoryURLGenerator generator) {
        this.urlGenerator = generator;
        this.notifyListeners(new PlotChangeEvent(this));
    }
    
    public LegendItemCollection getLegendItems() {
        final LegendItemCollection result = new LegendItemCollection();
        List keys = null;
        if (this.dataExtractOrder == TableOrder.BY_ROW) {
            keys = this.dataset.getRowKeys();
        }
        else if (this.dataExtractOrder == TableOrder.BY_COLUMN) {
            keys = this.dataset.getColumnKeys();
        }
        if (keys != null) {
            int series = 0;
            final Iterator iterator = keys.iterator();
            final Shape shape = this.getLegendItemShape();
            while (iterator.hasNext()) {
                final String description;
                final String label = description = iterator.next().toString();
                final Paint paint = this.getSeriesPaint(series);
                final Paint outlinePaint = this.getSeriesOutlinePaint(series);
                final Stroke stroke = this.getSeriesOutlineStroke(series);
                final LegendItem item = new LegendItem(label, description, null, null, shape, paint, stroke, outlinePaint);
                result.add(item);
                ++series;
            }
        }
        return result;
    }
    
    protected Point2D getWebPoint(final Rectangle2D bounds, final double angle, final double length) {
        final double angrad = Math.toRadians(angle);
        final double x = Math.cos(angrad) * length * bounds.getWidth() / 2.0;
        final double y = -Math.sin(angrad) * length * bounds.getHeight() / 2.0;
        return new Point2D.Double(bounds.getX() + x + bounds.getWidth() / 2.0, bounds.getY() + y + bounds.getHeight() / 2.0);
    }
    
    public void draw(final Graphics2D g2, final Rectangle2D area, final Point2D anchor, final PlotState parentState, final PlotRenderingInfo info) {
        final RectangleInsets insets = this.getInsets();
        insets.trim(area);
        if (info != null) {
            info.setPlotArea(area);
            info.setDataArea(area);
        }
        this.drawBackground(g2, area);
        this.drawOutline(g2, area);
        final Shape savedClip = g2.getClip();
        g2.clip(area);
        final Composite originalComposite = g2.getComposite();
        g2.setComposite(AlphaComposite.getInstance(3, this.getForegroundAlpha()));
        if (!DatasetUtilities.isEmptyOrNull(this.dataset)) {
            int seriesCount = 0;
            int catCount = 0;
            if (this.dataExtractOrder == TableOrder.BY_ROW) {
                seriesCount = this.dataset.getRowCount();
                catCount = this.dataset.getColumnCount();
            }
            else {
                seriesCount = this.dataset.getColumnCount();
                catCount = this.dataset.getRowCount();
            }
            if (this.maxValue == -1.0) {
                this.calculateMaxValue(seriesCount, catCount);
            }
            final double gapHorizontal = area.getWidth() * this.getInteriorGap();
            final double gapVertical = area.getHeight() * this.getInteriorGap();
            double X = area.getX() + gapHorizontal / 2.0;
            double Y = area.getY() + gapVertical / 2.0;
            double W = area.getWidth() - gapHorizontal;
            double H = area.getHeight() - gapVertical;
            final double headW = area.getWidth() * this.headPercent;
            final double headH = area.getHeight() * this.headPercent;
            final double min = Math.min(W, H) / 2.0;
            X = (X + X + W) / 2.0 - min;
            Y = (Y + Y + H) / 2.0 - min;
            W = 2.0 * min;
            H = 2.0 * min;
            final Point2D centre = new Point2D.Double(X + W / 2.0, Y + H / 2.0);
            final Rectangle2D radarArea = new Rectangle2D.Double(X, Y, W, H);
            for (int series = 0; series < seriesCount; ++series) {
                this.drawRadarPoly(g2, radarArea, centre, info, series, catCount, headH, headW);
            }
        }
        else {
            this.drawNoDataMessage(g2, area);
        }
        g2.clip(savedClip);
        g2.setComposite(originalComposite);
        this.drawOutline(g2, area);
    }
    
    private void calculateMaxValue(final int seriesCount, final int catCount) {
        double v = 0.0;
        Number nV = null;
        for (int seriesIndex = 0; seriesIndex < seriesCount; ++seriesIndex) {
            for (int catIndex = 0; catIndex < catCount; ++catIndex) {
                nV = this.getPlotValue(seriesIndex, catIndex);
                if (nV != null) {
                    v = nV.doubleValue();
                    if (v > this.maxValue) {
                        this.maxValue = v;
                    }
                }
            }
        }
    }
    
    protected void drawRadarPoly(final Graphics2D g2, final Rectangle2D plotArea, final Point2D centre, final PlotRenderingInfo info, final int series, final int catCount, final double headH, final double headW) {
        final Polygon polygon = new Polygon();
        EntityCollection entities = null;
        if (info != null) {
            entities = info.getOwner().getEntityCollection();
        }
        for (int cat = 0; cat < catCount; ++cat) {
            final Number dataValue = this.getPlotValue(series, cat);
            if (dataValue != null) {
                final double value = dataValue.doubleValue();
                if (value >= 0.0) {
                    final double angle = this.getStartAngle() + this.getDirection().getFactor() * cat * 360.0 / catCount;
                    final Point2D point = this.getWebPoint(plotArea, angle, value / this.maxValue);
                    polygon.addPoint((int)point.getX(), (int)point.getY());
                    final Paint paint = this.getSeriesPaint(series);
                    final Paint outlinePaint = this.getSeriesOutlinePaint(series);
                    final Stroke outlineStroke = this.getSeriesOutlineStroke(series);
                    final Ellipse2D head = new Ellipse2D.Double(point.getX() - headW / 2.0, point.getY() - headH / 2.0, headW, headH);
                    g2.setPaint(paint);
                    g2.fill(head);
                    g2.setStroke(outlineStroke);
                    g2.setPaint(outlinePaint);
                    g2.draw(head);
                    if (entities != null) {
                        String tip = null;
                        if (this.toolTipGenerator != null) {
                            tip = this.toolTipGenerator.generateToolTip(this.dataset, series, cat);
                        }
                        String url = null;
                        if (this.urlGenerator != null) {
                            url = this.urlGenerator.generateURL(this.dataset, series, cat);
                        }
                        final Shape area = new Rectangle((int)(point.getX() - headW), (int)(point.getY() - headH), (int)(headW * 2.0), (int)(headH * 2.0));
                        final CategoryItemEntity entity = new CategoryItemEntity(area, tip, url, this.dataset, series, this.dataset.getColumnKey(cat), cat);
                        entities.add(entity);
                    }
                    if (series == 0) {
                        final Point2D endPoint = this.getWebPoint(plotArea, angle, 1.0);
                        final Line2D line = new Line2D.Double(centre, endPoint);
                        g2.draw(line);
                        this.drawLabel(g2, plotArea, value, cat, angle, 360.0 / catCount);
                    }
                }
            }
        }
        final Paint paint2 = this.getSeriesPaint(series);
        g2.setPaint(paint2);
        g2.draw(polygon);
        if (this.webFilled) {
            g2.setComposite(AlphaComposite.getInstance(3, 0.1f));
            g2.fill(polygon);
            g2.setComposite(AlphaComposite.getInstance(3, this.getForegroundAlpha()));
        }
    }
    
    Number getPlotValue(final int series, final int cat) {
        Number value = null;
        if (this.dataExtractOrder == TableOrder.BY_ROW) {
            value = this.dataset.getValue(series, cat);
        }
        else if (this.dataExtractOrder == TableOrder.BY_COLUMN) {
            value = this.dataset.getValue(cat, series);
        }
        return value;
    }
    
    protected void drawLabel(final Graphics2D g2, final Rectangle2D plotArea, final double value, final int cat, final double startAngle, final double extent) {
        final FontRenderContext frc = g2.getFontRenderContext();
        String label = null;
        if (this.dataExtractOrder == TableOrder.BY_ROW) {
            label = this.labelGenerator.generateColumnLabel(this.dataset, cat);
        }
        else {
            label = this.labelGenerator.generateRowLabel(this.dataset, cat);
        }
        final Rectangle2D labelBounds = this.getLabelFont().getStringBounds(label, frc);
        final LineMetrics lm = this.getLabelFont().getLineMetrics(label, frc);
        final double ascent = lm.getAscent();
        final Point2D labelLocation = this.calculateLabelLocation(labelBounds, ascent, plotArea, startAngle);
        final Composite saveComposite = g2.getComposite();
        g2.setComposite(AlphaComposite.getInstance(3, 1.0f));
        g2.setPaint(this.getLabelPaint());
        g2.setFont(this.getLabelFont());
        g2.drawString(label, (float)labelLocation.getX(), (float)labelLocation.getY());
        g2.setComposite(saveComposite);
    }
    
    protected Point2D calculateLabelLocation(final Rectangle2D labelBounds, final double ascent, final Rectangle2D plotArea, final double startAngle) {
        final Arc2D arc1 = new Arc2D.Double(plotArea, startAngle, 0.0, 0);
        final Point2D point1 = arc1.getEndPoint();
        final double deltaX = -(point1.getX() - plotArea.getCenterX()) * this.axisLabelGap;
        final double deltaY = -(point1.getY() - plotArea.getCenterY()) * this.axisLabelGap;
        double labelX = point1.getX() - deltaX;
        double labelY = point1.getY() - deltaY;
        if (labelX < plotArea.getCenterX()) {
            labelX -= labelBounds.getWidth();
        }
        if (labelX == plotArea.getCenterX()) {
            labelX -= labelBounds.getWidth() / 2.0;
        }
        if (labelY > plotArea.getCenterY()) {
            labelY += ascent;
        }
        return new Point2D.Double(labelX, labelY);
    }
    
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof SpiderWebPlot)) {
            return false;
        }
        if (!super.equals(obj)) {
            return false;
        }
        final SpiderWebPlot that = (SpiderWebPlot)obj;
        return this.dataExtractOrder.equals((Object)that.dataExtractOrder) && this.headPercent == that.headPercent && this.interiorGap == that.interiorGap && this.startAngle == that.startAngle && this.direction.equals((Object)that.direction) && this.maxValue == that.maxValue && this.webFilled == that.webFilled && ShapeUtilities.equal(this.legendItemShape, that.legendItemShape) && PaintUtilities.equal(this.seriesPaint, that.seriesPaint) && this.seriesPaintList.equals((Object)that.seriesPaintList) && PaintUtilities.equal(this.baseSeriesPaint, that.baseSeriesPaint) && PaintUtilities.equal(this.seriesOutlinePaint, that.seriesOutlinePaint) && this.seriesOutlinePaintList.equals((Object)that.seriesOutlinePaintList) && PaintUtilities.equal(this.baseSeriesOutlinePaint, that.baseSeriesOutlinePaint) && ObjectUtilities.equal((Object)this.seriesOutlineStroke, (Object)that.seriesOutlineStroke) && this.seriesOutlineStrokeList.equals((Object)that.seriesOutlineStrokeList) && this.baseSeriesOutlineStroke.equals(that.baseSeriesOutlineStroke) && this.labelFont.equals(that.labelFont) && PaintUtilities.equal(this.labelPaint, that.labelPaint) && this.labelGenerator.equals(that.labelGenerator) && ObjectUtilities.equal((Object)this.toolTipGenerator, (Object)that.toolTipGenerator) && ObjectUtilities.equal((Object)this.urlGenerator, (Object)that.urlGenerator);
    }
    
    private void writeObject(final ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        SerialUtilities.writeShape(this.legendItemShape, stream);
        SerialUtilities.writePaint(this.seriesPaint, stream);
        SerialUtilities.writePaint(this.baseSeriesPaint, stream);
        SerialUtilities.writePaint(this.seriesOutlinePaint, stream);
        SerialUtilities.writePaint(this.baseSeriesOutlinePaint, stream);
        SerialUtilities.writeStroke(this.seriesOutlineStroke, stream);
        SerialUtilities.writeStroke(this.baseSeriesOutlineStroke, stream);
        SerialUtilities.writePaint(this.labelPaint, stream);
    }
    
    private void readObject(final ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        this.legendItemShape = SerialUtilities.readShape(stream);
        this.seriesPaint = SerialUtilities.readPaint(stream);
        this.baseSeriesPaint = SerialUtilities.readPaint(stream);
        this.seriesOutlinePaint = SerialUtilities.readPaint(stream);
        this.baseSeriesOutlinePaint = SerialUtilities.readPaint(stream);
        this.seriesOutlineStroke = SerialUtilities.readStroke(stream);
        this.baseSeriesOutlineStroke = SerialUtilities.readStroke(stream);
        this.labelPaint = SerialUtilities.readPaint(stream);
        if (this.dataset != null) {
            this.dataset.addChangeListener(this);
        }
    }
    
    static {
        DEFAULT_LABEL_FONT = new Font("SansSerif", 0, 10);
        DEFAULT_LABEL_PAINT = Color.black;
        DEFAULT_LABEL_BACKGROUND_PAINT = new Color(255, 255, 192);
        DEFAULT_LABEL_OUTLINE_PAINT = Color.black;
        DEFAULT_LABEL_OUTLINE_STROKE = new BasicStroke(0.5f);
        DEFAULT_LABEL_SHADOW_PAINT = Color.lightGray;
    }
}
