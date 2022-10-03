package com.sun.org.apache.xalan.internal.xsltc.dom;

import java.io.ObjectInput;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.Externalizable;

public class BitArray implements Externalizable
{
    static final long serialVersionUID = -4876019880708377663L;
    private int[] _bits;
    private int _bitSize;
    private int _intSize;
    private int _mask;
    private static final int[] _masks;
    private static final boolean DEBUG_ASSERTIONS = false;
    private int _pos;
    private int _node;
    private int _int;
    private int _bit;
    int _first;
    int _last;
    
    public BitArray() {
        this(32);
    }
    
    public BitArray(int size) {
        this._pos = Integer.MAX_VALUE;
        this._node = 0;
        this._int = 0;
        this._bit = 0;
        this._first = Integer.MAX_VALUE;
        this._last = Integer.MIN_VALUE;
        if (size < 32) {
            size = 32;
        }
        this._bitSize = size;
        this._intSize = (this._bitSize >>> 5) + 1;
        this._bits = new int[this._intSize + 1];
    }
    
    public BitArray(int size, final int[] bits) {
        this._pos = Integer.MAX_VALUE;
        this._node = 0;
        this._int = 0;
        this._bit = 0;
        this._first = Integer.MAX_VALUE;
        this._last = Integer.MIN_VALUE;
        if (size < 32) {
            size = 32;
        }
        this._bitSize = size;
        this._intSize = (this._bitSize >>> 5) + 1;
        this._bits = bits;
    }
    
    public void setMask(final int mask) {
        this._mask = mask;
    }
    
    public int getMask() {
        return this._mask;
    }
    
    public final int size() {
        return this._bitSize;
    }
    
    public final boolean getBit(final int bit) {
        return (this._bits[bit >>> 5] & BitArray._masks[bit % 32]) != 0x0;
    }
    
    public final int getNextBit(int startBit) {
        for (int i = startBit >>> 5; i <= this._intSize; ++i) {
            final int bits = this._bits[i];
            if (bits != 0) {
                for (int b = startBit % 32; b < 32; ++b) {
                    if ((bits & BitArray._masks[b]) != 0x0) {
                        return (i << 5) + b;
                    }
                }
            }
            startBit = 0;
        }
        return -1;
    }
    
    public final int getBitNumber(final int pos) {
        if (pos == this._pos) {
            return this._node;
        }
        if (pos < this._pos) {
            final int int1 = 0;
            this._pos = int1;
            this._bit = int1;
            this._int = int1;
        }
        while (this._int <= this._intSize) {
            final int bits = this._bits[this._int];
            if (bits != 0) {
                while (this._bit < 32) {
                    if ((bits & BitArray._masks[this._bit]) != 0x0 && ++this._pos == pos) {
                        return this._node = (this._int << 5) + this._bit - 1;
                    }
                    ++this._bit;
                }
                this._bit = 0;
            }
            ++this._int;
        }
        return 0;
    }
    
    public final int[] data() {
        return this._bits;
    }
    
    public final void setBit(final int bit) {
        if (bit >= this._bitSize) {
            return;
        }
        final int i = bit >>> 5;
        if (i < this._first) {
            this._first = i;
        }
        if (i > this._last) {
            this._last = i;
        }
        final int[] bits = this._bits;
        final int n = i;
        bits[n] |= BitArray._masks[bit % 32];
    }
    
    public final BitArray merge(final BitArray other) {
        if (this._last == -1) {
            this._bits = other._bits;
        }
        else if (other._last != -1) {
            final int start = (this._first < other._first) ? this._first : other._first;
            int stop = (this._last > other._last) ? this._last : other._last;
            if (other._intSize > this._intSize) {
                if (stop > this._intSize) {
                    stop = this._intSize;
                }
                for (int i = start; i <= stop; ++i) {
                    final int[] bits = other._bits;
                    final int n = i;
                    bits[n] |= this._bits[i];
                }
                this._bits = other._bits;
            }
            else {
                if (stop > other._intSize) {
                    stop = other._intSize;
                }
                for (int i = start; i <= stop; ++i) {
                    final int[] bits2 = this._bits;
                    final int n2 = i;
                    bits2[n2] |= other._bits[i];
                }
            }
        }
        return this;
    }
    
    public final void resize(final int newSize) {
        if (newSize > this._bitSize) {
            this._intSize = (newSize >>> 5) + 1;
            final int[] newBits = new int[this._intSize + 1];
            System.arraycopy(this._bits, 0, newBits, 0, (this._bitSize >>> 5) + 1);
            this._bits = newBits;
            this._bitSize = newSize;
        }
    }
    
    public BitArray cloneArray() {
        return new BitArray(this._intSize, this._bits);
    }
    
    @Override
    public void writeExternal(final ObjectOutput out) throws IOException {
        out.writeInt(this._bitSize);
        out.writeInt(this._mask);
        out.writeObject(this._bits);
        out.flush();
    }
    
    @Override
    public void readExternal(final ObjectInput in) throws IOException, ClassNotFoundException {
        this._bitSize = in.readInt();
        this._intSize = (this._bitSize >>> 5) + 1;
        this._mask = in.readInt();
        this._bits = (int[])in.readObject();
    }
    
    static {
        _masks = new int[] { Integer.MIN_VALUE, 1073741824, 536870912, 268435456, 134217728, 67108864, 33554432, 16777216, 8388608, 4194304, 2097152, 1048576, 524288, 262144, 131072, 65536, 32768, 16384, 8192, 4096, 2048, 1024, 512, 256, 128, 64, 32, 16, 8, 4, 2, 1 };
    }
}
