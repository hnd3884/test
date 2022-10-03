package sun.security.krb5.internal;

import sun.security.util.DerInputStream;
import java.math.BigInteger;
import sun.security.util.DerOutputStream;
import sun.security.krb5.Realm;
import java.io.IOException;
import sun.security.krb5.RealmException;
import sun.security.krb5.Asn1Exception;
import sun.security.util.DerValue;
import sun.security.krb5.EncryptedData;
import sun.security.krb5.PrincipalName;

public class Ticket implements Cloneable
{
    public int tkt_vno;
    public PrincipalName sname;
    public EncryptedData encPart;
    
    private Ticket() {
    }
    
    public Object clone() {
        final Ticket ticket = new Ticket();
        ticket.sname = (PrincipalName)this.sname.clone();
        ticket.encPart = (EncryptedData)this.encPart.clone();
        ticket.tkt_vno = this.tkt_vno;
        return ticket;
    }
    
    public Ticket(final PrincipalName sname, final EncryptedData encPart) {
        this.tkt_vno = 5;
        this.sname = sname;
        this.encPart = encPart;
    }
    
    public Ticket(final byte[] array) throws Asn1Exception, RealmException, KrbApErrException, IOException {
        this.init(new DerValue(array));
    }
    
    public Ticket(final DerValue derValue) throws Asn1Exception, RealmException, KrbApErrException, IOException {
        this.init(derValue);
    }
    
    private void init(final DerValue derValue) throws Asn1Exception, RealmException, KrbApErrException, IOException {
        if ((derValue.getTag() & 0x1F) != 0x1 || !derValue.isApplication() || !derValue.isConstructed()) {
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
        this.tkt_vno = derValue3.getData().getBigInteger().intValue();
        if (this.tkt_vno != 5) {
            throw new KrbApErrException(39);
        }
        this.sname = PrincipalName.parse(derValue2.getData(), (byte)2, false, Realm.parse(derValue2.getData(), (byte)1, false));
        this.encPart = EncryptedData.parse(derValue2.getData(), (byte)3, false);
        if (derValue2.getData().available() > 0) {
            throw new Asn1Exception(906);
        }
    }
    
    public byte[] asn1Encode() throws Asn1Exception, IOException {
        final DerOutputStream derOutputStream = new DerOutputStream();
        final DerOutputStream derOutputStream2 = new DerOutputStream();
        final DerValue[] array = new DerValue[4];
        derOutputStream2.putInteger(BigInteger.valueOf(this.tkt_vno));
        derOutputStream.write(DerValue.createTag((byte)(-128), true, (byte)0), derOutputStream2);
        derOutputStream.write(DerValue.createTag((byte)(-128), true, (byte)1), this.sname.getRealm().asn1Encode());
        derOutputStream.write(DerValue.createTag((byte)(-128), true, (byte)2), this.sname.asn1Encode());
        derOutputStream.write(DerValue.createTag((byte)(-128), true, (byte)3), this.encPart.asn1Encode());
        final DerOutputStream derOutputStream3 = new DerOutputStream();
        derOutputStream3.write((byte)48, derOutputStream);
        final DerOutputStream derOutputStream4 = new DerOutputStream();
        derOutputStream4.write(DerValue.createTag((byte)64, true, (byte)1), derOutputStream3);
        return derOutputStream4.toByteArray();
    }
    
    public static Ticket parse(final DerInputStream derInputStream, final byte b, final boolean b2) throws Asn1Exception, IOException, RealmException, KrbApErrException {
        if (b2 && ((byte)derInputStream.peekByte() & 0x1F) != b) {
            return null;
        }
        final DerValue derValue = derInputStream.getDerValue();
        if (b != (derValue.getTag() & 0x1F)) {
            throw new Asn1Exception(906);
        }
        return new Ticket(derValue.getData().getDerValue());
    }
}
