package org.bouncycastle.asn1.x9;

import java.math.BigInteger;
import org.bouncycastle.math.ec.ECFieldElement;
import org.bouncycastle.math.ec.ECCurve;

public class X9IntegerConverter
{
    public int getByteLength(final ECCurve ecCurve) {
        return (ecCurve.getFieldSize() + 7) / 8;
    }
    
    public int getByteLength(final ECFieldElement ecFieldElement) {
        return (ecFieldElement.getFieldSize() + 7) / 8;
    }
    
    public byte[] integerToBytes(final BigInteger bigInteger, final int n) {
        final byte[] byteArray = bigInteger.toByteArray();
        if (n < byteArray.length) {
            final byte[] array = new byte[n];
            System.arraycopy(byteArray, byteArray.length - array.length, array, 0, array.length);
            return array;
        }
        if (n > byteArray.length) {
            final byte[] array2 = new byte[n];
            System.arraycopy(byteArray, 0, array2, array2.length - byteArray.length, byteArray.length);
            return array2;
        }
        return byteArray;
    }
}
