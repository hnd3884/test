package org.apache.poi.sl.draw.binding;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CT_Vector3D")
public class CTVector3D
{
    @XmlAttribute(name = "dx", required = true)
    protected long dx;
    @XmlAttribute(name = "dy", required = true)
    protected long dy;
    @XmlAttribute(name = "dz", required = true)
    protected long dz;
    
    public long getDx() {
        return this.dx;
    }
    
    public void setDx(final long value) {
        this.dx = value;
    }
    
    public boolean isSetDx() {
        return true;
    }
    
    public long getDy() {
        return this.dy;
    }
    
    public void setDy(final long value) {
        this.dy = value;
    }
    
    public boolean isSetDy() {
        return true;
    }
    
    public long getDz() {
        return this.dz;
    }
    
    public void setDz(final long value) {
        this.dz = value;
    }
    
    public boolean isSetDz() {
        return true;
    }
}
