package org.apache.poi.xddf.usermodel.chart;

import org.openxmlformats.schemas.drawingml.x2006.chart.CTNumRef;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTNumData;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTNumDataSource;
import org.apache.poi.xddf.usermodel.XDDFShapeProperties;
import org.apache.xmlbeans.XmlObject;
import org.apache.poi.util.Internal;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTErrBars;

public class XDDFErrorBars
{
    private CTErrBars bars;
    
    public XDDFErrorBars() {
        this(CTErrBars.Factory.newInstance());
    }
    
    @Internal
    protected XDDFErrorBars(final CTErrBars bars) {
        this.bars = bars;
    }
    
    @Internal
    protected XmlObject getXmlObject() {
        return (XmlObject)this.bars;
    }
    
    public XDDFChartExtensionList getExtensionList() {
        if (this.bars.isSetExtLst()) {
            return new XDDFChartExtensionList(this.bars.getExtLst());
        }
        return null;
    }
    
    public void setExtensionList(final XDDFChartExtensionList list) {
        if (list == null) {
            if (this.bars.isSetExtLst()) {
                this.bars.unsetExtLst();
            }
        }
        else {
            this.bars.setExtLst(list.getXmlObject());
        }
    }
    
    public XDDFShapeProperties getShapeProperties() {
        if (this.bars.isSetSpPr()) {
            return new XDDFShapeProperties(this.bars.getSpPr());
        }
        return null;
    }
    
    public void setShapeProperties(final XDDFShapeProperties properties) {
        if (properties == null) {
            if (this.bars.isSetSpPr()) {
                this.bars.unsetSpPr();
            }
        }
        else if (this.bars.isSetSpPr()) {
            this.bars.setSpPr(properties.getXmlObject());
        }
        else {
            this.bars.addNewSpPr().set((XmlObject)properties.getXmlObject());
        }
    }
    
    public ErrorBarType getErrorBarType() {
        return ErrorBarType.valueOf(this.bars.getErrBarType().getVal());
    }
    
    public void setErrorBarType(final ErrorBarType barType) {
        this.bars.getErrBarType().setVal(barType.underlying);
    }
    
    public ErrorValueType getErrorValueType() {
        return ErrorValueType.valueOf(this.bars.getErrValType().getVal());
    }
    
    public void setErrorValueType(final ErrorValueType valueType) {
        this.bars.getErrValType().setVal(valueType.underlying);
    }
    
    public ErrorDirection getErrorDirection() {
        if (this.bars.isSetErrDir()) {
            return ErrorDirection.valueOf(this.bars.getErrDir().getVal());
        }
        return null;
    }
    
    public void setErrorDirection(final ErrorDirection direction) {
        if (direction == null) {
            if (this.bars.isSetErrDir()) {
                this.bars.unsetErrDir();
            }
        }
        else if (this.bars.isSetErrDir()) {
            this.bars.getErrDir().setVal(direction.underlying);
        }
        else {
            this.bars.addNewErrDir().setVal(direction.underlying);
        }
    }
    
    public Boolean getNoEndCap() {
        if (this.bars.isSetVal()) {
            return this.bars.getNoEndCap().getVal();
        }
        return null;
    }
    
    public void setNoEndCap(final Boolean noEndCap) {
        if (noEndCap == null) {
            if (this.bars.isSetNoEndCap()) {
                this.bars.unsetNoEndCap();
            }
        }
        else if (this.bars.isSetNoEndCap()) {
            this.bars.getNoEndCap().setVal((boolean)noEndCap);
        }
        else {
            this.bars.addNewNoEndCap().setVal((boolean)noEndCap);
        }
    }
    
    public Double getValue() {
        if (this.bars.isSetVal()) {
            return this.bars.getVal().getVal();
        }
        return null;
    }
    
    public void setValue(final Double value) {
        if (value == null) {
            if (this.bars.isSetVal()) {
                this.bars.unsetVal();
            }
        }
        else if (this.bars.isSetVal()) {
            this.bars.getVal().setVal((double)value);
        }
        else {
            this.bars.addNewVal().setVal((double)value);
        }
    }
    
    public XDDFNumericalDataSource<Double> getMinus() {
        if (this.bars.isSetMinus()) {
            return XDDFDataSourcesFactory.fromDataSource(this.bars.getMinus());
        }
        return null;
    }
    
    public void setMinus(final XDDFNumericalDataSource<Double> ds) {
        if (ds == null) {
            if (this.bars.isSetMinus()) {
                this.bars.unsetMinus();
            }
        }
        else if (this.bars.isSetMinus()) {
            ds.fillNumericalCache(this.retrieveCache(this.bars.getMinus(), ds.getDataRangeReference()));
        }
        else {
            final CTNumDataSource ctDS = this.bars.addNewMinus();
            ctDS.addNewNumLit();
            ds.fillNumericalCache(this.retrieveCache(ctDS, ds.getDataRangeReference()));
        }
    }
    
    public XDDFNumericalDataSource<Double> getPlus() {
        if (this.bars.isSetPlus()) {
            return XDDFDataSourcesFactory.fromDataSource(this.bars.getPlus());
        }
        return null;
    }
    
    public void setPlus(final XDDFNumericalDataSource<Double> ds) {
        if (ds == null) {
            if (this.bars.isSetPlus()) {
                this.bars.unsetPlus();
            }
        }
        else if (this.bars.isSetPlus()) {
            ds.fillNumericalCache(this.retrieveCache(this.bars.getPlus(), ds.getDataRangeReference()));
        }
        else {
            final CTNumDataSource ctDS = this.bars.addNewPlus();
            ctDS.addNewNumLit();
            ds.fillNumericalCache(this.retrieveCache(ctDS, ds.getDataRangeReference()));
        }
    }
    
    private CTNumData retrieveCache(final CTNumDataSource ds, final String dataRangeReference) {
        if (ds.isSetNumRef()) {
            final CTNumRef numRef = ds.getNumRef();
            numRef.setF(dataRangeReference);
            return numRef.getNumCache();
        }
        return ds.getNumLit();
    }
}
