package org.bouncycastle.cms.jcajce;

import org.bouncycastle.cms.CMSException;
import org.bouncycastle.jcajce.io.CipherInputStream;
import java.io.InputStream;
import javax.crypto.Cipher;
import org.bouncycastle.operator.InputDecryptor;
import org.bouncycastle.cms.RecipientOperator;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import java.io.IOException;
import org.bouncycastle.cms.KeyTransRecipientId;
import java.security.PrivateKey;

public class JceKTSKeyTransEnvelopedRecipient extends JceKTSKeyTransRecipient
{
    public JceKTSKeyTransEnvelopedRecipient(final PrivateKey privateKey, final KeyTransRecipientId keyTransRecipientId) throws IOException {
        super(privateKey, JceKTSKeyTransRecipient.getPartyVInfoFromRID(keyTransRecipientId));
    }
    
    public RecipientOperator getRecipientOperator(final AlgorithmIdentifier algorithmIdentifier, final AlgorithmIdentifier algorithmIdentifier2, final byte[] array) throws CMSException {
        return new RecipientOperator(new InputDecryptor() {
            final /* synthetic */ Cipher val$dataCipher = JceKTSKeyTransEnvelopedRecipient.this.contentHelper.createContentCipher(this.extractSecretKey(algorithmIdentifier, algorithmIdentifier2, array), algorithmIdentifier2);
            
            public AlgorithmIdentifier getAlgorithmIdentifier() {
                return algorithmIdentifier2;
            }
            
            public InputStream getInputStream(final InputStream inputStream) {
                return (InputStream)new CipherInputStream(inputStream, this.val$dataCipher);
            }
        });
    }
}
