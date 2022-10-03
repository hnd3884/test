package org.apache.poi.xddf.usermodel.text;

import org.apache.poi.util.Internal;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextSpacing;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextSpacingPoint;

public class XDDFSpacingPoints extends XDDFSpacing
{
    private CTTextSpacingPoint points;
    
    public XDDFSpacingPoints(final double value) {
        this(CTTextSpacing.Factory.newInstance(), CTTextSpacingPoint.Factory.newInstance());
        if (this.spacing.isSetSpcPct()) {
            this.spacing.unsetSpcPct();
        }
        this.spacing.setSpcPts(this.points);
        this.setPoints(value);
    }
    
    @Internal
    protected XDDFSpacingPoints(final CTTextSpacing parent, final CTTextSpacingPoint points) {
        super(parent);
        this.points = points;
    }
    
    @Override
    public Kind getType() {
        return Kind.POINTS;
    }
    
    public double getPoints() {
        return this.points.getVal() * 0.01;
    }
    
    public void setPoints(final double value) {
        this.points.setVal((int)(100.0 * value));
    }
}
