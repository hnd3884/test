package org.apache.poi.xddf.usermodel;

import org.apache.xmlbeans.XmlObject;
import org.apache.poi.util.Internal;
import org.openxmlformats.schemas.drawingml.x2006.main.CTColor;

public abstract class XDDFColor
{
    protected CTColor container;
    
    @Internal
    protected XDDFColor(final CTColor container) {
        this.container = container;
    }
    
    public static XDDFColor from(final byte[] color) {
        return new XDDFColorRgbBinary(color);
    }
    
    public static XDDFColor from(final int red, final int green, final int blue) {
        return new XDDFColorRgbPercent(red, green, blue);
    }
    
    public static XDDFColor from(final PresetColor color) {
        return new XDDFColorPreset(color);
    }
    
    public static XDDFColor from(final SchemeColor color) {
        return new XDDFColorSchemeBased(color);
    }
    
    public static XDDFColor from(final SystemColor color) {
        return new XDDFColorSystemDefined(color);
    }
    
    @Internal
    public static XDDFColor forColorContainer(final CTColor container) {
        if (container.isSetHslClr()) {
            return new XDDFColorHsl(container.getHslClr(), container);
        }
        if (container.isSetPrstClr()) {
            return new XDDFColorPreset(container.getPrstClr(), container);
        }
        if (container.isSetSchemeClr()) {
            return new XDDFColorSchemeBased(container.getSchemeClr(), container);
        }
        if (container.isSetScrgbClr()) {
            return new XDDFColorRgbPercent(container.getScrgbClr(), container);
        }
        if (container.isSetSrgbClr()) {
            return new XDDFColorRgbBinary(container.getSrgbClr(), container);
        }
        if (container.isSetSysClr()) {
            return new XDDFColorSystemDefined(container.getSysClr(), container);
        }
        return null;
    }
    
    @Internal
    public CTColor getColorContainer() {
        return this.container;
    }
    
    @Internal
    protected abstract XmlObject getXmlObject();
}
