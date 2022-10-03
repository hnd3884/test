package org.apache.commons.math3.geometry.euclidean.twod;

import org.apache.commons.math3.exception.MathParseException;
import java.text.ParsePosition;
import java.text.FieldPosition;
import org.apache.commons.math3.geometry.Vector;
import java.util.Locale;
import java.text.NumberFormat;
import org.apache.commons.math3.util.CompositeFormat;
import org.apache.commons.math3.geometry.VectorFormat;

public class Vector2DFormat extends VectorFormat<Euclidean2D>
{
    public Vector2DFormat() {
        super("{", "}", "; ", CompositeFormat.getDefaultNumberFormat());
    }
    
    public Vector2DFormat(final NumberFormat format) {
        super("{", "}", "; ", format);
    }
    
    public Vector2DFormat(final String prefix, final String suffix, final String separator) {
        super(prefix, suffix, separator, CompositeFormat.getDefaultNumberFormat());
    }
    
    public Vector2DFormat(final String prefix, final String suffix, final String separator, final NumberFormat format) {
        super(prefix, suffix, separator, format);
    }
    
    public static Vector2DFormat getInstance() {
        return getInstance(Locale.getDefault());
    }
    
    public static Vector2DFormat getInstance(final Locale locale) {
        return new Vector2DFormat(CompositeFormat.getDefaultNumberFormat(locale));
    }
    
    @Override
    public StringBuffer format(final Vector<Euclidean2D> vector, final StringBuffer toAppendTo, final FieldPosition pos) {
        final Vector2D p2 = (Vector2D)vector;
        return this.format(toAppendTo, pos, p2.getX(), p2.getY());
    }
    
    @Override
    public Vector2D parse(final String source) throws MathParseException {
        final ParsePosition parsePosition = new ParsePosition(0);
        final Vector2D result = this.parse(source, parsePosition);
        if (parsePosition.getIndex() == 0) {
            throw new MathParseException(source, parsePosition.getErrorIndex(), Vector2D.class);
        }
        return result;
    }
    
    @Override
    public Vector2D parse(final String source, final ParsePosition pos) {
        final double[] coordinates = this.parseCoordinates(2, source, pos);
        if (coordinates == null) {
            return null;
        }
        return new Vector2D(coordinates[0], coordinates[1]);
    }
}
