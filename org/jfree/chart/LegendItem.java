package org.jfree.chart;

import java.awt.BasicStroke;
import java.awt.geom.Line2D;
import java.io.ObjectInputStream;
import java.io.IOException;
import org.jfree.io.SerialUtilities;
import java.io.ObjectOutputStream;
import org.jfree.util.ShapeUtilities;
import org.jfree.util.ObjectUtilities;
import org.jfree.util.AttributedStringUtilities;
import java.text.CharacterIterator;
import java.awt.Color;
import java.awt.Stroke;
import java.awt.Paint;
import java.awt.Shape;
import java.text.AttributedString;
import java.io.Serializable;

public class LegendItem implements Serializable
{
    private static final long serialVersionUID = -797214582948827144L;
    private int datasetIndex;
    private int series;
    private String label;
    private transient AttributedString attributedLabel;
    private String description;
    private String toolTipText;
    private String urlText;
    private boolean shapeVisible;
    private transient Shape shape;
    private boolean shapeFilled;
    private transient Paint fillPaint;
    private boolean shapeOutlineVisible;
    private transient Paint outlinePaint;
    private transient Stroke outlineStroke;
    private boolean lineVisible;
    private transient Shape line;
    private transient Stroke lineStroke;
    private transient Paint linePaint;
    private static final Shape UNUSED_SHAPE;
    private static final Stroke UNUSED_STROKE;
    
    public LegendItem(final String label, final String description, final String toolTipText, final String urlText, final Shape shape, final Paint fillPaint) {
        this(label, description, toolTipText, urlText, true, shape, true, fillPaint, false, Color.black, LegendItem.UNUSED_STROKE, false, LegendItem.UNUSED_SHAPE, LegendItem.UNUSED_STROKE, Color.black);
    }
    
    public LegendItem(final String label, final String description, final String toolTipText, final String urlText, final Shape shape, final Paint fillPaint, final Stroke outlineStroke, final Paint outlinePaint) {
        this(label, description, toolTipText, urlText, true, shape, true, fillPaint, true, outlinePaint, outlineStroke, false, LegendItem.UNUSED_SHAPE, LegendItem.UNUSED_STROKE, Color.black);
    }
    
    public LegendItem(final String label, final String description, final String toolTipText, final String urlText, final Shape line, final Stroke lineStroke, final Paint linePaint) {
        this(label, description, toolTipText, urlText, false, LegendItem.UNUSED_SHAPE, false, Color.black, false, Color.black, LegendItem.UNUSED_STROKE, true, line, lineStroke, linePaint);
    }
    
    public LegendItem(final String label, final String description, final String toolTipText, final String urlText, final boolean shapeVisible, final Shape shape, final boolean shapeFilled, final Paint fillPaint, final boolean shapeOutlineVisible, final Paint outlinePaint, final Stroke outlineStroke, final boolean lineVisible, final Shape line, final Stroke lineStroke, final Paint linePaint) {
        if (label == null) {
            throw new IllegalArgumentException("Null 'label' argument.");
        }
        if (fillPaint == null) {
            throw new IllegalArgumentException("Null 'fillPaint' argument.");
        }
        if (lineStroke == null) {
            throw new IllegalArgumentException("Null 'lineStroke' argument.");
        }
        if (outlinePaint == null) {
            throw new IllegalArgumentException("Null 'outlinePaint' argument.");
        }
        if (outlineStroke == null) {
            throw new IllegalArgumentException("Null 'outlineStroke' argument.");
        }
        this.label = label;
        this.attributedLabel = null;
        this.description = description;
        this.shapeVisible = shapeVisible;
        this.shape = shape;
        this.shapeFilled = shapeFilled;
        this.fillPaint = fillPaint;
        this.shapeOutlineVisible = shapeOutlineVisible;
        this.outlinePaint = outlinePaint;
        this.outlineStroke = outlineStroke;
        this.lineVisible = lineVisible;
        this.line = line;
        this.lineStroke = lineStroke;
        this.linePaint = linePaint;
        this.toolTipText = toolTipText;
        this.urlText = urlText;
    }
    
    public LegendItem(final AttributedString label, final String description, final String toolTipText, final String urlText, final Shape shape, final Paint fillPaint) {
        this(label, description, toolTipText, urlText, true, shape, true, fillPaint, false, Color.black, LegendItem.UNUSED_STROKE, false, LegendItem.UNUSED_SHAPE, LegendItem.UNUSED_STROKE, Color.black);
    }
    
    public LegendItem(final AttributedString label, final String description, final String toolTipText, final String urlText, final Shape shape, final Paint fillPaint, final Stroke outlineStroke, final Paint outlinePaint) {
        this(label, description, toolTipText, urlText, true, shape, true, fillPaint, true, outlinePaint, outlineStroke, false, LegendItem.UNUSED_SHAPE, LegendItem.UNUSED_STROKE, Color.black);
    }
    
    public LegendItem(final AttributedString label, final String description, final String toolTipText, final String urlText, final Shape line, final Stroke lineStroke, final Paint linePaint) {
        this(label, description, toolTipText, urlText, false, LegendItem.UNUSED_SHAPE, false, Color.black, false, Color.black, LegendItem.UNUSED_STROKE, true, line, lineStroke, linePaint);
    }
    
    public LegendItem(final AttributedString label, final String description, final String toolTipText, final String urlText, final boolean shapeVisible, final Shape shape, final boolean shapeFilled, final Paint fillPaint, final boolean shapeOutlineVisible, final Paint outlinePaint, final Stroke outlineStroke, final boolean lineVisible, final Shape line, final Stroke lineStroke, final Paint linePaint) {
        if (label == null) {
            throw new IllegalArgumentException("Null 'label' argument.");
        }
        if (fillPaint == null) {
            throw new IllegalArgumentException("Null 'fillPaint' argument.");
        }
        if (lineStroke == null) {
            throw new IllegalArgumentException("Null 'lineStroke' argument.");
        }
        if (outlinePaint == null) {
            throw new IllegalArgumentException("Null 'outlinePaint' argument.");
        }
        if (outlineStroke == null) {
            throw new IllegalArgumentException("Null 'outlineStroke' argument.");
        }
        this.label = this.characterIteratorToString(label.getIterator());
        this.attributedLabel = label;
        this.description = description;
        this.shapeVisible = shapeVisible;
        this.shape = shape;
        this.shapeFilled = shapeFilled;
        this.fillPaint = fillPaint;
        this.shapeOutlineVisible = shapeOutlineVisible;
        this.outlinePaint = outlinePaint;
        this.outlineStroke = outlineStroke;
        this.lineVisible = lineVisible;
        this.line = line;
        this.lineStroke = lineStroke;
        this.linePaint = linePaint;
        this.toolTipText = toolTipText;
        this.urlText = urlText;
    }
    
    private String characterIteratorToString(final CharacterIterator iterator) {
        final int endIndex = iterator.getEndIndex();
        final int beginIndex = iterator.getBeginIndex();
        final int count = endIndex - beginIndex;
        if (count <= 0) {
            return "";
        }
        final char[] chars = new char[count];
        int i = 0;
        for (char c = iterator.first(); c != '\uffff'; c = iterator.next()) {
            chars[i] = c;
            ++i;
        }
        return new String(chars);
    }
    
    public int getDatasetIndex() {
        return this.datasetIndex;
    }
    
    public void setDatasetIndex(final int index) {
        this.datasetIndex = index;
    }
    
    public int getSeriesIndex() {
        return this.series;
    }
    
    public void setSeriesIndex(final int index) {
        this.series = index;
    }
    
    public String getLabel() {
        return this.label;
    }
    
    public AttributedString getAttributedLabel() {
        return this.attributedLabel;
    }
    
    public String getDescription() {
        return this.description;
    }
    
    public String getToolTipText() {
        return this.toolTipText;
    }
    
    public String getURLText() {
        return this.urlText;
    }
    
    public boolean isShapeVisible() {
        return this.shapeVisible;
    }
    
    public Shape getShape() {
        return this.shape;
    }
    
    public boolean isShapeFilled() {
        return this.shapeFilled;
    }
    
    public Paint getFillPaint() {
        return this.fillPaint;
    }
    
    public boolean isShapeOutlineVisible() {
        return this.shapeOutlineVisible;
    }
    
    public Stroke getLineStroke() {
        return this.lineStroke;
    }
    
    public Paint getLinePaint() {
        return this.linePaint;
    }
    
    public Paint getOutlinePaint() {
        return this.outlinePaint;
    }
    
    public Stroke getOutlineStroke() {
        return this.outlineStroke;
    }
    
    public boolean isLineVisible() {
        return this.lineVisible;
    }
    
    public Shape getLine() {
        return this.line;
    }
    
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof LegendItem)) {
            return false;
        }
        final LegendItem that = (LegendItem)obj;
        return this.datasetIndex == that.datasetIndex && this.series == that.series && this.label.equals(that.label) && AttributedStringUtilities.equal(this.attributedLabel, that.attributedLabel) && ObjectUtilities.equal((Object)this.description, (Object)that.description) && this.shapeVisible == that.shapeVisible && ShapeUtilities.equal(this.shape, that.shape) && this.shapeFilled == that.shapeFilled && this.fillPaint.equals(that.fillPaint) && this.shapeOutlineVisible == that.shapeOutlineVisible && this.outlineStroke.equals(that.outlineStroke) && this.outlinePaint.equals(that.outlinePaint) && !this.lineVisible != that.lineVisible && ShapeUtilities.equal(this.line, that.line) && this.lineStroke.equals(that.lineStroke) && this.linePaint.equals(that.linePaint);
    }
    
    private void writeObject(final ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        SerialUtilities.writeAttributedString(this.attributedLabel, stream);
        SerialUtilities.writeShape(this.shape, stream);
        SerialUtilities.writePaint(this.fillPaint, stream);
        SerialUtilities.writeStroke(this.outlineStroke, stream);
        SerialUtilities.writePaint(this.outlinePaint, stream);
        SerialUtilities.writeShape(this.line, stream);
        SerialUtilities.writeStroke(this.lineStroke, stream);
        SerialUtilities.writePaint(this.linePaint, stream);
    }
    
    private void readObject(final ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        this.attributedLabel = SerialUtilities.readAttributedString(stream);
        this.shape = SerialUtilities.readShape(stream);
        this.fillPaint = SerialUtilities.readPaint(stream);
        this.outlineStroke = SerialUtilities.readStroke(stream);
        this.outlinePaint = SerialUtilities.readPaint(stream);
        this.line = SerialUtilities.readShape(stream);
        this.lineStroke = SerialUtilities.readStroke(stream);
        this.linePaint = SerialUtilities.readPaint(stream);
    }
    
    static {
        UNUSED_SHAPE = new Line2D.Float();
        UNUSED_STROKE = new BasicStroke(0.0f);
    }
}
