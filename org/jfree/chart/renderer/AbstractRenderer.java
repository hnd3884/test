package org.jfree.chart.renderer;

import java.awt.geom.Rectangle2D;
import java.awt.BasicStroke;
import java.io.ObjectInputStream;
import java.io.IOException;
import org.jfree.io.SerialUtilities;
import java.io.ObjectOutputStream;
import org.jfree.util.ShapeUtilities;
import org.jfree.util.PaintUtilities;
import org.jfree.util.ObjectUtilities;
import java.util.List;
import java.util.Arrays;
import java.util.EventListener;
import org.jfree.chart.event.RendererChangeListener;
import java.awt.geom.Point2D;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.util.BooleanUtilities;
import org.jfree.chart.event.RendererChangeEvent;
import org.jfree.chart.plot.DrawingSupplier;
import org.jfree.ui.TextAnchor;
import org.jfree.chart.labels.ItemLabelAnchor;
import java.awt.Color;
import javax.swing.event.EventListenerList;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.util.ObjectList;
import org.jfree.util.ShapeList;
import org.jfree.util.StrokeList;
import org.jfree.util.PaintList;
import org.jfree.util.BooleanList;
import java.awt.Font;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.Paint;
import java.io.Serializable;

public abstract class AbstractRenderer implements Cloneable, Serializable
{
    private static final long serialVersionUID = -828267569428206075L;
    public static final Double ZERO;
    public static final Paint DEFAULT_PAINT;
    public static final Paint DEFAULT_OUTLINE_PAINT;
    public static final Stroke DEFAULT_STROKE;
    public static final Stroke DEFAULT_OUTLINE_STROKE;
    public static final Shape DEFAULT_SHAPE;
    public static final Font DEFAULT_VALUE_LABEL_FONT;
    public static final Paint DEFAULT_VALUE_LABEL_PAINT;
    private Boolean seriesVisible;
    private BooleanList seriesVisibleList;
    private boolean baseSeriesVisible;
    private Boolean seriesVisibleInLegend;
    private BooleanList seriesVisibleInLegendList;
    private boolean baseSeriesVisibleInLegend;
    private transient Paint paint;
    private PaintList paintList;
    private transient Paint basePaint;
    private transient Paint fillPaint;
    private PaintList fillPaintList;
    private transient Paint baseFillPaint;
    private transient Paint outlinePaint;
    private PaintList outlinePaintList;
    private transient Paint baseOutlinePaint;
    private transient Stroke stroke;
    private StrokeList strokeList;
    private transient Stroke baseStroke;
    private transient Stroke outlineStroke;
    private StrokeList outlineStrokeList;
    private transient Stroke baseOutlineStroke;
    private transient Shape shape;
    private ShapeList shapeList;
    private transient Shape baseShape;
    private Boolean itemLabelsVisible;
    private BooleanList itemLabelsVisibleList;
    private Boolean baseItemLabelsVisible;
    private Font itemLabelFont;
    private ObjectList itemLabelFontList;
    private Font baseItemLabelFont;
    private transient Paint itemLabelPaint;
    private PaintList itemLabelPaintList;
    private transient Paint baseItemLabelPaint;
    private ItemLabelPosition positiveItemLabelPosition;
    private ObjectList positiveItemLabelPositionList;
    private ItemLabelPosition basePositiveItemLabelPosition;
    private ItemLabelPosition negativeItemLabelPosition;
    private ObjectList negativeItemLabelPositionList;
    private ItemLabelPosition baseNegativeItemLabelPosition;
    private double itemLabelAnchorOffset;
    private Boolean createEntities;
    private BooleanList createEntitiesList;
    private boolean baseCreateEntities;
    private transient EventListenerList listenerList;
    private static final double ADJ;
    private static final double OPP;
    
    public AbstractRenderer() {
        this.itemLabelAnchorOffset = 2.0;
        this.seriesVisible = null;
        this.seriesVisibleList = new BooleanList();
        this.baseSeriesVisible = true;
        this.seriesVisibleInLegend = null;
        this.seriesVisibleInLegendList = new BooleanList();
        this.baseSeriesVisibleInLegend = true;
        this.paint = null;
        this.paintList = new PaintList();
        this.basePaint = AbstractRenderer.DEFAULT_PAINT;
        this.fillPaint = null;
        this.fillPaintList = new PaintList();
        this.baseFillPaint = Color.white;
        this.outlinePaint = null;
        this.outlinePaintList = new PaintList();
        this.baseOutlinePaint = AbstractRenderer.DEFAULT_OUTLINE_PAINT;
        this.stroke = null;
        this.strokeList = new StrokeList();
        this.baseStroke = AbstractRenderer.DEFAULT_STROKE;
        this.outlineStroke = null;
        this.outlineStrokeList = new StrokeList();
        this.baseOutlineStroke = AbstractRenderer.DEFAULT_OUTLINE_STROKE;
        this.shape = null;
        this.shapeList = new ShapeList();
        this.baseShape = AbstractRenderer.DEFAULT_SHAPE;
        this.itemLabelsVisible = null;
        this.itemLabelsVisibleList = new BooleanList();
        this.baseItemLabelsVisible = Boolean.FALSE;
        this.itemLabelFont = null;
        this.itemLabelFontList = new ObjectList();
        this.baseItemLabelFont = new Font("SansSerif", 0, 10);
        this.itemLabelPaint = null;
        this.itemLabelPaintList = new PaintList();
        this.baseItemLabelPaint = Color.black;
        this.positiveItemLabelPosition = null;
        this.positiveItemLabelPositionList = new ObjectList();
        this.basePositiveItemLabelPosition = new ItemLabelPosition(ItemLabelAnchor.OUTSIDE12, TextAnchor.BOTTOM_CENTER);
        this.negativeItemLabelPosition = null;
        this.negativeItemLabelPositionList = new ObjectList();
        this.baseNegativeItemLabelPosition = new ItemLabelPosition(ItemLabelAnchor.OUTSIDE6, TextAnchor.TOP_CENTER);
        this.createEntities = null;
        this.createEntitiesList = new BooleanList();
        this.baseCreateEntities = true;
        this.listenerList = new EventListenerList();
    }
    
    public abstract DrawingSupplier getDrawingSupplier();
    
    public boolean getItemVisible(final int series, final int item) {
        return this.isSeriesVisible(series);
    }
    
    public boolean isSeriesVisible(final int series) {
        boolean result = this.baseSeriesVisible;
        if (this.seriesVisible != null) {
            result = this.seriesVisible;
        }
        else {
            final Boolean b = this.seriesVisibleList.getBoolean(series);
            if (b != null) {
                result = b;
            }
        }
        return result;
    }
    
    public Boolean getSeriesVisible() {
        return this.seriesVisible;
    }
    
    public void setSeriesVisible(final Boolean visible) {
        this.setSeriesVisible(visible, true);
    }
    
    public void setSeriesVisible(final Boolean visible, final boolean notify) {
        this.seriesVisible = visible;
        if (notify) {
            this.notifyListeners(new RendererChangeEvent(this));
        }
    }
    
    public Boolean getSeriesVisible(final int series) {
        return this.seriesVisibleList.getBoolean(series);
    }
    
    public void setSeriesVisible(final int series, final Boolean visible) {
        this.setSeriesVisible(series, visible, true);
    }
    
    public void setSeriesVisible(final int series, final Boolean visible, final boolean notify) {
        this.seriesVisibleList.setBoolean(series, visible);
        if (notify) {
            this.notifyListeners(new RendererChangeEvent(this));
        }
    }
    
    public boolean getBaseSeriesVisible() {
        return this.baseSeriesVisible;
    }
    
    public void setBaseSeriesVisible(final boolean visible) {
        this.setBaseSeriesVisible(visible, true);
    }
    
    public void setBaseSeriesVisible(final boolean visible, final boolean notify) {
        this.baseSeriesVisible = visible;
        if (notify) {
            this.notifyListeners(new RendererChangeEvent(this));
        }
    }
    
    public boolean isSeriesVisibleInLegend(final int series) {
        boolean result = this.baseSeriesVisibleInLegend;
        if (this.seriesVisibleInLegend != null) {
            result = this.seriesVisibleInLegend;
        }
        else {
            final Boolean b = this.seriesVisibleInLegendList.getBoolean(series);
            if (b != null) {
                result = b;
            }
        }
        return result;
    }
    
    public Boolean getSeriesVisibleInLegend() {
        return this.seriesVisibleInLegend;
    }
    
    public void setSeriesVisibleInLegend(final Boolean visible) {
        this.setSeriesVisibleInLegend(visible, true);
    }
    
    public void setSeriesVisibleInLegend(final Boolean visible, final boolean notify) {
        this.seriesVisibleInLegend = visible;
        if (notify) {
            this.notifyListeners(new RendererChangeEvent(this));
        }
    }
    
    public Boolean getSeriesVisibleInLegend(final int series) {
        return this.seriesVisibleInLegendList.getBoolean(series);
    }
    
    public void setSeriesVisibleInLegend(final int series, final Boolean visible) {
        this.setSeriesVisibleInLegend(series, visible, true);
    }
    
    public void setSeriesVisibleInLegend(final int series, final Boolean visible, final boolean notify) {
        this.seriesVisibleInLegendList.setBoolean(series, visible);
        if (notify) {
            this.notifyListeners(new RendererChangeEvent(this));
        }
    }
    
    public boolean getBaseSeriesVisibleInLegend() {
        return this.baseSeriesVisibleInLegend;
    }
    
    public void setBaseSeriesVisibleInLegend(final boolean visible) {
        this.setBaseSeriesVisibleInLegend(visible, true);
    }
    
    public void setBaseSeriesVisibleInLegend(final boolean visible, final boolean notify) {
        this.baseSeriesVisibleInLegend = visible;
        if (notify) {
            this.notifyListeners(new RendererChangeEvent(this));
        }
    }
    
    public Paint getItemPaint(final int row, final int column) {
        return this.getSeriesPaint(row);
    }
    
    public Paint getSeriesPaint(final int series) {
        if (this.paint != null) {
            return this.paint;
        }
        Paint seriesPaint = this.paintList.getPaint(series);
        if (seriesPaint == null) {
            final DrawingSupplier supplier = this.getDrawingSupplier();
            if (supplier != null) {
                seriesPaint = supplier.getNextPaint();
                this.paintList.setPaint(series, seriesPaint);
            }
            else {
                seriesPaint = this.basePaint;
            }
        }
        return seriesPaint;
    }
    
    public void setPaint(final Paint paint) {
        this.setPaint(paint, true);
    }
    
    public void setPaint(final Paint paint, final boolean notify) {
        this.paint = paint;
        if (notify) {
            this.notifyListeners(new RendererChangeEvent(this));
        }
    }
    
    public void setSeriesPaint(final int series, final Paint paint) {
        this.setSeriesPaint(series, paint, true);
    }
    
    public void setSeriesPaint(final int series, final Paint paint, final boolean notify) {
        this.paintList.setPaint(series, paint);
        if (notify) {
            this.notifyListeners(new RendererChangeEvent(this));
        }
    }
    
    public Paint getBasePaint() {
        return this.basePaint;
    }
    
    public void setBasePaint(final Paint paint) {
        this.setBasePaint(paint, true);
    }
    
    public void setBasePaint(final Paint paint, final boolean notify) {
        this.basePaint = paint;
        if (notify) {
            this.notifyListeners(new RendererChangeEvent(this));
        }
    }
    
    public Paint getItemFillPaint(final int row, final int column) {
        return this.getSeriesFillPaint(row);
    }
    
    public Paint getSeriesFillPaint(final int series) {
        if (this.fillPaint != null) {
            return this.fillPaint;
        }
        Paint seriesFillPaint = this.fillPaintList.getPaint(series);
        if (seriesFillPaint == null) {
            seriesFillPaint = this.baseFillPaint;
        }
        return seriesFillPaint;
    }
    
    public void setSeriesFillPaint(final int series, final Paint paint) {
        this.setSeriesFillPaint(series, paint, true);
    }
    
    public void setSeriesFillPaint(final int series, final Paint paint, final boolean notify) {
        this.fillPaintList.setPaint(series, paint);
        if (notify) {
            this.notifyListeners(new RendererChangeEvent(this));
        }
    }
    
    public void setFillPaint(final Paint paint) {
        this.setFillPaint(paint, true);
    }
    
    public void setFillPaint(final Paint paint, final boolean notify) {
        this.fillPaint = paint;
        if (notify) {
            this.notifyListeners(new RendererChangeEvent(this));
        }
    }
    
    public Paint getBaseFillPaint() {
        return this.baseFillPaint;
    }
    
    public void setBaseFillPaint(final Paint paint) {
        this.setBaseFillPaint(paint, true);
    }
    
    public void setBaseFillPaint(final Paint paint, final boolean notify) {
        if (paint == null) {
            throw new IllegalArgumentException("Null 'paint' argument.");
        }
        this.baseFillPaint = paint;
        if (notify) {
            this.notifyListeners(new RendererChangeEvent(this));
        }
    }
    
    public Paint getItemOutlinePaint(final int row, final int column) {
        return this.getSeriesOutlinePaint(row);
    }
    
    public Paint getSeriesOutlinePaint(final int series) {
        if (this.outlinePaint != null) {
            return this.outlinePaint;
        }
        Paint seriesOutlinePaint = this.outlinePaintList.getPaint(series);
        if (seriesOutlinePaint == null) {
            final DrawingSupplier supplier = this.getDrawingSupplier();
            if (supplier != null) {
                seriesOutlinePaint = supplier.getNextOutlinePaint();
                this.outlinePaintList.setPaint(series, seriesOutlinePaint);
            }
            else {
                seriesOutlinePaint = this.baseOutlinePaint;
            }
        }
        return seriesOutlinePaint;
    }
    
    public void setSeriesOutlinePaint(final int series, final Paint paint) {
        this.setSeriesOutlinePaint(series, paint, true);
    }
    
    public void setSeriesOutlinePaint(final int series, final Paint paint, final boolean notify) {
        this.outlinePaintList.setPaint(series, paint);
        if (notify) {
            this.notifyListeners(new RendererChangeEvent(this));
        }
    }
    
    public void setOutlinePaint(final Paint paint) {
        this.setOutlinePaint(paint, true);
    }
    
    public void setOutlinePaint(final Paint paint, final boolean notify) {
        this.outlinePaint = paint;
        if (notify) {
            this.notifyListeners(new RendererChangeEvent(this));
        }
    }
    
    public Paint getBaseOutlinePaint() {
        return this.baseOutlinePaint;
    }
    
    public void setBaseOutlinePaint(final Paint paint) {
        this.setBaseOutlinePaint(paint, true);
    }
    
    public void setBaseOutlinePaint(final Paint paint, final boolean notify) {
        if (paint == null) {
            throw new IllegalArgumentException("Null 'paint' argument.");
        }
        this.baseOutlinePaint = paint;
        if (notify) {
            this.notifyListeners(new RendererChangeEvent(this));
        }
    }
    
    public Stroke getItemStroke(final int row, final int column) {
        return this.getSeriesStroke(row);
    }
    
    public Stroke getSeriesStroke(final int series) {
        if (this.stroke != null) {
            return this.stroke;
        }
        Stroke result = this.strokeList.getStroke(series);
        if (result == null) {
            final DrawingSupplier supplier = this.getDrawingSupplier();
            if (supplier != null) {
                result = supplier.getNextStroke();
                this.strokeList.setStroke(series, result);
            }
            else {
                result = this.baseStroke;
            }
        }
        return result;
    }
    
    public void setStroke(final Stroke stroke) {
        this.setStroke(stroke, true);
    }
    
    public void setStroke(final Stroke stroke, final boolean notify) {
        this.stroke = stroke;
        if (notify) {
            this.notifyListeners(new RendererChangeEvent(this));
        }
    }
    
    public void setSeriesStroke(final int series, final Stroke stroke) {
        this.setSeriesStroke(series, stroke, true);
    }
    
    public void setSeriesStroke(final int series, final Stroke stroke, final boolean notify) {
        this.strokeList.setStroke(series, stroke);
        if (notify) {
            this.notifyListeners(new RendererChangeEvent(this));
        }
    }
    
    public Stroke getBaseStroke() {
        return this.baseStroke;
    }
    
    public void setBaseStroke(final Stroke stroke) {
        this.setBaseStroke(stroke, true);
    }
    
    public void setBaseStroke(final Stroke stroke, final boolean notify) {
        if (stroke == null) {
            throw new IllegalArgumentException("Null 'stroke' argument.");
        }
        this.baseStroke = stroke;
        if (notify) {
            this.notifyListeners(new RendererChangeEvent(this));
        }
    }
    
    public Stroke getItemOutlineStroke(final int row, final int column) {
        return this.getSeriesOutlineStroke(row);
    }
    
    public Stroke getSeriesOutlineStroke(final int series) {
        if (this.outlineStroke != null) {
            return this.outlineStroke;
        }
        Stroke result = this.outlineStrokeList.getStroke(series);
        if (result == null) {
            final DrawingSupplier supplier = this.getDrawingSupplier();
            if (supplier != null) {
                result = supplier.getNextOutlineStroke();
                this.outlineStrokeList.setStroke(series, result);
            }
            else {
                result = this.baseOutlineStroke;
            }
        }
        return result;
    }
    
    public void setOutlineStroke(final Stroke stroke) {
        this.setOutlineStroke(stroke, true);
    }
    
    public void setOutlineStroke(final Stroke stroke, final boolean notify) {
        this.outlineStroke = stroke;
        if (notify) {
            this.notifyListeners(new RendererChangeEvent(this));
        }
    }
    
    public void setSeriesOutlineStroke(final int series, final Stroke stroke) {
        this.setSeriesOutlineStroke(series, stroke, true);
    }
    
    public void setSeriesOutlineStroke(final int series, final Stroke stroke, final boolean notify) {
        this.outlineStrokeList.setStroke(series, stroke);
        if (notify) {
            this.notifyListeners(new RendererChangeEvent(this));
        }
    }
    
    public Stroke getBaseOutlineStroke() {
        return this.baseOutlineStroke;
    }
    
    public void setBaseOutlineStroke(final Stroke stroke) {
        this.setBaseOutlineStroke(stroke, true);
    }
    
    public void setBaseOutlineStroke(final Stroke stroke, final boolean notify) {
        if (stroke == null) {
            throw new IllegalArgumentException("Null 'stroke' argument.");
        }
        this.baseOutlineStroke = stroke;
        if (notify) {
            this.notifyListeners(new RendererChangeEvent(this));
        }
    }
    
    public Shape getItemShape(final int row, final int column) {
        return this.getSeriesShape(row);
    }
    
    public Shape getSeriesShape(final int series) {
        if (this.shape != null) {
            return this.shape;
        }
        Shape result = this.shapeList.getShape(series);
        if (result == null) {
            final DrawingSupplier supplier = this.getDrawingSupplier();
            if (supplier != null) {
                result = supplier.getNextShape();
                this.shapeList.setShape(series, result);
            }
            else {
                result = this.baseShape;
            }
        }
        return result;
    }
    
    public void setShape(final Shape shape) {
        this.setShape(shape, true);
    }
    
    public void setShape(final Shape shape, final boolean notify) {
        this.shape = shape;
        if (notify) {
            this.notifyListeners(new RendererChangeEvent(this));
        }
    }
    
    public void setSeriesShape(final int series, final Shape shape) {
        this.setSeriesShape(series, shape, true);
    }
    
    public void setSeriesShape(final int series, final Shape shape, final boolean notify) {
        this.shapeList.setShape(series, shape);
        if (notify) {
            this.notifyListeners(new RendererChangeEvent(this));
        }
    }
    
    public Shape getBaseShape() {
        return this.baseShape;
    }
    
    public void setBaseShape(final Shape shape) {
        this.setBaseShape(shape, true);
    }
    
    public void setBaseShape(final Shape shape, final boolean notify) {
        if (shape == null) {
            throw new IllegalArgumentException("Null 'shape' argument.");
        }
        this.baseShape = shape;
        if (notify) {
            this.notifyListeners(new RendererChangeEvent(this));
        }
    }
    
    public boolean isItemLabelVisible(final int row, final int column) {
        return this.isSeriesItemLabelsVisible(row);
    }
    
    public boolean isSeriesItemLabelsVisible(final int series) {
        if (this.itemLabelsVisible != null) {
            return this.itemLabelsVisible;
        }
        Boolean b = this.itemLabelsVisibleList.getBoolean(series);
        if (b == null) {
            b = this.baseItemLabelsVisible;
        }
        if (b == null) {
            b = Boolean.FALSE;
        }
        return b;
    }
    
    public void setItemLabelsVisible(final boolean visible) {
        this.setItemLabelsVisible(BooleanUtilities.valueOf(visible));
    }
    
    public void setItemLabelsVisible(final Boolean visible) {
        this.setItemLabelsVisible(visible, true);
    }
    
    public void setItemLabelsVisible(final Boolean visible, final boolean notify) {
        this.itemLabelsVisible = visible;
        if (notify) {
            this.notifyListeners(new RendererChangeEvent(this));
        }
    }
    
    public void setSeriesItemLabelsVisible(final int series, final boolean visible) {
        this.setSeriesItemLabelsVisible(series, BooleanUtilities.valueOf(visible));
    }
    
    public void setSeriesItemLabelsVisible(final int series, final Boolean visible) {
        this.setSeriesItemLabelsVisible(series, visible, true);
    }
    
    public void setSeriesItemLabelsVisible(final int series, final Boolean visible, final boolean notify) {
        this.itemLabelsVisibleList.setBoolean(series, visible);
        if (notify) {
            this.notifyListeners(new RendererChangeEvent(this));
        }
    }
    
    public Boolean getBaseItemLabelsVisible() {
        return this.baseItemLabelsVisible;
    }
    
    public void setBaseItemLabelsVisible(final boolean visible) {
        this.setBaseItemLabelsVisible(BooleanUtilities.valueOf(visible));
    }
    
    public void setBaseItemLabelsVisible(final Boolean visible) {
        this.setBaseItemLabelsVisible(visible, true);
    }
    
    public void setBaseItemLabelsVisible(final Boolean visible, final boolean notify) {
        this.baseItemLabelsVisible = visible;
        if (notify) {
            this.notifyListeners(new RendererChangeEvent(this));
        }
    }
    
    public Font getItemLabelFont(final int row, final int column) {
        Font result = this.itemLabelFont;
        if (result == null) {
            result = this.getSeriesItemLabelFont(row);
            if (result == null) {
                result = this.baseItemLabelFont;
            }
        }
        return result;
    }
    
    public Font getItemLabelFont() {
        return this.itemLabelFont;
    }
    
    public void setItemLabelFont(final Font font) {
        this.setItemLabelFont(font, true);
    }
    
    public void setItemLabelFont(final Font font, final boolean notify) {
        this.itemLabelFont = font;
        if (notify) {
            this.notifyListeners(new RendererChangeEvent(this));
        }
    }
    
    public Font getSeriesItemLabelFont(final int series) {
        return (Font)this.itemLabelFontList.get(series);
    }
    
    public void setSeriesItemLabelFont(final int series, final Font font) {
        this.setSeriesItemLabelFont(series, font, true);
    }
    
    public void setSeriesItemLabelFont(final int series, final Font font, final boolean notify) {
        this.itemLabelFontList.set(series, (Object)font);
        if (notify) {
            this.notifyListeners(new RendererChangeEvent(this));
        }
    }
    
    public Font getBaseItemLabelFont() {
        return this.baseItemLabelFont;
    }
    
    public void setBaseItemLabelFont(final Font font) {
        if (font == null) {
            throw new IllegalArgumentException("Null 'font' argument.");
        }
        this.setBaseItemLabelFont(font, true);
    }
    
    public void setBaseItemLabelFont(final Font font, final boolean notify) {
        this.baseItemLabelFont = font;
        if (notify) {
            this.notifyListeners(new RendererChangeEvent(this));
        }
    }
    
    public Paint getItemLabelPaint(final int row, final int column) {
        Paint result = this.itemLabelPaint;
        if (result == null) {
            result = this.getSeriesItemLabelPaint(row);
            if (result == null) {
                result = this.baseItemLabelPaint;
            }
        }
        return result;
    }
    
    public Paint getItemLabelPaint() {
        return this.itemLabelPaint;
    }
    
    public void setItemLabelPaint(final Paint paint) {
        this.setItemLabelPaint(paint, true);
    }
    
    public void setItemLabelPaint(final Paint paint, final boolean notify) {
        this.itemLabelPaint = paint;
        if (notify) {
            this.notifyListeners(new RendererChangeEvent(this));
        }
    }
    
    public Paint getSeriesItemLabelPaint(final int series) {
        return this.itemLabelPaintList.getPaint(series);
    }
    
    public void setSeriesItemLabelPaint(final int series, final Paint paint) {
        this.setSeriesItemLabelPaint(series, paint, true);
    }
    
    public void setSeriesItemLabelPaint(final int series, final Paint paint, final boolean notify) {
        this.itemLabelPaintList.setPaint(series, paint);
        if (notify) {
            this.notifyListeners(new RendererChangeEvent(this));
        }
    }
    
    public Paint getBaseItemLabelPaint() {
        return this.baseItemLabelPaint;
    }
    
    public void setBaseItemLabelPaint(final Paint paint) {
        this.setBaseItemLabelPaint(paint, true);
    }
    
    public void setBaseItemLabelPaint(final Paint paint, final boolean notify) {
        if (paint == null) {
            throw new IllegalArgumentException("Null 'paint' argument.");
        }
        this.baseItemLabelPaint = paint;
        if (notify) {
            this.notifyListeners(new RendererChangeEvent(this));
        }
    }
    
    public ItemLabelPosition getPositiveItemLabelPosition(final int row, final int column) {
        return this.getSeriesPositiveItemLabelPosition(row);
    }
    
    public ItemLabelPosition getPositiveItemLabelPosition() {
        return this.positiveItemLabelPosition;
    }
    
    public void setPositiveItemLabelPosition(final ItemLabelPosition position) {
        this.setPositiveItemLabelPosition(position, true);
    }
    
    public void setPositiveItemLabelPosition(final ItemLabelPosition position, final boolean notify) {
        this.positiveItemLabelPosition = position;
        if (notify) {
            this.notifyListeners(new RendererChangeEvent(this));
        }
    }
    
    public ItemLabelPosition getSeriesPositiveItemLabelPosition(final int series) {
        if (this.positiveItemLabelPosition != null) {
            return this.positiveItemLabelPosition;
        }
        ItemLabelPosition position = (ItemLabelPosition)this.positiveItemLabelPositionList.get(series);
        if (position == null) {
            position = this.basePositiveItemLabelPosition;
        }
        return position;
    }
    
    public void setSeriesPositiveItemLabelPosition(final int series, final ItemLabelPosition position) {
        this.setSeriesPositiveItemLabelPosition(series, position, true);
    }
    
    public void setSeriesPositiveItemLabelPosition(final int series, final ItemLabelPosition position, final boolean notify) {
        this.positiveItemLabelPositionList.set(series, (Object)position);
        if (notify) {
            this.notifyListeners(new RendererChangeEvent(this));
        }
    }
    
    public ItemLabelPosition getBasePositiveItemLabelPosition() {
        return this.basePositiveItemLabelPosition;
    }
    
    public void setBasePositiveItemLabelPosition(final ItemLabelPosition position) {
        this.setBasePositiveItemLabelPosition(position, true);
    }
    
    public void setBasePositiveItemLabelPosition(final ItemLabelPosition position, final boolean notify) {
        if (position == null) {
            throw new IllegalArgumentException("Null 'position' argument.");
        }
        this.basePositiveItemLabelPosition = position;
        if (notify) {
            this.notifyListeners(new RendererChangeEvent(this));
        }
    }
    
    public ItemLabelPosition getNegativeItemLabelPosition(final int row, final int column) {
        return this.getSeriesNegativeItemLabelPosition(row);
    }
    
    public ItemLabelPosition getNegativeItemLabelPosition() {
        return this.negativeItemLabelPosition;
    }
    
    public void setNegativeItemLabelPosition(final ItemLabelPosition position) {
        this.setNegativeItemLabelPosition(position, true);
    }
    
    public void setNegativeItemLabelPosition(final ItemLabelPosition position, final boolean notify) {
        this.negativeItemLabelPosition = position;
        if (notify) {
            this.notifyListeners(new RendererChangeEvent(this));
        }
    }
    
    public ItemLabelPosition getSeriesNegativeItemLabelPosition(final int series) {
        if (this.negativeItemLabelPosition != null) {
            return this.negativeItemLabelPosition;
        }
        ItemLabelPosition position = (ItemLabelPosition)this.negativeItemLabelPositionList.get(series);
        if (position == null) {
            position = this.baseNegativeItemLabelPosition;
        }
        return position;
    }
    
    public void setSeriesNegativeItemLabelPosition(final int series, final ItemLabelPosition position) {
        this.setSeriesNegativeItemLabelPosition(series, position, true);
    }
    
    public void setSeriesNegativeItemLabelPosition(final int series, final ItemLabelPosition position, final boolean notify) {
        this.negativeItemLabelPositionList.set(series, (Object)position);
        if (notify) {
            this.notifyListeners(new RendererChangeEvent(this));
        }
    }
    
    public ItemLabelPosition getBaseNegativeItemLabelPosition() {
        return this.baseNegativeItemLabelPosition;
    }
    
    public void setBaseNegativeItemLabelPosition(final ItemLabelPosition position) {
        this.setBaseNegativeItemLabelPosition(position, true);
    }
    
    public void setBaseNegativeItemLabelPosition(final ItemLabelPosition position, final boolean notify) {
        if (position == null) {
            throw new IllegalArgumentException("Null 'position' argument.");
        }
        this.baseNegativeItemLabelPosition = position;
        if (notify) {
            this.notifyListeners(new RendererChangeEvent(this));
        }
    }
    
    public double getItemLabelAnchorOffset() {
        return this.itemLabelAnchorOffset;
    }
    
    public void setItemLabelAnchorOffset(final double offset) {
        this.itemLabelAnchorOffset = offset;
        this.notifyListeners(new RendererChangeEvent(this));
    }
    
    public boolean getItemCreateEntity(final int series, final int item) {
        if (this.createEntities != null) {
            return this.createEntities;
        }
        final Boolean b = this.getSeriesCreateEntities(series);
        if (b != null) {
            return b;
        }
        return this.baseCreateEntities;
    }
    
    public Boolean getCreateEntities() {
        return this.createEntities;
    }
    
    public void setCreateEntities(final Boolean create) {
        this.setCreateEntities(create, true);
    }
    
    public void setCreateEntities(final Boolean create, final boolean notify) {
        this.createEntities = create;
        if (notify) {
            this.notifyListeners(new RendererChangeEvent(this));
        }
    }
    
    public Boolean getSeriesCreateEntities(final int series) {
        return this.createEntitiesList.getBoolean(series);
    }
    
    public void setSeriesCreateEntities(final int series, final Boolean create) {
        this.setSeriesCreateEntities(series, create, true);
    }
    
    public void setSeriesCreateEntities(final int series, final Boolean create, final boolean notify) {
        this.createEntitiesList.setBoolean(series, create);
        if (notify) {
            this.notifyListeners(new RendererChangeEvent(this));
        }
    }
    
    public boolean getBaseCreateEntities() {
        return this.baseCreateEntities;
    }
    
    public void setBaseCreateEntities(final boolean create) {
        this.setBaseCreateEntities(create, true);
    }
    
    public void setBaseCreateEntities(final boolean create, final boolean notify) {
        this.baseCreateEntities = create;
        if (notify) {
            this.notifyListeners(new RendererChangeEvent(this));
        }
    }
    
    protected Point2D calculateLabelAnchorPoint(final ItemLabelAnchor anchor, final double x, final double y, final PlotOrientation orientation) {
        Point2D result = null;
        if (anchor == ItemLabelAnchor.CENTER) {
            result = new Point2D.Double(x, y);
        }
        else if (anchor == ItemLabelAnchor.INSIDE1) {
            result = new Point2D.Double(x + AbstractRenderer.OPP * this.itemLabelAnchorOffset, y - AbstractRenderer.ADJ * this.itemLabelAnchorOffset);
        }
        else if (anchor == ItemLabelAnchor.INSIDE2) {
            result = new Point2D.Double(x + AbstractRenderer.ADJ * this.itemLabelAnchorOffset, y - AbstractRenderer.OPP * this.itemLabelAnchorOffset);
        }
        else if (anchor == ItemLabelAnchor.INSIDE3) {
            result = new Point2D.Double(x + this.itemLabelAnchorOffset, y);
        }
        else if (anchor == ItemLabelAnchor.INSIDE4) {
            result = new Point2D.Double(x + AbstractRenderer.ADJ * this.itemLabelAnchorOffset, y + AbstractRenderer.OPP * this.itemLabelAnchorOffset);
        }
        else if (anchor == ItemLabelAnchor.INSIDE5) {
            result = new Point2D.Double(x + AbstractRenderer.OPP * this.itemLabelAnchorOffset, y + AbstractRenderer.ADJ * this.itemLabelAnchorOffset);
        }
        else if (anchor == ItemLabelAnchor.INSIDE6) {
            result = new Point2D.Double(x, y + this.itemLabelAnchorOffset);
        }
        else if (anchor == ItemLabelAnchor.INSIDE7) {
            result = new Point2D.Double(x - AbstractRenderer.OPP * this.itemLabelAnchorOffset, y + AbstractRenderer.ADJ * this.itemLabelAnchorOffset);
        }
        else if (anchor == ItemLabelAnchor.INSIDE8) {
            result = new Point2D.Double(x - AbstractRenderer.ADJ * this.itemLabelAnchorOffset, y + AbstractRenderer.OPP * this.itemLabelAnchorOffset);
        }
        else if (anchor == ItemLabelAnchor.INSIDE9) {
            result = new Point2D.Double(x - this.itemLabelAnchorOffset, y);
        }
        else if (anchor == ItemLabelAnchor.INSIDE10) {
            result = new Point2D.Double(x - AbstractRenderer.ADJ * this.itemLabelAnchorOffset, y - AbstractRenderer.OPP * this.itemLabelAnchorOffset);
        }
        else if (anchor == ItemLabelAnchor.INSIDE11) {
            result = new Point2D.Double(x - AbstractRenderer.OPP * this.itemLabelAnchorOffset, y - AbstractRenderer.ADJ * this.itemLabelAnchorOffset);
        }
        else if (anchor == ItemLabelAnchor.INSIDE12) {
            result = new Point2D.Double(x, y - this.itemLabelAnchorOffset);
        }
        else if (anchor == ItemLabelAnchor.OUTSIDE1) {
            result = new Point2D.Double(x + 2.0 * AbstractRenderer.OPP * this.itemLabelAnchorOffset, y - 2.0 * AbstractRenderer.ADJ * this.itemLabelAnchorOffset);
        }
        else if (anchor == ItemLabelAnchor.OUTSIDE2) {
            result = new Point2D.Double(x + 2.0 * AbstractRenderer.ADJ * this.itemLabelAnchorOffset, y - 2.0 * AbstractRenderer.OPP * this.itemLabelAnchorOffset);
        }
        else if (anchor == ItemLabelAnchor.OUTSIDE3) {
            result = new Point2D.Double(x + 2.0 * this.itemLabelAnchorOffset, y);
        }
        else if (anchor == ItemLabelAnchor.OUTSIDE4) {
            result = new Point2D.Double(x + 2.0 * AbstractRenderer.ADJ * this.itemLabelAnchorOffset, y + 2.0 * AbstractRenderer.OPP * this.itemLabelAnchorOffset);
        }
        else if (anchor == ItemLabelAnchor.OUTSIDE5) {
            result = new Point2D.Double(x + 2.0 * AbstractRenderer.OPP * this.itemLabelAnchorOffset, y + 2.0 * AbstractRenderer.ADJ * this.itemLabelAnchorOffset);
        }
        else if (anchor == ItemLabelAnchor.OUTSIDE6) {
            result = new Point2D.Double(x, y + 2.0 * this.itemLabelAnchorOffset);
        }
        else if (anchor == ItemLabelAnchor.OUTSIDE7) {
            result = new Point2D.Double(x - 2.0 * AbstractRenderer.OPP * this.itemLabelAnchorOffset, y + 2.0 * AbstractRenderer.ADJ * this.itemLabelAnchorOffset);
        }
        else if (anchor == ItemLabelAnchor.OUTSIDE8) {
            result = new Point2D.Double(x - 2.0 * AbstractRenderer.ADJ * this.itemLabelAnchorOffset, y + 2.0 * AbstractRenderer.OPP * this.itemLabelAnchorOffset);
        }
        else if (anchor == ItemLabelAnchor.OUTSIDE9) {
            result = new Point2D.Double(x - 2.0 * this.itemLabelAnchorOffset, y);
        }
        else if (anchor == ItemLabelAnchor.OUTSIDE10) {
            result = new Point2D.Double(x - 2.0 * AbstractRenderer.ADJ * this.itemLabelAnchorOffset, y - 2.0 * AbstractRenderer.OPP * this.itemLabelAnchorOffset);
        }
        else if (anchor == ItemLabelAnchor.OUTSIDE11) {
            result = new Point2D.Double(x - 2.0 * AbstractRenderer.OPP * this.itemLabelAnchorOffset, y - 2.0 * AbstractRenderer.ADJ * this.itemLabelAnchorOffset);
        }
        else if (anchor == ItemLabelAnchor.OUTSIDE12) {
            result = new Point2D.Double(x, y - 2.0 * this.itemLabelAnchorOffset);
        }
        return result;
    }
    
    public void addChangeListener(final RendererChangeListener listener) {
        if (listener == null) {
            throw new IllegalArgumentException("Null 'listener' argument.");
        }
        this.listenerList.add(RendererChangeListener.class, listener);
    }
    
    public void removeChangeListener(final RendererChangeListener listener) {
        if (listener == null) {
            throw new IllegalArgumentException("Null 'listener' argument.");
        }
        this.listenerList.remove(RendererChangeListener.class, listener);
    }
    
    public boolean hasListener(final EventListener listener) {
        final List list = Arrays.asList(this.listenerList.getListenerList());
        return list.contains(listener);
    }
    
    public void notifyListeners(final RendererChangeEvent event) {
        final Object[] ls = this.listenerList.getListenerList();
        for (int i = ls.length - 2; i >= 0; i -= 2) {
            if (ls[i] == RendererChangeListener.class) {
                ((RendererChangeListener)ls[i + 1]).rendererChanged(event);
            }
        }
    }
    
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof AbstractRenderer)) {
            return false;
        }
        final AbstractRenderer that = (AbstractRenderer)obj;
        return ObjectUtilities.equal((Object)this.seriesVisible, (Object)that.seriesVisible) && this.seriesVisibleList.equals((Object)that.seriesVisibleList) && this.baseSeriesVisible == that.baseSeriesVisible && ObjectUtilities.equal((Object)this.seriesVisibleInLegend, (Object)that.seriesVisibleInLegend) && this.seriesVisibleInLegendList.equals((Object)that.seriesVisibleInLegendList) && this.baseSeriesVisibleInLegend == that.baseSeriesVisibleInLegend && PaintUtilities.equal(this.paint, that.paint) && ObjectUtilities.equal((Object)this.paintList, (Object)that.paintList) && PaintUtilities.equal(this.basePaint, that.basePaint) && PaintUtilities.equal(this.fillPaint, that.fillPaint) && ObjectUtilities.equal((Object)this.fillPaintList, (Object)that.fillPaintList) && PaintUtilities.equal(this.baseFillPaint, that.baseFillPaint) && PaintUtilities.equal(this.outlinePaint, that.outlinePaint) && ObjectUtilities.equal((Object)this.outlinePaintList, (Object)that.outlinePaintList) && PaintUtilities.equal(this.baseOutlinePaint, that.baseOutlinePaint) && ObjectUtilities.equal((Object)this.stroke, (Object)that.stroke) && ObjectUtilities.equal((Object)this.strokeList, (Object)that.strokeList) && ObjectUtilities.equal((Object)this.baseStroke, (Object)that.baseStroke) && ObjectUtilities.equal((Object)this.outlineStroke, (Object)that.outlineStroke) && ObjectUtilities.equal((Object)this.outlineStrokeList, (Object)that.outlineStrokeList) && ObjectUtilities.equal((Object)this.baseOutlineStroke, (Object)that.baseOutlineStroke) && ObjectUtilities.equal((Object)this.shape, (Object)that.shape) && ObjectUtilities.equal((Object)this.shapeList, (Object)that.shapeList) && ObjectUtilities.equal((Object)this.baseShape, (Object)that.baseShape) && ObjectUtilities.equal((Object)this.itemLabelsVisible, (Object)that.itemLabelsVisible) && ObjectUtilities.equal((Object)this.itemLabelsVisibleList, (Object)that.itemLabelsVisibleList) && ObjectUtilities.equal((Object)this.baseItemLabelsVisible, (Object)that.baseItemLabelsVisible) && ObjectUtilities.equal((Object)this.itemLabelFont, (Object)that.itemLabelFont) && ObjectUtilities.equal((Object)this.itemLabelFontList, (Object)that.itemLabelFontList) && ObjectUtilities.equal((Object)this.baseItemLabelFont, (Object)that.baseItemLabelFont) && PaintUtilities.equal(this.itemLabelPaint, that.itemLabelPaint) && ObjectUtilities.equal((Object)this.itemLabelPaintList, (Object)that.itemLabelPaintList) && PaintUtilities.equal(this.baseItemLabelPaint, that.baseItemLabelPaint) && ObjectUtilities.equal((Object)this.positiveItemLabelPosition, (Object)that.positiveItemLabelPosition) && ObjectUtilities.equal((Object)this.positiveItemLabelPositionList, (Object)that.positiveItemLabelPositionList) && ObjectUtilities.equal((Object)this.basePositiveItemLabelPosition, (Object)that.basePositiveItemLabelPosition) && ObjectUtilities.equal((Object)this.negativeItemLabelPosition, (Object)that.negativeItemLabelPosition) && ObjectUtilities.equal((Object)this.negativeItemLabelPositionList, (Object)that.negativeItemLabelPositionList) && ObjectUtilities.equal((Object)this.baseNegativeItemLabelPosition, (Object)that.baseNegativeItemLabelPosition) && this.itemLabelAnchorOffset == that.itemLabelAnchorOffset && ObjectUtilities.equal((Object)this.createEntities, (Object)that.createEntities) && ObjectUtilities.equal((Object)this.createEntitiesList, (Object)that.createEntitiesList) && this.baseCreateEntities == that.baseCreateEntities;
    }
    
    public int hashCode() {
        int result = 193;
        result = 37 * result + ObjectUtilities.hashCode((Object)this.stroke);
        result = 37 * result + ObjectUtilities.hashCode((Object)this.baseStroke);
        result = 37 * result + ObjectUtilities.hashCode((Object)this.outlineStroke);
        result = 37 * result + ObjectUtilities.hashCode((Object)this.baseOutlineStroke);
        return result;
    }
    
    protected Object clone() throws CloneNotSupportedException {
        final AbstractRenderer clone = (AbstractRenderer)super.clone();
        if (this.paintList != null) {
            clone.paintList = (PaintList)this.paintList.clone();
        }
        if (this.fillPaintList != null) {
            clone.fillPaintList = (PaintList)this.fillPaintList.clone();
        }
        if (this.outlinePaintList != null) {
            clone.outlinePaintList = (PaintList)this.outlinePaintList.clone();
        }
        if (this.strokeList != null) {
            clone.strokeList = (StrokeList)this.strokeList.clone();
        }
        if (this.outlineStrokeList != null) {
            clone.outlineStrokeList = (StrokeList)this.outlineStrokeList.clone();
        }
        if (this.shape != null) {
            clone.shape = ShapeUtilities.clone(this.shape);
        }
        if (this.baseShape != null) {
            clone.baseShape = ShapeUtilities.clone(this.baseShape);
        }
        if (this.itemLabelsVisibleList != null) {
            clone.itemLabelsVisibleList = (BooleanList)this.itemLabelsVisibleList.clone();
        }
        if (this.itemLabelFontList != null) {
            clone.itemLabelFontList = (ObjectList)this.itemLabelFontList.clone();
        }
        if (this.itemLabelPaintList != null) {
            clone.itemLabelPaintList = (PaintList)this.itemLabelPaintList.clone();
        }
        if (this.positiveItemLabelPositionList != null) {
            clone.positiveItemLabelPositionList = (ObjectList)this.positiveItemLabelPositionList.clone();
        }
        if (this.negativeItemLabelPositionList != null) {
            clone.negativeItemLabelPositionList = (ObjectList)this.negativeItemLabelPositionList.clone();
        }
        return clone;
    }
    
    private void writeObject(final ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        SerialUtilities.writePaint(this.paint, stream);
        SerialUtilities.writePaint(this.basePaint, stream);
        SerialUtilities.writePaint(this.fillPaint, stream);
        SerialUtilities.writePaint(this.baseFillPaint, stream);
        SerialUtilities.writePaint(this.outlinePaint, stream);
        SerialUtilities.writePaint(this.baseOutlinePaint, stream);
        SerialUtilities.writeStroke(this.stroke, stream);
        SerialUtilities.writeStroke(this.baseStroke, stream);
        SerialUtilities.writeStroke(this.outlineStroke, stream);
        SerialUtilities.writeStroke(this.baseOutlineStroke, stream);
        SerialUtilities.writeShape(this.shape, stream);
        SerialUtilities.writeShape(this.baseShape, stream);
        SerialUtilities.writePaint(this.itemLabelPaint, stream);
        SerialUtilities.writePaint(this.baseItemLabelPaint, stream);
    }
    
    private void readObject(final ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        this.paint = SerialUtilities.readPaint(stream);
        this.basePaint = SerialUtilities.readPaint(stream);
        this.fillPaint = SerialUtilities.readPaint(stream);
        this.baseFillPaint = SerialUtilities.readPaint(stream);
        this.outlinePaint = SerialUtilities.readPaint(stream);
        this.baseOutlinePaint = SerialUtilities.readPaint(stream);
        this.stroke = SerialUtilities.readStroke(stream);
        this.baseStroke = SerialUtilities.readStroke(stream);
        this.outlineStroke = SerialUtilities.readStroke(stream);
        this.baseOutlineStroke = SerialUtilities.readStroke(stream);
        this.shape = SerialUtilities.readShape(stream);
        this.baseShape = SerialUtilities.readShape(stream);
        this.itemLabelPaint = SerialUtilities.readPaint(stream);
        this.baseItemLabelPaint = SerialUtilities.readPaint(stream);
        this.listenerList = new EventListenerList();
    }
    
    static {
        ZERO = new Double(0.0);
        DEFAULT_PAINT = Color.blue;
        DEFAULT_OUTLINE_PAINT = Color.gray;
        DEFAULT_STROKE = new BasicStroke(1.0f);
        DEFAULT_OUTLINE_STROKE = new BasicStroke(1.0f);
        DEFAULT_SHAPE = new Rectangle2D.Double(-3.0, -3.0, 6.0, 6.0);
        DEFAULT_VALUE_LABEL_FONT = new Font("SansSerif", 0, 10);
        DEFAULT_VALUE_LABEL_PAINT = Color.black;
        ADJ = Math.cos(0.5235987755982988);
        OPP = Math.sin(0.5235987755982988);
    }
}
