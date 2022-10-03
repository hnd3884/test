package org.apache.poi.ss.format;

import org.apache.poi.util.POILogFactory;
import java.text.FieldPosition;
import java.util.Set;
import com.zaxxer.sparsebits.SparseBitSet;
import java.util.Formatter;
import java.util.TreeSet;
import java.util.ListIterator;
import java.util.Collections;
import java.text.DecimalFormatSymbols;
import java.util.Iterator;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Locale;
import org.apache.poi.util.LocaleUtil;
import java.text.DecimalFormat;
import java.util.List;
import org.apache.poi.util.POILogger;

public class CellNumberFormatter extends CellFormatter
{
    private static final POILogger LOG;
    private final String desc;
    private final String printfFmt;
    private final double scale;
    private final Special decimalPoint;
    private final Special slash;
    private final Special exponent;
    private final Special numerator;
    private final Special afterInteger;
    private final Special afterFractional;
    private final boolean showGroupingSeparator;
    private final List<Special> specials;
    private final List<Special> integerSpecials;
    private final List<Special> fractionalSpecials;
    private final List<Special> numeratorSpecials;
    private final List<Special> denominatorSpecials;
    private final List<Special> exponentSpecials;
    private final List<Special> exponentDigitSpecials;
    private final int maxDenominator;
    private final String numeratorFmt;
    private final String denominatorFmt;
    private final boolean improperFraction;
    private final DecimalFormat decimalFmt;
    private final CellFormatter SIMPLE_NUMBER;
    
    public CellNumberFormatter(final String format) {
        this(LocaleUtil.getUserLocale(), format);
    }
    
    public CellNumberFormatter(final Locale locale, final String format) {
        super(locale, format);
        this.specials = new ArrayList<Special>();
        this.integerSpecials = new ArrayList<Special>();
        this.fractionalSpecials = new ArrayList<Special>();
        this.numeratorSpecials = new ArrayList<Special>();
        this.denominatorSpecials = new ArrayList<Special>();
        this.exponentSpecials = new ArrayList<Special>();
        this.exponentDigitSpecials = new ArrayList<Special>();
        this.SIMPLE_NUMBER = new GeneralNumberFormatter(this.locale);
        final CellNumberPartHandler ph = new CellNumberPartHandler();
        final StringBuffer descBuf = CellFormatPart.parseFormat(format, CellFormatType.NUMBER, ph);
        this.exponent = ph.getExponent();
        this.specials.addAll(ph.getSpecials());
        this.improperFraction = ph.isImproperFraction();
        if ((ph.getDecimalPoint() != null || ph.getExponent() != null) && ph.getSlash() != null) {
            this.slash = null;
            this.numerator = null;
        }
        else {
            this.slash = ph.getSlash();
            this.numerator = ph.getNumerator();
        }
        final int precision = interpretPrecision(ph.getDecimalPoint(), this.specials);
        int fractionPartWidth = 0;
        if (ph.getDecimalPoint() != null) {
            fractionPartWidth = 1 + precision;
            if (precision == 0) {
                this.specials.remove(ph.getDecimalPoint());
                this.decimalPoint = null;
            }
            else {
                this.decimalPoint = ph.getDecimalPoint();
            }
        }
        else {
            this.decimalPoint = null;
        }
        if (this.decimalPoint != null) {
            this.afterInteger = this.decimalPoint;
        }
        else if (this.exponent != null) {
            this.afterInteger = this.exponent;
        }
        else if (this.numerator != null) {
            this.afterInteger = this.numerator;
        }
        else {
            this.afterInteger = null;
        }
        if (this.exponent != null) {
            this.afterFractional = this.exponent;
        }
        else if (this.numerator != null) {
            this.afterFractional = this.numerator;
        }
        else {
            this.afterFractional = null;
        }
        final double[] scaleByRef = { ph.getScale() };
        this.showGroupingSeparator = interpretIntegerCommas(descBuf, this.specials, this.decimalPoint, this.integerEnd(), this.fractionalEnd(), scaleByRef);
        if (this.exponent == null) {
            this.scale = scaleByRef[0];
        }
        else {
            this.scale = 1.0;
        }
        if (precision != 0) {
            this.fractionalSpecials.addAll(this.specials.subList(this.specials.indexOf(this.decimalPoint) + 1, this.fractionalEnd()));
        }
        if (this.exponent != null) {
            final int exponentPos = this.specials.indexOf(this.exponent);
            this.exponentSpecials.addAll(this.specialsFor(exponentPos, 2));
            this.exponentDigitSpecials.addAll(this.specialsFor(exponentPos + 2));
        }
        if (this.slash != null) {
            if (this.numerator != null) {
                this.numeratorSpecials.addAll(this.specialsFor(this.specials.indexOf(this.numerator)));
            }
            this.denominatorSpecials.addAll(this.specialsFor(this.specials.indexOf(this.slash) + 1));
            if (this.denominatorSpecials.isEmpty()) {
                this.numeratorSpecials.clear();
                this.maxDenominator = 1;
                this.numeratorFmt = null;
                this.denominatorFmt = null;
            }
            else {
                this.maxDenominator = maxValue(this.denominatorSpecials);
                this.numeratorFmt = singleNumberFormat(this.numeratorSpecials);
                this.denominatorFmt = singleNumberFormat(this.denominatorSpecials);
            }
        }
        else {
            this.maxDenominator = 1;
            this.numeratorFmt = null;
            this.denominatorFmt = null;
        }
        this.integerSpecials.addAll(this.specials.subList(0, this.integerEnd()));
        if (this.exponent == null) {
            final StringBuilder fmtBuf = new StringBuilder("%");
            final int integerPartWidth = this.calculateIntegerPartWidth();
            final int totalWidth = integerPartWidth + fractionPartWidth;
            fmtBuf.append('0').append(totalWidth).append('.').append(precision);
            fmtBuf.append("f");
            this.printfFmt = fmtBuf.toString();
            this.decimalFmt = null;
        }
        else {
            final StringBuffer fmtBuf2 = new StringBuffer();
            boolean first = true;
            if (this.integerSpecials.size() == 1) {
                fmtBuf2.append("0");
                first = false;
            }
            else {
                for (final Special s : this.integerSpecials) {
                    if (isDigitFmt(s)) {
                        fmtBuf2.append(first ? '#' : '0');
                        first = false;
                    }
                }
            }
            if (this.fractionalSpecials.size() > 0) {
                fmtBuf2.append('.');
                for (final Special s : this.fractionalSpecials) {
                    if (isDigitFmt(s)) {
                        if (!first) {
                            fmtBuf2.append('0');
                        }
                        first = false;
                    }
                }
            }
            fmtBuf2.append('E');
            placeZeros(fmtBuf2, this.exponentSpecials.subList(2, this.exponentSpecials.size()));
            this.decimalFmt = new DecimalFormat(fmtBuf2.toString(), this.getDecimalFormatSymbols());
            this.printfFmt = null;
        }
        this.desc = descBuf.toString();
    }
    
    private DecimalFormatSymbols getDecimalFormatSymbols() {
        return DecimalFormatSymbols.getInstance(this.locale);
    }
    
    private static void placeZeros(final StringBuffer sb, final List<Special> specials) {
        for (final Special s : specials) {
            if (isDigitFmt(s)) {
                sb.append('0');
            }
        }
    }
    
    private static CellNumberStringMod insertMod(final Special special, final CharSequence toAdd, final int where) {
        return new CellNumberStringMod(special, toAdd, where);
    }
    
    private static CellNumberStringMod deleteMod(final Special start, final boolean startInclusive, final Special end, final boolean endInclusive) {
        return new CellNumberStringMod(start, startInclusive, end, endInclusive);
    }
    
    private static CellNumberStringMod replaceMod(final Special start, final boolean startInclusive, final Special end, final boolean endInclusive, final char withChar) {
        return new CellNumberStringMod(start, startInclusive, end, endInclusive, withChar);
    }
    
    private static String singleNumberFormat(final List<Special> numSpecials) {
        return "%0" + numSpecials.size() + "d";
    }
    
    private static int maxValue(final List<Special> s) {
        return Math.toIntExact(Math.round(Math.pow(10.0, s.size()) - 1.0));
    }
    
    private List<Special> specialsFor(final int pos, final int takeFirst) {
        if (pos >= this.specials.size()) {
            return Collections.emptyList();
        }
        final ListIterator<Special> it = this.specials.listIterator(pos + takeFirst);
        Special last = it.next();
        int end = pos + takeFirst;
        while (it.hasNext()) {
            final Special s = it.next();
            if (!isDigitFmt(s)) {
                break;
            }
            if (s.pos - last.pos > 1) {
                break;
            }
            ++end;
            last = s;
        }
        return this.specials.subList(pos, end + 1);
    }
    
    private List<Special> specialsFor(final int pos) {
        return this.specialsFor(pos, 0);
    }
    
    private static boolean isDigitFmt(final Special s) {
        return s.ch == '0' || s.ch == '?' || s.ch == '#';
    }
    
    private int calculateIntegerPartWidth() {
        int digitCount = 0;
        for (final Special s : this.specials) {
            if (s == this.afterInteger) {
                break;
            }
            if (!isDigitFmt(s)) {
                continue;
            }
            ++digitCount;
        }
        return digitCount;
    }
    
    private static int interpretPrecision(final Special decimalPoint, final List<Special> specials) {
        final int idx = specials.indexOf(decimalPoint);
        int precision = 0;
        if (idx != -1) {
            final ListIterator<Special> it = specials.listIterator(idx + 1);
            while (it.hasNext()) {
                final Special s = it.next();
                if (!isDigitFmt(s)) {
                    break;
                }
                ++precision;
            }
        }
        return precision;
    }
    
    private static boolean interpretIntegerCommas(final StringBuffer sb, final List<Special> specials, final Special decimalPoint, final int integerEnd, final int fractionalEnd, final double[] scale) {
        ListIterator<Special> it = specials.listIterator(integerEnd);
        boolean stillScaling = true;
        boolean integerCommas = false;
        while (it.hasPrevious()) {
            final Special s = it.previous();
            if (s.ch != ',') {
                stillScaling = false;
            }
            else if (stillScaling) {
                final int n = 0;
                scale[n] /= 1000.0;
            }
            else {
                integerCommas = true;
            }
        }
        if (decimalPoint != null) {
            it = specials.listIterator(fractionalEnd);
            while (it.hasPrevious()) {
                final Special s = it.previous();
                if (s.ch != ',') {
                    break;
                }
                final int n2 = 0;
                scale[n2] /= 1000.0;
            }
        }
        it = specials.listIterator();
        int removed = 0;
        while (it.hasNext()) {
            final Special special;
            final Special s2 = special = it.next();
            special.pos -= removed;
            if (s2.ch == ',') {
                ++removed;
                it.remove();
                sb.deleteCharAt(s2.pos);
            }
        }
        return integerCommas;
    }
    
    private int integerEnd() {
        return (this.afterInteger == null) ? this.specials.size() : this.specials.indexOf(this.afterInteger);
    }
    
    private int fractionalEnd() {
        return (this.afterFractional == null) ? this.specials.size() : this.specials.indexOf(this.afterFractional);
    }
    
    @Override
    public void formatValue(final StringBuffer toAppendTo, final Object valueObject) {
        double value = ((Number)valueObject).doubleValue();
        value *= this.scale;
        final boolean negative = value < 0.0;
        if (negative) {
            value = -value;
        }
        double fractional = 0.0;
        if (this.slash != null) {
            if (this.improperFraction) {
                fractional = value;
                value = 0.0;
            }
            else {
                fractional = value % 1.0;
                value = (double)(long)value;
            }
        }
        final Set<CellNumberStringMod> mods = new TreeSet<CellNumberStringMod>();
        final StringBuffer output = new StringBuffer(this.localiseFormat(this.desc));
        if (this.exponent != null) {
            this.writeScientific(value, output, mods);
        }
        else if (this.improperFraction) {
            this.writeFraction(value, null, fractional, output, mods);
        }
        else {
            final StringBuffer result = new StringBuffer();
            try (final Formatter f = new Formatter(result, this.locale)) {
                f.format(this.locale, this.printfFmt, value);
            }
            if (this.numerator == null) {
                this.writeFractional(result, output);
                this.writeInteger(result, output, this.integerSpecials, mods, this.showGroupingSeparator);
            }
            else {
                this.writeFraction(value, result, fractional, output, mods);
            }
        }
        final DecimalFormatSymbols dfs = this.getDecimalFormatSymbols();
        final String groupingSeparator = Character.toString(dfs.getGroupingSeparator());
        final Iterator<CellNumberStringMod> changes = mods.iterator();
        CellNumberStringMod nextChange = changes.hasNext() ? changes.next() : null;
        final SparseBitSet deletedChars = new SparseBitSet();
        int adjust = 0;
        for (final Special s : this.specials) {
            final int adjustedPos = s.pos + adjust;
            if (!deletedChars.get(s.pos) && output.charAt(adjustedPos) == '#') {
                output.deleteCharAt(adjustedPos);
                --adjust;
                deletedChars.set(s.pos);
            }
            while (nextChange != null && s == nextChange.getSpecial()) {
                final int lenBefore = output.length();
                int modPos = s.pos + adjust;
                switch (nextChange.getOp()) {
                    case 2: {
                        if (nextChange.getToAdd().equals(groupingSeparator) && deletedChars.get(s.pos)) {
                            break;
                        }
                        output.insert(modPos + 1, nextChange.getToAdd());
                        break;
                    }
                    case 1: {
                        output.insert(modPos, nextChange.getToAdd());
                        break;
                    }
                    case 3: {
                        int delPos = s.pos;
                        if (!nextChange.isStartInclusive()) {
                            ++delPos;
                            ++modPos;
                        }
                        while (deletedChars.get(delPos)) {
                            ++delPos;
                            ++modPos;
                        }
                        int delEndPos = nextChange.getEnd().pos;
                        if (nextChange.isEndInclusive()) {
                            ++delEndPos;
                        }
                        final int modEndPos = delEndPos + adjust;
                        if (modPos < modEndPos) {
                            if ("".equals(nextChange.getToAdd())) {
                                output.delete(modPos, modEndPos);
                            }
                            else {
                                final char fillCh = nextChange.getToAdd().charAt(0);
                                for (int i = modPos; i < modEndPos; ++i) {
                                    output.setCharAt(i, fillCh);
                                }
                            }
                            deletedChars.set(delPos, delEndPos);
                            break;
                        }
                        break;
                    }
                    default: {
                        throw new IllegalStateException("Unknown op: " + nextChange.getOp());
                    }
                }
                adjust += output.length() - lenBefore;
                nextChange = (changes.hasNext() ? changes.next() : null);
            }
        }
        if (negative) {
            toAppendTo.append('-');
        }
        toAppendTo.append(output);
    }
    
    private void writeScientific(final double value, final StringBuffer output, final Set<CellNumberStringMod> mods) {
        final StringBuffer result = new StringBuffer();
        final FieldPosition fractionPos = new FieldPosition(1);
        this.decimalFmt.format(value, result, fractionPos);
        this.writeInteger(result, output, this.integerSpecials, mods, this.showGroupingSeparator);
        this.writeFractional(result, output);
        final int ePos = fractionPos.getEndIndex();
        final int signPos = ePos + 1;
        char expSignRes = result.charAt(signPos);
        if (expSignRes != '-') {
            expSignRes = '+';
            result.insert(signPos, '+');
        }
        final ListIterator<Special> it = this.exponentSpecials.listIterator(1);
        final Special expSign = it.next();
        final char expSignFmt = expSign.ch;
        if (expSignRes == '-' || expSignFmt == '+') {
            mods.add(replaceMod(expSign, true, expSign, true, expSignRes));
        }
        else {
            mods.add(deleteMod(expSign, true, expSign, true));
        }
        final StringBuffer exponentNum = new StringBuffer(result.substring(signPos + 1));
        this.writeInteger(exponentNum, output, this.exponentDigitSpecials, mods, false);
    }
    
    private void writeFraction(final double value, final StringBuffer result, final double fractional, final StringBuffer output, final Set<CellNumberStringMod> mods) {
        if (!this.improperFraction) {
            if (fractional == 0.0 && !hasChar('0', this.numeratorSpecials)) {
                this.writeInteger(result, output, this.integerSpecials, mods, false);
                final Special start = lastSpecial(this.integerSpecials);
                final Special end = lastSpecial(this.denominatorSpecials);
                if (hasChar('?', this.integerSpecials, this.numeratorSpecials, this.denominatorSpecials)) {
                    mods.add(replaceMod(start, false, end, true, ' '));
                }
                else {
                    mods.add(deleteMod(start, false, end, true));
                }
                return;
            }
            final boolean numNoZero = !hasChar('0', this.numeratorSpecials);
            final boolean intNoZero = !hasChar('0', this.integerSpecials);
            final boolean intOnlyHash = this.integerSpecials.isEmpty() || (this.integerSpecials.size() == 1 && hasChar('#', this.integerSpecials));
            final boolean removeBecauseZero = fractional == 0.0 && (intOnlyHash || numNoZero);
            final boolean removeBecauseFraction = fractional != 0.0 && intNoZero;
            if (value == 0.0 && (removeBecauseZero || removeBecauseFraction)) {
                final Special start2 = lastSpecial(this.integerSpecials);
                final boolean hasPlaceHolder = hasChar('?', this.integerSpecials, this.numeratorSpecials);
                final CellNumberStringMod sm = hasPlaceHolder ? replaceMod(start2, true, this.numerator, false, ' ') : deleteMod(start2, true, this.numerator, false);
                mods.add(sm);
            }
            else {
                this.writeInteger(result, output, this.integerSpecials, mods, false);
            }
        }
        try {
            int n;
            int d;
            if (fractional == 0.0 || (this.improperFraction && fractional % 1.0 == 0.0)) {
                n = (int)Math.round(fractional);
                d = 1;
            }
            else {
                final SimpleFraction frac = SimpleFraction.buildFractionMaxDenominator(fractional, this.maxDenominator);
                n = frac.getNumerator();
                d = frac.getDenominator();
            }
            if (this.improperFraction) {
                n += (int)Math.round(value * d);
            }
            this.writeSingleInteger(this.numeratorFmt, n, output, this.numeratorSpecials, mods);
            this.writeSingleInteger(this.denominatorFmt, d, output, this.denominatorSpecials, mods);
        }
        catch (final RuntimeException ignored) {
            CellNumberFormatter.LOG.log(7, "error while fraction evaluation", ignored);
        }
    }
    
    private String localiseFormat(String format) {
        final DecimalFormatSymbols dfs = this.getDecimalFormatSymbols();
        if (format.contains(",") && dfs.getGroupingSeparator() != ',') {
            if (format.contains(".") && dfs.getDecimalSeparator() != '.') {
                format = replaceLast(format, "\\.", "[DECIMAL_SEPARATOR]");
                format = format.replace(',', dfs.getGroupingSeparator()).replace("[DECIMAL_SEPARATOR]", Character.toString(dfs.getDecimalSeparator()));
            }
            else {
                format = format.replace(',', dfs.getGroupingSeparator());
            }
        }
        else if (format.contains(".") && dfs.getDecimalSeparator() != '.') {
            format = format.replace('.', dfs.getDecimalSeparator());
        }
        return format;
    }
    
    private static String replaceLast(final String text, final String regex, final String replacement) {
        return text.replaceFirst("(?s)(.*)" + regex, "$1" + replacement);
    }
    
    private static boolean hasChar(final char ch, final List<Special>... numSpecials) {
        for (final List<Special> specials : numSpecials) {
            for (final Special s : specials) {
                if (s.ch == ch) {
                    return true;
                }
            }
        }
        return false;
    }
    
    private void writeSingleInteger(final String fmt, final int num, final StringBuffer output, final List<Special> numSpecials, final Set<CellNumberStringMod> mods) {
        final StringBuffer sb = new StringBuffer();
        try (final Formatter formatter = new Formatter(sb, this.locale)) {
            formatter.format(this.locale, fmt, num);
        }
        this.writeInteger(sb, output, numSpecials, mods, false);
    }
    
    private void writeInteger(final StringBuffer result, final StringBuffer output, final List<Special> numSpecials, final Set<CellNumberStringMod> mods, final boolean showGroupingSeparator) {
        final DecimalFormatSymbols dfs = this.getDecimalFormatSymbols();
        final String decimalSeparator = Character.toString(dfs.getDecimalSeparator());
        final String groupingSeparator = Character.toString(dfs.getGroupingSeparator());
        int pos = result.indexOf(decimalSeparator) - 1;
        if (pos < 0) {
            if (this.exponent != null && numSpecials == this.integerSpecials) {
                pos = result.indexOf("E") - 1;
            }
            else {
                pos = result.length() - 1;
            }
        }
        int strip;
        for (strip = 0; strip < pos; ++strip) {
            final char resultCh = result.charAt(strip);
            if (resultCh != '0' && resultCh != dfs.getGroupingSeparator()) {
                break;
            }
        }
        final ListIterator<Special> it = numSpecials.listIterator(numSpecials.size());
        boolean followWithGroupingSeparator = false;
        Special lastOutputIntegerDigit = null;
        int digit = 0;
        while (it.hasPrevious()) {
            char resultCh2;
            if (pos >= 0) {
                resultCh2 = result.charAt(pos);
            }
            else {
                resultCh2 = '0';
            }
            final Special s = it.previous();
            followWithGroupingSeparator = (showGroupingSeparator && digit > 0 && digit % 3 == 0);
            boolean zeroStrip = false;
            if (resultCh2 != '0' || s.ch == '0' || s.ch == '?' || pos >= strip) {
                zeroStrip = (s.ch == '?' && pos < strip);
                output.setCharAt(s.pos, zeroStrip ? ' ' : resultCh2);
                lastOutputIntegerDigit = s;
            }
            if (followWithGroupingSeparator) {
                mods.add(insertMod(s, zeroStrip ? " " : groupingSeparator, 2));
                followWithGroupingSeparator = false;
            }
            ++digit;
            --pos;
        }
        if (pos >= 0) {
            ++pos;
            final StringBuffer extraLeadingDigits = new StringBuffer(result.substring(0, pos));
            if (showGroupingSeparator) {
                while (pos > 0) {
                    if (digit > 0 && digit % 3 == 0) {
                        extraLeadingDigits.insert(pos, groupingSeparator);
                    }
                    ++digit;
                    --pos;
                }
            }
            mods.add(insertMod(lastOutputIntegerDigit, extraLeadingDigits, 1));
        }
    }
    
    private void writeFractional(final StringBuffer result, final StringBuffer output) {
        if (this.fractionalSpecials.size() > 0) {
            final String decimalSeparator = Character.toString(this.getDecimalFormatSymbols().getDecimalSeparator());
            int digit = result.indexOf(decimalSeparator) + 1;
            int strip;
            if (this.exponent != null) {
                strip = result.indexOf("e") - 1;
            }
            else {
                strip = result.length() - 1;
            }
            while (strip > digit && result.charAt(strip) == '0') {
                --strip;
            }
            for (final Special s : this.fractionalSpecials) {
                final char resultCh = result.charAt(digit);
                if (resultCh != '0' || s.ch == '0' || digit < strip) {
                    output.setCharAt(s.pos, resultCh);
                }
                else if (s.ch == '?') {
                    output.setCharAt(s.pos, ' ');
                }
                ++digit;
            }
        }
    }
    
    @Override
    public void simpleValue(final StringBuffer toAppendTo, final Object value) {
        this.SIMPLE_NUMBER.formatValue(toAppendTo, value);
    }
    
    private static Special lastSpecial(final List<Special> s) {
        return s.get(s.size() - 1);
    }
    
    static {
        LOG = POILogFactory.getLogger(CellNumberFormatter.class);
    }
    
    private static class GeneralNumberFormatter extends CellFormatter
    {
        private GeneralNumberFormatter(final Locale locale) {
            super(locale, "General");
        }
        
        @Override
        public void formatValue(final StringBuffer toAppendTo, final Object value) {
            if (value == null) {
                return;
            }
            CellFormatter cf;
            if (value instanceof Number) {
                final Number num = (Number)value;
                cf = ((num.doubleValue() % 1.0 == 0.0) ? new CellNumberFormatter(this.locale, "#") : new CellNumberFormatter(this.locale, "#.#"));
            }
            else {
                cf = CellTextFormatter.SIMPLE_TEXT;
            }
            cf.formatValue(toAppendTo, value);
        }
        
        @Override
        public void simpleValue(final StringBuffer toAppendTo, final Object value) {
            this.formatValue(toAppendTo, value);
        }
    }
    
    static class Special
    {
        final char ch;
        int pos;
        
        Special(final char ch, final int pos) {
            this.ch = ch;
            this.pos = pos;
        }
        
        @Override
        public String toString() {
            return "'" + this.ch + "' @ " + this.pos;
        }
    }
}
