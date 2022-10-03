package org.apache.commons.math3.fraction;

import java.text.FieldPosition;
import java.text.ParsePosition;
import org.apache.commons.math3.exception.util.Localizable;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import java.util.Locale;
import java.io.Serializable;
import java.text.NumberFormat;

public abstract class AbstractFormat extends NumberFormat implements Serializable
{
    private static final long serialVersionUID = -6981118387974191891L;
    private NumberFormat denominatorFormat;
    private NumberFormat numeratorFormat;
    
    protected AbstractFormat() {
        this(getDefaultNumberFormat());
    }
    
    protected AbstractFormat(final NumberFormat format) {
        this(format, (NumberFormat)format.clone());
    }
    
    protected AbstractFormat(final NumberFormat numeratorFormat, final NumberFormat denominatorFormat) {
        this.numeratorFormat = numeratorFormat;
        this.denominatorFormat = denominatorFormat;
    }
    
    protected static NumberFormat getDefaultNumberFormat() {
        return getDefaultNumberFormat(Locale.getDefault());
    }
    
    protected static NumberFormat getDefaultNumberFormat(final Locale locale) {
        final NumberFormat nf = NumberFormat.getNumberInstance(locale);
        nf.setMaximumFractionDigits(0);
        nf.setParseIntegerOnly(true);
        return nf;
    }
    
    public NumberFormat getDenominatorFormat() {
        return this.denominatorFormat;
    }
    
    public NumberFormat getNumeratorFormat() {
        return this.numeratorFormat;
    }
    
    public void setDenominatorFormat(final NumberFormat format) {
        if (format == null) {
            throw new NullArgumentException(LocalizedFormats.DENOMINATOR_FORMAT, new Object[0]);
        }
        this.denominatorFormat = format;
    }
    
    public void setNumeratorFormat(final NumberFormat format) {
        if (format == null) {
            throw new NullArgumentException(LocalizedFormats.NUMERATOR_FORMAT, new Object[0]);
        }
        this.numeratorFormat = format;
    }
    
    protected static void parseAndIgnoreWhitespace(final String source, final ParsePosition pos) {
        parseNextCharacter(source, pos);
        pos.setIndex(pos.getIndex() - 1);
    }
    
    protected static char parseNextCharacter(final String source, final ParsePosition pos) {
        int index = pos.getIndex();
        final int n = source.length();
        char ret = '\0';
        if (index < n) {
            char c;
            do {
                c = source.charAt(index++);
            } while (Character.isWhitespace(c) && index < n);
            pos.setIndex(index);
            if (index < n) {
                ret = c;
            }
        }
        return ret;
    }
    
    @Override
    public StringBuffer format(final double value, final StringBuffer buffer, final FieldPosition position) {
        return this.format((Object)value, buffer, position);
    }
    
    @Override
    public StringBuffer format(final long value, final StringBuffer buffer, final FieldPosition position) {
        return this.format((Object)value, buffer, position);
    }
}
