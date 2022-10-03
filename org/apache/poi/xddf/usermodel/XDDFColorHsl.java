package org.apache.poi.xddf.usermodel;

import org.apache.xmlbeans.XmlObject;
import org.apache.poi.util.Internal;
import org.openxmlformats.schemas.drawingml.x2006.main.CTColor;
import org.openxmlformats.schemas.drawingml.x2006.main.CTHslColor;

public class XDDFColorHsl extends XDDFColor
{
    private CTHslColor color;
    
    public XDDFColorHsl(final int hue, final int saturation, final int luminance) {
        this(CTHslColor.Factory.newInstance(), CTColor.Factory.newInstance());
        this.setHue(hue);
        this.setSaturation(saturation);
        this.setLuminance(luminance);
    }
    
    @Internal
    protected XDDFColorHsl(final CTHslColor color) {
        this(color, null);
    }
    
    @Internal
    protected XDDFColorHsl(final CTHslColor color, final CTColor container) {
        super(container);
        this.color = color;
    }
    
    @Internal
    @Override
    protected XmlObject getXmlObject() {
        return (XmlObject)this.color;
    }
    
    public int getHue() {
        return this.color.getHue2();
    }
    
    public void setHue(final int hue) {
        this.color.setHue2(hue);
    }
    
    public int getSaturation() {
        return this.color.getSat2();
    }
    
    public void setSaturation(final int saturation) {
        this.color.setSat2(saturation);
    }
    
    public int getLuminance() {
        return this.color.getLum2();
    }
    
    public void setLuminance(final int lightness) {
        this.color.setLum2(lightness);
    }
}
