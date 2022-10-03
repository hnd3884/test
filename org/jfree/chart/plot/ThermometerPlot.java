package org.jfree.chart.plot;

import java.io.ObjectInputStream;
import java.io.IOException;
import org.jfree.io.SerialUtilities;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import org.jfree.util.PaintUtilities;
import org.jfree.util.ObjectUtilities;
import org.jfree.chart.LegendItemCollection;
import org.jfree.data.Range;
import java.awt.FontMetrics;
import java.awt.geom.Line2D;
import org.jfree.ui.RectangleEdge;
import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.Graphics2D;
import org.jfree.chart.event.PlotChangeEvent;
import org.jfree.chart.event.AxisChangeListener;
import org.jfree.data.general.Dataset;
import org.jfree.data.general.DatasetChangeEvent;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.data.general.DatasetChangeListener;
import org.jfree.util.UnitType;
import java.text.DecimalFormat;
import java.awt.Color;
import java.awt.BasicStroke;
import org.jfree.data.general.DefaultValueDataset;
import java.util.ResourceBundle;
import java.text.NumberFormat;
import java.awt.Font;
import java.awt.Paint;
import java.awt.Stroke;
import org.jfree.ui.RectangleInsets;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.data.general.ValueDataset;
import java.io.Serializable;

public class ThermometerPlot extends Plot implements ValueAxisPlot, Zoomable, Cloneable, Serializable
{
    private static final long serialVersionUID = 4087093313147984390L;
    public static final int UNITS_NONE = 0;
    public static final int UNITS_FAHRENHEIT = 1;
    public static final int UNITS_CELCIUS = 2;
    public static final int UNITS_KELVIN = 3;
    public static final int NONE = 0;
    public static final int RIGHT = 1;
    public static final int LEFT = 2;
    public static final int BULB = 3;
    public static final int NORMAL = 0;
    public static final int WARNING = 1;
    public static final int CRITICAL = 2;
    protected static final int BULB_RADIUS = 40;
    protected static final int BULB_DIAMETER = 80;
    protected static final int COLUMN_RADIUS = 20;
    protected static final int COLUMN_DIAMETER = 40;
    protected static final int GAP_RADIUS = 5;
    protected static final int GAP_DIAMETER = 10;
    protected static final int AXIS_GAP = 10;
    protected static final String[] UNITS;
    protected static final int RANGE_LOW = 0;
    protected static final int RANGE_HIGH = 1;
    protected static final int DISPLAY_LOW = 2;
    protected static final int DISPLAY_HIGH = 3;
    protected static final double DEFAULT_LOWER_BOUND = 0.0;
    protected static final double DEFAULT_UPPER_BOUND = 100.0;
    private ValueDataset dataset;
    private ValueAxis rangeAxis;
    private double lowerBound;
    private double upperBound;
    private RectangleInsets padding;
    private transient Stroke thermometerStroke;
    private transient Paint thermometerPaint;
    private int units;
    private int valueLocation;
    private int axisLocation;
    private Font valueFont;
    private transient Paint valuePaint;
    private NumberFormat valueFormat;
    private transient Paint mercuryPaint;
    private boolean showValueLines;
    private int subrange;
    private double[][] subrangeInfo;
    private boolean followDataInSubranges;
    private boolean useSubrangePaint;
    private Paint[] subrangePaint;
    private boolean subrangeIndicatorsVisible;
    private transient Stroke subrangeIndicatorStroke;
    private transient Stroke rangeIndicatorStroke;
    protected static ResourceBundle localizationResources;
    
    public ThermometerPlot() {
        this(new DefaultValueDataset());
    }
    
    public ThermometerPlot(final ValueDataset dataset) {
        this.lowerBound = 0.0;
        this.upperBound = 100.0;
        this.thermometerStroke = new BasicStroke(1.0f);
        this.thermometerPaint = Color.black;
        this.units = 2;
        this.valueLocation = 3;
        this.axisLocation = 2;
        this.valueFont = new Font("SansSerif", 1, 16);
        this.valuePaint = Color.white;
        this.valueFormat = new DecimalFormat();
        this.mercuryPaint = Color.lightGray;
        this.showValueLines = false;
        this.subrange = -1;
        this.subrangeInfo = new double[][] { { 0.0, 50.0, 0.0, 50.0 }, { 50.0, 75.0, 50.0, 75.0 }, { 75.0, 100.0, 75.0, 100.0 } };
        this.followDataInSubranges = false;
        this.useSubrangePaint = true;
        this.subrangePaint = new Paint[] { Color.green, Color.orange, Color.red };
        this.subrangeIndicatorsVisible = true;
        this.subrangeIndicatorStroke = new BasicStroke(2.0f);
        this.rangeIndicatorStroke = new BasicStroke(3.0f);
        this.padding = new RectangleInsets(UnitType.RELATIVE, 0.05, 0.05, 0.05, 0.05);
        this.dataset = dataset;
        if (dataset != null) {
            dataset.addChangeListener(this);
        }
        final NumberAxis axis = new NumberAxis(null);
        axis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        axis.setAxisLineVisible(false);
        this.setRangeAxis(axis);
        this.setAxisRange();
    }
    
    public ValueDataset getDataset() {
        return this.dataset;
    }
    
    public void setDataset(final ValueDataset dataset) {
        final ValueDataset existing = this.dataset;
        if (existing != null) {
            existing.removeChangeListener(this);
        }
        if ((this.dataset = dataset) != null) {
            this.setDatasetGroup(dataset.getGroup());
            dataset.addChangeListener(this);
        }
        final DatasetChangeEvent event = new DatasetChangeEvent(this, dataset);
        this.datasetChanged(event);
    }
    
    public ValueAxis getRangeAxis() {
        return this.rangeAxis;
    }
    
    public void setRangeAxis(final ValueAxis axis) {
        if (axis != null) {
            axis.setPlot(this);
            axis.addChangeListener(this);
        }
        if (this.rangeAxis != null) {
            this.rangeAxis.removeChangeListener(this);
        }
        this.rangeAxis = axis;
    }
    
    public double getLowerBound() {
        return this.lowerBound;
    }
    
    public void setLowerBound(final double lower) {
        this.lowerBound = lower;
        this.setAxisRange();
    }
    
    public double getUpperBound() {
        return this.upperBound;
    }
    
    public void setUpperBound(final double upper) {
        this.upperBound = upper;
        this.setAxisRange();
    }
    
    public void setRange(final double lower, final double upper) {
        this.lowerBound = lower;
        this.upperBound = upper;
        this.setAxisRange();
    }
    
    public RectangleInsets getPadding() {
        return this.padding;
    }
    
    public void setPadding(final RectangleInsets padding) {
        this.padding = padding;
        this.notifyListeners(new PlotChangeEvent(this));
    }
    
    public Stroke getThermometerStroke() {
        return this.thermometerStroke;
    }
    
    public void setThermometerStroke(final Stroke s) {
        if (s != null) {
            this.thermometerStroke = s;
            this.notifyListeners(new PlotChangeEvent(this));
        }
    }
    
    public Paint getThermometerPaint() {
        return this.thermometerPaint;
    }
    
    public void setThermometerPaint(final Paint paint) {
        if (paint != null) {
            this.thermometerPaint = paint;
            this.notifyListeners(new PlotChangeEvent(this));
        }
    }
    
    public int getUnits() {
        return this.units;
    }
    
    public void setUnits(final int u) {
        if (u >= 0 && u < ThermometerPlot.UNITS.length && this.units != u) {
            this.units = u;
            this.notifyListeners(new PlotChangeEvent(this));
        }
    }
    
    public void setUnits(String u) {
        if (u == null) {
            return;
        }
        u = u.toUpperCase().trim();
        for (int i = 0; i < ThermometerPlot.UNITS.length; ++i) {
            if (u.equals(ThermometerPlot.UNITS[i].toUpperCase().trim())) {
                this.setUnits(i);
                i = ThermometerPlot.UNITS.length;
            }
        }
    }
    
    public int getValueLocation() {
        return this.valueLocation;
    }
    
    public void setValueLocation(final int location) {
        if (location >= 0 && location < 4) {
            this.valueLocation = location;
            this.notifyListeners(new PlotChangeEvent(this));
            return;
        }
        throw new IllegalArgumentException("Location not recognised.");
    }
    
    public void setAxisLocation(final int location) {
        if (location >= 0 && location < 3) {
            this.axisLocation = location;
            this.notifyListeners(new PlotChangeEvent(this));
            return;
        }
        throw new IllegalArgumentException("Location not recognised.");
    }
    
    public int getAxisLocation() {
        return this.axisLocation;
    }
    
    public Font getValueFont() {
        return this.valueFont;
    }
    
    public void setValueFont(final Font f) {
        if (f != null && !this.valueFont.equals(f)) {
            this.valueFont = f;
            this.notifyListeners(new PlotChangeEvent(this));
        }
    }
    
    public Paint getValuePaint() {
        return this.valuePaint;
    }
    
    public void setValuePaint(final Paint p) {
        if (p != null && !this.valuePaint.equals(p)) {
            this.valuePaint = p;
            this.notifyListeners(new PlotChangeEvent(this));
        }
    }
    
    public void setValueFormat(final NumberFormat formatter) {
        if (formatter != null) {
            this.valueFormat = formatter;
            this.notifyListeners(new PlotChangeEvent(this));
        }
    }
    
    public Paint getMercuryPaint() {
        return this.mercuryPaint;
    }
    
    public void setMercuryPaint(final Paint paint) {
        this.mercuryPaint = paint;
        this.notifyListeners(new PlotChangeEvent(this));
    }
    
    public boolean getShowValueLines() {
        return this.showValueLines;
    }
    
    public void setShowValueLines(final boolean b) {
        this.showValueLines = b;
        this.notifyListeners(new PlotChangeEvent(this));
    }
    
    public void setSubrangeInfo(final int range, final double low, final double hi) {
        this.setSubrangeInfo(range, low, hi, low, hi);
    }
    
    public void setSubrangeInfo(final int range, final double rangeLow, final double rangeHigh, final double displayLow, final double displayHigh) {
        if (range >= 0 && range < 3) {
            this.setSubrange(range, rangeLow, rangeHigh);
            this.setDisplayRange(range, displayLow, displayHigh);
            this.setAxisRange();
            this.notifyListeners(new PlotChangeEvent(this));
        }
    }
    
    public void setSubrange(final int range, final double low, final double high) {
        if (range >= 0 && range < 3) {
            this.subrangeInfo[range][1] = high;
            this.subrangeInfo[range][0] = low;
        }
    }
    
    public void setDisplayRange(final int range, final double low, final double high) {
        if (range >= 0 && range < this.subrangeInfo.length && isValidNumber(high) && isValidNumber(low)) {
            if (high > low) {
                this.subrangeInfo[range][3] = high;
                this.subrangeInfo[range][2] = low;
            }
            else {
                this.subrangeInfo[range][3] = low;
                this.subrangeInfo[range][2] = high;
            }
        }
    }
    
    public Paint getSubrangePaint(final int range) {
        if (range >= 0 && range < this.subrangePaint.length) {
            return this.subrangePaint[range];
        }
        return this.mercuryPaint;
    }
    
    public void setSubrangePaint(final int range, final Paint paint) {
        if (range >= 0 && range < this.subrangePaint.length && paint != null) {
            this.subrangePaint[range] = paint;
            this.notifyListeners(new PlotChangeEvent(this));
        }
    }
    
    public boolean getFollowDataInSubranges() {
        return this.followDataInSubranges;
    }
    
    public void setFollowDataInSubranges(final boolean flag) {
        this.followDataInSubranges = flag;
        this.notifyListeners(new PlotChangeEvent(this));
    }
    
    public boolean getUseSubrangePaint() {
        return this.useSubrangePaint;
    }
    
    public void setUseSubrangePaint(final boolean flag) {
        this.useSubrangePaint = flag;
        this.notifyListeners(new PlotChangeEvent(this));
    }
    
    public void draw(final Graphics2D g2, final Rectangle2D area, final Point2D anchor, final PlotState parentState, final PlotRenderingInfo info) {
        final RoundRectangle2D outerStem = new RoundRectangle2D.Double();
        final RoundRectangle2D innerStem = new RoundRectangle2D.Double();
        final RoundRectangle2D mercuryStem = new RoundRectangle2D.Double();
        final Ellipse2D outerBulb = new Ellipse2D.Double();
        final Ellipse2D innerBulb = new Ellipse2D.Double();
        String temp = null;
        FontMetrics metrics = null;
        if (info != null) {
            info.setPlotArea(area);
        }
        final RectangleInsets insets = this.getInsets();
        insets.trim(area);
        this.drawBackground(g2, area);
        final int midX = (int)(area.getX() + area.getWidth() / 2.0);
        final int midY = (int)(area.getY() + area.getHeight() / 2.0);
        final int stemTop = (int)(area.getMinY() + 40.0);
        final int stemBottom = (int)(area.getMaxY() - 80.0);
        final Rectangle2D dataArea = new Rectangle2D.Double(midX - 20, stemTop, 20.0, stemBottom - stemTop);
        outerBulb.setFrame(midX - 40, stemBottom, 80.0, 80.0);
        outerStem.setRoundRect(midX - 20, area.getMinY(), 40.0, stemBottom + 80 - stemTop, 40.0, 40.0);
        final Area outerThermometer = new Area(outerBulb);
        Area tempArea = new Area(outerStem);
        outerThermometer.add(tempArea);
        innerBulb.setFrame(midX - 40 + 5, stemBottom + 5, 70.0, 70.0);
        innerStem.setRoundRect(midX - 20 + 5, area.getMinY() + 5.0, 30.0, stemBottom + 80 - 10 - stemTop, 30.0, 30.0);
        final Area innerThermometer = new Area(innerBulb);
        tempArea = new Area(innerStem);
        innerThermometer.add(tempArea);
        if (this.dataset != null && this.dataset.getValue() != null) {
            final double current = this.dataset.getValue().doubleValue();
            final double ds = this.rangeAxis.valueToJava2D(current, dataArea, RectangleEdge.LEFT);
            int i = 30;
            final int j = 15;
            int l = i / 2;
            int k = (int)Math.round(ds);
            if (k < 5.0 + area.getMinY()) {
                k = (int)(5.0 + area.getMinY());
                l = 40;
            }
            final Area mercury = new Area(innerBulb);
            if (k < stemBottom + 40) {
                mercuryStem.setRoundRect(midX - j, k, i, stemBottom + 40 - k, l, l);
                tempArea = new Area(mercuryStem);
                mercury.add(tempArea);
            }
            g2.setPaint(this.getCurrentPaint());
            g2.fill(mercury);
            if (this.subrangeIndicatorsVisible) {
                g2.setStroke(this.subrangeIndicatorStroke);
                final Range range = this.rangeAxis.getRange();
                double value = this.subrangeInfo[0][0];
                if (range.contains(value)) {
                    final double x = midX + 20 + 2;
                    final double y = this.rangeAxis.valueToJava2D(value, dataArea, RectangleEdge.LEFT);
                    final Line2D line = new Line2D.Double(x, y, x + 10.0, y);
                    g2.setPaint(this.subrangePaint[0]);
                    g2.draw(line);
                }
                value = this.subrangeInfo[1][0];
                if (range.contains(value)) {
                    final double x = midX + 20 + 2;
                    final double y = this.rangeAxis.valueToJava2D(value, dataArea, RectangleEdge.LEFT);
                    final Line2D line = new Line2D.Double(x, y, x + 10.0, y);
                    g2.setPaint(this.subrangePaint[1]);
                    g2.draw(line);
                }
                value = this.subrangeInfo[2][0];
                if (range.contains(value)) {
                    final double x = midX + 20 + 2;
                    final double y = this.rangeAxis.valueToJava2D(value, dataArea, RectangleEdge.LEFT);
                    final Line2D line = new Line2D.Double(x, y, x + 10.0, y);
                    g2.setPaint(this.subrangePaint[2]);
                    g2.draw(line);
                }
            }
            if (this.rangeAxis != null && this.axisLocation != 0) {
                int drawWidth = 10;
                if (this.showValueLines) {
                    drawWidth += 40;
                }
                double cursor = 0.0;
                switch (this.axisLocation) {
                    case 1: {
                        cursor = midX + 20;
                        final Rectangle2D drawArea = new Rectangle2D.Double(cursor, stemTop, drawWidth, stemBottom - stemTop + 1);
                        this.rangeAxis.draw(g2, cursor, area, drawArea, RectangleEdge.RIGHT, null);
                        break;
                    }
                    default: {
                        cursor = midX - 20;
                        final Rectangle2D drawArea = new Rectangle2D.Double(cursor, stemTop, drawWidth, stemBottom - stemTop + 1);
                        this.rangeAxis.draw(g2, cursor, area, drawArea, RectangleEdge.LEFT, null);
                        break;
                    }
                }
            }
            g2.setFont(this.valueFont);
            g2.setPaint(this.valuePaint);
            metrics = g2.getFontMetrics();
            switch (this.valueLocation) {
                case 1: {
                    g2.drawString(this.valueFormat.format(current), midX + 20 + 5, midY);
                    break;
                }
                case 2: {
                    final String valueString = this.valueFormat.format(current);
                    final int stringWidth = metrics.stringWidth(valueString);
                    g2.drawString(valueString, midX - 20 - 5 - stringWidth, midY);
                    break;
                }
                case 3: {
                    temp = this.valueFormat.format(current);
                    i = metrics.stringWidth(temp) / 2;
                    g2.drawString(temp, midX - i, stemBottom + 40 + 5);
                    break;
                }
            }
        }
        g2.setPaint(this.thermometerPaint);
        g2.setFont(this.valueFont);
        metrics = g2.getFontMetrics();
        final int tickX1 = midX - 20 - 10 - metrics.stringWidth(ThermometerPlot.UNITS[this.units]);
        if (tickX1 > area.getMinX()) {
            g2.drawString(ThermometerPlot.UNITS[this.units], tickX1, (int)(area.getMinY() + 20.0));
        }
        g2.setStroke(this.thermometerStroke);
        g2.draw(outerThermometer);
        g2.draw(innerThermometer);
        this.drawOutline(g2, area);
    }
    
    public void zoom(final double percent) {
    }
    
    public String getPlotType() {
        return ThermometerPlot.localizationResources.getString("Thermometer_Plot");
    }
    
    public void datasetChanged(final DatasetChangeEvent event) {
        final Number vn = this.dataset.getValue();
        if (vn != null) {
            final double value = vn.doubleValue();
            if (this.inSubrange(0, value)) {
                this.subrange = 0;
            }
            else if (this.inSubrange(1, value)) {
                this.subrange = 1;
            }
            else if (this.inSubrange(2, value)) {
                this.subrange = 2;
            }
            else {
                this.subrange = -1;
            }
            this.setAxisRange();
        }
        super.datasetChanged(event);
    }
    
    public Number getMinimumVerticalDataValue() {
        return new Double(this.lowerBound);
    }
    
    public Number getMaximumVerticalDataValue() {
        return new Double(this.upperBound);
    }
    
    public Range getDataRange(final ValueAxis axis) {
        return new Range(this.lowerBound, this.upperBound);
    }
    
    protected void setAxisRange() {
        if (this.subrange >= 0 && this.followDataInSubranges) {
            this.rangeAxis.setRange(new Range(this.subrangeInfo[this.subrange][2], this.subrangeInfo[this.subrange][3]));
        }
        else {
            this.rangeAxis.setRange(this.lowerBound, this.upperBound);
        }
    }
    
    public LegendItemCollection getLegendItems() {
        return null;
    }
    
    public PlotOrientation getOrientation() {
        return PlotOrientation.VERTICAL;
    }
    
    protected static boolean isValidNumber(final double d) {
        return !Double.isNaN(d) && !Double.isInfinite(d);
    }
    
    private boolean inSubrange(final int subrange, final double value) {
        return value > this.subrangeInfo[subrange][0] && value <= this.subrangeInfo[subrange][1];
    }
    
    private Paint getCurrentPaint() {
        Paint result = this.mercuryPaint;
        if (this.useSubrangePaint) {
            final double value = this.dataset.getValue().doubleValue();
            if (this.inSubrange(0, value)) {
                result = this.subrangePaint[0];
            }
            else if (this.inSubrange(1, value)) {
                result = this.subrangePaint[1];
            }
            else if (this.inSubrange(2, value)) {
                result = this.subrangePaint[2];
            }
        }
        return result;
    }
    
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof ThermometerPlot)) {
            return false;
        }
        final ThermometerPlot that = (ThermometerPlot)obj;
        if (!super.equals(obj)) {
            return false;
        }
        if (!ObjectUtilities.equal((Object)this.rangeAxis, (Object)that.rangeAxis)) {
            return false;
        }
        if (this.axisLocation != that.axisLocation) {
            return false;
        }
        if (this.lowerBound != that.lowerBound) {
            return false;
        }
        if (this.upperBound != that.upperBound) {
            return false;
        }
        if (!ObjectUtilities.equal((Object)this.padding, (Object)that.padding)) {
            return false;
        }
        if (!ObjectUtilities.equal((Object)this.thermometerStroke, (Object)that.thermometerStroke)) {
            return false;
        }
        if (!PaintUtilities.equal(this.thermometerPaint, that.thermometerPaint)) {
            return false;
        }
        if (this.units != that.units) {
            return false;
        }
        if (this.valueLocation != that.valueLocation) {
            return false;
        }
        if (!ObjectUtilities.equal((Object)this.valueFont, (Object)that.valueFont)) {
            return false;
        }
        if (!PaintUtilities.equal(this.valuePaint, that.valuePaint)) {
            return false;
        }
        if (!ObjectUtilities.equal((Object)this.valueFormat, (Object)that.valueFormat)) {
            return false;
        }
        if (!PaintUtilities.equal(this.mercuryPaint, that.mercuryPaint)) {
            return false;
        }
        if (this.showValueLines != that.showValueLines) {
            return false;
        }
        if (this.subrange != that.subrange) {
            return false;
        }
        if (this.followDataInSubranges != that.followDataInSubranges) {
            return false;
        }
        if (!equal(this.subrangeInfo, that.subrangeInfo)) {
            return false;
        }
        if (this.useSubrangePaint != that.useSubrangePaint) {
            return false;
        }
        for (int i = 0; i < this.subrangePaint.length; ++i) {
            if (!PaintUtilities.equal(this.subrangePaint[i], that.subrangePaint[i])) {
                return false;
            }
        }
        return true;
    }
    
    private static boolean equal(final double[][] array1, final double[][] array2) {
        if (array1 == null) {
            return array2 == null;
        }
        if (array2 == null) {
            return false;
        }
        if (array1.length != array2.length) {
            return false;
        }
        for (int i = 0; i < array1.length; ++i) {
            if (!Arrays.equals(array1[i], array2[i])) {
                return false;
            }
        }
        return true;
    }
    
    public Object clone() throws CloneNotSupportedException {
        final ThermometerPlot clone = (ThermometerPlot)super.clone();
        if (clone.dataset != null) {
            clone.dataset.addChangeListener(clone);
        }
        clone.rangeAxis = (ValueAxis)ObjectUtilities.clone((Object)this.rangeAxis);
        if (clone.rangeAxis != null) {
            clone.rangeAxis.setPlot(clone);
            clone.rangeAxis.addChangeListener(clone);
        }
        clone.valueFormat = (NumberFormat)this.valueFormat.clone();
        clone.subrangePaint = this.subrangePaint.clone();
        return clone;
    }
    
    private void writeObject(final ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        SerialUtilities.writeStroke(this.thermometerStroke, stream);
        SerialUtilities.writePaint(this.thermometerPaint, stream);
        SerialUtilities.writePaint(this.valuePaint, stream);
        SerialUtilities.writePaint(this.mercuryPaint, stream);
        SerialUtilities.writeStroke(this.subrangeIndicatorStroke, stream);
        SerialUtilities.writeStroke(this.rangeIndicatorStroke, stream);
    }
    
    private void readObject(final ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        this.thermometerStroke = SerialUtilities.readStroke(stream);
        this.thermometerPaint = SerialUtilities.readPaint(stream);
        this.valuePaint = SerialUtilities.readPaint(stream);
        this.mercuryPaint = SerialUtilities.readPaint(stream);
        this.subrangeIndicatorStroke = SerialUtilities.readStroke(stream);
        this.rangeIndicatorStroke = SerialUtilities.readStroke(stream);
        if (this.rangeAxis != null) {
            this.rangeAxis.addChangeListener(this);
        }
    }
    
    public void zoomDomainAxes(final double factor, final PlotRenderingInfo state, final Point2D source) {
    }
    
    public void zoomRangeAxes(final double factor, final PlotRenderingInfo state, final Point2D source) {
        this.rangeAxis.resizeRange(factor);
    }
    
    public void zoomDomainAxes(final double lowerPercent, final double upperPercent, final PlotRenderingInfo state, final Point2D source) {
    }
    
    public void zoomRangeAxes(final double lowerPercent, final double upperPercent, final PlotRenderingInfo state, final Point2D source) {
        this.rangeAxis.zoomRange(lowerPercent, upperPercent);
    }
    
    public boolean isDomainZoomable() {
        return false;
    }
    
    public boolean isRangeZoomable() {
        return true;
    }
    
    static {
        UNITS = new String[] { "", "°F", "°C", "°K" };
        ThermometerPlot.localizationResources = ResourceBundle.getBundle("org.jfree.chart.plot.LocalizationBundle");
    }
}
