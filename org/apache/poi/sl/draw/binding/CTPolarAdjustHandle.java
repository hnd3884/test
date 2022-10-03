package org.apache.poi.sl.draw.binding;

import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CT_PolarAdjustHandle", propOrder = { "pos" })
public class CTPolarAdjustHandle
{
    @XmlElement(required = true)
    protected CTAdjPoint2D pos;
    @XmlAttribute(name = "gdRefR")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String gdRefR;
    @XmlAttribute(name = "minR")
    protected String minR;
    @XmlAttribute(name = "maxR")
    protected String maxR;
    @XmlAttribute(name = "gdRefAng")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String gdRefAng;
    @XmlAttribute(name = "minAng")
    protected String minAng;
    @XmlAttribute(name = "maxAng")
    protected String maxAng;
    
    public CTAdjPoint2D getPos() {
        return this.pos;
    }
    
    public void setPos(final CTAdjPoint2D value) {
        this.pos = value;
    }
    
    public boolean isSetPos() {
        return this.pos != null;
    }
    
    public String getGdRefR() {
        return this.gdRefR;
    }
    
    public void setGdRefR(final String value) {
        this.gdRefR = value;
    }
    
    public boolean isSetGdRefR() {
        return this.gdRefR != null;
    }
    
    public String getMinR() {
        return this.minR;
    }
    
    public void setMinR(final String value) {
        this.minR = value;
    }
    
    public boolean isSetMinR() {
        return this.minR != null;
    }
    
    public String getMaxR() {
        return this.maxR;
    }
    
    public void setMaxR(final String value) {
        this.maxR = value;
    }
    
    public boolean isSetMaxR() {
        return this.maxR != null;
    }
    
    public String getGdRefAng() {
        return this.gdRefAng;
    }
    
    public void setGdRefAng(final String value) {
        this.gdRefAng = value;
    }
    
    public boolean isSetGdRefAng() {
        return this.gdRefAng != null;
    }
    
    public String getMinAng() {
        return this.minAng;
    }
    
    public void setMinAng(final String value) {
        this.minAng = value;
    }
    
    public boolean isSetMinAng() {
        return this.minAng != null;
    }
    
    public String getMaxAng() {
        return this.maxAng;
    }
    
    public void setMaxAng(final String value) {
        this.maxAng = value;
    }
    
    public boolean isSetMaxAng() {
        return this.maxAng != null;
    }
}
