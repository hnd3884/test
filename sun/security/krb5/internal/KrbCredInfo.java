package sun.security.krb5.internal;

import sun.security.util.DerOutputStream;
import java.util.Vector;
import sun.security.krb5.RealmException;
import java.io.IOException;
import sun.security.krb5.Realm;
import sun.security.krb5.Asn1Exception;
import sun.security.util.DerValue;
import sun.security.krb5.PrincipalName;
import sun.security.krb5.EncryptionKey;

public class KrbCredInfo
{
    public EncryptionKey key;
    public PrincipalName pname;
    public TicketFlags flags;
    public KerberosTime authtime;
    public KerberosTime starttime;
    public KerberosTime endtime;
    public KerberosTime renewTill;
    public PrincipalName sname;
    public HostAddresses caddr;
    
    private KrbCredInfo() {
    }
    
    public KrbCredInfo(final EncryptionKey key, final PrincipalName pname, final TicketFlags flags, final KerberosTime authtime, final KerberosTime starttime, final KerberosTime endtime, final KerberosTime renewTill, final PrincipalName sname, final HostAddresses caddr) {
        this.key = key;
        this.pname = pname;
        this.flags = flags;
        this.authtime = authtime;
        this.starttime = starttime;
        this.endtime = endtime;
        this.renewTill = renewTill;
        this.sname = sname;
        this.caddr = caddr;
    }
    
    public KrbCredInfo(final DerValue derValue) throws Asn1Exception, IOException, RealmException {
        if (derValue.getTag() != 48) {
            throw new Asn1Exception(906);
        }
        this.pname = null;
        this.flags = null;
        this.authtime = null;
        this.starttime = null;
        this.endtime = null;
        this.renewTill = null;
        this.sname = null;
        this.caddr = null;
        this.key = EncryptionKey.parse(derValue.getData(), (byte)0, false);
        Realm parse = null;
        Realm parse2 = null;
        if (derValue.getData().available() > 0) {
            parse = Realm.parse(derValue.getData(), (byte)1, true);
        }
        if (derValue.getData().available() > 0) {
            this.pname = PrincipalName.parse(derValue.getData(), (byte)2, true, parse);
        }
        if (derValue.getData().available() > 0) {
            this.flags = TicketFlags.parse(derValue.getData(), (byte)3, true);
        }
        if (derValue.getData().available() > 0) {
            this.authtime = KerberosTime.parse(derValue.getData(), (byte)4, true);
        }
        if (derValue.getData().available() > 0) {
            this.starttime = KerberosTime.parse(derValue.getData(), (byte)5, true);
        }
        if (derValue.getData().available() > 0) {
            this.endtime = KerberosTime.parse(derValue.getData(), (byte)6, true);
        }
        if (derValue.getData().available() > 0) {
            this.renewTill = KerberosTime.parse(derValue.getData(), (byte)7, true);
        }
        if (derValue.getData().available() > 0) {
            parse2 = Realm.parse(derValue.getData(), (byte)8, true);
        }
        if (derValue.getData().available() > 0) {
            this.sname = PrincipalName.parse(derValue.getData(), (byte)9, true, parse2);
        }
        if (derValue.getData().available() > 0) {
            this.caddr = HostAddresses.parse(derValue.getData(), (byte)10, true);
        }
        if (derValue.getData().available() > 0) {
            throw new Asn1Exception(906);
        }
    }
    
    public byte[] asn1Encode() throws Asn1Exception, IOException {
        final Vector vector = new Vector();
        vector.addElement(new DerValue(DerValue.createTag((byte)(-128), true, (byte)0), this.key.asn1Encode()));
        if (this.pname != null) {
            vector.addElement(new DerValue(DerValue.createTag((byte)(-128), true, (byte)1), this.pname.getRealm().asn1Encode()));
            vector.addElement(new DerValue(DerValue.createTag((byte)(-128), true, (byte)2), this.pname.asn1Encode()));
        }
        if (this.flags != null) {
            vector.addElement(new DerValue(DerValue.createTag((byte)(-128), true, (byte)3), this.flags.asn1Encode()));
        }
        if (this.authtime != null) {
            vector.addElement(new DerValue(DerValue.createTag((byte)(-128), true, (byte)4), this.authtime.asn1Encode()));
        }
        if (this.starttime != null) {
            vector.addElement(new DerValue(DerValue.createTag((byte)(-128), true, (byte)5), this.starttime.asn1Encode()));
        }
        if (this.endtime != null) {
            vector.addElement(new DerValue(DerValue.createTag((byte)(-128), true, (byte)6), this.endtime.asn1Encode()));
        }
        if (this.renewTill != null) {
            vector.addElement(new DerValue(DerValue.createTag((byte)(-128), true, (byte)7), this.renewTill.asn1Encode()));
        }
        if (this.sname != null) {
            vector.addElement(new DerValue(DerValue.createTag((byte)(-128), true, (byte)8), this.sname.getRealm().asn1Encode()));
            vector.addElement(new DerValue(DerValue.createTag((byte)(-128), true, (byte)9), this.sname.asn1Encode()));
        }
        if (this.caddr != null) {
            vector.addElement(new DerValue(DerValue.createTag((byte)(-128), true, (byte)10), this.caddr.asn1Encode()));
        }
        final DerValue[] array = new DerValue[vector.size()];
        vector.copyInto(array);
        final DerOutputStream derOutputStream = new DerOutputStream();
        derOutputStream.putSequence(array);
        return derOutputStream.toByteArray();
    }
    
    public Object clone() {
        final KrbCredInfo krbCredInfo = new KrbCredInfo();
        krbCredInfo.key = (EncryptionKey)this.key.clone();
        if (this.pname != null) {
            krbCredInfo.pname = (PrincipalName)this.pname.clone();
        }
        if (this.flags != null) {
            krbCredInfo.flags = (TicketFlags)this.flags.clone();
        }
        krbCredInfo.authtime = this.authtime;
        krbCredInfo.starttime = this.starttime;
        krbCredInfo.endtime = this.endtime;
        krbCredInfo.renewTill = this.renewTill;
        if (this.sname != null) {
            krbCredInfo.sname = (PrincipalName)this.sname.clone();
        }
        if (this.caddr != null) {
            krbCredInfo.caddr = (HostAddresses)this.caddr.clone();
        }
        return krbCredInfo;
    }
}
