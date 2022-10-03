package org.jfree.util;

import java.awt.GradientPaint;
import java.awt.Paint;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.awt.Color;

public class PaintUtilities
{
    static /* synthetic */ Class class$java$awt$Color;
    
    private PaintUtilities() {
    }
    
    static /* synthetic */ Class class$(final String class$) {
        try {
            return Class.forName(class$);
        }
        catch (final ClassNotFoundException forName) {
            throw new NoClassDefFoundError(forName.getMessage());
        }
    }
    
    public static String colorToString(final Color c) {
        try {
            final Field[] fields = ((PaintUtilities.class$java$awt$Color != null) ? PaintUtilities.class$java$awt$Color : (PaintUtilities.class$java$awt$Color = class$("java.awt.Color"))).getFields();
            for (int i = 0; i < fields.length; ++i) {
                final Field f = fields[i];
                if (Modifier.isPublic(f.getModifiers()) && Modifier.isFinal(f.getModifiers()) && Modifier.isStatic(f.getModifiers())) {
                    final String name = f.getName();
                    final Object oColor = f.get(null);
                    if (oColor instanceof Color && c.equals(oColor)) {
                        return name;
                    }
                }
            }
        }
        catch (final Exception ex) {}
        final String color = Integer.toHexString(c.getRGB() & 0xFFFFFF);
        final StringBuffer retval = new StringBuffer(7);
        retval.append("#");
        for (int fillUp = 6 - color.length(), j = 0; j < fillUp; ++j) {
            retval.append("0");
        }
        retval.append(color);
        return retval.toString();
    }
    
    public static boolean equal(final Paint p1, final Paint p2) {
        if (p1 == null) {
            return p2 == null;
        }
        if (p2 == null) {
            return false;
        }
        boolean result = false;
        if (p1 instanceof GradientPaint && p2 instanceof GradientPaint) {
            final GradientPaint gp1 = (GradientPaint)p1;
            final GradientPaint gp2 = (GradientPaint)p2;
            result = (gp1.getColor1().equals(gp2.getColor1()) && gp1.getColor2().equals(gp2.getColor2()) && gp1.getPoint1().equals(gp2.getPoint1()) && gp1.getPoint2().equals(gp2.getPoint2()) && gp1.isCyclic() == gp2.isCyclic() && gp1.getTransparency() == gp1.getTransparency());
        }
        else {
            result = p1.equals(p2);
        }
        return result;
    }
    
    public static Color stringToColor(final String value) {
        if (value == null) {
            return Color.black;
        }
        try {
            return Color.decode(value);
        }
        catch (final NumberFormatException ex) {
            try {
                final Field f = ((PaintUtilities.class$java$awt$Color != null) ? PaintUtilities.class$java$awt$Color : (PaintUtilities.class$java$awt$Color = class$("java.awt.Color"))).getField(value);
                return (Color)f.get(null);
            }
            catch (final Exception ex2) {
                Log.info("No such Color : " + value);
                return Color.black;
            }
        }
    }
}
