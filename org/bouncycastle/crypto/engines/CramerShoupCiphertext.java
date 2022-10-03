package org.bouncycastle.crypto.engines;

import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Pack;
import java.math.BigInteger;

public class CramerShoupCiphertext
{
    BigInteger u1;
    BigInteger u2;
    BigInteger e;
    BigInteger v;
    
    public CramerShoupCiphertext() {
    }
    
    public CramerShoupCiphertext(final BigInteger u1, final BigInteger u2, final BigInteger e, final BigInteger v) {
        this.u1 = u1;
        this.u2 = u2;
        this.e = e;
        this.v = v;
    }
    
    public CramerShoupCiphertext(final byte[] array) {
        int n = 0;
        final int bigEndianToInt = Pack.bigEndianToInt(array, n);
        n += 4;
        final byte[] copyOfRange = Arrays.copyOfRange(array, n, n + bigEndianToInt);
        int n2 = n + bigEndianToInt;
        this.u1 = new BigInteger(copyOfRange);
        final int bigEndianToInt2 = Pack.bigEndianToInt(array, n2);
        n2 += 4;
        final byte[] copyOfRange2 = Arrays.copyOfRange(array, n2, n2 + bigEndianToInt2);
        int n3 = n2 + bigEndianToInt2;
        this.u2 = new BigInteger(copyOfRange2);
        final int bigEndianToInt3 = Pack.bigEndianToInt(array, n3);
        n3 += 4;
        final byte[] copyOfRange3 = Arrays.copyOfRange(array, n3, n3 + bigEndianToInt3);
        int n4 = n3 + bigEndianToInt3;
        this.e = new BigInteger(copyOfRange3);
        final int bigEndianToInt4 = Pack.bigEndianToInt(array, n4);
        n4 += 4;
        this.v = new BigInteger(Arrays.copyOfRange(array, n4, n4 + bigEndianToInt4));
    }
    
    public BigInteger getU1() {
        return this.u1;
    }
    
    public void setU1(final BigInteger u1) {
        this.u1 = u1;
    }
    
    public BigInteger getU2() {
        return this.u2;
    }
    
    public void setU2(final BigInteger u2) {
        this.u2 = u2;
    }
    
    public BigInteger getE() {
        return this.e;
    }
    
    public void setE(final BigInteger e) {
        this.e = e;
    }
    
    public BigInteger getV() {
        return this.v;
    }
    
    public void setV(final BigInteger v) {
        this.v = v;
    }
    
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        sb.append("u1: " + this.u1.toString());
        sb.append("\nu2: " + this.u2.toString());
        sb.append("\ne: " + this.e.toString());
        sb.append("\nv: " + this.v.toString());
        return sb.toString();
    }
    
    public byte[] toByteArray() {
        final byte[] byteArray = this.u1.toByteArray();
        final int length = byteArray.length;
        final byte[] byteArray2 = this.u2.toByteArray();
        final int length2 = byteArray2.length;
        final byte[] byteArray3 = this.e.toByteArray();
        final int length3 = byteArray3.length;
        final byte[] byteArray4 = this.v.toByteArray();
        final int length4 = byteArray4.length;
        int n = 0;
        final byte[] array = new byte[length + length2 + length3 + length4 + 16];
        Pack.intToBigEndian(length, array, n);
        n += 4;
        System.arraycopy(byteArray, 0, array, n, length);
        int n2 = n + length;
        Pack.intToBigEndian(length2, array, n2);
        n2 += 4;
        System.arraycopy(byteArray2, 0, array, n2, length2);
        int n3 = n2 + length2;
        Pack.intToBigEndian(length3, array, n3);
        n3 += 4;
        System.arraycopy(byteArray3, 0, array, n3, length3);
        int n4 = n3 + length3;
        Pack.intToBigEndian(length4, array, n4);
        n4 += 4;
        System.arraycopy(byteArray4, 0, array, n4, length4);
        return array;
    }
}
