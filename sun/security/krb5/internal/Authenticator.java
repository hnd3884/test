package sun.security.krb5.internal;

import java.math.BigInteger;
import sun.security.util.DerOutputStream;
import java.util.Vector;
import sun.security.krb5.Realm;
import sun.security.krb5.RealmException;
import java.io.IOException;
import sun.security.krb5.Asn1Exception;
import sun.security.util.DerValue;
import sun.security.krb5.EncryptionKey;
import sun.security.krb5.Checksum;
import sun.security.krb5.PrincipalName;

public class Authenticator
{
    public int authenticator_vno;
    public PrincipalName cname;
    Checksum cksum;
    public int cusec;
    public KerberosTime ctime;
    EncryptionKey subKey;
    Integer seqNumber;
    public AuthorizationData authorizationData;
    
    public Authenticator(final PrincipalName cname, final Checksum cksum, final int cusec, final KerberosTime ctime, final EncryptionKey subKey, final Integer seqNumber, final AuthorizationData authorizationData) {
        this.authenticator_vno = 5;
        this.cname = cname;
        this.cksum = cksum;
        this.cusec = cusec;
        this.ctime = ctime;
        this.subKey = subKey;
        this.seqNumber = seqNumber;
        this.authorizationData = authorizationData;
    }
    
    public Authenticator(final byte[] array) throws Asn1Exception, IOException, KrbApErrException, RealmException {
        this.init(new DerValue(array));
    }
    
    public Authenticator(final DerValue derValue) throws Asn1Exception, IOException, KrbApErrException, RealmException {
        this.init(derValue);
    }
    
    private void init(final DerValue derValue) throws Asn1Exception, IOException, KrbApErrException, RealmException {
        if ((derValue.getTag() & 0x1F) != 0x2 || !derValue.isApplication() || !derValue.isConstructed()) {
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
        this.authenticator_vno = derValue3.getData().getBigInteger().intValue();
        if (this.authenticator_vno != 5) {
            throw new KrbApErrException(39);
        }
        this.cname = PrincipalName.parse(derValue2.getData(), (byte)2, false, Realm.parse(derValue2.getData(), (byte)1, false));
        this.cksum = Checksum.parse(derValue2.getData(), (byte)3, true);
        final DerValue derValue4 = derValue2.getData().getDerValue();
        if ((derValue4.getTag() & 0x1F) != 0x4) {
            throw new Asn1Exception(906);
        }
        this.cusec = derValue4.getData().getBigInteger().intValue();
        this.ctime = KerberosTime.parse(derValue2.getData(), (byte)5, false);
        if (derValue2.getData().available() > 0) {
            this.subKey = EncryptionKey.parse(derValue2.getData(), (byte)6, true);
        }
        else {
            this.subKey = null;
            this.seqNumber = null;
            this.authorizationData = null;
        }
        if (derValue2.getData().available() > 0) {
            if ((derValue2.getData().peekByte() & 0x1F) == 0x7) {
                final DerValue derValue5 = derValue2.getData().getDerValue();
                if ((derValue5.getTag() & 0x1F) == 0x7) {
                    this.seqNumber = new Integer(derValue5.getData().getBigInteger().intValue());
                }
            }
        }
        else {
            this.seqNumber = null;
            this.authorizationData = null;
        }
        if (derValue2.getData().available() > 0) {
            this.authorizationData = AuthorizationData.parse(derValue2.getData(), (byte)8, true);
        }
        else {
            this.authorizationData = null;
        }
        if (derValue2.getData().available() > 0) {
            throw new Asn1Exception(906);
        }
    }
    
    public byte[] asn1Encode() throws Asn1Exception, IOException {
        final Vector vector = new Vector();
        final DerOutputStream derOutputStream = new DerOutputStream();
        derOutputStream.putInteger(BigInteger.valueOf(this.authenticator_vno));
        vector.addElement(new DerValue(DerValue.createTag((byte)(-128), true, (byte)0), derOutputStream.toByteArray()));
        vector.addElement(new DerValue(DerValue.createTag((byte)(-128), true, (byte)1), this.cname.getRealm().asn1Encode()));
        vector.addElement(new DerValue(DerValue.createTag((byte)(-128), true, (byte)2), this.cname.asn1Encode()));
        if (this.cksum != null) {
            vector.addElement(new DerValue(DerValue.createTag((byte)(-128), true, (byte)3), this.cksum.asn1Encode()));
        }
        final DerOutputStream derOutputStream2 = new DerOutputStream();
        derOutputStream2.putInteger(BigInteger.valueOf(this.cusec));
        vector.addElement(new DerValue(DerValue.createTag((byte)(-128), true, (byte)4), derOutputStream2.toByteArray()));
        vector.addElement(new DerValue(DerValue.createTag((byte)(-128), true, (byte)5), this.ctime.asn1Encode()));
        if (this.subKey != null) {
            vector.addElement(new DerValue(DerValue.createTag((byte)(-128), true, (byte)6), this.subKey.asn1Encode()));
        }
        if (this.seqNumber != null) {
            final DerOutputStream derOutputStream3 = new DerOutputStream();
            derOutputStream3.putInteger(BigInteger.valueOf(this.seqNumber));
            vector.addElement(new DerValue(DerValue.createTag((byte)(-128), true, (byte)7), derOutputStream3.toByteArray()));
        }
        if (this.authorizationData != null) {
            vector.addElement(new DerValue(DerValue.createTag((byte)(-128), true, (byte)8), this.authorizationData.asn1Encode()));
        }
        final DerValue[] array = new DerValue[vector.size()];
        vector.copyInto(array);
        final DerOutputStream derOutputStream4 = new DerOutputStream();
        derOutputStream4.putSequence(array);
        final DerOutputStream derOutputStream5 = new DerOutputStream();
        derOutputStream5.write(DerValue.createTag((byte)64, true, (byte)2), derOutputStream4);
        return derOutputStream5.toByteArray();
    }
    
    public final Checksum getChecksum() {
        return this.cksum;
    }
    
    public final Integer getSeqNumber() {
        return this.seqNumber;
    }
    
    public final EncryptionKey getSubKey() {
        return this.subKey;
    }
}
