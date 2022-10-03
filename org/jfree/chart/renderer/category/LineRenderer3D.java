package org.jfree.chart.renderer.category;

import org.jfree.chart.entity.EntityCollection;
import org.jfree.util.ShapeUtilities;
import org.jfree.data.category.CategoryDataset;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.Marker;
import org.jfree.data.Range;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.PlotOrientation;
import java.awt.Stroke;
import java.awt.Image;
import java.awt.image.ImageObserver;
import java.awt.Composite;
import java.awt.AlphaComposite;
import java.awt.geom.Line2D;
import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import org.jfree.chart.plot.CategoryPlot;
import java.awt.Graphics2D;
import org.jfree.chart.event.RendererChangeEvent;
import java.awt.Paint;
import java.io.Serializable;
import org.jfree.chart.Effect3D;

public class LineRenderer3D extends LineAndShapeRenderer implements Effect3D, Serializable
{
    private static final long serialVersionUID = 5467931468380928736L;
    public static final double DEFAULT_X_OFFSET = 12.0;
    public static final double DEFAULT_Y_OFFSET = 8.0;
    public static final Paint DEFAULT_WALL_PAINT;
    private double xOffset;
    private double yOffset;
    private transient Paint wallPaint;
    
    public LineRenderer3D() {
        super(true, false);
        this.xOffset = 12.0;
        this.yOffset = 8.0;
        this.wallPaint = LineRenderer3D.DEFAULT_WALL_PAINT;
    }
    
    public double getXOffset() {
        return this.xOffset;
    }
    
    public double getYOffset() {
        return this.yOffset;
    }
    
    public void setXOffset(final double xOffset) {
        this.xOffset = xOffset;
        this.notifyListeners(new RendererChangeEvent(this));
    }
    
    public void setYOffset(final double yOffset) {
        this.yOffset = yOffset;
        this.notifyListeners(new RendererChangeEvent(this));
    }
    
    public Paint getWallPaint() {
        return this.wallPaint;
    }
    
    public void setWallPaint(final Paint paint) {
        this.wallPaint = paint;
        this.notifyListeners(new RendererChangeEvent(this));
    }
    
    public void drawBackground(final Graphics2D g2, final CategoryPlot plot, final Rectangle2D dataArea) {
        final float x0 = (float)dataArea.getX();
        final float x2 = x0 + (float)Math.abs(this.xOffset);
        final float x3 = (float)dataArea.getMaxX();
        final float x4 = x3 - (float)Math.abs(this.xOffset);
        final float y0 = (float)dataArea.getMaxY();
        final float y2 = y0 - (float)Math.abs(this.yOffset);
        final float y3 = (float)dataArea.getMinY();
        final float y4 = y3 + (float)Math.abs(this.yOffset);
        final GeneralPath clip = new GeneralPath();
        clip.moveTo(x0, y0);
        clip.lineTo(x0, y4);
        clip.lineTo(x2, y3);
        clip.lineTo(x3, y3);
        clip.lineTo(x3, y2);
        clip.lineTo(x4, y0);
        clip.closePath();
        final Paint backgroundPaint = plot.getBackgroundPaint();
        if (backgroundPaint != null) {
            g2.setPaint(backgroundPaint);
            g2.fill(clip);
        }
        final GeneralPath leftWall = new GeneralPath();
        leftWall.moveTo(x0, y0);
        leftWall.lineTo(x0, y4);
        leftWall.lineTo(x2, y3);
        leftWall.lineTo(x2, y2);
        leftWall.closePath();
        g2.setPaint(this.getWallPaint());
        g2.fill(leftWall);
        final GeneralPath bottomWall = new GeneralPath();
        bottomWall.moveTo(x0, y0);
        bottomWall.lineTo(x2, y2);
        bottomWall.lineTo(x3, y2);
        bottomWall.lineTo(x4, y0);
        bottomWall.closePath();
        g2.setPaint(this.getWallPaint());
        g2.fill(bottomWall);
        g2.setPaint(Color.lightGray);
        final Line2D corner = new Line2D.Double(x0, y0, x2, y2);
        g2.draw(corner);
        corner.setLine(x2, y2, x2, y3);
        g2.draw(corner);
        corner.setLine(x2, y2, x3, y2);
        g2.draw(corner);
        final Image backgroundImage = plot.getBackgroundImage();
        if (backgroundImage != null) {
            final Composite originalComposite = g2.getComposite();
            g2.setComposite(AlphaComposite.getInstance(2, plot.getBackgroundAlpha()));
            g2.drawImage(backgroundImage, (int)x2, (int)y3, (int)(x3 - x2 + 1.0f), (int)(y2 - y3 + 1.0f), null);
            g2.setComposite(originalComposite);
        }
    }
    
    public void drawOutline(final Graphics2D g2, final CategoryPlot plot, final Rectangle2D dataArea) {
        final float x0 = (float)dataArea.getX();
        final float x2 = x0 + (float)Math.abs(this.xOffset);
        final float x3 = (float)dataArea.getMaxX();
        final float x4 = x3 - (float)Math.abs(this.xOffset);
        final float y0 = (float)dataArea.getMaxY();
        final float y2 = y0 - (float)Math.abs(this.yOffset);
        final float y3 = (float)dataArea.getMinY();
        final float y4 = y3 + (float)Math.abs(this.yOffset);
        final GeneralPath clip = new GeneralPath();
        clip.moveTo(x0, y0);
        clip.lineTo(x0, y4);
        clip.lineTo(x2, y3);
        clip.lineTo(x3, y3);
        clip.lineTo(x3, y2);
        clip.lineTo(x4, y0);
        clip.closePath();
        final Stroke outlineStroke = plot.getOutlineStroke();
        final Paint outlinePaint = plot.getOutlinePaint();
        if (outlineStroke != null && outlinePaint != null) {
            g2.setStroke(outlineStroke);
            g2.setPaint(outlinePaint);
            g2.draw(clip);
        }
    }
    
    public void drawDomainGridline(final Graphics2D g2, final CategoryPlot plot, final Rectangle2D dataArea, final double value) {
        Line2D line1 = null;
        Line2D line2 = null;
        final PlotOrientation orientation = plot.getOrientation();
        if (orientation == PlotOrientation.HORIZONTAL) {
            final double y0 = value;
            final double y2 = value - this.getYOffset();
            final double x0 = dataArea.getMinX();
            final double x2 = x0 + this.getXOffset();
            final double x3 = dataArea.getMaxY();
            line1 = new Line2D.Double(x0, y0, x2, y2);
            line2 = new Line2D.Double(x2, y2, x3, y2);
        }
        else if (orientation == PlotOrientation.VERTICAL) {
            final double x4 = value;
            final double x5 = value + this.getXOffset();
            final double y3 = dataArea.getMaxY();
            final double y4 = y3 - this.getYOffset();
            final double y5 = dataArea.getMinY();
            line1 = new Line2D.Double(x4, y3, x5, y4);
            line2 = new Line2D.Double(x5, y4, x5, y5);
        }
        g2.setPaint(plot.getDomainGridlinePaint());
        g2.setStroke(plot.getDomainGridlineStroke());
        g2.draw(line1);
        g2.draw(line2);
    }
    
    public void drawRangeGridline(final Graphics2D g2, final CategoryPlot plot, final ValueAxis axis, final Rectangle2D dataArea, final double value) {
        final Range range = axis.getRange();
        if (!range.contains(value)) {
            return;
        }
        final Rectangle2D adjusted = new Rectangle2D.Double(dataArea.getX(), dataArea.getY() + this.getYOffset(), dataArea.getWidth() - this.getXOffset(), dataArea.getHeight() - this.getYOffset());
        Line2D line1 = null;
        Line2D line2 = null;
        final PlotOrientation orientation = plot.getOrientation();
        if (orientation == PlotOrientation.HORIZONTAL) {
            final double x0 = axis.valueToJava2D(value, adjusted, plot.getRangeAxisEdge());
            final double x2 = x0 + this.getXOffset();
            final double y0 = dataArea.getMaxY();
            final double y2 = y0 - this.getYOffset();
            final double y3 = dataArea.getMinY();
            line1 = new Line2D.Double(x0, y0, x2, y2);
            line2 = new Line2D.Double(x2, y2, x2, y3);
        }
        else if (orientation == PlotOrientation.VERTICAL) {
            final double y4 = axis.valueToJava2D(value, adjusted, plot.getRangeAxisEdge());
            final double y5 = y4 - this.getYOffset();
            final double x3 = dataArea.getMinX();
            final double x4 = x3 + this.getXOffset();
            final double x5 = dataArea.getMaxX();
            line1 = new Line2D.Double(x3, y4, x4, y5);
            line2 = new Line2D.Double(x4, y5, x5, y5);
        }
        g2.setPaint(plot.getRangeGridlinePaint());
        g2.setStroke(plot.getRangeGridlineStroke());
        g2.draw(line1);
        g2.draw(line2);
    }
    
    public void drawRangeMarker(final Graphics2D g2, final CategoryPlot plot, final ValueAxis axis, final Marker marker, final Rectangle2D dataArea) {
        if (marker instanceof ValueMarker) {
            final ValueMarker vm = (ValueMarker)marker;
            final double value = vm.getValue();
            final Range range = axis.getRange();
            if (!range.contains(value)) {
                return;
            }
            final Rectangle2D adjusted = new Rectangle2D.Double(dataArea.getX(), dataArea.getY() + this.getYOffset(), dataArea.getWidth() - this.getXOffset(), dataArea.getHeight() - this.getYOffset());
            GeneralPath path = null;
            final PlotOrientation orientation = plot.getOrientation();
            if (orientation == PlotOrientation.HORIZONTAL) {
                final float x = (float)axis.valueToJava2D(value, adjusted, plot.getRangeAxisEdge());
                final float y = (float)adjusted.getMaxY();
                path = new GeneralPath();
                path.moveTo(x, y);
                path.lineTo((float)(x + this.getXOffset()), y - (float)this.getYOffset());
                path.lineTo((float)(x + this.getXOffset()), (float)(adjusted.getMinY() - this.getYOffset()));
                path.lineTo(x, (float)adjusted.getMinY());
                path.closePath();
            }
            else if (orientation == PlotOrientation.VERTICAL) {
                final float y2 = (float)axis.valueToJava2D(value, adjusted, plot.getRangeAxisEdge());
                final float x2 = (float)dataArea.getX();
                path = new GeneralPath();
                path.moveTo(x2, y2);
                path.lineTo(x2 + (float)this.xOffset, y2 - (float)this.yOffset);
                path.lineTo((float)(adjusted.getMaxX() + this.xOffset), y2 - (float)this.yOffset);
                path.lineTo((float)adjusted.getMaxX(), y2);
                path.closePath();
            }
            g2.setPaint(marker.getPaint());
            g2.fill(path);
            g2.setPaint(marker.getOutlinePaint());
            g2.draw(path);
        }
    }
    
    public void drawItem(final Graphics2D g2, final CategoryItemRendererState state, final Rectangle2D dataArea, final CategoryPlot plot, final CategoryAxis domainAxis, final ValueAxis rangeAxis, final CategoryDataset dataset, final int row, final int column, final int pass) {
        if (!this.getItemVisible(row, column)) {
            return;
        }
        final Number v = dataset.getValue(row, column);
        if (v == null) {
            return;
        }
        final Rectangle2D adjusted = new Rectangle2D.Double(dataArea.getX(), dataArea.getY() + this.getYOffset(), dataArea.getWidth() - this.getXOffset(), dataArea.getHeight() - this.getYOffset());
        final PlotOrientation orientation = plot.getOrientation();
        final double x1 = domainAxis.getCategoryMiddle(column, this.getColumnCount(), adjusted, plot.getDomainAxisEdge());
        final double value = v.doubleValue();
        final double y1 = rangeAxis.valueToJava2D(value, adjusted, plot.getRangeAxisEdge());
        Shape shape = this.getItemShape(row, column);
        if (orientation == PlotOrientation.HORIZONTAL) {
            shape = ShapeUtilities.createTranslatedShape(shape, y1, x1);
        }
        else if (orientation == PlotOrientation.VERTICAL) {
            shape = ShapeUtilities.createTranslatedShape(shape, x1, y1);
        }
        if (this.getItemLineVisible(row, column) && column != 0) {
            final Number previousValue = dataset.getValue(row, column - 1);
            if (previousValue != null) {
                final double previous = previousValue.doubleValue();
                final double x2 = domainAxis.getCategoryMiddle(column - 1, this.getColumnCount(), adjusted, plot.getDomainAxisEdge());
                final double y2 = rangeAxis.valueToJava2D(previous, adjusted, plot.getRangeAxisEdge());
                final double x3 = x2 + this.getXOffset();
                final double y3 = y2 - this.getYOffset();
                final double x4 = x1 + this.getXOffset();
                final double y4 = y1 - this.getYOffset();
                final GeneralPath clip = new GeneralPath();
                if (orientation == PlotOrientation.HORIZONTAL) {
                    clip.moveTo((float)y2, (float)x2);
                    clip.lineTo((float)y1, (float)x1);
                    clip.lineTo((float)y4, (float)x4);
                    clip.lineTo((float)y3, (float)x3);
                    clip.lineTo((float)y2, (float)x2);
                    clip.closePath();
                }
                else if (orientation == PlotOrientation.VERTICAL) {
                    clip.moveTo((float)x2, (float)y2);
                    clip.lineTo((float)x1, (float)y1);
                    clip.lineTo((float)x4, (float)y4);
                    clip.lineTo((float)x3, (float)y3);
                    clip.lineTo((float)x2, (float)y2);
                    clip.closePath();
                }
                g2.setPaint(this.getItemPaint(row, column));
                g2.fill(clip);
                g2.setStroke(this.getItemOutlineStroke(row, column));
                g2.setPaint(this.getItemOutlinePaint(row, column));
                g2.draw(clip);
            }
        }
        if (this.isItemLabelVisible(row, column)) {
            this.drawItemLabel(g2, orientation, dataset, row, column, x1, y1, value < 0.0);
        }
        final EntityCollection entities = state.getEntityCollection();
        if (entities != null) {
            this.addItemEntity(entities, dataset, row, column, shape);
        }
    }
    
    static {
        DEFAULT_WALL_PAINT = new Color(221, 221, 221);
    }
}
