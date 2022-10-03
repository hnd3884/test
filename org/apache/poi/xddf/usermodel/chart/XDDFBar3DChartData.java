package org.apache.poi.xddf.usermodel.chart;

import org.apache.xmlbeans.XmlObject;
import org.apache.poi.xddf.usermodel.XDDFShapeProperties;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTSerTx;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTNumDataSource;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTAxDataSource;
import org.apache.poi.util.Internal;
import java.util.Iterator;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTBarSer;
import java.util.Map;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTBar3DChart;

public class XDDFBar3DChartData extends XDDFChartData
{
    private CTBar3DChart chart;
    
    @Internal
    protected XDDFBar3DChartData(final XDDFChart parent, final CTBar3DChart chart, final Map<Long, XDDFChartAxis> categories, final Map<Long, XDDFValueAxis> values) {
        super(parent);
        this.chart = chart;
        if (chart.getBarDir() == null) {
            chart.addNewBarDir().setVal(BarDirection.BAR.underlying);
        }
        for (final CTBarSer series : chart.getSerList()) {
            this.series.add(new Series(series, series.getCat(), series.getVal()));
        }
        this.defineAxes(categories, values);
    }
    
    private void defineAxes(final Map<Long, XDDFChartAxis> categories, final Map<Long, XDDFValueAxis> values) {
        if (this.chart.sizeOfAxIdArray() == 0) {
            for (final Long id : categories.keySet()) {
                this.chart.addNewAxId().setVal((long)id);
            }
            for (final Long id : values.keySet()) {
                this.chart.addNewAxId().setVal((long)id);
            }
        }
        this.defineAxes(this.chart.getAxIdArray(), categories, values);
    }
    
    @Internal
    @Override
    protected void removeCTSeries(final int n) {
        this.chart.removeSer(n);
    }
    
    @Override
    public void setVaryColors(final Boolean varyColors) {
        if (varyColors == null) {
            if (this.chart.isSetVaryColors()) {
                this.chart.unsetVaryColors();
            }
        }
        else if (this.chart.isSetVaryColors()) {
            this.chart.getVaryColors().setVal((boolean)varyColors);
        }
        else {
            this.chart.addNewVaryColors().setVal((boolean)varyColors);
        }
    }
    
    public BarDirection getBarDirection() {
        return BarDirection.valueOf(this.chart.getBarDir().getVal());
    }
    
    public void setBarDirection(final BarDirection direction) {
        this.chart.getBarDir().setVal(direction.underlying);
    }
    
    public BarGrouping getBarGrouping() {
        if (this.chart.isSetGrouping()) {
            return BarGrouping.valueOf(this.chart.getGrouping().getVal());
        }
        return null;
    }
    
    public void setBarGrouping(final BarGrouping grouping) {
        if (grouping == null) {
            if (this.chart.isSetGrouping()) {
                this.chart.unsetGrouping();
            }
        }
        else if (this.chart.isSetGrouping()) {
            this.chart.getGrouping().setVal(grouping.underlying);
        }
        else {
            this.chart.addNewGrouping().setVal(grouping.underlying);
        }
    }
    
    public Integer getGapDepth() {
        if (this.chart.isSetGapDepth()) {
            return this.chart.getGapDepth().getVal();
        }
        return null;
    }
    
    public void setGapDepth(final Integer depth) {
        if (depth == null) {
            if (this.chart.isSetGapDepth()) {
                this.chart.unsetGapDepth();
            }
        }
        else if (this.chart.isSetGapDepth()) {
            this.chart.getGapDepth().setVal((int)depth);
        }
        else {
            this.chart.addNewGapDepth().setVal((int)depth);
        }
    }
    
    public Integer getGapWidth() {
        if (this.chart.isSetGapWidth()) {
            return this.chart.getGapWidth().getVal();
        }
        return null;
    }
    
    public void setGapWidth(final Integer width) {
        if (width == null) {
            if (this.chart.isSetGapWidth()) {
                this.chart.unsetGapWidth();
            }
        }
        else if (this.chart.isSetGapWidth()) {
            this.chart.getGapWidth().setVal((int)width);
        }
        else {
            this.chart.addNewGapWidth().setVal((int)width);
        }
    }
    
    public Shape getShape() {
        if (this.chart.isSetShape()) {
            return Shape.valueOf(this.chart.getShape().getVal());
        }
        return null;
    }
    
    public void setShape(final Shape shape) {
        if (shape == null) {
            if (this.chart.isSetShape()) {
                this.chart.unsetShape();
            }
        }
        else if (this.chart.isSetShape()) {
            this.chart.getShape().setVal(shape.underlying);
        }
        else {
            this.chart.addNewShape().setVal(shape.underlying);
        }
    }
    
    @Override
    public XDDFChartData.Series addSeries(final XDDFDataSource<?> category, final XDDFNumericalDataSource<? extends Number> values) {
        final long index = this.parent.incrementSeriesCount();
        final CTBarSer ctSer = this.chart.addNewSer();
        ctSer.addNewTx();
        ctSer.addNewCat();
        ctSer.addNewVal();
        ctSer.addNewIdx().setVal(index);
        ctSer.addNewOrder().setVal(index);
        final Series added = new Series(ctSer, category, values);
        this.series.add(added);
        return added;
    }
    
    public class Series extends XDDFChartData.Series
    {
        private CTBarSer series;
        
        protected Series(final CTBarSer series, final XDDFDataSource<?> category, final XDDFNumericalDataSource<? extends Number> values) {
            super(category, values);
            this.series = series;
        }
        
        protected Series(final CTBarSer series, final CTAxDataSource category, final CTNumDataSource values) {
            super(XDDFDataSourcesFactory.fromDataSource(category), XDDFDataSourcesFactory.fromDataSource(values));
            this.series = series;
        }
        
        @Override
        protected CTSerTx getSeriesText() {
            if (this.series.isSetTx()) {
                return this.series.getTx();
            }
            return this.series.addNewTx();
        }
        
        public boolean hasErrorBars() {
            return this.series.isSetErrBars();
        }
        
        public XDDFErrorBars getErrorBars() {
            if (this.series.isSetErrBars()) {
                return new XDDFErrorBars(this.series.getErrBars());
            }
            return null;
        }
        
        public void setErrorBars(final XDDFErrorBars bars) {
            if (bars == null) {
                if (this.series.isSetErrBars()) {
                    this.series.unsetErrBars();
                }
            }
            else if (this.series.isSetErrBars()) {
                this.series.getErrBars().set(bars.getXmlObject());
            }
            else {
                this.series.addNewErrBars().set(bars.getXmlObject());
            }
        }
        
        public boolean getInvertIfNegative() {
            return this.series.isSetInvertIfNegative() && this.series.getInvertIfNegative().getVal();
        }
        
        public void setInvertIfNegative(final boolean invertIfNegative) {
            if (this.series.isSetInvertIfNegative()) {
                this.series.getInvertIfNegative().setVal(invertIfNegative);
            }
            else {
                this.series.addNewInvertIfNegative().setVal(invertIfNegative);
            }
        }
        
        @Override
        public void setShowLeaderLines(final boolean showLeaderLines) {
            if (!this.series.isSetDLbls()) {
                this.series.addNewDLbls();
            }
            if (this.series.getDLbls().isSetShowLeaderLines()) {
                this.series.getDLbls().getShowLeaderLines().setVal(showLeaderLines);
            }
            else {
                this.series.getDLbls().addNewShowLeaderLines().setVal(showLeaderLines);
            }
        }
        
        @Override
        public XDDFShapeProperties getShapeProperties() {
            if (this.series.isSetSpPr()) {
                return new XDDFShapeProperties(this.series.getSpPr());
            }
            return null;
        }
        
        @Override
        public void setShapeProperties(final XDDFShapeProperties properties) {
            if (properties == null) {
                if (this.series.isSetSpPr()) {
                    this.series.unsetSpPr();
                }
            }
            else if (this.series.isSetSpPr()) {
                this.series.setSpPr(properties.getXmlObject());
            }
            else {
                this.series.addNewSpPr().set((XmlObject)properties.getXmlObject());
            }
        }
        
        @Override
        protected CTAxDataSource getAxDS() {
            return this.series.getCat();
        }
        
        @Override
        protected CTNumDataSource getNumDS() {
            return this.series.getVal();
        }
        
        @Override
        protected void setIndex(final long val) {
            this.series.getIdx().setVal(val);
        }
        
        @Override
        protected void setOrder(final long val) {
            this.series.getOrder().setVal(val);
        }
    }
}
