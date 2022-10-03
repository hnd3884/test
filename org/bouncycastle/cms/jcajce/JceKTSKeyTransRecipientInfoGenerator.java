package org.bouncycastle.cms.jcajce;

import org.bouncycastle.util.encoders.Hex;
import java.security.Provider;
import org.bouncycastle.operator.jcajce.JceAsymmetricKeyWrapper;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.DEROctetString;
import java.io.IOException;
import java.security.PublicKey;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import java.security.cert.CertificateEncodingException;
import org.bouncycastle.operator.AsymmetricKeyWrapper;
import org.bouncycastle.operator.jcajce.JceKTSKeyWrapper;
import org.bouncycastle.asn1.cms.IssuerAndSerialNumber;
import java.security.cert.X509Certificate;
import org.bouncycastle.cms.KeyTransRecipientInfoGenerator;

public class JceKTSKeyTransRecipientInfoGenerator extends KeyTransRecipientInfoGenerator
{
    private static final byte[] ANONYMOUS_SENDER;
    
    private JceKTSKeyTransRecipientInfoGenerator(final X509Certificate x509Certificate, final IssuerAndSerialNumber issuerAndSerialNumber, final String s, final int n) throws CertificateEncodingException {
        super(issuerAndSerialNumber, new JceKTSKeyWrapper(x509Certificate, s, n, JceKTSKeyTransRecipientInfoGenerator.ANONYMOUS_SENDER, getEncodedRecipID(issuerAndSerialNumber)));
    }
    
    public JceKTSKeyTransRecipientInfoGenerator(final X509Certificate x509Certificate, final String s, final int n) throws CertificateEncodingException {
        this(x509Certificate, new IssuerAndSerialNumber(new JcaX509CertificateHolder(x509Certificate).toASN1Structure()), s, n);
    }
    
    public JceKTSKeyTransRecipientInfoGenerator(final byte[] array, final PublicKey publicKey, final String s, final int n) {
        super(array, new JceKTSKeyWrapper(publicKey, s, n, JceKTSKeyTransRecipientInfoGenerator.ANONYMOUS_SENDER, getEncodedSubKeyId(array)));
    }
    
    private static byte[] getEncodedRecipID(final IssuerAndSerialNumber issuerAndSerialNumber) throws CertificateEncodingException {
        try {
            return issuerAndSerialNumber.getEncoded("DER");
        }
        catch (final IOException ex) {
            throw new CertificateEncodingException("Cannot process extracted IssuerAndSerialNumber: " + ex.getMessage()) {
                @Override
                public Throwable getCause() {
                    return ex;
                }
            };
        }
    }
    
    private static byte[] getEncodedSubKeyId(final byte[] array) {
        try {
            return new DEROctetString(array).getEncoded();
        }
        catch (final IOException ex) {
            throw new IllegalArgumentException("Cannot process subject key identifier: " + ex.getMessage()) {
                @Override
                public Throwable getCause() {
                    return ex;
                }
            };
        }
    }
    
    public JceKTSKeyTransRecipientInfoGenerator(final X509Certificate x509Certificate, final AlgorithmIdentifier algorithmIdentifier) throws CertificateEncodingException {
        super(new IssuerAndSerialNumber(new JcaX509CertificateHolder(x509Certificate).toASN1Structure()), new JceAsymmetricKeyWrapper(algorithmIdentifier, x509Certificate.getPublicKey()));
    }
    
    public JceKTSKeyTransRecipientInfoGenerator(final byte[] array, final AlgorithmIdentifier algorithmIdentifier, final PublicKey publicKey) {
        super(array, new JceAsymmetricKeyWrapper(algorithmIdentifier, publicKey));
    }
    
    public JceKTSKeyTransRecipientInfoGenerator setProvider(final String provider) {
        ((JceKTSKeyWrapper)this.wrapper).setProvider(provider);
        return this;
    }
    
    public JceKTSKeyTransRecipientInfoGenerator setProvider(final Provider provider) {
        ((JceKTSKeyWrapper)this.wrapper).setProvider(provider);
        return this;
    }
    
    static {
        ANONYMOUS_SENDER = Hex.decode("0c14416e6f6e796d6f75732053656e64657220202020");
    }
}
