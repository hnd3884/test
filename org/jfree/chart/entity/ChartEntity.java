package org.jfree.chart.entity;

import java.io.ObjectInputStream;
import java.io.IOException;
import org.jfree.io.SerialUtilities;
import java.io.ObjectOutputStream;
import org.jfree.util.ObjectUtilities;
import org.jfree.chart.imagemap.URLTagFragmentGenerator;
import org.jfree.chart.imagemap.ToolTipTagFragmentGenerator;
import java.awt.geom.PathIterator;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.Shape;
import java.io.Serializable;
import org.jfree.util.PublicCloneable;

public class ChartEntity implements Cloneable, PublicCloneable, Serializable
{
    private static final long serialVersionUID = -4445994133561919083L;
    private transient Shape area;
    private String toolTipText;
    private String urlText;
    
    public ChartEntity(final Shape area) {
        this(area, null);
    }
    
    public ChartEntity(final Shape area, final String toolTipText) {
        this(area, toolTipText, null);
    }
    
    public ChartEntity(final Shape area, final String toolTipText, final String urlText) {
        if (area == null) {
            throw new IllegalArgumentException("Null 'area' argument.");
        }
        this.area = area;
        this.toolTipText = toolTipText;
        this.urlText = urlText;
    }
    
    public Shape getArea() {
        return this.area;
    }
    
    public void setArea(final Shape area) {
        if (area == null) {
            throw new IllegalArgumentException("Null 'area' argument.");
        }
        this.area = area;
    }
    
    public String getToolTipText() {
        return this.toolTipText;
    }
    
    public void setToolTipText(final String text) {
        this.toolTipText = text;
    }
    
    public String getURLText() {
        return this.urlText;
    }
    
    public void setURLText(final String text) {
        this.urlText = text;
    }
    
    public String getShapeType() {
        if (this.area instanceof Rectangle2D) {
            return "rect";
        }
        return "poly";
    }
    
    public String getShapeCoords() {
        if (this.area instanceof Rectangle2D) {
            return this.getRectCoords((Rectangle2D)this.area);
        }
        return this.getPolyCoords(this.area);
    }
    
    private String getRectCoords(final Rectangle2D rectangle) {
        if (rectangle == null) {
            throw new IllegalArgumentException("Null 'rectangle' argument.");
        }
        final int x1 = (int)rectangle.getX();
        final int y1 = (int)rectangle.getY();
        int x2 = x1 + (int)rectangle.getWidth();
        int y2 = y1 + (int)rectangle.getHeight();
        if (x2 == x1) {
            ++x2;
        }
        if (y2 == y1) {
            ++y2;
        }
        return x1 + "," + y1 + "," + x2 + "," + y2;
    }
    
    private String getPolyCoords(final Shape shape) {
        if (shape == null) {
            throw new IllegalArgumentException("Null 'shape' argument.");
        }
        final StringBuffer result = new StringBuffer();
        boolean first = true;
        final float[] coords = new float[6];
        final PathIterator pi = shape.getPathIterator(null, 1.0);
        while (!pi.isDone()) {
            pi.currentSegment(coords);
            if (first) {
                first = false;
                result.append((int)coords[0]);
                result.append(",").append((int)coords[1]);
            }
            else {
                result.append(",");
                result.append((int)coords[0]);
                result.append(",");
                result.append((int)coords[1]);
            }
            pi.next();
        }
        return result.toString();
    }
    
    public String getImageMapAreaTag(final ToolTipTagFragmentGenerator toolTipTagFragmentGenerator, final URLTagFragmentGenerator urlTagFragmentGenerator) {
        final StringBuffer tag = new StringBuffer();
        final boolean hasURL = this.urlText != null && urlTagFragmentGenerator != null && !this.urlText.equals("");
        final boolean hasToolTip = this.toolTipText != null && toolTipTagFragmentGenerator != null && !this.toolTipText.equals("");
        if (hasURL || hasToolTip) {
            tag.append("<area shape=\"" + this.getShapeType() + "\" coords=\"" + this.getShapeCoords() + "\"");
            if (hasToolTip) {
                tag.append(toolTipTagFragmentGenerator.generateToolTipFragment(this.toolTipText));
            }
            if (hasURL) {
                tag.append(urlTagFragmentGenerator.generateURLFragment(this.urlText));
            }
            if (!hasToolTip) {
                tag.append(" alt=\"\"");
            }
            tag.append("/>");
        }
        return tag.toString();
    }
    
    @Override
    public String toString() {
        final StringBuffer buf = new StringBuffer("ChartEntity: ");
        buf.append("tooltip = ");
        buf.append(this.toolTipText);
        return buf.toString();
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof ChartEntity) {
            final ChartEntity that = (ChartEntity)obj;
            return this.area.equals(that.area) && ObjectUtilities.equal((Object)this.toolTipText, (Object)that.toolTipText) && ObjectUtilities.equal((Object)this.urlText, (Object)that.urlText);
        }
        return false;
    }
    
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
    
    private void writeObject(final ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        SerialUtilities.writeShape(this.area, stream);
    }
    
    private void readObject(final ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        this.area = SerialUtilities.readShape(stream);
    }
}
