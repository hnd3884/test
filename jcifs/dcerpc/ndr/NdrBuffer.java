package jcifs.dcerpc.ndr;

import java.io.UnsupportedEncodingException;
import jcifs.util.Encdec;
import java.util.HashMap;

public class NdrBuffer
{
    int referent;
    HashMap referents;
    public byte[] buf;
    public int start;
    public int index;
    public int length;
    public NdrBuffer deferred;
    
    public NdrBuffer(final byte[] buf, final int start) {
        this.buf = buf;
        this.index = start;
        this.start = start;
        this.length = 0;
        this.deferred = this;
    }
    
    public NdrBuffer derive(final int idx) {
        final NdrBuffer nb = new NdrBuffer(this.buf, this.start);
        nb.index = idx;
        nb.deferred = this.deferred;
        return nb;
    }
    
    public void reset() {
        this.index = this.start;
        this.length = 0;
        this.deferred = this;
    }
    
    public int getIndex() {
        return this.index;
    }
    
    public void setIndex(final int index) {
        this.index = index;
    }
    
    public int getCapacity() {
        return this.buf.length - this.start;
    }
    
    public int getTailSpace() {
        return this.buf.length - this.index;
    }
    
    public byte[] getBuffer() {
        return this.buf;
    }
    
    public int align(final int boundary, final byte value) {
        int i;
        int n;
        for (n = (i = this.align(boundary)); i > 0; --i) {
            this.buf[this.index - i] = value;
        }
        return n;
    }
    
    public void writeOctetArray(final byte[] b, final int i, final int l) {
        System.arraycopy(b, i, this.buf, this.index, l);
        this.advance(l);
    }
    
    public void readOctetArray(final byte[] b, final int i, final int l) {
        System.arraycopy(this.buf, this.index, b, i, l);
        this.advance(l);
    }
    
    public int getLength() {
        return this.deferred.length;
    }
    
    public void advance(final int n) {
        this.index += n;
        if (this.index - this.start > this.deferred.length) {
            this.deferred.length = this.index - this.start;
        }
    }
    
    public int align(final int boundary) {
        final int m = boundary - 1;
        final int i = this.index - this.start;
        final int n = (i + m & ~m) - i;
        this.advance(n);
        return n;
    }
    
    public void enc_ndr_small(final int s) {
        this.buf[this.index] = (byte)(s & 0xFF);
        this.advance(1);
    }
    
    public int dec_ndr_small() {
        final int val = this.buf[this.index] & 0xFF;
        this.advance(1);
        return val;
    }
    
    public void enc_ndr_short(final int s) {
        this.align(2);
        Encdec.enc_uint16le((short)s, this.buf, this.index);
        this.advance(2);
    }
    
    public int dec_ndr_short() {
        this.align(2);
        final int val = Encdec.dec_uint16le(this.buf, this.index);
        this.advance(2);
        return val;
    }
    
    public void enc_ndr_long(final int l) {
        this.align(4);
        Encdec.enc_uint32le(l, this.buf, this.index);
        this.advance(4);
    }
    
    public int dec_ndr_long() {
        this.align(4);
        final int val = Encdec.dec_uint32le(this.buf, this.index);
        this.advance(4);
        return val;
    }
    
    public void enc_ndr_hyper(final long h) {
        this.align(8);
        Encdec.enc_uint64le(h, this.buf, this.index);
        this.advance(8);
    }
    
    public long dec_ndr_hyper() {
        this.align(8);
        final long val = Encdec.dec_uint64le(this.buf, this.index);
        this.advance(8);
        return val;
    }
    
    public void enc_ndr_string(final String s) {
        this.align(4);
        int i = this.index;
        final int len = s.length();
        Encdec.enc_uint32le(len + 1, this.buf, i);
        i += 4;
        Encdec.enc_uint32le(0, this.buf, i);
        i += 4;
        Encdec.enc_uint32le(len + 1, this.buf, i);
        i += 4;
        try {
            System.arraycopy(s.getBytes("UnicodeLittleUnmarked"), 0, this.buf, i, len * 2);
        }
        catch (final UnsupportedEncodingException ex) {}
        i += len * 2;
        this.buf[i++] = 0;
        this.buf[i++] = 0;
        this.advance(i - this.index);
    }
    
    public String dec_ndr_string() throws NdrException {
        this.align(4);
        int i = this.index;
        String val = null;
        int len = Encdec.dec_uint32le(this.buf, i);
        i += 12;
        if (len != 0) {
            final int size = --len * 2;
            try {
                if (size < 0 || size > 65535) {
                    throw new NdrException("invalid array conformance");
                }
                val = new String(this.buf, i, size, "UnicodeLittleUnmarked");
                i += size + 2;
            }
            catch (final UnsupportedEncodingException ex) {}
        }
        this.advance(i - this.index);
        return val;
    }
    
    private int getDceReferent(final Object obj) {
        if (this.referents == null) {
            this.referents = new HashMap();
            this.referent = 1;
        }
        Entry e;
        if ((e = this.referents.get(obj)) == null) {
            e = new Entry();
            e.referent = this.referent++;
            e.obj = obj;
            this.referents.put(obj, e);
        }
        return e.referent;
    }
    
    public void enc_ndr_referent(final Object obj, final int type) {
        if (obj == null) {
            this.enc_ndr_long(0);
            return;
        }
        switch (type) {
            case 1:
            case 3: {
                this.enc_ndr_long(System.identityHashCode(obj));
                return;
            }
            case 2: {
                this.enc_ndr_long(this.getDceReferent(obj));
            }
            default: {}
        }
    }
    
    public String toString() {
        return "start=" + this.start + ",index=" + this.index + ",length=" + this.getLength();
    }
    
    static class Entry
    {
        int referent;
        Object obj;
    }
}
