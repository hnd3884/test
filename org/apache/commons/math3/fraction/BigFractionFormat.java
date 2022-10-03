package org.apache.commons.math3.fraction;

import java.text.ParseException;
import org.apache.commons.math3.exception.MathParseException;
import java.text.ParsePosition;
import org.apache.commons.math3.exception.util.Localizable;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import java.math.BigInteger;
import java.text.FieldPosition;
import java.util.Locale;
import java.text.NumberFormat;
import java.io.Serializable;

public class BigFractionFormat extends AbstractFormat implements Serializable
{
    private static final long serialVersionUID = -2932167925527338976L;
    
    public BigFractionFormat() {
    }
    
    public BigFractionFormat(final NumberFormat format) {
        super(format);
    }
    
    public BigFractionFormat(final NumberFormat numeratorFormat, final NumberFormat denominatorFormat) {
        super(numeratorFormat, denominatorFormat);
    }
    
    public static Locale[] getAvailableLocales() {
        return NumberFormat.getAvailableLocales();
    }
    
    public static String formatBigFraction(final BigFraction f) {
        return getImproperInstance().format(f);
    }
    
    public static BigFractionFormat getImproperInstance() {
        return getImproperInstance(Locale.getDefault());
    }
    
    public static BigFractionFormat getImproperInstance(final Locale locale) {
        return new BigFractionFormat(AbstractFormat.getDefaultNumberFormat(locale));
    }
    
    public static BigFractionFormat getProperInstance() {
        return getProperInstance(Locale.getDefault());
    }
    
    public static BigFractionFormat getProperInstance(final Locale locale) {
        return new ProperBigFractionFormat(AbstractFormat.getDefaultNumberFormat(locale));
    }
    
    public StringBuffer format(final BigFraction BigFraction, final StringBuffer toAppendTo, final FieldPosition pos) {
        pos.setBeginIndex(0);
        pos.setEndIndex(0);
        this.getNumeratorFormat().format(BigFraction.getNumerator(), toAppendTo, pos);
        toAppendTo.append(" / ");
        this.getDenominatorFormat().format(BigFraction.getDenominator(), toAppendTo, pos);
        return toAppendTo;
    }
    
    @Override
    public StringBuffer format(final Object obj, final StringBuffer toAppendTo, final FieldPosition pos) {
        StringBuffer ret;
        if (obj instanceof BigFraction) {
            ret = this.format((BigFraction)obj, toAppendTo, pos);
        }
        else if (obj instanceof BigInteger) {
            ret = this.format(new BigFraction((BigInteger)obj), toAppendTo, pos);
        }
        else {
            if (!(obj instanceof Number)) {
                throw new MathIllegalArgumentException(LocalizedFormats.CANNOT_FORMAT_OBJECT_TO_FRACTION, new Object[0]);
            }
            ret = this.format(new BigFraction(((Number)obj).doubleValue()), toAppendTo, pos);
        }
        return ret;
    }
    
    @Override
    public BigFraction parse(final String source) throws MathParseException {
        final ParsePosition parsePosition = new ParsePosition(0);
        final BigFraction result = this.parse(source, parsePosition);
        if (parsePosition.getIndex() == 0) {
            throw new MathParseException(source, parsePosition.getErrorIndex(), BigFraction.class);
        }
        return result;
    }
    
    @Override
    public BigFraction parse(final String source, final ParsePosition pos) {
        final int initialIndex = pos.getIndex();
        AbstractFormat.parseAndIgnoreWhitespace(source, pos);
        final BigInteger num = this.parseNextBigInteger(source, pos);
        if (num == null) {
            pos.setIndex(initialIndex);
            return null;
        }
        final int startIndex = pos.getIndex();
        final char c = AbstractFormat.parseNextCharacter(source, pos);
        switch (c) {
            case '\0': {
                return new BigFraction(num);
            }
            case '/': {
                AbstractFormat.parseAndIgnoreWhitespace(source, pos);
                final BigInteger den = this.parseNextBigInteger(source, pos);
                if (den == null) {
                    pos.setIndex(initialIndex);
                    return null;
                }
                return new BigFraction(num, den);
            }
            default: {
                pos.setIndex(initialIndex);
                pos.setErrorIndex(startIndex);
                return null;
            }
        }
    }
    
    protected BigInteger parseNextBigInteger(final String source, final ParsePosition pos) {
        final int start = pos.getIndex();
        int end;
        for (end = ((source.charAt(start) == '-') ? (start + 1) : start); end < source.length() && Character.isDigit(source.charAt(end)); ++end) {}
        try {
            final BigInteger n = new BigInteger(source.substring(start, end));
            pos.setIndex(end);
            return n;
        }
        catch (final NumberFormatException nfe) {
            pos.setErrorIndex(start);
            return null;
        }
    }
}
