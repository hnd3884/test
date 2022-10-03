package org.apache.poi.xssf.usermodel.charts;

import org.openxmlformats.schemas.drawingml.x2006.chart.STTickMark;
import org.openxmlformats.schemas.drawingml.x2006.chart.STAxPos;
import org.openxmlformats.schemas.drawingml.x2006.chart.STCrosses;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTOrientation;
import org.openxmlformats.schemas.drawingml.x2006.main.CTShapeProperties;
import org.apache.poi.util.Internal;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTChartLines;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTTickMark;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTBoolean;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTCrosses;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTNumFmt;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTAxPos;
import org.apache.poi.ss.usermodel.charts.AxisTickMark;
import org.apache.poi.ss.usermodel.charts.AxisCrosses;
import org.openxmlformats.schemas.drawingml.x2006.chart.STOrientation;
import org.apache.poi.ss.usermodel.charts.AxisOrientation;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTLogBase;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTScaling;
import org.apache.poi.ss.usermodel.charts.AxisPosition;
import org.apache.poi.xssf.usermodel.XSSFChart;
import org.apache.poi.util.Removal;
import org.apache.poi.ss.usermodel.charts.ChartAxis;

@Deprecated
@Removal(version = "4.2")
public abstract class XSSFChartAxis implements ChartAxis
{
    protected XSSFChart chart;
    private static final double MIN_LOG_BASE = 2.0;
    private static final double MAX_LOG_BASE = 1000.0;
    
    protected XSSFChartAxis(final XSSFChart chart) {
        this.chart = chart;
    }
    
    public AxisPosition getPosition() {
        return toAxisPosition(this.getCTAxPos());
    }
    
    public void setPosition(final AxisPosition position) {
        this.getCTAxPos().setVal(fromAxisPosition(position));
    }
    
    public void setNumberFormat(final String format) {
        this.getCTNumFmt().setFormatCode(format);
        this.getCTNumFmt().setSourceLinked(true);
    }
    
    public String getNumberFormat() {
        return this.getCTNumFmt().getFormatCode();
    }
    
    public boolean isSetLogBase() {
        return this.getCTScaling().isSetLogBase();
    }
    
    public void setLogBase(final double logBase) {
        if (logBase < 2.0 || 1000.0 < logBase) {
            throw new IllegalArgumentException("Axis log base must be between 2 and 1000 (inclusive), got: " + logBase);
        }
        final CTScaling scaling = this.getCTScaling();
        if (scaling.isSetLogBase()) {
            scaling.getLogBase().setVal(logBase);
        }
        else {
            scaling.addNewLogBase().setVal(logBase);
        }
    }
    
    public double getLogBase() {
        final CTLogBase logBase = this.getCTScaling().getLogBase();
        if (logBase != null) {
            return logBase.getVal();
        }
        return 0.0;
    }
    
    public boolean isSetMinimum() {
        return this.getCTScaling().isSetMin();
    }
    
    public void setMinimum(final double min) {
        final CTScaling scaling = this.getCTScaling();
        if (scaling.isSetMin()) {
            scaling.getMin().setVal(min);
        }
        else {
            scaling.addNewMin().setVal(min);
        }
    }
    
    public double getMinimum() {
        final CTScaling scaling = this.getCTScaling();
        if (scaling.isSetMin()) {
            return scaling.getMin().getVal();
        }
        return 0.0;
    }
    
    public boolean isSetMaximum() {
        return this.getCTScaling().isSetMax();
    }
    
    public void setMaximum(final double max) {
        final CTScaling scaling = this.getCTScaling();
        if (scaling.isSetMax()) {
            scaling.getMax().setVal(max);
        }
        else {
            scaling.addNewMax().setVal(max);
        }
    }
    
    public double getMaximum() {
        final CTScaling scaling = this.getCTScaling();
        if (scaling.isSetMax()) {
            return scaling.getMax().getVal();
        }
        return 0.0;
    }
    
    public AxisOrientation getOrientation() {
        return toAxisOrientation(this.getCTScaling().getOrientation());
    }
    
    public void setOrientation(final AxisOrientation orientation) {
        final CTScaling scaling = this.getCTScaling();
        final STOrientation.Enum stOrientation = fromAxisOrientation(orientation);
        if (scaling.isSetOrientation()) {
            scaling.getOrientation().setVal(stOrientation);
        }
        else {
            this.getCTScaling().addNewOrientation().setVal(stOrientation);
        }
    }
    
    public AxisCrosses getCrosses() {
        return toAxisCrosses(this.getCTCrosses());
    }
    
    public void setCrosses(final AxisCrosses crosses) {
        this.getCTCrosses().setVal(fromAxisCrosses(crosses));
    }
    
    public boolean isVisible() {
        return !this.getDelete().getVal();
    }
    
    public void setVisible(final boolean value) {
        this.getDelete().setVal(!value);
    }
    
    public AxisTickMark getMajorTickMark() {
        return toAxisTickMark(this.getMajorCTTickMark());
    }
    
    public void setMajorTickMark(final AxisTickMark tickMark) {
        this.getMajorCTTickMark().setVal(fromAxisTickMark(tickMark));
    }
    
    public AxisTickMark getMinorTickMark() {
        return toAxisTickMark(this.getMinorCTTickMark());
    }
    
    public void setMinorTickMark(final AxisTickMark tickMark) {
        this.getMinorCTTickMark().setVal(fromAxisTickMark(tickMark));
    }
    
    protected abstract CTAxPos getCTAxPos();
    
    protected abstract CTNumFmt getCTNumFmt();
    
    protected abstract CTScaling getCTScaling();
    
    protected abstract CTCrosses getCTCrosses();
    
    protected abstract CTBoolean getDelete();
    
    protected abstract CTTickMark getMajorCTTickMark();
    
    protected abstract CTTickMark getMinorCTTickMark();
    
    @Internal
    public abstract CTChartLines getMajorGridLines();
    
    @Internal
    public abstract CTShapeProperties getLine();
    
    private static STOrientation.Enum fromAxisOrientation(final AxisOrientation orientation) {
        switch (orientation) {
            case MIN_MAX: {
                return STOrientation.MIN_MAX;
            }
            case MAX_MIN: {
                return STOrientation.MAX_MIN;
            }
            default: {
                throw new IllegalArgumentException();
            }
        }
    }
    
    private static AxisOrientation toAxisOrientation(final CTOrientation ctOrientation) {
        switch (ctOrientation.getVal().intValue()) {
            case 2: {
                return AxisOrientation.MIN_MAX;
            }
            case 1: {
                return AxisOrientation.MAX_MIN;
            }
            default: {
                throw new IllegalArgumentException();
            }
        }
    }
    
    private static STCrosses.Enum fromAxisCrosses(final AxisCrosses crosses) {
        switch (crosses) {
            case AUTO_ZERO: {
                return STCrosses.AUTO_ZERO;
            }
            case MIN: {
                return STCrosses.MIN;
            }
            case MAX: {
                return STCrosses.MAX;
            }
            default: {
                throw new IllegalArgumentException();
            }
        }
    }
    
    private static AxisCrosses toAxisCrosses(final CTCrosses ctCrosses) {
        switch (ctCrosses.getVal().intValue()) {
            case 1: {
                return AxisCrosses.AUTO_ZERO;
            }
            case 2: {
                return AxisCrosses.MAX;
            }
            case 3: {
                return AxisCrosses.MIN;
            }
            default: {
                throw new IllegalArgumentException();
            }
        }
    }
    
    private static STAxPos.Enum fromAxisPosition(final AxisPosition position) {
        switch (position) {
            case BOTTOM: {
                return STAxPos.B;
            }
            case LEFT: {
                return STAxPos.L;
            }
            case RIGHT: {
                return STAxPos.R;
            }
            case TOP: {
                return STAxPos.T;
            }
            default: {
                throw new IllegalArgumentException();
            }
        }
    }
    
    private static AxisPosition toAxisPosition(final CTAxPos ctAxPos) {
        switch (ctAxPos.getVal().intValue()) {
            case 1: {
                return AxisPosition.BOTTOM;
            }
            case 2: {
                return AxisPosition.LEFT;
            }
            case 3: {
                return AxisPosition.RIGHT;
            }
            case 4: {
                return AxisPosition.TOP;
            }
            default: {
                return AxisPosition.BOTTOM;
            }
        }
    }
    
    private static STTickMark.Enum fromAxisTickMark(final AxisTickMark tickMark) {
        switch (tickMark) {
            case NONE: {
                return STTickMark.NONE;
            }
            case IN: {
                return STTickMark.IN;
            }
            case OUT: {
                return STTickMark.OUT;
            }
            case CROSS: {
                return STTickMark.CROSS;
            }
            default: {
                throw new IllegalArgumentException("Unknown AxisTickMark: " + tickMark);
            }
        }
    }
    
    private static AxisTickMark toAxisTickMark(final CTTickMark ctTickMark) {
        switch (ctTickMark.getVal().intValue()) {
            case 3: {
                return AxisTickMark.NONE;
            }
            case 2: {
                return AxisTickMark.IN;
            }
            case 4: {
                return AxisTickMark.OUT;
            }
            case 1: {
                return AxisTickMark.CROSS;
            }
            default: {
                return AxisTickMark.CROSS;
            }
        }
    }
}
