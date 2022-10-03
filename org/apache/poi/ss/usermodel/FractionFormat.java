package org.apache.poi.ss.usermodel;

import org.apache.poi.util.POILogFactory;
import org.apache.poi.ss.formula.eval.NotImplementedException;
import java.text.ParsePosition;
import java.text.FieldPosition;
import org.apache.poi.ss.format.SimpleFraction;
import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.poi.util.POILogger;
import java.text.Format;

public class FractionFormat extends Format
{
    private static final POILogger LOGGER;
    private static final Pattern DENOM_FORMAT_PATTERN;
    private static final int MAX_DENOM_POW = 4;
    private final int exactDenom;
    private final int maxDenom;
    private final String wholePartFormatString;
    
    public FractionFormat(final String wholePartFormatString, final String denomFormatString) {
        this.wholePartFormatString = wholePartFormatString;
        final Matcher m = FractionFormat.DENOM_FORMAT_PATTERN.matcher(denomFormatString);
        int tmpExact = -1;
        int tmpMax = -1;
        Label_0122: {
            if (m.find()) {
                if (m.group(2) != null) {
                    try {
                        tmpExact = Integer.parseInt(m.group(2));
                        if (tmpExact == 0) {
                            tmpExact = -1;
                        }
                        break Label_0122;
                    }
                    catch (final NumberFormatException e) {
                        throw new IllegalStateException(e);
                    }
                }
                if (m.group(1) != null) {
                    int len = m.group(1).length();
                    len = ((len > 4) ? 4 : len);
                    tmpMax = (int)Math.pow(10.0, len);
                }
                else {
                    tmpExact = 100;
                }
            }
        }
        if (tmpExact <= 0 && tmpMax <= 0) {
            tmpExact = 100;
        }
        this.exactDenom = tmpExact;
        this.maxDenom = tmpMax;
    }
    
    public String format(final Number num) {
        final BigDecimal doubleValue = new BigDecimal(num.doubleValue());
        final boolean isNeg = doubleValue.compareTo(BigDecimal.ZERO) < 0;
        final BigDecimal absValue = doubleValue.abs();
        final BigDecimal wholePart = new BigDecimal(absValue.toBigInteger());
        final BigDecimal decPart = absValue.remainder(BigDecimal.ONE);
        if (wholePart.add(decPart).compareTo(BigDecimal.ZERO) == 0) {
            return "0";
        }
        if (decPart.compareTo(BigDecimal.ZERO) == 0) {
            final StringBuilder sb = new StringBuilder();
            if (isNeg) {
                sb.append("-");
            }
            sb.append(wholePart);
            return sb.toString();
        }
        SimpleFraction fract;
        try {
            if (this.exactDenom > 0) {
                fract = SimpleFraction.buildFractionExactDenominator(decPart.doubleValue(), this.exactDenom);
            }
            else {
                fract = SimpleFraction.buildFractionMaxDenominator(decPart.doubleValue(), this.maxDenom);
            }
        }
        catch (final RuntimeException e) {
            FractionFormat.LOGGER.log(5, "Can't format fraction", e);
            return Double.toString(doubleValue.doubleValue());
        }
        final StringBuilder sb2 = new StringBuilder();
        if (isNeg) {
            sb2.append("-");
        }
        if (this.wholePartFormatString == null || this.wholePartFormatString.isEmpty()) {
            final int fden = fract.getDenominator();
            final int fnum = fract.getNumerator();
            final BigDecimal trueNum = wholePart.multiply(new BigDecimal(fden)).add(new BigDecimal(fnum));
            sb2.append(trueNum.toBigInteger()).append("/").append(fden);
            return sb2.toString();
        }
        if (fract.getNumerator() == 0) {
            sb2.append(wholePart);
            return sb2.toString();
        }
        if (fract.getNumerator() == fract.getDenominator()) {
            sb2.append(wholePart.add(BigDecimal.ONE));
            return sb2.toString();
        }
        if (wholePart.compareTo(BigDecimal.ZERO) > 0) {
            sb2.append(wholePart).append(" ");
        }
        sb2.append(fract.getNumerator()).append("/").append(fract.getDenominator());
        return sb2.toString();
    }
    
    @Override
    public StringBuffer format(final Object obj, final StringBuffer toAppendTo, final FieldPosition pos) {
        return toAppendTo.append(this.format((Number)obj));
    }
    
    @Override
    public Object parseObject(final String source, final ParsePosition pos) {
        throw new NotImplementedException("Reverse parsing not supported");
    }
    
    static {
        LOGGER = POILogFactory.getLogger(FractionFormat.class);
        DENOM_FORMAT_PATTERN = Pattern.compile("(?:(#+)|(\\d+))");
    }
}
