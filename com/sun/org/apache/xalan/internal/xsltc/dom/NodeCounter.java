package com.sun.org.apache.xalan.internal.xsltc.dom;

import java.util.Vector;
import com.sun.org.apache.xalan.internal.xsltc.Translet;
import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;
import com.sun.org.apache.xalan.internal.xsltc.DOM;

public abstract class NodeCounter
{
    public static final int END = -1;
    protected int _node;
    protected int _nodeType;
    protected double _value;
    public final DOM _document;
    public final DTMAxisIterator _iterator;
    public final Translet _translet;
    protected String _format;
    protected String _lang;
    protected String _letterValue;
    protected String _groupSep;
    protected int _groupSize;
    private boolean _separFirst;
    private boolean _separLast;
    private Vector _separToks;
    private Vector _formatToks;
    private int _nSepars;
    private int _nFormats;
    private static final String[] Thousands;
    private static final String[] Hundreds;
    private static final String[] Tens;
    private static final String[] Ones;
    private StringBuilder _tempBuffer;
    protected boolean _hasFrom;
    
    protected NodeCounter(final Translet translet, final DOM document, final DTMAxisIterator iterator) {
        this._node = -1;
        this._nodeType = -1;
        this._value = -2.147483648E9;
        this._separFirst = true;
        this._separLast = false;
        this._separToks = new Vector();
        this._formatToks = new Vector();
        this._nSepars = 0;
        this._nFormats = 0;
        this._tempBuffer = new StringBuilder();
        this._translet = translet;
        this._document = document;
        this._iterator = iterator;
    }
    
    protected NodeCounter(final Translet translet, final DOM document, final DTMAxisIterator iterator, final boolean hasFrom) {
        this._node = -1;
        this._nodeType = -1;
        this._value = -2.147483648E9;
        this._separFirst = true;
        this._separLast = false;
        this._separToks = new Vector();
        this._formatToks = new Vector();
        this._nSepars = 0;
        this._nFormats = 0;
        this._tempBuffer = new StringBuilder();
        this._translet = translet;
        this._document = document;
        this._iterator = iterator;
        this._hasFrom = hasFrom;
    }
    
    public abstract NodeCounter setStartNode(final int p0);
    
    public NodeCounter setValue(final double value) {
        this._value = value;
        return this;
    }
    
    protected void setFormatting(final String format, final String lang, final String letterValue, final String groupSep, final String groupSize) {
        this._lang = lang;
        this._groupSep = groupSep;
        this._letterValue = letterValue;
        this._groupSize = this.parseStringToAnInt(groupSize);
        this.setTokens(format);
    }
    
    private int parseStringToAnInt(final String s) {
        if (s == null) {
            return 0;
        }
        int result = 0;
        boolean negative = false;
        final int radix = 10;
        int i = 0;
        final int max = s.length();
        if (max <= 0) {
            return 0;
        }
        int limit;
        if (s.charAt(0) == '-') {
            negative = true;
            limit = Integer.MIN_VALUE;
            ++i;
        }
        else {
            limit = -2147483647;
        }
        final int multmin = limit / radix;
        if (i < max) {
            final int digit = Character.digit(s.charAt(i++), radix);
            if (digit < 0) {
                return 0;
            }
            result = -digit;
        }
        while (i < max) {
            final int digit = Character.digit(s.charAt(i++), radix);
            if (digit < 0) {
                return 0;
            }
            if (result < multmin) {
                return 0;
            }
            result *= radix;
            if (result < limit + digit) {
                return 0;
            }
            result -= digit;
        }
        if (!negative) {
            return -result;
        }
        if (i > 1) {
            return result;
        }
        return 0;
    }
    
    private final void setTokens(final String format) {
        if (this._format != null && format.equals(this._format)) {
            return;
        }
        this._format = format;
        final int length = this._format.length();
        boolean isFirst = true;
        this._separFirst = true;
        this._separLast = false;
        this._nSepars = 0;
        this._nFormats = 0;
        this._separToks.clear();
        this._formatToks.clear();
        int j = 0;
        int i = 0;
        while (i < length) {
            char c = format.charAt(i);
            j = i;
            while (Character.isLetterOrDigit(c) && ++i != length) {
                c = format.charAt(i);
            }
            if (i > j) {
                if (isFirst) {
                    this._separToks.addElement(".");
                    final boolean separFirst = false;
                    this._separFirst = separFirst;
                    isFirst = separFirst;
                }
                this._formatToks.addElement(format.substring(j, i));
            }
            if (i == length) {
                break;
            }
            c = format.charAt(i);
            j = i;
            while (!Character.isLetterOrDigit(c) && ++i != length) {
                c = format.charAt(i);
                isFirst = false;
            }
            if (i <= j) {
                continue;
            }
            this._separToks.addElement(format.substring(j, i));
        }
        this._nSepars = this._separToks.size();
        this._nFormats = this._formatToks.size();
        if (this._nSepars > this._nFormats) {
            this._separLast = true;
        }
        if (this._separFirst) {
            --this._nSepars;
        }
        if (this._separLast) {
            --this._nSepars;
        }
        if (this._nSepars == 0) {
            this._separToks.insertElementAt(".", 1);
            ++this._nSepars;
        }
        if (this._separFirst) {
            ++this._nSepars;
        }
    }
    
    public NodeCounter setDefaultFormatting() {
        this.setFormatting("1", "en", "alphabetic", null, null);
        return this;
    }
    
    public abstract String getCounter();
    
    public String getCounter(final String format, final String lang, final String letterValue, final String groupSep, final String groupSize) {
        this.setFormatting(format, lang, letterValue, groupSep, groupSize);
        return this.getCounter();
    }
    
    public boolean matchesCount(final int node) {
        return this._nodeType == this._document.getExpandedTypeID(node);
    }
    
    public boolean matchesFrom(final int node) {
        return false;
    }
    
    protected String formatNumbers(final int value) {
        return this.formatNumbers(new int[] { value });
    }
    
    protected String formatNumbers(final int[] values) {
        final int nValues = values.length;
        boolean isEmpty = true;
        for (int i = 0; i < nValues; ++i) {
            if (values[i] != Integer.MIN_VALUE) {
                isEmpty = false;
            }
        }
        if (isEmpty) {
            return "";
        }
        boolean isFirst = true;
        int t = 0;
        int n = 0;
        int s = 1;
        this._tempBuffer.setLength(0);
        final StringBuilder buffer = this._tempBuffer;
        if (this._separFirst) {
            buffer.append(this._separToks.elementAt(0));
        }
        while (n < nValues) {
            final int value = values[n];
            if (value != Integer.MIN_VALUE) {
                if (!isFirst) {
                    buffer.append(this._separToks.elementAt(s++));
                }
                this.formatValue(value, this._formatToks.elementAt(t++), buffer);
                if (t == this._nFormats) {
                    --t;
                }
                if (s >= this._nSepars) {
                    --s;
                }
                isFirst = false;
            }
            ++n;
        }
        if (this._separLast) {
            buffer.append(this._separToks.lastElement());
        }
        return buffer.toString();
    }
    
    private void formatValue(final int value, final String format, final StringBuilder buffer) {
        final char c = format.charAt(0);
        if (Character.isDigit(c)) {
            final char zero = (char)(c - Character.getNumericValue(c));
            StringBuilder temp = buffer;
            if (this._groupSize > 0) {
                temp = new StringBuilder();
            }
            String s = "";
            for (int n = value; n > 0; n /= 10) {
                s = (char)(zero + n % 10) + s;
            }
            for (int i = 0; i < format.length() - s.length(); ++i) {
                temp.append(zero);
            }
            temp.append(s);
            if (this._groupSize > 0) {
                for (int i = 0; i < temp.length(); ++i) {
                    if (i != 0 && (temp.length() - i) % this._groupSize == 0) {
                        buffer.append(this._groupSep);
                    }
                    buffer.append(temp.charAt(i));
                }
            }
        }
        else if (c == 'i' && !this._letterValue.equals("alphabetic")) {
            buffer.append(this.romanValue(value));
        }
        else if (c == 'I' && !this._letterValue.equals("alphabetic")) {
            buffer.append(this.romanValue(value).toUpperCase());
        }
        else {
            final int min = c;
            int max;
            if ((max = c) >= 945 && c <= '\u03c9') {
                max = 969;
            }
            else {
                while (Character.isLetterOrDigit((char)(max + 1))) {
                    ++max;
                }
            }
            buffer.append(this.alphaValue(value, min, max));
        }
    }
    
    private String alphaValue(final int value, final int min, final int max) {
        if (value <= 0) {
            return "" + value;
        }
        final int range = max - min + 1;
        final char last = (char)((value - 1) % range + min);
        if (value > range) {
            return this.alphaValue((value - 1) / range, min, max) + last;
        }
        return "" + last;
    }
    
    private String romanValue(final int n) {
        if (n <= 0 || n > 4000) {
            return "" + n;
        }
        return NodeCounter.Thousands[n / 1000] + NodeCounter.Hundreds[n / 100 % 10] + NodeCounter.Tens[n / 10 % 10] + NodeCounter.Ones[n % 10];
    }
    
    static {
        Thousands = new String[] { "", "m", "mm", "mmm" };
        Hundreds = new String[] { "", "c", "cc", "ccc", "cd", "d", "dc", "dcc", "dccc", "cm" };
        Tens = new String[] { "", "x", "xx", "xxx", "xl", "l", "lx", "lxx", "lxxx", "xc" };
        Ones = new String[] { "", "i", "ii", "iii", "iv", "v", "vi", "vii", "viii", "ix" };
    }
}
