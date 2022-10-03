package org.apache.commons.math3.fraction;

import org.apache.commons.math3.exception.util.Localizable;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.util.MathUtils;
import java.text.ParsePosition;
import org.apache.commons.math3.util.FastMath;
import java.text.FieldPosition;
import java.text.NumberFormat;

public class ProperFractionFormat extends FractionFormat
{
    private static final long serialVersionUID = 760934726031766749L;
    private NumberFormat wholeFormat;
    
    public ProperFractionFormat() {
        this(FractionFormat.getDefaultNumberFormat());
    }
    
    public ProperFractionFormat(final NumberFormat format) {
        this(format, (NumberFormat)format.clone(), (NumberFormat)format.clone());
    }
    
    public ProperFractionFormat(final NumberFormat wholeFormat, final NumberFormat numeratorFormat, final NumberFormat denominatorFormat) {
        super(numeratorFormat, denominatorFormat);
        this.setWholeFormat(wholeFormat);
    }
    
    @Override
    public StringBuffer format(final Fraction fraction, final StringBuffer toAppendTo, final FieldPosition pos) {
        pos.setBeginIndex(0);
        pos.setEndIndex(0);
        int num = fraction.getNumerator();
        final int den = fraction.getDenominator();
        final int whole = num / den;
        num %= den;
        if (whole != 0) {
            this.getWholeFormat().format(whole, toAppendTo, pos);
            toAppendTo.append(' ');
            num = FastMath.abs(num);
        }
        this.getNumeratorFormat().format(num, toAppendTo, pos);
        toAppendTo.append(" / ");
        this.getDenominatorFormat().format(den, toAppendTo, pos);
        return toAppendTo;
    }
    
    public NumberFormat getWholeFormat() {
        return this.wholeFormat;
    }
    
    @Override
    public Fraction parse(final String source, final ParsePosition pos) {
        final Fraction ret = super.parse(source, pos);
        if (ret != null) {
            return ret;
        }
        final int initialIndex = pos.getIndex();
        AbstractFormat.parseAndIgnoreWhitespace(source, pos);
        final Number whole = this.getWholeFormat().parse(source, pos);
        if (whole == null) {
            pos.setIndex(initialIndex);
            return null;
        }
        AbstractFormat.parseAndIgnoreWhitespace(source, pos);
        final Number num = this.getNumeratorFormat().parse(source, pos);
        if (num == null) {
            pos.setIndex(initialIndex);
            return null;
        }
        if (num.intValue() < 0) {
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
                if (den.intValue() < 0) {
                    pos.setIndex(initialIndex);
                    return null;
                }
                final int w = whole.intValue();
                final int n = num.intValue();
                final int d = den.intValue();
                return new Fraction((FastMath.abs(w) * d + n) * MathUtils.copySign(1, w), d);
            }
            default: {
                pos.setIndex(initialIndex);
                pos.setErrorIndex(startIndex);
                return null;
            }
        }
    }
    
    public void setWholeFormat(final NumberFormat format) {
        if (format == null) {
            throw new NullArgumentException(LocalizedFormats.WHOLE_FORMAT, new Object[0]);
        }
        this.wholeFormat = format;
    }
}
