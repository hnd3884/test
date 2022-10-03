package sun.security.krb5.internal;

import java.math.BigInteger;
import sun.security.util.DerOutputStream;
import java.util.Vector;
import java.io.IOException;
import sun.security.krb5.Asn1Exception;
import sun.security.util.DerValue;
import sun.security.krb5.EncryptionKey;

public class EncAPRepPart
{
    public KerberosTime ctime;
    public int cusec;
    EncryptionKey subKey;
    Integer seqNumber;
    
    public EncAPRepPart(final KerberosTime ctime, final int cusec, final EncryptionKey subKey, final Integer seqNumber) {
        this.ctime = ctime;
        this.cusec = cusec;
        this.subKey = subKey;
        this.seqNumber = seqNumber;
    }
    
    public EncAPRepPart(final byte[] array) throws Asn1Exception, IOException {
        this.init(new DerValue(array));
    }
    
    public EncAPRepPart(final DerValue derValue) throws Asn1Exception, IOException {
        this.init(derValue);
    }
    
    private void init(final DerValue derValue) throws Asn1Exception, IOException {
        if ((derValue.getTag() & 0x1F) != 0x1B || !derValue.isApplication() || !derValue.isConstructed()) {
            throw new Asn1Exception(906);
        }
        final DerValue derValue2 = derValue.getData().getDerValue();
        if (derValue2.getTag() != 48) {
            throw new Asn1Exception(906);
        }
        this.ctime = KerberosTime.parse(derValue2.getData(), (byte)0, true);
        final DerValue derValue3 = derValue2.getData().getDerValue();
        if ((derValue3.getTag() & 0x1F) != 0x1) {
            throw new Asn1Exception(906);
        }
        this.cusec = derValue3.getData().getBigInteger().intValue();
        if (derValue2.getData().available() > 0) {
            this.subKey = EncryptionKey.parse(derValue2.getData(), (byte)2, true);
        }
        else {
            this.subKey = null;
            this.seqNumber = null;
        }
        if (derValue2.getData().available() > 0) {
            final DerValue derValue4 = derValue2.getData().getDerValue();
            if ((derValue4.getTag() & 0x1F) != 0x3) {
                throw new Asn1Exception(906);
            }
            this.seqNumber = new Integer(derValue4.getData().getBigInteger().intValue());
        }
        else {
            this.seqNumber = null;
        }
        if (derValue2.getData().available() > 0) {
            throw new Asn1Exception(906);
        }
    }
    
    public byte[] asn1Encode() throws Asn1Exception, IOException {
        final Vector vector = new Vector();
        final DerOutputStream derOutputStream = new DerOutputStream();
        vector.addElement(new DerValue(DerValue.createTag((byte)(-128), true, (byte)0), this.ctime.asn1Encode()));
        derOutputStream.putInteger(BigInteger.valueOf(this.cusec));
        vector.addElement(new DerValue(DerValue.createTag((byte)(-128), true, (byte)1), derOutputStream.toByteArray()));
        if (this.subKey != null) {
            vector.addElement(new DerValue(DerValue.createTag((byte)(-128), true, (byte)2), this.subKey.asn1Encode()));
        }
        if (this.seqNumber != null) {
            final DerOutputStream derOutputStream2 = new DerOutputStream();
            derOutputStream2.putInteger(BigInteger.valueOf(this.seqNumber));
            vector.addElement(new DerValue(DerValue.createTag((byte)(-128), true, (byte)3), derOutputStream2.toByteArray()));
        }
        final DerValue[] array = new DerValue[vector.size()];
        vector.copyInto(array);
        final DerOutputStream derOutputStream3 = new DerOutputStream();
        derOutputStream3.putSequence(array);
        final DerOutputStream derOutputStream4 = new DerOutputStream();
        derOutputStream4.write(DerValue.createTag((byte)64, true, (byte)27), derOutputStream3);
        return derOutputStream4.toByteArray();
    }
    
    public final EncryptionKey getSubKey() {
        return this.subKey;
    }
    
    public final Integer getSeqNumber() {
        return this.seqNumber;
    }
}
