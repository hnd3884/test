package org.apache.poi.sl.draw.binding;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CT_Transform2D", propOrder = { "off", "ext" })
public class CTTransform2D
{
    protected CTPoint2D off;
    protected CTPositiveSize2D ext;
    @XmlAttribute(name = "rot")
    protected Integer rot;
    @XmlAttribute(name = "flipH")
    protected Boolean flipH;
    @XmlAttribute(name = "flipV")
    protected Boolean flipV;
    
    public CTPoint2D getOff() {
        return this.off;
    }
    
    public void setOff(final CTPoint2D value) {
        this.off = value;
    }
    
    public boolean isSetOff() {
        return this.off != null;
    }
    
    public CTPositiveSize2D getExt() {
        return this.ext;
    }
    
    public void setExt(final CTPositiveSize2D value) {
        this.ext = value;
    }
    
    public boolean isSetExt() {
        return this.ext != null;
    }
    
    public int getRot() {
        if (this.rot == null) {
            return 0;
        }
        return this.rot;
    }
    
    public void setRot(final int value) {
        this.rot = value;
    }
    
    public boolean isSetRot() {
        return this.rot != null;
    }
    
    public void unsetRot() {
        this.rot = null;
    }
    
    public boolean isFlipH() {
        return this.flipH != null && this.flipH;
    }
    
    public void setFlipH(final boolean value) {
        this.flipH = value;
    }
    
    public boolean isSetFlipH() {
        return this.flipH != null;
    }
    
    public void unsetFlipH() {
        this.flipH = null;
    }
    
    public boolean isFlipV() {
        return this.flipV != null && this.flipV;
    }
    
    public void setFlipV(final boolean value) {
        this.flipV = value;
    }
    
    public boolean isSetFlipV() {
        return this.flipV != null;
    }
    
    public void unsetFlipV() {
        this.flipV = null;
    }
}
