package org.jfree.chart.plot;

import java.io.ObjectInputStream;
import java.io.IOException;
import org.jfree.io.SerialUtilities;
import java.io.ObjectOutputStream;
import org.jfree.util.PaintUtilities;
import org.jfree.util.ObjectUtilities;
import org.jfree.chart.urls.PieURLGenerator;
import org.jfree.chart.labels.PieToolTipGenerator;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.entity.ChartEntity;
import org.jfree.chart.entity.PieSectionEntity;
import java.awt.Shape;
import org.jfree.util.ShapeUtilities;
import java.awt.geom.Line2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import org.jfree.ui.RectangleInsets;
import org.jfree.util.UnitType;
import java.awt.geom.Arc2D;
import org.jfree.util.Rotation;
import java.awt.geom.Rectangle2D;
import java.awt.Graphics2D;
import org.jfree.chart.event.PlotChangeEvent;
import java.awt.Color;
import java.awt.BasicStroke;
import org.jfree.data.general.PieDataset;
import java.awt.Paint;
import java.awt.Stroke;
import java.io.Serializable;

public class RingPlot extends PiePlot implements Cloneable, Serializable
{
    private static final long serialVersionUID = 1556064784129676620L;
    private boolean separatorsVisible;
    private transient Stroke separatorStroke;
    private transient Paint separatorPaint;
    private double innerSeparatorExtension;
    private double outerSeparatorExtension;
    
    public RingPlot() {
        this(null);
    }
    
    public RingPlot(final PieDataset dataset) {
        super(dataset);
        this.separatorsVisible = true;
        this.separatorStroke = new BasicStroke(0.5f);
        this.separatorPaint = Color.gray;
        this.innerSeparatorExtension = 0.2;
        this.outerSeparatorExtension = 0.2;
    }
    
    public boolean getSeparatorsVisible() {
        return this.separatorsVisible;
    }
    
    public void setSeparatorsVisible(final boolean visible) {
        this.separatorsVisible = visible;
        this.notifyListeners(new PlotChangeEvent(this));
    }
    
    public Stroke getSeparatorStroke() {
        return this.separatorStroke;
    }
    
    public void setSeparatorStroke(final Stroke stroke) {
        if (stroke == null) {
            throw new IllegalArgumentException("Null 'stroke' argument.");
        }
        this.separatorStroke = stroke;
        this.notifyListeners(new PlotChangeEvent(this));
    }
    
    public Paint getSeparatorPaint() {
        return this.separatorPaint;
    }
    
    public void setSeparatorPaint(final Paint paint) {
        if (paint == null) {
            throw new IllegalArgumentException("Null 'paint' argument.");
        }
        this.separatorPaint = paint;
        this.notifyListeners(new PlotChangeEvent(this));
    }
    
    public double getInnerSeparatorExtension() {
        return this.innerSeparatorExtension;
    }
    
    public void setInnerSeparatorExtension(final double percent) {
        this.innerSeparatorExtension = percent;
        this.notifyListeners(new PlotChangeEvent(this));
    }
    
    public double getOuterSeparatorExtension() {
        return this.outerSeparatorExtension;
    }
    
    public void setOuterSeparatorExtension(final double percent) {
        this.outerSeparatorExtension = percent;
        this.notifyListeners(new PlotChangeEvent(this));
    }
    
    protected void drawItem(final Graphics2D g2, final int section, final Rectangle2D dataArea, final PiePlotState state, final int currentPass) {
        final PieDataset dataset = this.getDataset();
        final Number n = dataset.getValue(section);
        if (n == null) {
            return;
        }
        final double value = n.doubleValue();
        double angle1 = 0.0;
        double angle2 = 0.0;
        final Rotation direction = this.getDirection();
        if (direction == Rotation.CLOCKWISE) {
            angle1 = state.getLatestAngle();
            angle2 = angle1 - value / state.getTotal() * 360.0;
        }
        else {
            if (direction != Rotation.ANTICLOCKWISE) {
                throw new IllegalStateException("Rotation type not recognised.");
            }
            angle1 = state.getLatestAngle();
            angle2 = angle1 + value / state.getTotal() * 360.0;
        }
        final double angle3 = angle2 - angle1;
        if (Math.abs(angle3) > this.getMinimumArcAngleToDraw()) {
            double ep = 0.0;
            final double mep = this.getMaximumExplodePercent();
            if (mep > 0.0) {
                ep = this.getExplodePercent(section) / mep;
            }
            final Rectangle2D arcBounds = this.getArcBounds(state.getPieArea(), state.getExplodedPieArea(), angle1, angle3, ep);
            final Arc2D.Double arc = new Arc2D.Double(arcBounds, angle1, angle3, 0);
            final RectangleInsets s = new RectangleInsets(UnitType.RELATIVE, 0.1, 0.1, 0.1, 0.1);
            final Rectangle2D innerArcBounds = new Rectangle2D.Double();
            innerArcBounds.setRect(arcBounds);
            s.trim(innerArcBounds);
            final Arc2D.Double arc2 = new Arc2D.Double(innerArcBounds, angle1 + angle3, -angle3, 0);
            final GeneralPath path = new GeneralPath();
            path.moveTo((float)arc.getStartPoint().getX(), (float)arc.getStartPoint().getY());
            path.append(arc.getPathIterator(null), false);
            path.append(arc2.getPathIterator(null), true);
            path.closePath();
            final Line2D separator = new Line2D.Double(arc2.getEndPoint(), arc.getStartPoint());
            if (currentPass == 0) {
                final Paint shadowPaint = this.getShadowPaint();
                final double shadowXOffset = this.getShadowXOffset();
                final double shadowYOffset = this.getShadowYOffset();
                if (shadowPaint != null) {
                    final Shape shadowArc = ShapeUtilities.createTranslatedShape((Shape)path, (double)(float)shadowXOffset, (double)(float)shadowYOffset);
                    g2.setPaint(shadowPaint);
                    g2.fill(shadowArc);
                }
            }
            else if (currentPass == 1) {
                final Paint paint = this.getSectionPaint(section);
                g2.setPaint(paint);
                g2.fill(path);
                final Paint outlinePaint = this.getSectionOutlinePaint(section);
                final Stroke outlineStroke = this.getSectionOutlineStroke(section);
                if (outlinePaint != null && outlineStroke != null) {
                    g2.setPaint(outlinePaint);
                    g2.setStroke(outlineStroke);
                    g2.draw(path);
                }
                if (this.separatorsVisible) {
                    final Line2D extendedSeparator = this.extendLine(separator, this.innerSeparatorExtension, this.innerSeparatorExtension);
                    g2.setStroke(this.separatorStroke);
                    g2.setPaint(this.separatorPaint);
                    g2.draw(extendedSeparator);
                }
                if (state.getInfo() != null) {
                    final EntityCollection entities = state.getEntityCollection();
                    if (entities != null) {
                        final Comparable key = dataset.getKey(section);
                        String tip = null;
                        final PieToolTipGenerator toolTipGenerator = this.getToolTipGenerator();
                        if (toolTipGenerator != null) {
                            tip = toolTipGenerator.generateToolTip(dataset, key);
                        }
                        String url = null;
                        final PieURLGenerator urlGenerator = this.getURLGenerator();
                        if (urlGenerator != null) {
                            url = urlGenerator.generateURL(dataset, key, this.getPieIndex());
                        }
                        final PieSectionEntity entity = new PieSectionEntity(path, dataset, this.getPieIndex(), section, key, tip, url);
                        entities.add(entity);
                    }
                }
            }
        }
        state.setLatestAngle(angle2);
    }
    
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof RingPlot)) {
            return false;
        }
        if (!super.equals(obj)) {
            return false;
        }
        final RingPlot that = (RingPlot)obj;
        return this.separatorsVisible == that.separatorsVisible && ObjectUtilities.equal((Object)this.separatorStroke, (Object)that.separatorStroke) && PaintUtilities.equal(this.separatorPaint, that.separatorPaint) && this.innerSeparatorExtension == that.innerSeparatorExtension && this.outerSeparatorExtension == that.outerSeparatorExtension;
    }
    
    private Line2D extendLine(final Line2D line, final double startPercent, final double endPercent) {
        if (line == null) {
            throw new IllegalArgumentException("Null 'line' argument.");
        }
        double x1 = line.getX1();
        double x2 = line.getX2();
        final double deltaX = x2 - x1;
        double y1 = line.getY1();
        double y2 = line.getY2();
        final double deltaY = y2 - y1;
        x1 -= startPercent * deltaX;
        y1 -= startPercent * deltaY;
        x2 += endPercent * deltaX;
        y2 += endPercent * deltaY;
        return new Line2D.Double(x1, y1, x2, y2);
    }
    
    private void writeObject(final ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        SerialUtilities.writeStroke(this.separatorStroke, stream);
        SerialUtilities.writePaint(this.separatorPaint, stream);
    }
    
    private void readObject(final ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        this.separatorStroke = SerialUtilities.readStroke(stream);
        this.separatorPaint = SerialUtilities.readPaint(stream);
    }
}
