package org.apache.commons.math3.geometry;

import java.text.ParsePosition;
import org.apache.commons.math3.exception.MathParseException;
import java.text.FieldPosition;
import java.util.Locale;
import org.apache.commons.math3.util.CompositeFormat;
import java.text.NumberFormat;

public abstract class VectorFormat<S extends Space>
{
    public static final String DEFAULT_PREFIX = "{";
    public static final String DEFAULT_SUFFIX = "}";
    public static final String DEFAULT_SEPARATOR = "; ";
    private final String prefix;
    private final String suffix;
    private final String separator;
    private final String trimmedPrefix;
    private final String trimmedSuffix;
    private final String trimmedSeparator;
    private final NumberFormat format;
    
    protected VectorFormat() {
        this("{", "}", "; ", CompositeFormat.getDefaultNumberFormat());
    }
    
    protected VectorFormat(final NumberFormat format) {
        this("{", "}", "; ", format);
    }
    
    protected VectorFormat(final String prefix, final String suffix, final String separator) {
        this(prefix, suffix, separator, CompositeFormat.getDefaultNumberFormat());
    }
    
    protected VectorFormat(final String prefix, final String suffix, final String separator, final NumberFormat format) {
        this.prefix = prefix;
        this.suffix = suffix;
        this.separator = separator;
        this.trimmedPrefix = prefix.trim();
        this.trimmedSuffix = suffix.trim();
        this.trimmedSeparator = separator.trim();
        this.format = format;
    }
    
    public static Locale[] getAvailableLocales() {
        return NumberFormat.getAvailableLocales();
    }
    
    public String getPrefix() {
        return this.prefix;
    }
    
    public String getSuffix() {
        return this.suffix;
    }
    
    public String getSeparator() {
        return this.separator;
    }
    
    public NumberFormat getFormat() {
        return this.format;
    }
    
    public String format(final Vector<S> vector) {
        return this.format(vector, new StringBuffer(), new FieldPosition(0)).toString();
    }
    
    public abstract StringBuffer format(final Vector<S> p0, final StringBuffer p1, final FieldPosition p2);
    
    protected StringBuffer format(final StringBuffer toAppendTo, final FieldPosition pos, final double... coordinates) {
        pos.setBeginIndex(0);
        pos.setEndIndex(0);
        toAppendTo.append(this.prefix);
        for (int i = 0; i < coordinates.length; ++i) {
            if (i > 0) {
                toAppendTo.append(this.separator);
            }
            CompositeFormat.formatDouble(coordinates[i], this.format, toAppendTo, pos);
        }
        toAppendTo.append(this.suffix);
        return toAppendTo;
    }
    
    public abstract Vector<S> parse(final String p0) throws MathParseException;
    
    public abstract Vector<S> parse(final String p0, final ParsePosition p1);
    
    protected double[] parseCoordinates(final int dimension, final String source, final ParsePosition pos) {
        final int initialIndex = pos.getIndex();
        final double[] coordinates = new double[dimension];
        CompositeFormat.parseAndIgnoreWhitespace(source, pos);
        if (!CompositeFormat.parseFixedstring(source, this.trimmedPrefix, pos)) {
            return null;
        }
        for (int i = 0; i < dimension; ++i) {
            CompositeFormat.parseAndIgnoreWhitespace(source, pos);
            if (i > 0 && !CompositeFormat.parseFixedstring(source, this.trimmedSeparator, pos)) {
                return null;
            }
            CompositeFormat.parseAndIgnoreWhitespace(source, pos);
            final Number c = CompositeFormat.parseNumber(source, this.format, pos);
            if (c == null) {
                pos.setIndex(initialIndex);
                return null;
            }
            coordinates[i] = c.doubleValue();
        }
        CompositeFormat.parseAndIgnoreWhitespace(source, pos);
        if (!CompositeFormat.parseFixedstring(source, this.trimmedSuffix, pos)) {
            return null;
        }
        return coordinates;
    }
}
