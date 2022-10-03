package org.apache.poi.xddf.usermodel;

import java.util.Locale;
import org.apache.xmlbeans.XmlObject;
import org.apache.poi.util.Internal;
import org.openxmlformats.schemas.drawingml.x2006.main.CTColor;
import org.openxmlformats.schemas.drawingml.x2006.main.CTSRgbColor;

public class XDDFColorRgbBinary extends XDDFColor
{
    private CTSRgbColor color;
    
    public XDDFColorRgbBinary(final byte[] color) {
        this(CTSRgbColor.Factory.newInstance(), CTColor.Factory.newInstance());
        this.setValue(color);
    }
    
    @Internal
    protected XDDFColorRgbBinary(final CTSRgbColor color) {
        this(color, null);
    }
    
    @Internal
    protected XDDFColorRgbBinary(final CTSRgbColor color, final CTColor container) {
        super(container);
        this.color = color;
    }
    
    @Internal
    @Override
    protected XmlObject getXmlObject() {
        return (XmlObject)this.color;
    }
    
    public byte[] getValue() {
        return this.color.getVal();
    }
    
    public void setValue(final byte[] value) {
        this.color.setVal(value);
    }
    
    public String toRGBHex() {
        final StringBuilder sb = new StringBuilder(6);
        for (final byte b : this.color.getVal()) {
            sb.append(String.format(Locale.ROOT, "%02X", b));
        }
        return sb.toString().toUpperCase(Locale.ROOT);
    }
}
