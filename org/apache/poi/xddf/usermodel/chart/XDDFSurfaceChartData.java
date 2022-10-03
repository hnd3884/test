package org.apache.poi.xddf.usermodel.chart;

import org.apache.xmlbeans.XmlObject;
import org.apache.poi.xddf.usermodel.XDDFShapeProperties;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTSerTx;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTNumDataSource;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTAxDataSource;
import org.apache.poi.util.Internal;
import java.util.Iterator;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTSurfaceSer;
import java.util.Map;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTSurfaceChart;

public class XDDFSurfaceChartData extends XDDFChartData
{
    private CTSurfaceChart chart;
    
    @Internal
    protected XDDFSurfaceChartData(final XDDFChart parent, final CTSurfaceChart chart, final Map<Long, XDDFChartAxis> categories, final Map<Long, XDDFValueAxis> values) {
        super(parent);
        this.chart = chart;
        for (final CTSurfaceSer series : chart.getSerList()) {
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
    }
    
    public void defineSeriesAxis(final XDDFSeriesAxis seriesAxis) {
        this.chart.addNewAxId().setVal(seriesAxis.getId());
    }
    
    public Boolean isWireframe() {
        if (this.chart.isSetWireframe()) {
            return this.chart.getWireframe().getVal();
        }
        return null;
    }
    
    public void setWireframe(final Boolean show) {
        if (show == null) {
            if (this.chart.isSetWireframe()) {
                this.chart.unsetWireframe();
            }
        }
        else if (this.chart.isSetWireframe()) {
            this.chart.getWireframe().setVal((boolean)show);
        }
        else {
            this.chart.addNewWireframe().setVal((boolean)show);
        }
    }
    
    @Override
    public XDDFChartData.Series addSeries(final XDDFDataSource<?> category, final XDDFNumericalDataSource<? extends Number> values) {
        final long index = this.parent.incrementSeriesCount();
        final CTSurfaceSer ctSer = this.chart.addNewSer();
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
        private CTSurfaceSer series;
        
        protected Series(final CTSurfaceSer series, final XDDFDataSource<?> category, final XDDFNumericalDataSource<? extends Number> values) {
            super(category, values);
            this.series = series;
        }
        
        protected Series(final CTSurfaceSer series, final CTAxDataSource category, final CTNumDataSource values) {
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
