package sun.security.krb5.internal;

import java.math.BigInteger;
import sun.security.util.DerOutputStream;
import sun.security.krb5.RealmException;
import sun.security.krb5.Asn1Exception;
import sun.security.util.DerValue;
import java.io.IOException;

public class EncKrbCredPart
{
    public KrbCredInfo[] ticketInfo;
    public KerberosTime timeStamp;
    private Integer nonce;
    private Integer usec;
    private HostAddress sAddress;
    private HostAddresses rAddress;
    
    public EncKrbCredPart(final KrbCredInfo[] array, final KerberosTime timeStamp, final Integer usec, final Integer nonce, final HostAddress sAddress, final HostAddresses rAddress) throws IOException {
        this.ticketInfo = null;
        if (array != null) {
            this.ticketInfo = new KrbCredInfo[array.length];
            for (int i = 0; i < array.length; ++i) {
                if (array[i] == null) {
                    throw new IOException("Cannot create a EncKrbCredPart");
                }
                this.ticketInfo[i] = (KrbCredInfo)array[i].clone();
            }
        }
        this.timeStamp = timeStamp;
        this.usec = usec;
        this.nonce = nonce;
        this.sAddress = sAddress;
        this.rAddress = rAddress;
    }
    
    public EncKrbCredPart(final byte[] array) throws Asn1Exception, IOException, RealmException {
        this.ticketInfo = null;
        this.init(new DerValue(array));
    }
    
    public EncKrbCredPart(final DerValue derValue) throws Asn1Exception, IOException, RealmException {
        this.ticketInfo = null;
        this.init(derValue);
    }
    
    private void init(final DerValue derValue) throws Asn1Exception, IOException, RealmException {
        this.nonce = null;
        this.timeStamp = null;
        this.usec = null;
        this.sAddress = null;
        this.rAddress = null;
        if ((derValue.getTag() & 0x1F) != 0x1D || !derValue.isApplication() || !derValue.isConstructed()) {
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
        final DerValue[] sequence = derValue3.getData().getSequence(1);
        this.ticketInfo = new KrbCredInfo[sequence.length];
        for (int i = 0; i < sequence.length; ++i) {
            this.ticketInfo[i] = new KrbCredInfo(sequence[i]);
        }
        if (derValue2.getData().available() > 0 && ((byte)derValue2.getData().peekByte() & 0x1F) == 0x1) {
            this.nonce = new Integer(derValue2.getData().getDerValue().getData().getBigInteger().intValue());
        }
        if (derValue2.getData().available() > 0) {
            this.timeStamp = KerberosTime.parse(derValue2.getData(), (byte)2, true);
        }
        if (derValue2.getData().available() > 0 && ((byte)derValue2.getData().peekByte() & 0x1F) == 0x3) {
            this.usec = new Integer(derValue2.getData().getDerValue().getData().getBigInteger().intValue());
        }
        if (derValue2.getData().available() > 0) {
            this.sAddress = HostAddress.parse(derValue2.getData(), (byte)4, true);
        }
        if (derValue2.getData().available() > 0) {
            this.rAddress = HostAddresses.parse(derValue2.getData(), (byte)5, true);
        }
        if (derValue2.getData().available() > 0) {
            throw new Asn1Exception(906);
        }
    }
    
    public byte[] asn1Encode() throws Asn1Exception, IOException {
        final DerOutputStream derOutputStream = new DerOutputStream();
        final DerOutputStream derOutputStream2 = new DerOutputStream();
        final DerValue[] array = new DerValue[this.ticketInfo.length];
        for (int i = 0; i < this.ticketInfo.length; ++i) {
            array[i] = new DerValue(this.ticketInfo[i].asn1Encode());
        }
        derOutputStream2.putSequence(array);
        derOutputStream.write(DerValue.createTag((byte)(-128), true, (byte)0), derOutputStream2);
        if (this.nonce != null) {
            final DerOutputStream derOutputStream3 = new DerOutputStream();
            derOutputStream3.putInteger(BigInteger.valueOf(this.nonce));
            derOutputStream.write(DerValue.createTag((byte)(-128), true, (byte)1), derOutputStream3);
        }
        if (this.timeStamp != null) {
            derOutputStream.write(DerValue.createTag((byte)(-128), true, (byte)2), this.timeStamp.asn1Encode());
        }
        if (this.usec != null) {
            final DerOutputStream derOutputStream4 = new DerOutputStream();
            derOutputStream4.putInteger(BigInteger.valueOf(this.usec));
            derOutputStream.write(DerValue.createTag((byte)(-128), true, (byte)3), derOutputStream4);
        }
        if (this.sAddress != null) {
            derOutputStream.write(DerValue.createTag((byte)(-128), true, (byte)4), this.sAddress.asn1Encode());
        }
        if (this.rAddress != null) {
            derOutputStream.write(DerValue.createTag((byte)(-128), true, (byte)5), this.rAddress.asn1Encode());
        }
        final DerOutputStream derOutputStream5 = new DerOutputStream();
        derOutputStream5.write((byte)48, derOutputStream);
        final DerOutputStream derOutputStream6 = new DerOutputStream();
        derOutputStream6.write(DerValue.createTag((byte)64, true, (byte)29), derOutputStream5);
        return derOutputStream6.toByteArray();
    }
}
