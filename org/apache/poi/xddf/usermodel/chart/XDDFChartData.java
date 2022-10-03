package org.apache.poi.xddf.usermodel.chart;

import org.openxmlformats.schemas.drawingml.x2006.chart.CTNumRef;
import org.apache.poi.xddf.usermodel.XDDFLineProperties;
import org.apache.poi.xddf.usermodel.XDDFFillProperties;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTNumData;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTStrData;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTStrRef;
import org.apache.poi.ss.util.CellReference;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTNumDataSource;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTAxDataSource;
import org.apache.poi.xddf.usermodel.XDDFShapeProperties;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTSerTx;
import org.apache.poi.util.Internal;
import java.util.Locale;
import java.util.Collections;
import java.util.Map;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTUnsignedInt;
import java.util.ArrayList;
import java.util.List;

public abstract class XDDFChartData
{
    protected XDDFChart parent;
    protected List<Series> series;
    private XDDFCategoryAxis categoryAxis;
    private List<XDDFValueAxis> valueAxes;
    
    protected XDDFChartData(final XDDFChart chart) {
        this.parent = chart;
        this.series = new ArrayList<Series>();
    }
    
    protected void defineAxes(final CTUnsignedInt[] axes, final Map<Long, XDDFChartAxis> categories, final Map<Long, XDDFValueAxis> values) {
        final List<XDDFValueAxis> list = new ArrayList<XDDFValueAxis>(axes.length);
        for (final CTUnsignedInt axe : axes) {
            final Long axisId = axe.getVal();
            final XDDFChartAxis category = categories.get(axisId);
            if (category == null) {
                final XDDFValueAxis axis = values.get(axisId);
                if (axis != null) {
                    list.add(axis);
                }
            }
            else if (category instanceof XDDFCategoryAxis) {
                this.categoryAxis = (XDDFCategoryAxis)category;
            }
        }
        this.valueAxes = Collections.unmodifiableList((List<? extends XDDFValueAxis>)list);
    }
    
    public XDDFCategoryAxis getCategoryAxis() {
        return this.categoryAxis;
    }
    
    public List<XDDFValueAxis> getValueAxes() {
        return this.valueAxes;
    }
    
    @Deprecated
    public List<Series> getSeries() {
        return Collections.unmodifiableList((List<? extends Series>)this.series);
    }
    
    public final int getSeriesCount() {
        return this.series.size();
    }
    
    public final Series getSeries(final int n) {
        return this.series.get(n);
    }
    
    public final void removeSeries(final int n) {
        final String procName = "removeSeries";
        if (n < 0 || this.series.size() <= n) {
            throw new IllegalArgumentException(String.format(Locale.ROOT, "%s(%d): illegal index", "removeSeries", n));
        }
        this.series.remove(n);
        this.removeCTSeries(n);
    }
    
    @Internal
    protected abstract void removeCTSeries(final int p0);
    
    public abstract void setVaryColors(final Boolean p0);
    
    public abstract Series addSeries(final XDDFDataSource<?> p0, final XDDFNumericalDataSource<? extends Number> p1);
    
    public abstract class Series
    {
        protected XDDFDataSource<?> categoryData;
        protected XDDFNumericalDataSource<? extends Number> valuesData;
        
        protected abstract CTSerTx getSeriesText();
        
        public abstract void setShowLeaderLines(final boolean p0);
        
        public abstract XDDFShapeProperties getShapeProperties();
        
        public abstract void setShapeProperties(final XDDFShapeProperties p0);
        
        protected abstract CTAxDataSource getAxDS();
        
        protected abstract CTNumDataSource getNumDS();
        
        protected abstract void setIndex(final long p0);
        
        protected abstract void setOrder(final long p0);
        
        protected Series(final XDDFDataSource<?> category, final XDDFNumericalDataSource<? extends Number> values) {
            this.replaceData(category, values);
        }
        
        public void replaceData(final XDDFDataSource<?> category, final XDDFNumericalDataSource<? extends Number> values) {
            if (category == null || values == null) {
                throw new IllegalStateException("Category and values must be defined before filling chart data.");
            }
            final int numOfPoints = category.getPointCount();
            if (numOfPoints != values.getPointCount()) {
                throw new IllegalStateException("Category and values must have the same point count.");
            }
            this.categoryData = category;
            this.valuesData = values;
        }
        
        public void setTitle(final String title, final CellReference titleRef) {
            if (titleRef == null) {
                this.getSeriesText().setV(title);
            }
            else {
                CTStrRef ref;
                if (this.getSeriesText().isSetStrRef()) {
                    ref = this.getSeriesText().getStrRef();
                }
                else {
                    ref = this.getSeriesText().addNewStrRef();
                }
                ref.setF(titleRef.formatAsString());
                if (title != null) {
                    CTStrData cache;
                    if (ref.isSetStrCache()) {
                        cache = ref.getStrCache();
                    }
                    else {
                        cache = ref.addNewStrCache();
                    }
                    if (cache.sizeOfPtArray() < 1) {
                        cache.addNewPtCount().setVal(1L);
                        cache.addNewPt().setIdx(0L);
                    }
                    cache.getPtArray(0).setV(title);
                }
            }
        }
        
        public XDDFDataSource<?> getCategoryData() {
            return this.categoryData;
        }
        
        public XDDFNumericalDataSource<? extends Number> getValuesData() {
            return this.valuesData;
        }
        
        public void plot() {
            if (this.categoryData.isNumeric()) {
                final CTNumData cache = this.retrieveNumCache(this.getAxDS(), this.categoryData);
                ((XDDFNumericalDataSource)this.categoryData).fillNumericalCache(cache);
            }
            else {
                final CTStrData cache2 = this.retrieveStrCache(this.getAxDS(), this.categoryData);
                this.categoryData.fillStringCache(cache2);
            }
            final CTNumData cache = this.retrieveNumCache(this.getNumDS(), this.valuesData);
            this.valuesData.fillNumericalCache(cache);
        }
        
        public void setFillProperties(final XDDFFillProperties fill) {
            XDDFShapeProperties properties = this.getShapeProperties();
            if (properties == null) {
                properties = new XDDFShapeProperties();
            }
            properties.setFillProperties(fill);
            this.setShapeProperties(properties);
        }
        
        public void setLineProperties(final XDDFLineProperties line) {
            XDDFShapeProperties properties = this.getShapeProperties();
            if (properties == null) {
                properties = new XDDFShapeProperties();
            }
            properties.setLineProperties(line);
            this.setShapeProperties(properties);
        }
        
        private CTNumData retrieveNumCache(final CTAxDataSource axDataSource, final XDDFDataSource<?> data) {
            CTNumData numCache;
            if (data.isReference()) {
                CTNumRef numRef;
                if (axDataSource.isSetNumRef()) {
                    numRef = axDataSource.getNumRef();
                }
                else {
                    numRef = axDataSource.addNewNumRef();
                }
                if (numRef.isSetNumCache()) {
                    numCache = numRef.getNumCache();
                }
                else {
                    numCache = numRef.addNewNumCache();
                }
                numRef.setF(data.getDataRangeReference());
                if (axDataSource.isSetNumLit()) {
                    axDataSource.unsetNumLit();
                }
            }
            else {
                if (axDataSource.isSetNumLit()) {
                    numCache = axDataSource.getNumLit();
                }
                else {
                    numCache = axDataSource.addNewNumLit();
                }
                if (axDataSource.isSetNumRef()) {
                    axDataSource.unsetNumRef();
                }
            }
            return numCache;
        }
        
        private CTStrData retrieveStrCache(final CTAxDataSource axDataSource, final XDDFDataSource<?> data) {
            CTStrData strCache;
            if (data.isReference()) {
                CTStrRef strRef;
                if (axDataSource.isSetStrRef()) {
                    strRef = axDataSource.getStrRef();
                }
                else {
                    strRef = axDataSource.addNewStrRef();
                }
                if (strRef.isSetStrCache()) {
                    strCache = strRef.getStrCache();
                }
                else {
                    strCache = strRef.addNewStrCache();
                }
                strRef.setF(data.getDataRangeReference());
                if (axDataSource.isSetStrLit()) {
                    axDataSource.unsetStrLit();
                }
            }
            else {
                if (axDataSource.isSetStrLit()) {
                    strCache = axDataSource.getStrLit();
                }
                else {
                    strCache = axDataSource.addNewStrLit();
                }
                if (axDataSource.isSetStrRef()) {
                    axDataSource.unsetStrRef();
                }
            }
            return strCache;
        }
        
        private CTNumData retrieveNumCache(final CTNumDataSource numDataSource, final XDDFDataSource<?> data) {
            CTNumData numCache;
            if (data.isReference()) {
                CTNumRef numRef;
                if (numDataSource.isSetNumRef()) {
                    numRef = numDataSource.getNumRef();
                }
                else {
                    numRef = numDataSource.addNewNumRef();
                }
                if (numRef.isSetNumCache()) {
                    numCache = numRef.getNumCache();
                }
                else {
                    numCache = numRef.addNewNumCache();
                }
                numRef.setF(data.getDataRangeReference());
                if (numDataSource.isSetNumLit()) {
                    numDataSource.unsetNumLit();
                }
            }
            else {
                if (numDataSource.isSetNumLit()) {
                    numCache = numDataSource.getNumLit();
                }
                else {
                    numCache = numDataSource.addNewNumLit();
                }
                if (numDataSource.isSetNumRef()) {
                    numDataSource.unsetNumRef();
                }
            }
            return numCache;
        }
    }
}
