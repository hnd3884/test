package sun.security.krb5;

import sun.security.krb5.internal.Krb5;
import java.net.UnknownHostException;
import java.util.Arrays;
import sun.security.krb5.internal.SeqNumber;
import sun.security.krb5.internal.APOptions;
import sun.security.krb5.internal.KDCReqBody;
import sun.security.krb5.internal.crypto.Nonce;
import sun.security.krb5.internal.crypto.EType;
import java.time.Instant;
import java.io.IOException;
import sun.security.krb5.internal.AuthorizationData;
import sun.security.krb5.internal.HostAddresses;
import sun.security.krb5.internal.PAData;
import sun.security.krb5.internal.KDCOptions;
import sun.security.krb5.internal.Ticket;
import sun.security.krb5.internal.KerberosTime;
import sun.security.krb5.internal.TGSReq;

public class KrbTgsReq
{
    private PrincipalName princName;
    private PrincipalName clientAlias;
    private PrincipalName servName;
    private PrincipalName serverAlias;
    private TGSReq tgsReqMessg;
    private KerberosTime ctime;
    private Ticket secondTicket;
    private boolean useSubkey;
    EncryptionKey tgsReqKey;
    private static final boolean DEBUG;
    private byte[] obuf;
    private byte[] ibuf;
    
    public KrbTgsReq(final KDCOptions kdcOptions, final Credentials credentials, final PrincipalName principalName, final PrincipalName principalName2, final PrincipalName principalName3, final PrincipalName principalName4, final Ticket[] array, final PAData[] array2) throws KrbException, IOException {
        this(kdcOptions, credentials, principalName, principalName2, principalName3, principalName4, null, null, null, null, null, null, array, null, array2);
    }
    
    KrbTgsReq(final KDCOptions kdcOptions, final Credentials credentials, final PrincipalName principalName, final PrincipalName principalName2, final KerberosTime kerberosTime, final KerberosTime kerberosTime2, final KerberosTime kerberosTime3, final int[] array, final HostAddresses hostAddresses, final AuthorizationData authorizationData, final Ticket[] array2, final EncryptionKey encryptionKey) throws KrbException, IOException {
        this(kdcOptions, credentials, credentials.getClient(), credentials.getClientAlias(), principalName, principalName2, kerberosTime, kerberosTime2, kerberosTime3, array, hostAddresses, authorizationData, array2, encryptionKey, null);
    }
    
    private KrbTgsReq(final KDCOptions kdcOptions, final Credentials credentials, final PrincipalName princName, final PrincipalName clientAlias, final PrincipalName servName, final PrincipalName serverAlias, KerberosTime kerberosTime, final KerberosTime kerberosTime2, KerberosTime kerberosTime3, final int[] array, final HostAddresses hostAddresses, final AuthorizationData authorizationData, Ticket[] array2, final EncryptionKey encryptionKey, final PAData[] array3) throws KrbException, IOException {
        this.secondTicket = null;
        this.useSubkey = false;
        this.princName = princName;
        this.clientAlias = clientAlias;
        this.servName = servName;
        this.serverAlias = serverAlias;
        this.ctime = KerberosTime.now();
        if (kdcOptions.get(1) && !credentials.flags.get(1)) {
            kdcOptions.set(1, false);
        }
        if (kdcOptions.get(2) && !credentials.flags.get(1)) {
            throw new KrbException(101);
        }
        if (kdcOptions.get(3) && !credentials.flags.get(3)) {
            throw new KrbException(101);
        }
        if (kdcOptions.get(4) && !credentials.flags.get(3)) {
            throw new KrbException(101);
        }
        if (kdcOptions.get(5) && !credentials.flags.get(5)) {
            throw new KrbException(101);
        }
        if (kdcOptions.get(8) && !credentials.flags.get(8)) {
            throw new KrbException(101);
        }
        if (kdcOptions.get(6)) {
            if (!credentials.flags.get(6)) {
                throw new KrbException(101);
            }
        }
        else if (kerberosTime != null) {
            kerberosTime = null;
        }
        if (kdcOptions.get(8)) {
            if (!credentials.flags.get(8)) {
                throw new KrbException(101);
            }
        }
        else if (kerberosTime3 != null) {
            kerberosTime3 = null;
        }
        if (kdcOptions.get(28) || kdcOptions.get(14)) {
            if (array2 == null) {
                throw new KrbException(101);
            }
            this.secondTicket = array2[0];
        }
        else if (array2 != null) {
            array2 = null;
        }
        this.tgsReqMessg = this.createRequest(kdcOptions, credentials.ticket, credentials.key, this.ctime, this.princName, this.servName, kerberosTime, kerberosTime2, kerberosTime3, array, hostAddresses, authorizationData, array2, encryptionKey, array3);
        this.obuf = this.tgsReqMessg.asn1Encode();
        if (credentials.flags.get(2)) {
            kdcOptions.set(2, true);
        }
    }
    
    public void send() throws IOException, KrbException {
        String realmString = null;
        if (this.servName != null) {
            realmString = this.servName.getRealmString();
        }
        this.ibuf = new KdcComm(realmString).send(this.obuf);
    }
    
    public KrbTgsRep getReply() throws KrbException, IOException {
        return new KrbTgsRep(this.ibuf, this);
    }
    
    public Credentials sendAndGetCreds() throws IOException, KrbException {
        this.send();
        return this.getReply().getCreds();
    }
    
    KerberosTime getCtime() {
        return this.ctime;
    }
    
    private TGSReq createRequest(final KDCOptions kdcOptions, final Ticket ticket, final EncryptionKey tgsReqKey, final KerberosTime kerberosTime, final PrincipalName principalName, final PrincipalName principalName2, final KerberosTime kerberosTime2, final KerberosTime kerberosTime3, final KerberosTime kerberosTime4, final int[] array, final HostAddresses hostAddresses, final AuthorizationData authorizationData, final Ticket[] array2, final EncryptionKey tgsReqKey2, final PAData[] array3) throws IOException, KrbException, UnknownHostException {
        KerberosTime kerberosTime5;
        if (kerberosTime3 == null) {
            final String value = Config.getInstance().get("libdefaults", "ticket_lifetime");
            if (value != null) {
                kerberosTime5 = new KerberosTime(Instant.now().plusSeconds(Config.duration(value)));
            }
            else {
                kerberosTime5 = new KerberosTime(0L);
            }
        }
        else {
            kerberosTime5 = kerberosTime3;
        }
        this.tgsReqKey = tgsReqKey;
        int[] defaults;
        if (array == null) {
            defaults = EType.getDefaults("default_tgs_enctypes");
        }
        else {
            defaults = array;
        }
        EncryptionKey encryptionKey = null;
        EncryptedData encryptedData = null;
        if (authorizationData != null) {
            final byte[] asn1Encode = authorizationData.asn1Encode();
            if (tgsReqKey2 != null) {
                encryptionKey = tgsReqKey2;
                this.tgsReqKey = tgsReqKey2;
                this.useSubkey = true;
                encryptedData = new EncryptedData(encryptionKey, asn1Encode, 5);
            }
            else {
                encryptedData = new EncryptedData(tgsReqKey, asn1Encode, 4);
            }
        }
        final KDCReqBody kdcReqBody = new KDCReqBody(kdcOptions, principalName, principalName2, kerberosTime2, kerberosTime5, kerberosTime4, Nonce.value(), defaults, hostAddresses, encryptedData, array2);
        final PAData paData = new PAData(1, new KrbApReq(new APOptions(), ticket, tgsReqKey, principalName, new Checksum(Checksum.CKSUMTYPE_DEFAULT, kdcReqBody.asn1Encode(12), tgsReqKey, 6), kerberosTime, encryptionKey, null, null).getMessage());
        PAData[] array4;
        if (array3 != null) {
            array4 = Arrays.copyOf(array3, array3.length + 1);
            array4[array3.length] = paData;
        }
        else {
            array4 = new PAData[] { paData };
        }
        return new TGSReq(array4, kdcReqBody);
    }
    
    TGSReq getMessage() {
        return this.tgsReqMessg;
    }
    
    Ticket getSecondTicket() {
        return this.secondTicket;
    }
    
    PrincipalName getClientAlias() {
        return this.clientAlias;
    }
    
    PrincipalName getServerAlias() {
        return this.serverAlias;
    }
    
    private static void debug(final String s) {
    }
    
    boolean usedSubkey() {
        return this.useSubkey;
    }
    
    static {
        DEBUG = Krb5.DEBUG;
    }
}
