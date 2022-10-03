package org.apache.poi.xddf.usermodel;

import org.openxmlformats.schemas.drawingml.x2006.main.CTSystemColor;
import org.openxmlformats.schemas.drawingml.x2006.main.CTSRgbColor;
import org.openxmlformats.schemas.drawingml.x2006.main.CTScRgbColor;
import org.openxmlformats.schemas.drawingml.x2006.main.CTSchemeColor;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPresetColor;
import org.openxmlformats.schemas.drawingml.x2006.main.CTHslColor;
import org.apache.poi.util.Internal;
import org.openxmlformats.schemas.drawingml.x2006.main.CTSolidColorFillProperties;

public class XDDFSolidFillProperties implements XDDFFillProperties
{
    private CTSolidColorFillProperties props;
    
    public XDDFSolidFillProperties() {
        this(CTSolidColorFillProperties.Factory.newInstance());
    }
    
    public XDDFSolidFillProperties(final XDDFColor color) {
        this(CTSolidColorFillProperties.Factory.newInstance());
        this.setColor(color);
    }
    
    @Internal
    public XDDFSolidFillProperties(final CTSolidColorFillProperties properties) {
        this.props = properties;
    }
    
    @Internal
    public CTSolidColorFillProperties getXmlObject() {
        return this.props;
    }
    
    public XDDFColor getColor() {
        if (this.props.isSetHslClr()) {
            return new XDDFColorHsl(this.props.getHslClr());
        }
        if (this.props.isSetPrstClr()) {
            return new XDDFColorPreset(this.props.getPrstClr());
        }
        if (this.props.isSetSchemeClr()) {
            return new XDDFColorSchemeBased(this.props.getSchemeClr());
        }
        if (this.props.isSetScrgbClr()) {
            return new XDDFColorRgbPercent(this.props.getScrgbClr());
        }
        if (this.props.isSetSrgbClr()) {
            return new XDDFColorRgbBinary(this.props.getSrgbClr());
        }
        if (this.props.isSetSysClr()) {
            return new XDDFColorSystemDefined(this.props.getSysClr());
        }
        return null;
    }
    
    public void setColor(final XDDFColor color) {
        if (this.props.isSetHslClr()) {
            this.props.unsetHslClr();
        }
        if (this.props.isSetPrstClr()) {
            this.props.unsetPrstClr();
        }
        if (this.props.isSetSchemeClr()) {
            this.props.unsetSchemeClr();
        }
        if (this.props.isSetScrgbClr()) {
            this.props.unsetScrgbClr();
        }
        if (this.props.isSetSrgbClr()) {
            this.props.unsetSrgbClr();
        }
        if (this.props.isSetSysClr()) {
            this.props.unsetSysClr();
        }
        if (color == null) {
            return;
        }
        if (color instanceof XDDFColorHsl) {
            this.props.setHslClr((CTHslColor)color.getXmlObject());
        }
        else if (color instanceof XDDFColorPreset) {
            this.props.setPrstClr((CTPresetColor)color.getXmlObject());
        }
        else if (color instanceof XDDFColorSchemeBased) {
            this.props.setSchemeClr((CTSchemeColor)color.getXmlObject());
        }
        else if (color instanceof XDDFColorRgbPercent) {
            this.props.setScrgbClr((CTScRgbColor)color.getXmlObject());
        }
        else if (color instanceof XDDFColorRgbBinary) {
            this.props.setSrgbClr((CTSRgbColor)color.getXmlObject());
        }
        else if (color instanceof XDDFColorSystemDefined) {
            this.props.setSysClr((CTSystemColor)color.getXmlObject());
        }
    }
}
