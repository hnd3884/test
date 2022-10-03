package sun.security.krb5.internal;

import sun.security.util.DerInputStream;
import java.math.BigInteger;
import sun.security.util.DerOutputStream;
import java.io.IOException;
import sun.security.krb5.Asn1Exception;
import sun.security.util.DerValue;

public class KRBSafeBody
{
    public byte[] userData;
    public KerberosTime timestamp;
    public Integer usec;
    public Integer seqNumber;
    public HostAddress sAddress;
    public HostAddress rAddress;
    
    public KRBSafeBody(final byte[] array, final KerberosTime timestamp, final Integer usec, final Integer seqNumber, final HostAddress sAddress, final HostAddress rAddress) {
        this.userData = null;
        if (array != null) {
            this.userData = array.clone();
        }
        this.timestamp = timestamp;
        this.usec = usec;
        this.seqNumber = seqNumber;
        this.sAddress = sAddress;
        this.rAddress = rAddress;
    }
    
    public KRBSafeBody(final DerValue derValue) throws Asn1Exception, IOException {
        this.userData = null;
        if (derValue.getTag() != 48) {
            throw new Asn1Exception(906);
        }
        final DerValue derValue2 = derValue.getData().getDerValue();
        if ((derValue2.getTag() & 0x1F) != 0x0) {
            throw new Asn1Exception(906);
        }
        this.userData = derValue2.getData().getOctetString();
        this.timestamp = KerberosTime.parse(derValue.getData(), (byte)1, true);
        if ((derValue.getData().peekByte() & 0x1F) == 0x2) {
            this.usec = new Integer(derValue.getData().getDerValue().getData().getBigInteger().intValue());
        }
        if ((derValue.getData().peekByte() & 0x1F) == 0x3) {
            this.seqNumber = new Integer(derValue.getData().getDerValue().getData().getBigInteger().intValue());
        }
        this.sAddress = HostAddress.parse(derValue.getData(), (byte)4, false);
        if (derValue.getData().available() > 0) {
            this.rAddress = HostAddress.parse(derValue.getData(), (byte)5, true);
        }
        if (derValue.getData().available() > 0) {
            throw new Asn1Exception(906);
        }
    }
    
    public byte[] asn1Encode() throws Asn1Exception, IOException {
        final DerOutputStream derOutputStream = new DerOutputStream();
        DerOutputStream derOutputStream2 = new DerOutputStream();
        derOutputStream2.putOctetString(this.userData);
        derOutputStream.write(DerValue.createTag((byte)(-128), true, (byte)0), derOutputStream2);
        if (this.timestamp != null) {
            derOutputStream.write(DerValue.createTag((byte)(-128), true, (byte)1), this.timestamp.asn1Encode());
        }
        if (this.usec != null) {
            derOutputStream2 = new DerOutputStream();
            derOutputStream2.putInteger(BigInteger.valueOf(this.usec));
            derOutputStream.write(DerValue.createTag((byte)(-128), true, (byte)2), derOutputStream2);
        }
        if (this.seqNumber != null) {
            derOutputStream2 = new DerOutputStream();
            derOutputStream2.putInteger(BigInteger.valueOf(this.seqNumber));
            derOutputStream.write(DerValue.createTag((byte)(-128), true, (byte)3), derOutputStream2);
        }
        derOutputStream.write(DerValue.createTag((byte)(-128), true, (byte)4), this.sAddress.asn1Encode());
        if (this.rAddress != null) {
            derOutputStream2 = new DerOutputStream();
        }
        derOutputStream2.write((byte)48, derOutputStream);
        return derOutputStream2.toByteArray();
    }
    
    public static KRBSafeBody parse(final DerInputStream derInputStream, final byte b, final boolean b2) throws Asn1Exception, IOException {
        if (b2 && ((byte)derInputStream.peekByte() & 0x1F) != b) {
            return null;
        }
        final DerValue derValue = derInputStream.getDerValue();
        if (b != (derValue.getTag() & 0x1F)) {
            throw new Asn1Exception(906);
        }
        return new KRBSafeBody(derValue.getData().getDerValue());
    }
}
