package org.apache.poi.xddf.usermodel;

import java.util.Locale;
import org.apache.xmlbeans.XmlObject;
import org.apache.poi.util.Internal;
import org.openxmlformats.schemas.drawingml.x2006.main.CTColor;
import org.openxmlformats.schemas.drawingml.x2006.main.CTScRgbColor;

public class XDDFColorRgbPercent extends XDDFColor
{
    private CTScRgbColor color;
    
    public XDDFColorRgbPercent(final int red, final int green, final int blue) {
        this(CTScRgbColor.Factory.newInstance(), CTColor.Factory.newInstance());
        this.setRed(red);
        this.setGreen(green);
        this.setBlue(blue);
    }
    
    @Internal
    protected XDDFColorRgbPercent(final CTScRgbColor color) {
        this(color, null);
    }
    
    @Internal
    protected XDDFColorRgbPercent(final CTScRgbColor color, final CTColor container) {
        super(container);
        this.color = color;
    }
    
    @Internal
    @Override
    protected XmlObject getXmlObject() {
        return (XmlObject)this.color;
    }
    
    public int getRed() {
        return this.color.getR();
    }
    
    public void setRed(final int red) {
        this.color.setR(this.normalize(red));
    }
    
    public int getGreen() {
        return this.color.getG();
    }
    
    public void setGreen(final int green) {
        this.color.setG(this.normalize(green));
    }
    
    public int getBlue() {
        return this.color.getB();
    }
    
    public void setBlue(final int blue) {
        this.color.setB(this.normalize(blue));
    }
    
    private int normalize(final int value) {
        if (value < 0) {
            return 0;
        }
        if (100000 < value) {
            return 100000;
        }
        return value;
    }
    
    public String toRGBHex() {
        final StringBuilder sb = new StringBuilder(6);
        this.appendHex(sb, this.color.getR());
        this.appendHex(sb, this.color.getG());
        this.appendHex(sb, this.color.getB());
        return sb.toString().toUpperCase(Locale.ROOT);
    }
    
    private void appendHex(final StringBuilder sb, final int value) {
        final int b = value * 255 / 100000;
        sb.append(String.format(Locale.ROOT, "%02X", b));
    }
}
