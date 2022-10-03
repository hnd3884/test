package org.apache.poi.sl.draw.binding;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CT_PositiveSize2D")
public class CTPositiveSize2D
{
    @XmlAttribute(name = "cx", required = true)
    protected long cx;
    @XmlAttribute(name = "cy", required = true)
    protected long cy;
    
    public long getCx() {
        return this.cx;
    }
    
    public void setCx(final long value) {
        this.cx = value;
    }
    
    public boolean isSetCx() {
        return true;
    }
    
    public long getCy() {
        return this.cy;
    }
    
    public void setCy(final long value) {
        this.cy = value;
    }
    
    public boolean isSetCy() {
        return true;
    }
}
