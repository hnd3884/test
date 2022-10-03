package sun.security.krb5;

import sun.security.krb5.internal.Krb5;
import java.util.Arrays;
import sun.security.krb5.internal.HostAddress;
import sun.security.krb5.internal.rcache.AuthTimeWithHash;
import java.security.NoSuchAlgorithmException;
import java.security.MessageDigest;
import sun.security.krb5.internal.KrbApErrException;
import sun.security.krb5.internal.EncTicketPart;
import sun.security.krb5.internal.crypto.EType;
import sun.security.krb5.internal.KRBError;
import sun.security.util.DerValue;
import sun.security.krb5.internal.KdcErrException;
import sun.security.krb5.internal.Ticket;
import java.net.InetAddress;
import sun.security.jgss.krb5.Krb5AcceptCredential;
import java.io.IOException;
import sun.security.krb5.internal.AuthorizationData;
import sun.security.krb5.internal.SeqNumber;
import sun.security.krb5.internal.LocalSeqNumber;
import sun.security.krb5.internal.APOptions;
import sun.security.krb5.internal.ReplayCache;
import sun.security.krb5.internal.APReq;
import sun.security.krb5.internal.Authenticator;
import sun.security.krb5.internal.KerberosTime;

public class KrbApReq
{
    private byte[] obuf;
    private KerberosTime ctime;
    private int cusec;
    private Authenticator authenticator;
    private Credentials creds;
    private APReq apReqMessg;
    private static ReplayCache rcache;
    private static boolean DEBUG;
    private static final char[] hexConst;
    
    public KrbApReq(final Credentials credentials, final boolean b, final boolean b2, final boolean b3, final Checksum checksum) throws Asn1Exception, KrbCryptoException, KrbException, IOException {
        final APOptions apOptions = b ? new APOptions(2) : new APOptions();
        if (KrbApReq.DEBUG) {
            System.out.println(">>> KrbApReq: APOptions are " + apOptions);
        }
        this.init(apOptions, credentials, checksum, b2 ? new EncryptionKey(credentials.getSessionKey()) : null, new LocalSeqNumber(), null, 11);
    }
    
    public KrbApReq(final byte[] obuf, final Krb5AcceptCredential krb5AcceptCredential, final InetAddress inetAddress) throws KrbException, IOException {
        this.obuf = obuf;
        if (this.apReqMessg == null) {
            this.decode();
        }
        this.authenticate(krb5AcceptCredential, inetAddress);
    }
    
    KrbApReq(final APOptions apOptions, final Ticket ticket, final EncryptionKey encryptionKey, final PrincipalName principalName, final Checksum checksum, final KerberosTime kerberosTime, final EncryptionKey encryptionKey2, final SeqNumber seqNumber, final AuthorizationData authorizationData) throws Asn1Exception, IOException, KdcErrException, KrbCryptoException {
        this.init(apOptions, ticket, encryptionKey, principalName, checksum, kerberosTime, encryptionKey2, seqNumber, authorizationData, 7);
    }
    
    private void init(final APOptions apOptions, final Credentials credentials, final Checksum checksum, final EncryptionKey encryptionKey, final SeqNumber seqNumber, final AuthorizationData authorizationData, final int n) throws KrbException, IOException {
        this.ctime = KerberosTime.now();
        this.init(apOptions, credentials.ticket, credentials.key, credentials.client, checksum, this.ctime, encryptionKey, seqNumber, authorizationData, n);
    }
    
    private void init(final APOptions apOptions, final Ticket ticket, final EncryptionKey encryptionKey, final PrincipalName principalName, final Checksum checksum, final KerberosTime kerberosTime, final EncryptionKey encryptionKey2, final SeqNumber seqNumber, final AuthorizationData authorizationData, final int n) throws Asn1Exception, IOException, KdcErrException, KrbCryptoException {
        this.createMessage(apOptions, ticket, encryptionKey, principalName, checksum, kerberosTime, encryptionKey2, seqNumber, authorizationData, n);
        this.obuf = this.apReqMessg.asn1Encode();
    }
    
    void decode() throws KrbException, IOException {
        this.decode(new DerValue(this.obuf));
    }
    
    void decode(final DerValue derValue) throws KrbException, IOException {
        this.apReqMessg = null;
        try {
            this.apReqMessg = new APReq(derValue);
        }
        catch (final Asn1Exception ex) {
            this.apReqMessg = null;
            final KRBError krbError = new KRBError(derValue);
            final String errorString = krbError.getErrorString();
            String substring;
            if (errorString.charAt(errorString.length() - 1) == '\0') {
                substring = errorString.substring(0, errorString.length() - 1);
            }
            else {
                substring = errorString;
            }
            final KrbException ex2 = new KrbException(krbError.getErrorCode(), substring);
            ex2.initCause(ex);
            throw ex2;
        }
    }
    
    private void authenticate(final Krb5AcceptCredential krb5AcceptCredential, final InetAddress inetAddress) throws KrbException, IOException {
        final int eType = this.apReqMessg.ticket.encPart.getEType();
        final EncryptionKey key = EncryptionKey.findKey(eType, this.apReqMessg.ticket.encPart.getKeyVersionNumber(), krb5AcceptCredential.getKrb5EncryptionKeys(this.apReqMessg.ticket.sname));
        if (key == null) {
            throw new KrbException(400, "Cannot find key of appropriate type to decrypt AP REP - " + EType.toString(eType));
        }
        final EncTicketPart encTicketPart = new EncTicketPart(this.apReqMessg.ticket.encPart.reset(this.apReqMessg.ticket.encPart.decrypt(key, 2)));
        checkPermittedEType(encTicketPart.key.getEType());
        this.authenticator = new Authenticator(this.apReqMessg.authenticator.reset(this.apReqMessg.authenticator.decrypt(encTicketPart.key, 11)));
        this.ctime = this.authenticator.ctime;
        this.cusec = this.authenticator.cusec;
        this.authenticator.ctime = this.authenticator.ctime.withMicroSeconds(this.authenticator.cusec);
        if (!this.authenticator.cname.equals(encTicketPart.cname)) {
            throw new KrbApErrException(36);
        }
        if (!this.authenticator.ctime.inClockSkew()) {
            throw new KrbApErrException(37);
        }
        byte[] digest;
        try {
            digest = MessageDigest.getInstance("MD5").digest(this.apReqMessg.authenticator.cipher);
        }
        catch (final NoSuchAlgorithmException ex) {
            throw new AssertionError((Object)"Impossible");
        }
        final char[] array = new char[digest.length * 2];
        for (int i = 0; i < digest.length; ++i) {
            array[2 * i] = KrbApReq.hexConst[(digest[i] & 0xFF) >> 4];
            array[2 * i + 1] = KrbApReq.hexConst[digest[i] & 0xF];
        }
        KrbApReq.rcache.checkAndStore(KerberosTime.now(), new AuthTimeWithHash(this.authenticator.cname.toString(), this.apReqMessg.ticket.sname.toString(), this.authenticator.ctime.getSeconds(), this.authenticator.cusec, new String(array)));
        if (inetAddress != null) {
            final HostAddress hostAddress = new HostAddress(inetAddress);
            if (encTicketPart.caddr != null && !encTicketPart.caddr.inList(hostAddress)) {
                if (KrbApReq.DEBUG) {
                    System.out.println(">>> KrbApReq: initiator is " + hostAddress.getInetAddress() + ", but caddr is " + Arrays.toString(encTicketPart.caddr.getInetAddresses()));
                }
                throw new KrbApErrException(38);
            }
        }
        final KerberosTime now = KerberosTime.now();
        if ((encTicketPart.starttime != null && encTicketPart.starttime.greaterThanWRTClockSkew(now)) || encTicketPart.flags.get(7)) {
            throw new KrbApErrException(33);
        }
        if (encTicketPart.endtime != null && now.greaterThanWRTClockSkew(encTicketPart.endtime)) {
            throw new KrbApErrException(32);
        }
        this.creds = new Credentials(this.apReqMessg.ticket, this.authenticator.cname, null, this.apReqMessg.ticket.sname, null, encTicketPart.key, encTicketPart.flags, encTicketPart.authtime, encTicketPart.starttime, encTicketPart.endtime, encTicketPart.renewTill, encTicketPart.caddr, encTicketPart.authorizationData);
        if (KrbApReq.DEBUG) {
            System.out.println(">>> KrbApReq: authenticate succeed.");
        }
    }
    
    public Credentials getCreds() {
        return this.creds;
    }
    
    KerberosTime getCtime() {
        if (this.ctime != null) {
            return this.ctime;
        }
        return this.authenticator.ctime;
    }
    
    int cusec() {
        return this.cusec;
    }
    
    APOptions getAPOptions() throws KrbException, IOException {
        if (this.apReqMessg == null) {
            this.decode();
        }
        if (this.apReqMessg != null) {
            return this.apReqMessg.apOptions;
        }
        return null;
    }
    
    public boolean getMutualAuthRequired() throws KrbException, IOException {
        if (this.apReqMessg == null) {
            this.decode();
        }
        return this.apReqMessg != null && this.apReqMessg.apOptions.get(2);
    }
    
    boolean useSessionKey() throws KrbException, IOException {
        if (this.apReqMessg == null) {
            this.decode();
        }
        return this.apReqMessg != null && this.apReqMessg.apOptions.get(1);
    }
    
    public EncryptionKey getSubKey() {
        return this.authenticator.getSubKey();
    }
    
    public Integer getSeqNumber() {
        return this.authenticator.getSeqNumber();
    }
    
    public Checksum getChecksum() {
        return this.authenticator.getChecksum();
    }
    
    public byte[] getMessage() {
        return this.obuf;
    }
    
    public PrincipalName getClient() {
        return this.creds.getClient();
    }
    
    private void createMessage(final APOptions apOptions, final Ticket ticket, final EncryptionKey encryptionKey, final PrincipalName principalName, final Checksum checksum, final KerberosTime kerberosTime, final EncryptionKey encryptionKey2, final SeqNumber seqNumber, final AuthorizationData authorizationData, final int n) throws Asn1Exception, IOException, KdcErrException, KrbCryptoException {
        Integer n2 = null;
        if (seqNumber != null) {
            n2 = new Integer(seqNumber.current());
        }
        this.authenticator = new Authenticator(principalName, checksum, kerberosTime.getMicroSeconds(), kerberosTime, encryptionKey2, n2, authorizationData);
        this.apReqMessg = new APReq(apOptions, ticket, new EncryptedData(encryptionKey, this.authenticator.asn1Encode(), n));
    }
    
    private static void checkPermittedEType(final int n) throws KrbException {
        if (!EType.isSupported(n, EType.getDefaults("permitted_enctypes"))) {
            throw new KrbException(EType.toString(n) + " encryption type not in permitted_enctypes list");
        }
    }
    
    static {
        KrbApReq.rcache = ReplayCache.getInstance();
        KrbApReq.DEBUG = Krb5.DEBUG;
        hexConst = "0123456789ABCDEF".toCharArray();
    }
}
