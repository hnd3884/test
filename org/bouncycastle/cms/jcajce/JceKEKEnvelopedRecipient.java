package org.bouncycastle.cms.jcajce;

import org.bouncycastle.cms.CMSException;
import org.bouncycastle.jcajce.io.CipherInputStream;
import java.io.InputStream;
import javax.crypto.Cipher;
import org.bouncycastle.operator.InputDecryptor;
import org.bouncycastle.cms.RecipientOperator;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import javax.crypto.SecretKey;

public class JceKEKEnvelopedRecipient extends JceKEKRecipient
{
    public JceKEKEnvelopedRecipient(final SecretKey secretKey) {
        super(secretKey);
    }
    
    public RecipientOperator getRecipientOperator(final AlgorithmIdentifier algorithmIdentifier, final AlgorithmIdentifier algorithmIdentifier2, final byte[] array) throws CMSException {
        return new RecipientOperator(new InputDecryptor() {
            final /* synthetic */ Cipher val$dataCipher = JceKEKEnvelopedRecipient.this.contentHelper.createContentCipher(this.extractSecretKey(algorithmIdentifier, algorithmIdentifier2, array), algorithmIdentifier2);
            
            public AlgorithmIdentifier getAlgorithmIdentifier() {
                return algorithmIdentifier2;
            }
            
            public InputStream getInputStream(final InputStream inputStream) {
                return (InputStream)new CipherInputStream(inputStream, this.val$dataCipher);
            }
        });
    }
}
