package org.apache.poi.sl.draw.binding;

import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CT_GeomGuide")
public class CTGeomGuide
{
    @XmlAttribute(name = "name", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String name;
    @XmlAttribute(name = "fmla", required = true)
    protected String fmla;
    
    public String getName() {
        return this.name;
    }
    
    public void setName(final String value) {
        this.name = value;
    }
    
    public boolean isSetName() {
        return this.name != null;
    }
    
    public String getFmla() {
        return this.fmla;
    }
    
    public void setFmla(final String value) {
        this.fmla = value;
    }
    
    public boolean isSetFmla() {
        return this.fmla != null;
    }
}
