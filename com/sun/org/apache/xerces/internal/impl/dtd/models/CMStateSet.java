package com.sun.org.apache.xerces.internal.impl.dtd.models;

public class CMStateSet
{
    int fBitCount;
    int fByteCount;
    int fBits1;
    int fBits2;
    byte[] fByteArray;
    
    public CMStateSet(final int bitCount) {
        this.fBitCount = bitCount;
        if (this.fBitCount < 0) {
            throw new RuntimeException("ImplementationMessages.VAL_CMSI");
        }
        if (this.fBitCount > 64) {
            this.fByteCount = this.fBitCount / 8;
            if (this.fBitCount % 8 != 0) {
                ++this.fByteCount;
            }
            this.fByteArray = new byte[this.fByteCount];
        }
        this.zeroBits();
    }
    
    @Override
    public String toString() {
        final StringBuffer strRet = new StringBuffer();
        try {
            strRet.append("{");
            for (int index = 0; index < this.fBitCount; ++index) {
                if (this.getBit(index)) {
                    strRet.append(" " + index);
                }
            }
            strRet.append(" }");
        }
        catch (final RuntimeException ex) {}
        return strRet.toString();
    }
    
    public final void intersection(final CMStateSet setToAnd) {
        if (this.fBitCount < 65) {
            this.fBits1 &= setToAnd.fBits1;
            this.fBits2 &= setToAnd.fBits2;
        }
        else {
            for (int index = this.fByteCount - 1; index >= 0; --index) {
                final byte[] fByteArray = this.fByteArray;
                final int n = index;
                fByteArray[n] &= setToAnd.fByteArray[index];
            }
        }
    }
    
    public final boolean getBit(final int bitToGet) {
        if (bitToGet >= this.fBitCount) {
            throw new RuntimeException("ImplementationMessages.VAL_CMSI");
        }
        if (this.fBitCount >= 65) {
            final byte mask = (byte)(1 << bitToGet % 8);
            final int ofs = bitToGet >> 3;
            return (this.fByteArray[ofs] & mask) != 0x0;
        }
        final int mask2 = 1 << bitToGet % 32;
        if (bitToGet < 32) {
            return (this.fBits1 & mask2) != 0x0;
        }
        return (this.fBits2 & mask2) != 0x0;
    }
    
    public final boolean isEmpty() {
        if (this.fBitCount < 65) {
            return this.fBits1 == 0 && this.fBits2 == 0;
        }
        for (int index = this.fByteCount - 1; index >= 0; --index) {
            if (this.fByteArray[index] != 0) {
                return false;
            }
        }
        return true;
    }
    
    final boolean isSameSet(final CMStateSet setToCompare) {
        if (this.fBitCount != setToCompare.fBitCount) {
            return false;
        }
        if (this.fBitCount < 65) {
            return this.fBits1 == setToCompare.fBits1 && this.fBits2 == setToCompare.fBits2;
        }
        for (int index = this.fByteCount - 1; index >= 0; --index) {
            if (this.fByteArray[index] != setToCompare.fByteArray[index]) {
                return false;
            }
        }
        return true;
    }
    
    public final void union(final CMStateSet setToOr) {
        if (this.fBitCount < 65) {
            this.fBits1 |= setToOr.fBits1;
            this.fBits2 |= setToOr.fBits2;
        }
        else {
            for (int index = this.fByteCount - 1; index >= 0; --index) {
                final byte[] fByteArray = this.fByteArray;
                final int n = index;
                fByteArray[n] |= setToOr.fByteArray[index];
            }
        }
    }
    
    public final void setBit(final int bitToSet) {
        if (bitToSet >= this.fBitCount) {
            throw new RuntimeException("ImplementationMessages.VAL_CMSI");
        }
        if (this.fBitCount < 65) {
            final int mask = 1 << bitToSet % 32;
            if (bitToSet < 32) {
                this.fBits1 &= ~mask;
                this.fBits1 |= mask;
            }
            else {
                this.fBits2 &= ~mask;
                this.fBits2 |= mask;
            }
        }
        else {
            final byte mask2 = (byte)(1 << bitToSet % 8);
            final int ofs = bitToSet >> 3;
            final byte[] fByteArray = this.fByteArray;
            final int n = ofs;
            fByteArray[n] &= (byte)~mask2;
            final byte[] fByteArray2 = this.fByteArray;
            final int n2 = ofs;
            fByteArray2[n2] |= mask2;
        }
    }
    
    public final void setTo(final CMStateSet srcSet) {
        if (this.fBitCount != srcSet.fBitCount) {
            throw new RuntimeException("ImplementationMessages.VAL_CMSI");
        }
        if (this.fBitCount < 65) {
            this.fBits1 = srcSet.fBits1;
            this.fBits2 = srcSet.fBits2;
        }
        else {
            for (int index = this.fByteCount - 1; index >= 0; --index) {
                this.fByteArray[index] = srcSet.fByteArray[index];
            }
        }
    }
    
    public final void zeroBits() {
        if (this.fBitCount < 65) {
            this.fBits1 = 0;
            this.fBits2 = 0;
        }
        else {
            for (int index = this.fByteCount - 1; index >= 0; --index) {
                this.fByteArray[index] = 0;
            }
        }
    }
    
    @Override
    public boolean equals(final Object o) {
        return o instanceof CMStateSet && this.isSameSet((CMStateSet)o);
    }
    
    @Override
    public int hashCode() {
        if (this.fBitCount < 65) {
            return this.fBits1 + this.fBits2 * 31;
        }
        int hash = 0;
        for (int index = this.fByteCount - 1; index >= 0; --index) {
            hash = this.fByteArray[index] + hash * 31;
        }
        return hash;
    }
}
