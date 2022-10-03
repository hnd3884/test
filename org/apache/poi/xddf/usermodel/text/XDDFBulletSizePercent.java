package org.apache.poi.xddf.usermodel.text;

import org.apache.poi.util.Internal;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextBulletSizePercent;

public class XDDFBulletSizePercent implements XDDFBulletSize
{
    private CTTextBulletSizePercent percent;
    private Double scale;
    
    public XDDFBulletSizePercent(final double value) {
        this(CTTextBulletSizePercent.Factory.newInstance(), null);
        this.setPercent(value);
    }
    
    @Internal
    protected XDDFBulletSizePercent(final CTTextBulletSizePercent percent, final Double scale) {
        this.percent = percent;
        this.scale = ((scale == null) ? 0.001 : (scale * 0.001));
    }
    
    @Internal
    protected CTTextBulletSizePercent getXmlObject() {
        return this.percent;
    }
    
    public double getPercent() {
        return this.percent.getVal() * this.scale;
    }
    
    public void setPercent(final double value) {
        this.percent.setVal(Math.toIntExact(Math.round(1000.0 * value)));
    }
}
