package com.sun.org.apache.xpath.internal.objects;

import com.sun.org.apache.xml.internal.utils.XMLStringFactory;
import com.sun.org.apache.xml.internal.utils.XMLCharacterRecognizer;
import com.sun.org.apache.xml.internal.utils.XMLString;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.SAXException;
import org.xml.sax.ContentHandler;
import com.sun.org.apache.xpath.internal.res.XPATHMessages;
import com.sun.org.apache.xml.internal.utils.FastStringBuffer;

public class XStringForFSB extends XString
{
    static final long serialVersionUID = -1533039186550674548L;
    int m_start;
    int m_length;
    protected String m_strCache;
    protected int m_hash;
    
    public XStringForFSB(final FastStringBuffer val, final int start, final int length) {
        super(val);
        this.m_strCache = null;
        this.m_hash = 0;
        this.m_start = start;
        this.m_length = length;
        if (null == val) {
            throw new IllegalArgumentException(XPATHMessages.createXPATHMessage("ER_FASTSTRINGBUFFER_CANNOT_BE_NULL", null));
        }
    }
    
    private XStringForFSB(final String val) {
        super(val);
        this.m_strCache = null;
        this.m_hash = 0;
        throw new IllegalArgumentException(XPATHMessages.createXPATHMessage("ER_FSB_CANNOT_TAKE_STRING", null));
    }
    
    public FastStringBuffer fsb() {
        return (FastStringBuffer)this.m_obj;
    }
    
    @Override
    public void appendToFsb(final FastStringBuffer fsb) {
        fsb.append(this.str());
    }
    
    @Override
    public boolean hasString() {
        return null != this.m_strCache;
    }
    
    @Override
    public Object object() {
        return this.str();
    }
    
    @Override
    public String str() {
        if (null == this.m_strCache) {
            this.m_strCache = this.fsb().getString(this.m_start, this.m_length);
        }
        return this.m_strCache;
    }
    
    @Override
    public void dispatchCharactersEvents(final ContentHandler ch) throws SAXException {
        this.fsb().sendSAXcharacters(ch, this.m_start, this.m_length);
    }
    
    @Override
    public void dispatchAsComment(final LexicalHandler lh) throws SAXException {
        this.fsb().sendSAXComment(lh, this.m_start, this.m_length);
    }
    
    @Override
    public int length() {
        return this.m_length;
    }
    
    @Override
    public char charAt(final int index) {
        return this.fsb().charAt(this.m_start + index);
    }
    
    @Override
    public void getChars(final int srcBegin, final int srcEnd, final char[] dst, final int dstBegin) {
        int n = srcEnd - srcBegin;
        if (n > this.m_length) {
            n = this.m_length;
        }
        if (n > dst.length - dstBegin) {
            n = dst.length - dstBegin;
        }
        final int end = srcBegin + this.m_start + n;
        int d = dstBegin;
        final FastStringBuffer fsb = this.fsb();
        for (int i = srcBegin + this.m_start; i < end; ++i) {
            dst[d++] = fsb.charAt(i);
        }
    }
    
    @Override
    public boolean equals(final XMLString obj2) {
        if (this == obj2) {
            return true;
        }
        int n = this.m_length;
        if (n == obj2.length()) {
            final FastStringBuffer fsb = this.fsb();
            int i = this.m_start;
            int j = 0;
            while (n-- != 0) {
                if (fsb.charAt(i) != obj2.charAt(j)) {
                    return false;
                }
                ++i;
                ++j;
            }
            return true;
        }
        return false;
    }
    
    @Override
    public boolean equals(final XObject obj2) {
        if (this == obj2) {
            return true;
        }
        if (obj2.getType() == 2) {
            return obj2.equals(this);
        }
        final String str = obj2.str();
        int n = this.m_length;
        if (n == str.length()) {
            final FastStringBuffer fsb = this.fsb();
            int i = this.m_start;
            int j = 0;
            while (n-- != 0) {
                if (fsb.charAt(i) != str.charAt(j)) {
                    return false;
                }
                ++i;
                ++j;
            }
            return true;
        }
        return false;
    }
    
    @Override
    public boolean equals(final String anotherString) {
        int n = this.m_length;
        if (n == anotherString.length()) {
            final FastStringBuffer fsb = this.fsb();
            int i = this.m_start;
            int j = 0;
            while (n-- != 0) {
                if (fsb.charAt(i) != anotherString.charAt(j)) {
                    return false;
                }
                ++i;
                ++j;
            }
            return true;
        }
        return false;
    }
    
    @Override
    public boolean equals(final Object obj2) {
        if (null == obj2) {
            return false;
        }
        if (obj2 instanceof XNumber) {
            return obj2.equals(this);
        }
        if (obj2 instanceof XNodeSet) {
            return obj2.equals(this);
        }
        if (obj2 instanceof XStringForFSB) {
            return this.equals((XMLString)obj2);
        }
        return this.equals(obj2.toString());
    }
    
    @Override
    public boolean equalsIgnoreCase(final String anotherString) {
        return this.m_length == anotherString.length() && this.str().equalsIgnoreCase(anotherString);
    }
    
    @Override
    public int compareTo(final XMLString xstr) {
        final int len1 = this.m_length;
        final int len2 = xstr.length();
        int n = Math.min(len1, len2);
        final FastStringBuffer fsb = this.fsb();
        int i = this.m_start;
        int j = 0;
        while (n-- != 0) {
            final char c1 = fsb.charAt(i);
            final char c2 = xstr.charAt(j);
            if (c1 != c2) {
                return c1 - c2;
            }
            ++i;
            ++j;
        }
        return len1 - len2;
    }
    
    @Override
    public int compareToIgnoreCase(final XMLString xstr) {
        final int len1 = this.m_length;
        final int len2 = xstr.length();
        int n = Math.min(len1, len2);
        final FastStringBuffer fsb = this.fsb();
        int i = this.m_start;
        int j = 0;
        while (n-- != 0) {
            final char c1 = Character.toLowerCase(fsb.charAt(i));
            final char c2 = Character.toLowerCase(xstr.charAt(j));
            if (c1 != c2) {
                return c1 - c2;
            }
            ++i;
            ++j;
        }
        return len1 - len2;
    }
    
    @Override
    public int hashCode() {
        return super.hashCode();
    }
    
    @Override
    public boolean startsWith(final XMLString prefix, final int toffset) {
        final FastStringBuffer fsb = this.fsb();
        int to = this.m_start + toffset;
        final int tlim = this.m_start + this.m_length;
        int po = 0;
        int pc = prefix.length();
        if (toffset < 0 || toffset > this.m_length - pc) {
            return false;
        }
        while (--pc >= 0) {
            if (fsb.charAt(to) != prefix.charAt(po)) {
                return false;
            }
            ++to;
            ++po;
        }
        return true;
    }
    
    @Override
    public boolean startsWith(final XMLString prefix) {
        return this.startsWith(prefix, 0);
    }
    
    @Override
    public int indexOf(final int ch) {
        return this.indexOf(ch, 0);
    }
    
    @Override
    public int indexOf(final int ch, int fromIndex) {
        final int max = this.m_start + this.m_length;
        final FastStringBuffer fsb = this.fsb();
        if (fromIndex < 0) {
            fromIndex = 0;
        }
        else if (fromIndex >= this.m_length) {
            return -1;
        }
        for (int i = this.m_start + fromIndex; i < max; ++i) {
            if (fsb.charAt(i) == ch) {
                return i - this.m_start;
            }
        }
        return -1;
    }
    
    @Override
    public XMLString substring(final int beginIndex) {
        final int len = this.m_length - beginIndex;
        if (len <= 0) {
            return XString.EMPTYSTRING;
        }
        final int start = this.m_start + beginIndex;
        return new XStringForFSB(this.fsb(), start, len);
    }
    
    @Override
    public XMLString substring(final int beginIndex, final int endIndex) {
        int len = endIndex - beginIndex;
        if (len > this.m_length) {
            len = this.m_length;
        }
        if (len <= 0) {
            return XString.EMPTYSTRING;
        }
        final int start = this.m_start + beginIndex;
        return new XStringForFSB(this.fsb(), start, len);
    }
    
    @Override
    public XMLString concat(final String str) {
        return new XString(this.str().concat(str));
    }
    
    @Override
    public XMLString trim() {
        return this.fixWhiteSpace(true, true, false);
    }
    
    private static boolean isSpace(final char ch) {
        return XMLCharacterRecognizer.isWhiteSpace(ch);
    }
    
    @Override
    public XMLString fixWhiteSpace(final boolean trimHead, final boolean trimTail, final boolean doublePunctuationSpaces) {
        final int end = this.m_length + this.m_start;
        final char[] buf = new char[this.m_length];
        final FastStringBuffer fsb = this.fsb();
        boolean edit = false;
        int d = 0;
        boolean pres = false;
        for (int s = this.m_start; s < end; ++s) {
            final char c = fsb.charAt(s);
            if (isSpace(c)) {
                if (!pres) {
                    if (' ' != c) {
                        edit = true;
                    }
                    buf[d++] = ' ';
                    if (doublePunctuationSpaces && d != 0) {
                        final char prevChar = buf[d - 1];
                        if (prevChar != '.' && prevChar != '!' && prevChar != '?') {
                            pres = true;
                        }
                    }
                    else {
                        pres = true;
                    }
                }
                else {
                    edit = true;
                    pres = true;
                }
            }
            else {
                buf[d++] = c;
                pres = false;
            }
        }
        if (trimTail && 1 <= d && ' ' == buf[d - 1]) {
            edit = true;
            --d;
        }
        int start = 0;
        if (trimHead && 0 < d && ' ' == buf[0]) {
            edit = true;
            ++start;
        }
        final XMLStringFactory xsf = XMLStringFactoryImpl.getFactory();
        return edit ? xsf.newstr(buf, start, d - start) : this;
    }
    
    @Override
    public double toDouble() {
        if (this.m_length == 0) {
            return Double.NaN;
        }
        String valueString;
        int i;
        for (valueString = this.fsb().getString(this.m_start, this.m_length), i = 0; i < this.m_length && XMLCharacterRecognizer.isWhiteSpace(valueString.charAt(i)); ++i) {}
        if (i == this.m_length) {
            return Double.NaN;
        }
        if (valueString.charAt(i) == '-') {
            ++i;
        }
        while (i < this.m_length) {
            final char c = valueString.charAt(i);
            if (c != '.') {
                if (c < '0') {
                    break;
                }
                if (c > '9') {
                    break;
                }
            }
            ++i;
        }
        while (i < this.m_length && XMLCharacterRecognizer.isWhiteSpace(valueString.charAt(i))) {
            ++i;
        }
        if (i != this.m_length) {
            return Double.NaN;
        }
        try {
            return Double.parseDouble(valueString);
        }
        catch (final NumberFormatException nfe) {
            return Double.NaN;
        }
    }
}
