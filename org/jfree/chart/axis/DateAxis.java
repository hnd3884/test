package org.jfree.chart.axis;

import org.jfree.util.ObjectUtilities;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.data.time.Year;
import org.jfree.ui.TextAnchor;
import java.util.ArrayList;
import java.util.List;
import java.awt.FontMetrics;
import java.awt.font.LineMetrics;
import java.awt.font.FontRenderContext;
import java.awt.Font;
import org.jfree.ui.RectangleInsets;
import java.awt.Graphics2D;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.ValueAxisPlot;
import java.text.SimpleDateFormat;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.Month;
import java.util.Calendar;
import org.jfree.ui.RectangleEdge;
import java.awt.geom.Rectangle2D;
import org.jfree.chart.event.AxisChangeEvent;
import org.jfree.data.Range;
import java.util.TimeZone;
import java.text.DateFormat;
import java.util.Date;
import org.jfree.data.time.DateRange;
import java.io.Serializable;

public class DateAxis extends ValueAxis implements Cloneable, Serializable
{
    private static final long serialVersionUID = -1013460999649007604L;
    public static final DateRange DEFAULT_DATE_RANGE;
    public static final double DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS = 2.0;
    public static final DateTickUnit DEFAULT_DATE_TICK_UNIT;
    public static final Date DEFAULT_ANCHOR_DATE;
    private DateTickUnit tickUnit;
    private DateFormat dateFormatOverride;
    private DateTickMarkPosition tickMarkPosition;
    private static final Timeline DEFAULT_TIMELINE;
    private TimeZone timeZone;
    private Timeline timeline;
    
    public DateAxis() {
        this(null);
    }
    
    public DateAxis(final String label) {
        this(label, TimeZone.getDefault());
    }
    
    public DateAxis(final String label, final TimeZone zone) {
        super(label, createStandardDateTickUnits(zone));
        this.tickMarkPosition = DateTickMarkPosition.START;
        this.setTickUnit(DateAxis.DEFAULT_DATE_TICK_UNIT, false, false);
        this.setAutoRangeMinimumSize(2.0);
        this.setRange(DateAxis.DEFAULT_DATE_RANGE, false, false);
        this.dateFormatOverride = null;
        this.timeZone = zone;
        this.timeline = DateAxis.DEFAULT_TIMELINE;
    }
    
    public Timeline getTimeline() {
        return this.timeline;
    }
    
    public void setTimeline(final Timeline timeline) {
        if (this.timeline != timeline) {
            this.timeline = timeline;
            this.notifyListeners(new AxisChangeEvent(this));
        }
    }
    
    public DateTickUnit getTickUnit() {
        return this.tickUnit;
    }
    
    public void setTickUnit(final DateTickUnit unit) {
        this.setTickUnit(unit, true, true);
    }
    
    public void setTickUnit(final DateTickUnit unit, final boolean notify, final boolean turnOffAutoSelection) {
        this.tickUnit = unit;
        if (turnOffAutoSelection) {
            this.setAutoTickUnitSelection(false, false);
        }
        if (notify) {
            this.notifyListeners(new AxisChangeEvent(this));
        }
    }
    
    public DateFormat getDateFormatOverride() {
        return this.dateFormatOverride;
    }
    
    public void setDateFormatOverride(final DateFormat formatter) {
        this.dateFormatOverride = formatter;
        this.notifyListeners(new AxisChangeEvent(this));
    }
    
    public void setRange(final Range range) {
        this.setRange(range, true, true);
    }
    
    public void setRange(Range range, final boolean turnOffAutoRange, final boolean notify) {
        if (range == null) {
            throw new IllegalArgumentException("Null 'range' argument.");
        }
        if (!(range instanceof DateRange)) {
            range = new DateRange(range);
        }
        super.setRange(range, turnOffAutoRange, notify);
    }
    
    public void setRange(final Date lower, final Date upper) {
        if (lower.getTime() >= upper.getTime()) {
            throw new IllegalArgumentException("Requires 'lower' < 'upper'.");
        }
        this.setRange(new DateRange(lower, upper));
    }
    
    public void setRange(final double lower, final double upper) {
        if (lower >= upper) {
            throw new IllegalArgumentException("Requires 'lower' < 'upper'.");
        }
        this.setRange(new DateRange(lower, upper));
    }
    
    public Date getMinimumDate() {
        Date result = null;
        final Range range = this.getRange();
        if (range instanceof DateRange) {
            final DateRange r = (DateRange)range;
            result = r.getLowerDate();
        }
        else {
            result = new Date((long)range.getLowerBound());
        }
        return result;
    }
    
    public void setMinimumDate(final Date date) {
        this.setRange(new DateRange(date, this.getMaximumDate()), true, false);
        this.notifyListeners(new AxisChangeEvent(this));
    }
    
    public Date getMaximumDate() {
        Date result = null;
        final Range range = this.getRange();
        if (range instanceof DateRange) {
            final DateRange r = (DateRange)range;
            result = r.getUpperDate();
        }
        else {
            result = new Date((long)range.getUpperBound());
        }
        return result;
    }
    
    public void setMaximumDate(final Date maximumDate) {
        this.setRange(new DateRange(this.getMinimumDate(), maximumDate), true, false);
        this.notifyListeners(new AxisChangeEvent(this));
    }
    
    public DateTickMarkPosition getTickMarkPosition() {
        return this.tickMarkPosition;
    }
    
    public void setTickMarkPosition(final DateTickMarkPosition position) {
        if (position == null) {
            throw new IllegalArgumentException("Null 'position' argument.");
        }
        this.tickMarkPosition = position;
        this.notifyListeners(new AxisChangeEvent(this));
    }
    
    public void configure() {
        if (this.isAutoRange()) {
            this.autoAdjustRange();
        }
    }
    
    public boolean isHiddenValue(final long millis) {
        return !this.timeline.containsDomainValue(new Date(millis));
    }
    
    public double valueToJava2D(double value, final Rectangle2D area, final RectangleEdge edge) {
        value = (double)this.timeline.toTimelineValue((long)value);
        final DateRange range = (DateRange)this.getRange();
        final double axisMin = (double)this.timeline.toTimelineValue(range.getLowerDate());
        final double axisMax = (double)this.timeline.toTimelineValue(range.getUpperDate());
        double result = 0.0;
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
    
    public double dateToJava2D(final Date date, final Rectangle2D area, final RectangleEdge edge) {
        final double value = (double)date.getTime();
        return this.valueToJava2D(value, area, edge);
    }
    
    public double java2DToValue(final double java2DValue, final Rectangle2D area, final RectangleEdge edge) {
        final DateRange range = (DateRange)this.getRange();
        final double axisMin = (double)this.timeline.toTimelineValue(range.getLowerDate());
        final double axisMax = (double)this.timeline.toTimelineValue(range.getUpperDate());
        double min = 0.0;
        double max = 0.0;
        if (RectangleEdge.isTopOrBottom(edge)) {
            min = area.getX();
            max = area.getMaxX();
        }
        else if (RectangleEdge.isLeftOrRight(edge)) {
            min = area.getMaxY();
            max = area.getY();
        }
        double result;
        if (this.isInverted()) {
            result = axisMax - (java2DValue - min) / (max - min) * (axisMax - axisMin);
        }
        else {
            result = axisMin + (java2DValue - min) / (max - min) * (axisMax - axisMin);
        }
        return (double)this.timeline.toMillisecond((long)result);
    }
    
    public Date calculateLowestVisibleTickValue(final DateTickUnit unit) {
        return this.nextStandardDate(this.getMinimumDate(), unit);
    }
    
    public Date calculateHighestVisibleTickValue(final DateTickUnit unit) {
        return this.previousStandardDate(this.getMaximumDate(), unit);
    }
    
    protected Date previousStandardDate(final Date date, final DateTickUnit unit) {
        final Calendar calendar = Calendar.getInstance(this.timeZone);
        calendar.setTime(date);
        final int count = unit.getCount();
        final int current = calendar.get(unit.getCalendarField());
        final int value = count * (current / count);
        switch (unit.getUnit()) {
            case 6: {
                final int years = calendar.get(1);
                final int months = calendar.get(2);
                final int days = calendar.get(5);
                final int hours = calendar.get(11);
                final int minutes = calendar.get(12);
                final int seconds = calendar.get(13);
                calendar.set(years, months, days, hours, minutes, seconds);
                calendar.set(14, value);
                return calendar.getTime();
            }
            case 5: {
                final int years = calendar.get(1);
                final int months = calendar.get(2);
                final int days = calendar.get(5);
                final int hours = calendar.get(11);
                final int minutes = calendar.get(12);
                int milliseconds;
                if (this.tickMarkPosition == DateTickMarkPosition.START) {
                    milliseconds = 0;
                }
                else if (this.tickMarkPosition == DateTickMarkPosition.MIDDLE) {
                    milliseconds = 500;
                }
                else {
                    milliseconds = 999;
                }
                calendar.set(14, milliseconds);
                calendar.set(years, months, days, hours, minutes, value);
                return calendar.getTime();
            }
            case 4: {
                final int years = calendar.get(1);
                final int months = calendar.get(2);
                final int days = calendar.get(5);
                final int hours = calendar.get(11);
                int seconds;
                if (this.tickMarkPosition == DateTickMarkPosition.START) {
                    seconds = 0;
                }
                else if (this.tickMarkPosition == DateTickMarkPosition.MIDDLE) {
                    seconds = 30;
                }
                else {
                    seconds = 59;
                }
                calendar.clear(14);
                calendar.set(years, months, days, hours, value, seconds);
                return calendar.getTime();
            }
            case 3: {
                final int years = calendar.get(1);
                final int months = calendar.get(2);
                final int days = calendar.get(5);
                int minutes;
                int seconds;
                if (this.tickMarkPosition == DateTickMarkPosition.START) {
                    minutes = 0;
                    seconds = 0;
                }
                else if (this.tickMarkPosition == DateTickMarkPosition.MIDDLE) {
                    minutes = 30;
                    seconds = 0;
                }
                else {
                    minutes = 59;
                    seconds = 59;
                }
                calendar.clear(14);
                calendar.set(years, months, days, value, minutes, seconds);
                return calendar.getTime();
            }
            case 2: {
                final int years = calendar.get(1);
                final int months = calendar.get(2);
                int hours;
                if (this.tickMarkPosition == DateTickMarkPosition.START) {
                    hours = 0;
                    final int minutes = 0;
                    final int seconds = 0;
                }
                else if (this.tickMarkPosition == DateTickMarkPosition.MIDDLE) {
                    hours = 12;
                    final int minutes = 0;
                    final int seconds = 0;
                }
                else {
                    hours = 23;
                    final int minutes = 59;
                    final int seconds = 59;
                }
                calendar.clear(14);
                calendar.set(years, months, value, hours, 0, 0);
                final long result = calendar.getTime().getTime();
                if (result > date.getTime()) {
                    calendar.set(years, months, value - 1, hours, 0, 0);
                }
                return calendar.getTime();
            }
            case 1: {
                final int years = calendar.get(1);
                calendar.clear(14);
                calendar.set(years, value, 1, 0, 0, 0);
                Month month = new Month(calendar.getTime());
                Date standardDate = this.calculateDateForPosition(month, this.tickMarkPosition);
                final long millis = standardDate.getTime();
                if (millis > date.getTime()) {
                    month = (Month)month.previous();
                    standardDate = this.calculateDateForPosition(month, this.tickMarkPosition);
                }
                return standardDate;
            }
            case 0: {
                int months;
                int days;
                if (this.tickMarkPosition == DateTickMarkPosition.START) {
                    months = 0;
                    days = 1;
                }
                else if (this.tickMarkPosition == DateTickMarkPosition.MIDDLE) {
                    months = 6;
                    days = 1;
                }
                else {
                    months = 11;
                    days = 31;
                }
                calendar.clear(14);
                calendar.set(value, months, days, 0, 0, 0);
                return calendar.getTime();
            }
            default: {
                return null;
            }
        }
    }
    
    private Date calculateDateForPosition(final RegularTimePeriod period, final DateTickMarkPosition position) {
        if (position == null) {
            throw new IllegalArgumentException("Null 'position' argument.");
        }
        Date result = null;
        if (position == DateTickMarkPosition.START) {
            result = new Date(period.getFirstMillisecond());
        }
        else if (position == DateTickMarkPosition.MIDDLE) {
            result = new Date(period.getMiddleMillisecond());
        }
        else if (position == DateTickMarkPosition.END) {
            result = new Date(period.getLastMillisecond());
        }
        return result;
    }
    
    protected Date nextStandardDate(final Date date, final DateTickUnit unit) {
        final Date previous = this.previousStandardDate(date, unit);
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(previous);
        calendar.add(unit.getCalendarField(), unit.getCount());
        return calendar.getTime();
    }
    
    public static TickUnitSource createStandardDateTickUnits() {
        return createStandardDateTickUnits(TimeZone.getDefault());
    }
    
    public static TickUnitSource createStandardDateTickUnits(final TimeZone zone) {
        if (zone == null) {
            throw new IllegalArgumentException("Null 'zone' argument.");
        }
        final TickUnits units = new TickUnits();
        final DateFormat f1 = new SimpleDateFormat("HH:mm:ss.SSS");
        final DateFormat f2 = new SimpleDateFormat("HH:mm:ss");
        final DateFormat f3 = new SimpleDateFormat("HH:mm");
        final DateFormat f4 = new SimpleDateFormat("d-MMM, HH:mm");
        final DateFormat f5 = new SimpleDateFormat("d-MMM");
        final DateFormat f6 = new SimpleDateFormat("MMM-yyyy");
        final DateFormat f7 = new SimpleDateFormat("yyyy");
        f1.setTimeZone(zone);
        f2.setTimeZone(zone);
        f3.setTimeZone(zone);
        f4.setTimeZone(zone);
        f5.setTimeZone(zone);
        f6.setTimeZone(zone);
        f7.setTimeZone(zone);
        units.add(new DateTickUnit(6, 1, f1));
        units.add(new DateTickUnit(6, 5, 6, 1, f1));
        units.add(new DateTickUnit(6, 10, 6, 1, f1));
        units.add(new DateTickUnit(6, 25, 6, 5, f1));
        units.add(new DateTickUnit(6, 50, 6, 10, f1));
        units.add(new DateTickUnit(6, 100, 6, 10, f1));
        units.add(new DateTickUnit(6, 250, 6, 10, f1));
        units.add(new DateTickUnit(6, 500, 6, 50, f1));
        units.add(new DateTickUnit(5, 1, 6, 50, f2));
        units.add(new DateTickUnit(5, 5, 5, 1, f2));
        units.add(new DateTickUnit(5, 10, 5, 1, f2));
        units.add(new DateTickUnit(5, 30, 5, 5, f2));
        units.add(new DateTickUnit(4, 1, 5, 5, f3));
        units.add(new DateTickUnit(4, 2, 5, 10, f3));
        units.add(new DateTickUnit(4, 5, 4, 1, f3));
        units.add(new DateTickUnit(4, 10, 4, 1, f3));
        units.add(new DateTickUnit(4, 15, 4, 5, f3));
        units.add(new DateTickUnit(4, 20, 4, 5, f3));
        units.add(new DateTickUnit(4, 30, 4, 5, f3));
        units.add(new DateTickUnit(3, 1, 4, 5, f3));
        units.add(new DateTickUnit(3, 2, 4, 10, f3));
        units.add(new DateTickUnit(3, 4, 4, 30, f3));
        units.add(new DateTickUnit(3, 6, 3, 1, f3));
        units.add(new DateTickUnit(3, 12, 3, 1, f4));
        units.add(new DateTickUnit(2, 1, 3, 1, f5));
        units.add(new DateTickUnit(2, 2, 3, 1, f5));
        units.add(new DateTickUnit(2, 7, 2, 1, f5));
        units.add(new DateTickUnit(2, 15, 2, 1, f5));
        units.add(new DateTickUnit(1, 1, 2, 1, f6));
        units.add(new DateTickUnit(1, 2, 2, 1, f6));
        units.add(new DateTickUnit(1, 3, 1, 1, f6));
        units.add(new DateTickUnit(1, 4, 1, 1, f6));
        units.add(new DateTickUnit(1, 6, 1, 1, f6));
        units.add(new DateTickUnit(0, 1, 1, 1, f7));
        units.add(new DateTickUnit(0, 2, 1, 3, f7));
        units.add(new DateTickUnit(0, 5, 0, 1, f7));
        units.add(new DateTickUnit(0, 10, 0, 1, f7));
        units.add(new DateTickUnit(0, 25, 0, 5, f7));
        units.add(new DateTickUnit(0, 50, 0, 10, f7));
        units.add(new DateTickUnit(0, 100, 0, 20, f7));
        return units;
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
                if (this.timeline instanceof SegmentedTimeline) {
                    r = new DateRange((double)((SegmentedTimeline)this.timeline).getStartTime(), (double)(((SegmentedTimeline)this.timeline).getStartTime() + 1L));
                }
                else {
                    r = new DateRange();
                }
            }
            long upper = this.timeline.toTimelineValue((long)r.getUpperBound());
            final long fixedAutoRange = (long)this.getFixedAutoRange();
            long lower;
            if (fixedAutoRange > 0.0) {
                lower = upper - fixedAutoRange;
            }
            else {
                lower = this.timeline.toTimelineValue((long)r.getLowerBound());
                final double range = (double)(upper - lower);
                final long minRange = (long)this.getAutoRangeMinimumSize();
                if (range < minRange) {
                    final long expand = (long)(minRange - range) / 2L;
                    upper += expand;
                    lower -= expand;
                }
                upper += (long)(range * this.getUpperMargin());
                lower -= (long)(range * this.getLowerMargin());
            }
            upper = this.timeline.toMillisecond(upper);
            lower = this.timeline.toMillisecond(lower);
            final DateRange dr = new DateRange(new Date(lower), new Date(upper));
            this.setRange(dr, false, false);
        }
    }
    
    protected void selectAutoTickUnit(final Graphics2D g2, final Rectangle2D dataArea, final RectangleEdge edge) {
        if (RectangleEdge.isTopOrBottom(edge)) {
            this.selectHorizontalAutoTickUnit(g2, dataArea, edge);
        }
        else if (RectangleEdge.isLeftOrRight(edge)) {
            this.selectVerticalAutoTickUnit(g2, dataArea, edge);
        }
    }
    
    protected void selectHorizontalAutoTickUnit(final Graphics2D g2, final Rectangle2D dataArea, final RectangleEdge edge) {
        long shift = 0L;
        if (this.timeline instanceof SegmentedTimeline) {
            shift = ((SegmentedTimeline)this.timeline).getStartTime();
        }
        final double zero = this.valueToJava2D(shift + 0.0, dataArea, edge);
        double tickLabelWidth = this.estimateMaximumTickLabelWidth(g2, this.getTickUnit());
        final TickUnitSource tickUnits = this.getStandardTickUnits();
        final TickUnit unit1 = tickUnits.getCeilingTickUnit(this.getTickUnit());
        final double x1 = this.valueToJava2D(shift + unit1.getSize(), dataArea, edge);
        final double unit1Width = Math.abs(x1 - zero);
        final double guess = tickLabelWidth / unit1Width * unit1.getSize();
        DateTickUnit unit2 = (DateTickUnit)tickUnits.getCeilingTickUnit(guess);
        final double x2 = this.valueToJava2D(shift + unit2.getSize(), dataArea, edge);
        final double unit2Width = Math.abs(x2 - zero);
        tickLabelWidth = this.estimateMaximumTickLabelWidth(g2, unit2);
        if (tickLabelWidth > unit2Width) {
            unit2 = (DateTickUnit)tickUnits.getLargerTickUnit(unit2);
        }
        this.setTickUnit(unit2, false, false);
    }
    
    protected void selectVerticalAutoTickUnit(final Graphics2D g2, final Rectangle2D dataArea, final RectangleEdge edge) {
        final TickUnitSource tickUnits = this.getStandardTickUnits();
        final double zero = this.valueToJava2D(0.0, dataArea, edge);
        final double estimate1 = this.getRange().getLength() / 10.0;
        final DateTickUnit candidate1 = (DateTickUnit)tickUnits.getCeilingTickUnit(estimate1);
        final double labelHeight1 = this.estimateMaximumTickLabelHeight(g2, candidate1);
        final double y1 = this.valueToJava2D(candidate1.getSize(), dataArea, edge);
        final double candidate1UnitHeight = Math.abs(y1 - zero);
        final double estimate2 = labelHeight1 / candidate1UnitHeight * candidate1.getSize();
        final DateTickUnit candidate2 = (DateTickUnit)tickUnits.getCeilingTickUnit(estimate2);
        final double labelHeight2 = this.estimateMaximumTickLabelHeight(g2, candidate2);
        final double y2 = this.valueToJava2D(candidate2.getSize(), dataArea, edge);
        final double unit2Height = Math.abs(y2 - zero);
        DateTickUnit finalUnit;
        if (labelHeight2 < unit2Height) {
            finalUnit = candidate2;
        }
        else {
            finalUnit = (DateTickUnit)tickUnits.getLargerTickUnit(candidate2);
        }
        this.setTickUnit(finalUnit, false, false);
    }
    
    private double estimateMaximumTickLabelWidth(final Graphics2D g2, final DateTickUnit unit) {
        final RectangleInsets tickLabelInsets = this.getTickLabelInsets();
        double result = tickLabelInsets.getLeft() + tickLabelInsets.getRight();
        final Font tickLabelFont = this.getTickLabelFont();
        final FontRenderContext frc = g2.getFontRenderContext();
        final LineMetrics lm = tickLabelFont.getLineMetrics("ABCxyz", frc);
        if (this.isVerticalTickLabels()) {
            result += lm.getHeight();
        }
        else {
            final DateRange range = (DateRange)this.getRange();
            final Date lower = range.getLowerDate();
            final Date upper = range.getUpperDate();
            String lowerStr = null;
            String upperStr = null;
            final DateFormat formatter = this.getDateFormatOverride();
            if (formatter != null) {
                lowerStr = formatter.format(lower);
                upperStr = formatter.format(upper);
            }
            else {
                lowerStr = unit.dateToString(lower);
                upperStr = unit.dateToString(upper);
            }
            final FontMetrics fm = g2.getFontMetrics(tickLabelFont);
            final double w1 = fm.stringWidth(lowerStr);
            final double w2 = fm.stringWidth(upperStr);
            result += Math.max(w1, w2);
        }
        return result;
    }
    
    private double estimateMaximumTickLabelHeight(final Graphics2D g2, final DateTickUnit unit) {
        final RectangleInsets tickLabelInsets = this.getTickLabelInsets();
        double result = tickLabelInsets.getTop() + tickLabelInsets.getBottom();
        final Font tickLabelFont = this.getTickLabelFont();
        final FontRenderContext frc = g2.getFontRenderContext();
        final LineMetrics lm = tickLabelFont.getLineMetrics("ABCxyz", frc);
        if (!this.isVerticalTickLabels()) {
            result += lm.getHeight();
        }
        else {
            final DateRange range = (DateRange)this.getRange();
            final Date lower = range.getLowerDate();
            final Date upper = range.getUpperDate();
            String lowerStr = null;
            String upperStr = null;
            final DateFormat formatter = this.getDateFormatOverride();
            if (formatter != null) {
                lowerStr = formatter.format(lower);
                upperStr = formatter.format(upper);
            }
            else {
                lowerStr = unit.dateToString(lower);
                upperStr = unit.dateToString(upper);
            }
            final FontMetrics fm = g2.getFontMetrics(tickLabelFont);
            final double w1 = fm.stringWidth(lowerStr);
            final double w2 = fm.stringWidth(upperStr);
            result += Math.max(w1, w2);
        }
        return result;
    }
    
    public List refreshTicks(final Graphics2D g2, final AxisState state, final Rectangle2D dataArea, final RectangleEdge edge) {
        List result = null;
        if (RectangleEdge.isTopOrBottom(edge)) {
            result = this.refreshTicksHorizontal(g2, dataArea, edge);
        }
        else if (RectangleEdge.isLeftOrRight(edge)) {
            result = this.refreshTicksVertical(g2, dataArea, edge);
        }
        return result;
    }
    
    protected List refreshTicksHorizontal(final Graphics2D g2, final Rectangle2D dataArea, final RectangleEdge edge) {
        final List result = new ArrayList();
        final Font tickLabelFont = this.getTickLabelFont();
        g2.setFont(tickLabelFont);
        if (this.isAutoTickUnitSelection()) {
            this.selectAutoTickUnit(g2, dataArea, edge);
        }
        final DateTickUnit unit = this.getTickUnit();
        Date tickDate = this.calculateLowestVisibleTickValue(unit);
        final Date upperDate = this.getMaximumDate();
        while (tickDate.before(upperDate)) {
            if (!this.isHiddenValue(tickDate.getTime())) {
                final DateFormat formatter = this.getDateFormatOverride();
                String tickLabel;
                if (formatter != null) {
                    tickLabel = formatter.format(tickDate);
                }
                else {
                    tickLabel = this.tickUnit.dateToString(tickDate);
                }
                TextAnchor anchor = null;
                TextAnchor rotationAnchor = null;
                double angle = 0.0;
                if (this.isVerticalTickLabels()) {
                    anchor = TextAnchor.CENTER_RIGHT;
                    rotationAnchor = TextAnchor.CENTER_RIGHT;
                    if (edge == RectangleEdge.TOP) {
                        angle = 1.5707963267948966;
                    }
                    else {
                        angle = -1.5707963267948966;
                    }
                }
                else if (edge == RectangleEdge.TOP) {
                    anchor = TextAnchor.BOTTOM_CENTER;
                    rotationAnchor = TextAnchor.BOTTOM_CENTER;
                }
                else {
                    anchor = TextAnchor.TOP_CENTER;
                    rotationAnchor = TextAnchor.TOP_CENTER;
                }
                final Tick tick = new DateTick(tickDate, tickLabel, anchor, rotationAnchor, angle);
                result.add(tick);
                tickDate = unit.addToDate(tickDate);
                switch (unit.getUnit()) {
                    case 2:
                    case 3:
                    case 4:
                    case 5:
                    case 6: {
                        continue;
                    }
                    case 1: {
                        tickDate = this.calculateDateForPosition(new Month(tickDate), this.tickMarkPosition);
                        continue;
                    }
                    case 0: {
                        tickDate = this.calculateDateForPosition(new Year(tickDate), this.tickMarkPosition);
                        continue;
                    }
                    default: {
                        continue;
                    }
                }
            }
            else {
                tickDate = unit.rollDate(tickDate);
            }
        }
        return result;
    }
    
    protected List refreshTicksVertical(final Graphics2D g2, final Rectangle2D dataArea, final RectangleEdge edge) {
        final List result = new ArrayList();
        final Font tickLabelFont = this.getTickLabelFont();
        g2.setFont(tickLabelFont);
        if (this.isAutoTickUnitSelection()) {
            this.selectAutoTickUnit(g2, dataArea, edge);
        }
        final DateTickUnit unit = this.getTickUnit();
        Date tickDate = this.calculateLowestVisibleTickValue(unit);
        final Date upperDate = this.getMaximumDate();
        while (tickDate.before(upperDate)) {
            if (!this.isHiddenValue(tickDate.getTime())) {
                final DateFormat formatter = this.getDateFormatOverride();
                String tickLabel;
                if (formatter != null) {
                    tickLabel = formatter.format(tickDate);
                }
                else {
                    tickLabel = this.tickUnit.dateToString(tickDate);
                }
                TextAnchor anchor = null;
                TextAnchor rotationAnchor = null;
                double angle = 0.0;
                if (this.isVerticalTickLabels()) {
                    anchor = TextAnchor.BOTTOM_CENTER;
                    rotationAnchor = TextAnchor.BOTTOM_CENTER;
                    if (edge == RectangleEdge.LEFT) {
                        angle = -1.5707963267948966;
                    }
                    else {
                        angle = 1.5707963267948966;
                    }
                }
                else if (edge == RectangleEdge.LEFT) {
                    anchor = TextAnchor.CENTER_RIGHT;
                    rotationAnchor = TextAnchor.CENTER_RIGHT;
                }
                else {
                    anchor = TextAnchor.CENTER_LEFT;
                    rotationAnchor = TextAnchor.CENTER_LEFT;
                }
                final Tick tick = new DateTick(tickDate, tickLabel, anchor, rotationAnchor, angle);
                result.add(tick);
                tickDate = unit.addToDate(tickDate);
            }
            else {
                tickDate = unit.rollDate(tickDate);
            }
        }
        return result;
    }
    
    public AxisState draw(final Graphics2D g2, final double cursor, final Rectangle2D plotArea, final Rectangle2D dataArea, final RectangleEdge edge, final PlotRenderingInfo plotState) {
        if (!this.isVisible()) {
            final AxisState state = new AxisState(cursor);
            final List ticks = this.refreshTicks(g2, state, dataArea, edge);
            state.setTicks(ticks);
            return state;
        }
        AxisState state = this.drawTickMarksAndLabels(g2, cursor, plotArea, dataArea, edge);
        state = this.drawLabel(this.getLabel(), g2, plotArea, dataArea, edge, state);
        return state;
    }
    
    public void zoomRange(final double lowerPercent, final double upperPercent) {
        final double start = (double)this.timeline.toTimelineValue((long)this.getRange().getLowerBound());
        final double length = (double)(this.timeline.toTimelineValue((long)this.getRange().getUpperBound()) - this.timeline.toTimelineValue((long)this.getRange().getLowerBound()));
        Range adjusted = null;
        if (this.isInverted()) {
            adjusted = new DateRange((double)this.timeline.toMillisecond((long)(start + length * (1.0 - upperPercent))), (double)this.timeline.toMillisecond((long)(start + length * (1.0 - lowerPercent))));
        }
        else {
            adjusted = new DateRange((double)this.timeline.toMillisecond((long)(start + length * lowerPercent)), (double)this.timeline.toMillisecond((long)(start + length * upperPercent)));
        }
        this.setRange(adjusted);
    }
    
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof DateAxis)) {
            return false;
        }
        final DateAxis that = (DateAxis)obj;
        return ObjectUtilities.equal((Object)this.tickUnit, (Object)that.tickUnit) && ObjectUtilities.equal((Object)this.dateFormatOverride, (Object)that.dateFormatOverride) && ObjectUtilities.equal((Object)this.tickMarkPosition, (Object)that.tickMarkPosition) && ObjectUtilities.equal((Object)this.timeline, (Object)that.timeline) && super.equals(obj);
    }
    
    public int hashCode() {
        if (this.getLabel() != null) {
            return this.getLabel().hashCode();
        }
        return 0;
    }
    
    public Object clone() throws CloneNotSupportedException {
        final DateAxis clone = (DateAxis)super.clone();
        if (this.dateFormatOverride != null) {
            clone.dateFormatOverride = (DateFormat)this.dateFormatOverride.clone();
        }
        return clone;
    }
    
    static {
        DEFAULT_DATE_RANGE = new DateRange();
        DEFAULT_DATE_TICK_UNIT = new DateTickUnit(2, 1, new SimpleDateFormat());
        DEFAULT_ANCHOR_DATE = new Date();
        DEFAULT_TIMELINE = new DefaultTimeline();
    }
    
    private static class DefaultTimeline implements Timeline, Serializable
    {
        public long toTimelineValue(final long millisecond) {
            return millisecond;
        }
        
        public long toTimelineValue(final Date date) {
            return date.getTime();
        }
        
        public long toMillisecond(final long value) {
            return value;
        }
        
        public boolean containsDomainValue(final long millisecond) {
            return true;
        }
        
        public boolean containsDomainValue(final Date date) {
            return true;
        }
        
        public boolean containsDomainRange(final long from, final long to) {
            return true;
        }
        
        public boolean containsDomainRange(final Date from, final Date to) {
            return true;
        }
        
        public boolean equals(final Object object) {
            return object != null && (object == this || object instanceof DefaultTimeline);
        }
    }
}
