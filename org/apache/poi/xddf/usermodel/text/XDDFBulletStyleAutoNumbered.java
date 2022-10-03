package org.apache.poi.xddf.usermodel.text;

import org.apache.poi.util.Internal;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextAutonumberBullet;

public class XDDFBulletStyleAutoNumbered implements XDDFBulletStyle
{
    private CTTextAutonumberBullet style;
    
    @Internal
    protected XDDFBulletStyleAutoNumbered(final CTTextAutonumberBullet style) {
        this.style = style;
    }
    
    @Internal
    protected CTTextAutonumberBullet getXmlObject() {
        return this.style;
    }
    
    public AutonumberScheme getType() {
        return AutonumberScheme.valueOf(this.style.getType());
    }
    
    public void setType(final AutonumberScheme scheme) {
        this.style.setType(scheme.underlying);
    }
    
    public int getStartAt() {
        if (this.style.isSetStartAt()) {
            return this.style.getStartAt();
        }
        return 1;
    }
    
    public void setStartAt(final Integer value) {
        if (value == null) {
            if (this.style.isSetStartAt()) {
                this.style.unsetStartAt();
            }
        }
        else {
            this.style.setStartAt((int)value);
        }
    }
}
