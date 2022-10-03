package sun.text;

public final class CompactByteArray implements Cloneable
{
    public static final int UNICODECOUNT = 65536;
    private static final int BLOCKSHIFT = 7;
    private static final int BLOCKCOUNT = 128;
    private static final int INDEXSHIFT = 9;
    private static final int INDEXCOUNT = 512;
    private static final int BLOCKMASK = 127;
    private byte[] values;
    private short[] indices;
    private boolean isCompact;
    private int[] hashes;
    
    public CompactByteArray(final byte b) {
        this.values = new byte[65536];
        this.indices = new short[512];
        this.hashes = new int[512];
        for (int i = 0; i < 65536; ++i) {
            this.values[i] = b;
        }
        for (int j = 0; j < 512; ++j) {
            this.indices[j] = (short)(j << 7);
            this.hashes[j] = 0;
        }
        this.isCompact = false;
    }
    
    public CompactByteArray(final short[] indices, final byte[] values) {
        if (indices.length != 512) {
            throw new IllegalArgumentException("Index out of bounds!");
        }
        for (int i = 0; i < 512; ++i) {
            final short n = indices[i];
            if (n < 0 || n >= values.length + 128) {
                throw new IllegalArgumentException("Index out of bounds!");
            }
        }
        this.indices = indices;
        this.values = values;
        this.isCompact = true;
    }
    
    public byte elementAt(final char c) {
        return this.values[(this.indices[c >> 7] & 0xFFFF) + (c & '\u007f')];
    }
    
    public void setElementAt(final char c, final byte b) {
        if (this.isCompact) {
            this.expand();
        }
        this.touchBlock(c >> 7, this.values[c] = b);
    }
    
    public void setElementAt(final char c, final char c2, final byte b) {
        if (this.isCompact) {
            this.expand();
        }
        for (char c3 = c; c3 <= c2; ++c3) {
            this.touchBlock(c3 >> 7, this.values[c3] = b);
        }
    }
    
    public void compact() {
        if (!this.isCompact) {
            int n = 0;
            int n2 = 0;
            short n3 = -1;
            for (int i = 0; i < this.indices.length; ++i, n2 += 128) {
                this.indices[i] = -1;
                final boolean blockTouched = this.blockTouched(i);
                if (!blockTouched && n3 != -1) {
                    this.indices[i] = n3;
                }
                else {
                    int n4;
                    int j;
                    for (n4 = 0, j = 0; j < n; ++j, n4 += 128) {
                        if (this.hashes[i] == this.hashes[j] && arrayRegionMatches(this.values, n2, this.values, n4, 128)) {
                            this.indices[i] = (short)n4;
                            break;
                        }
                    }
                    if (this.indices[i] == -1) {
                        System.arraycopy(this.values, n2, this.values, n4, 128);
                        this.indices[i] = (short)n4;
                        this.hashes[j] = this.hashes[i];
                        ++n;
                        if (!blockTouched) {
                            n3 = (short)n4;
                        }
                    }
                }
            }
            final int n5 = n * 128;
            final byte[] values = new byte[n5];
            System.arraycopy(this.values, 0, values, 0, n5);
            this.values = values;
            this.isCompact = true;
            this.hashes = null;
        }
    }
    
    static final boolean arrayRegionMatches(final byte[] array, final int n, final byte[] array2, final int n2, final int n3) {
        final int n4 = n + n3;
        final int n5 = n2 - n;
        for (int i = n; i < n4; ++i) {
            if (array[i] != array2[i + n5]) {
                return false;
            }
        }
        return true;
    }
    
    private final void touchBlock(final int n, final int n2) {
        this.hashes[n] = (this.hashes[n] + (n2 << 1) | 0x1);
    }
    
    private final boolean blockTouched(final int n) {
        return this.hashes[n] != 0;
    }
    
    public short[] getIndexArray() {
        return this.indices;
    }
    
    public byte[] getStringArray() {
        return this.values;
    }
    
    public Object clone() {
        try {
            final CompactByteArray compactByteArray = (CompactByteArray)super.clone();
            compactByteArray.values = this.values.clone();
            compactByteArray.indices = this.indices.clone();
            if (this.hashes != null) {
                compactByteArray.hashes = this.hashes.clone();
            }
            return compactByteArray;
        }
        catch (final CloneNotSupportedException ex) {
            throw new InternalError(ex);
        }
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == null) {
            return false;
        }
        if (this == o) {
            return true;
        }
        if (this.getClass() != o.getClass()) {
            return false;
        }
        final CompactByteArray compactByteArray = (CompactByteArray)o;
        for (int i = 0; i < 65536; ++i) {
            if (this.elementAt((char)i) != compactByteArray.elementAt((char)i)) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public int hashCode() {
        int n = 0;
        for (int min = Math.min(3, this.values.length / 16), i = 0; i < this.values.length; i += min) {
            n = n * 37 + this.values[i];
        }
        return n;
    }
    
    private void expand() {
        if (this.isCompact) {
            this.hashes = new int[512];
            final byte[] values = new byte[65536];
            for (int i = 0; i < 65536; ++i) {
                this.touchBlock(i >> 7, values[i] = this.elementAt((char)i));
            }
            for (int j = 0; j < 512; ++j) {
                this.indices[j] = (short)(j << 7);
            }
            this.values = null;
            this.values = values;
            this.isCompact = false;
        }
    }
    
    private byte[] getArray() {
        return this.values;
    }
}
