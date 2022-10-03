package cryptix.jce.provider.asn;

import java.io.IOException;

public final class AsnObjectId extends AsnObject
{
    private static final int[] _rsaEncryption;
    private static final int[] _md4WithRSAEncryption;
    private static final int[] _md5WithRSAEncryption;
    private static final int[] _sha1WithRSAEncryption;
    private static final int[] _rsaOAEPEncryptionSET;
    private static final int[] _id_hmacWithSHA1;
    private static final int[] _rc2_CBC;
    private static final int[] _des_EDE3_CBC;
    private static final int[] _rc5_CBC_PAD;
    private static final int[] _pbeWithMD2AndDES_CBC;
    private static final int[] _pbeWithMD5AndDES_CBC;
    private static final int[] _pbeWithMD2AndRC2_CBC;
    private static final int[] _pbeWithMD5AndRC2_CBC;
    private static final int[] _pbeWithSHA1AndDES_CBC;
    private static final int[] _pbeWithSHA1AndRC2_CBC;
    private static final int[] _id_PBKDF2;
    private static final int[] _id_PBES2;
    private static final int[] _id_PBMAC1;
    public static final AsnObjectId OID_rsaEncryption;
    public static final AsnObjectId OID_md5WithRSAEncryption;
    private final int[] components;
    
    private static int[] decodePayload(final byte[] payload) {
        int compCount = 2;
        for (int i = 1; i < payload.length; ++i) {
            if ((payload[i] & 0x80) == 0x0) {
                ++compCount;
            }
        }
        final int[] comps = new int[compCount];
        comps[0] = payload[0] / 40;
        comps[1] = payload[0] % 40;
        int payloadOff = 1;
        for (int j = 2; j < comps.length; ++j) {
            int c = 0;
            byte b;
            do {
                b = payload[payloadOff++];
                c = (c << 7 | (b & 0x7F));
            } while ((b & 0x80) == 0x80);
            comps[j] = c;
        }
        return comps;
    }
    
    public String toString(final String indent) {
        String res = "OBJECT ID { ";
        for (int i = 0; i < this.components.length; ++i) {
            res = res + this.components[i] + " ";
        }
        return indent + res + "}";
    }
    
    public static void main(final String[] argv) throws Exception {
        final int[] comps = { 1, 2, 840 };
        final AsnObjectId o = new AsnObjectId(comps);
        final AsnOutputStream aos = new AsnOutputStream();
        aos.write(o);
        final byte[] enc = aos.toByteArray();
        final AsnInputStream ais = new AsnInputStream(enc);
        final AsnObject oo = ais.read();
        System.out.println(oo);
    }
    
    protected void encodePayload(final AsnOutputStream os) throws IOException {
        os.writeByte((byte)(40 * this.components[0] + this.components[1]));
        for (int i = 2; i < this.components.length; ++i) {
            this.writeComponent(os, this.components[i]);
        }
    }
    
    protected int getEncodedLengthOfPayload(final AsnOutputStream os) {
        int len = 1;
        for (int i = 2; i < this.components.length; ++i) {
            len += getEncodedComponentLen(this.components[i]);
        }
        return len;
    }
    
    private void writeComponent(final AsnOutputStream os, final int c) throws IOException {
        final int len = getEncodedComponentLen(c);
        for (int i = len - 1; i > 0; --i) {
            os.writeByte((byte)(c >>> i * 7 | 0x80));
        }
        os.writeByte((byte)(c & 0x7F));
    }
    
    private static int getEncodedComponentLen(final int c) {
        if (c < 0) {
            throw new IllegalArgumentException("c: < 0");
        }
        if (c <= 127) {
            return 1;
        }
        if (c <= 16383) {
            return 2;
        }
        if (c <= 2097151) {
            return 3;
        }
        if (c <= 268435455) {
            return 4;
        }
        return 5;
    }
    
    public AsnObjectId(final AsnInputStream is) throws IOException {
        super((byte)6);
        final int len = is.readLength();
        if (len < 2) {
            throw new IOException("Invalid OBJECT_ID.");
        }
        final byte[] payload = is.readBytes(len);
        this.components = decodePayload(payload);
    }
    
    public AsnObjectId(final int[] components) {
        super((byte)6);
        if (components.length < 2) {
            throw new IllegalArgumentException("Less than 2 components.");
        }
        if (components[0] < 0 || components[0] > 2) {
            throw new IllegalArgumentException("First comp must be 0, 1 or 2.");
        }
        if (components[1] >= components[0] * 40) {
            throw new IllegalArgumentException("Scnd comp >= (First comp*40).");
        }
        for (int i = 0; i < components.length; ++i) {
            if (components[i] < 0) {
                throw new IllegalArgumentException("Negative comp (" + i + ").");
            }
        }
        this.components = components.clone();
    }
    
    static {
        _rsaEncryption = new int[] { 1, 2, 840, 113549, 1, 1, 1 };
        _md4WithRSAEncryption = new int[] { 1, 2, 840, 113549, 1, 1, 3 };
        _md5WithRSAEncryption = new int[] { 1, 2, 840, 113549, 1, 1, 4 };
        _sha1WithRSAEncryption = new int[] { 1, 2, 840, 113549, 1, 1, 5 };
        _rsaOAEPEncryptionSET = new int[] { 1, 2, 840, 113549, 1, 1, 6 };
        _id_hmacWithSHA1 = new int[] { 1, 2, 840, 113549, 2, 7 };
        _rc2_CBC = new int[] { 1, 2, 840, 113549, 3, 2 };
        _des_EDE3_CBC = new int[] { 1, 2, 840, 113549, 3, 7 };
        _rc5_CBC_PAD = new int[] { 1, 2, 840, 113549, 3, 9 };
        _pbeWithMD2AndDES_CBC = new int[] { 1, 2, 840, 113549, 1, 5, 1 };
        _pbeWithMD5AndDES_CBC = new int[] { 1, 2, 840, 113549, 1, 5, 3 };
        _pbeWithMD2AndRC2_CBC = new int[] { 1, 2, 840, 113549, 1, 5, 4 };
        _pbeWithMD5AndRC2_CBC = new int[] { 1, 2, 840, 113549, 1, 5, 6 };
        _pbeWithSHA1AndDES_CBC = new int[] { 1, 2, 840, 113549, 1, 5, 10 };
        _pbeWithSHA1AndRC2_CBC = new int[] { 1, 2, 840, 113549, 1, 5, 11 };
        _id_PBKDF2 = new int[] { 1, 2, 840, 113549, 1, 5, 12 };
        _id_PBES2 = new int[] { 1, 2, 840, 113549, 1, 5, 13 };
        _id_PBMAC1 = new int[] { 1, 2, 840, 113549, 1, 5, 14 };
        OID_rsaEncryption = new AsnObjectId(AsnObjectId._rsaEncryption);
        OID_md5WithRSAEncryption = new AsnObjectId(AsnObjectId._md5WithRSAEncryption);
    }
}
