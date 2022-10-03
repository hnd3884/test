package com.sun.org.apache.xpath.internal.objects;

import com.sun.org.apache.xpath.internal.XPathVisitor;
import com.sun.org.apache.xpath.internal.ExpressionOwner;
import com.sun.org.apache.xml.internal.utils.XMLStringFactory;
import com.sun.org.apache.xml.internal.utils.XMLCharacterRecognizer;
import java.util.Locale;
import javax.xml.transform.TransformerException;
import com.sun.org.apache.xml.internal.utils.WrappedRuntimeException;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.SAXException;
import org.xml.sax.ContentHandler;
import com.sun.org.apache.xml.internal.dtm.DTM;
import com.sun.org.apache.xpath.internal.XPathContext;
import com.sun.org.apache.xml.internal.utils.XMLString;

public class XString extends XObject implements XMLString
{
    static final long serialVersionUID = 2020470518395094525L;
    public static final XString EMPTYSTRING;
    
    protected XString(final Object val) {
        super(val);
    }
    
    public XString(final String val) {
        super(val);
    }
    
    @Override
    public int getType() {
        return 3;
    }
    
    @Override
    public String getTypeString() {
        return "#STRING";
    }
    
    @Override
    public boolean hasString() {
        return true;
    }
    
    @Override
    public double num() {
        return this.toDouble();
    }
    
    @Override
    public double toDouble() {
        final XMLString s = this.trim();
        double result = Double.NaN;
        for (int i = 0; i < s.length(); ++i) {
            final char c = s.charAt(i);
            if (c != '-' && c != '.' && (c < '0' || c > '9')) {
                return result;
            }
        }
        try {
            result = Double.parseDouble(s.toString());
        }
        catch (final NumberFormatException ex) {}
        return result;
    }
    
    @Override
    public boolean bool() {
        return this.str().length() > 0;
    }
    
    @Override
    public XMLString xstr() {
        return this;
    }
    
    @Override
    public String str() {
        return (String)((null != this.m_obj) ? this.m_obj : "");
    }
    
    @Override
    public int rtf(final XPathContext support) {
        final DTM frag = support.createDocumentFragment();
        frag.appendTextChild(this.str());
        return frag.getDocument();
    }
    
    @Override
    public void dispatchCharactersEvents(final ContentHandler ch) throws SAXException {
        final String str = this.str();
        ch.characters(str.toCharArray(), 0, str.length());
    }
    
    @Override
    public void dispatchAsComment(final LexicalHandler lh) throws SAXException {
        final String str = this.str();
        lh.comment(str.toCharArray(), 0, str.length());
    }
    
    @Override
    public int length() {
        return this.str().length();
    }
    
    @Override
    public char charAt(final int index) {
        return this.str().charAt(index);
    }
    
    @Override
    public void getChars(final int srcBegin, final int srcEnd, final char[] dst, final int dstBegin) {
        this.str().getChars(srcBegin, srcEnd, dst, dstBegin);
    }
    
    @Override
    public boolean equals(final XObject obj2) {
        final int t = obj2.getType();
        try {
            if (4 == t) {
                return obj2.equals(this);
            }
            if (1 == t) {
                return obj2.bool() == this.bool();
            }
            if (2 == t) {
                return obj2.num() == this.num();
            }
        }
        catch (final TransformerException te) {
            throw new WrappedRuntimeException(te);
        }
        return this.xstr().equals(obj2.xstr());
    }
    
    @Override
    public boolean equals(final String obj2) {
        return this.str().equals(obj2);
    }
    
    @Override
    public boolean equals(final XMLString obj2) {
        if (obj2 == null) {
            return false;
        }
        if (!obj2.hasString()) {
            return obj2.equals(this.str());
        }
        return this.str().equals(obj2.toString());
    }
    
    @Override
    public boolean equals(final Object obj2) {
        if (null == obj2) {
            return false;
        }
        if (obj2 instanceof XNodeSet) {
            return obj2.equals(this);
        }
        if (obj2 instanceof XNumber) {
            return obj2.equals(this);
        }
        return this.str().equals(obj2.toString());
    }
    
    @Override
    public boolean equalsIgnoreCase(final String anotherString) {
        return this.str().equalsIgnoreCase(anotherString);
    }
    
    @Override
    public int compareTo(final XMLString xstr) {
        final int len1 = this.length();
        final int len2 = xstr.length();
        int n = Math.min(len1, len2);
        int i = 0;
        int j = 0;
        while (n-- != 0) {
            final char c1 = this.charAt(i);
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
    public int compareToIgnoreCase(final XMLString str) {
        throw new WrappedRuntimeException(new NoSuchMethodException("Java 1.2 method, not yet implemented"));
    }
    
    @Override
    public boolean startsWith(final String prefix, final int toffset) {
        return this.str().startsWith(prefix, toffset);
    }
    
    @Override
    public boolean startsWith(final String prefix) {
        return this.startsWith(prefix, 0);
    }
    
    @Override
    public boolean startsWith(final XMLString prefix, final int toffset) {
        int to = toffset;
        final int tlim = this.length();
        int po = 0;
        int pc = prefix.length();
        if (toffset < 0 || toffset > tlim - pc) {
            return false;
        }
        while (--pc >= 0) {
            if (this.charAt(to) != prefix.charAt(po)) {
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
    public boolean endsWith(final String suffix) {
        return this.str().endsWith(suffix);
    }
    
    @Override
    public int hashCode() {
        return this.str().hashCode();
    }
    
    @Override
    public int indexOf(final int ch) {
        return this.str().indexOf(ch);
    }
    
    @Override
    public int indexOf(final int ch, final int fromIndex) {
        return this.str().indexOf(ch, fromIndex);
    }
    
    @Override
    public int lastIndexOf(final int ch) {
        return this.str().lastIndexOf(ch);
    }
    
    @Override
    public int lastIndexOf(final int ch, final int fromIndex) {
        return this.str().lastIndexOf(ch, fromIndex);
    }
    
    @Override
    public int indexOf(final String str) {
        return this.str().indexOf(str);
    }
    
    @Override
    public int indexOf(final XMLString str) {
        return this.str().indexOf(str.toString());
    }
    
    @Override
    public int indexOf(final String str, final int fromIndex) {
        return this.str().indexOf(str, fromIndex);
    }
    
    @Override
    public int lastIndexOf(final String str) {
        return this.str().lastIndexOf(str);
    }
    
    @Override
    public int lastIndexOf(final String str, final int fromIndex) {
        return this.str().lastIndexOf(str, fromIndex);
    }
    
    @Override
    public XMLString substring(final int beginIndex) {
        return new XString(this.str().substring(beginIndex));
    }
    
    @Override
    public XMLString substring(final int beginIndex, final int endIndex) {
        return new XString(this.str().substring(beginIndex, endIndex));
    }
    
    @Override
    public XMLString concat(final String str) {
        return new XString(this.str().concat(str));
    }
    
    @Override
    public XMLString toLowerCase(final Locale locale) {
        return new XString(this.str().toLowerCase(locale));
    }
    
    @Override
    public XMLString toLowerCase() {
        return new XString(this.str().toLowerCase());
    }
    
    @Override
    public XMLString toUpperCase(final Locale locale) {
        return new XString(this.str().toUpperCase(locale));
    }
    
    @Override
    public XMLString toUpperCase() {
        return new XString(this.str().toUpperCase());
    }
    
    @Override
    public XMLString trim() {
        return new XString(this.str().trim());
    }
    
    private static boolean isSpace(final char ch) {
        return XMLCharacterRecognizer.isWhiteSpace(ch);
    }
    
    @Override
    public XMLString fixWhiteSpace(final boolean trimHead, final boolean trimTail, final boolean doublePunctuationSpaces) {
        final int len = this.length();
        final char[] buf = new char[len];
        this.getChars(0, len, buf, 0);
        boolean edit = false;
        int s;
        for (s = 0; s < len && !isSpace(buf[s]); ++s) {}
        int d = s;
        boolean pres = false;
        while (s < len) {
            final char c = buf[s];
            if (isSpace(c)) {
                if (!pres) {
                    if (' ' != c) {
                        edit = true;
                    }
                    buf[d++] = ' ';
                    if (doublePunctuationSpaces && s != 0) {
                        final char prevChar = buf[s - 1];
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
            ++s;
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
        return edit ? xsf.newstr(new String(buf, start, d - start)) : this;
    }
    
    @Override
    public void callVisitors(final ExpressionOwner owner, final XPathVisitor visitor) {
        visitor.visitStringLiteral(owner, this);
    }
    
    static {
        EMPTYSTRING = new XString("");
    }
}
