package org.apache.poi.sl.draw.binding;

import java.util.ArrayList;
import javax.xml.bind.annotation.XmlElement;
import java.util.List;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CT_Path2DCubicBezierTo", propOrder = { "pt" })
public class CTPath2DCubicBezierTo
{
    @XmlElement(required = true)
    protected List<CTAdjPoint2D> pt;
    
    public List<CTAdjPoint2D> getPt() {
        if (this.pt == null) {
            this.pt = new ArrayList<CTAdjPoint2D>();
        }
        return this.pt;
    }
    
    public boolean isSetPt() {
        return this.pt != null && !this.pt.isEmpty();
    }
    
    public void unsetPt() {
        this.pt = null;
    }
}
