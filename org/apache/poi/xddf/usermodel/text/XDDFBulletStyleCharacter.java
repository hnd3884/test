package org.apache.poi.xddf.usermodel.text;

import org.apache.poi.util.Internal;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextCharBullet;

public class XDDFBulletStyleCharacter implements XDDFBulletStyle
{
    private CTTextCharBullet style;
    
    @Internal
    protected XDDFBulletStyleCharacter(final CTTextCharBullet style) {
        this.style = style;
    }
    
    @Internal
    protected CTTextCharBullet getXmlObject() {
        return this.style;
    }
    
    public String getCharacter() {
        return this.style.getChar();
    }
    
    public void setCharacter(final String value) {
        this.style.setChar(value);
    }
}
