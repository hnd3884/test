package org.apache.poi.sl.draw.geom;

import org.apache.poi.sl.draw.binding.CTGeomGuide;

public class AdjustValue extends Guide
{
    public AdjustValue(final CTGeomGuide gd) {
        super(gd.getName(), gd.getFmla());
    }
    
    @Override
    public double evaluate(final Context ctx) {
        final String name = this.getName();
        final Guide adj = ctx.getAdjustValue(name);
        return (adj != null) ? adj.evaluate(ctx) : super.evaluate(ctx);
    }
}
