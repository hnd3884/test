package org.bouncycastle.cms.jcajce;

import org.bouncycastle.cms.CMSException;
import org.bouncycastle.jcajce.io.CipherInputStream;
import java.io.InputStream;
import javax.crypto.Cipher;
import org.bouncycastle.operator.InputDecryptor;
import org.bouncycastle.cms.RecipientOperator;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import java.security.PrivateKey;

public class JceKeyTransEnvelopedRecipient extends JceKeyTransRecipient
{
    public JceKeyTransEnvelopedRecipient(final PrivateKey privateKey) {
        super(privateKey);
    }
    
    public RecipientOperator getRecipientOperator(final AlgorithmIdentifier algorithmIdentifier, final AlgorithmIdentifier algorithmIdentifier2, final byte[] array) throws CMSException {
        return new RecipientOperator(new InputDecryptor() {
            final /* synthetic */ Cipher val$dataCipher = JceKeyTransEnvelopedRecipient.this.contentHelper.createContentCipher(this.extractSecretKey(algorithmIdentifier, algorithmIdentifier2, array), algorithmIdentifier2);
            
            public AlgorithmIdentifier getAlgorithmIdentifier() {
                return algorithmIdentifier2;
            }
            
            public InputStream getInputStream(final InputStream inputStream) {
                return (InputStream)new CipherInputStream(inputStream, this.val$dataCipher);
            }
        });
    }
}
