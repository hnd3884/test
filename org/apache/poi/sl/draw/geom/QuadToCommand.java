package org.apache.poi.sl.draw.geom;

import java.awt.geom.Path2D;
import org.apache.poi.sl.draw.binding.CTAdjPoint2D;

public class QuadToCommand implements PathCommand
{
    private String arg1;
    private String arg2;
    private String arg3;
    private String arg4;
    
    QuadToCommand(final CTAdjPoint2D pt1, final CTAdjPoint2D pt2) {
        this.arg1 = pt1.getX();
        this.arg2 = pt1.getY();
        this.arg3 = pt2.getX();
        this.arg4 = pt2.getY();
    }
    
    @Override
    public void execute(final Path2D.Double path, final Context ctx) {
        final double x1 = ctx.getValue(this.arg1);
        final double y1 = ctx.getValue(this.arg2);
        final double x2 = ctx.getValue(this.arg3);
        final double y2 = ctx.getValue(this.arg4);
        path.quadTo(x1, y1, x2, y2);
    }
}
