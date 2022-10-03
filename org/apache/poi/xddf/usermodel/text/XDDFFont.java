package org.apache.poi.xddf.usermodel.text;

import org.apache.poi.util.Internal;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextFont;
import org.apache.poi.common.usermodel.fonts.FontGroup;

public class XDDFFont
{
    private FontGroup group;
    private CTTextFont font;
    
    public static XDDFFont unsetFontForGroup(final FontGroup group) {
        return new XDDFFont(group, null);
    }
    
    public XDDFFont(final FontGroup group, final String typeface, final Byte charset, final Byte pitch, final byte[] panose) {
        this(group, CTTextFont.Factory.newInstance());
        if (typeface == null) {
            if (this.font.isSetTypeface()) {
                this.font.unsetTypeface();
            }
        }
        else {
            this.font.setTypeface(typeface);
        }
        if (charset == null) {
            if (this.font.isSetCharset()) {
                this.font.unsetCharset();
            }
        }
        else {
            this.font.setCharset((byte)charset);
        }
        if (pitch == null) {
            if (this.font.isSetPitchFamily()) {
                this.font.unsetPitchFamily();
            }
        }
        else {
            this.font.setPitchFamily((byte)pitch);
        }
        if (panose == null || panose.length == 0) {
            if (this.font.isSetPanose()) {
                this.font.unsetPanose();
            }
        }
        else {
            this.font.setPanose(panose);
        }
    }
    
    @Internal
    protected XDDFFont(final FontGroup group, final CTTextFont font) {
        this.group = group;
        this.font = font;
    }
    
    @Internal
    protected CTTextFont getXmlObject() {
        return this.font;
    }
    
    public FontGroup getGroup() {
        return this.group;
    }
    
    public String getTypeface() {
        if (this.font.isSetTypeface()) {
            return this.font.getTypeface();
        }
        return null;
    }
    
    public Byte getCharset() {
        if (this.font.isSetCharset()) {
            return this.font.getCharset();
        }
        return null;
    }
    
    public Byte getPitchFamily() {
        if (this.font.isSetPitchFamily()) {
            return this.font.getPitchFamily();
        }
        return null;
    }
    
    public byte[] getPanose() {
        if (this.font.isSetPanose()) {
            return this.font.getPanose();
        }
        return null;
    }
}
