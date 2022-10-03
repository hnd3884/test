package org.bouncycastle.cert.dane;

import java.io.IOException;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.cert.X509CertificateHolder;

public class DANEEntry
{
    public static final int CERT_USAGE_CA = 0;
    public static final int CERT_USAGE_PKIX_VALIDATE = 1;
    public static final int CERT_USAGE_TRUST_ANCHOR = 2;
    public static final int CERT_USAGE_ACCEPT = 3;
    static final int CERT_USAGE = 0;
    static final int SELECTOR = 1;
    static final int MATCHING_TYPE = 2;
    private final String domainName;
    private final byte[] flags;
    private final X509CertificateHolder certHolder;
    
    DANEEntry(final String domainName, final byte[] flags, final X509CertificateHolder certHolder) {
        this.flags = flags;
        this.domainName = domainName;
        this.certHolder = certHolder;
    }
    
    public DANEEntry(final String s, final byte[] array) throws IOException {
        this(s, Arrays.copyOfRange(array, 0, 3), new X509CertificateHolder(Arrays.copyOfRange(array, 3, array.length)));
    }
    
    public byte[] getFlags() {
        return Arrays.clone(this.flags);
    }
    
    public X509CertificateHolder getCertificate() {
        return this.certHolder;
    }
    
    public String getDomainName() {
        return this.domainName;
    }
    
    public byte[] getRDATA() throws IOException {
        final byte[] encoded = this.certHolder.getEncoded();
        final byte[] array = new byte[this.flags.length + encoded.length];
        System.arraycopy(this.flags, 0, array, 0, this.flags.length);
        System.arraycopy(encoded, 0, array, this.flags.length, encoded.length);
        return array;
    }
    
    public static boolean isValidCertificate(final byte[] array) {
        return (array[0] >= 0 || array[0] <= 3) && array[1] == 0 && array[2] == 0;
    }
}
