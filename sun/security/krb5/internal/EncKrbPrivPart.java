package sun.security.krb5.internal;

import java.math.BigInteger;
import sun.security.util.DerOutputStream;
import java.io.IOException;
import sun.security.krb5.Asn1Exception;
import sun.security.util.DerValue;

public class EncKrbPrivPart
{
    public byte[] userData;
    public KerberosTime timestamp;
    public Integer usec;
    public Integer seqNumber;
    public HostAddress sAddress;
    public HostAddress rAddress;
    
    public EncKrbPrivPart(final byte[] array, final KerberosTime timestamp, final Integer usec, final Integer seqNumber, final HostAddress sAddress, final HostAddress rAddress) {
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
    
    public EncKrbPrivPart(final byte[] array) throws Asn1Exception, IOException {
        this.userData = null;
        this.init(new DerValue(array));
    }
    
    public EncKrbPrivPart(final DerValue derValue) throws Asn1Exception, IOException {
        this.userData = null;
        this.init(derValue);
    }
    
    private void init(final DerValue derValue) throws Asn1Exception, IOException {
        if ((derValue.getTag() & 0x1F) != 0x1C || !derValue.isApplication() || !derValue.isConstructed()) {
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
        this.userData = derValue3.getData().getOctetString();
        this.timestamp = KerberosTime.parse(derValue2.getData(), (byte)1, true);
        if ((derValue2.getData().peekByte() & 0x1F) == 0x2) {
            this.usec = new Integer(derValue2.getData().getDerValue().getData().getBigInteger().intValue());
        }
        else {
            this.usec = null;
        }
        if ((derValue2.getData().peekByte() & 0x1F) == 0x3) {
            this.seqNumber = new Integer(derValue2.getData().getDerValue().getData().getBigInteger().intValue());
        }
        else {
            this.seqNumber = null;
        }
        this.sAddress = HostAddress.parse(derValue2.getData(), (byte)4, false);
        if (derValue2.getData().available() > 0) {
            this.rAddress = HostAddress.parse(derValue2.getData(), (byte)5, true);
        }
        if (derValue2.getData().available() > 0) {
            throw new Asn1Exception(906);
        }
    }
    
    public byte[] asn1Encode() throws Asn1Exception, IOException {
        final DerOutputStream derOutputStream = new DerOutputStream();
        final DerOutputStream derOutputStream2 = new DerOutputStream();
        derOutputStream.putOctetString(this.userData);
        derOutputStream2.write(DerValue.createTag((byte)(-128), true, (byte)0), derOutputStream);
        if (this.timestamp != null) {
            derOutputStream2.write(DerValue.createTag((byte)(-128), true, (byte)1), this.timestamp.asn1Encode());
        }
        if (this.usec != null) {
            final DerOutputStream derOutputStream3 = new DerOutputStream();
            derOutputStream3.putInteger(BigInteger.valueOf(this.usec));
            derOutputStream2.write(DerValue.createTag((byte)(-128), true, (byte)2), derOutputStream3);
        }
        if (this.seqNumber != null) {
            final DerOutputStream derOutputStream4 = new DerOutputStream();
            derOutputStream4.putInteger(BigInteger.valueOf(this.seqNumber));
            derOutputStream2.write(DerValue.createTag((byte)(-128), true, (byte)3), derOutputStream4);
        }
        derOutputStream2.write(DerValue.createTag((byte)(-128), true, (byte)4), this.sAddress.asn1Encode());
        if (this.rAddress != null) {
            derOutputStream2.write(DerValue.createTag((byte)(-128), true, (byte)5), this.rAddress.asn1Encode());
        }
        final DerOutputStream derOutputStream5 = new DerOutputStream();
        derOutputStream5.write((byte)48, derOutputStream2);
        final DerOutputStream derOutputStream6 = new DerOutputStream();
        derOutputStream6.write(DerValue.createTag((byte)64, true, (byte)28), derOutputStream5);
        return derOutputStream6.toByteArray();
    }
}
