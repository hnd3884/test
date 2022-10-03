package org.tartarus.snowball;

import org.apache.lucene.util.ArrayUtil;

public abstract class SnowballProgram
{
    private char[] current;
    protected int cursor;
    protected int limit;
    protected int limit_backward;
    protected int bra;
    protected int ket;
    
    protected SnowballProgram() {
        this.current = new char[8];
        this.setCurrent("");
    }
    
    public abstract boolean stem();
    
    public void setCurrent(final String value) {
        this.current = value.toCharArray();
        this.cursor = 0;
        this.limit = value.length();
        this.limit_backward = 0;
        this.bra = this.cursor;
        this.ket = this.limit;
    }
    
    public String getCurrent() {
        return new String(this.current, 0, this.limit);
    }
    
    public void setCurrent(final char[] text, final int length) {
        this.current = text;
        this.cursor = 0;
        this.limit = length;
        this.limit_backward = 0;
        this.bra = this.cursor;
        this.ket = this.limit;
    }
    
    public char[] getCurrentBuffer() {
        return this.current;
    }
    
    public int getCurrentBufferLength() {
        return this.limit;
    }
    
    protected void copy_from(final SnowballProgram other) {
        this.current = other.current;
        this.cursor = other.cursor;
        this.limit = other.limit;
        this.limit_backward = other.limit_backward;
        this.bra = other.bra;
        this.ket = other.ket;
    }
    
    protected boolean in_grouping(final char[] s, final int min, final int max) {
        if (this.cursor >= this.limit) {
            return false;
        }
        char ch = this.current[this.cursor];
        if (ch > max || ch < min) {
            return false;
        }
        ch -= (char)min;
        if ((s[ch >> 3] & 1 << (ch & '\u0007')) == 0x0) {
            return false;
        }
        ++this.cursor;
        return true;
    }
    
    protected boolean in_grouping_b(final char[] s, final int min, final int max) {
        if (this.cursor <= this.limit_backward) {
            return false;
        }
        char ch = this.current[this.cursor - 1];
        if (ch > max || ch < min) {
            return false;
        }
        ch -= (char)min;
        if ((s[ch >> 3] & 1 << (ch & '\u0007')) == 0x0) {
            return false;
        }
        --this.cursor;
        return true;
    }
    
    protected boolean out_grouping(final char[] s, final int min, final int max) {
        if (this.cursor >= this.limit) {
            return false;
        }
        char ch = this.current[this.cursor];
        if (ch > max || ch < min) {
            ++this.cursor;
            return true;
        }
        ch -= (char)min;
        if ((s[ch >> 3] & 1 << (ch & '\u0007')) == 0x0) {
            ++this.cursor;
            return true;
        }
        return false;
    }
    
    protected boolean out_grouping_b(final char[] s, final int min, final int max) {
        if (this.cursor <= this.limit_backward) {
            return false;
        }
        char ch = this.current[this.cursor - 1];
        if (ch > max || ch < min) {
            --this.cursor;
            return true;
        }
        ch -= (char)min;
        if ((s[ch >> 3] & 1 << (ch & '\u0007')) == 0x0) {
            --this.cursor;
            return true;
        }
        return false;
    }
    
    protected boolean in_range(final int min, final int max) {
        if (this.cursor >= this.limit) {
            return false;
        }
        final char ch = this.current[this.cursor];
        if (ch > max || ch < min) {
            return false;
        }
        ++this.cursor;
        return true;
    }
    
    protected boolean in_range_b(final int min, final int max) {
        if (this.cursor <= this.limit_backward) {
            return false;
        }
        final char ch = this.current[this.cursor - 1];
        if (ch > max || ch < min) {
            return false;
        }
        --this.cursor;
        return true;
    }
    
    protected boolean out_range(final int min, final int max) {
        if (this.cursor >= this.limit) {
            return false;
        }
        final char ch = this.current[this.cursor];
        if (ch <= max && ch >= min) {
            return false;
        }
        ++this.cursor;
        return true;
    }
    
    protected boolean out_range_b(final int min, final int max) {
        if (this.cursor <= this.limit_backward) {
            return false;
        }
        final char ch = this.current[this.cursor - 1];
        if (ch <= max && ch >= min) {
            return false;
        }
        --this.cursor;
        return true;
    }
    
    protected boolean eq_s(final int s_size, final CharSequence s) {
        if (this.limit - this.cursor < s_size) {
            return false;
        }
        for (int i = 0; i != s_size; ++i) {
            if (this.current[this.cursor + i] != s.charAt(i)) {
                return false;
            }
        }
        this.cursor += s_size;
        return true;
    }
    
    protected boolean eq_s_b(final int s_size, final CharSequence s) {
        if (this.cursor - this.limit_backward < s_size) {
            return false;
        }
        for (int i = 0; i != s_size; ++i) {
            if (this.current[this.cursor - s_size + i] != s.charAt(i)) {
                return false;
            }
        }
        this.cursor -= s_size;
        return true;
    }
    
    protected boolean eq_v(final CharSequence s) {
        return this.eq_s(s.length(), s);
    }
    
    protected boolean eq_v_b(final CharSequence s) {
        return this.eq_s_b(s.length(), s);
    }
    
    protected int find_among(final Among[] v, final int v_size) {
        int i = 0;
        int j = v_size;
        final int c = this.cursor;
        final int l = this.limit;
        int common_i = 0;
        int common_j = 0;
        boolean first_key_inspected = false;
        while (true) {
            final int k = i + (j - i >> 1);
            int diff = 0;
            int common = (common_i < common_j) ? common_i : common_j;
            final Among w = v[k];
            for (int i2 = common; i2 < w.s_size; ++i2) {
                if (c + common == l) {
                    diff = -1;
                    break;
                }
                diff = this.current[c + common] - w.s[i2];
                if (diff != 0) {
                    break;
                }
                ++common;
            }
            if (diff < 0) {
                j = k;
                common_j = common;
            }
            else {
                i = k;
                common_i = common;
            }
            if (j - i <= 1) {
                if (i > 0) {
                    break;
                }
                if (j == i) {
                    break;
                }
                if (first_key_inspected) {
                    break;
                }
                first_key_inspected = true;
            }
        }
        while (true) {
            final Among w2 = v[i];
            if (common_i >= w2.s_size) {
                this.cursor = c + w2.s_size;
                if (w2.method == null) {
                    return w2.result;
                }
                boolean res = false;
                try {
                    res = w2.method.invokeExact(this);
                }
                catch (final Throwable e) {
                    rethrow(e);
                }
                this.cursor = c + w2.s_size;
                if (res) {
                    return w2.result;
                }
            }
            i = w2.substring_i;
            if (i < 0) {
                return 0;
            }
        }
    }
    
    protected int find_among_b(final Among[] v, final int v_size) {
        int i = 0;
        int j = v_size;
        final int c = this.cursor;
        final int lb = this.limit_backward;
        int common_i = 0;
        int common_j = 0;
        boolean first_key_inspected = false;
        while (true) {
            final int k = i + (j - i >> 1);
            int diff = 0;
            int common = (common_i < common_j) ? common_i : common_j;
            final Among w = v[k];
            for (int i2 = w.s_size - 1 - common; i2 >= 0; --i2) {
                if (c - common == lb) {
                    diff = -1;
                    break;
                }
                diff = this.current[c - 1 - common] - w.s[i2];
                if (diff != 0) {
                    break;
                }
                ++common;
            }
            if (diff < 0) {
                j = k;
                common_j = common;
            }
            else {
                i = k;
                common_i = common;
            }
            if (j - i <= 1) {
                if (i > 0) {
                    break;
                }
                if (j == i) {
                    break;
                }
                if (first_key_inspected) {
                    break;
                }
                first_key_inspected = true;
            }
        }
        while (true) {
            final Among w2 = v[i];
            if (common_i >= w2.s_size) {
                this.cursor = c - w2.s_size;
                if (w2.method == null) {
                    return w2.result;
                }
                boolean res = false;
                try {
                    res = w2.method.invokeExact(this);
                }
                catch (final Throwable e) {
                    rethrow(e);
                }
                this.cursor = c - w2.s_size;
                if (res) {
                    return w2.result;
                }
            }
            i = w2.substring_i;
            if (i < 0) {
                return 0;
            }
        }
    }
    
    protected int replace_s(final int c_bra, final int c_ket, final CharSequence s) {
        final int adjustment = s.length() - (c_ket - c_bra);
        final int newLength = this.limit + adjustment;
        if (newLength > this.current.length) {
            final char[] newBuffer = new char[ArrayUtil.oversize(newLength, 2)];
            System.arraycopy(this.current, 0, newBuffer, 0, this.limit);
            this.current = newBuffer;
        }
        if (adjustment != 0 && c_ket < this.limit) {
            System.arraycopy(this.current, c_ket, this.current, c_bra + s.length(), this.limit - c_ket);
        }
        for (int i = 0; i < s.length(); ++i) {
            this.current[c_bra + i] = s.charAt(i);
        }
        this.limit += adjustment;
        if (this.cursor >= c_ket) {
            this.cursor += adjustment;
        }
        else if (this.cursor > c_bra) {
            this.cursor = c_bra;
        }
        return adjustment;
    }
    
    protected void slice_check() {
        if (this.bra < 0 || this.bra > this.ket || this.ket > this.limit) {
            throw new IllegalArgumentException("faulty slice operation: bra=" + this.bra + ",ket=" + this.ket + ",limit=" + this.limit);
        }
    }
    
    protected void slice_from(final CharSequence s) {
        this.slice_check();
        this.replace_s(this.bra, this.ket, s);
    }
    
    protected void slice_del() {
        this.slice_from("");
    }
    
    protected void insert(final int c_bra, final int c_ket, final CharSequence s) {
        final int adjustment = this.replace_s(c_bra, c_ket, s);
        if (c_bra <= this.bra) {
            this.bra += adjustment;
        }
        if (c_bra <= this.ket) {
            this.ket += adjustment;
        }
    }
    
    protected StringBuilder slice_to(final StringBuilder s) {
        this.slice_check();
        final int len = this.ket - this.bra;
        s.setLength(0);
        s.append(this.current, this.bra, len);
        return s;
    }
    
    protected StringBuilder assign_to(final StringBuilder s) {
        s.setLength(0);
        s.append(this.current, 0, this.limit);
        return s;
    }
    
    private static void rethrow(final Throwable t) {
        rethrow0(t);
    }
    
    private static <T extends Throwable> void rethrow0(final Throwable t) throws T, Throwable {
        throw t;
    }
}
