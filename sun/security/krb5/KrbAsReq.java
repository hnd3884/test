package sun.security.krb5;

import java.io.IOException;
import sun.security.krb5.internal.Ticket;
import sun.security.krb5.internal.KDCReqBody;
import sun.security.krb5.internal.crypto.Nonce;
import java.time.Instant;
import java.util.Arrays;
import sun.security.krb5.internal.PAEncTSEnc;
import sun.security.krb5.internal.Krb5;
import sun.security.krb5.internal.PAData;
import sun.security.krb5.internal.HostAddresses;
import sun.security.krb5.internal.KerberosTime;
import sun.security.krb5.internal.KDCOptions;
import sun.security.krb5.internal.ASReq;

public class KrbAsReq
{
    private ASReq asReqMessg;
    private boolean DEBUG;
    
    public KrbAsReq(final EncryptionKey encryptionKey, KDCOptions kdcOptions, final PrincipalName principalName, PrincipalName tgsService, KerberosTime kerberosTime, KerberosTime kerberosTime2, KerberosTime kerberosTime3, final int[] array, HostAddresses localAddresses, final PAData[] array2) throws KrbException, IOException {
        this.DEBUG = Krb5.DEBUG;
        if (kdcOptions == null) {
            kdcOptions = new KDCOptions();
        }
        if (kdcOptions.get(2) || kdcOptions.get(4) || kdcOptions.get(28) || kdcOptions.get(30) || kdcOptions.get(31)) {
            throw new KrbException(101);
        }
        if (!kdcOptions.get(6)) {
            if (kerberosTime != null) {
                kerberosTime = null;
            }
        }
        PAData[] array3 = null;
        if (encryptionKey != null) {
            array3 = new PAData[] { new PAData(2, new EncryptedData(encryptionKey, new PAEncTSEnc().asn1Encode(), 1).asn1Encode()) };
        }
        if (array2 != null && array2.length > 0) {
            if (array3 == null) {
                array3 = new PAData[array2.length];
            }
            else {
                array3 = Arrays.copyOf(array3, array3.length + array2.length);
            }
            System.arraycopy(array2, 0, array3, array3.length - array2.length, array2.length);
        }
        if (principalName.getRealm() == null) {
            throw new RealmException(601, "default realm not specified ");
        }
        if (this.DEBUG) {
            System.out.println(">>> KrbAsReq creating message");
        }
        final Config instance = Config.getInstance();
        if (localAddresses == null && instance.useAddresses()) {
            localAddresses = HostAddresses.getLocalAddresses();
        }
        if (tgsService == null) {
            final String realmAsString = principalName.getRealmAsString();
            tgsService = PrincipalName.tgsService(realmAsString, realmAsString);
        }
        if (kerberosTime2 == null) {
            final String value = instance.get("libdefaults", "ticket_lifetime");
            if (value != null) {
                kerberosTime2 = new KerberosTime(Instant.now().plusSeconds(Config.duration(value)));
            }
            else {
                kerberosTime2 = new KerberosTime(0L);
            }
        }
        if (kerberosTime3 == null) {
            final String value2 = instance.get("libdefaults", "renew_lifetime");
            if (value2 != null) {
                kerberosTime3 = new KerberosTime(Instant.now().plusSeconds(Config.duration(value2)));
            }
        }
        if (kerberosTime3 != null) {
            kdcOptions.set(8, true);
            if (kerberosTime2.greaterThan(kerberosTime3)) {
                kerberosTime3 = kerberosTime2;
            }
        }
        this.asReqMessg = new ASReq(array3, new KDCReqBody(kdcOptions, principalName, tgsService, kerberosTime, kerberosTime2, kerberosTime3, Nonce.value(), array, localAddresses, null, null));
    }
    
    byte[] encoding() throws IOException, Asn1Exception {
        return this.asReqMessg.asn1Encode();
    }
    
    ASReq getMessage() {
        return this.asReqMessg;
    }
}
