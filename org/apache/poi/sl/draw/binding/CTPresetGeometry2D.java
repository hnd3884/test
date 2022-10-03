package org.apache.poi.sl.draw.binding;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CT_PresetGeometry2D", propOrder = { "avLst" })
public class CTPresetGeometry2D
{
    protected CTGeomGuideList avLst;
    @XmlAttribute(name = "prst", required = true)
    protected STShapeType prst;
    
    public CTGeomGuideList getAvLst() {
        return this.avLst;
    }
    
    public void setAvLst(final CTGeomGuideList value) {
        this.avLst = value;
    }
    
    public boolean isSetAvLst() {
        return this.avLst != null;
    }
    
    public STShapeType getPrst() {
        return this.prst;
    }
    
    public void setPrst(final STShapeType value) {
        this.prst = value;
    }
    
    public boolean isSetPrst() {
        return this.prst != null;
    }
}
