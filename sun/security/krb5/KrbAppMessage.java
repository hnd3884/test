package sun.security.krb5;

import sun.security.krb5.internal.Krb5;
import sun.security.krb5.internal.KrbApErrException;
import sun.security.krb5.internal.SeqNumber;
import sun.security.krb5.internal.HostAddress;
import sun.security.krb5.internal.KerberosTime;

abstract class KrbAppMessage
{
    private static boolean DEBUG;
    
    void check(KerberosTime withMicroSeconds, final Integer n, final Integer n2, final HostAddress hostAddress, final HostAddress hostAddress2, final SeqNumber seqNumber, final HostAddress hostAddress3, final HostAddress hostAddress4, final boolean b, final boolean b2, final PrincipalName principalName) throws KrbApErrException {
        if (hostAddress3 != null && (hostAddress == null || hostAddress3 == null || !hostAddress.equals(hostAddress3))) {
            if (KrbAppMessage.DEBUG && hostAddress == null) {
                System.out.println("packetSAddress is null");
            }
            if (KrbAppMessage.DEBUG && hostAddress3 == null) {
                System.out.println("sAddress is null");
            }
            throw new KrbApErrException(38);
        }
        if (hostAddress4 != null && (hostAddress2 == null || hostAddress4 == null || !hostAddress2.equals(hostAddress4))) {
            throw new KrbApErrException(38);
        }
        if (withMicroSeconds != null) {
            if (n != null) {
                withMicroSeconds = withMicroSeconds.withMicroSeconds(n);
            }
            if (!withMicroSeconds.inClockSkew()) {
                throw new KrbApErrException(37);
            }
        }
        else if (b) {
            throw new KrbApErrException(37);
        }
        if (seqNumber == null && b2) {
            throw new KrbApErrException(400);
        }
        if (n2 != null && seqNumber != null) {
            if (n2 != seqNumber.current()) {
                throw new KrbApErrException(42);
            }
            seqNumber.step();
        }
        else if (b2) {
            throw new KrbApErrException(42);
        }
        if (withMicroSeconds == null && n2 == null) {
            throw new KrbApErrException(41);
        }
    }
    
    static {
        KrbAppMessage.DEBUG = Krb5.DEBUG;
    }
}
