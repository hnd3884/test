package org.apache.poi.xddf.usermodel.text;

import org.apache.poi.util.Internal;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextSpacing;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextSpacingPercent;

public class XDDFSpacingPercent extends XDDFSpacing
{
    private CTTextSpacingPercent percent;
    private Double scale;
    
    public XDDFSpacingPercent(final double value) {
        this(CTTextSpacing.Factory.newInstance(), CTTextSpacingPercent.Factory.newInstance(), null);
        if (this.spacing.isSetSpcPts()) {
            this.spacing.unsetSpcPts();
        }
        this.spacing.setSpcPct(this.percent);
        this.setPercent(value);
    }
    
    @Internal
    protected XDDFSpacingPercent(final CTTextSpacing parent, final CTTextSpacingPercent percent, final Double scale) {
        super(parent);
        this.percent = percent;
        this.scale = ((scale == null) ? 0.001 : (scale * 0.001));
    }
    
    @Override
    public Kind getType() {
        return Kind.PERCENT;
    }
    
    public double getPercent() {
        return this.percent.getVal() * this.scale;
    }
    
    public void setPercent(final double value) {
        this.percent.setVal((int)(1000.0 * value));
    }
}
