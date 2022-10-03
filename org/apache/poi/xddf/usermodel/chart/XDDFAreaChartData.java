package org.apache.poi.xddf.usermodel.chart;

import org.apache.xmlbeans.XmlObject;
import org.apache.poi.xddf.usermodel.XDDFShapeProperties;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTSerTx;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTNumDataSource;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTAxDataSource;
import org.apache.poi.util.Internal;
import java.util.Iterator;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTAreaSer;
import java.util.Map;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTAreaChart;

public class XDDFAreaChartData extends XDDFChartData
{
    private CTAreaChart chart;
    
    @Internal
    protected XDDFAreaChartData(final XDDFChart parent, final CTAreaChart chart, final Map<Long, XDDFChartAxis> categories, final Map<Long, XDDFValueAxis> values) {
        super(parent);
        this.chart = chart;
        for (final CTAreaSer series : chart.getSerList()) {
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
    
    public Grouping getGrouping() {
        if (this.chart.isSetGrouping()) {
            return Grouping.valueOf(this.chart.getGrouping().getVal());
        }
        return null;
    }
    
    public void setGrouping(final Grouping grouping) {
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
    
    @Override
    public XDDFChartData.Series addSeries(final XDDFDataSource<?> category, final XDDFNumericalDataSource<? extends Number> values) {
        final long index = this.parent.incrementSeriesCount();
        final CTAreaSer ctSer = this.chart.addNewSer();
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
        private CTAreaSer series;
        
        protected Series(final CTAreaSer series, final XDDFDataSource<?> category, final XDDFNumericalDataSource<? extends Number> values) {
            super(category, values);
            this.series = series;
        }
        
        protected Series(final CTAreaSer series, final CTAxDataSource category, final CTNumDataSource values) {
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
