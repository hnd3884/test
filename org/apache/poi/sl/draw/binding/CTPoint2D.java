package org.apache.poi.sl.draw.binding;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CT_Point2D")
public class CTPoint2D
{
    @XmlAttribute(name = "x", required = true)
    protected long x;
    @XmlAttribute(name = "y", required = true)
    protected long y;
    
    public long getX() {
        return this.x;
    }
    
    public void setX(final long value) {
        this.x = value;
    }
    
    public boolean isSetX() {
        return true;
    }
    
    public long getY() {
        return this.y;
    }
    
    public void setY(final long value) {
        this.y = value;
    }
    
    public boolean isSetY() {
        return true;
    }
}
