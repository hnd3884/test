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
import org.openxmlformats.schemas.drawingml.x2006.chart.CTCatAx;

public class XDDFCategoryAxis extends XDDFChartAxis
{
    private CTCatAx ctCatAx;
    
    public XDDFCategoryAxis(final CTPlotArea plotArea, final AxisPosition position) {
        this.initializeAxis(plotArea, position);
    }
    
    public XDDFCategoryAxis(final CTCatAx ctCatAx) {
        this.ctCatAx = ctCatAx;
    }
    
    @Override
    public XDDFShapeProperties getOrAddMajorGridProperties() {
        CTChartLines majorGridlines;
        if (this.ctCatAx.isSetMajorGridlines()) {
            majorGridlines = this.ctCatAx.getMajorGridlines();
        }
        else {
            majorGridlines = this.ctCatAx.addNewMajorGridlines();
        }
        return new XDDFShapeProperties(this.getOrAddLinesProperties(majorGridlines));
    }
    
    @Override
    public XDDFShapeProperties getOrAddMinorGridProperties() {
        CTChartLines minorGridlines;
        if (this.ctCatAx.isSetMinorGridlines()) {
            minorGridlines = this.ctCatAx.getMinorGridlines();
        }
        else {
            minorGridlines = this.ctCatAx.addNewMinorGridlines();
        }
        return new XDDFShapeProperties(this.getOrAddLinesProperties(minorGridlines));
    }
    
    @Override
    public XDDFShapeProperties getOrAddShapeProperties() {
        CTShapeProperties properties;
        if (this.ctCatAx.isSetSpPr()) {
            properties = this.ctCatAx.getSpPr();
        }
        else {
            properties = this.ctCatAx.addNewSpPr();
        }
        return new XDDFShapeProperties(properties);
    }
    
    @Override
    public XDDFRunProperties getOrAddTextProperties() {
        CTTextBody text;
        if (this.ctCatAx.isSetTxPr()) {
            text = this.ctCatAx.getTxPr();
        }
        else {
            text = this.ctCatAx.addNewTxPr();
        }
        return new XDDFRunProperties(this.getOrAddTextProperties(text));
    }
    
    @Override
    public void setTitle(final String text) {
        if (!this.ctCatAx.isSetTitle()) {
            this.ctCatAx.addNewTitle();
        }
        final XDDFTitle title = new XDDFTitle(null, this.ctCatAx.getTitle());
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
        this.ctCatAx.getCrossAx().setVal(axis.getId());
    }
    
    @Override
    protected CTUnsignedInt getCTAxId() {
        return this.ctCatAx.getAxId();
    }
    
    @Override
    protected CTAxPos getCTAxPos() {
        return this.ctCatAx.getAxPos();
    }
    
    @Override
    public boolean hasNumberFormat() {
        return this.ctCatAx.isSetNumFmt();
    }
    
    @Override
    protected CTNumFmt getCTNumFmt() {
        if (this.ctCatAx.isSetNumFmt()) {
            return this.ctCatAx.getNumFmt();
        }
        return this.ctCatAx.addNewNumFmt();
    }
    
    @Override
    protected CTScaling getCTScaling() {
        return this.ctCatAx.getScaling();
    }
    
    @Override
    protected CTCrosses getCTCrosses() {
        final CTCrosses crosses = this.ctCatAx.getCrosses();
        if (crosses == null) {
            return this.ctCatAx.addNewCrosses();
        }
        return crosses;
    }
    
    @Override
    protected CTBoolean getDelete() {
        return this.ctCatAx.getDelete();
    }
    
    @Override
    protected CTTickMark getMajorCTTickMark() {
        return this.ctCatAx.getMajorTickMark();
    }
    
    @Override
    protected CTTickMark getMinorCTTickMark() {
        return this.ctCatAx.getMinorTickMark();
    }
    
    @Override
    protected CTTickLblPos getCTTickLblPos() {
        return this.ctCatAx.getTickLblPos();
    }
    
    public AxisLabelAlignment getLabelAlignment() {
        return AxisLabelAlignment.valueOf(this.ctCatAx.getLblAlgn().getVal());
    }
    
    public void setLabelAlignment(final AxisLabelAlignment labelAlignment) {
        this.ctCatAx.getLblAlgn().setVal(labelAlignment.underlying);
    }
    
    private void initializeAxis(final CTPlotArea plotArea, final AxisPosition position) {
        final long id = this.getNextAxId(plotArea);
        this.ctCatAx = plotArea.addNewCatAx();
        this.ctCatAx.addNewAxId().setVal(id);
        this.ctCatAx.addNewAuto().setVal(false);
        this.ctCatAx.addNewAxPos();
        this.ctCatAx.addNewScaling();
        this.ctCatAx.addNewCrosses();
        this.ctCatAx.addNewCrossAx();
        this.ctCatAx.addNewTickLblPos();
        this.ctCatAx.addNewDelete();
        this.ctCatAx.addNewMajorTickMark();
        this.ctCatAx.addNewMinorTickMark();
        this.ctCatAx.addNewNumFmt().setSourceLinked(true);
        this.ctCatAx.getNumFmt().setFormatCode("");
        this.setPosition(position);
        this.setOrientation(AxisOrientation.MIN_MAX);
        this.setCrosses(AxisCrosses.AUTO_ZERO);
        this.setVisible(true);
        this.setMajorTickMark(AxisTickMark.CROSS);
        this.setMinorTickMark(AxisTickMark.NONE);
        this.setTickLabelPosition(AxisTickLabelPosition.NEXT_TO);
    }
}
