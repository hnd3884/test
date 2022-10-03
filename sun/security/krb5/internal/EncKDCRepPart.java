package sun.security.krb5.internal;

import java.math.BigInteger;
import sun.security.util.DerOutputStream;
import sun.security.krb5.Realm;
import sun.security.krb5.RealmException;
import java.io.IOException;
import sun.security.krb5.Asn1Exception;
import sun.security.util.DerValue;
import sun.security.krb5.PrincipalName;
import sun.security.krb5.EncryptionKey;

public class EncKDCRepPart
{
    public EncryptionKey key;
    public LastReq lastReq;
    public int nonce;
    public KerberosTime keyExpiration;
    public TicketFlags flags;
    public KerberosTime authtime;
    public KerberosTime starttime;
    public KerberosTime endtime;
    public KerberosTime renewTill;
    public PrincipalName sname;
    public HostAddresses caddr;
    public PAData[] pAData;
    public int msgType;
    
    public EncKDCRepPart(final EncryptionKey key, final LastReq lastReq, final int nonce, final KerberosTime keyExpiration, final TicketFlags flags, final KerberosTime authtime, final KerberosTime starttime, final KerberosTime endtime, final KerberosTime renewTill, final PrincipalName sname, final HostAddresses caddr, final PAData[] paData, final int msgType) {
        this.key = key;
        this.lastReq = lastReq;
        this.nonce = nonce;
        this.keyExpiration = keyExpiration;
        this.flags = flags;
        this.authtime = authtime;
        this.starttime = starttime;
        this.endtime = endtime;
        this.renewTill = renewTill;
        this.sname = sname;
        this.caddr = caddr;
        this.pAData = paData;
        this.msgType = msgType;
    }
    
    public EncKDCRepPart() {
    }
    
    public EncKDCRepPart(final byte[] array, final int n) throws Asn1Exception, IOException, RealmException {
        this.init(new DerValue(array), n);
    }
    
    public EncKDCRepPart(final DerValue derValue, final int n) throws Asn1Exception, IOException, RealmException {
        this.init(derValue, n);
    }
    
    protected void init(final DerValue derValue, final int n) throws Asn1Exception, IOException, RealmException {
        this.msgType = (derValue.getTag() & 0x1F);
        if (this.msgType != 25 && this.msgType != 26) {
            throw new Asn1Exception(906);
        }
        final DerValue derValue2 = derValue.getData().getDerValue();
        if (derValue2.getTag() != 48) {
            throw new Asn1Exception(906);
        }
        this.key = EncryptionKey.parse(derValue2.getData(), (byte)0, false);
        this.lastReq = LastReq.parse(derValue2.getData(), (byte)1, false);
        final DerValue derValue3 = derValue2.getData().getDerValue();
        if ((derValue3.getTag() & 0x1F) == 0x2) {
            this.nonce = derValue3.getData().getBigInteger().intValue();
            this.keyExpiration = KerberosTime.parse(derValue2.getData(), (byte)3, true);
            this.flags = TicketFlags.parse(derValue2.getData(), (byte)4, false);
            this.authtime = KerberosTime.parse(derValue2.getData(), (byte)5, false);
            this.starttime = KerberosTime.parse(derValue2.getData(), (byte)6, true);
            this.endtime = KerberosTime.parse(derValue2.getData(), (byte)7, false);
            this.renewTill = KerberosTime.parse(derValue2.getData(), (byte)8, true);
            this.sname = PrincipalName.parse(derValue2.getData(), (byte)10, false, Realm.parse(derValue2.getData(), (byte)9, false));
            if (derValue2.getData().available() > 0) {
                this.caddr = HostAddresses.parse(derValue2.getData(), (byte)11, true);
            }
            if (derValue2.getData().available() > 0) {
                this.pAData = PAData.parseSequence(derValue2.getData(), (byte)12, true);
            }
            return;
        }
        throw new Asn1Exception(906);
    }
    
    public byte[] asn1Encode(final int n) throws Asn1Exception, IOException {
        final DerOutputStream derOutputStream = new DerOutputStream();
        final DerOutputStream derOutputStream2 = new DerOutputStream();
        derOutputStream2.write(DerValue.createTag((byte)(-128), true, (byte)0), this.key.asn1Encode());
        derOutputStream2.write(DerValue.createTag((byte)(-128), true, (byte)1), this.lastReq.asn1Encode());
        derOutputStream.putInteger(BigInteger.valueOf(this.nonce));
        derOutputStream2.write(DerValue.createTag((byte)(-128), true, (byte)2), derOutputStream);
        if (this.keyExpiration != null) {
            derOutputStream2.write(DerValue.createTag((byte)(-128), true, (byte)3), this.keyExpiration.asn1Encode());
        }
        derOutputStream2.write(DerValue.createTag((byte)(-128), true, (byte)4), this.flags.asn1Encode());
        derOutputStream2.write(DerValue.createTag((byte)(-128), true, (byte)5), this.authtime.asn1Encode());
        if (this.starttime != null) {
            derOutputStream2.write(DerValue.createTag((byte)(-128), true, (byte)6), this.starttime.asn1Encode());
        }
        derOutputStream2.write(DerValue.createTag((byte)(-128), true, (byte)7), this.endtime.asn1Encode());
        if (this.renewTill != null) {
            derOutputStream2.write(DerValue.createTag((byte)(-128), true, (byte)8), this.renewTill.asn1Encode());
        }
        derOutputStream2.write(DerValue.createTag((byte)(-128), true, (byte)9), this.sname.getRealm().asn1Encode());
        derOutputStream2.write(DerValue.createTag((byte)(-128), true, (byte)10), this.sname.asn1Encode());
        if (this.caddr != null) {
            derOutputStream2.write(DerValue.createTag((byte)(-128), true, (byte)11), this.caddr.asn1Encode());
        }
        if (this.pAData != null && this.pAData.length > 0) {
            final DerOutputStream derOutputStream3 = new DerOutputStream();
            for (int i = 0; i < this.pAData.length; ++i) {
                derOutputStream3.write(this.pAData[i].asn1Encode());
            }
            final DerOutputStream derOutputStream4 = new DerOutputStream();
            derOutputStream4.write((byte)48, derOutputStream3);
            derOutputStream2.write(DerValue.createTag((byte)(-128), true, (byte)12), derOutputStream4);
        }
        final DerOutputStream derOutputStream5 = new DerOutputStream();
        derOutputStream5.write((byte)48, derOutputStream2);
        final DerOutputStream derOutputStream6 = new DerOutputStream();
        derOutputStream6.write(DerValue.createTag((byte)64, true, (byte)this.msgType), derOutputStream5);
        return derOutputStream6.toByteArray();
    }
}
