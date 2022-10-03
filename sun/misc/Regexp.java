package sun.misc;

public class Regexp
{
    public boolean ignoreCase;
    public String exp;
    public String prefix;
    public String suffix;
    public boolean exact;
    public int prefixLen;
    public int suffixLen;
    public int totalLen;
    public String[] mids;
    
    public Regexp(final String exp) {
        this.exp = exp;
        final int index = exp.indexOf(42);
        final int lastIndex = exp.lastIndexOf(42);
        if (index < 0) {
            this.totalLen = exp.length();
            this.exact = true;
        }
        else {
            if ((this.prefixLen = index) == 0) {
                this.prefix = null;
            }
            else {
                this.prefix = exp.substring(0, index);
            }
            this.suffixLen = exp.length() - lastIndex - 1;
            if (this.suffixLen == 0) {
                this.suffix = null;
            }
            else {
                this.suffix = exp.substring(lastIndex + 1);
            }
            int n = 0;
            for (int index2 = index; index2 < lastIndex && index2 >= 0; index2 = exp.indexOf(42, index2 + 1)) {
                ++n;
            }
            this.totalLen = this.prefixLen + this.suffixLen;
            if (n > 0) {
                this.mids = new String[n];
                int n2 = index;
                for (int i = 0; i < n; ++i) {
                    ++n2;
                    final int index3 = exp.indexOf(42, n2);
                    if (n2 < index3) {
                        this.mids[i] = exp.substring(n2, index3);
                        this.totalLen += this.mids[i].length();
                    }
                    n2 = index3;
                }
            }
        }
    }
    
    final boolean matches(final String s) {
        return this.matches(s, 0, s.length());
    }
    
    boolean matches(final String s, final int n, final int n2) {
        if (this.exact) {
            return n2 == this.totalLen && this.exp.regionMatches(this.ignoreCase, 0, s, n, n2);
        }
        if (n2 < this.totalLen) {
            return false;
        }
        if ((this.prefixLen > 0 && !this.prefix.regionMatches(this.ignoreCase, 0, s, n, this.prefixLen)) || (this.suffixLen > 0 && !this.suffix.regionMatches(this.ignoreCase, 0, s, n + n2 - this.suffixLen, this.suffixLen))) {
            return false;
        }
        if (this.mids == null) {
            return true;
        }
        final int length = this.mids.length;
        int n3 = n + this.prefixLen;
        final int n4 = n + n2 - this.suffixLen;
        for (int i = 0; i < length; ++i) {
            String s2;
            int length2;
            for (s2 = this.mids[i], length2 = s2.length(); n3 + length2 <= n4 && !s2.regionMatches(this.ignoreCase, 0, s, n3, length2); ++n3) {}
            if (n3 + length2 > n4) {
                return false;
            }
            n3 += length2;
        }
        return true;
    }
}
