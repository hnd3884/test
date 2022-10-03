package org.apache.commons.math3.linear;

import java.util.List;
import java.util.ArrayList;
import org.apache.commons.math3.exception.MathParseException;
import java.text.ParsePosition;
import java.text.FieldPosition;
import java.util.Locale;
import org.apache.commons.math3.util.CompositeFormat;
import java.text.NumberFormat;

public class RealVectorFormat
{
    private static final String DEFAULT_PREFIX = "{";
    private static final String DEFAULT_SUFFIX = "}";
    private static final String DEFAULT_SEPARATOR = "; ";
    private final String prefix;
    private final String suffix;
    private final String separator;
    private final String trimmedPrefix;
    private final String trimmedSuffix;
    private final String trimmedSeparator;
    private final NumberFormat format;
    
    public RealVectorFormat() {
        this("{", "}", "; ", CompositeFormat.getDefaultNumberFormat());
    }
    
    public RealVectorFormat(final NumberFormat format) {
        this("{", "}", "; ", format);
    }
    
    public RealVectorFormat(final String prefix, final String suffix, final String separator) {
        this(prefix, suffix, separator, CompositeFormat.getDefaultNumberFormat());
    }
    
    public RealVectorFormat(final String prefix, final String suffix, final String separator, final NumberFormat format) {
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
    
    public static RealVectorFormat getInstance() {
        return getInstance(Locale.getDefault());
    }
    
    public static RealVectorFormat getInstance(final Locale locale) {
        return new RealVectorFormat(CompositeFormat.getDefaultNumberFormat(locale));
    }
    
    public String format(final RealVector v) {
        return this.format(v, new StringBuffer(), new FieldPosition(0)).toString();
    }
    
    public StringBuffer format(final RealVector vector, final StringBuffer toAppendTo, final FieldPosition pos) {
        pos.setBeginIndex(0);
        pos.setEndIndex(0);
        toAppendTo.append(this.prefix);
        for (int i = 0; i < vector.getDimension(); ++i) {
            if (i > 0) {
                toAppendTo.append(this.separator);
            }
            CompositeFormat.formatDouble(vector.getEntry(i), this.format, toAppendTo, pos);
        }
        toAppendTo.append(this.suffix);
        return toAppendTo;
    }
    
    public ArrayRealVector parse(final String source) {
        final ParsePosition parsePosition = new ParsePosition(0);
        final ArrayRealVector result = this.parse(source, parsePosition);
        if (parsePosition.getIndex() == 0) {
            throw new MathParseException(source, parsePosition.getErrorIndex(), ArrayRealVector.class);
        }
        return result;
    }
    
    public ArrayRealVector parse(final String source, final ParsePosition pos) {
        final int initialIndex = pos.getIndex();
        CompositeFormat.parseAndIgnoreWhitespace(source, pos);
        if (!CompositeFormat.parseFixedstring(source, this.trimmedPrefix, pos)) {
            return null;
        }
        final List<Number> components = new ArrayList<Number>();
        boolean loop = true;
        while (loop) {
            if (!components.isEmpty()) {
                CompositeFormat.parseAndIgnoreWhitespace(source, pos);
                if (!CompositeFormat.parseFixedstring(source, this.trimmedSeparator, pos)) {
                    loop = false;
                }
            }
            if (loop) {
                CompositeFormat.parseAndIgnoreWhitespace(source, pos);
                final Number component = CompositeFormat.parseNumber(source, this.format, pos);
                if (component == null) {
                    pos.setIndex(initialIndex);
                    return null;
                }
                components.add(component);
            }
        }
        CompositeFormat.parseAndIgnoreWhitespace(source, pos);
        if (!CompositeFormat.parseFixedstring(source, this.trimmedSuffix, pos)) {
            return null;
        }
        final double[] data = new double[components.size()];
        for (int i = 0; i < data.length; ++i) {
            data[i] = components.get(i).doubleValue();
        }
        return new ArrayRealVector(data, false);
    }
}
