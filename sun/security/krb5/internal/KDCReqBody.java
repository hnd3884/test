package sun.security.krb5.internal;

import java.math.BigInteger;
import sun.security.util.DerOutputStream;
import sun.security.krb5.KrbException;
import sun.security.krb5.RealmException;
import java.util.Vector;
import sun.security.krb5.Realm;
import sun.security.krb5.Asn1Exception;
import sun.security.util.DerValue;
import java.io.IOException;
import sun.security.krb5.EncryptedData;
import sun.security.krb5.PrincipalName;

public class KDCReqBody
{
    public KDCOptions kdcOptions;
    public PrincipalName cname;
    public PrincipalName sname;
    public KerberosTime from;
    public KerberosTime till;
    public KerberosTime rtime;
    public HostAddresses addresses;
    private int nonce;
    private int[] eType;
    private EncryptedData encAuthorizationData;
    private Ticket[] additionalTickets;
    
    public KDCReqBody(final KDCOptions kdcOptions, final PrincipalName cname, final PrincipalName sname, final KerberosTime from, final KerberosTime till, final KerberosTime rtime, final int nonce, final int[] array, final HostAddresses addresses, final EncryptedData encAuthorizationData, final Ticket[] array2) throws IOException {
        this.eType = null;
        this.kdcOptions = kdcOptions;
        this.cname = cname;
        this.sname = sname;
        this.from = from;
        this.till = till;
        this.rtime = rtime;
        this.nonce = nonce;
        if (array != null) {
            this.eType = array.clone();
        }
        this.addresses = addresses;
        this.encAuthorizationData = encAuthorizationData;
        if (array2 != null) {
            this.additionalTickets = new Ticket[array2.length];
            for (int i = 0; i < array2.length; ++i) {
                if (array2[i] == null) {
                    throw new IOException("Cannot create a KDCReqBody");
                }
                this.additionalTickets[i] = (Ticket)array2[i].clone();
            }
        }
    }
    
    public KDCReqBody(final DerValue derValue, final int n) throws Asn1Exception, RealmException, KrbException, IOException {
        this.eType = null;
        this.addresses = null;
        this.encAuthorizationData = null;
        this.additionalTickets = null;
        if (derValue.getTag() != 48) {
            throw new Asn1Exception(906);
        }
        this.kdcOptions = KDCOptions.parse(derValue.getData(), (byte)0, false);
        this.cname = PrincipalName.parse(derValue.getData(), (byte)1, true, new Realm("PLACEHOLDER"));
        if (n != 10 && this.cname != null) {
            throw new Asn1Exception(906);
        }
        final Realm parse = Realm.parse(derValue.getData(), (byte)2, false);
        if (this.cname != null) {
            this.cname = new PrincipalName(this.cname.getNameType(), this.cname.getNameStrings(), parse);
        }
        this.sname = PrincipalName.parse(derValue.getData(), (byte)3, true, parse);
        this.from = KerberosTime.parse(derValue.getData(), (byte)4, true);
        this.till = KerberosTime.parse(derValue.getData(), (byte)5, false);
        this.rtime = KerberosTime.parse(derValue.getData(), (byte)6, true);
        final DerValue derValue2 = derValue.getData().getDerValue();
        if ((derValue2.getTag() & 0x1F) != 0x7) {
            throw new Asn1Exception(906);
        }
        this.nonce = derValue2.getData().getBigInteger().intValue();
        final DerValue derValue3 = derValue.getData().getDerValue();
        final Vector vector = new Vector();
        if ((derValue3.getTag() & 0x1F) != 0x8) {
            throw new Asn1Exception(906);
        }
        final DerValue derValue4 = derValue3.getData().getDerValue();
        if (derValue4.getTag() != 48) {
            throw new Asn1Exception(906);
        }
        while (derValue4.getData().available() > 0) {
            vector.addElement(derValue4.getData().getBigInteger().intValue());
        }
        this.eType = new int[vector.size()];
        for (int i = 0; i < vector.size(); ++i) {
            this.eType[i] = (int)vector.elementAt(i);
        }
        if (derValue.getData().available() > 0) {
            this.addresses = HostAddresses.parse(derValue.getData(), (byte)9, true);
        }
        if (derValue.getData().available() > 0) {
            this.encAuthorizationData = EncryptedData.parse(derValue.getData(), (byte)10, true);
        }
        if (derValue.getData().available() > 0) {
            final Vector vector2 = new Vector();
            final DerValue derValue5 = derValue.getData().getDerValue();
            if ((derValue5.getTag() & 0x1F) != 0xB) {
                throw new Asn1Exception(906);
            }
            final DerValue derValue6 = derValue5.getData().getDerValue();
            if (derValue6.getTag() != 48) {
                throw new Asn1Exception(906);
            }
            while (derValue6.getData().available() > 0) {
                vector2.addElement(new Ticket(derValue6.getData().getDerValue()));
            }
            if (vector2.size() > 0) {
                vector2.copyInto(this.additionalTickets = new Ticket[vector2.size()]);
            }
        }
        if (derValue.getData().available() > 0) {
            throw new Asn1Exception(906);
        }
    }
    
    public byte[] asn1Encode(final int n) throws Asn1Exception, IOException {
        final Vector vector = new Vector();
        vector.addElement(new DerValue(DerValue.createTag((byte)(-128), true, (byte)0), this.kdcOptions.asn1Encode()));
        if (n == 10 && this.cname != null) {
            vector.addElement(new DerValue(DerValue.createTag((byte)(-128), true, (byte)1), this.cname.asn1Encode()));
        }
        if (this.sname != null) {
            vector.addElement(new DerValue(DerValue.createTag((byte)(-128), true, (byte)2), this.sname.getRealm().asn1Encode()));
            vector.addElement(new DerValue(DerValue.createTag((byte)(-128), true, (byte)3), this.sname.asn1Encode()));
        }
        else if (this.cname != null) {
            vector.addElement(new DerValue(DerValue.createTag((byte)(-128), true, (byte)2), this.cname.getRealm().asn1Encode()));
        }
        if (this.from != null) {
            vector.addElement(new DerValue(DerValue.createTag((byte)(-128), true, (byte)4), this.from.asn1Encode()));
        }
        vector.addElement(new DerValue(DerValue.createTag((byte)(-128), true, (byte)5), this.till.asn1Encode()));
        if (this.rtime != null) {
            vector.addElement(new DerValue(DerValue.createTag((byte)(-128), true, (byte)6), this.rtime.asn1Encode()));
        }
        final DerOutputStream derOutputStream = new DerOutputStream();
        derOutputStream.putInteger(BigInteger.valueOf(this.nonce));
        vector.addElement(new DerValue(DerValue.createTag((byte)(-128), true, (byte)7), derOutputStream.toByteArray()));
        final DerOutputStream derOutputStream2 = new DerOutputStream();
        for (int i = 0; i < this.eType.length; ++i) {
            derOutputStream2.putInteger(BigInteger.valueOf(this.eType[i]));
        }
        final DerOutputStream derOutputStream3 = new DerOutputStream();
        derOutputStream3.write((byte)48, derOutputStream2);
        vector.addElement(new DerValue(DerValue.createTag((byte)(-128), true, (byte)8), derOutputStream3.toByteArray()));
        if (this.addresses != null) {
            vector.addElement(new DerValue(DerValue.createTag((byte)(-128), true, (byte)9), this.addresses.asn1Encode()));
        }
        if (this.encAuthorizationData != null) {
            vector.addElement(new DerValue(DerValue.createTag((byte)(-128), true, (byte)10), this.encAuthorizationData.asn1Encode()));
        }
        if (this.additionalTickets != null && this.additionalTickets.length > 0) {
            final DerOutputStream derOutputStream4 = new DerOutputStream();
            for (int j = 0; j < this.additionalTickets.length; ++j) {
                derOutputStream4.write(this.additionalTickets[j].asn1Encode());
            }
            final DerOutputStream derOutputStream5 = new DerOutputStream();
            derOutputStream5.write((byte)48, derOutputStream4);
            vector.addElement(new DerValue(DerValue.createTag((byte)(-128), true, (byte)11), derOutputStream5.toByteArray()));
        }
        final DerValue[] array = new DerValue[vector.size()];
        vector.copyInto(array);
        final DerOutputStream derOutputStream6 = new DerOutputStream();
        derOutputStream6.putSequence(array);
        return derOutputStream6.toByteArray();
    }
    
    public int getNonce() {
        return this.nonce;
    }
}
