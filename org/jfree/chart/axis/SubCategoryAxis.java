package org.jfree.chart.axis;

import java.io.ObjectInputStream;
import java.io.IOException;
import org.jfree.io.SerialUtilities;
import java.io.ObjectOutputStream;
import org.jfree.data.category.CategoryDataset;
import org.jfree.ui.TextAnchor;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotRenderingInfo;
import java.util.Iterator;
import java.awt.FontMetrics;
import org.jfree.text.TextUtilities;
import org.jfree.ui.RectangleEdge;
import java.awt.geom.Rectangle2D;
import org.jfree.chart.plot.Plot;
import java.awt.Graphics2D;
import org.jfree.chart.event.AxisChangeEvent;
import java.util.ArrayList;
import java.awt.Color;
import java.awt.Paint;
import java.awt.Font;
import java.util.List;
import java.io.Serializable;

public class SubCategoryAxis extends CategoryAxis implements Cloneable, Serializable
{
    private static final long serialVersionUID = -1279463299793228344L;
    private List subCategories;
    private Font subLabelFont;
    private transient Paint subLabelPaint;
    
    public SubCategoryAxis(final String label) {
        super(label);
        this.subLabelFont = new Font("SansSerif", 0, 10);
        this.subLabelPaint = Color.black;
        this.subCategories = new ArrayList();
    }
    
    public void addSubCategory(final Comparable subCategory) {
        this.subCategories.add(subCategory);
    }
    
    public Font getSubLabelFont() {
        return this.subLabelFont;
    }
    
    public void setSubLabelFont(final Font font) {
        if (font == null) {
            throw new IllegalArgumentException("Null 'font' argument.");
        }
        this.subLabelFont = font;
        this.notifyListeners(new AxisChangeEvent(this));
    }
    
    public Paint getSubLabelPaint() {
        return this.subLabelPaint;
    }
    
    public void setSubLabelPaint(final Paint paint) {
        if (paint == null) {
            throw new IllegalArgumentException("Null 'paint' argument.");
        }
        this.subLabelPaint = paint;
        this.notifyListeners(new AxisChangeEvent(this));
    }
    
    public AxisSpace reserveSpace(final Graphics2D g2, final Plot plot, final Rectangle2D plotArea, final RectangleEdge edge, AxisSpace space) {
        if (space == null) {
            space = new AxisSpace();
        }
        if (!this.isVisible()) {
            return space;
        }
        space = super.reserveSpace(g2, plot, plotArea, edge, space);
        final double maxdim = this.getMaxDim(g2, edge);
        if (RectangleEdge.isTopOrBottom(edge)) {
            space.add(maxdim, edge);
        }
        else if (RectangleEdge.isLeftOrRight(edge)) {
            space.add(maxdim, edge);
        }
        return space;
    }
    
    private double getMaxDim(final Graphics2D g2, final RectangleEdge edge) {
        double result = 0.0;
        g2.setFont(this.subLabelFont);
        final FontMetrics fm = g2.getFontMetrics();
        final Iterator iterator = this.subCategories.iterator();
        while (iterator.hasNext()) {
            final Comparable subcategory = iterator.next();
            final String label = subcategory.toString();
            final Rectangle2D bounds = TextUtilities.getTextBounds(label, g2, fm);
            double dim = 0.0;
            if (RectangleEdge.isLeftOrRight(edge)) {
                dim = bounds.getWidth();
            }
            else {
                dim = bounds.getHeight();
            }
            result = Math.max(result, dim);
        }
        return result;
    }
    
    public AxisState draw(final Graphics2D g2, final double cursor, final Rectangle2D plotArea, final Rectangle2D dataArea, final RectangleEdge edge, final PlotRenderingInfo plotState) {
        if (!this.isVisible()) {
            return new AxisState(cursor);
        }
        if (this.isAxisLineVisible()) {
            this.drawAxisLine(g2, cursor, dataArea, edge);
        }
        AxisState state = new AxisState(cursor);
        state = this.drawSubCategoryLabels(g2, plotArea, dataArea, edge, state, plotState);
        state = this.drawCategoryLabels(g2, plotArea, dataArea, edge, state, plotState);
        state = this.drawLabel(this.getLabel(), g2, plotArea, dataArea, edge, state);
        return state;
    }
    
    protected AxisState drawSubCategoryLabels(final Graphics2D g2, final Rectangle2D plotArea, final Rectangle2D dataArea, final RectangleEdge edge, final AxisState state, final PlotRenderingInfo plotState) {
        if (state == null) {
            throw new IllegalArgumentException("Null 'state' argument.");
        }
        g2.setFont(this.subLabelFont);
        g2.setPaint(this.subLabelPaint);
        final CategoryPlot plot = (CategoryPlot)this.getPlot();
        final CategoryDataset dataset = plot.getDataset();
        final int categoryCount = dataset.getColumnCount();
        final double maxdim = this.getMaxDim(g2, edge);
        for (int categoryIndex = 0; categoryIndex < categoryCount; ++categoryIndex) {
            double x0 = 0.0;
            double x2 = 0.0;
            double y0 = 0.0;
            double y2 = 0.0;
            if (edge == RectangleEdge.TOP) {
                x0 = this.getCategoryStart(categoryIndex, categoryCount, dataArea, edge);
                x2 = this.getCategoryEnd(categoryIndex, categoryCount, dataArea, edge);
                y2 = state.getCursor();
                y0 = y2 - maxdim;
            }
            else if (edge == RectangleEdge.BOTTOM) {
                x0 = this.getCategoryStart(categoryIndex, categoryCount, dataArea, edge);
                x2 = this.getCategoryEnd(categoryIndex, categoryCount, dataArea, edge);
                y0 = state.getCursor();
                y2 = y0 + maxdim;
            }
            else if (edge == RectangleEdge.LEFT) {
                y0 = this.getCategoryStart(categoryIndex, categoryCount, dataArea, edge);
                y2 = this.getCategoryEnd(categoryIndex, categoryCount, dataArea, edge);
                x2 = state.getCursor();
                x0 = x2 - maxdim;
            }
            else if (edge == RectangleEdge.RIGHT) {
                y0 = this.getCategoryStart(categoryIndex, categoryCount, dataArea, edge);
                y2 = this.getCategoryEnd(categoryIndex, categoryCount, dataArea, edge);
                x0 = state.getCursor();
                x2 = x0 + maxdim;
            }
            final Rectangle2D area = new Rectangle2D.Double(x0, y0, x2 - x0, y2 - y0);
            final int subCategoryCount = this.subCategories.size();
            final float width = (float)((x2 - x0) / subCategoryCount);
            final float height = (float)((y2 - y0) / subCategoryCount);
            float xx = 0.0f;
            float yy = 0.0f;
            for (int i = 0; i < subCategoryCount; ++i) {
                if (RectangleEdge.isTopOrBottom(edge)) {
                    xx = (float)(x0 + (i + 0.5) * width);
                    yy = (float)area.getCenterY();
                }
                else {
                    xx = (float)area.getCenterX();
                    yy = (float)(y0 + (i + 0.5) * height);
                }
                final String label = this.subCategories.get(i).toString();
                TextUtilities.drawRotatedString(label, g2, xx, yy, TextAnchor.CENTER, 0.0, TextAnchor.CENTER);
            }
        }
        if (edge.equals((Object)RectangleEdge.TOP)) {
            final double h = maxdim;
            state.cursorUp(h);
        }
        else if (edge.equals((Object)RectangleEdge.BOTTOM)) {
            final double h = maxdim;
            state.cursorDown(h);
        }
        else if (edge == RectangleEdge.LEFT) {
            final double w = maxdim;
            state.cursorLeft(w);
        }
        else if (edge == RectangleEdge.RIGHT) {
            final double w = maxdim;
            state.cursorRight(w);
        }
        return state;
    }
    
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof SubCategoryAxis && super.equals(obj)) {
            final SubCategoryAxis axis = (SubCategoryAxis)obj;
            return this.subCategories.equals(axis.subCategories) && this.subLabelFont.equals(axis.subLabelFont) && this.subLabelPaint.equals(axis.subLabelPaint);
        }
        return false;
    }
    
    private void writeObject(final ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        SerialUtilities.writePaint(this.subLabelPaint, stream);
    }
    
    private void readObject(final ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        this.subLabelPaint = SerialUtilities.readPaint(stream);
    }
}
