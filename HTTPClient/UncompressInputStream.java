package HTTPClient;

import java.io.FileInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.FilterInputStream;

class UncompressInputStream extends FilterInputStream
{
    byte[] one;
    private static final int TBL_CLEAR = 256;
    private static final int TBL_FIRST = 257;
    private int[] tab_prefix;
    private byte[] tab_suffix;
    private int[] zeros;
    private byte[] stack;
    private boolean block_mode;
    private int n_bits;
    private int maxbits;
    private int maxmaxcode;
    private int maxcode;
    private int bitmask;
    private int oldcode;
    private byte finchar;
    private int stackp;
    private int free_ent;
    private byte[] data;
    private int bit_pos;
    private int end;
    private int got;
    private boolean eof;
    private static final int EXTRA = 64;
    private static final int LZW_MAGIC = 8093;
    private static final int MAX_BITS = 16;
    private static final int INIT_BITS = 9;
    private static final int HDR_MAXBITS = 31;
    private static final int HDR_EXTENDED = 32;
    private static final int HDR_FREE = 64;
    private static final int HDR_BLOCK_MODE = 128;
    private static final boolean debug = false;
    
    public UncompressInputStream(final InputStream is) throws IOException {
        super(is);
        this.one = new byte[1];
        this.zeros = new int[256];
        this.data = new byte[10000];
        this.eof = false;
        this.parse_header();
    }
    
    public synchronized int read() throws IOException {
        final int b = super.in.read(this.one, 0, 1);
        if (b == 1) {
            return this.one[0] & 0xFF;
        }
        return -1;
    }
    
    public synchronized int read(final byte[] buf, int off, int len) throws IOException {
        if (this.eof) {
            return -1;
        }
        final int start = off;
        final int[] l_tab_prefix = this.tab_prefix;
        final byte[] l_tab_suffix = this.tab_suffix;
        final byte[] l_stack = this.stack;
        int l_n_bits = this.n_bits;
        int l_maxcode = this.maxcode;
        final int l_maxmaxcode = this.maxmaxcode;
        int l_bitmask = this.bitmask;
        int l_oldcode = this.oldcode;
        byte l_finchar = this.finchar;
        int l_stackp = this.stackp;
        int l_free_ent = this.free_ent;
        final byte[] l_data = this.data;
        int l_bit_pos = this.bit_pos;
        int s_size = l_stack.length - l_stackp;
        if (s_size > 0) {
            final int num = (s_size >= len) ? len : s_size;
            System.arraycopy(l_stack, l_stackp, buf, off, num);
            off += num;
            len -= num;
            l_stackp += num;
        }
        if (len == 0) {
            this.stackp = l_stackp;
            return off - start;
        }
    Label_0779:
        do {
            if (this.end < 64) {
                this.fill();
            }
            final int bit_in = (this.got > 0) ? (this.end - this.end % l_n_bits << 3) : ((this.end << 3) - (l_n_bits - 1));
            while (l_bit_pos < bit_in) {
                if (l_free_ent > l_maxcode) {
                    final int n_bytes = l_n_bits << 3;
                    l_bit_pos = l_bit_pos - 1 + n_bytes - (l_bit_pos - 1 + n_bytes) % n_bytes;
                    l_maxcode = ((++l_n_bits == this.maxbits) ? l_maxmaxcode : ((1 << l_n_bits) - 1));
                    l_bitmask = (1 << l_n_bits) - 1;
                    l_bit_pos = this.resetbuf(l_bit_pos);
                    continue Label_0779;
                }
                final int pos = l_bit_pos >> 3;
                int code = ((l_data[pos] & 0xFF) | (l_data[pos + 1] & 0xFF) << 8 | (l_data[pos + 2] & 0xFF) << 16) >> (l_bit_pos & 0x7) & l_bitmask;
                l_bit_pos += l_n_bits;
                if (l_oldcode == -1) {
                    if (code >= 256) {
                        throw new IOException("corrupt input: " + code + " > 255");
                    }
                    l_finchar = (byte)(l_oldcode = code);
                    buf[off++] = l_finchar;
                    --len;
                }
                else {
                    if (code == 256 && this.block_mode) {
                        System.arraycopy(this.zeros, 0, l_tab_prefix, 0, this.zeros.length);
                        l_free_ent = 256;
                        final int n_bytes2 = l_n_bits << 3;
                        l_bit_pos = l_bit_pos - 1 + n_bytes2 - (l_bit_pos - 1 + n_bytes2) % n_bytes2;
                        l_n_bits = 9;
                        l_maxcode = (l_bitmask = (1 << l_n_bits) - 1);
                        l_bit_pos = this.resetbuf(l_bit_pos);
                        continue Label_0779;
                    }
                    final int incode = code;
                    l_stackp = l_stack.length;
                    if (code >= l_free_ent) {
                        if (code > l_free_ent) {
                            throw new IOException("corrupt input: code=" + code + ", free_ent=" + l_free_ent);
                        }
                        l_stack[--l_stackp] = l_finchar;
                        code = l_oldcode;
                    }
                    while (code >= 256) {
                        l_stack[--l_stackp] = l_tab_suffix[code];
                        code = l_tab_prefix[code];
                    }
                    l_finchar = l_tab_suffix[code];
                    buf[off++] = l_finchar;
                    --len;
                    s_size = l_stack.length - l_stackp;
                    final int num2 = (s_size >= len) ? len : s_size;
                    System.arraycopy(l_stack, l_stackp, buf, off, num2);
                    off += num2;
                    len -= num2;
                    l_stackp += num2;
                    if (l_free_ent < l_maxmaxcode) {
                        l_tab_prefix[l_free_ent] = l_oldcode;
                        l_tab_suffix[l_free_ent] = l_finchar;
                        ++l_free_ent;
                    }
                    l_oldcode = incode;
                    if (len == 0) {
                        this.n_bits = l_n_bits;
                        this.maxcode = l_maxcode;
                        this.bitmask = l_bitmask;
                        this.oldcode = l_oldcode;
                        this.finchar = l_finchar;
                        this.stackp = l_stackp;
                        this.free_ent = l_free_ent;
                        this.bit_pos = l_bit_pos;
                        return off - start;
                    }
                    continue;
                }
            }
            l_bit_pos = this.resetbuf(l_bit_pos);
        } while (this.got > 0);
        this.n_bits = l_n_bits;
        this.maxcode = l_maxcode;
        this.bitmask = l_bitmask;
        this.oldcode = l_oldcode;
        this.finchar = l_finchar;
        this.stackp = l_stackp;
        this.free_ent = l_free_ent;
        this.bit_pos = l_bit_pos;
        this.eof = true;
        return off - start;
    }
    
    private final int resetbuf(final int bit_pos) {
        final int pos = bit_pos >> 3;
        System.arraycopy(this.data, pos, this.data, 0, this.end - pos);
        this.end -= pos;
        return 0;
    }
    
    private final void fill() throws IOException {
        this.got = super.in.read(this.data, this.end, this.data.length - 1 - this.end);
        if (this.got > 0) {
            this.end += this.got;
        }
    }
    
    public synchronized long skip(final long num) throws IOException {
        final byte[] tmp = new byte[(int)num];
        final int got = this.read(tmp, 0, (int)num);
        if (got > 0) {
            return got;
        }
        return 0L;
    }
    
    public synchronized int available() throws IOException {
        if (this.eof) {
            return 0;
        }
        return super.in.available();
    }
    
    private void parse_header() throws IOException {
        int t = super.in.read();
        if (t < 0) {
            throw new EOFException("Failed to read magic number");
        }
        int magic = (t & 0xFF) << 8;
        t = super.in.read();
        if (t < 0) {
            throw new EOFException("Failed to read magic number");
        }
        magic += (t & 0xFF);
        if (magic != 8093) {
            throw new IOException("Input not in compress format (read magic number 0x" + Integer.toHexString(magic) + ")");
        }
        final int header = super.in.read();
        if (header < 0) {
            throw new EOFException("Failed to read header");
        }
        this.block_mode = ((header & 0x80) > 0);
        this.maxbits = (header & 0x1F);
        if (this.maxbits > 16) {
            throw new IOException("Stream compressed with " + this.maxbits + " bits, but can only handle " + 16 + " bits");
        }
        if ((header & 0x20) > 0) {
            throw new IOException("Header extension bit set");
        }
        if ((header & 0x40) > 0) {
            throw new IOException("Header bit 6 set");
        }
        this.maxmaxcode = 1 << this.maxbits;
        this.n_bits = 9;
        this.maxcode = (1 << this.n_bits) - 1;
        this.bitmask = this.maxcode;
        this.oldcode = -1;
        this.finchar = 0;
        this.free_ent = (this.block_mode ? 257 : 256);
        this.tab_prefix = new int[1 << this.maxbits];
        this.tab_suffix = new byte[1 << this.maxbits];
        this.stack = new byte[1 << this.maxbits];
        this.stackp = this.stack.length;
        for (int idx = 255; idx >= 0; --idx) {
            this.tab_suffix[idx] = (byte)idx;
        }
    }
    
    public static void main(final String[] args) throws Exception {
        if (args.length != 1) {
            System.err.println("Usage: UncompressInputStream <file>");
            System.exit(1);
        }
        final InputStream in = new UncompressInputStream(new FileInputStream(args[0]));
        final byte[] buf = new byte[100000];
        int tot = 0;
        final long beg = System.currentTimeMillis();
        while (true) {
            final int read = in.read(buf);
            if (read < 0) {
                break;
            }
            System.out.write(buf, 0, read);
            tot += read;
        }
        final long end = System.currentTimeMillis();
        System.err.println("Decompressed " + tot + " bytes");
        System.err.println("Time: " + (end - beg) / 1000.0 + " seconds");
    }
}
