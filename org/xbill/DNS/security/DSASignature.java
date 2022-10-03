package org.xbill.DNS.security;

import java.util.Arrays;
import java.security.SignatureException;
import java.security.interfaces.DSAParams;

public class DSASignature
{
    static final int ASN1_SEQ = 48;
    static final int ASN1_INT = 2;
    
    private DSASignature() {
    }
    
    public static byte[] fromDNS(final byte[] sig) {
        final int len = 20;
        int n = 0;
        byte rlen = 20;
        if (sig[1] < 0) {
            ++rlen;
        }
        byte slen = 20;
        if (sig[21] < 0) {
            ++slen;
        }
        final byte seqlen = (byte)(rlen + slen + 4);
        final byte[] array = new byte[seqlen + 2];
        array[n++] = 48;
        array[n++] = seqlen;
        array[n++] = 2;
        if ((array[n++] = rlen) > 20) {
            array[n++] = 0;
        }
        for (int i = 0; i < 20; ++i, ++n) {
            array[n] = sig[1 + i];
        }
        array[n++] = 2;
        if ((array[n++] = slen) > 20) {
            array[n++] = 0;
        }
        for (int i = 0; i < 20; ++i, ++n) {
            array[n] = sig[21 + i];
        }
        return array;
    }
    
    public static byte[] toDNS(final DSAParams params, final byte[] sig) throws SignatureException {
        if (sig[0] != 48 || sig[2] != 2) {
            throw new SignatureException("Expected SEQ, INT");
        }
        int rLength = sig[3];
        int rOffset = 4;
        if (sig[rOffset] == 0) {
            --rLength;
            ++rOffset;
        }
        if (sig[rOffset + rLength] != 2) {
            throw new SignatureException("Expected INT");
        }
        int sLength = sig[rOffset + rLength + 1];
        int sOffset = rOffset + rLength + 2;
        if (sig[sOffset] == 0) {
            --sLength;
            ++sOffset;
        }
        if (rLength > 20 || sLength > 20) {
            throw new SignatureException("DSA R/S too long");
        }
        final byte[] newSig = new byte[41];
        Arrays.fill(newSig, (byte)0);
        newSig[0] = (byte)((params.getP().bitLength() - 512) / 64);
        System.arraycopy(sig, rOffset, newSig, 1 + (20 - rLength), rLength);
        System.arraycopy(sig, sOffset, newSig, 21 + (20 - sLength), sLength);
        return newSig;
    }
}
