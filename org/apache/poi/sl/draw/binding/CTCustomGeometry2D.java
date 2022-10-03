package org.apache.poi.sl.draw.binding;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CT_CustomGeometry2D", propOrder = { "avLst", "gdLst", "ahLst", "cxnLst", "rect", "pathLst" })
public class CTCustomGeometry2D
{
    protected CTGeomGuideList avLst;
    protected CTGeomGuideList gdLst;
    protected CTAdjustHandleList ahLst;
    protected CTConnectionSiteList cxnLst;
    protected CTGeomRect rect;
    @XmlElement(required = true)
    protected CTPath2DList pathLst;
    
    public CTGeomGuideList getAvLst() {
        return this.avLst;
    }
    
    public void setAvLst(final CTGeomGuideList value) {
        this.avLst = value;
    }
    
    public boolean isSetAvLst() {
        return this.avLst != null;
    }
    
    public CTGeomGuideList getGdLst() {
        return this.gdLst;
    }
    
    public void setGdLst(final CTGeomGuideList value) {
        this.gdLst = value;
    }
    
    public boolean isSetGdLst() {
        return this.gdLst != null;
    }
    
    public CTAdjustHandleList getAhLst() {
        return this.ahLst;
    }
    
    public void setAhLst(final CTAdjustHandleList value) {
        this.ahLst = value;
    }
    
    public boolean isSetAhLst() {
        return this.ahLst != null;
    }
    
    public CTConnectionSiteList getCxnLst() {
        return this.cxnLst;
    }
    
    public void setCxnLst(final CTConnectionSiteList value) {
        this.cxnLst = value;
    }
    
    public boolean isSetCxnLst() {
        return this.cxnLst != null;
    }
    
    public CTGeomRect getRect() {
        return this.rect;
    }
    
    public void setRect(final CTGeomRect value) {
        this.rect = value;
    }
    
    public boolean isSetRect() {
        return this.rect != null;
    }
    
    public CTPath2DList getPathLst() {
        return this.pathLst;
    }
    
    public void setPathLst(final CTPath2DList value) {
        this.pathLst = value;
    }
    
    public boolean isSetPathLst() {
        return this.pathLst != null;
    }
}
