package org.apache.poi.xddf.usermodel.chart;

import org.apache.xmlbeans.XmlObject;
import org.apache.poi.xddf.usermodel.XDDFShapeProperties;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTMarker;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTSerTx;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTNumDataSource;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTAxDataSource;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTScatterStyle;
import org.apache.poi.util.Internal;
import java.util.Iterator;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTScatterSer;
import java.util.Map;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTScatterChart;

public class XDDFScatterChartData extends XDDFChartData
{
    private CTScatterChart chart;
    
    @Internal
    protected XDDFScatterChartData(final XDDFChart parent, final CTScatterChart chart, final Map<Long, XDDFChartAxis> categories, final Map<Long, XDDFValueAxis> values) {
        super(parent);
        this.chart = chart;
        for (final CTScatterSer series : chart.getSerList()) {
            this.series.add(new Series(series, series.getXVal(), series.getYVal()));
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
    
    public ScatterStyle getStyle() {
        CTScatterStyle scatterStyle = this.chart.getScatterStyle();
        if (scatterStyle == null) {
            scatterStyle = this.chart.addNewScatterStyle();
            scatterStyle.setVal(ScatterStyle.LINE_MARKER.underlying);
        }
        return ScatterStyle.valueOf(scatterStyle.getVal());
    }
    
    public void setStyle(final ScatterStyle style) {
        CTScatterStyle scatterStyle = this.chart.getScatterStyle();
        if (scatterStyle == null) {
            scatterStyle = this.chart.addNewScatterStyle();
        }
        scatterStyle.setVal(style.underlying);
    }
    
    @Override
    public XDDFChartData.Series addSeries(final XDDFDataSource<?> category, final XDDFNumericalDataSource<? extends Number> values) {
        final long index = this.parent.incrementSeriesCount();
        final CTScatterSer ctSer = this.chart.addNewSer();
        ctSer.addNewXVal();
        ctSer.addNewYVal();
        ctSer.addNewIdx().setVal(index);
        ctSer.addNewOrder().setVal(index);
        final Series added = new Series(ctSer, category, values);
        added.setMarkerStyle(MarkerStyle.NONE);
        this.series.add(added);
        return added;
    }
    
    public class Series extends XDDFChartData.Series
    {
        private CTScatterSer series;
        
        protected Series(final CTScatterSer series, final XDDFDataSource<?> category, final XDDFNumericalDataSource<?> values) {
            super(category, (XDDFNumericalDataSource<? extends Number>)values);
            this.series = series;
        }
        
        protected Series(final CTScatterSer series, final CTAxDataSource category, final CTNumDataSource values) {
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
        
        public int getErrorBarsCount() {
            return this.series.sizeOfErrBarsArray();
        }
        
        public XDDFErrorBars getErrorBars(final int index) {
            return new XDDFErrorBars(this.series.getErrBarsArray(index));
        }
        
        public XDDFErrorBars addNewErrorBars() {
            return new XDDFErrorBars(this.series.addNewErrBars());
        }
        
        public XDDFErrorBars insertNewErrorBars(final int index) {
            return new XDDFErrorBars(this.series.insertNewErrBars(index));
        }
        
        public void removeErrorBars(final int index) {
            this.series.removeErrBars(index);
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
            return this.series.getXVal();
        }
        
        @Override
        protected CTNumDataSource getNumDS() {
            return this.series.getYVal();
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
