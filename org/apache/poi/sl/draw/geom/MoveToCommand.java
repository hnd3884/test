package org.apache.poi.sl.draw.geom;

import java.awt.geom.Path2D;
import org.apache.poi.sl.draw.binding.CTAdjPoint2D;

public class MoveToCommand implements PathCommand
{
    private String arg1;
    private String arg2;
    
    MoveToCommand(final CTAdjPoint2D pt) {
        this.arg1 = pt.getX();
        this.arg2 = pt.getY();
    }
    
    MoveToCommand(final String s1, final String s2) {
        this.arg1 = s1;
        this.arg2 = s2;
    }
    
    @Override
    public void execute(final Path2D.Double path, final Context ctx) {
        final double x = ctx.getValue(this.arg1);
        final double y = ctx.getValue(this.arg2);
        path.moveTo(x, y);
    }
}
