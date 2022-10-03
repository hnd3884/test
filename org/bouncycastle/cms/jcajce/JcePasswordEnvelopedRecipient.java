package org.bouncycastle.cms.jcajce;

import org.bouncycastle.cms.CMSException;
import org.bouncycastle.jcajce.io.CipherInputStream;
import java.io.InputStream;
import javax.crypto.Cipher;
import org.bouncycastle.operator.InputDecryptor;
import org.bouncycastle.cms.RecipientOperator;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

public class JcePasswordEnvelopedRecipient extends JcePasswordRecipient
{
    public JcePasswordEnvelopedRecipient(final char[] array) {
        super(array);
    }
    
    public RecipientOperator getRecipientOperator(final AlgorithmIdentifier algorithmIdentifier, final AlgorithmIdentifier algorithmIdentifier2, final byte[] array, final byte[] array2) throws CMSException {
        return new RecipientOperator(new InputDecryptor() {
            final /* synthetic */ Cipher val$dataCipher = JcePasswordEnvelopedRecipient.this.helper.createContentCipher(this.extractSecretKey(algorithmIdentifier, algorithmIdentifier2, array, array2), algorithmIdentifier2);
            
            public AlgorithmIdentifier getAlgorithmIdentifier() {
                return algorithmIdentifier2;
            }
            
            public InputStream getInputStream(final InputStream inputStream) {
                return (InputStream)new CipherInputStream(inputStream, this.val$dataCipher);
            }
        });
    }
}
