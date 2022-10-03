package org.apache.poi.sl.draw;

import org.apache.poi.sl.draw.geom.CustomGeometry;
import org.apache.poi.sl.draw.geom.IAdjustableShape;
import org.apache.poi.sl.draw.geom.Context;
import org.apache.poi.util.Units;
import org.apache.poi.sl.usermodel.Shadow;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.AffineTransform;
import org.apache.poi.sl.usermodel.LineDecoration;
import java.util.List;
import java.util.ArrayList;
import org.apache.poi.sl.draw.geom.Path;
import java.util.Iterator;
import org.apache.poi.sl.usermodel.PaintStyle;
import java.util.function.Consumer;
import java.util.Collection;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Paint;
import org.apache.poi.sl.draw.geom.Outline;
import java.awt.RenderingHints;
import java.awt.geom.Path2D;
import java.awt.Stroke;
import org.apache.poi.sl.usermodel.PlaceableShape;
import java.awt.Graphics2D;
import org.apache.poi.sl.usermodel.Shape;
import org.apache.poi.sl.usermodel.SimpleShape;

public class DrawSimpleShape extends DrawShape
{
    private static final double DECO_SIZE_POW = 1.5;
    
    public DrawSimpleShape(final SimpleShape<?, ?> shape) {
        super(shape);
    }
    
    @Override
    public void draw(final Graphics2D graphics) {
        if (DrawShape.getAnchor(graphics, this.getShape()) == null) {
            return;
        }
        final Paint oldPaint = graphics.getPaint();
        final Stroke oldStroke = graphics.getStroke();
        final Color oldColor = graphics.getColor();
        final Paint fill = this.getFillPaint(graphics);
        final Paint line = this.getLinePaint(graphics);
        final BasicStroke stroke = this.getStroke();
        graphics.setStroke(stroke);
        final Collection<Outline> elems = this.computeOutlines(graphics);
        this.drawShadow(graphics, elems, fill, line);
        if (fill != null) {
            final Path2D area = new Path2D.Double();
            graphics.setRenderingHint(Drawable.GRADIENT_SHAPE, area);
            PaintStyle.PaintModifier pm = null;
            final Consumer<PaintStyle.PaintModifier> fun = pm -> this.fillArea(graphics, pm, area);
            pm = null;
            for (final Outline o : elems) {
                final Path path = o.getPath();
                if (path.isFilled()) {
                    final PaintStyle.PaintModifier pmOld = pm;
                    pm = path.getFill();
                    if (pmOld != null && pmOld != pm) {
                        fun.accept(pmOld);
                        area.reset();
                    }
                    else {
                        area.append(o.getOutline(), false);
                    }
                }
            }
            if (area.getCurrentPoint() != null) {
                fun.accept(pm);
            }
        }
        this.drawContent(graphics);
        if (line != null) {
            graphics.setPaint(line);
            graphics.setStroke(stroke);
            for (final Outline o2 : elems) {
                if (o2.getPath().isStroked()) {
                    final java.awt.Shape s = o2.getOutline();
                    graphics.setRenderingHint(Drawable.GRADIENT_SHAPE, s);
                    graphics.draw(s);
                }
            }
        }
        this.drawDecoration(graphics, line, stroke);
        graphics.setColor(oldColor);
        graphics.setPaint(oldPaint);
        graphics.setStroke(oldStroke);
    }
    
    private void fillArea(final Graphics2D graphics, final PaintStyle.PaintModifier pm, final Path2D area) {
        final SimpleShape<?, ?> ss = this.getShape();
        final PaintStyle ps = ss.getFillStyle().getPaint();
        final DrawPaint drawPaint = DrawFactory.getInstance(graphics).getPaint(ss);
        final Paint fillMod = drawPaint.getPaint(graphics, ps, pm);
        if (fillMod != null) {
            graphics.setPaint(fillMod);
            DrawPaint.fillPaintWorkaround(graphics, area);
        }
    }
    
    protected Paint getFillPaint(final Graphics2D graphics) {
        final PaintStyle ps = this.getShape().getFillStyle().getPaint();
        final DrawPaint drawPaint = DrawFactory.getInstance(graphics).getPaint(this.getShape());
        return drawPaint.getPaint(graphics, ps);
    }
    
    protected Paint getLinePaint(final Graphics2D graphics) {
        final PaintStyle ps = this.getShape().getFillStyle().getPaint();
        final DrawPaint drawPaint = DrawFactory.getInstance(graphics).getPaint(this.getShape());
        return drawPaint.getPaint(graphics, this.getShape().getStrokeStyle().getPaint());
    }
    
    protected void drawDecoration(final Graphics2D graphics, final Paint line, final BasicStroke stroke) {
        if (line == null) {
            return;
        }
        graphics.setPaint(line);
        final List<Outline> lst = new ArrayList<Outline>();
        final LineDecoration deco = this.getShape().getLineDecoration();
        final Outline head = this.getHeadDecoration(graphics, deco, stroke);
        if (head != null) {
            lst.add(head);
        }
        final Outline tail = this.getTailDecoration(graphics, deco, stroke);
        if (tail != null) {
            lst.add(tail);
        }
        for (final Outline o : lst) {
            final java.awt.Shape s = o.getOutline();
            final Path p = o.getPath();
            graphics.setRenderingHint(Drawable.GRADIENT_SHAPE, s);
            if (p.isFilled()) {
                graphics.fill(s);
            }
            if (p.isStroked()) {
                graphics.draw(s);
            }
        }
    }
    
    protected Outline getTailDecoration(final Graphics2D graphics, final LineDecoration deco, final BasicStroke stroke) {
        if (deco == null || stroke == null) {
            return null;
        }
        LineDecoration.DecorationSize tailLength = deco.getTailLength();
        if (tailLength == null) {
            tailLength = LineDecoration.DecorationSize.MEDIUM;
        }
        LineDecoration.DecorationSize tailWidth = deco.getTailWidth();
        if (tailWidth == null) {
            tailWidth = LineDecoration.DecorationSize.MEDIUM;
        }
        final double lineWidth = Math.max(2.5, stroke.getLineWidth());
        final Rectangle2D anchor = DrawShape.getAnchor(graphics, this.getShape());
        final double x2 = anchor.getX() + anchor.getWidth();
        final double y2 = anchor.getY() + anchor.getHeight();
        final double alpha = Math.atan(anchor.getHeight() / anchor.getWidth());
        final AffineTransform at = new AffineTransform();
        java.awt.Shape tailShape = null;
        Path p = null;
        final double scaleY = Math.pow(1.5, tailWidth.ordinal() + 1.0);
        final double scaleX = Math.pow(1.5, tailLength.ordinal() + 1.0);
        final LineDecoration.DecorationShape tailShapeEnum = deco.getTailShape();
        if (tailShapeEnum == null) {
            return null;
        }
        switch (tailShapeEnum) {
            case OVAL: {
                p = new Path();
                tailShape = new Ellipse2D.Double(0.0, 0.0, lineWidth * scaleX, lineWidth * scaleY);
                final Rectangle2D bounds = tailShape.getBounds2D();
                at.translate(x2 - bounds.getWidth() / 2.0, y2 - bounds.getHeight() / 2.0);
                at.rotate(alpha, bounds.getX() + bounds.getWidth() / 2.0, bounds.getY() + bounds.getHeight() / 2.0);
                break;
            }
            case STEALTH:
            case ARROW: {
                p = new Path(false, true);
                final Path2D.Double arrow = new Path2D.Double();
                arrow.moveTo(-lineWidth * scaleX, -lineWidth * scaleY / 2.0);
                arrow.lineTo(0.0, 0.0);
                arrow.lineTo(-lineWidth * scaleX, lineWidth * scaleY / 2.0);
                tailShape = arrow;
                at.translate(x2, y2);
                at.rotate(alpha);
                break;
            }
            case TRIANGLE: {
                p = new Path();
                final Path2D.Double triangle = new Path2D.Double();
                triangle.moveTo(-lineWidth * scaleX, -lineWidth * scaleY / 2.0);
                triangle.lineTo(0.0, 0.0);
                triangle.lineTo(-lineWidth * scaleX, lineWidth * scaleY / 2.0);
                triangle.closePath();
                tailShape = triangle;
                at.translate(x2, y2);
                at.rotate(alpha);
                break;
            }
        }
        if (tailShape != null) {
            tailShape = at.createTransformedShape(tailShape);
        }
        return (tailShape == null) ? null : new Outline(tailShape, p);
    }
    
    protected Outline getHeadDecoration(final Graphics2D graphics, final LineDecoration deco, final BasicStroke stroke) {
        if (deco == null || stroke == null) {
            return null;
        }
        LineDecoration.DecorationSize headLength = deco.getHeadLength();
        if (headLength == null) {
            headLength = LineDecoration.DecorationSize.MEDIUM;
        }
        LineDecoration.DecorationSize headWidth = deco.getHeadWidth();
        if (headWidth == null) {
            headWidth = LineDecoration.DecorationSize.MEDIUM;
        }
        final double lineWidth = Math.max(2.5, stroke.getLineWidth());
        final Rectangle2D anchor = DrawShape.getAnchor(graphics, this.getShape());
        final double x1 = anchor.getX();
        final double y1 = anchor.getY();
        final double alpha = Math.atan(anchor.getHeight() / anchor.getWidth());
        final AffineTransform at = new AffineTransform();
        java.awt.Shape headShape = null;
        Path p = null;
        final double scaleY = Math.pow(1.5, headWidth.ordinal() + 1.0);
        final double scaleX = Math.pow(1.5, headLength.ordinal() + 1.0);
        final LineDecoration.DecorationShape headShapeEnum = deco.getHeadShape();
        if (headShapeEnum == null) {
            return null;
        }
        switch (headShapeEnum) {
            case OVAL: {
                p = new Path();
                headShape = new Ellipse2D.Double(0.0, 0.0, lineWidth * scaleX, lineWidth * scaleY);
                final Rectangle2D bounds = headShape.getBounds2D();
                at.translate(x1 - bounds.getWidth() / 2.0, y1 - bounds.getHeight() / 2.0);
                at.rotate(alpha, bounds.getX() + bounds.getWidth() / 2.0, bounds.getY() + bounds.getHeight() / 2.0);
                break;
            }
            case STEALTH:
            case ARROW: {
                p = new Path(false, true);
                final Path2D.Double arrow = new Path2D.Double();
                arrow.moveTo(lineWidth * scaleX, -lineWidth * scaleY / 2.0);
                arrow.lineTo(0.0, 0.0);
                arrow.lineTo(lineWidth * scaleX, lineWidth * scaleY / 2.0);
                headShape = arrow;
                at.translate(x1, y1);
                at.rotate(alpha);
                break;
            }
            case TRIANGLE: {
                p = new Path();
                final Path2D.Double triangle = new Path2D.Double();
                triangle.moveTo(lineWidth * scaleX, -lineWidth * scaleY / 2.0);
                triangle.lineTo(0.0, 0.0);
                triangle.lineTo(lineWidth * scaleX, lineWidth * scaleY / 2.0);
                triangle.closePath();
                headShape = triangle;
                at.translate(x1, y1);
                at.rotate(alpha);
                break;
            }
        }
        if (headShape != null) {
            headShape = at.createTransformedShape(headShape);
        }
        return (headShape == null) ? null : new Outline(headShape, p);
    }
    
    public BasicStroke getStroke() {
        return DrawShape.getStroke(this.getShape().getStrokeStyle());
    }
    
    protected void drawShadow(final Graphics2D graphics, final Collection<Outline> outlines, final Paint fill, final Paint line) {
        final Shadow<?, ?> shadow = this.getShape().getShadow();
        if (shadow == null || (fill == null && line == null)) {
            return;
        }
        final PaintStyle.SolidPaint shadowPaint = shadow.getFillStyle();
        final Color shadowColor = DrawPaint.applyColorTransform(shadowPaint.getSolidColor());
        double shapeRotation = this.getShape().getRotation();
        if (this.getShape().getFlipVertical()) {
            shapeRotation += 180.0;
        }
        final double angle = shadow.getAngle() - shapeRotation;
        final double dist = shadow.getDistance();
        final double dx = dist * Math.cos(Math.toRadians(angle));
        final double dy = dist * Math.sin(Math.toRadians(angle));
        graphics.translate(dx, dy);
        for (final Outline o : outlines) {
            final java.awt.Shape s = o.getOutline();
            final Path p = o.getPath();
            graphics.setRenderingHint(Drawable.GRADIENT_SHAPE, s);
            graphics.setPaint(shadowColor);
            if (fill != null && p.isFilled()) {
                DrawPaint.fillPaintWorkaround(graphics, s);
            }
            else {
                if (line == null || !p.isStroked()) {
                    continue;
                }
                graphics.draw(s);
            }
        }
        graphics.translate(-dx, -dy);
    }
    
    protected Collection<Outline> computeOutlines(final Graphics2D graphics) {
        final SimpleShape<?, ?> sh = this.getShape();
        final List<Outline> lst = new ArrayList<Outline>();
        final CustomGeometry geom = sh.getGeometry();
        if (geom == null) {
            return lst;
        }
        final Rectangle2D anchor = DrawShape.getAnchor(graphics, sh);
        if (anchor == null) {
            return lst;
        }
        for (final Path p : geom) {
            double w = (double)p.getW();
            double h = (double)p.getH();
            double scaleX;
            if (w == -1.0) {
                w = Units.toEMU(anchor.getWidth());
                scaleX = Units.toPoints(1L);
            }
            else if (anchor.getWidth() == 0.0) {
                scaleX = 1.0;
            }
            else {
                scaleX = anchor.getWidth() / w;
            }
            double scaleY;
            if (h == -1.0) {
                h = Units.toEMU(anchor.getHeight());
                scaleY = Units.toPoints(1L);
            }
            else if (anchor.getHeight() == 0.0) {
                scaleY = 1.0;
            }
            else {
                scaleY = anchor.getHeight() / h;
            }
            final Rectangle2D pathAnchor = new Rectangle2D.Double(0.0, 0.0, w, h);
            final Context ctx = new Context(geom, pathAnchor, sh);
            final java.awt.Shape gp = p.getPath(ctx);
            final AffineTransform at = new AffineTransform();
            at.translate(anchor.getX(), anchor.getY());
            at.scale(scaleX, scaleY);
            final java.awt.Shape canvasShape = at.createTransformedShape(gp);
            lst.add(new Outline(canvasShape, p));
        }
        return lst;
    }
    
    @Override
    protected SimpleShape<?, ?> getShape() {
        return (SimpleShape)this.shape;
    }
}
