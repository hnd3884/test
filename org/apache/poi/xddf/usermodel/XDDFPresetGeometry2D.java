package org.apache.poi.xddf.usermodel;

import org.openxmlformats.schemas.drawingml.x2006.main.CTGeomGuide;
import java.util.Collections;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.List;
import org.apache.poi.util.Internal;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPresetGeometry2D;

public class XDDFPresetGeometry2D
{
    private CTPresetGeometry2D geometry;
    
    protected XDDFPresetGeometry2D(final CTPresetGeometry2D geometry) {
        this.geometry = geometry;
    }
    
    @Internal
    protected CTPresetGeometry2D getXmlObject() {
        return this.geometry;
    }
    
    public PresetGeometry getGeometry() {
        return PresetGeometry.valueOf(this.geometry.getPrst());
    }
    
    public void setGeometry(final PresetGeometry preset) {
        this.geometry.setPrst(preset.underlying);
    }
    
    public XDDFGeometryGuide addAdjustValue() {
        if (!this.geometry.isSetAvLst()) {
            this.geometry.addNewAvLst();
        }
        return new XDDFGeometryGuide(this.geometry.getAvLst().addNewGd());
    }
    
    public XDDFGeometryGuide insertAdjustValue(final int index) {
        if (!this.geometry.isSetAvLst()) {
            this.geometry.addNewAvLst();
        }
        return new XDDFGeometryGuide(this.geometry.getAvLst().insertNewGd(index));
    }
    
    public void removeAdjustValue(final int index) {
        if (this.geometry.isSetAvLst()) {
            this.geometry.getAvLst().removeGd(index);
        }
    }
    
    public XDDFGeometryGuide getAdjustValue(final int index) {
        if (this.geometry.isSetAvLst()) {
            return new XDDFGeometryGuide(this.geometry.getAvLst().getGdArray(index));
        }
        return null;
    }
    
    public List<XDDFGeometryGuide> getAdjustValues() {
        if (this.geometry.isSetAvLst()) {
            return Collections.unmodifiableList((List<? extends XDDFGeometryGuide>)this.geometry.getAvLst().getGdList().stream().map(guide -> new XDDFGeometryGuide(guide)).collect(Collectors.toList()));
        }
        return Collections.emptyList();
    }
}
