package org.apache.poi.sl.draw.geom;

import org.apache.poi.util.Internal;
import java.awt.geom.Point2D;
import java.awt.Shape;
import java.awt.geom.Arc2D;
import java.awt.geom.Path2D;
import org.apache.poi.sl.draw.binding.CTPath2DArcTo;

public class ArcToCommand implements PathCommand
{
    private String hr;
    private String wr;
    private String stAng;
    private String swAng;
    
    ArcToCommand(final CTPath2DArcTo arc) {
        this.hr = arc.getHR();
        this.wr = arc.getWR();
        this.stAng = arc.getStAng();
        this.swAng = arc.getSwAng();
    }
    
    @Override
    public void execute(final Path2D.Double path, final Context ctx) {
        final double rx = ctx.getValue(this.wr);
        final double ry = ctx.getValue(this.hr);
        final double ooStart = ctx.getValue(this.stAng) / 60000.0;
        final double ooExtent = ctx.getValue(this.swAng) / 60000.0;
        final double awtStart = convertOoxml2AwtAngle(ooStart, rx, ry);
        final double awtSweep = convertOoxml2AwtAngle(ooStart + ooExtent, rx, ry) - awtStart;
        final double radStart = Math.toRadians(ooStart);
        final double invStart = Math.atan2(rx * Math.sin(radStart), ry * Math.cos(radStart));
        final Point2D pt = path.getCurrentPoint();
        final double x0 = pt.getX() - rx * Math.cos(invStart) - rx;
        final double y0 = pt.getY() - ry * Math.sin(invStart) - ry;
        final Arc2D arc = new Arc2D.Double(x0, y0, 2.0 * rx, 2.0 * ry, awtStart, awtSweep, 0);
        path.append(arc, true);
    }
    
    @Internal
    public static double convertOoxml2AwtAngle(final double ooAngle, final double width, final double height) {
        final double aspect = height / width;
        double awtAngle = -ooAngle;
        double awtAngle2 = awtAngle % 360.0;
        double awtAngle3 = awtAngle - awtAngle2;
        switch ((int)(awtAngle2 / 90.0)) {
            case -3: {
                awtAngle3 -= 360.0;
                awtAngle2 += 360.0;
                break;
            }
            case -2:
            case -1: {
                awtAngle3 -= 180.0;
                awtAngle2 += 180.0;
                break;
            }
            case 1:
            case 2: {
                awtAngle3 += 180.0;
                awtAngle2 -= 180.0;
                break;
            }
            case 3: {
                awtAngle3 += 360.0;
                awtAngle2 -= 360.0;
                break;
            }
        }
        awtAngle = Math.toDegrees(Math.atan2(Math.tan(Math.toRadians(awtAngle2)), aspect)) + awtAngle3;
        return awtAngle;
    }
}
