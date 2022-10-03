package org.apache.commons.math3.geometry.euclidean.oned;

import org.apache.commons.math3.exception.MathParseException;
import java.text.ParsePosition;
import java.text.FieldPosition;
import org.apache.commons.math3.geometry.Vector;
import java.util.Locale;
import java.text.NumberFormat;
import org.apache.commons.math3.util.CompositeFormat;
import org.apache.commons.math3.geometry.VectorFormat;

public class Vector1DFormat extends VectorFormat<Euclidean1D>
{
    public Vector1DFormat() {
        super("{", "}", "; ", CompositeFormat.getDefaultNumberFormat());
    }
    
    public Vector1DFormat(final NumberFormat format) {
        super("{", "}", "; ", format);
    }
    
    public Vector1DFormat(final String prefix, final String suffix) {
        super(prefix, suffix, "; ", CompositeFormat.getDefaultNumberFormat());
    }
    
    public Vector1DFormat(final String prefix, final String suffix, final NumberFormat format) {
        super(prefix, suffix, "; ", format);
    }
    
    public static Vector1DFormat getInstance() {
        return getInstance(Locale.getDefault());
    }
    
    public static Vector1DFormat getInstance(final Locale locale) {
        return new Vector1DFormat(CompositeFormat.getDefaultNumberFormat(locale));
    }
    
    @Override
    public StringBuffer format(final Vector<Euclidean1D> vector, final StringBuffer toAppendTo, final FieldPosition pos) {
        final Vector1D p1 = (Vector1D)vector;
        return this.format(toAppendTo, pos, p1.getX());
    }
    
    @Override
    public Vector1D parse(final String source) throws MathParseException {
        final ParsePosition parsePosition = new ParsePosition(0);
        final Vector1D result = this.parse(source, parsePosition);
        if (parsePosition.getIndex() == 0) {
            throw new MathParseException(source, parsePosition.getErrorIndex(), Vector1D.class);
        }
        return result;
    }
    
    @Override
    public Vector1D parse(final String source, final ParsePosition pos) {
        final double[] coordinates = this.parseCoordinates(1, source, pos);
        if (coordinates == null) {
            return null;
        }
        return new Vector1D(coordinates[0]);
    }
}
