package org.apache.poi.xddf.usermodel;

import org.openxmlformats.schemas.drawingml.x2006.main.CTPolarAdjustHandle;
import org.openxmlformats.schemas.drawingml.x2006.main.CTXYAdjustHandle;
import org.openxmlformats.schemas.drawingml.x2006.main.CTConnectionSite;
import org.openxmlformats.schemas.drawingml.x2006.main.CTGeomGuide;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPath2D;
import java.util.Collections;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.List;
import org.apache.poi.util.Internal;
import org.openxmlformats.schemas.drawingml.x2006.main.CTCustomGeometry2D;

public class XDDFCustomGeometry2D
{
    private CTCustomGeometry2D geometry;
    
    protected XDDFCustomGeometry2D(final CTCustomGeometry2D geometry) {
        this.geometry = geometry;
    }
    
    @Internal
    protected CTCustomGeometry2D getXmlObject() {
        return this.geometry;
    }
    
    public XDDFGeometryRectangle getRectangle() {
        if (this.geometry.isSetRect()) {
            return new XDDFGeometryRectangle(this.geometry.getRect());
        }
        return null;
    }
    
    public void setRectangle(final XDDFGeometryRectangle rectangle) {
        if (rectangle == null) {
            if (this.geometry.isSetRect()) {
                this.geometry.unsetRect();
            }
        }
        else {
            this.geometry.setRect(rectangle.getXmlObject());
        }
    }
    
    public XDDFAdjustHandlePolar addPolarAdjustHandle() {
        if (!this.geometry.isSetAhLst()) {
            this.geometry.addNewAhLst();
        }
        return new XDDFAdjustHandlePolar(this.geometry.getAhLst().addNewAhPolar());
    }
    
    public XDDFAdjustHandlePolar insertPolarAdjustHandle(final int index) {
        if (!this.geometry.isSetAhLst()) {
            this.geometry.addNewAhLst();
        }
        return new XDDFAdjustHandlePolar(this.geometry.getAhLst().insertNewAhPolar(index));
    }
    
    public void removePolarAdjustHandle(final int index) {
        if (this.geometry.isSetAhLst()) {
            this.geometry.getAhLst().removeAhPolar(index);
        }
    }
    
    public XDDFAdjustHandlePolar getPolarAdjustHandle(final int index) {
        if (this.geometry.isSetAhLst()) {
            return new XDDFAdjustHandlePolar(this.geometry.getAhLst().getAhPolarArray(index));
        }
        return null;
    }
    
    public List<XDDFAdjustHandlePolar> getPolarAdjustHandles() {
        if (this.geometry.isSetAhLst()) {
            return Collections.unmodifiableList((List<? extends XDDFAdjustHandlePolar>)this.geometry.getAhLst().getAhPolarList().stream().map(guide -> new XDDFAdjustHandlePolar(guide)).collect(Collectors.toList()));
        }
        return Collections.emptyList();
    }
    
    public XDDFAdjustHandleXY addXYAdjustHandle() {
        if (!this.geometry.isSetAhLst()) {
            this.geometry.addNewAhLst();
        }
        return new XDDFAdjustHandleXY(this.geometry.getAhLst().addNewAhXY());
    }
    
    public XDDFAdjustHandleXY insertXYAdjustHandle(final int index) {
        if (!this.geometry.isSetAhLst()) {
            this.geometry.addNewAhLst();
        }
        return new XDDFAdjustHandleXY(this.geometry.getAhLst().insertNewAhXY(index));
    }
    
    public void removeXYAdjustHandle(final int index) {
        if (this.geometry.isSetAhLst()) {
            this.geometry.getAhLst().removeAhXY(index);
        }
    }
    
    public XDDFAdjustHandleXY getXYAdjustHandle(final int index) {
        if (this.geometry.isSetAhLst()) {
            return new XDDFAdjustHandleXY(this.geometry.getAhLst().getAhXYArray(index));
        }
        return null;
    }
    
    public List<XDDFAdjustHandleXY> getXYAdjustHandles() {
        if (this.geometry.isSetAhLst()) {
            return Collections.unmodifiableList((List<? extends XDDFAdjustHandleXY>)this.geometry.getAhLst().getAhXYList().stream().map(guide -> new XDDFAdjustHandleXY(guide)).collect(Collectors.toList()));
        }
        return Collections.emptyList();
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
    
    public XDDFConnectionSite addConnectionSite() {
        if (!this.geometry.isSetCxnLst()) {
            this.geometry.addNewCxnLst();
        }
        return new XDDFConnectionSite(this.geometry.getCxnLst().addNewCxn());
    }
    
    public XDDFConnectionSite insertConnectionSite(final int index) {
        if (!this.geometry.isSetCxnLst()) {
            this.geometry.addNewCxnLst();
        }
        return new XDDFConnectionSite(this.geometry.getCxnLst().insertNewCxn(index));
    }
    
    public void removeConnectionSite(final int index) {
        if (this.geometry.isSetCxnLst()) {
            this.geometry.getCxnLst().removeCxn(index);
        }
    }
    
    public XDDFConnectionSite getConnectionSite(final int index) {
        if (this.geometry.isSetCxnLst()) {
            return new XDDFConnectionSite(this.geometry.getCxnLst().getCxnArray(index));
        }
        return null;
    }
    
    public List<XDDFConnectionSite> getConnectionSites() {
        if (this.geometry.isSetCxnLst()) {
            return Collections.unmodifiableList((List<? extends XDDFConnectionSite>)this.geometry.getCxnLst().getCxnList().stream().map(guide -> new XDDFConnectionSite(guide)).collect(Collectors.toList()));
        }
        return Collections.emptyList();
    }
    
    public XDDFGeometryGuide addGuide() {
        if (!this.geometry.isSetGdLst()) {
            this.geometry.addNewGdLst();
        }
        return new XDDFGeometryGuide(this.geometry.getGdLst().addNewGd());
    }
    
    public XDDFGeometryGuide insertGuide(final int index) {
        if (!this.geometry.isSetGdLst()) {
            this.geometry.addNewGdLst();
        }
        return new XDDFGeometryGuide(this.geometry.getGdLst().insertNewGd(index));
    }
    
    public void removeGuide(final int index) {
        if (this.geometry.isSetGdLst()) {
            this.geometry.getGdLst().removeGd(index);
        }
    }
    
    public XDDFGeometryGuide getGuide(final int index) {
        if (this.geometry.isSetGdLst()) {
            return new XDDFGeometryGuide(this.geometry.getGdLst().getGdArray(index));
        }
        return null;
    }
    
    public List<XDDFGeometryGuide> getGuides() {
        if (this.geometry.isSetGdLst()) {
            return Collections.unmodifiableList((List<? extends XDDFGeometryGuide>)this.geometry.getGdLst().getGdList().stream().map(guide -> new XDDFGeometryGuide(guide)).collect(Collectors.toList()));
        }
        return Collections.emptyList();
    }
    
    public XDDFPath addNewPath() {
        return new XDDFPath(this.geometry.getPathLst().addNewPath());
    }
    
    public XDDFPath insertNewPath(final int index) {
        return new XDDFPath(this.geometry.getPathLst().insertNewPath(index));
    }
    
    public void removePath(final int index) {
        this.geometry.getPathLst().removePath(index);
    }
    
    public XDDFPath getPath(final int index) {
        return new XDDFPath(this.geometry.getPathLst().getPathArray(index));
    }
    
    public List<XDDFPath> getPaths() {
        return Collections.unmodifiableList((List<? extends XDDFPath>)this.geometry.getPathLst().getPathList().stream().map(ds -> new XDDFPath(ds)).collect(Collectors.toList()));
    }
}
