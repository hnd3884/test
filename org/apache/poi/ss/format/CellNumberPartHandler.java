package org.apache.poi.ss.format;

import java.util.Iterator;
import java.util.ListIterator;
import java.util.regex.Matcher;
import java.util.LinkedList;
import java.util.List;
import org.apache.poi.util.Internal;

@Internal
public class CellNumberPartHandler implements CellFormatPart.PartHandler
{
    private char insertSignForExponent;
    private double scale;
    private CellNumberFormatter.Special decimalPoint;
    private CellNumberFormatter.Special slash;
    private CellNumberFormatter.Special exponent;
    private CellNumberFormatter.Special numerator;
    private final List<CellNumberFormatter.Special> specials;
    private boolean improperFraction;
    
    public CellNumberPartHandler() {
        this.scale = 1.0;
        this.specials = new LinkedList<CellNumberFormatter.Special>();
    }
    
    @Override
    public String handlePart(final Matcher m, final String part, final CellFormatType type, final StringBuffer descBuf) {
        int pos = descBuf.length();
        final char firstCh = part.charAt(0);
        switch (firstCh) {
            case 'E':
            case 'e': {
                if (this.exponent == null && this.specials.size() > 0) {
                    this.exponent = new CellNumberFormatter.Special('.', pos);
                    this.specials.add(this.exponent);
                    this.insertSignForExponent = part.charAt(1);
                    return part.substring(0, 1);
                }
                break;
            }
            case '#':
            case '0':
            case '?': {
                if (this.insertSignForExponent != '\0') {
                    this.specials.add(new CellNumberFormatter.Special(this.insertSignForExponent, pos));
                    descBuf.append(this.insertSignForExponent);
                    this.insertSignForExponent = '\0';
                    ++pos;
                }
                for (int i = 0; i < part.length(); ++i) {
                    final char ch = part.charAt(i);
                    this.specials.add(new CellNumberFormatter.Special(ch, pos + i));
                }
                break;
            }
            case '.': {
                if (this.decimalPoint == null && this.specials.size() > 0) {
                    this.decimalPoint = new CellNumberFormatter.Special('.', pos);
                    this.specials.add(this.decimalPoint);
                    break;
                }
                break;
            }
            case '/': {
                if (this.slash == null && this.specials.size() > 0) {
                    this.numerator = this.previousNumber();
                    this.improperFraction |= (this.numerator == firstDigit(this.specials));
                    this.slash = new CellNumberFormatter.Special('.', pos);
                    this.specials.add(this.slash);
                    break;
                }
                break;
            }
            case '%': {
                this.scale *= 100.0;
                break;
            }
            default: {
                return null;
            }
        }
        return part;
    }
    
    public double getScale() {
        return this.scale;
    }
    
    public CellNumberFormatter.Special getDecimalPoint() {
        return this.decimalPoint;
    }
    
    public CellNumberFormatter.Special getSlash() {
        return this.slash;
    }
    
    public CellNumberFormatter.Special getExponent() {
        return this.exponent;
    }
    
    public CellNumberFormatter.Special getNumerator() {
        return this.numerator;
    }
    
    public List<CellNumberFormatter.Special> getSpecials() {
        return this.specials;
    }
    
    public boolean isImproperFraction() {
        return this.improperFraction;
    }
    
    private CellNumberFormatter.Special previousNumber() {
        final ListIterator<CellNumberFormatter.Special> it = this.specials.listIterator(this.specials.size());
        while (it.hasPrevious()) {
            CellNumberFormatter.Special s = it.previous();
            if (isDigitFmt(s)) {
                CellNumberFormatter.Special last = s;
                while (it.hasPrevious()) {
                    s = it.previous();
                    if (last.pos - s.pos > 1) {
                        break;
                    }
                    if (!isDigitFmt(s)) {
                        break;
                    }
                    last = s;
                }
                return last;
            }
        }
        return null;
    }
    
    private static boolean isDigitFmt(final CellNumberFormatter.Special s) {
        return s.ch == '0' || s.ch == '?' || s.ch == '#';
    }
    
    private static CellNumberFormatter.Special firstDigit(final List<CellNumberFormatter.Special> specials) {
        for (final CellNumberFormatter.Special s : specials) {
            if (isDigitFmt(s)) {
                return s;
            }
        }
        return null;
    }
}
