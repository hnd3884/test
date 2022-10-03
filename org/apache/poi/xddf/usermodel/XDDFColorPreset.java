package org.apache.poi.xddf.usermodel;

import org.apache.xmlbeans.XmlObject;
import org.apache.poi.util.Internal;
import org.openxmlformats.schemas.drawingml.x2006.main.CTColor;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPresetColor;

public class XDDFColorPreset extends XDDFColor
{
    private CTPresetColor color;
    
    public XDDFColorPreset(final PresetColor color) {
        this(CTPresetColor.Factory.newInstance(), CTColor.Factory.newInstance());
        this.setValue(color);
    }
    
    @Internal
    protected XDDFColorPreset(final CTPresetColor color) {
        this(color, null);
    }
    
    @Internal
    protected XDDFColorPreset(final CTPresetColor color, final CTColor container) {
        super(container);
        this.color = color;
    }
    
    @Internal
    @Override
    protected XmlObject getXmlObject() {
        return (XmlObject)this.color;
    }
    
    public PresetColor getValue() {
        if (this.color.isSetVal()) {
            return PresetColor.valueOf(this.color.getVal());
        }
        return null;
    }
    
    public void setValue(final PresetColor value) {
        if (value == null) {
            if (this.color.isSetVal()) {
                this.color.unsetVal();
            }
        }
        else {
            this.color.setVal(value.underlying);
        }
    }
}
