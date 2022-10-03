package org.jfree.chart.axis;

import org.jfree.data.time.Year;
import org.jfree.data.time.Month;
import java.io.ObjectInputStream;
import java.io.IOException;
import org.jfree.io.SerialUtilities;
import java.io.ObjectOutputStream;
import java.lang.reflect.Constructor;
import java.util.Arrays;
import org.jfree.chart.plot.ValueAxisPlot;
import java.util.Collections;
import org.jfree.text.TextUtilities;
import java.util.List;
import java.awt.Shape;
import java.awt.geom.Line2D;
import org.jfree.ui.TextAnchor;
import java.util.ArrayList;
import org.jfree.chart.plot.PlotRenderingInfo;
import java.awt.FontMetrics;
import org.jfree.ui.RectangleEdge;
import java.awt.geom.Rectangle2D;
import org.jfree.chart.plot.Plot;
import java.awt.Graphics2D;
import java.util.Date;
import org.jfree.data.Range;
import org.jfree.chart.event.AxisChangeEvent;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.awt.Color;
import java.awt.BasicStroke;
import org.jfree.data.time.Day;
import java.awt.Paint;
import java.awt.Stroke;
import java.util.TimeZone;
import org.jfree.data.time.RegularTimePeriod;
import java.io.Serializable;
import org.jfree.util.PublicCloneable;

public class PeriodAxis extends ValueAxis implements Cloneable, PublicCloneable, Serializable
{
    private static final long serialVersionUID = 8353295532075872069L;
    private RegularTimePeriod first;
    private RegularTimePeriod last;
    private TimeZone timeZone;
    private Class autoRangeTimePeriodClass;
    private Class majorTickTimePeriodClass;
    private boolean minorTickMarksVisible;
    private Class minorTickTimePeriodClass;
    private float minorTickMarkInsideLength;
    private float minorTickMarkOutsideLength;
    private transient Stroke minorTickMarkStroke;
    private transient Paint minorTickMarkPaint;
    private PeriodAxisLabelInfo[] labelInfo;
    
    public PeriodAxis(final String label) {
        this(label, new Day(), new Day());
    }
    
    public PeriodAxis(final String label, final RegularTimePeriod first, final RegularTimePeriod last) {
        this(label, first, last, TimeZone.getDefault());
    }
    
    public PeriodAxis(final String label, final RegularTimePeriod first, final RegularTimePeriod last, final TimeZone timeZone) {
        super(label, null);
        this.minorTickMarkInsideLength = 0.0f;
        this.minorTickMarkOutsideLength = 2.0f;
        this.minorTickMarkStroke = new BasicStroke(0.5f);
        this.minorTickMarkPaint = Color.black;
        this.first = first;
        this.last = last;
        this.timeZone = timeZone;
        this.autoRangeTimePeriodClass = first.getClass();
        this.majorTickTimePeriodClass = first.getClass();
        this.minorTickMarksVisible = false;
        this.minorTickTimePeriodClass = RegularTimePeriod.downsize(this.majorTickTimePeriodClass);
        this.setAutoRange(true);
        (this.labelInfo = new PeriodAxisLabelInfo[2])[0] = new PeriodAxisLabelInfo(Month.class, new SimpleDateFormat("MMM"));
        this.labelInfo[1] = new PeriodAxisLabelInfo(Year.class, new SimpleDateFormat("yyyy"));
    }
    
    public RegularTimePeriod getFirst() {
        return this.first;
    }
    
    public void setFirst(final RegularTimePeriod first) {
        if (first == null) {
            throw new IllegalArgumentException("Null 'first' argument.");
        }
        this.first = first;
        this.notifyListeners(new AxisChangeEvent(this));
    }
    
    public RegularTimePeriod getLast() {
        return this.last;
    }
    
    public void setLast(final RegularTimePeriod last) {
        if (last == null) {
            throw new IllegalArgumentException("Null 'last' argument.");
        }
        this.last = last;
        this.notifyListeners(new AxisChangeEvent(this));
    }
    
    public TimeZone getTimeZone() {
        return this.timeZone;
    }
    
    public void setTimeZone(final TimeZone zone) {
        if (zone == null) {
            throw new IllegalArgumentException("Null 'zone' argument.");
        }
        this.timeZone = zone;
        this.notifyListeners(new AxisChangeEvent(this));
    }
    
    public Class getAutoRangeTimePeriodClass() {
        return this.autoRangeTimePeriodClass;
    }
    
    public void setAutoRangeTimePeriodClass(final Class c) {
        if (c == null) {
            throw new IllegalArgumentException("Null 'c' argument.");
        }
        this.autoRangeTimePeriodClass = c;
        this.notifyListeners(new AxisChangeEvent(this));
    }
    
    public Class getMajorTickTimePeriodClass() {
        return this.majorTickTimePeriodClass;
    }
    
    public void setMajorTickTimePeriodClass(final Class c) {
        if (c == null) {
            throw new IllegalArgumentException("Null 'c' argument.");
        }
        this.majorTickTimePeriodClass = c;
        this.notifyListeners(new AxisChangeEvent(this));
    }
    
    public boolean isMinorTickMarksVisible() {
        return this.minorTickMarksVisible;
    }
    
    public void setMinorTickMarksVisible(final boolean visible) {
        this.minorTickMarksVisible = visible;
        this.notifyListeners(new AxisChangeEvent(this));
    }
    
    public Class getMinorTickTimePeriodClass() {
        return this.minorTickTimePeriodClass;
    }
    
    public void setMinorTickTimePeriodClass(final Class c) {
        if (c == null) {
            throw new IllegalArgumentException("Null 'c' argument.");
        }
        this.minorTickTimePeriodClass = c;
        this.notifyListeners(new AxisChangeEvent(this));
    }
    
    public Stroke getMinorTickMarkStroke() {
        return this.minorTickMarkStroke;
    }
    
    public void setMinorTickMarkStroke(final Stroke stroke) {
        if (stroke == null) {
            throw new IllegalArgumentException("Null 'stroke' argument.");
        }
        this.minorTickMarkStroke = stroke;
        this.notifyListeners(new AxisChangeEvent(this));
    }
    
    public Paint getMinorTickMarkPaint() {
        return this.minorTickMarkPaint;
    }
    
    public void setMinorTickMarkPaint(final Paint paint) {
        if (paint == null) {
            throw new IllegalArgumentException("Null 'paint' argument.");
        }
        this.minorTickMarkPaint = paint;
        this.notifyListeners(new AxisChangeEvent(this));
    }
    
    public float getMinorTickMarkInsideLength() {
        return this.minorTickMarkInsideLength;
    }
    
    public void setMinorTickMarkInsideLength(final float length) {
        this.minorTickMarkInsideLength = length;
        this.notifyListeners(new AxisChangeEvent(this));
    }
    
    public float getMinorTickMarkOutsideLength() {
        return this.minorTickMarkOutsideLength;
    }
    
    public void setMinorTickMarkOutsideLength(final float length) {
        this.minorTickMarkOutsideLength = length;
        this.notifyListeners(new AxisChangeEvent(this));
    }
    
    public PeriodAxisLabelInfo[] getLabelInfo() {
        return this.labelInfo;
    }
    
    public void setLabelInfo(final PeriodAxisLabelInfo[] info) {
        this.labelInfo = info;
    }
    
    public Range getRange() {
        return new Range((double)this.first.getFirstMillisecond(this.timeZone), (double)this.last.getLastMillisecond(this.timeZone));
    }
    
    public void setRange(final Range range, final boolean turnOffAutoRange, final boolean notify) {
        super.setRange(range, turnOffAutoRange, false);
        final long upper = Math.round(range.getUpperBound());
        final long lower = Math.round(range.getLowerBound());
        this.first = this.createInstance(this.autoRangeTimePeriodClass, new Date(lower), this.timeZone);
        this.last = this.createInstance(this.autoRangeTimePeriodClass, new Date(upper), this.timeZone);
    }
    
    public void configure() {
        if (this.isAutoRange()) {
            this.autoAdjustRange();
        }
    }
    
    public AxisSpace reserveSpace(final Graphics2D g2, final Plot plot, final Rectangle2D plotArea, final RectangleEdge edge, AxisSpace space) {
        if (space == null) {
            space = new AxisSpace();
        }
        if (!this.isVisible()) {
            return space;
        }
        final double dimension = this.getFixedDimension();
        if (dimension > 0.0) {
            space.ensureAtLeast(dimension, edge);
        }
        final Rectangle2D labelEnclosure = this.getLabelEnclosure(g2, edge);
        double labelHeight = 0.0;
        double labelWidth = 0.0;
        double tickLabelBandsDimension = 0.0;
        for (int i = 0; i < this.labelInfo.length; ++i) {
            final PeriodAxisLabelInfo info = this.labelInfo[i];
            final FontMetrics fm = g2.getFontMetrics(info.getLabelFont());
            tickLabelBandsDimension += info.getPadding().extendHeight((double)fm.getHeight());
        }
        if (RectangleEdge.isTopOrBottom(edge)) {
            labelHeight = labelEnclosure.getHeight();
            space.add(labelHeight + tickLabelBandsDimension, edge);
        }
        else if (RectangleEdge.isLeftOrRight(edge)) {
            labelWidth = labelEnclosure.getWidth();
            space.add(labelWidth + tickLabelBandsDimension, edge);
        }
        double tickMarkSpace = 0.0;
        if (this.isTickMarksVisible()) {
            tickMarkSpace = this.getTickMarkOutsideLength();
        }
        if (this.minorTickMarksVisible) {
            tickMarkSpace = Math.max(tickMarkSpace, this.minorTickMarkOutsideLength);
        }
        space.add(tickMarkSpace, edge);
        return space;
    }
    
    public AxisState draw(final Graphics2D g2, final double cursor, final Rectangle2D plotArea, final Rectangle2D dataArea, final RectangleEdge edge, final PlotRenderingInfo plotState) {
        AxisState axisState = new AxisState(cursor);
        if (this.isAxisLineVisible()) {
            this.drawAxisLine(g2, cursor, dataArea, edge);
        }
        this.drawTickMarks(g2, axisState, dataArea, edge);
        for (int band = 0; band < this.labelInfo.length; ++band) {
            axisState = this.drawTickLabels(band, g2, axisState, dataArea, edge);
        }
        axisState = this.drawLabel(this.getLabel(), g2, plotArea, dataArea, edge, axisState);
        return axisState;
    }
    
    protected void drawTickMarks(final Graphics2D g2, final AxisState state, final Rectangle2D dataArea, final RectangleEdge edge) {
        if (RectangleEdge.isTopOrBottom(edge)) {
            this.drawTickMarksHorizontal(g2, state, dataArea, edge);
        }
        else if (RectangleEdge.isLeftOrRight(edge)) {
            this.drawTickMarksVertical(g2, state, dataArea, edge);
        }
    }
    
    protected void drawTickMarksHorizontal(final Graphics2D g2, final AxisState state, final Rectangle2D dataArea, final RectangleEdge edge) {
        final List ticks = new ArrayList();
        double x0 = dataArea.getX();
        final double y0 = state.getCursor();
        final double insideLength = this.getTickMarkInsideLength();
        final double outsideLength = this.getTickMarkOutsideLength();
        RegularTimePeriod t = RegularTimePeriod.createInstance(this.majorTickTimePeriodClass, this.first.getStart(), this.getTimeZone());
        long t2 = t.getFirstMillisecond(this.getTimeZone());
        Line2D inside = null;
        Line2D outside = null;
        final long firstOnAxis = this.getFirst().getFirstMillisecond(this.getTimeZone());
        for (long lastOnAxis = this.getLast().getLastMillisecond(this.getTimeZone()); t2 <= lastOnAxis; t2 = t.getFirstMillisecond(this.getTimeZone())) {
            ticks.add(new NumberTick(new Double((double)t2), "", TextAnchor.CENTER, TextAnchor.CENTER, 0.0));
            x0 = this.valueToJava2D((double)t2, dataArea, edge);
            if (edge == RectangleEdge.TOP) {
                inside = new Line2D.Double(x0, y0, x0, y0 + insideLength);
                outside = new Line2D.Double(x0, y0, x0, y0 - outsideLength);
            }
            else if (edge == RectangleEdge.BOTTOM) {
                inside = new Line2D.Double(x0, y0, x0, y0 - insideLength);
                outside = new Line2D.Double(x0, y0, x0, y0 + outsideLength);
            }
            if (t2 > firstOnAxis) {
                g2.setPaint(this.getTickMarkPaint());
                g2.setStroke(this.getTickMarkStroke());
                g2.draw(inside);
                g2.draw(outside);
            }
            if (this.minorTickMarksVisible) {
                RegularTimePeriod tminor = RegularTimePeriod.createInstance(this.minorTickTimePeriodClass, new Date(t2), this.getTimeZone());
                for (long tt0 = tminor.getFirstMillisecond(this.getTimeZone()); tt0 < t.getLastMillisecond(this.getTimeZone()) && tt0 < lastOnAxis; tt0 = tminor.getFirstMillisecond(this.getTimeZone())) {
                    final double xx0 = this.valueToJava2D((double)tt0, dataArea, edge);
                    if (edge == RectangleEdge.TOP) {
                        inside = new Line2D.Double(xx0, y0, xx0, y0 + this.minorTickMarkInsideLength);
                        outside = new Line2D.Double(xx0, y0, xx0, y0 - this.minorTickMarkOutsideLength);
                    }
                    else if (edge == RectangleEdge.BOTTOM) {
                        inside = new Line2D.Double(xx0, y0, xx0, y0 - this.minorTickMarkInsideLength);
                        outside = new Line2D.Double(xx0, y0, xx0, y0 + this.minorTickMarkOutsideLength);
                    }
                    if (tt0 >= firstOnAxis) {
                        g2.setPaint(this.minorTickMarkPaint);
                        g2.setStroke(this.minorTickMarkStroke);
                        g2.draw(inside);
                        g2.draw(outside);
                    }
                    tminor = tminor.next();
                }
            }
            t = t.next();
        }
        if (edge == RectangleEdge.TOP) {
            state.cursorUp(Math.max(outsideLength, this.minorTickMarkOutsideLength));
        }
        else if (edge == RectangleEdge.BOTTOM) {
            state.cursorDown(Math.max(outsideLength, this.minorTickMarkOutsideLength));
        }
        state.setTicks(ticks);
    }
    
    protected void drawTickMarksVertical(final Graphics2D g2, final AxisState state, final Rectangle2D dataArea, final RectangleEdge edge) {
    }
    
    protected AxisState drawTickLabels(final int band, final Graphics2D g2, final AxisState state, final Rectangle2D dataArea, final RectangleEdge edge) {
        double delta1 = 0.0;
        final FontMetrics fm = g2.getFontMetrics(this.labelInfo[band].getLabelFont());
        if (edge == RectangleEdge.BOTTOM) {
            delta1 = this.labelInfo[band].getPadding().calculateTopOutset((double)fm.getHeight());
        }
        else if (edge == RectangleEdge.TOP) {
            delta1 = this.labelInfo[band].getPadding().calculateBottomOutset((double)fm.getHeight());
        }
        state.moveCursor(delta1, edge);
        final long axisMin = this.first.getFirstMillisecond(this.timeZone);
        final long axisMax = this.last.getLastMillisecond(this.timeZone);
        g2.setFont(this.labelInfo[band].getLabelFont());
        g2.setPaint(this.labelInfo[band].getLabelPaint());
        final RegularTimePeriod p1 = this.labelInfo[band].createInstance(new Date(axisMin), this.timeZone);
        final RegularTimePeriod p2 = this.labelInfo[band].createInstance(new Date(axisMax), this.timeZone);
        final String label1 = this.labelInfo[band].getDateFormat().format(new Date(p1.getMiddleMillisecond(this.timeZone)));
        final String label2 = this.labelInfo[band].getDateFormat().format(new Date(p2.getMiddleMillisecond(this.timeZone)));
        final Rectangle2D b1 = TextUtilities.getTextBounds(label1, g2, g2.getFontMetrics());
        final Rectangle2D b2 = TextUtilities.getTextBounds(label2, g2, g2.getFontMetrics());
        final double w = Math.max(b1.getWidth(), b2.getWidth());
        final long ww = Math.round(this.java2DToValue(dataArea.getX() + w + 5.0, dataArea, edge)) - axisMin;
        final long length = p1.getLastMillisecond(this.timeZone) - p1.getFirstMillisecond(this.timeZone);
        final int periods = (int)(ww / length) + 1;
        RegularTimePeriod p3 = this.labelInfo[band].createInstance(new Date(axisMin), this.timeZone);
        Rectangle2D b3 = null;
        long lastXX = 0L;
        final float y = (float)state.getCursor();
        TextAnchor anchor = TextAnchor.TOP_CENTER;
        float yDelta = (float)b1.getHeight();
        if (edge == RectangleEdge.TOP) {
            anchor = TextAnchor.BOTTOM_CENTER;
            yDelta = -yDelta;
        }
        while (p3.getFirstMillisecond(this.timeZone) <= axisMax) {
            float x = (float)this.valueToJava2D((double)p3.getMiddleMillisecond(this.timeZone), dataArea, edge);
            final DateFormat df = this.labelInfo[band].getDateFormat();
            String label3 = df.format(new Date(p3.getMiddleMillisecond(this.timeZone)));
            final long first = p3.getFirstMillisecond(this.timeZone);
            final long last = p3.getLastMillisecond(this.timeZone);
            if (last > axisMax) {
                final Rectangle2D bb = TextUtilities.getTextBounds(label3, g2, g2.getFontMetrics());
                if (x + bb.getWidth() / 2.0 > dataArea.getMaxX()) {
                    final float xstart = (float)this.valueToJava2D((double)Math.max(first, axisMin), dataArea, edge);
                    if (bb.getWidth() < dataArea.getMaxX() - xstart) {
                        x = ((float)dataArea.getMaxX() + xstart) / 2.0f;
                    }
                    else {
                        label3 = null;
                    }
                }
            }
            if (first < axisMin) {
                final Rectangle2D bb = TextUtilities.getTextBounds(label3, g2, g2.getFontMetrics());
                if (x - bb.getWidth() / 2.0 < dataArea.getX()) {
                    final float xlast = (float)this.valueToJava2D((double)Math.min(last, axisMax), dataArea, edge);
                    if (bb.getWidth() < xlast - dataArea.getX()) {
                        x = (xlast + (float)dataArea.getX()) / 2.0f;
                    }
                    else {
                        label3 = null;
                    }
                }
            }
            if (label3 != null) {
                g2.setPaint(this.labelInfo[band].getLabelPaint());
                b3 = TextUtilities.drawAlignedString(label3, g2, x, y, anchor);
            }
            if (lastXX > 0L && this.labelInfo[band].getDrawDividers()) {
                final long nextXX = p3.getFirstMillisecond(this.timeZone);
                final long mid = (lastXX + nextXX) / 2L;
                final float mid2d = (float)this.valueToJava2D((double)mid, dataArea, edge);
                g2.setStroke(this.labelInfo[band].getDividerStroke());
                g2.setPaint(this.labelInfo[band].getDividerPaint());
                g2.draw(new Line2D.Float(mid2d, y, mid2d, y + yDelta));
            }
            lastXX = last;
            for (int i = 0; i < periods; ++i) {
                p3 = p3.next();
            }
        }
        double used = 0.0;
        if (b3 != null) {
            used = b3.getHeight();
            if (edge == RectangleEdge.BOTTOM) {
                used += this.labelInfo[band].getPadding().calculateBottomOutset((double)fm.getHeight());
            }
            else if (edge == RectangleEdge.TOP) {
                used += this.labelInfo[band].getPadding().calculateTopOutset((double)fm.getHeight());
            }
        }
        state.moveCursor(used, edge);
        return state;
    }
    
    public List refreshTicks(final Graphics2D g2, final AxisState state, final Rectangle2D dataArea, final RectangleEdge edge) {
        return Collections.EMPTY_LIST;
    }
    
    public double valueToJava2D(final double value, final Rectangle2D area, final RectangleEdge edge) {
        double result = Double.NaN;
        final double axisMin = (double)this.first.getFirstMillisecond(this.timeZone);
        final double axisMax = (double)this.last.getLastMillisecond(this.timeZone);
        if (RectangleEdge.isTopOrBottom(edge)) {
            final double minX = area.getX();
            final double maxX = area.getMaxX();
            if (this.isInverted()) {
                result = maxX + (value - axisMin) / (axisMax - axisMin) * (minX - maxX);
            }
            else {
                result = minX + (value - axisMin) / (axisMax - axisMin) * (maxX - minX);
            }
        }
        else if (RectangleEdge.isLeftOrRight(edge)) {
            final double minY = area.getMinY();
            final double maxY = area.getMaxY();
            if (this.isInverted()) {
                result = minY + (value - axisMin) / (axisMax - axisMin) * (maxY - minY);
            }
            else {
                result = maxY - (value - axisMin) / (axisMax - axisMin) * (maxY - minY);
            }
        }
        return result;
    }
    
    public double java2DToValue(final double java2DValue, final Rectangle2D area, final RectangleEdge edge) {
        double result = Double.NaN;
        double min = 0.0;
        double max = 0.0;
        final double axisMin = (double)this.first.getFirstMillisecond(this.timeZone);
        final double axisMax = (double)this.last.getLastMillisecond(this.timeZone);
        if (RectangleEdge.isTopOrBottom(edge)) {
            min = area.getX();
            max = area.getMaxX();
        }
        else if (RectangleEdge.isLeftOrRight(edge)) {
            min = area.getMaxY();
            max = area.getY();
        }
        if (this.isInverted()) {
            result = axisMax - (java2DValue - min) / (max - min) * (axisMax - axisMin);
        }
        else {
            result = axisMin + (java2DValue - min) / (max - min) * (axisMax - axisMin);
        }
        return result;
    }
    
    protected void autoAdjustRange() {
        final Plot plot = this.getPlot();
        if (plot == null) {
            return;
        }
        if (plot instanceof ValueAxisPlot) {
            final ValueAxisPlot vap = (ValueAxisPlot)plot;
            Range r = vap.getDataRange(this);
            if (r == null) {
                r = new Range(0.0, 1.0);
            }
            final long upper = Math.round(r.getUpperBound());
            final long lower = Math.round(r.getLowerBound());
            this.first = this.createInstance(this.autoRangeTimePeriodClass, new Date(lower), this.timeZone);
            this.last = this.createInstance(this.autoRangeTimePeriodClass, new Date(upper), this.timeZone);
            this.setRange(r, false, false);
        }
    }
    
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof PeriodAxis && super.equals(obj)) {
            final PeriodAxis that = (PeriodAxis)obj;
            return this.first.equals(that.first) && this.last.equals(that.last) && this.timeZone.equals(that.timeZone) && this.autoRangeTimePeriodClass.equals(that.autoRangeTimePeriodClass) && this.isMinorTickMarksVisible() == that.isMinorTickMarksVisible() && this.majorTickTimePeriodClass.equals(that.majorTickTimePeriodClass) && this.minorTickTimePeriodClass.equals(that.minorTickTimePeriodClass) && this.minorTickMarkPaint.equals(that.minorTickMarkPaint) && this.minorTickMarkStroke.equals(that.minorTickMarkStroke) && Arrays.equals(this.labelInfo, that.labelInfo);
        }
        return false;
    }
    
    public int hashCode() {
        if (this.getLabel() != null) {
            return this.getLabel().hashCode();
        }
        return 0;
    }
    
    public Object clone() throws CloneNotSupportedException {
        final PeriodAxis clone = (PeriodAxis)super.clone();
        clone.timeZone = (TimeZone)this.timeZone.clone();
        clone.labelInfo = new PeriodAxisLabelInfo[this.labelInfo.length];
        for (int i = 0; i < this.labelInfo.length; ++i) {
            clone.labelInfo[i] = this.labelInfo[i];
        }
        return clone;
    }
    
    private RegularTimePeriod createInstance(final Class periodClass, final Date millisecond, final TimeZone zone) {
        RegularTimePeriod result = null;
        try {
            final Constructor c = periodClass.getDeclaredConstructor(Date.class, TimeZone.class);
            result = c.newInstance(millisecond, zone);
        }
        catch (final Exception ex) {}
        return result;
    }
    
    private void writeObject(final ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        SerialUtilities.writeStroke(this.minorTickMarkStroke, stream);
        SerialUtilities.writePaint(this.minorTickMarkPaint, stream);
    }
    
    private void readObject(final ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        this.minorTickMarkStroke = SerialUtilities.readStroke(stream);
        this.minorTickMarkPaint = SerialUtilities.readPaint(stream);
    }
}
