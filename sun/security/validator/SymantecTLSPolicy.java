package sun.security.validator;

import java.util.Collection;
import java.util.HashSet;
import java.util.Arrays;
import java.util.HashMap;
import java.time.Month;
import java.time.chrono.ChronoLocalDate;
import sun.security.x509.X509CertImpl;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.security.cert.X509Certificate;
import java.util.Set;
import java.util.Map;
import java.time.LocalDate;

final class SymantecTLSPolicy
{
    private static final LocalDate DECEMBER_31_2019;
    private static final Map<String, LocalDate> EXEMPT_SUBCAS;
    private static final Set<String> FINGERPRINTS;
    private static final LocalDate APRIL_16_2019;
    
    static void checkDistrust(final X509Certificate[] array) throws ValidatorException {
        final X509Certificate x509Certificate = array[array.length - 1];
        if (SymantecTLSPolicy.FINGERPRINTS.contains(fingerprint(x509Certificate))) {
            final LocalDate localDate = array[0].getNotBefore().toInstant().atZone(ZoneOffset.UTC).toLocalDate();
            if (array.length > 2) {
                final LocalDate localDate2 = SymantecTLSPolicy.EXEMPT_SUBCAS.get(fingerprint(array[array.length - 2]));
                if (localDate2 != null) {
                    checkNotBefore(localDate, localDate2, x509Certificate);
                    return;
                }
            }
            checkNotBefore(localDate, SymantecTLSPolicy.APRIL_16_2019, x509Certificate);
        }
    }
    
    private static String fingerprint(final X509Certificate x509Certificate) {
        return (x509Certificate instanceof X509CertImpl) ? ((X509CertImpl)x509Certificate).getFingerprint("SHA-256") : X509CertImpl.getFingerprint("SHA-256", x509Certificate);
    }
    
    private static void checkNotBefore(final LocalDate localDate, final LocalDate localDate2, final X509Certificate x509Certificate) throws ValidatorException {
        if (localDate.isAfter(localDate2)) {
            throw new ValidatorException("TLS Server certificate issued after " + localDate2 + " and anchored by a distrusted legacy Symantec root CA: " + x509Certificate.getSubjectX500Principal(), ValidatorException.T_UNTRUSTED_CERT, x509Certificate);
        }
    }
    
    private SymantecTLSPolicy() {
    }
    
    static {
        DECEMBER_31_2019 = LocalDate.of(2019, Month.DECEMBER, 31);
        (EXEMPT_SUBCAS = new HashMap<String, LocalDate>()).put("AC2B922ECFD5E01711772FEA8ED372DE9D1E2245FCE3F57A9CDBEC77296A424B", SymantecTLSPolicy.DECEMBER_31_2019);
        SymantecTLSPolicy.EXEMPT_SUBCAS.put("A4FE7C7F15155F3F0AEF7AAA83CF6E06DEB97CA3F909DF920AC1490882D488ED", SymantecTLSPolicy.DECEMBER_31_2019);
        FINGERPRINTS = new HashSet<String>(Arrays.asList("FF856A2D251DCD88D36656F450126798CFABAADE40799C722DE4D2B5DB36A73A", "37D51006C512EAAB626421F1EC8C92013FC5F82AE98EE533EB4619B8DEB4D06C", "5EDB7AC43B82A06A8761E8D7BE4979EBF2611F7DD79BF91C1C6B566A219ED766", "B478B812250DF878635C2AA7EC7D155EAA625EE82916E2CD294361886CD1FBD4", "A0459B9F63B22559F5FA5D4C6DB3F9F72FF19342033578F073BF1D1B46CBB912", "8D722F81A9C113C0791DF136A2966DB26C950A971DB46B4199F4EA54B78BFB9F", "A4310D50AF18A6447190372A86AFAF8B951FFB431D837F1E5688B45971ED1557", "4B03F45807AD70F21BFC2CAE71C9FDE4604C064CF5FFB686BAE5DBAAD7FDD34C", "3F9F27D583204B9E09C8A3D2066C4B57D3A2479C3693650880505698105DBCE9", "3A43E220FE7F3EA9653D1E21742EAC2B75C20FD8980305BC502CAF8C2D9B41A1", "A4B6B3996FC2F306B3FD8681BD63413D8C5009CC4FA329C2CCF0E2FA1B140305", "83CE3C1229688A593D485F81973C0F9195431EDA37CC5E36430E79C7A888638B", "EB04CF5EB1F39AFA762F2BB120F296CBA520C1B97DB1589565B81CB9A17B7244", "69DDD7EA90BB57C93E135DC85EA6FCD5480B603239BDC454FC758B2A26CF7F79", "9ACFAB7E43C8D880D06B262A94DEEEE4B4659989C3D0CAF19BAF6405E41AB7DF", "2399561127A57125DE8CEFEA610DDF2FA078B5C8067F4E828290BFB860E84B3C"));
        APRIL_16_2019 = LocalDate.of(2019, Month.APRIL, 16);
    }
}
