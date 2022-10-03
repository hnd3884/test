package sun.security.krb5;

import sun.security.krb5.internal.KrbApErrException;
import sun.security.util.DerValue;
import sun.security.krb5.internal.KdcErrException;
import sun.security.krb5.internal.EncKrbPrivPart;
import sun.security.krb5.internal.KRBPriv;
import java.io.IOException;
import sun.security.krb5.internal.HostAddress;
import sun.security.krb5.internal.SeqNumber;
import sun.security.krb5.internal.KerberosTime;

class KrbPriv extends KrbAppMessage
{
    private byte[] obuf;
    private byte[] userData;
    
    private KrbPriv(final byte[] array, final Credentials credentials, final EncryptionKey encryptionKey, final KerberosTime kerberosTime, final SeqNumber seqNumber, final HostAddress hostAddress, final HostAddress hostAddress2) throws KrbException, IOException {
        EncryptionKey key;
        if (encryptionKey != null) {
            key = encryptionKey;
        }
        else {
            key = credentials.key;
        }
        this.obuf = this.mk_priv(array, key, kerberosTime, seqNumber, hostAddress, hostAddress2);
    }
    
    private KrbPriv(final byte[] array, final Credentials credentials, final EncryptionKey encryptionKey, final SeqNumber seqNumber, final HostAddress hostAddress, final HostAddress hostAddress2, final boolean b, final boolean b2) throws KrbException, IOException {
        final KRBPriv krbPriv = new KRBPriv(array);
        EncryptionKey key;
        if (encryptionKey != null) {
            key = encryptionKey;
        }
        else {
            key = credentials.key;
        }
        this.userData = this.rd_priv(krbPriv, key, seqNumber, hostAddress, hostAddress2, b, b2, credentials.client);
    }
    
    public byte[] getMessage() throws KrbException {
        return this.obuf;
    }
    
    public byte[] getData() {
        return this.userData;
    }
    
    private byte[] mk_priv(final byte[] array, final EncryptionKey encryptionKey, final KerberosTime kerberosTime, final SeqNumber seqNumber, final HostAddress hostAddress, final HostAddress hostAddress2) throws Asn1Exception, IOException, KdcErrException, KrbCryptoException {
        Integer n = null;
        Integer n2 = null;
        if (kerberosTime != null) {
            n = new Integer(kerberosTime.getMicroSeconds());
        }
        if (seqNumber != null) {
            n2 = new Integer(seqNumber.current());
            seqNumber.step();
        }
        final KRBPriv krbPriv = new KRBPriv(new EncryptedData(encryptionKey, new EncKrbPrivPart(array, kerberosTime, n, n2, hostAddress, hostAddress2).asn1Encode(), 13));
        krbPriv.asn1Encode();
        return krbPriv.asn1Encode();
    }
    
    private byte[] rd_priv(final KRBPriv krbPriv, final EncryptionKey encryptionKey, final SeqNumber seqNumber, final HostAddress hostAddress, final HostAddress hostAddress2, final boolean b, final boolean b2, final PrincipalName principalName) throws Asn1Exception, KdcErrException, KrbApErrException, IOException, KrbCryptoException {
        final EncKrbPrivPart encKrbPrivPart = new EncKrbPrivPart(new DerValue(krbPriv.encPart.reset(krbPriv.encPart.decrypt(encryptionKey, 13))));
        this.check(encKrbPrivPart.timestamp, encKrbPrivPart.usec, encKrbPrivPart.seqNumber, encKrbPrivPart.sAddress, encKrbPrivPart.rAddress, seqNumber, hostAddress, hostAddress2, b, b2, principalName);
        return encKrbPrivPart.userData;
    }
}
