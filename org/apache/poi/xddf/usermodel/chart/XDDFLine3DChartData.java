package org.apache.poi.xddf.usermodel.chart;

import org.openxmlformats.schemas.drawingml.x2006.chart.CTMarker;
import org.apache.xmlbeans.XmlObject;
import org.apache.poi.xddf.usermodel.XDDFShapeProperties;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTSerTx;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTNumDataSource;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTAxDataSource;
import org.apache.poi.util.Internal;
import java.util.Iterator;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTLineSer;
import java.util.Map;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTLine3DChart;

public class XDDFLine3DChartData extends XDDFChartData
{
    private CTLine3DChart chart;
    
    @Internal
    protected XDDFLine3DChartData(final XDDFChart parent, final CTLine3DChart chart, final Map<Long, XDDFChartAxis> categories, final Map<Long, XDDFValueAxis> values) {
        super(parent);
        this.chart = chart;
        for (final CTLineSer series : chart.getSerList()) {
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
        return Grouping.valueOf(this.chart.getGrouping().getVal());
    }
    
    public void setGrouping(final Grouping grouping) {
        if (this.chart.getGrouping() != null) {
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
    
    @Override
    public XDDFChartData.Series addSeries(final XDDFDataSource<?> category, final XDDFNumericalDataSource<? extends Number> values) {
        final long index = this.parent.incrementSeriesCount();
        final CTLineSer ctSer = this.chart.addNewSer();
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
        private CTLineSer series;
        
        protected Series(final CTLineSer series, final XDDFDataSource<?> category, final XDDFNumericalDataSource<? extends Number> values) {
            super(category, values);
            this.series = series;
        }
        
        protected Series(final CTLineSer series, final CTAxDataSource category, final CTNumDataSource values) {
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
        
        public Boolean isSmooth() {
            if (this.series.isSetSmooth()) {
                return this.series.getSmooth().getVal();
            }
            return null;
        }
        
        public void setSmooth(final Boolean smooth) {
            if (smooth == null) {
                if (this.series.isSetSmooth()) {
                    this.series.unsetSmooth();
                }
            }
            else if (this.series.isSetSmooth()) {
                this.series.getSmooth().setVal((boolean)smooth);
            }
            else {
                this.series.addNewSmooth().setVal((boolean)smooth);
            }
        }
        
        public void setMarkerSize(final short size) {
            if (size < 2 || 72 < size) {
                throw new IllegalArgumentException("Minimum inclusive: 2; Maximum inclusive: 72");
            }
            final CTMarker marker = this.getMarker();
            if (marker.isSetSize()) {
                marker.getSize().setVal(size);
            }
            else {
                marker.addNewSize().setVal(size);
            }
        }
        
        public void setMarkerStyle(final MarkerStyle style) {
            final CTMarker marker = this.getMarker();
            if (marker.isSetSymbol()) {
                marker.getSymbol().setVal(style.underlying);
            }
            else {
                marker.addNewSymbol().setVal(style.underlying);
            }
        }
        
        private CTMarker getMarker() {
            if (this.series.isSetMarker()) {
                return this.series.getMarker();
            }
            return this.series.addNewMarker();
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
