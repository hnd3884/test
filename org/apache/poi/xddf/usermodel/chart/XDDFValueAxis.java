package org.apache.poi.xddf.usermodel.chart;

import org.openxmlformats.schemas.drawingml.x2006.chart.CTTickLblPos;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTTickMark;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTBoolean;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTCrosses;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTScaling;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTNumFmt;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTAxPos;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTUnsignedInt;
import org.apache.poi.xddf.usermodel.text.TextContainer;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextBody;
import org.apache.poi.xddf.usermodel.text.XDDFRunProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTShapeProperties;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTChartLines;
import org.apache.poi.xddf.usermodel.XDDFShapeProperties;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTPlotArea;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTValAx;

public class XDDFValueAxis extends XDDFChartAxis
{
    private CTValAx ctValAx;
    
    public XDDFValueAxis(final CTPlotArea plotArea, final AxisPosition position) {
        this.initializeAxis(plotArea, position);
    }
    
    public XDDFValueAxis(final CTValAx ctValAx) {
        this.ctValAx = ctValAx;
    }
    
    @Override
    public XDDFShapeProperties getOrAddMajorGridProperties() {
        CTChartLines majorGridlines;
        if (this.ctValAx.isSetMajorGridlines()) {
            majorGridlines = this.ctValAx.getMajorGridlines();
        }
        else {
            majorGridlines = this.ctValAx.addNewMajorGridlines();
        }
        return new XDDFShapeProperties(this.getOrAddLinesProperties(majorGridlines));
    }
    
    @Override
    public XDDFShapeProperties getOrAddMinorGridProperties() {
        CTChartLines minorGridlines;
        if (this.ctValAx.isSetMinorGridlines()) {
            minorGridlines = this.ctValAx.getMinorGridlines();
        }
        else {
            minorGridlines = this.ctValAx.addNewMinorGridlines();
        }
        return new XDDFShapeProperties(this.getOrAddLinesProperties(minorGridlines));
    }
    
    @Override
    public XDDFShapeProperties getOrAddShapeProperties() {
        CTShapeProperties properties;
        if (this.ctValAx.isSetSpPr()) {
            properties = this.ctValAx.getSpPr();
        }
        else {
            properties = this.ctValAx.addNewSpPr();
        }
        return new XDDFShapeProperties(properties);
    }
    
    @Override
    public XDDFRunProperties getOrAddTextProperties() {
        CTTextBody text;
        if (this.ctValAx.isSetTxPr()) {
            text = this.ctValAx.getTxPr();
        }
        else {
            text = this.ctValAx.addNewTxPr();
        }
        return new XDDFRunProperties(this.getOrAddTextProperties(text));
    }
    
    @Override
    public void setTitle(final String text) {
        if (!this.ctValAx.isSetTitle()) {
            this.ctValAx.addNewTitle();
        }
        final XDDFTitle title = new XDDFTitle(null, this.ctValAx.getTitle());
        title.setOverlay(false);
        title.setText(text);
    }
    
    @Override
    public boolean isSetMinorUnit() {
        return this.ctValAx.isSetMinorUnit();
    }
    
    @Override
    public void setMinorUnit(final double minor) {
        if (Double.isNaN(minor)) {
            if (this.ctValAx.isSetMinorUnit()) {
                this.ctValAx.unsetMinorUnit();
            }
        }
        else if (this.ctValAx.isSetMinorUnit()) {
            this.ctValAx.getMinorUnit().setVal(minor);
        }
        else {
            this.ctValAx.addNewMinorUnit().setVal(minor);
        }
    }
    
    @Override
    public double getMinorUnit() {
        if (this.ctValAx.isSetMinorUnit()) {
            return this.ctValAx.getMinorUnit().getVal();
        }
        return Double.NaN;
    }
    
    @Override
    public boolean isSetMajorUnit() {
        return this.ctValAx.isSetMajorUnit();
    }
    
    @Override
    public void setMajorUnit(final double major) {
        if (Double.isNaN(major)) {
            if (this.ctValAx.isSetMajorUnit()) {
                this.ctValAx.unsetMajorUnit();
            }
        }
        else if (this.ctValAx.isSetMajorUnit()) {
            this.ctValAx.getMajorUnit().setVal(major);
        }
        else {
            this.ctValAx.addNewMajorUnit().setVal(major);
        }
    }
    
    @Override
    public double getMajorUnit() {
        if (this.ctValAx.isSetMajorUnit()) {
            return this.ctValAx.getMajorUnit().getVal();
        }
        return Double.NaN;
    }
    
    @Override
    public void crossAxis(final XDDFChartAxis axis) {
        this.ctValAx.getCrossAx().setVal(axis.getId());
    }
    
    @Override
    protected CTUnsignedInt getCTAxId() {
        return this.ctValAx.getAxId();
    }
    
    @Override
    protected CTAxPos getCTAxPos() {
        return this.ctValAx.getAxPos();
    }
    
    @Override
    public boolean hasNumberFormat() {
        return this.ctValAx.isSetNumFmt();
    }
    
    @Override
    protected CTNumFmt getCTNumFmt() {
        if (this.ctValAx.isSetNumFmt()) {
            return this.ctValAx.getNumFmt();
        }
        return this.ctValAx.addNewNumFmt();
    }
    
    @Override
    protected CTScaling getCTScaling() {
        return this.ctValAx.getScaling();
    }
    
    @Override
    protected CTCrosses getCTCrosses() {
        final CTCrosses crosses = this.ctValAx.getCrosses();
        if (crosses == null) {
            return this.ctValAx.addNewCrosses();
        }
        return crosses;
    }
    
    @Override
    protected CTBoolean getDelete() {
        return this.ctValAx.getDelete();
    }
    
    @Override
    protected CTTickMark getMajorCTTickMark() {
        return this.ctValAx.getMajorTickMark();
    }
    
    @Override
    protected CTTickMark getMinorCTTickMark() {
        return this.ctValAx.getMinorTickMark();
    }
    
    @Override
    protected CTTickLblPos getCTTickLblPos() {
        return this.ctValAx.getTickLblPos();
    }
    
    public AxisCrossBetween getCrossBetween() {
        return AxisCrossBetween.valueOf(this.ctValAx.getCrossBetween().getVal());
    }
    
    public void setCrossBetween(final AxisCrossBetween crossBetween) {
        this.ctValAx.getCrossBetween().setVal(crossBetween.underlying);
    }
    
    private void initializeAxis(final CTPlotArea plotArea, final AxisPosition position) {
        final long id = this.getNextAxId(plotArea);
        this.ctValAx = plotArea.addNewValAx();
        this.ctValAx.addNewAxId().setVal(id);
        this.ctValAx.addNewAxPos();
        this.ctValAx.addNewScaling();
        this.ctValAx.addNewCrossBetween();
        this.ctValAx.addNewCrosses();
        this.ctValAx.addNewCrossAx();
        this.ctValAx.addNewTickLblPos();
        this.ctValAx.addNewDelete();
        this.ctValAx.addNewMajorTickMark();
        this.ctValAx.addNewMinorTickMark();
        this.setPosition(position);
        this.setOrientation(AxisOrientation.MIN_MAX);
        this.setCrossBetween(AxisCrossBetween.MIDPOINT_CATEGORY);
        this.setCrosses(AxisCrosses.AUTO_ZERO);
        this.setVisible(true);
        this.setMajorTickMark(AxisTickMark.CROSS);
        this.setMinorTickMark(AxisTickMark.NONE);
        this.setTickLabelPosition(AxisTickLabelPosition.NEXT_TO);
    }
}
