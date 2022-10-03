package org.apache.commons.math3.geometry.euclidean.threed;

import org.apache.commons.math3.exception.MathParseException;
import java.text.ParsePosition;
import java.text.FieldPosition;
import org.apache.commons.math3.geometry.Vector;
import java.util.Locale;
import java.text.NumberFormat;
import org.apache.commons.math3.util.CompositeFormat;
import org.apache.commons.math3.geometry.VectorFormat;

public class Vector3DFormat extends VectorFormat<Euclidean3D>
{
    public Vector3DFormat() {
        super("{", "}", "; ", CompositeFormat.getDefaultNumberFormat());
    }
    
    public Vector3DFormat(final NumberFormat format) {
        super("{", "}", "; ", format);
    }
    
    public Vector3DFormat(final String prefix, final String suffix, final String separator) {
        super(prefix, suffix, separator, CompositeFormat.getDefaultNumberFormat());
    }
    
    public Vector3DFormat(final String prefix, final String suffix, final String separator, final NumberFormat format) {
        super(prefix, suffix, separator, format);
    }
    
    public static Vector3DFormat getInstance() {
        return getInstance(Locale.getDefault());
    }
    
    public static Vector3DFormat getInstance(final Locale locale) {
        return new Vector3DFormat(CompositeFormat.getDefaultNumberFormat(locale));
    }
    
    @Override
    public StringBuffer format(final Vector<Euclidean3D> vector, final StringBuffer toAppendTo, final FieldPosition pos) {
        final Vector3D v3 = (Vector3D)vector;
        return this.format(toAppendTo, pos, v3.getX(), v3.getY(), v3.getZ());
    }
    
    @Override
    public Vector3D parse(final String source) throws MathParseException {
        final ParsePosition parsePosition = new ParsePosition(0);
        final Vector3D result = this.parse(source, parsePosition);
        if (parsePosition.getIndex() == 0) {
            throw new MathParseException(source, parsePosition.getErrorIndex(), Vector3D.class);
        }
        return result;
    }
    
    @Override
    public Vector3D parse(final String source, final ParsePosition pos) {
        final double[] coordinates = this.parseCoordinates(3, source, pos);
        if (coordinates == null) {
            return null;
        }
        return new Vector3D(coordinates[0], coordinates[1], coordinates[2]);
    }
}
