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
import org.openxmlformats.schemas.drawingml.x2006.chart.CTSerAx;

public class XDDFSeriesAxis extends XDDFChartAxis
{
    private CTSerAx ctSerAx;
    
    public XDDFSeriesAxis(final CTPlotArea plotArea, final AxisPosition position) {
        this.initializeAxis(plotArea, position);
    }
    
    public XDDFSeriesAxis(final CTSerAx ctSerAx) {
        this.ctSerAx = ctSerAx;
    }
    
    @Override
    public XDDFShapeProperties getOrAddMajorGridProperties() {
        CTChartLines majorGridlines;
        if (this.ctSerAx.isSetMajorGridlines()) {
            majorGridlines = this.ctSerAx.getMajorGridlines();
        }
        else {
            majorGridlines = this.ctSerAx.addNewMajorGridlines();
        }
        return new XDDFShapeProperties(this.getOrAddLinesProperties(majorGridlines));
    }
    
    @Override
    public XDDFShapeProperties getOrAddMinorGridProperties() {
        CTChartLines minorGridlines;
        if (this.ctSerAx.isSetMinorGridlines()) {
            minorGridlines = this.ctSerAx.getMinorGridlines();
        }
        else {
            minorGridlines = this.ctSerAx.addNewMinorGridlines();
        }
        return new XDDFShapeProperties(this.getOrAddLinesProperties(minorGridlines));
    }
    
    @Override
    public XDDFShapeProperties getOrAddShapeProperties() {
        CTShapeProperties properties;
        if (this.ctSerAx.isSetSpPr()) {
            properties = this.ctSerAx.getSpPr();
        }
        else {
            properties = this.ctSerAx.addNewSpPr();
        }
        return new XDDFShapeProperties(properties);
    }
    
    @Override
    public XDDFRunProperties getOrAddTextProperties() {
        CTTextBody text;
        if (this.ctSerAx.isSetTxPr()) {
            text = this.ctSerAx.getTxPr();
        }
        else {
            text = this.ctSerAx.addNewTxPr();
        }
        return new XDDFRunProperties(this.getOrAddTextProperties(text));
    }
    
    @Override
    public void setTitle(final String text) {
        if (!this.ctSerAx.isSetTitle()) {
            this.ctSerAx.addNewTitle();
        }
        final XDDFTitle title = new XDDFTitle(null, this.ctSerAx.getTitle());
        title.setOverlay(false);
        title.setText(text);
    }
    
    @Override
    public boolean isSetMinorUnit() {
        return false;
    }
    
    @Override
    public void setMinorUnit(final double minor) {
    }
    
    @Override
    public double getMinorUnit() {
        return Double.NaN;
    }
    
    @Override
    public boolean isSetMajorUnit() {
        return false;
    }
    
    @Override
    public void setMajorUnit(final double major) {
    }
    
    @Override
    public double getMajorUnit() {
        return Double.NaN;
    }
    
    @Override
    public void crossAxis(final XDDFChartAxis axis) {
        this.ctSerAx.getCrossAx().setVal(axis.getId());
    }
    
    @Override
    protected CTUnsignedInt getCTAxId() {
        return this.ctSerAx.getAxId();
    }
    
    @Override
    protected CTAxPos getCTAxPos() {
        return this.ctSerAx.getAxPos();
    }
    
    @Override
    public boolean hasNumberFormat() {
        return this.ctSerAx.isSetNumFmt();
    }
    
    @Override
    protected CTNumFmt getCTNumFmt() {
        if (this.ctSerAx.isSetNumFmt()) {
            return this.ctSerAx.getNumFmt();
        }
        return this.ctSerAx.addNewNumFmt();
    }
    
    @Override
    protected CTScaling getCTScaling() {
        return this.ctSerAx.getScaling();
    }
    
    @Override
    protected CTCrosses getCTCrosses() {
        final CTCrosses crosses = this.ctSerAx.getCrosses();
        if (crosses == null) {
            return this.ctSerAx.addNewCrosses();
        }
        return crosses;
    }
    
    @Override
    protected CTBoolean getDelete() {
        return this.ctSerAx.getDelete();
    }
    
    @Override
    protected CTTickMark getMajorCTTickMark() {
        return this.ctSerAx.getMajorTickMark();
    }
    
    @Override
    protected CTTickMark getMinorCTTickMark() {
        return this.ctSerAx.getMinorTickMark();
    }
    
    @Override
    protected CTTickLblPos getCTTickLblPos() {
        return this.ctSerAx.getTickLblPos();
    }
    
    private void initializeAxis(final CTPlotArea plotArea, final AxisPosition position) {
        final long id = this.getNextAxId(plotArea);
        this.ctSerAx = plotArea.addNewSerAx();
        this.ctSerAx.addNewAxId().setVal(id);
        this.ctSerAx.addNewAxPos();
        this.ctSerAx.addNewScaling();
        this.ctSerAx.addNewCrosses();
        this.ctSerAx.addNewCrossAx();
        this.ctSerAx.addNewTickLblPos();
        this.ctSerAx.addNewDelete();
        this.ctSerAx.addNewMajorTickMark();
        this.ctSerAx.addNewMinorTickMark();
        this.setPosition(position);
        this.setOrientation(AxisOrientation.MIN_MAX);
        this.setCrosses(AxisCrosses.AUTO_ZERO);
        this.setVisible(true);
        this.setMajorTickMark(AxisTickMark.CROSS);
        this.setMinorTickMark(AxisTickMark.NONE);
        this.setTickLabelPosition(AxisTickLabelPosition.NEXT_TO);
    }
}
