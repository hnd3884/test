package org.apache.poi.xssf.usermodel.charts;

import org.apache.poi.ss.usermodel.charts.AxisTickMark;
import org.apache.poi.ss.usermodel.charts.AxisCrosses;
import org.apache.poi.ss.usermodel.charts.AxisOrientation;
import org.openxmlformats.schemas.drawingml.x2006.chart.STTickLblPos;
import org.apache.poi.ss.usermodel.charts.ChartAxis;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTChartLines;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTTickMark;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTBoolean;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTCrosses;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTScaling;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTNumFmt;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTAxPos;
import org.apache.poi.util.Internal;
import org.openxmlformats.schemas.drawingml.x2006.main.CTShapeProperties;
import org.apache.poi.ss.usermodel.charts.AxisPosition;
import org.apache.poi.xssf.usermodel.XSSFChart;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTDateAx;
import org.apache.poi.util.Removal;

@Deprecated
@Removal(version = "4.2")
public class XSSFDateAxis extends XSSFChartAxis
{
    private CTDateAx ctDateAx;
    
    public XSSFDateAxis(final XSSFChart chart, final long id, final AxisPosition pos) {
        super(chart);
        this.createAxis(id, pos);
    }
    
    public XSSFDateAxis(final XSSFChart chart, final CTDateAx ctDateAx) {
        super(chart);
        this.ctDateAx = ctDateAx;
    }
    
    public long getId() {
        return this.ctDateAx.getAxId().getVal();
    }
    
    @Internal
    @Override
    public CTShapeProperties getLine() {
        return this.ctDateAx.getSpPr();
    }
    
    @Override
    protected CTAxPos getCTAxPos() {
        return this.ctDateAx.getAxPos();
    }
    
    @Override
    protected CTNumFmt getCTNumFmt() {
        if (this.ctDateAx.isSetNumFmt()) {
            return this.ctDateAx.getNumFmt();
        }
        return this.ctDateAx.addNewNumFmt();
    }
    
    @Override
    protected CTScaling getCTScaling() {
        return this.ctDateAx.getScaling();
    }
    
    @Override
    protected CTCrosses getCTCrosses() {
        return this.ctDateAx.getCrosses();
    }
    
    @Override
    protected CTBoolean getDelete() {
        return this.ctDateAx.getDelete();
    }
    
    @Override
    protected CTTickMark getMajorCTTickMark() {
        return this.ctDateAx.getMajorTickMark();
    }
    
    @Override
    protected CTTickMark getMinorCTTickMark() {
        return this.ctDateAx.getMinorTickMark();
    }
    
    @Internal
    @Override
    public CTChartLines getMajorGridLines() {
        return this.ctDateAx.getMajorGridlines();
    }
    
    public void crossAxis(final ChartAxis axis) {
        this.ctDateAx.getCrossAx().setVal(axis.getId());
    }
    
    private void createAxis(final long id, final AxisPosition pos) {
        this.ctDateAx = this.chart.getCTChart().getPlotArea().addNewDateAx();
        this.ctDateAx.addNewAxId().setVal(id);
        this.ctDateAx.addNewAxPos();
        this.ctDateAx.addNewScaling();
        this.ctDateAx.addNewCrosses();
        this.ctDateAx.addNewCrossAx();
        this.ctDateAx.addNewTickLblPos().setVal(STTickLblPos.NEXT_TO);
        this.ctDateAx.addNewDelete();
        this.ctDateAx.addNewMajorTickMark();
        this.ctDateAx.addNewMinorTickMark();
        this.setPosition(pos);
        this.setOrientation(AxisOrientation.MIN_MAX);
        this.setCrosses(AxisCrosses.AUTO_ZERO);
        this.setVisible(true);
        this.setMajorTickMark(AxisTickMark.CROSS);
        this.setMinorTickMark(AxisTickMark.NONE);
    }
    
    public boolean hasNumberFormat() {
        return this.ctDateAx.isSetNumFmt();
    }
}
