package org.jscep.client.verification;

import org.slf4j.LoggerFactory;
import java.security.cert.CertificateEncodingException;
import java.util.Arrays;
import java.security.cert.X509Certificate;
import org.apache.commons.lang.ArrayUtils;
import java.security.MessageDigest;
import org.slf4j.Logger;

public final class MessageDigestCertificateVerifier implements CertificateVerifier
{
    private static final Logger LOGGER;
    private final MessageDigest digest;
    private final byte[] expected;
    
    public MessageDigestCertificateVerifier(final MessageDigest digest, final byte[] expected) {
        this.digest = digest;
        this.expected = ArrayUtils.clone(expected);
    }
    
    @Override
    public boolean verify(final X509Certificate cert) {
        try {
            this.digest.reset();
            byte[] actual = this.digest.digest(cert.getEncoded());
            if (Arrays.equals(actual, this.expected)) {
                return true;
            }
            actual = this.digest.digest(cert.getTBSCertificate());
            if (Arrays.equals(actual, this.expected)) {
                MessageDigestCertificateVerifier.LOGGER.warn("MessageDigest over the Certificate.tbsCertificate is configured, but it should be over the DER encoded Certificate");
                return true;
            }
            return false;
        }
        catch (final CertificateEncodingException e) {
            return false;
        }
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)MessageDigestCertificateVerifier.class);
    }
}
