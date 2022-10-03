package org.apache.poi.sl.draw.binding;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CT_ConnectionSite", propOrder = { "pos" })
public class CTConnectionSite
{
    @XmlElement(required = true)
    protected CTAdjPoint2D pos;
    @XmlAttribute(name = "ang", required = true)
    protected String ang;
    
    public CTAdjPoint2D getPos() {
        return this.pos;
    }
    
    public void setPos(final CTAdjPoint2D value) {
        this.pos = value;
    }
    
    public boolean isSetPos() {
        return this.pos != null;
    }
    
    public String getAng() {
        return this.ang;
    }
    
    public void setAng(final String value) {
        this.ang = value;
    }
    
    public boolean isSetAng() {
        return this.ang != null;
    }
}
