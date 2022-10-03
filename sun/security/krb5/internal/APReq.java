package sun.security.krb5.internal;

import java.math.BigInteger;
import sun.security.util.DerOutputStream;
import sun.security.krb5.RealmException;
import java.io.IOException;
import sun.security.krb5.Asn1Exception;
import sun.security.util.DerValue;
import sun.security.krb5.EncryptedData;

public class APReq
{
    public int pvno;
    public int msgType;
    public APOptions apOptions;
    public Ticket ticket;
    public EncryptedData authenticator;
    
    public APReq(final APOptions apOptions, final Ticket ticket, final EncryptedData authenticator) {
        this.pvno = 5;
        this.msgType = 14;
        this.apOptions = apOptions;
        this.ticket = ticket;
        this.authenticator = authenticator;
    }
    
    public APReq(final byte[] array) throws Asn1Exception, IOException, KrbApErrException, RealmException {
        this.init(new DerValue(array));
    }
    
    public APReq(final DerValue derValue) throws Asn1Exception, IOException, KrbApErrException, RealmException {
        this.init(derValue);
    }
    
    private void init(final DerValue derValue) throws Asn1Exception, IOException, KrbApErrException, RealmException {
        if ((derValue.getTag() & 0x1F) != 0xE || !derValue.isApplication() || !derValue.isConstructed()) {
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
        if (this.msgType != 14) {
            throw new KrbApErrException(40);
        }
        this.apOptions = APOptions.parse(derValue2.getData(), (byte)2, false);
        this.ticket = Ticket.parse(derValue2.getData(), (byte)3, false);
        this.authenticator = EncryptedData.parse(derValue2.getData(), (byte)4, false);
        if (derValue2.getData().available() > 0) {
            throw new Asn1Exception(906);
        }
    }
    
    public byte[] asn1Encode() throws Asn1Exception, IOException {
        final DerOutputStream derOutputStream = new DerOutputStream();
        final DerOutputStream derOutputStream2 = new DerOutputStream();
        derOutputStream2.putInteger(BigInteger.valueOf(this.pvno));
        derOutputStream.write(DerValue.createTag((byte)(-128), true, (byte)0), derOutputStream2);
        final DerOutputStream derOutputStream3 = new DerOutputStream();
        derOutputStream3.putInteger(BigInteger.valueOf(this.msgType));
        derOutputStream.write(DerValue.createTag((byte)(-128), true, (byte)1), derOutputStream3);
        derOutputStream.write(DerValue.createTag((byte)(-128), true, (byte)2), this.apOptions.asn1Encode());
        derOutputStream.write(DerValue.createTag((byte)(-128), true, (byte)3), this.ticket.asn1Encode());
        derOutputStream.write(DerValue.createTag((byte)(-128), true, (byte)4), this.authenticator.asn1Encode());
        final DerOutputStream derOutputStream4 = new DerOutputStream();
        derOutputStream4.write((byte)48, derOutputStream);
        final DerOutputStream derOutputStream5 = new DerOutputStream();
        derOutputStream5.write(DerValue.createTag((byte)64, true, (byte)14), derOutputStream4);
        return derOutputStream5.toByteArray();
    }
}
