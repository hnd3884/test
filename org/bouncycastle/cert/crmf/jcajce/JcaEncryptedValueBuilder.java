package org.bouncycastle.cert.crmf.jcajce;

import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import java.security.PrivateKey;
import org.bouncycastle.cert.crmf.CRMFException;
import java.security.cert.CertificateEncodingException;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import org.bouncycastle.asn1.crmf.EncryptedValue;
import java.security.cert.X509Certificate;
import org.bouncycastle.operator.OutputEncryptor;
import org.bouncycastle.operator.KeyWrapper;
import org.bouncycastle.cert.crmf.EncryptedValueBuilder;

public class JcaEncryptedValueBuilder extends EncryptedValueBuilder
{
    public JcaEncryptedValueBuilder(final KeyWrapper keyWrapper, final OutputEncryptor outputEncryptor) {
        super(keyWrapper, outputEncryptor);
    }
    
    public EncryptedValue build(final X509Certificate x509Certificate) throws CertificateEncodingException, CRMFException {
        return this.build(new JcaX509CertificateHolder(x509Certificate));
    }
    
    public EncryptedValue build(final PrivateKey privateKey) throws CertificateEncodingException, CRMFException {
        return this.build(PrivateKeyInfo.getInstance((Object)privateKey.getEncoded()));
    }
}
