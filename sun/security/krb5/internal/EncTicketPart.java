package sun.security.krb5.internal;

import sun.security.util.DerOutputStream;
import sun.security.krb5.RealmException;
import sun.security.krb5.Realm;
import java.io.IOException;
import sun.security.krb5.KrbException;
import sun.security.krb5.Asn1Exception;
import sun.security.util.DerValue;
import sun.security.krb5.PrincipalName;
import sun.security.krb5.EncryptionKey;

public class EncTicketPart
{
    public TicketFlags flags;
    public EncryptionKey key;
    public PrincipalName cname;
    public TransitedEncoding transited;
    public KerberosTime authtime;
    public KerberosTime starttime;
    public KerberosTime endtime;
    public KerberosTime renewTill;
    public HostAddresses caddr;
    public AuthorizationData authorizationData;
    
    public EncTicketPart(final TicketFlags flags, final EncryptionKey key, final PrincipalName cname, final TransitedEncoding transited, final KerberosTime authtime, final KerberosTime starttime, final KerberosTime endtime, final KerberosTime renewTill, final HostAddresses caddr, final AuthorizationData authorizationData) {
        this.flags = flags;
        this.key = key;
        this.cname = cname;
        this.transited = transited;
        this.authtime = authtime;
        this.starttime = starttime;
        this.endtime = endtime;
        this.renewTill = renewTill;
        this.caddr = caddr;
        this.authorizationData = authorizationData;
    }
    
    public EncTicketPart(final byte[] array) throws Asn1Exception, KrbException, IOException {
        this.init(new DerValue(array));
    }
    
    public EncTicketPart(final DerValue derValue) throws Asn1Exception, KrbException, IOException {
        this.init(derValue);
    }
    
    private static String getHexBytes(final byte[] array, final int n) throws IOException {
        final StringBuffer sb = new StringBuffer();
        for (int i = 0; i < n; ++i) {
            final int n2 = array[i] >> 4 & 0xF;
            final int n3 = array[i] & 0xF;
            sb.append(Integer.toHexString(n2));
            sb.append(Integer.toHexString(n3));
            sb.append(' ');
        }
        return sb.toString();
    }
    
    private void init(final DerValue derValue) throws Asn1Exception, IOException, RealmException {
        this.renewTill = null;
        this.caddr = null;
        this.authorizationData = null;
        if ((derValue.getTag() & 0x1F) != 0x3 || !derValue.isApplication() || !derValue.isConstructed()) {
            throw new Asn1Exception(906);
        }
        final DerValue derValue2 = derValue.getData().getDerValue();
        if (derValue2.getTag() != 48) {
            throw new Asn1Exception(906);
        }
        this.flags = TicketFlags.parse(derValue2.getData(), (byte)0, false);
        this.key = EncryptionKey.parse(derValue2.getData(), (byte)1, false);
        this.cname = PrincipalName.parse(derValue2.getData(), (byte)3, false, Realm.parse(derValue2.getData(), (byte)2, false));
        this.transited = TransitedEncoding.parse(derValue2.getData(), (byte)4, false);
        this.authtime = KerberosTime.parse(derValue2.getData(), (byte)5, false);
        this.starttime = KerberosTime.parse(derValue2.getData(), (byte)6, true);
        this.endtime = KerberosTime.parse(derValue2.getData(), (byte)7, false);
        if (derValue2.getData().available() > 0) {
            this.renewTill = KerberosTime.parse(derValue2.getData(), (byte)8, true);
        }
        if (derValue2.getData().available() > 0) {
            this.caddr = HostAddresses.parse(derValue2.getData(), (byte)9, true);
        }
        if (derValue2.getData().available() > 0) {
            this.authorizationData = AuthorizationData.parse(derValue2.getData(), (byte)10, true);
        }
        if (derValue2.getData().available() > 0) {
            throw new Asn1Exception(906);
        }
    }
    
    public byte[] asn1Encode() throws Asn1Exception, IOException {
        final DerOutputStream derOutputStream = new DerOutputStream();
        final DerOutputStream derOutputStream2 = new DerOutputStream();
        derOutputStream.write(DerValue.createTag((byte)(-128), true, (byte)0), this.flags.asn1Encode());
        derOutputStream.write(DerValue.createTag((byte)(-128), true, (byte)1), this.key.asn1Encode());
        derOutputStream.write(DerValue.createTag((byte)(-128), true, (byte)2), this.cname.getRealm().asn1Encode());
        derOutputStream.write(DerValue.createTag((byte)(-128), true, (byte)3), this.cname.asn1Encode());
        derOutputStream.write(DerValue.createTag((byte)(-128), true, (byte)4), this.transited.asn1Encode());
        derOutputStream.write(DerValue.createTag((byte)(-128), true, (byte)5), this.authtime.asn1Encode());
        if (this.starttime != null) {
            derOutputStream.write(DerValue.createTag((byte)(-128), true, (byte)6), this.starttime.asn1Encode());
        }
        derOutputStream.write(DerValue.createTag((byte)(-128), true, (byte)7), this.endtime.asn1Encode());
        if (this.renewTill != null) {
            derOutputStream.write(DerValue.createTag((byte)(-128), true, (byte)8), this.renewTill.asn1Encode());
        }
        if (this.caddr != null) {
            derOutputStream.write(DerValue.createTag((byte)(-128), true, (byte)9), this.caddr.asn1Encode());
        }
        if (this.authorizationData != null) {
            derOutputStream.write(DerValue.createTag((byte)(-128), true, (byte)10), this.authorizationData.asn1Encode());
        }
        derOutputStream2.write((byte)48, derOutputStream);
        final DerOutputStream derOutputStream3 = new DerOutputStream();
        derOutputStream3.write(DerValue.createTag((byte)64, true, (byte)3), derOutputStream2);
        return derOutputStream3.toByteArray();
    }
}
