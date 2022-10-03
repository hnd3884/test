package org.apache.poi.sl.draw.geom;

import java.awt.geom.Path2D;
import java.util.Iterator;
import org.apache.poi.sl.draw.binding.CTPath2DClose;
import org.apache.poi.sl.draw.binding.CTPath2DCubicBezierTo;
import org.apache.poi.sl.draw.binding.CTAdjPoint2D;
import org.apache.poi.sl.draw.binding.CTPath2DQuadBezierTo;
import org.apache.poi.sl.draw.binding.CTPath2DArcTo;
import org.apache.poi.sl.draw.binding.CTPath2DLineTo;
import org.apache.poi.sl.draw.binding.CTPath2DMoveTo;
import org.apache.poi.sl.draw.binding.CTPath2D;
import java.util.ArrayList;
import org.apache.poi.sl.usermodel.PaintStyle;
import java.util.List;

public class Path
{
    private final List<PathCommand> commands;
    PaintStyle.PaintModifier _fill;
    boolean _stroke;
    long _w;
    long _h;
    
    public Path() {
        this(true, true);
    }
    
    public Path(final boolean fill, final boolean stroke) {
        this.commands = new ArrayList<PathCommand>();
        this._w = -1L;
        this._h = -1L;
        this._fill = (fill ? PaintStyle.PaintModifier.NORM : PaintStyle.PaintModifier.NONE);
        this._stroke = stroke;
    }
    
    public Path(final CTPath2D spPath) {
        switch (spPath.getFill()) {
            case NONE: {
                this._fill = PaintStyle.PaintModifier.NONE;
                break;
            }
            case DARKEN: {
                this._fill = PaintStyle.PaintModifier.DARKEN;
                break;
            }
            case DARKEN_LESS: {
                this._fill = PaintStyle.PaintModifier.DARKEN_LESS;
                break;
            }
            case LIGHTEN: {
                this._fill = PaintStyle.PaintModifier.LIGHTEN;
                break;
            }
            case LIGHTEN_LESS: {
                this._fill = PaintStyle.PaintModifier.LIGHTEN_LESS;
                break;
            }
            default: {
                this._fill = PaintStyle.PaintModifier.NORM;
                break;
            }
        }
        this._stroke = spPath.isStroke();
        this._w = (spPath.isSetW() ? spPath.getW() : -1L);
        this._h = (spPath.isSetH() ? spPath.getH() : -1L);
        this.commands = new ArrayList<PathCommand>();
        for (final Object ch : spPath.getCloseOrMoveToOrLnTo()) {
            if (ch instanceof CTPath2DMoveTo) {
                final CTAdjPoint2D pt = ((CTPath2DMoveTo)ch).getPt();
                this.commands.add(new MoveToCommand(pt));
            }
            else if (ch instanceof CTPath2DLineTo) {
                final CTAdjPoint2D pt = ((CTPath2DLineTo)ch).getPt();
                this.commands.add(new LineToCommand(pt));
            }
            else if (ch instanceof CTPath2DArcTo) {
                final CTPath2DArcTo arc = (CTPath2DArcTo)ch;
                this.commands.add(new ArcToCommand(arc));
            }
            else if (ch instanceof CTPath2DQuadBezierTo) {
                final CTPath2DQuadBezierTo bez = (CTPath2DQuadBezierTo)ch;
                final CTAdjPoint2D pt2 = bez.getPt().get(0);
                final CTAdjPoint2D pt3 = bez.getPt().get(1);
                this.commands.add(new QuadToCommand(pt2, pt3));
            }
            else if (ch instanceof CTPath2DCubicBezierTo) {
                final CTPath2DCubicBezierTo bez2 = (CTPath2DCubicBezierTo)ch;
                final CTAdjPoint2D pt2 = bez2.getPt().get(0);
                final CTAdjPoint2D pt3 = bez2.getPt().get(1);
                final CTAdjPoint2D pt4 = bez2.getPt().get(2);
                this.commands.add(new CurveToCommand(pt2, pt3, pt4));
            }
            else {
                if (!(ch instanceof CTPath2DClose)) {
                    throw new IllegalStateException("Unsupported path segment: " + ch);
                }
                this.commands.add(new ClosePathCommand());
            }
        }
    }
    
    public void addCommand(final PathCommand cmd) {
        this.commands.add(cmd);
    }
    
    public Path2D.Double getPath(final Context ctx) {
        final Path2D.Double path = new Path2D.Double();
        for (final PathCommand cmd : this.commands) {
            cmd.execute(path, ctx);
        }
        return path;
    }
    
    public boolean isStroked() {
        return this._stroke;
    }
    
    public boolean isFilled() {
        return this._fill != PaintStyle.PaintModifier.NONE;
    }
    
    public PaintStyle.PaintModifier getFill() {
        return this._fill;
    }
    
    public long getW() {
        return this._w;
    }
    
    public long getH() {
        return this._h;
    }
}
