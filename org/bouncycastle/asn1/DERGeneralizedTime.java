package org.bouncycastle.asn1;

import java.io.IOException;
import org.bouncycastle.util.Strings;
import java.util.Date;

public class DERGeneralizedTime extends ASN1GeneralizedTime
{
    public DERGeneralizedTime(final byte[] array) {
        super(array);
    }
    
    public DERGeneralizedTime(final Date date) {
        super(date);
    }
    
    public DERGeneralizedTime(final String s) {
        super(s);
    }
    
    private byte[] getDERTime() {
        if (this.time[this.time.length - 1] != 90) {
            return this.time;
        }
        if (!this.hasMinutes()) {
            final byte[] array = new byte[this.time.length + 4];
            System.arraycopy(this.time, 0, array, 0, this.time.length - 1);
            System.arraycopy(Strings.toByteArray("0000Z"), 0, array, this.time.length - 1, 5);
            return array;
        }
        if (!this.hasSeconds()) {
            final byte[] array2 = new byte[this.time.length + 2];
            System.arraycopy(this.time, 0, array2, 0, this.time.length - 1);
            System.arraycopy(Strings.toByteArray("00Z"), 0, array2, this.time.length - 1, 3);
            return array2;
        }
        if (!this.hasFractionalSeconds()) {
            return this.time;
        }
        int n;
        for (n = this.time.length - 2; n > 0 && this.time[n] == 48; --n) {}
        if (this.time[n] == 46) {
            final byte[] array3 = new byte[n + 1];
            System.arraycopy(this.time, 0, array3, 0, n);
            array3[n] = 90;
            return array3;
        }
        final byte[] array4 = new byte[n + 2];
        System.arraycopy(this.time, 0, array4, 0, n + 1);
        array4[n + 1] = 90;
        return array4;
    }
    
    @Override
    int encodedLength() {
        final int length = this.getDERTime().length;
        return 1 + StreamUtil.calculateBodyLength(length) + length;
    }
    
    @Override
    void encode(final ASN1OutputStream asn1OutputStream) throws IOException {
        asn1OutputStream.writeEncoded(24, this.getDERTime());
    }
}
