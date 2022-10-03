package sun.security.krb5.internal;

import java.math.BigInteger;
import sun.security.util.DerOutputStream;
import java.io.IOException;
import sun.security.krb5.RealmException;
import sun.security.krb5.Asn1Exception;
import sun.security.util.DerValue;
import sun.security.krb5.Checksum;

public class KRBSafe
{
    public int pvno;
    public int msgType;
    public KRBSafeBody safeBody;
    public Checksum cksum;
    
    public KRBSafe(final KRBSafeBody safeBody, final Checksum cksum) {
        this.pvno = 5;
        this.msgType = 20;
        this.safeBody = safeBody;
        this.cksum = cksum;
    }
    
    public KRBSafe(final byte[] array) throws Asn1Exception, RealmException, KrbApErrException, IOException {
        this.init(new DerValue(array));
    }
    
    public KRBSafe(final DerValue derValue) throws Asn1Exception, RealmException, KrbApErrException, IOException {
        this.init(derValue);
    }
    
    private void init(final DerValue derValue) throws Asn1Exception, RealmException, KrbApErrException, IOException {
        if ((derValue.getTag() & 0x1F) != 0x14 || !derValue.isApplication() || !derValue.isConstructed()) {
            throw new Asn1Exception(906);
        }
        final DerValue derValue2 = derValue.getData().getDerValue();
        if (derValue2.getTag() != 48) {
            throw new Asn1Exception(906);
        }
        final DerValue derValue3 = derValue2.getData().getDerValue();
        if ((derValue3.getTag() & 0x1F) != 0x0) {
            throw new Asn1Exception(906);
        }
        this.pvno = derValue3.getData().getBigInteger().intValue();
        if (this.pvno != 5) {
            throw new KrbApErrException(39);
        }
        final DerValue derValue4 = derValue2.getData().getDerValue();
        if ((derValue4.getTag() & 0x1F) != 0x1) {
            throw new Asn1Exception(906);
        }
        this.msgType = derValue4.getData().getBigInteger().intValue();
        if (this.msgType != 20) {
            throw new KrbApErrException(40);
        }
        this.safeBody = KRBSafeBody.parse(derValue2.getData(), (byte)2, false);
        this.cksum = Checksum.parse(derValue2.getData(), (byte)3, false);
        if (derValue2.getData().available() > 0) {
            throw new Asn1Exception(906);
        }
    }
    
    public byte[] asn1Encode() throws Asn1Exception, IOException {
        final DerOutputStream derOutputStream = new DerOutputStream();
        final DerOutputStream derOutputStream2 = new DerOutputStream();
        derOutputStream.putInteger(BigInteger.valueOf(this.pvno));
        derOutputStream2.write(DerValue.createTag((byte)(-128), true, (byte)0), derOutputStream);
        final DerOutputStream derOutputStream3 = new DerOutputStream();
        derOutputStream3.putInteger(BigInteger.valueOf(this.msgType));
        derOutputStream2.write(DerValue.createTag((byte)(-128), true, (byte)1), derOutputStream3);
        derOutputStream2.write(DerValue.createTag((byte)(-128), true, (byte)2), this.safeBody.asn1Encode());
        derOutputStream2.write(DerValue.createTag((byte)(-128), true, (byte)3), this.cksum.asn1Encode());
        final DerOutputStream derOutputStream4 = new DerOutputStream();
        derOutputStream4.write((byte)48, derOutputStream2);
        final DerOutputStream derOutputStream5 = new DerOutputStream();
        derOutputStream5.write(DerValue.createTag((byte)64, true, (byte)20), derOutputStream4);
        return derOutputStream5.toByteArray();
    }
}
