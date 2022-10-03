package org.jfree.io;

import java.awt.geom.PathIterator;
import java.awt.geom.AffineTransform;
import java.io.Serializable;
import java.util.HashMap;
import java.io.ObjectOutputStream;
import java.awt.BasicStroke;
import java.awt.Stroke;
import java.awt.geom.GeneralPath;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Line2D;
import java.awt.Shape;
import java.awt.geom.Point2D;
import java.awt.GradientPaint;
import java.awt.Color;
import java.awt.Paint;
import java.io.IOException;
import java.text.AttributedCharacterIterator;
import java.util.Map;
import java.text.AttributedString;
import java.io.ObjectInputStream;

public class SerialUtilities
{
    static /* synthetic */ Class class$java$io$Serializable;
    static /* synthetic */ Class class$java$awt$GradientPaint;
    static /* synthetic */ Class class$java$awt$BasicStroke;
    static /* synthetic */ Class class$java$awt$geom$Line2D;
    static /* synthetic */ Class class$java$awt$geom$Rectangle2D;
    static /* synthetic */ Class class$java$awt$geom$Ellipse2D;
    static /* synthetic */ Class class$java$awt$geom$Arc2D;
    static /* synthetic */ Class class$java$awt$geom$GeneralPath;
    
    private SerialUtilities() {
    }
    
    static /* synthetic */ Class class$(final String class$) {
        try {
            return Class.forName(class$);
        }
        catch (final ClassNotFoundException forName) {
            throw new NoClassDefFoundError(forName.getMessage());
        }
    }
    
    public static boolean isSerializable(final Class c) {
        return ((SerialUtilities.class$java$io$Serializable != null) ? SerialUtilities.class$java$io$Serializable : (SerialUtilities.class$java$io$Serializable = class$("java.io.Serializable"))).isAssignableFrom(c);
    }
    
    public static AttributedString readAttributedString(final ObjectInputStream stream) throws IOException, ClassNotFoundException {
        if (stream == null) {
            throw new IllegalArgumentException("Null 'stream' argument.");
        }
        AttributedString result = null;
        final boolean isNull = stream.readBoolean();
        if (!isNull) {
            final String plainStr = (String)stream.readObject();
            result = new AttributedString(plainStr);
            char c = stream.readChar();
            int start = 0;
            while (c != '\uffff') {
                final int limit = stream.readInt();
                final Map atts = (Map)stream.readObject();
                result.addAttributes(atts, start, limit);
                start = limit;
                c = stream.readChar();
            }
        }
        return result;
    }
    
    public static Paint readPaint(final ObjectInputStream stream) throws IOException, ClassNotFoundException {
        if (stream == null) {
            throw new IllegalArgumentException("Null 'stream' argument.");
        }
        Paint result = null;
        final boolean isNull = stream.readBoolean();
        if (!isNull) {
            final Class c = (Class)stream.readObject();
            if (isSerializable(c)) {
                result = (Paint)stream.readObject();
            }
            else if (c.equals((SerialUtilities.class$java$awt$GradientPaint != null) ? SerialUtilities.class$java$awt$GradientPaint : (SerialUtilities.class$java$awt$GradientPaint = class$("java.awt.GradientPaint")))) {
                final float x1 = stream.readFloat();
                final float y1 = stream.readFloat();
                final Color c2 = (Color)stream.readObject();
                final float x2 = stream.readFloat();
                final float y2 = stream.readFloat();
                final Color c3 = (Color)stream.readObject();
                final boolean isCyclic = stream.readBoolean();
                result = new GradientPaint(x1, y1, c2, x2, y2, c3, isCyclic);
            }
        }
        return result;
    }
    
    public static Point2D readPoint2D(final ObjectInputStream stream) throws IOException {
        if (stream == null) {
            throw new IllegalArgumentException("Null 'stream' argument.");
        }
        Point2D result = null;
        final boolean isNull = stream.readBoolean();
        if (!isNull) {
            final double x = stream.readDouble();
            final double y = stream.readDouble();
            result = new Point2D.Double(x, y);
        }
        return result;
    }
    
    public static Shape readShape(final ObjectInputStream stream) throws IOException, ClassNotFoundException {
        if (stream == null) {
            throw new IllegalArgumentException("Null 'stream' argument.");
        }
        Shape result = null;
        final boolean isNull = stream.readBoolean();
        if (!isNull) {
            final Class c = (Class)stream.readObject();
            if (c.equals((SerialUtilities.class$java$awt$geom$Line2D != null) ? SerialUtilities.class$java$awt$geom$Line2D : (SerialUtilities.class$java$awt$geom$Line2D = class$("java.awt.geom.Line2D")))) {
                final double x1 = stream.readDouble();
                final double y1 = stream.readDouble();
                final double x2 = stream.readDouble();
                final double y2 = stream.readDouble();
                result = new Line2D.Double(x1, y1, x2, y2);
            }
            else if (c.equals((SerialUtilities.class$java$awt$geom$Rectangle2D != null) ? SerialUtilities.class$java$awt$geom$Rectangle2D : (SerialUtilities.class$java$awt$geom$Rectangle2D = class$("java.awt.geom.Rectangle2D")))) {
                final double x3 = stream.readDouble();
                final double y3 = stream.readDouble();
                final double w = stream.readDouble();
                final double h = stream.readDouble();
                result = new Rectangle2D.Double(x3, y3, w, h);
            }
            else if (c.equals((SerialUtilities.class$java$awt$geom$Ellipse2D != null) ? SerialUtilities.class$java$awt$geom$Ellipse2D : (SerialUtilities.class$java$awt$geom$Ellipse2D = class$("java.awt.geom.Ellipse2D")))) {
                final double x3 = stream.readDouble();
                final double y3 = stream.readDouble();
                final double w = stream.readDouble();
                final double h = stream.readDouble();
                result = new Ellipse2D.Double(x3, y3, w, h);
            }
            else if (c.equals((SerialUtilities.class$java$awt$geom$Arc2D != null) ? SerialUtilities.class$java$awt$geom$Arc2D : (SerialUtilities.class$java$awt$geom$Arc2D = class$("java.awt.geom.Arc2D")))) {
                final double x3 = stream.readDouble();
                final double y3 = stream.readDouble();
                final double w = stream.readDouble();
                final double h = stream.readDouble();
                final double as = stream.readDouble();
                final double ae = stream.readDouble();
                final int at = stream.readInt();
                result = new Arc2D.Double(x3, y3, w, h, as, ae, at);
            }
            else if (c.equals((SerialUtilities.class$java$awt$geom$GeneralPath != null) ? SerialUtilities.class$java$awt$geom$GeneralPath : (SerialUtilities.class$java$awt$geom$GeneralPath = class$("java.awt.geom.GeneralPath")))) {
                final GeneralPath gp = new GeneralPath();
                final float[] args = new float[6];
                boolean hasNext = stream.readBoolean();
            Label_0558_Outer:
                while (!hasNext) {
                    final int type = stream.readInt();
                    for (int i = 0; i < 6; ++i) {
                        args[i] = stream.readFloat();
                    }
                    while (true) {
                        switch (type) {
                            default: {
                                throw new RuntimeException("JFreeChart - No path exists");
                            }
                            case 4: {
                                gp.setWindingRule(stream.readInt());
                                hasNext = stream.readBoolean();
                                continue Label_0558_Outer;
                            }
                            case 0: {
                                gp.moveTo(args[0], args[1]);
                                continue;
                            }
                            case 1: {
                                gp.lineTo(args[0], args[1]);
                                continue;
                            }
                            case 3: {
                                gp.curveTo(args[0], args[1], args[2], args[3], args[4], args[5]);
                                continue;
                            }
                            case 2: {
                                gp.quadTo(args[0], args[1], args[2], args[3]);
                                continue;
                            }
                        }
                        break;
                    }
                }
                result = gp;
            }
            else {
                result = (Shape)stream.readObject();
            }
        }
        return result;
    }
    
    public static Stroke readStroke(final ObjectInputStream stream) throws IOException, ClassNotFoundException {
        if (stream == null) {
            throw new IllegalArgumentException("Null 'stream' argument.");
        }
        Stroke result = null;
        final boolean isNull = stream.readBoolean();
        if (!isNull) {
            final Class c = (Class)stream.readObject();
            if (c.equals((SerialUtilities.class$java$awt$BasicStroke != null) ? SerialUtilities.class$java$awt$BasicStroke : (SerialUtilities.class$java$awt$BasicStroke = class$("java.awt.BasicStroke")))) {
                final float width = stream.readFloat();
                final int cap = stream.readInt();
                final int join = stream.readInt();
                final float miterLimit = stream.readFloat();
                final float[] dash = (float[])stream.readObject();
                final float dashPhase = stream.readFloat();
                result = new BasicStroke(width, cap, join, miterLimit, dash, dashPhase);
            }
            else {
                result = (Stroke)stream.readObject();
            }
        }
        return result;
    }
    
    public static void writeAttributedString(final AttributedString as, final ObjectOutputStream stream) throws IOException {
        if (stream == null) {
            throw new IllegalArgumentException("Null 'stream' argument.");
        }
        if (as != null) {
            stream.writeBoolean(false);
            final AttributedCharacterIterator aci = as.getIterator();
            StringBuffer plainStr = new StringBuffer();
            for (char current = aci.first(); current != '\uffff'; current = aci.next()) {
                plainStr = plainStr.append(current);
            }
            stream.writeObject(plainStr.toString());
            char current = aci.first();
            final int begin = aci.getBeginIndex();
            while (current != '\uffff') {
                stream.writeChar(current);
                final int limit = aci.getRunLimit();
                stream.writeInt(limit - begin);
                final Map atts = new HashMap(aci.getAttributes());
                stream.writeObject(atts);
                current = aci.setIndex(limit);
            }
            stream.writeChar(65535);
        }
        else {
            stream.writeBoolean(true);
        }
    }
    
    public static void writePaint(final Paint paint, final ObjectOutputStream stream) throws IOException {
        if (stream == null) {
            throw new IllegalArgumentException("Null 'stream' argument.");
        }
        if (paint != null) {
            stream.writeBoolean(false);
            stream.writeObject(paint.getClass());
            if (paint instanceof Serializable) {
                stream.writeObject(paint);
            }
            else if (paint instanceof GradientPaint) {
                final GradientPaint gp = (GradientPaint)paint;
                stream.writeFloat((float)gp.getPoint1().getX());
                stream.writeFloat((float)gp.getPoint1().getY());
                stream.writeObject(gp.getColor1());
                stream.writeFloat((float)gp.getPoint2().getX());
                stream.writeFloat((float)gp.getPoint2().getY());
                stream.writeObject(gp.getColor2());
                stream.writeBoolean(gp.isCyclic());
            }
        }
        else {
            stream.writeBoolean(true);
        }
    }
    
    public static void writePoint2D(final Point2D p, final ObjectOutputStream stream) throws IOException {
        if (stream == null) {
            throw new IllegalArgumentException("Null 'stream' argument.");
        }
        if (p != null) {
            stream.writeBoolean(false);
            stream.writeDouble(p.getX());
            stream.writeDouble(p.getY());
        }
        else {
            stream.writeBoolean(true);
        }
    }
    
    public static void writeShape(final Shape shape, final ObjectOutputStream stream) throws IOException {
        if (stream == null) {
            throw new IllegalArgumentException("Null 'stream' argument.");
        }
        if (shape != null) {
            stream.writeBoolean(false);
            if (shape instanceof Line2D) {
                final Line2D line = (Line2D)shape;
                stream.writeObject((SerialUtilities.class$java$awt$geom$Line2D != null) ? SerialUtilities.class$java$awt$geom$Line2D : (SerialUtilities.class$java$awt$geom$Line2D = class$("java.awt.geom.Line2D")));
                stream.writeDouble(line.getX1());
                stream.writeDouble(line.getY1());
                stream.writeDouble(line.getX2());
                stream.writeDouble(line.getY2());
            }
            else if (shape instanceof Rectangle2D) {
                final Rectangle2D rectangle = (Rectangle2D)shape;
                stream.writeObject((SerialUtilities.class$java$awt$geom$Rectangle2D != null) ? SerialUtilities.class$java$awt$geom$Rectangle2D : (SerialUtilities.class$java$awt$geom$Rectangle2D = class$("java.awt.geom.Rectangle2D")));
                stream.writeDouble(rectangle.getX());
                stream.writeDouble(rectangle.getY());
                stream.writeDouble(rectangle.getWidth());
                stream.writeDouble(rectangle.getHeight());
            }
            else if (shape instanceof Ellipse2D) {
                final Ellipse2D ellipse = (Ellipse2D)shape;
                stream.writeObject((SerialUtilities.class$java$awt$geom$Ellipse2D != null) ? SerialUtilities.class$java$awt$geom$Ellipse2D : (SerialUtilities.class$java$awt$geom$Ellipse2D = class$("java.awt.geom.Ellipse2D")));
                stream.writeDouble(ellipse.getX());
                stream.writeDouble(ellipse.getY());
                stream.writeDouble(ellipse.getWidth());
                stream.writeDouble(ellipse.getHeight());
            }
            else if (shape instanceof Arc2D) {
                final Arc2D arc = (Arc2D)shape;
                stream.writeObject((SerialUtilities.class$java$awt$geom$Arc2D != null) ? SerialUtilities.class$java$awt$geom$Arc2D : (SerialUtilities.class$java$awt$geom$Arc2D = class$("java.awt.geom.Arc2D")));
                stream.writeDouble(arc.getX());
                stream.writeDouble(arc.getY());
                stream.writeDouble(arc.getWidth());
                stream.writeDouble(arc.getHeight());
                stream.writeDouble(arc.getAngleStart());
                stream.writeDouble(arc.getAngleExtent());
                stream.writeInt(arc.getArcType());
            }
            else if (shape instanceof GeneralPath) {
                stream.writeObject((SerialUtilities.class$java$awt$geom$GeneralPath != null) ? SerialUtilities.class$java$awt$geom$GeneralPath : (SerialUtilities.class$java$awt$geom$GeneralPath = class$("java.awt.geom.GeneralPath")));
                final PathIterator pi = shape.getPathIterator(null);
                final float[] args = new float[6];
                stream.writeBoolean(pi.isDone());
                while (!pi.isDone()) {
                    final int type = pi.currentSegment(args);
                    stream.writeInt(type);
                    for (int i = 0; i < 6; ++i) {
                        stream.writeFloat(args[i]);
                    }
                    stream.writeInt(pi.getWindingRule());
                    pi.next();
                    stream.writeBoolean(pi.isDone());
                }
            }
            else {
                stream.writeObject(shape.getClass());
                stream.writeObject(shape);
            }
        }
        else {
            stream.writeBoolean(true);
        }
    }
    
    public static void writeStroke(final Stroke stroke, final ObjectOutputStream stream) throws IOException {
        if (stream == null) {
            throw new IllegalArgumentException("Null 'stream' argument.");
        }
        if (stroke != null) {
            stream.writeBoolean(false);
            if (stroke instanceof BasicStroke) {
                final BasicStroke s = (BasicStroke)stroke;
                stream.writeObject((SerialUtilities.class$java$awt$BasicStroke != null) ? SerialUtilities.class$java$awt$BasicStroke : (SerialUtilities.class$java$awt$BasicStroke = class$("java.awt.BasicStroke")));
                stream.writeFloat(s.getLineWidth());
                stream.writeInt(s.getEndCap());
                stream.writeInt(s.getLineJoin());
                stream.writeFloat(s.getMiterLimit());
                stream.writeObject(s.getDashArray());
                stream.writeFloat(s.getDashPhase());
            }
            else {
                stream.writeObject(stroke.getClass());
                stream.writeObject(stroke);
            }
        }
        else {
            stream.writeBoolean(true);
        }
    }
}
