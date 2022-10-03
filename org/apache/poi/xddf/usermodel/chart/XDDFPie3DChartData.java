package org.apache.poi.xddf.usermodel.chart;

import org.apache.xmlbeans.XmlObject;
import org.apache.poi.xddf.usermodel.XDDFShapeProperties;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTSerTx;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTNumDataSource;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTAxDataSource;
import org.apache.poi.util.Internal;
import java.util.Iterator;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTPieSer;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTPie3DChart;

public class XDDFPie3DChartData extends XDDFChartData
{
    private CTPie3DChart chart;
    
    @Internal
    protected XDDFPie3DChartData(final XDDFChart parent, final CTPie3DChart chart) {
        super(parent);
        this.chart = chart;
        for (final CTPieSer series : chart.getSerList()) {
            this.series.add(new Series(series, series.getCat(), series.getVal()));
        }
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
    
    @Override
    public XDDFChartData.Series addSeries(final XDDFDataSource<?> category, final XDDFNumericalDataSource<? extends Number> values) {
        final long index = this.parent.incrementSeriesCount();
        final CTPieSer ctSer = this.chart.addNewSer();
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
        private CTPieSer series;
        
        protected Series(final CTPieSer series, final XDDFDataSource<?> category, final XDDFNumericalDataSource<? extends Number> values) {
            super(category, values);
            this.series = series;
        }
        
        protected Series(final CTPieSer series, final CTAxDataSource category, final CTNumDataSource values) {
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
        
        public Long getExplosion() {
            if (this.series.isSetExplosion()) {
                return this.series.getExplosion().getVal();
            }
            return null;
        }
        
        public void setExplosion(final Long explosion) {
            if (explosion == null) {
                if (this.series.isSetExplosion()) {
                    this.series.unsetExplosion();
                }
            }
            else if (this.series.isSetExplosion()) {
                this.series.getExplosion().setVal((long)explosion);
            }
            else {
                this.series.addNewExplosion().setVal((long)explosion);
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
