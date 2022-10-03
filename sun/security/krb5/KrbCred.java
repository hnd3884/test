package sun.security.krb5;

import sun.security.krb5.internal.Krb5;
import sun.security.krb5.internal.TicketFlags;
import sun.security.util.DerValue;
import sun.security.krb5.internal.HostAddress;
import sun.security.krb5.internal.KrbCredInfo;
import java.io.IOException;
import sun.security.krb5.internal.AuthorizationData;
import sun.security.krb5.internal.HostAddresses;
import sun.security.krb5.internal.KDCOptions;
import sun.security.krb5.internal.KerberosTime;
import sun.security.krb5.internal.EncKrbCredPart;
import sun.security.krb5.internal.Ticket;
import sun.security.krb5.internal.KRBCred;

public class KrbCred
{
    private static boolean DEBUG;
    private byte[] obuf;
    private KRBCred credMessg;
    private Ticket ticket;
    private EncKrbCredPart encPart;
    private Credentials creds;
    private KerberosTime timeStamp;
    
    public KrbCred(final Credentials credentials, final Credentials credentials2, final EncryptionKey encryptionKey) throws KrbException, IOException {
        this.obuf = null;
        this.credMessg = null;
        this.ticket = null;
        this.encPart = null;
        this.creds = null;
        this.timeStamp = null;
        final PrincipalName client = credentials.getClient();
        final PrincipalName server = credentials.getServer();
        if (!credentials2.getClient().equals(client)) {
            throw new KrbException(60, "Client principal does not match");
        }
        final KDCOptions kdcOptions = new KDCOptions();
        kdcOptions.set(2, true);
        kdcOptions.set(1, true);
        this.credMessg = this.createMessage(new KrbTgsReq(kdcOptions, credentials, server, null, null, null, null, null, null, null, null, null).sendAndGetCreds(), encryptionKey);
        this.obuf = this.credMessg.asn1Encode();
    }
    
    KRBCred createMessage(final Credentials credentials, final EncryptionKey encryptionKey) throws KrbException, IOException {
        final KrbCredInfo krbCredInfo = new KrbCredInfo(credentials.getSessionKey(), credentials.getClient(), credentials.flags, credentials.authTime, credentials.startTime, credentials.endTime, credentials.renewTill, credentials.getServer(), credentials.cAddr);
        this.timeStamp = KerberosTime.now();
        return this.credMessg = new KRBCred(new Ticket[] { credentials.ticket }, new EncryptedData(encryptionKey, new EncKrbCredPart(new KrbCredInfo[] { krbCredInfo }, this.timeStamp, null, null, null, null).asn1Encode(), 14));
    }
    
    public KrbCred(final byte[] array, EncryptionKey null_KEY) throws KrbException, IOException {
        this.obuf = null;
        this.credMessg = null;
        this.ticket = null;
        this.encPart = null;
        this.creds = null;
        this.timeStamp = null;
        this.credMessg = new KRBCred(array);
        this.ticket = this.credMessg.tickets[0];
        if (this.credMessg.encPart.getEType() == 0) {
            null_KEY = EncryptionKey.NULL_KEY;
        }
        final EncKrbCredPart encKrbCredPart = new EncKrbCredPart(new DerValue(this.credMessg.encPart.reset(this.credMessg.encPart.decrypt(null_KEY, 14))));
        this.timeStamp = encKrbCredPart.timeStamp;
        final KrbCredInfo krbCredInfo = encKrbCredPart.ticketInfo[0];
        final EncryptionKey key = krbCredInfo.key;
        final PrincipalName pname = krbCredInfo.pname;
        final TicketFlags flags = krbCredInfo.flags;
        final KerberosTime authtime = krbCredInfo.authtime;
        final KerberosTime starttime = krbCredInfo.starttime;
        final KerberosTime endtime = krbCredInfo.endtime;
        final KerberosTime renewTill = krbCredInfo.renewTill;
        final PrincipalName sname = krbCredInfo.sname;
        final HostAddresses caddr = krbCredInfo.caddr;
        if (KrbCred.DEBUG) {
            System.out.println(">>>Delegated Creds have pname=" + pname + " sname=" + sname + " authtime=" + authtime + " starttime=" + starttime + " endtime=" + endtime + "renewTill=" + renewTill);
        }
        this.creds = new Credentials(this.ticket, pname, null, sname, null, key, flags, authtime, starttime, endtime, renewTill, caddr);
    }
    
    public Credentials[] getDelegatedCreds() {
        return new Credentials[] { this.creds };
    }
    
    public byte[] getMessage() {
        return this.obuf;
    }
    
    static {
        KrbCred.DEBUG = Krb5.DEBUG;
    }
}
