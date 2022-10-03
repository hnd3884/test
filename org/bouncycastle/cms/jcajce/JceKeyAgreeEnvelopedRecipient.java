package org.bouncycastle.cms.jcajce;

import org.bouncycastle.cms.CMSException;
import org.bouncycastle.jcajce.io.CipherInputStream;
import java.io.InputStream;
import javax.crypto.Cipher;
import org.bouncycastle.operator.InputDecryptor;
import org.bouncycastle.cms.RecipientOperator;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import java.security.PrivateKey;

public class JceKeyAgreeEnvelopedRecipient extends JceKeyAgreeRecipient
{
    public JceKeyAgreeEnvelopedRecipient(final PrivateKey privateKey) {
        super(privateKey);
    }
    
    public RecipientOperator getRecipientOperator(final AlgorithmIdentifier algorithmIdentifier, final AlgorithmIdentifier algorithmIdentifier2, final SubjectPublicKeyInfo subjectPublicKeyInfo, final ASN1OctetString asn1OctetString, final byte[] array) throws CMSException {
        return new RecipientOperator(new InputDecryptor() {
            final /* synthetic */ Cipher val$dataCipher = JceKeyAgreeEnvelopedRecipient.this.contentHelper.createContentCipher(this.extractSecretKey(algorithmIdentifier, algorithmIdentifier2, subjectPublicKeyInfo, asn1OctetString, array), algorithmIdentifier2);
            
            public AlgorithmIdentifier getAlgorithmIdentifier() {
                return algorithmIdentifier2;
            }
            
            public InputStream getInputStream(final InputStream inputStream) {
                return (InputStream)new CipherInputStream(inputStream, this.val$dataCipher);
            }
        });
    }
}
