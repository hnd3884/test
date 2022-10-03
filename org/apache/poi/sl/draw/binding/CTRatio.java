package org.apache.poi.sl.draw.binding;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CT_Ratio")
public class CTRatio
{
    @XmlAttribute(name = "n", required = true)
    protected long n;
    @XmlAttribute(name = "d", required = true)
    protected long d;
    
    public long getN() {
        return this.n;
    }
    
    public void setN(final long value) {
        this.n = value;
    }
    
    public boolean isSetN() {
        return true;
    }
    
    public long getD() {
        return this.d;
    }
    
    public void setD(final long value) {
        this.d = value;
    }
    
    public boolean isSetD() {
        return true;
    }
}
