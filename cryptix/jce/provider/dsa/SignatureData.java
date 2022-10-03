package cryptix.jce.provider.dsa;

import java.security.SignatureException;
import java.math.BigInteger;

class SignatureData
{
    private BigInteger r;
    private BigInteger s;
    
    public BigInteger getR() {
        return this.r;
    }
    
    public BigInteger getS() {
        return this.s;
    }
    
    public byte[] getData() {
        final byte[] rdata = this.r.toByteArray();
        final byte[] sdata = this.s.toByteArray();
        final byte[] data = new byte[6 + rdata.length + sdata.length];
        int i = 0;
        data[i++] = 48;
        data[i++] = (byte)(data.length - 2);
        data[i++] = 2;
        data[i++] = (byte)rdata.length;
        for (int j = 0; j < rdata.length; ++j) {
            data[i++] = rdata[j];
        }
        data[i++] = 2;
        data[i++] = (byte)sdata.length;
        for (int j = 0; j < sdata.length; ++j) {
            data[i++] = sdata[j];
        }
        return data;
    }
    
    public SignatureData(final byte[] data) throws SignatureException {
        try {
            int i = 0;
            if (data[i++] != 48 || data[i++] != data.length - 2 || data[i++] != 2) {
                throw new SignatureException("Corrupted signature data");
            }
            byte len = data[i++];
            if (len > 21) {
                throw new SignatureException("Corrupted signature data");
            }
            final byte[] rdata = new byte[len];
            for (int j = 0; j < len; ++j) {
                rdata[j] = data[i++];
            }
            if (data[i++] != 2) {
                throw new SignatureException("Corrupted signature data");
            }
            len = data[i++];
            if (len > 21) {
                throw new SignatureException("Corrupted signature data");
            }
            final byte[] sdata = new byte[len];
            for (int k = 0; k < len; ++k) {
                sdata[k] = data[i++];
            }
            this.r = new BigInteger(rdata);
            this.s = new BigInteger(sdata);
            if (i != data.length || this.r.signum() != 1 || this.s.signum() != 1) {
                throw new SignatureException("Corrupted signature data");
            }
        }
        catch (final NullPointerException ex) {
            throw new SignatureException("Corrupted signature data");
        }
        catch (final ArrayIndexOutOfBoundsException aioobe) {
            throw new SignatureException("Corrupted signature data");
        }
    }
    
    public SignatureData(final BigInteger r, final BigInteger s) throws SignatureException {
        if (r == null || s == null) {
            throw new SignatureException("Invalid signature");
        }
        if (r.signum() != 1 || s.signum() != 1) {
            throw new SignatureException("Invalid signature");
        }
        this.r = r;
        this.s = s;
    }
}
