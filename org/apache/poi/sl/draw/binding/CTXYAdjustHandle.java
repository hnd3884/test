package org.apache.poi.sl.draw.binding;

import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CT_XYAdjustHandle", propOrder = { "pos" })
public class CTXYAdjustHandle
{
    @XmlElement(required = true)
    protected CTAdjPoint2D pos;
    @XmlAttribute(name = "gdRefX")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String gdRefX;
    @XmlAttribute(name = "minX")
    protected String minX;
    @XmlAttribute(name = "maxX")
    protected String maxX;
    @XmlAttribute(name = "gdRefY")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String gdRefY;
    @XmlAttribute(name = "minY")
    protected String minY;
    @XmlAttribute(name = "maxY")
    protected String maxY;
    
    public CTAdjPoint2D getPos() {
        return this.pos;
    }
    
    public void setPos(final CTAdjPoint2D value) {
        this.pos = value;
    }
    
    public boolean isSetPos() {
        return this.pos != null;
    }
    
    public String getGdRefX() {
        return this.gdRefX;
    }
    
    public void setGdRefX(final String value) {
        this.gdRefX = value;
    }
    
    public boolean isSetGdRefX() {
        return this.gdRefX != null;
    }
    
    public String getMinX() {
        return this.minX;
    }
    
    public void setMinX(final String value) {
        this.minX = value;
    }
    
    public boolean isSetMinX() {
        return this.minX != null;
    }
    
    public String getMaxX() {
        return this.maxX;
    }
    
    public void setMaxX(final String value) {
        this.maxX = value;
    }
    
    public boolean isSetMaxX() {
        return this.maxX != null;
    }
    
    public String getGdRefY() {
        return this.gdRefY;
    }
    
    public void setGdRefY(final String value) {
        this.gdRefY = value;
    }
    
    public boolean isSetGdRefY() {
        return this.gdRefY != null;
    }
    
    public String getMinY() {
        return this.minY;
    }
    
    public void setMinY(final String value) {
        this.minY = value;
    }
    
    public boolean isSetMinY() {
        return this.minY != null;
    }
    
    public String getMaxY() {
        return this.maxY;
    }
    
    public void setMaxY(final String value) {
        this.maxY = value;
    }
    
    public boolean isSetMaxY() {
        return this.maxY != null;
    }
}
