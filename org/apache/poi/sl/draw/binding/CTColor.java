package org.apache.poi.sl.draw.binding;

import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CT_Color", propOrder = { "scrgbClr", "srgbClr", "hslClr", "sysClr", "schemeClr", "prstClr" })
public class CTColor
{
    protected CTScRgbColor scrgbClr;
    protected CTSRgbColor srgbClr;
    protected CTHslColor hslClr;
    protected CTSystemColor sysClr;
    protected CTSchemeColor schemeClr;
    protected CTPresetColor prstClr;
    
    public CTScRgbColor getScrgbClr() {
        return this.scrgbClr;
    }
    
    public void setScrgbClr(final CTScRgbColor value) {
        this.scrgbClr = value;
    }
    
    public boolean isSetScrgbClr() {
        return this.scrgbClr != null;
    }
    
    public CTSRgbColor getSrgbClr() {
        return this.srgbClr;
    }
    
    public void setSrgbClr(final CTSRgbColor value) {
        this.srgbClr = value;
    }
    
    public boolean isSetSrgbClr() {
        return this.srgbClr != null;
    }
    
    public CTHslColor getHslClr() {
        return this.hslClr;
    }
    
    public void setHslClr(final CTHslColor value) {
        this.hslClr = value;
    }
    
    public boolean isSetHslClr() {
        return this.hslClr != null;
    }
    
    public CTSystemColor getSysClr() {
        return this.sysClr;
    }
    
    public void setSysClr(final CTSystemColor value) {
        this.sysClr = value;
    }
    
    public boolean isSetSysClr() {
        return this.sysClr != null;
    }
    
    public CTSchemeColor getSchemeClr() {
        return this.schemeClr;
    }
    
    public void setSchemeClr(final CTSchemeColor value) {
        this.schemeClr = value;
    }
    
    public boolean isSetSchemeClr() {
        return this.schemeClr != null;
    }
    
    public CTPresetColor getPrstClr() {
        return this.prstClr;
    }
    
    public void setPrstClr(final CTPresetColor value) {
        this.prstClr = value;
    }
    
    public boolean isSetPrstClr() {
        return this.prstClr != null;
    }
}
