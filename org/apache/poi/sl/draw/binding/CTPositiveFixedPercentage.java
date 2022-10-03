package org.apache.poi.sl.draw.binding;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CT_PositiveFixedPercentage")
public class CTPositiveFixedPercentage
{
    @XmlAttribute(name = "val", required = true)
    protected int val;
    
    public int getVal() {
        return this.val;
    }
    
    public void setVal(final int value) {
        this.val = value;
    }
    
    public boolean isSetVal() {
        return true;
    }
}
