package org.apache.poi.util;

import java.util.Arrays;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.io.InputStream;
import java.io.FilterInputStream;

@Internal
public class ReplacingInputStream extends FilterInputStream
{
    final int[] buf;
    private int matchedIndex;
    private int unbufferIndex;
    private int replacedIndex;
    private final byte[] pattern;
    private final byte[] replacement;
    private State state;
    
    public ReplacingInputStream(final InputStream in, final String pattern, final String replacement) {
        this(in, pattern.getBytes(StandardCharsets.UTF_8), (byte[])((replacement == null) ? null : replacement.getBytes(StandardCharsets.UTF_8)));
    }
    
    public ReplacingInputStream(final InputStream in, final byte[] pattern, final byte[] replacement) {
        super(in);
        this.state = State.NOT_MATCHED;
        if (pattern == null || pattern.length == 0) {
            throw new IllegalArgumentException("pattern length should be > 0");
        }
        this.pattern = pattern;
        this.replacement = replacement;
        this.buf = new int[pattern.length];
    }
    
    @Override
    public int read(final byte[] b, final int off, final int len) throws IOException {
        if (b == null) {
            throw new NullPointerException();
        }
        if (off < 0 || len < 0 || len > b.length - off) {
            throw new IndexOutOfBoundsException();
        }
        if (len == 0) {
            return 0;
        }
        int c = this.read();
        if (c == -1) {
            return -1;
        }
        b[off] = (byte)c;
        int i;
        for (i = 1; i < len; ++i) {
            c = this.read();
            if (c == -1) {
                break;
            }
            b[off + i] = (byte)c;
        }
        return i;
    }
    
    @Override
    public int read(final byte[] b) throws IOException {
        return this.read(b, 0, b.length);
    }
    
    @Override
    public int read() throws IOException {
        switch (this.state) {
            default: {
                final int next = super.read();
                if (this.pattern[0] != next) {
                    return next;
                }
                Arrays.fill(this.buf, 0);
                this.matchedIndex = 0;
                this.buf[this.matchedIndex++] = next;
                if (this.pattern.length == 1) {
                    this.state = State.REPLACING;
                    this.replacedIndex = 0;
                }
                else {
                    this.state = State.MATCHING;
                }
                return this.read();
            }
            case MATCHING: {
                final int next = super.read();
                if (this.pattern[this.matchedIndex] == next) {
                    this.buf[this.matchedIndex++] = next;
                    if (this.matchedIndex == this.pattern.length) {
                        if (this.replacement == null || this.replacement.length == 0) {
                            this.state = State.NOT_MATCHED;
                            this.matchedIndex = 0;
                        }
                        else {
                            this.state = State.REPLACING;
                            this.replacedIndex = 0;
                        }
                    }
                }
                else {
                    this.buf[this.matchedIndex++] = next;
                    this.state = State.UNBUFFER;
                    this.unbufferIndex = 0;
                }
                return this.read();
            }
            case REPLACING: {
                final int next = this.replacement[this.replacedIndex++];
                if (this.replacedIndex == this.replacement.length) {
                    this.state = State.NOT_MATCHED;
                    this.replacedIndex = 0;
                }
                return next;
            }
            case UNBUFFER: {
                final int next = this.buf[this.unbufferIndex++];
                if (this.unbufferIndex == this.matchedIndex) {
                    this.state = State.NOT_MATCHED;
                    this.matchedIndex = 0;
                }
                return next;
            }
        }
    }
    
    @Override
    public String toString() {
        return this.state.name() + " " + this.matchedIndex + " " + this.replacedIndex + " " + this.unbufferIndex;
    }
    
    private enum State
    {
        NOT_MATCHED, 
        MATCHING, 
        REPLACING, 
        UNBUFFER;
    }
}
