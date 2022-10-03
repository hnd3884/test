package org.apache.commons.math3.complex;

import org.apache.commons.math3.exception.MathParseException;
import java.text.ParsePosition;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import java.text.FieldPosition;
import java.util.Locale;
import org.apache.commons.math3.exception.NoDataException;
import org.apache.commons.math3.exception.util.Localizable;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.util.CompositeFormat;
import java.text.NumberFormat;

public class ComplexFormat
{
    private static final String DEFAULT_IMAGINARY_CHARACTER = "i";
    private final String imaginaryCharacter;
    private final NumberFormat imaginaryFormat;
    private final NumberFormat realFormat;
    
    public ComplexFormat() {
        this.imaginaryCharacter = "i";
        this.imaginaryFormat = CompositeFormat.getDefaultNumberFormat();
        this.realFormat = this.imaginaryFormat;
    }
    
    public ComplexFormat(final NumberFormat format) throws NullArgumentException {
        if (format == null) {
            throw new NullArgumentException(LocalizedFormats.IMAGINARY_FORMAT, new Object[0]);
        }
        this.imaginaryCharacter = "i";
        this.imaginaryFormat = format;
        this.realFormat = format;
    }
    
    public ComplexFormat(final NumberFormat realFormat, final NumberFormat imaginaryFormat) throws NullArgumentException {
        if (imaginaryFormat == null) {
            throw new NullArgumentException(LocalizedFormats.IMAGINARY_FORMAT, new Object[0]);
        }
        if (realFormat == null) {
            throw new NullArgumentException(LocalizedFormats.REAL_FORMAT, new Object[0]);
        }
        this.imaginaryCharacter = "i";
        this.imaginaryFormat = imaginaryFormat;
        this.realFormat = realFormat;
    }
    
    public ComplexFormat(final String imaginaryCharacter) throws NullArgumentException, NoDataException {
        this(imaginaryCharacter, CompositeFormat.getDefaultNumberFormat());
    }
    
    public ComplexFormat(final String imaginaryCharacter, final NumberFormat format) throws NullArgumentException, NoDataException {
        this(imaginaryCharacter, format, format);
    }
    
    public ComplexFormat(final String imaginaryCharacter, final NumberFormat realFormat, final NumberFormat imaginaryFormat) throws NullArgumentException, NoDataException {
        if (imaginaryCharacter == null) {
            throw new NullArgumentException();
        }
        if (imaginaryCharacter.length() == 0) {
            throw new NoDataException();
        }
        if (imaginaryFormat == null) {
            throw new NullArgumentException(LocalizedFormats.IMAGINARY_FORMAT, new Object[0]);
        }
        if (realFormat == null) {
            throw new NullArgumentException(LocalizedFormats.REAL_FORMAT, new Object[0]);
        }
        this.imaginaryCharacter = imaginaryCharacter;
        this.imaginaryFormat = imaginaryFormat;
        this.realFormat = realFormat;
    }
    
    public static Locale[] getAvailableLocales() {
        return NumberFormat.getAvailableLocales();
    }
    
    public String format(final Complex c) {
        return this.format(c, new StringBuffer(), new FieldPosition(0)).toString();
    }
    
    public String format(final Double c) {
        return this.format(new Complex(c, 0.0), new StringBuffer(), new FieldPosition(0)).toString();
    }
    
    public StringBuffer format(final Complex complex, final StringBuffer toAppendTo, final FieldPosition pos) {
        pos.setBeginIndex(0);
        pos.setEndIndex(0);
        final double re = complex.getReal();
        CompositeFormat.formatDouble(re, this.getRealFormat(), toAppendTo, pos);
        final double im = complex.getImaginary();
        if (im < 0.0) {
            toAppendTo.append(" - ");
            final StringBuffer imAppendTo = this.formatImaginary(-im, new StringBuffer(), pos);
            toAppendTo.append(imAppendTo);
            toAppendTo.append(this.getImaginaryCharacter());
        }
        else if (im > 0.0 || Double.isNaN(im)) {
            toAppendTo.append(" + ");
            final StringBuffer imAppendTo = this.formatImaginary(im, new StringBuffer(), pos);
            toAppendTo.append(imAppendTo);
            toAppendTo.append(this.getImaginaryCharacter());
        }
        return toAppendTo;
    }
    
    private StringBuffer formatImaginary(final double absIm, final StringBuffer toAppendTo, final FieldPosition pos) {
        pos.setBeginIndex(0);
        pos.setEndIndex(0);
        CompositeFormat.formatDouble(absIm, this.getImaginaryFormat(), toAppendTo, pos);
        if (toAppendTo.toString().equals("1")) {
            toAppendTo.setLength(0);
        }
        return toAppendTo;
    }
    
    public StringBuffer format(final Object obj, final StringBuffer toAppendTo, final FieldPosition pos) throws MathIllegalArgumentException {
        StringBuffer ret = null;
        if (obj instanceof Complex) {
            ret = this.format((Complex)obj, toAppendTo, pos);
        }
        else {
            if (!(obj instanceof Number)) {
                throw new MathIllegalArgumentException(LocalizedFormats.CANNOT_FORMAT_INSTANCE_AS_COMPLEX, new Object[] { obj.getClass().getName() });
            }
            ret = this.format(new Complex(((Number)obj).doubleValue(), 0.0), toAppendTo, pos);
        }
        return ret;
    }
    
    public String getImaginaryCharacter() {
        return this.imaginaryCharacter;
    }
    
    public NumberFormat getImaginaryFormat() {
        return this.imaginaryFormat;
    }
    
    public static ComplexFormat getInstance() {
        return getInstance(Locale.getDefault());
    }
    
    public static ComplexFormat getInstance(final Locale locale) {
        final NumberFormat f = CompositeFormat.getDefaultNumberFormat(locale);
        return new ComplexFormat(f);
    }
    
    public static ComplexFormat getInstance(final String imaginaryCharacter, final Locale locale) throws NullArgumentException, NoDataException {
        final NumberFormat f = CompositeFormat.getDefaultNumberFormat(locale);
        return new ComplexFormat(imaginaryCharacter, f);
    }
    
    public NumberFormat getRealFormat() {
        return this.realFormat;
    }
    
    public Complex parse(final String source) throws MathParseException {
        final ParsePosition parsePosition = new ParsePosition(0);
        final Complex result = this.parse(source, parsePosition);
        if (parsePosition.getIndex() == 0) {
            throw new MathParseException(source, parsePosition.getErrorIndex(), Complex.class);
        }
        return result;
    }
    
    public Complex parse(final String source, final ParsePosition pos) {
        final int initialIndex = pos.getIndex();
        CompositeFormat.parseAndIgnoreWhitespace(source, pos);
        final Number re = CompositeFormat.parseNumber(source, this.getRealFormat(), pos);
        if (re == null) {
            pos.setIndex(initialIndex);
            return null;
        }
        final int startIndex = pos.getIndex();
        final char c = CompositeFormat.parseNextCharacter(source, pos);
        int sign = 0;
        switch (c) {
            case '\0': {
                return new Complex(re.doubleValue(), 0.0);
            }
            case '-': {
                sign = -1;
                break;
            }
            case '+': {
                sign = 1;
                break;
            }
            default: {
                pos.setIndex(initialIndex);
                pos.setErrorIndex(startIndex);
                return null;
            }
        }
        CompositeFormat.parseAndIgnoreWhitespace(source, pos);
        final Number im = CompositeFormat.parseNumber(source, this.getRealFormat(), pos);
        if (im == null) {
            pos.setIndex(initialIndex);
            return null;
        }
        if (!CompositeFormat.parseFixedstring(source, this.getImaginaryCharacter(), pos)) {
            return null;
        }
        return new Complex(re.doubleValue(), im.doubleValue() * sign);
    }
}
