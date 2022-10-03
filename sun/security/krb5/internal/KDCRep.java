package sun.security.krb5.internal;

import java.math.BigInteger;
import sun.security.util.DerOutputStream;
import sun.security.krb5.Realm;
import sun.security.krb5.RealmException;
import sun.security.krb5.Asn1Exception;
import sun.security.util.DerValue;
import java.io.IOException;
import sun.security.krb5.EncryptedData;
import sun.security.krb5.PrincipalName;

public class KDCRep
{
    public PrincipalName cname;
    public Ticket ticket;
    public EncryptedData encPart;
    public EncKDCRepPart encKDCRepPart;
    private int pvno;
    private int msgType;
    public PAData[] pAData;
    private boolean DEBUG;
    
    public KDCRep(final PAData[] array, final PrincipalName cname, final Ticket ticket, final EncryptedData encPart, final int msgType) throws IOException {
        this.pAData = null;
        this.DEBUG = Krb5.DEBUG;
        this.pvno = 5;
        this.msgType = msgType;
        if (array != null) {
            this.pAData = new PAData[array.length];
            for (int i = 0; i < array.length; ++i) {
                if (array[i] == null) {
                    throw new IOException("Cannot create a KDCRep");
                }
                this.pAData[i] = (PAData)array[i].clone();
            }
        }
        this.cname = cname;
        this.ticket = ticket;
        this.encPart = encPart;
    }
    
    public KDCRep() {
        this.pAData = null;
        this.DEBUG = Krb5.DEBUG;
    }
    
    public KDCRep(final byte[] array, final int n) throws Asn1Exception, KrbApErrException, RealmException, IOException {
        this.pAData = null;
        this.DEBUG = Krb5.DEBUG;
        this.init(new DerValue(array), n);
    }
    
    public KDCRep(final DerValue derValue, final int n) throws Asn1Exception, RealmException, KrbApErrException, IOException {
        this.pAData = null;
        this.DEBUG = Krb5.DEBUG;
        this.init(derValue, n);
    }
    
    protected void init(final DerValue derValue, final int n) throws Asn1Exception, RealmException, IOException, KrbApErrException {
        if ((derValue.getTag() & 0x1F) != n) {
            if (this.DEBUG) {
                System.out.println(">>> KDCRep: init() encoding tag is " + derValue.getTag() + " req type is " + n);
            }
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
        if (this.msgType != n) {
            throw new KrbApErrException(40);
        }
        if ((derValue2.getData().peekByte() & 0x1F) == 0x2) {
            final DerValue[] sequence = derValue2.getData().getDerValue().getData().getSequence(1);
            this.pAData = new PAData[sequence.length];
            for (int i = 0; i < sequence.length; ++i) {
                this.pAData[i] = new PAData(sequence[i]);
            }
        }
        else {
            this.pAData = null;
        }
        this.cname = PrincipalName.parse(derValue2.getData(), (byte)4, false, Realm.parse(derValue2.getData(), (byte)3, false));
        this.ticket = Ticket.parse(derValue2.getData(), (byte)5, false);
        this.encPart = EncryptedData.parse(derValue2.getData(), (byte)6, false);
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
        if (this.pAData != null && this.pAData.length > 0) {
            final DerOutputStream derOutputStream4 = new DerOutputStream();
            for (int i = 0; i < this.pAData.length; ++i) {
                derOutputStream4.write(this.pAData[i].asn1Encode());
            }
            final DerOutputStream derOutputStream5 = new DerOutputStream();
            derOutputStream5.write((byte)48, derOutputStream4);
            derOutputStream.write(DerValue.createTag((byte)(-128), true, (byte)2), derOutputStream5);
        }
        derOutputStream.write(DerValue.createTag((byte)(-128), true, (byte)3), this.cname.getRealm().asn1Encode());
        derOutputStream.write(DerValue.createTag((byte)(-128), true, (byte)4), this.cname.asn1Encode());
        derOutputStream.write(DerValue.createTag((byte)(-128), true, (byte)5), this.ticket.asn1Encode());
        derOutputStream.write(DerValue.createTag((byte)(-128), true, (byte)6), this.encPart.asn1Encode());
        final DerOutputStream derOutputStream6 = new DerOutputStream();
        derOutputStream6.write((byte)48, derOutputStream);
        return derOutputStream6.toByteArray();
    }
}
