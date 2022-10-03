package org.jfree.chart.renderer.category;

import java.io.ObjectInputStream;
import java.io.IOException;
import org.jfree.io.SerialUtilities;
import java.io.ObjectOutputStream;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import org.jfree.chart.labels.CategoryToolTipGenerator;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.entity.ChartEntity;
import org.jfree.chart.entity.CategoryItemEntity;
import java.awt.Graphics;
import java.awt.Component;
import org.jfree.data.category.CategoryDataset;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.plot.CategoryPlot;
import java.awt.geom.Rectangle2D;
import java.awt.Graphics2D;
import org.jfree.chart.event.RendererChangeEvent;
import java.awt.geom.Line2D;
import java.awt.Shape;
import java.awt.geom.Arc2D;
import java.awt.BasicStroke;
import java.awt.Color;
import javax.swing.Icon;
import java.awt.Stroke;
import java.awt.Paint;

public class MinMaxCategoryRenderer extends AbstractCategoryItemRenderer
{
    private static final long serialVersionUID = 2935615937671064911L;
    private boolean plotLines;
    private transient Paint groupPaint;
    private transient Stroke groupStroke;
    private transient Icon minIcon;
    private transient Icon maxIcon;
    private transient Icon objectIcon;
    private int lastCategory;
    private double min;
    private double max;
    
    public MinMaxCategoryRenderer() {
        this.plotLines = false;
        this.groupPaint = Color.black;
        this.groupStroke = new BasicStroke(1.0f);
        this.minIcon = this.getIcon(new Arc2D.Double(-4.0, -4.0, 8.0, 8.0, 0.0, 360.0, 0), null, Color.black);
        this.maxIcon = this.getIcon(new Arc2D.Double(-4.0, -4.0, 8.0, 8.0, 0.0, 360.0, 0), null, Color.black);
        this.objectIcon = this.getIcon(new Line2D.Double(-4.0, 0.0, 4.0, 0.0), false, true);
        this.lastCategory = -1;
    }
    
    public boolean isDrawLines() {
        return this.plotLines;
    }
    
    public void setDrawLines(final boolean draw) {
        if (this.plotLines != draw) {
            this.plotLines = draw;
            this.notifyListeners(new RendererChangeEvent(this));
        }
    }
    
    public Paint getGroupPaint() {
        return this.groupPaint;
    }
    
    public void setGroupPaint(final Paint paint) {
        if (paint == null) {
            throw new IllegalArgumentException("Null 'paint' argument.");
        }
        this.groupPaint = paint;
        this.notifyListeners(new RendererChangeEvent(this));
    }
    
    public Stroke getGroupStroke() {
        return this.groupStroke;
    }
    
    public void setGroupStroke(final Stroke groupStroke) {
        this.groupStroke = groupStroke;
    }
    
    public Icon getObjectIcon() {
        return this.objectIcon;
    }
    
    public void setObjectIcon(final Icon icon) {
        if (icon == null) {
            throw new IllegalArgumentException("Null 'icon' argument.");
        }
        this.objectIcon = icon;
        this.notifyListeners(new RendererChangeEvent(this));
    }
    
    public Icon getMaxIcon() {
        return this.maxIcon;
    }
    
    public void setMaxIcon(final Icon icon) {
        if (icon == null) {
            throw new IllegalArgumentException("Null 'icon' argument.");
        }
        this.maxIcon = icon;
        this.notifyListeners(new RendererChangeEvent(this));
    }
    
    public Icon getMinIcon() {
        return this.minIcon;
    }
    
    public void setMinIcon(final Icon icon) {
        if (icon == null) {
            throw new IllegalArgumentException("Null 'icon' argument.");
        }
        this.minIcon = icon;
        this.notifyListeners(new RendererChangeEvent(this));
    }
    
    public void drawItem(final Graphics2D g2, final CategoryItemRendererState state, final Rectangle2D dataArea, final CategoryPlot plot, final CategoryAxis domainAxis, final ValueAxis rangeAxis, final CategoryDataset dataset, final int row, final int column, final int pass) {
        final Number value = dataset.getValue(row, column);
        if (value != null) {
            final double x1 = domainAxis.getCategoryMiddle(column, this.getColumnCount(), dataArea, plot.getDomainAxisEdge());
            final double y1 = rangeAxis.valueToJava2D(value.doubleValue(), dataArea, plot.getRangeAxisEdge());
            g2.setPaint(this.getItemPaint(row, column));
            g2.setStroke(this.getItemStroke(row, column));
            Shape shape = null;
            shape = new Rectangle2D.Double(x1 - 4.0, y1 - 4.0, 8.0, 8.0);
            this.objectIcon.paintIcon(null, g2, (int)x1, (int)y1);
            if (this.lastCategory == column) {
                if (this.min > value.doubleValue()) {
                    this.min = value.doubleValue();
                }
                if (this.max < value.doubleValue()) {
                    this.max = value.doubleValue();
                }
                if (dataset.getRowCount() - 1 == row) {
                    g2.setPaint(this.groupPaint);
                    g2.setStroke(this.groupStroke);
                    final double minY = rangeAxis.valueToJava2D(this.min, dataArea, plot.getRangeAxisEdge());
                    final double maxY = rangeAxis.valueToJava2D(this.max, dataArea, plot.getRangeAxisEdge());
                    g2.draw(new Line2D.Double(x1, minY, x1, maxY));
                    this.minIcon.paintIcon(null, g2, (int)x1, (int)minY);
                    this.maxIcon.paintIcon(null, g2, (int)x1, (int)maxY);
                }
            }
            else {
                this.lastCategory = column;
                this.min = value.doubleValue();
                this.max = value.doubleValue();
            }
            if (this.plotLines && column != 0) {
                final Number previousValue = dataset.getValue(row, column - 1);
                if (previousValue != null) {
                    final double previous = previousValue.doubleValue();
                    final double x2 = domainAxis.getCategoryMiddle(column - 1, this.getColumnCount(), dataArea, plot.getDomainAxisEdge());
                    final double y2 = rangeAxis.valueToJava2D(previous, dataArea, plot.getRangeAxisEdge());
                    g2.setPaint(this.getItemPaint(row, column));
                    g2.setStroke(this.getItemStroke(row, column));
                    final Line2D line = new Line2D.Double(x2, y2, x1, y1);
                    g2.draw(line);
                }
            }
            if (state.getInfo() != null) {
                final EntityCollection entities = state.getEntityCollection();
                if (entities != null && shape != null) {
                    String tip = null;
                    final CategoryToolTipGenerator tipster = this.getToolTipGenerator(row, column);
                    if (tipster != null) {
                        tip = tipster.generateToolTip(dataset, row, column);
                    }
                    final CategoryItemEntity entity = new CategoryItemEntity(shape, tip, null, dataset, row, dataset.getColumnKey(column), column);
                    entities.add(entity);
                }
            }
        }
    }
    
    private Icon getIcon(final Shape shape, final Paint fillPaint, final Paint outlinePaint) {
        final int width = shape.getBounds().width;
        final int height = shape.getBounds().height;
        final GeneralPath path = new GeneralPath(shape);
        return new Icon() {
            public void paintIcon(final Component c, final Graphics g, final int x, final int y) {
                final Graphics2D g2 = (Graphics2D)g;
                path.transform(AffineTransform.getTranslateInstance(x, y));
                if (fillPaint != null) {
                    g2.setPaint(fillPaint);
                    g2.fill(path);
                }
                if (outlinePaint != null) {
                    g2.setPaint(outlinePaint);
                    g2.draw(path);
                }
                path.transform(AffineTransform.getTranslateInstance(-x, -y));
            }
            
            public int getIconWidth() {
                return width;
            }
            
            public int getIconHeight() {
                return height;
            }
        };
    }
    
    private Icon getIcon(final Shape shape, final boolean fill, final boolean outline) {
        final int width = shape.getBounds().width;
        final int height = shape.getBounds().height;
        final GeneralPath path = new GeneralPath(shape);
        return new Icon() {
            public void paintIcon(final Component c, final Graphics g, final int x, final int y) {
                final Graphics2D g2 = (Graphics2D)g;
                path.transform(AffineTransform.getTranslateInstance(x, y));
                if (fill) {
                    g2.fill(path);
                }
                if (outline) {
                    g2.draw(path);
                }
                path.transform(AffineTransform.getTranslateInstance(-x, -y));
            }
            
            public int getIconWidth() {
                return width;
            }
            
            public int getIconHeight() {
                return height;
            }
        };
    }
    
    private void writeObject(final ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        SerialUtilities.writeStroke(this.groupStroke, stream);
        SerialUtilities.writePaint(this.groupPaint, stream);
    }
    
    private void readObject(final ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        this.groupStroke = SerialUtilities.readStroke(stream);
        this.groupPaint = SerialUtilities.readPaint(stream);
        this.minIcon = this.getIcon(new Arc2D.Double(-4.0, -4.0, 8.0, 8.0, 0.0, 360.0, 0), null, Color.black);
        this.maxIcon = this.getIcon(new Arc2D.Double(-4.0, -4.0, 8.0, 8.0, 0.0, 360.0, 0), null, Color.black);
        this.objectIcon = this.getIcon(new Line2D.Double(-4.0, 0.0, 4.0, 0.0), false, true);
    }
}
