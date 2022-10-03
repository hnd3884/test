package org.apache.poi.sl.draw.binding;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CT_Scale2D", propOrder = { "sx", "sy" })
public class CTScale2D
{
    @XmlElement(required = true)
    protected CTRatio sx;
    @XmlElement(required = true)
    protected CTRatio sy;
    
    public CTRatio getSx() {
        return this.sx;
    }
    
    public void setSx(final CTRatio value) {
        this.sx = value;
    }
    
    public boolean isSetSx() {
        return this.sx != null;
    }
    
    public CTRatio getSy() {
        return this.sy;
    }
    
    public void setSy(final CTRatio value) {
        this.sy = value;
    }
    
    public boolean isSetSy() {
        return this.sy != null;
    }
}
