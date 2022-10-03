package sun.security.krb5;

import sun.security.krb5.internal.KrbApErrException;
import sun.security.krb5.internal.KdcErrException;
import sun.security.krb5.internal.KRBSafeBody;
import sun.security.krb5.internal.KRBSafe;
import java.io.IOException;
import sun.security.krb5.internal.HostAddress;
import sun.security.krb5.internal.SeqNumber;
import sun.security.krb5.internal.KerberosTime;

class KrbSafe extends KrbAppMessage
{
    private byte[] obuf;
    private byte[] userData;
    
    public KrbSafe(final byte[] array, final Credentials credentials, final EncryptionKey encryptionKey, final KerberosTime kerberosTime, final SeqNumber seqNumber, final HostAddress hostAddress, final HostAddress hostAddress2) throws KrbException, IOException {
        EncryptionKey key;
        if (encryptionKey != null) {
            key = encryptionKey;
        }
        else {
            key = credentials.key;
        }
        this.obuf = this.mk_safe(array, key, kerberosTime, seqNumber, hostAddress, hostAddress2);
    }
    
    public KrbSafe(final byte[] array, final Credentials credentials, final EncryptionKey encryptionKey, final SeqNumber seqNumber, final HostAddress hostAddress, final HostAddress hostAddress2, final boolean b, final boolean b2) throws KrbException, IOException {
        final KRBSafe krbSafe = new KRBSafe(array);
        EncryptionKey key;
        if (encryptionKey != null) {
            key = encryptionKey;
        }
        else {
            key = credentials.key;
        }
        this.userData = this.rd_safe(krbSafe, key, seqNumber, hostAddress, hostAddress2, b, b2, credentials.client);
    }
    
    public byte[] getMessage() {
        return this.obuf;
    }
    
    public byte[] getData() {
        return this.userData;
    }
    
    private byte[] mk_safe(final byte[] array, final EncryptionKey encryptionKey, final KerberosTime kerberosTime, final SeqNumber seqNumber, final HostAddress hostAddress, final HostAddress hostAddress2) throws Asn1Exception, IOException, KdcErrException, KrbApErrException, KrbCryptoException {
        Integer n = null;
        Integer n2 = null;
        if (kerberosTime != null) {
            n = new Integer(kerberosTime.getMicroSeconds());
        }
        if (seqNumber != null) {
            n2 = new Integer(seqNumber.current());
            seqNumber.step();
        }
        final KRBSafeBody krbSafeBody = new KRBSafeBody(array, kerberosTime, n, n2, hostAddress, hostAddress2);
        final KRBSafe krbSafe = new KRBSafe(krbSafeBody, new Checksum(Checksum.SAFECKSUMTYPE_DEFAULT, krbSafeBody.asn1Encode(), encryptionKey, 15));
        krbSafe.asn1Encode();
        return krbSafe.asn1Encode();
    }
    
    private byte[] rd_safe(final KRBSafe krbSafe, final EncryptionKey encryptionKey, final SeqNumber seqNumber, final HostAddress hostAddress, final HostAddress hostAddress2, final boolean b, final boolean b2, final PrincipalName principalName) throws Asn1Exception, KdcErrException, KrbApErrException, IOException, KrbCryptoException {
        if (!krbSafe.cksum.verifyKeyedChecksum(krbSafe.safeBody.asn1Encode(), encryptionKey, 15)) {
            throw new KrbApErrException(41);
        }
        this.check(krbSafe.safeBody.timestamp, krbSafe.safeBody.usec, krbSafe.safeBody.seqNumber, krbSafe.safeBody.sAddress, krbSafe.safeBody.rAddress, seqNumber, hostAddress, hostAddress2, b, b2, principalName);
        return krbSafe.safeBody.userData;
    }
}
