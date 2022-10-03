package sun.security.krb5.internal;

import java.math.BigInteger;
import sun.security.util.DerOutputStream;
import java.util.Vector;
import sun.security.krb5.RealmException;
import sun.security.krb5.Asn1Exception;
import sun.security.util.DerValue;
import java.io.IOException;
import sun.security.krb5.EncryptedData;

public class KRBCred
{
    public Ticket[] tickets;
    public EncryptedData encPart;
    private int pvno;
    private int msgType;
    
    public KRBCred(final Ticket[] array, final EncryptedData encPart) throws IOException {
        this.tickets = null;
        this.pvno = 5;
        this.msgType = 22;
        if (array != null) {
            this.tickets = new Ticket[array.length];
            for (int i = 0; i < array.length; ++i) {
                if (array[i] == null) {
                    throw new IOException("Cannot create a KRBCred");
                }
                this.tickets[i] = (Ticket)array[i].clone();
            }
        }
        this.encPart = encPart;
    }
    
    public KRBCred(final byte[] array) throws Asn1Exception, RealmException, KrbApErrException, IOException {
        this.tickets = null;
        this.init(new DerValue(array));
    }
    
    public KRBCred(final DerValue derValue) throws Asn1Exception, RealmException, KrbApErrException, IOException {
        this.tickets = null;
        this.init(derValue);
    }
    
    private void init(final DerValue derValue) throws Asn1Exception, RealmException, KrbApErrException, IOException {
        if ((derValue.getTag() & 0x1F) != 0x16 || !derValue.isApplication() || !derValue.isConstructed()) {
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
        if (this.msgType != 22) {
            throw new KrbApErrException(40);
        }
        final DerValue derValue5 = derValue2.getData().getDerValue();
        if ((derValue5.getTag() & 0x1F) != 0x2) {
            throw new Asn1Exception(906);
        }
        final DerValue derValue6 = derValue5.getData().getDerValue();
        if (derValue6.getTag() != 48) {
            throw new Asn1Exception(906);
        }
        final Vector vector = new Vector();
        while (derValue6.getData().available() > 0) {
            vector.addElement(new Ticket(derValue6.getData().getDerValue()));
        }
        if (vector.size() > 0) {
            vector.copyInto(this.tickets = new Ticket[vector.size()]);
        }
        this.encPart = EncryptedData.parse(derValue2.getData(), (byte)3, false);
        if (derValue2.getData().available() > 0) {
            throw new Asn1Exception(906);
        }
    }
    
    public byte[] asn1Encode() throws Asn1Exception, IOException {
        final DerOutputStream derOutputStream = new DerOutputStream();
        derOutputStream.putInteger(BigInteger.valueOf(this.pvno));
        final DerOutputStream derOutputStream2 = new DerOutputStream();
        derOutputStream2.write(DerValue.createTag((byte)(-128), true, (byte)0), derOutputStream);
        final DerOutputStream derOutputStream3 = new DerOutputStream();
        derOutputStream3.putInteger(BigInteger.valueOf(this.msgType));
        derOutputStream2.write(DerValue.createTag((byte)(-128), true, (byte)1), derOutputStream3);
        final DerOutputStream derOutputStream4 = new DerOutputStream();
        for (int i = 0; i < this.tickets.length; ++i) {
            derOutputStream4.write(this.tickets[i].asn1Encode());
        }
        final DerOutputStream derOutputStream5 = new DerOutputStream();
        derOutputStream5.write((byte)48, derOutputStream4);
        derOutputStream2.write(DerValue.createTag((byte)(-128), true, (byte)2), derOutputStream5);
        derOutputStream2.write(DerValue.createTag((byte)(-128), true, (byte)3), this.encPart.asn1Encode());
        final DerOutputStream derOutputStream6 = new DerOutputStream();
        derOutputStream6.write((byte)48, derOutputStream2);
        final DerOutputStream derOutputStream7 = new DerOutputStream();
        derOutputStream7.write(DerValue.createTag((byte)64, true, (byte)22), derOutputStream6);
        return derOutputStream7.toByteArray();
    }
}
