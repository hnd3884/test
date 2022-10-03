package org.apache.poi.sl.draw.binding;

import java.util.ArrayList;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import java.util.List;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CT_Path2D", propOrder = { "closeOrMoveToOrLnTo" })
public class CTPath2D
{
    @XmlElements({ @XmlElement(name = "close", type = CTPath2DClose.class), @XmlElement(name = "moveTo", type = CTPath2DMoveTo.class), @XmlElement(name = "lnTo", type = CTPath2DLineTo.class), @XmlElement(name = "arcTo", type = CTPath2DArcTo.class), @XmlElement(name = "quadBezTo", type = CTPath2DQuadBezierTo.class), @XmlElement(name = "cubicBezTo", type = CTPath2DCubicBezierTo.class) })
    protected List<Object> closeOrMoveToOrLnTo;
    @XmlAttribute(name = "w")
    protected Long w;
    @XmlAttribute(name = "h")
    protected Long h;
    @XmlAttribute(name = "fill")
    protected STPathFillMode fill;
    @XmlAttribute(name = "stroke")
    protected Boolean stroke;
    @XmlAttribute(name = "extrusionOk")
    protected Boolean extrusionOk;
    
    public List<Object> getCloseOrMoveToOrLnTo() {
        if (this.closeOrMoveToOrLnTo == null) {
            this.closeOrMoveToOrLnTo = new ArrayList<Object>();
        }
        return this.closeOrMoveToOrLnTo;
    }
    
    public boolean isSetCloseOrMoveToOrLnTo() {
        return this.closeOrMoveToOrLnTo != null && !this.closeOrMoveToOrLnTo.isEmpty();
    }
    
    public void unsetCloseOrMoveToOrLnTo() {
        this.closeOrMoveToOrLnTo = null;
    }
    
    public long getW() {
        if (this.w == null) {
            return 0L;
        }
        return this.w;
    }
    
    public void setW(final long value) {
        this.w = value;
    }
    
    public boolean isSetW() {
        return this.w != null;
    }
    
    public void unsetW() {
        this.w = null;
    }
    
    public long getH() {
        if (this.h == null) {
            return 0L;
        }
        return this.h;
    }
    
    public void setH(final long value) {
        this.h = value;
    }
    
    public boolean isSetH() {
        return this.h != null;
    }
    
    public void unsetH() {
        this.h = null;
    }
    
    public STPathFillMode getFill() {
        if (this.fill == null) {
            return STPathFillMode.NORM;
        }
        return this.fill;
    }
    
    public void setFill(final STPathFillMode value) {
        this.fill = value;
    }
    
    public boolean isSetFill() {
        return this.fill != null;
    }
    
    public boolean isStroke() {
        return this.stroke == null || this.stroke;
    }
    
    public void setStroke(final boolean value) {
        this.stroke = value;
    }
    
    public boolean isSetStroke() {
        return this.stroke != null;
    }
    
    public void unsetStroke() {
        this.stroke = null;
    }
    
    public boolean isExtrusionOk() {
        return this.extrusionOk == null || this.extrusionOk;
    }
    
    public void setExtrusionOk(final boolean value) {
        this.extrusionOk = value;
    }
    
    public boolean isSetExtrusionOk() {
        return this.extrusionOk != null;
    }
    
    public void unsetExtrusionOk() {
        this.extrusionOk = null;
    }
}
