package org.bouncycastle.cms.jcajce;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import java.security.Provider;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import java.security.PublicKey;
import java.security.cert.CertificateEncodingException;
import org.bouncycastle.operator.AsymmetricKeyWrapper;
import org.bouncycastle.operator.jcajce.JceAsymmetricKeyWrapper;
import org.bouncycastle.asn1.cms.IssuerAndSerialNumber;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import java.security.cert.X509Certificate;
import org.bouncycastle.cms.KeyTransRecipientInfoGenerator;

public class JceKeyTransRecipientInfoGenerator extends KeyTransRecipientInfoGenerator
{
    public JceKeyTransRecipientInfoGenerator(final X509Certificate x509Certificate) throws CertificateEncodingException {
        super(new IssuerAndSerialNumber(new JcaX509CertificateHolder(x509Certificate).toASN1Structure()), new JceAsymmetricKeyWrapper(x509Certificate));
    }
    
    public JceKeyTransRecipientInfoGenerator(final byte[] array, final PublicKey publicKey) {
        super(array, new JceAsymmetricKeyWrapper(publicKey));
    }
    
    public JceKeyTransRecipientInfoGenerator(final X509Certificate x509Certificate, final AlgorithmIdentifier algorithmIdentifier) throws CertificateEncodingException {
        super(new IssuerAndSerialNumber(new JcaX509CertificateHolder(x509Certificate).toASN1Structure()), new JceAsymmetricKeyWrapper(algorithmIdentifier, x509Certificate.getPublicKey()));
    }
    
    public JceKeyTransRecipientInfoGenerator(final byte[] array, final AlgorithmIdentifier algorithmIdentifier, final PublicKey publicKey) {
        super(array, new JceAsymmetricKeyWrapper(algorithmIdentifier, publicKey));
    }
    
    public JceKeyTransRecipientInfoGenerator setProvider(final String provider) {
        ((JceAsymmetricKeyWrapper)this.wrapper).setProvider(provider);
        return this;
    }
    
    public JceKeyTransRecipientInfoGenerator setProvider(final Provider provider) {
        ((JceAsymmetricKeyWrapper)this.wrapper).setProvider(provider);
        return this;
    }
    
    public JceKeyTransRecipientInfoGenerator setAlgorithmMapping(final ASN1ObjectIdentifier asn1ObjectIdentifier, final String s) {
        ((JceAsymmetricKeyWrapper)this.wrapper).setAlgorithmMapping(asn1ObjectIdentifier, s);
        return this;
    }
}
