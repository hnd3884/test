package org.apache.commons.math3.fraction;

import java.text.ParseException;
import org.apache.commons.math3.exception.MathParseException;
import java.text.ParsePosition;
import org.apache.commons.math3.exception.util.Localizable;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import java.text.FieldPosition;
import java.util.Locale;
import java.text.NumberFormat;

public class FractionFormat extends AbstractFormat
{
    private static final long serialVersionUID = 3008655719530972611L;
    
    public FractionFormat() {
    }
    
    public FractionFormat(final NumberFormat format) {
        super(format);
    }
    
    public FractionFormat(final NumberFormat numeratorFormat, final NumberFormat denominatorFormat) {
        super(numeratorFormat, denominatorFormat);
    }
    
    public static Locale[] getAvailableLocales() {
        return NumberFormat.getAvailableLocales();
    }
    
    public static String formatFraction(final Fraction f) {
        return getImproperInstance().format(f);
    }
    
    public static FractionFormat getImproperInstance() {
        return getImproperInstance(Locale.getDefault());
    }
    
    public static FractionFormat getImproperInstance(final Locale locale) {
        return new FractionFormat(AbstractFormat.getDefaultNumberFormat(locale));
    }
    
    public static FractionFormat getProperInstance() {
        return getProperInstance(Locale.getDefault());
    }
    
    public static FractionFormat getProperInstance(final Locale locale) {
        return new ProperFractionFormat(AbstractFormat.getDefaultNumberFormat(locale));
    }
    
    protected static NumberFormat getDefaultNumberFormat() {
        return AbstractFormat.getDefaultNumberFormat(Locale.getDefault());
    }
    
    public StringBuffer format(final Fraction fraction, final StringBuffer toAppendTo, final FieldPosition pos) {
        pos.setBeginIndex(0);
        pos.setEndIndex(0);
        this.getNumeratorFormat().format(fraction.getNumerator(), toAppendTo, pos);
        toAppendTo.append(" / ");
        this.getDenominatorFormat().format(fraction.getDenominator(), toAppendTo, pos);
        return toAppendTo;
    }
    
    @Override
    public StringBuffer format(final Object obj, final StringBuffer toAppendTo, final FieldPosition pos) throws FractionConversionException, MathIllegalArgumentException {
        StringBuffer ret = null;
        if (obj instanceof Fraction) {
            ret = this.format((Fraction)obj, toAppendTo, pos);
        }
        else {
            if (!(obj instanceof Number)) {
                throw new MathIllegalArgumentException(LocalizedFormats.CANNOT_FORMAT_OBJECT_TO_FRACTION, new Object[0]);
            }
            ret = this.format(new Fraction(((Number)obj).doubleValue()), toAppendTo, pos);
        }
        return ret;
    }
    
    @Override
    public Fraction parse(final String source) throws MathParseException {
        final ParsePosition parsePosition = new ParsePosition(0);
        final Fraction result = this.parse(source, parsePosition);
        if (parsePosition.getIndex() == 0) {
            throw new MathParseException(source, parsePosition.getErrorIndex(), Fraction.class);
        }
        return result;
    }
    
    @Override
    public Fraction parse(final String source, final ParsePosition pos) {
        final int initialIndex = pos.getIndex();
        AbstractFormat.parseAndIgnoreWhitespace(source, pos);
        final Number num = this.getNumeratorFormat().parse(source, pos);
        if (num == null) {
            pos.setIndex(initialIndex);
            return null;
        }
        final int startIndex = pos.getIndex();
        final char c = AbstractFormat.parseNextCharacter(source, pos);
        switch (c) {
            case '\0': {
                return new Fraction(num.intValue(), 1);
            }
            case '/': {
                AbstractFormat.parseAndIgnoreWhitespace(source, pos);
                final Number den = this.getDenominatorFormat().parse(source, pos);
                if (den == null) {
                    pos.setIndex(initialIndex);
                    return null;
                }
                return new Fraction(num.intValue(), den.intValue());
            }
            default: {
                pos.setIndex(initialIndex);
                pos.setErrorIndex(startIndex);
                return null;
            }
        }
    }
}
