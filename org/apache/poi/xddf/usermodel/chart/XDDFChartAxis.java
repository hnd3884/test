package org.apache.poi.xddf.usermodel.chart;

import org.openxmlformats.schemas.drawingml.x2006.chart.CTPlotArea;
import org.openxmlformats.schemas.drawingml.x2006.main.CTShapeProperties;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTChartLines;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextParagraphProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextParagraph;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextCharacterProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextBody;
import org.apache.poi.xddf.usermodel.text.XDDFRunProperties;
import org.apache.poi.xddf.usermodel.XDDFShapeProperties;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTTickLblPos;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTTickMark;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTBoolean;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTCrosses;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTScaling;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTNumFmt;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTAxPos;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTUnsignedInt;
import org.apache.poi.xddf.usermodel.HasShapeProperties;

public abstract class XDDFChartAxis implements HasShapeProperties
{
    private static final double MIN_LOG_BASE = 2.0;
    private static final double MAX_LOG_BASE = 1000.0;
    
    protected abstract CTUnsignedInt getCTAxId();
    
    protected abstract CTAxPos getCTAxPos();
    
    protected abstract CTNumFmt getCTNumFmt();
    
    protected abstract CTScaling getCTScaling();
    
    protected abstract CTCrosses getCTCrosses();
    
    protected abstract CTBoolean getDelete();
    
    protected abstract CTTickMark getMajorCTTickMark();
    
    protected abstract CTTickMark getMinorCTTickMark();
    
    protected abstract CTTickLblPos getCTTickLblPos();
    
    public abstract XDDFShapeProperties getOrAddMajorGridProperties();
    
    public abstract XDDFShapeProperties getOrAddMinorGridProperties();
    
    public abstract XDDFRunProperties getOrAddTextProperties();
    
    public abstract void setTitle(final String p0);
    
    public abstract boolean isSetMinorUnit();
    
    public abstract void setMinorUnit(final double p0);
    
    public abstract double getMinorUnit();
    
    public abstract boolean isSetMajorUnit();
    
    public abstract void setMajorUnit(final double p0);
    
    public abstract double getMajorUnit();
    
    public long getId() {
        return this.getCTAxId().getVal();
    }
    
    public AxisPosition getPosition() {
        return AxisPosition.valueOf(this.getCTAxPos().getVal());
    }
    
    public void setPosition(final AxisPosition position) {
        this.getCTAxPos().setVal(position.underlying);
    }
    
    public abstract boolean hasNumberFormat();
    
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
        final CTScaling scaling = this.getCTScaling();
        if (scaling.isSetLogBase()) {
            return scaling.getLogBase().getVal();
        }
        return Double.NaN;
    }
    
    public boolean isSetMinimum() {
        return this.getCTScaling().isSetMin();
    }
    
    public void setMinimum(final double min) {
        final CTScaling scaling = this.getCTScaling();
        if (Double.isNaN(min)) {
            if (scaling.isSetMin()) {
                scaling.unsetMin();
            }
        }
        else if (scaling.isSetMin()) {
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
        return Double.NaN;
    }
    
    public boolean isSetMaximum() {
        return this.getCTScaling().isSetMax();
    }
    
    public void setMaximum(final double max) {
        final CTScaling scaling = this.getCTScaling();
        if (Double.isNaN(max)) {
            if (scaling.isSetMax()) {
                scaling.unsetMax();
            }
        }
        else if (scaling.isSetMax()) {
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
        return Double.NaN;
    }
    
    public AxisOrientation getOrientation() {
        return AxisOrientation.valueOf(this.getCTScaling().getOrientation().getVal());
    }
    
    public void setOrientation(final AxisOrientation orientation) {
        final CTScaling scaling = this.getCTScaling();
        if (scaling.isSetOrientation()) {
            scaling.getOrientation().setVal(orientation.underlying);
        }
        else {
            scaling.addNewOrientation().setVal(orientation.underlying);
        }
    }
    
    public AxisCrosses getCrosses() {
        return AxisCrosses.valueOf(this.getCTCrosses().getVal());
    }
    
    public void setCrosses(final AxisCrosses crosses) {
        this.getCTCrosses().setVal(crosses.underlying);
    }
    
    public abstract void crossAxis(final XDDFChartAxis p0);
    
    public boolean isVisible() {
        return !this.getDelete().getVal();
    }
    
    public void setVisible(final boolean value) {
        this.getDelete().setVal(!value);
    }
    
    public AxisTickMark getMajorTickMark() {
        return AxisTickMark.valueOf(this.getMajorCTTickMark().getVal());
    }
    
    public void setMajorTickMark(final AxisTickMark tickMark) {
        this.getMajorCTTickMark().setVal(tickMark.underlying);
    }
    
    public AxisTickMark getMinorTickMark() {
        return AxisTickMark.valueOf(this.getMinorCTTickMark().getVal());
    }
    
    public void setMinorTickMark(final AxisTickMark tickMark) {
        this.getMinorCTTickMark().setVal(tickMark.underlying);
    }
    
    public AxisTickLabelPosition getTickLabelPosition() {
        return AxisTickLabelPosition.valueOf(this.getCTTickLblPos().getVal());
    }
    
    public void setTickLabelPosition(final AxisTickLabelPosition labelPosition) {
        this.getCTTickLblPos().setVal(labelPosition.underlying);
    }
    
    protected CTTextCharacterProperties getOrAddTextProperties(final CTTextBody body) {
        if (body.getBodyPr() == null) {
            body.addNewBodyPr();
        }
        CTTextParagraph paragraph;
        if (body.sizeOfPArray() > 0) {
            paragraph = body.getPArray(0);
        }
        else {
            paragraph = body.addNewP();
        }
        CTTextParagraphProperties paraprops;
        if (paragraph.isSetPPr()) {
            paraprops = paragraph.getPPr();
        }
        else {
            paraprops = paragraph.addNewPPr();
        }
        CTTextCharacterProperties properties;
        if (paraprops.isSetDefRPr()) {
            properties = paraprops.getDefRPr();
        }
        else {
            properties = paraprops.addNewDefRPr();
        }
        return properties;
    }
    
    protected CTShapeProperties getOrAddLinesProperties(final CTChartLines gridlines) {
        CTShapeProperties properties;
        if (gridlines.isSetSpPr()) {
            properties = gridlines.getSpPr();
        }
        else {
            properties = gridlines.addNewSpPr();
        }
        return properties;
    }
    
    protected long getNextAxId(final CTPlotArea plotArea) {
        final long totalAxisCount = plotArea.sizeOfValAxArray() + plotArea.sizeOfCatAxArray() + plotArea.sizeOfDateAxArray() + plotArea.sizeOfSerAxArray();
        return totalAxisCount;
    }
}
